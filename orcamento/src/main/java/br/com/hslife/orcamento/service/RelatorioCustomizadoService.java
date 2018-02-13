/***

	Copyright (c) 2012 - 2021 Hércules S. S. José

	Este arquivo é parte do programa Orçamento Doméstico.


	Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

	modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

	publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

	Licença.


	Este programa é distribuído na esperança que possa ser útil, mas SEM

	NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer

	MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor

	GNU em português para maiores detalhes.


	Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob

	o nome de "LICENSE" junto com este programa, se não, acesse o site do

	projeto no endereco https://github.com/herculeshssj/orcamento ou escreva

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth

	Floor, Boston, MA  02110-1301, USA.


	Para mais informações sobre o programa Orçamento Doméstico e seu autor

	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

	para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.RelatorioCustomizado;
import br.com.hslife.orcamento.entity.RelatorioParametro;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.facade.IRelatorioCustomizado;
import br.com.hslife.orcamento.repository.RelatorioCustomizadoRepository;
import br.com.hslife.orcamento.util.Util;

@Service("relatorioCustomizadoService")
public class RelatorioCustomizadoService extends AbstractCRUDService<RelatorioCustomizado> implements IRelatorioCustomizado {

	@Autowired
	private RelatorioCustomizadoRepository repository;
	
	public RelatorioCustomizadoRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}

	@Override
	public List<RelatorioCustomizado> buscarNomePorUsuario(String nome,	Usuario usuario) {
		return getRepository().findNomeByUsuario(nome, usuario);
	}

	@Override
	public List<Map<String, Object>> processarRelatorioCustomizado(RelatorioCustomizado entity, Map<String, Object> parameterValues) {
		// TODO definir o tipo Date, Time e Timestamp(ou date/time), pois eles são diferentes. Atualmente foi definido o tipo Date.
		// FIXME terminar de implementar este método
		
		// Obtém a consulta SQL nativa
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
		
		// Executa a consulta montada
		List<Map<String,Object>> queryResult = getRepository().executeCustomNativeSQL(nativeSQL);
		
		// Retorna o resultado
		return queryResult;
	}
	
}
