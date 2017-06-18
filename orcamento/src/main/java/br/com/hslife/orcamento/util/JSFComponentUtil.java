/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor
	
	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

    para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 - 
	
	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/

package br.com.hslife.orcamento.util;

import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.html.HtmlColumn;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Cria os componentes que herdam de UIComponent,
 * como OutputText e InputText.
 * 
 * @author herculeshssj
 *
 */
public class JSFComponentUtil {
	
	// Classe não pode ser instanciada
	private JSFComponentUtil() {}
	
	/*
	 * Criar um novo <h:column /> 
	 */
	public static HtmlColumn createColumn(Map<String, Object> params) {
		HtmlColumn column = new HtmlColumn();
		
		try {
			// Caso haja mais parâmetros a serem setados, estes parâmetros estarão no Map
			if (params != null && !params.isEmpty()) {
				// Configure a partir daqui a atribuição dinâmica dos atribos do column
				
				// Atributo "rendered"
				column.setRendered(params.containsKey("rendered") ? (Boolean) params.get("rendered") : true);
				
			}
		} catch (Exception e) {
			// Encapsula a exceção original na IllegalArgumentException e relança a exceção
			throw new IllegalArgumentException(e.getCause());
		}
		
		return column;
	}
	
	/*
	 * Cria um novo <h:dataTable />
	 */
	public static HtmlDataTable createDatatable(String id, ValueExpression value, Map<String, Object> params) {
		HtmlDataTable dataTable = new HtmlDataTable();
		dataTable.setId(id);
		dataTable.setVar("item");
		dataTable.setValueExpression("value", value);
		
		try {
			// Caso haja mais parâmetros a serem setados, estes parâmetros estarão no Map
			if (params != null && !params.isEmpty()) {
				// Configure a partir daqui a atribuição dinâmica dos atribos do dataTable
				
				// Atributo "rendered"
				dataTable.setRendered(params.containsKey("rendered") ? (Boolean) params.get("rendered") : true);
				
			}
		} catch (Exception e) {
			// Encapsula a exceção original na IllegalArgumentException e relança a exceção
			throw new IllegalArgumentException(e.getCause());
		}
		
		return dataTable;
	}
	
	/*
	 * Cria um novo <h:inputText />
	 */
	public static HtmlInputText createInputText(String id, String value, Map<String, Object> params) {
		HtmlInputText inputText = new HtmlInputText();
		inputText.setId(id);
		inputText.setValue(value);
		
		try {
			// Caso haja mais parâmetros a serem setados, estes parâmetros estarão no Map
			if (params != null && !params.isEmpty()) {
				// Configure a partir daqui a atribuição dinâmica dos atribos do inputText
				
				// Atributo "rendered"
				inputText.setRendered(params.containsKey("rendered") ? (Boolean) params.get("rendered") : true);
				
				// Atributo "converter"
				inputText.setConverter(params.containsKey("converter") ? (Converter)params.get("converter") : null);
				
			}
		} catch (Exception e) {
			// Encapsula a exceção original na IllegalArgumentException e relança a exceção
			throw new IllegalArgumentException(e.getCause());
		}
		return inputText;
	}
	
	/*
	 * Cria um novo <h:outputText />
	 */
	public static HtmlOutputText createOutputText(String id, String value, Map<String, Object> params) {
		HtmlOutputText outputText = new HtmlOutputText();
		outputText.setId(id);
		outputText.setValue(value);
		
		try {
			// Caso haja mais parâmetros a serem setados, estes parâmetros estarão no Map
			if (params != null && !params.isEmpty()) {
				// Configure a partir daqui a atribuição dinâmica dos atribos do outputText
				
				// Atributo "rendered"
				outputText.setRendered(params.containsKey("rendered") ? (Boolean) params.get("rendered") : true);
				
				// Atributo "converter"
				outputText.setConverter(params.containsKey("converter") ? (Converter)params.get("converter") : null);
				
			}
		} catch (Exception e) {
			// Encapsula a exceção original na IllegalArgumentException e relança a exceção
			throw new IllegalArgumentException(e.getCause());
		}
		
		return outputText;
	}
	
	/*
	 * Cria um novo <h:outputText /> passando um ValueExpression
	 */
	public static HtmlOutputText createOutputText(String id, ValueExpression value, Map<String, Object> params) {
		HtmlOutputText outputText = new HtmlOutputText();
		outputText.setId(id);
		outputText.setValueExpression("value", value);
		
		try {
			// Caso haja mais parâmetros a serem setados, estes parâmetros estarão no Map
			if (params != null && !params.isEmpty()) {
				// Configure a partir daqui a atribuição dinâmica dos atribos do outputText
				
				// Atributo "rendered"
				outputText.setRendered(params.containsKey("rendered") ? (Boolean) params.get("rendered") : true);
				
				// Atributo "converter"
				outputText.setConverter(params.containsKey("converter") ? (Converter)params.get("converter") : null);
				
			}
		} catch (Exception e) {
			// Encapsula a exceção original na IllegalArgumentException e relança a exceção
			throw new IllegalArgumentException(e.getCause());
		}
		
		return outputText;
	}
	
	/*
	 * Cria um novo <h:panelGrid />
	 */
	public static HtmlPanelGrid createPanelGrid(String id, int columns, Map<String, Object> params) {
		HtmlPanelGrid panelGrid = new HtmlPanelGrid();
		panelGrid.setId(id);
		panelGrid.setColumns(columns);
		
		try {
			// Caso haja mais parâmetros a serem setados, estes parâmetros estarão no Map
			if (params != null && !params.isEmpty()) {
				// Configure a partir daqui a atribuição dinâmica dos atribos do panelGrid
				
				// Atributo "rendered"
				panelGrid.setRendered(params.containsKey("rendered") ? (Boolean) params.get("rendered") : true);
				
			}
		} catch (Exception e) {
			// Encapsula a exceção original na IllegalArgumentException e relança a exceção
			throw new IllegalArgumentException(e.getCause());
		}
		
		return panelGrid;
	}
	
	/*
	 * Cria um novo <h:panelGroup />
	 */
	public static HtmlPanelGroup createPanelGroup(String id, Map<String, Object> params) {
		HtmlPanelGroup panelGroup = new HtmlPanelGroup();
		panelGroup.setId(id);
		
		try {
			// Caso haja mais parâmetros a serem setados, estes parâmetros estarão no Map
			if (params != null && !params.isEmpty()) {
				// Configure a partir daqui a atribuição dinâmica dos atribos do panelGroup
				
				// Atributo "rendered"
				panelGroup.setRendered(params.containsKey("rendered") ? (Boolean) params.get("rendered") : true);
				
			}
		} catch (Exception e) {
			// Encapsula a exceção original na IllegalArgumentException e relança a exceção
			throw new IllegalArgumentException(e.getCause());
		}
		
		return panelGroup;
	}
	
	/*
	 * Cria um ValueExpression para atribuir em atributos de componentes JSF.
	 * Exemplo de value expression: "#{managedBean.atributo}" ou "#{managedBean.metodo}"
	 */
	public static ValueExpression createValueExpression(String expression, Class<?> clazz) {		
		ValueExpression valueExpression = FacesContext.getCurrentInstance()
				.getApplication().getExpressionFactory()
				.createValueExpression(FacesContext.getCurrentInstance().getELContext(), expression, clazz);
		return valueExpression;
	}
}