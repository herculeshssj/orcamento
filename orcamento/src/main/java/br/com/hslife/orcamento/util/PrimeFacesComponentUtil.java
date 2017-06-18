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

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.primefaces.component.inputmask.InputMask;

/**
 * Cria os componentes PrimeFaces que herdam de UIComponent,
 * como InputMask e FileUpload.
 * 
 * @author herculeshssj
 *
 */
public class PrimeFacesComponentUtil {
	
	// Classe não pode ser instanciada
	private PrimeFacesComponentUtil() {}
	
	/*
	 * Cria um novo <p:inputMask /> do PrimeFaces
	 */
	public static InputMask createInputMask(String id, String value, String mask, Map<String, Object> params) {
		InputMask inputMask = (InputMask)FacesContext.getCurrentInstance().getApplication().createComponent(InputMask.COMPONENT_TYPE);
		inputMask.setId(id);
		inputMask.setValue(value);
		inputMask.setMask(mask);
		
		try {
			// Caso haja mais parâmetros a serem setados, estes parâmetros estarão no Map
			if (params != null && !params.isEmpty()) {
				// Configure a partir daqui a atribuição dinâmica dos atribos do inputMask
				
				// Atributo "rendered"
				inputMask.setRendered(params.containsKey("rendered") ? (Boolean) params.get("rendered") : true);
				
				// Atributo "converter"
				inputMask.setConverter(params.containsKey("converter") ? (Converter)params.get("converter") : null);
				
			}
		} catch (Exception e) {
			// Encapsula a exceção original na IllegalArgumentException e relança a exceção
			throw new IllegalArgumentException(e.getCause());
		}
		return inputMask;
	}
}