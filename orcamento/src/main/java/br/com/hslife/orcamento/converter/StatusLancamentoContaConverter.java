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

para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor,

Boston, MA  02110-1301, USA.


Para mais informações sobre o programa Orçamento Doméstico e seu autor

entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;

@FacesConverter("statusLancamentoContaConverter")
public class StatusLancamentoContaConverter implements Converter{
	
	@Override
	public Object getAsObject(FacesContext contexto, UIComponent componente, String valor) {
		try {
			for (StatusLancamentoConta status : StatusLancamentoConta.values()) {
				if (status.toString().equalsIgnoreCase(valor))
					return status;
			}
			return null;
		} catch (Exception e) {
			throw new ConverterException(e);
		}
	}

	@Override
	public String getAsString(FacesContext contexto, UIComponent componente, Object valor) {
		try {
			for (StatusLancamentoConta status : StatusLancamentoConta.values()) {
				if ( ((StatusLancamentoConta)valor).equals(status) )
					return status.toString();
			}
			return "";
		} catch (Exception e) {
			throw new ConverterException(e);
		}
	}

}
