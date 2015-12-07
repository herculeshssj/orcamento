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

package br.com.hslife.orcamento.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlPanelGroup;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.RelatorioCustomizado;
import br.com.hslife.orcamento.enumeration.CadastroSistema;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoConta;

public class RelatorioCustomizadoUtil {
	
	/**
	 * Retorna os componentes gerados para a página de seleção 
	 * dos critérios de busca.
	 * 
	 * @return objeto HtmlPanelGroup com os componentes gerados
	 */
	public static HtmlPanelGroup getGeneratedComponentsToCriteriaPage(RelatorioCustomizado relatorioCustomizado) {
		HtmlPanelGroup panelGroup = new HtmlPanelGroup();
		panelGroup.setId("grpCriteriaComponents");
		
		// Cria o panelGrid para alinhar os componentes
		HtmlPanelGrid panelGrid = RelatorioCustomizadoUtil.createPanelGrid("pnlCriteria", 2);
		
		// Verifica a existência de critérios de busca. Existindo,
		// monta a tela de consulta
		if (relatorioCustomizado.getParametrosRelatorio() != null && !relatorioCustomizado.getParametrosRelatorio().isEmpty()) {
			
			// TODO implementar
			
		} else {
			panelGrid = RelatorioCustomizadoUtil.createPanelGrid("pnlCriteria", 1);
			
			// Cria um outputText avisando que não existe parâmetros de consulta
			panelGrid.getChildren().add(RelatorioCustomizadoUtil.createOutputText("txtMsgSemCriterios", "Sem critérios a selecionar.", null));
		}
		
		// Adiciona o panelGrid com os componentes criados ao panelGroup
		panelGroup.getChildren().add(panelGrid);
		
		return panelGroup;
	}
	
	/**
	 * Retorna os componentes gerados para a página de resultados 
	 * da busca.
	 * 
	 * @return objeto HtmlPanelGroup com os componentes gerados
	 */
	public static HtmlPanelGroup getGeneratedComponentsToResultPage() {
		return null;
	}
	
	private static HtmlPanelGrid createPanelGrid(String id, int columns) {
		HtmlPanelGrid panelGrid = new HtmlPanelGrid();
		panelGrid.setId(id);
		panelGrid.setColumns(columns);
		return panelGrid;
	}
	
	private static HtmlOutputText createOutputText(String id, String value, String title) {
		HtmlOutputText outputText = new HtmlOutputText();
		outputText.setId(id);
		outputText.setValue(value);
		outputText.setTitle(title);
		return outputText;
	}
}
