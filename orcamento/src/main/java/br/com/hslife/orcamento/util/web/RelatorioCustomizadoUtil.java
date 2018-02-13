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
package br.com.hslife.orcamento.util.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.html.HtmlColumn;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlPanelGroup;

import br.com.hslife.orcamento.converter.DateConverter;
import br.com.hslife.orcamento.entity.RelatorioColuna;
import br.com.hslife.orcamento.entity.RelatorioCustomizado;
import br.com.hslife.orcamento.entity.RelatorioParametro;

/**
 * Cria as páginas de consulta e resultado a partir
 * dos dados do relatório customizado passado.
 * 
 * @author herculeshssj
 *
 */
public class RelatorioCustomizadoUtil {
	
	// Classe não pode ser instantiada
	private RelatorioCustomizadoUtil() {}
	
	/**
	 * Retorna os componentes gerados para a página de seleção 
	 * dos critérios de busca.
	 * 
	 * @return objeto HtmlPanelGroup com os componentes gerados
	 */
	public static HtmlPanelGroup getGeneratedComponentsToCriteriaPage(RelatorioCustomizado relatorioCustomizado) {
		// Cria um novo panel group
		HtmlPanelGroup panelGroup = JSFComponentUtil.createPanelGroup("grpCriteriaComponents", null);
		
		// Cria um novo panel grip para alinhar os componentes
		HtmlPanelGrid panelGrid = JSFComponentUtil.createPanelGrid("pnlCriteria", 2, null);
		
		// Verifica a existência de critérios de busca. Existindo, monta a tela de consulta
		if (relatorioCustomizado.getParametrosRelatorio() != null & !relatorioCustomizado.getParametrosRelatorio().isEmpty()) {
			
			// Itera a lista de parâmetros para criar o formulário de critério de busca
			for (RelatorioParametro parametro : relatorioCustomizado.getParametrosRelatorio()) {
				switch (parametro.getTipoDado()) {
					case DATE :
						// Cria o label
						panelGrid.getChildren().add(CustomComponentUtil.createLabelField("lbl" + parametro.getNomeParametro(), parametro.getTextoExibicao()));
						// Cria o inputMask
						panelGrid.getChildren().add(CustomComponentUtil.createDateTimeField("txt" + parametro.getNomeParametro(), 
								JSFComponentUtil.createValueExpression("#{relatorioCustomizadoMB.parameterValues['" + parametro.getNomeParametro() + "']}", Object.class)));
						break;
					case BOOLEAN:
						throw new UnsupportedOperationException("Rotina não implementada.");
						//break;
					case DOUBLE:
						throw new UnsupportedOperationException("Rotina não implementada.");
						//break;
					case INTEGER:
						throw new UnsupportedOperationException("Rotina não implementada.");
						//break;
					case LONG:
						throw new UnsupportedOperationException("Rotina não implementada.");
						//break;
					case STRING:
						throw new UnsupportedOperationException("Rotina não implementada.");
						//break;
					default:
						// não faz nada por enquanto
				}
			}
			
		} else {
			// Define o panel grid para uma coluna
			panelGrid = JSFComponentUtil.createPanelGrid("pngCriteria", 1, null);
			
			// Seta uma mensagem de aviso informando a ausência de critérios de busca
			panelGrid.getChildren().add(JSFComponentUtil.createOutputText("txtMsgSemCriterio", "Sem critérios a selecionar", null));
		}
		
		// Adiciona o panel grid com os componentes criados no panel group
		panelGroup.getChildren().add(panelGrid);
		
		return panelGroup;
	}
	
	/**
	 * Retorna os componentes gerados para a página de resultados 
	 * da busca.
	 * 
	 * @return objeto HtmlPanelGroup com os componentes gerados
	 */
	public static HtmlPanelGroup getGeneratedComponentsToResultPage(RelatorioCustomizado relatorioCustomizado) {
		// Cria um novo panel group
		HtmlPanelGroup panelGroup = JSFComponentUtil.createPanelGroup("grpResultComponents", null);
		
		// Cria um novo dataTable
		HtmlDataTable dataTable = JSFComponentUtil.createDatatable("tableResults", JSFComponentUtil.createValueExpression("#{relatorioCustomizadoMB.queryResult}", List.class), null);
		
		// Itera a lista de colunas do relatório customizado para criar os componentes <h:column /> correspondentes
		for (RelatorioColuna coluna : relatorioCustomizado.getColunasRelatorio()) {
			
			// Cria o componente <h:column />
			HtmlColumn column = JSFComponentUtil.createColumn(null);
			column.getFacets().put("header", JSFComponentUtil.createOutputText("lblColumn" + coluna.getNomeColuna(), coluna.getTextoExibicao(), null));
			
			// Exibe o resultado da coluna
			switch (coluna.getTipoDado()) {
				case STRING:
					column.getChildren().add(JSFComponentUtil.createOutputText("txtResult" + coluna.getNomeColuna(), JSFComponentUtil.createValueExpression("#{item['" + coluna.getNomeColuna() +"']}", String.class), null));
					break;
				case DOUBLE:
					column.getChildren().add(JSFComponentUtil.createOutputText("txtResult" + coluna.getNomeColuna(), JSFComponentUtil.createValueExpression("#{item['" + coluna.getNomeColuna() +"']}", Double.class), null));
					break;
				case DATE:
					Map<String, Object> params = new HashMap<>();
					params.put("converter", new DateConverter());
					column.getChildren().add(JSFComponentUtil.createOutputText("txtResult" + coluna.getNomeColuna(), JSFComponentUtil.createValueExpression("#{item['" + coluna.getNomeColuna() +"']}", Date.class), params));
					break;
				case BOOLEAN:
					throw new UnsupportedOperationException("Rotina não implementada.");
					//break;
				case INTEGER:
					throw new UnsupportedOperationException("Rotina não implementada.");
					//break;
				case LONG:
					throw new UnsupportedOperationException("Rotina não implementada.");
					//break;
				default:
					break;
			}
			
			// Adiciona a coluna no dataTable
			dataTable.getChildren().add(column);
		}
		
		// Adição do rodapé da tabela
		HtmlPanelGroup panelFooter = JSFComponentUtil.createPanelGroup("pnlFooter", null);
		panelFooter.getChildren().add(JSFComponentUtil.createOutputText("lblFooterLabel", "Total de registros: ", null));
		panelFooter.getChildren().add(JSFComponentUtil.createOutputText("lblFooter", JSFComponentUtil.createValueExpression("#{relatorioCustomizadoMB.totalRegistros}", Integer.class), null));
		
		dataTable.getFacets().put("footer", panelFooter);		
		
		// Adiciona o dataTable ao panelGroup
		panelGroup.getChildren().add(dataTable);
		
		return panelGroup;
		
	}
}
