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

import javax.el.ValueExpression;
import javax.faces.component.html.HtmlOutputText;

import org.primefaces.component.inputmask.InputMask;

import br.com.hslife.orcamento.converter.DateConverter;

/**
 * Cria componentes já pré-definidos para uso em 
 * formulários dinâmicos, como campos de data
 * formatados, e colunas de tabelas com estilo.
 * 
 * @author herculeshssj
 *
 */
public class CustomComponentUtil {
	
	// Classe não pode ser instanciada
	private CustomComponentUtil() {}
	
	/*
	 * Cria um InputMask do PrimeFaces com a máscara no formato DD/MM/AAAA já com o DateConverter definido.
	 * Este InputMask recebe um value expression em Expression Language a fim de setar um atributo no 
	 * ManagedBean. 
	 */	
	public static InputMask createDateTimeField(String id, ValueExpression valueExpression) {
		InputMask inputMask = PrimeFacesComponentUtil.createInputMask(id, null, "99/99/9999", null);
		inputMask.setValueExpression("value", valueExpression);
		inputMask.setConverter(new DateConverter());
		return inputMask;
	}
	
	public static HtmlOutputText createLabelField(String id, String value) {
		return JSFComponentUtil.createOutputText(id, value, null);
	}
}
