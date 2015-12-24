/***
  
  	Copyright (c) 2012 - 2016 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou 

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como 

    publicada pela Fundação do Software Livre (FSF); na versão 2.1 da 

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, 

    mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a 
    
    qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública 
    
    Geral Menor GNU em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob o 

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento ou escreva 
    
    para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor, 
    
    Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor entre  

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.RelatorioColuna;
import br.com.hslife.orcamento.entity.RelatorioCustomizado;
import br.com.hslife.orcamento.entity.RelatorioParametro;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IRelatorioCustomizado;
import br.com.hslife.orcamento.repository.RelatorioCustomizadoRepository;
import br.com.hslife.orcamento.util.Util;

@Service("relatorioCustomizadoService")
public class RelatorioCustomizadoService extends AbstractCRUDService<RelatorioCustomizado> implements IRelatorioCustomizado {

	@Autowired
	private RelatorioCustomizadoRepository repository;
	
	public RelatorioCustomizadoRepository getRepository() {
		return repository;
	}

	public void setRepository(RelatorioCustomizadoRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<RelatorioCustomizado> buscarNomePorUsuario(String nome,	Usuario usuario) throws BusinessException {
		return getRepository().findNomeByUsuario(nome, usuario);
	}

	@Override
	public List<Map<String, Object>> processarRelatorioCustomizado(RelatorioCustomizado entity, Map<String, Object> parameterValues) throws BusinessException {
		// TODO definir o tipo Date, Time e Timestamp(ou date/time), pois eles são diferentes. Atualmente foi definido o tipo Date.
		
		/*** 1º - constrói o SQL nativo ***/
		String nativeSQL = entity.getConsultaSQL();
		
		// Itera a lista de parâmetros, caso exista, para poder definir os valores no formato correto do SQL nativo
		for (RelatorioParametro parametro : entity.getParametrosRelatorio()) {
			
			// Substitui no SQL o alias do parâmetro pelo valor que foi passado
			switch (parametro.getTipoDado()) {
				case DATE:
					nativeSQL = nativeSQL.replace(":" + parametro.getNomeParametro(), "'" + Util.formataDataHora((Date)parameterValues.get(parametro.getNomeParametro()), Util.DATABASE_DATA) + "'");
					break;
				default:
					// Faz nada por enquanto
					break;
			}
		}
		
		/*** 2º - executa a consulta nativa montada***/
		List<List<Object>> queryResult = getRepository().executeCustomNativeSQL(nativeSQL);
		
		/*** 3º - transforma o resultado em um List<Map<String, Object>> ***/
		List<Map<String, Object>> mapQueryResult = new LinkedList<>();
		
		// Itera as linhas
		for (List<Object> linhasResultado : queryResult) {
			
			// Itera as colunas
			int contador = 0;
			Map<String, Object> colunasResultado = new LinkedHashMap<>();
			for (RelatorioColuna coluna : entity.getColunasRelatorio()) {
				colunasResultado.put(coluna.getNomeColuna(), linhasResultado.get(contador));
				contador++;
			}
			
			// Adiciona no Map de resultados
			mapQueryResult.add(colunasResultado);
		}		
		
		/*** 4º - retorna o resultado ***/
		return mapQueryResult;
	}
	
}