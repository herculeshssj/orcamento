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

package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.component.html.HtmlPanelGroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.RelatorioColuna;
import br.com.hslife.orcamento.entity.RelatorioCustomizado;
import br.com.hslife.orcamento.entity.RelatorioParametro;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IRelatorioCustomizado;
import br.com.hslife.orcamento.util.RelatorioColunaComparator;
import br.com.hslife.orcamento.util.RelatorioCustomizadoUtil;

@Component("relatorioCustomizadoMB")
@Scope("session")
public class RelatorioCustomizadoController extends AbstractCRUDController<RelatorioCustomizado> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5508989331062227746L;

	@Autowired
	private IRelatorioCustomizado service; 
	
	private String nomeRelatorio;
	
	private RelatorioColuna colunaRelatorio = new RelatorioColuna();
	private RelatorioColuna colunaRelatorioTemp; // usado na mudança de ordem das colunas
	private RelatorioParametro parametroRelatorio = new RelatorioParametro();
	private RelatorioParametro parametroRelatorioTemp; // usado para exclusão de parâmentros da listagem 
	
	// Map que guarda os valores informados nos parâmetros de busca
	private Map<String, Object> parameterValues = new HashMap<>();
	
	public RelatorioCustomizadoController() {
		super(new RelatorioCustomizado());
		moduleTitle = "Relatórios Customizados";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new RelatorioCustomizado();
		listEntity = new ArrayList<RelatorioCustomizado>();
		
		colunaRelatorio = new RelatorioColuna();
		colunaRelatorioTemp = new RelatorioColuna();
		parametroRelatorio = new RelatorioParametro();
		parametroRelatorioTemp = new RelatorioParametro();
	}
	
	@Override
	public void find() {
		try {
			listEntity = getService().buscarNomePorUsuario(nomeRelatorio, getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String save() {
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}
	
	@Override
	public String edit() {
		String retorno = super.edit();
		
		// Ordena as colunas da consulta
		List<RelatorioColuna> listaColunas = new LinkedList<RelatorioColuna>();
		listaColunas.addAll(entity.getColunasRelatorio());
		Collections.sort(listaColunas, new RelatorioColunaComparator());
		entity.getColunasRelatorio().clear();
		entity.getColunasRelatorio().addAll(listaColunas);
		
		return retorno;
	}
	
	public String gerarRelatorioView() {
		try {
			entity = getService().buscarPorID(idEntity);
			
			// Inicializa o Map dos valores dos parâmetros
			if (entity.getParametrosRelatorio() != null && !entity.getParametrosRelatorio().isEmpty()) {
				for (RelatorioParametro parametro : entity.getParametrosRelatorio()) {
					parameterValues.put(parametro.getNomeParametro(), null);
				}
			}
			
			actionTitle = " - " + entity.getNome();
			return "/pages/RelatorioCustomizado/gerarRelatorio";
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String gerarRelatorio() {
		// Valida a entrada dos parâmetros
		if (entity.getParametrosRelatorio() != null && !entity.getParametrosRelatorio().isEmpty()) {
			for (String key : parameterValues.keySet()) {
				if (parameterValues.get(key) == null) {
					warnMessage("Todos os parâmetros devem ser informados!");
					return "";
				}
				if (parameterValues.get(key) != null && parameterValues.get(key) instanceof String && ((String)parameterValues.get(key)).isEmpty()) {
					warnMessage("Todos os parâmetros devem ser informados!");
					return "";
				}
			}
		}
		
		try {
			actionTitle = " - " + entity.getNome() + " [Resultado]";
			
			// Processa o relatório customizado passando os valores informados para os parâmetros
			getService().processarRelatorioCustomizado(entity, parameterValues);
			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		return "/pages/RelatorioCustomizado/visualizarRelatorio";
	}

	public HtmlPanelGroup getCriteriaComponents() {
		return RelatorioCustomizadoUtil.getGeneratedComponentsToCriteriaPage(entity);
	}
	
	public void setCriteriaComponents(HtmlPanelGroup panelGroup) {
		// Faz nada. Método criado por causa do JSF lifecycle que tenta gravar no 
		// atributo criteriaComponents
	}
	
	public HtmlPanelGroup getResultComponents() {
		return RelatorioCustomizadoUtil.getGeneratedComponentsToResultPage();
	}
	
	public void setResultComponents(HtmlPanelGroup panelGroup) {
		// Faz nada. Método criado por causa do JSF lifecycle que tenta gravar no 
		// atributo resultComponents
	}
	
	public void adicionarColuna() {
		colunaRelatorio.setOrdem(entity.getColunasRelatorio().size() + 1);
		entity.getColunasRelatorio().add(colunaRelatorio);
		colunaRelatorio = new RelatorioColuna();
	}
	
	public void removerColuna() {
		// Remove o item selecionado
		for (Iterator<RelatorioColuna> iterator = entity.getColunasRelatorio().iterator(); iterator.hasNext(); ) {
			RelatorioColuna item = iterator.next();
			if (item.getOrdem() == colunaRelatorioTemp.getOrdem()) {
				iterator.remove();
			}
		}
		
		// Renumera a numeração da ordem das colunas
		int i = 1;
		for (RelatorioColuna coluna : entity.getColunasRelatorio()) {
			coluna.setOrdem(i);
			i++;
		}
	}
	
	public void subirNivel() {
		// Adiciona o conteúdo do Set de colunas na lista temporária
		List<RelatorioColuna> listaColunas = new LinkedList<RelatorioColuna>();
		listaColunas.addAll(entity.getColunasRelatorio());
		
		// Variáveis auxiliares para guardar do índice
		int indiceAtual = 0;
		int indiceAnterior = 0;		
		
		// Varre a lista a procura do elemento selecionado
		for (int i = 0; i < listaColunas.size(); i++) {
			if (listaColunas.get(i).equals(colunaRelatorioTemp)) {
				indiceAtual = i;
				indiceAnterior = indiceAtual - 1;
				break;
			}
		}
		
		// Realiza o swap dos elementos da lista caso indice anterior ser
		// igual ou maior que zero
		if (indiceAnterior >= 0) {
			Collections.swap(listaColunas, indiceAtual, indiceAnterior);
		}
		
		// Limpa o Set da entidade e adiciona o conteúdo da lista
		entity.getColunasRelatorio().clear();
		entity.getColunasRelatorio().addAll(listaColunas);
		
		// Renumera a numeração da ordem das colunas
		int i = 1;
		for (RelatorioColuna coluna : entity.getColunasRelatorio()) {
			coluna.setOrdem(i);
			i++;
		}
	}
	
	public void descerNivel() {
		// Adiciona o conteúdo do Set de colunas na lista temporária
		List<RelatorioColuna> listaColunas = new LinkedList<RelatorioColuna>();
		listaColunas.addAll(entity.getColunasRelatorio());
		
		// Variáveis auxiliares para guardar do índice
		int indiceAtual = 0;
		int indicePosterior = 0;		
		
		// Varre a lista a procura do elemento selecionado
		for (int i = 0; i < listaColunas.size(); i++) {
			if (listaColunas.get(i).equals(colunaRelatorioTemp)) {
				indiceAtual = i;
				indicePosterior = indiceAtual + 1;
				break;
			}
		}
		
		// Realiza o swap dos elementos da lista caso indice anterior ser
		// igual ou maior que zero
		if (indicePosterior < listaColunas.size()) {
			Collections.swap(listaColunas, indiceAtual, indicePosterior);
		}
		
		// Limpa o Set da entidade e adiciona o conteúdo da lista
		entity.getColunasRelatorio().clear();
		entity.getColunasRelatorio().addAll(listaColunas);
		
		// Renumera a numeração da ordem das colunas
		int i = 1;
		for (RelatorioColuna coluna : entity.getColunasRelatorio()) {
			coluna.setOrdem(i);
			i++;
		}
	}
	
	public void atualizarFormColunas() {
		// Método criado para poder setar os valores digitados no momento
		// da seleção do checkbox "Formatar".
		this.getColunaRelatorio();
	}
	
	public void adicionarParametro() {
		entity.getParametrosRelatorio().add(parametroRelatorio);
		parametroRelatorio = new RelatorioParametro();
	}
	
	public void removerParametro() {
		// Remove o item selecionado
		for (Iterator<RelatorioParametro> iterator = entity.getParametrosRelatorio().iterator(); iterator.hasNext(); ) {
			RelatorioParametro item = iterator.next();
			if (item.getNomeParametro().equals(parametroRelatorioTemp.getNomeParametro())) {
				iterator.remove();
			}
		}
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	public IRelatorioCustomizado getService() {
		return service;
	}

	public RelatorioColuna getColunaRelatorio() {
		return colunaRelatorio;
	}

	public void setColunaRelatorio(RelatorioColuna colunaRelatorio) {
		this.colunaRelatorio = colunaRelatorio;
	}

	public RelatorioParametro getParametroRelatorio() {
		return parametroRelatorio;
	}

	public void setParametroRelatorio(RelatorioParametro parametroRelatorio) {
		this.parametroRelatorio = parametroRelatorio;
	}

	public RelatorioColuna getColunaRelatorioTemp() {
		return colunaRelatorioTemp;
	}

	public void setColunaRelatorioTemp(RelatorioColuna colunaRelatorioTemp) {
		this.colunaRelatorioTemp = colunaRelatorioTemp;
	}

	public RelatorioParametro getParametroRelatorioTemp() {
		return parametroRelatorioTemp;
	}

	public void setParametroRelatorioTemp(RelatorioParametro parametroRelatorioTemp) {
		this.parametroRelatorioTemp = parametroRelatorioTemp;
	}

	public Map<String, Object> getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(Map<String, Object> parameterValues) {
		this.parameterValues = parameterValues;
	}	
}