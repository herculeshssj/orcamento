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

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.enumeration.Abrangencia;
import br.com.hslife.orcamento.enumeration.Bandeira;
import br.com.hslife.orcamento.enumeration.CadastroSistema;
import br.com.hslife.orcamento.enumeration.FormaPagamentoFatura;
import br.com.hslife.orcamento.enumeration.IncrementoClonagemLancamento;
import br.com.hslife.orcamento.enumeration.StatusDivida;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoCartao;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoDado;
import br.com.hslife.orcamento.enumeration.TipoInvestimento;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoPessoa;
import br.com.hslife.orcamento.enumeration.TipoTermoDividaTerceiro;
import br.com.hslife.orcamento.enumeration.TipoTratamento;

@Component("enumCombo")
@Scope("application")
public class EnumComboUtil {

	public List<SelectItem> getListaTipoConta() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (TipoConta enumeration : TipoConta.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaTipoContaSemCartao() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (TipoConta enumeration : TipoConta.values()) {
			if (enumeration.equals(TipoConta.CARTAO)) continue;
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaTipoCartao() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (TipoCartao enumeration : TipoCartao.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaBandeira() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (Bandeira enumeration : Bandeira.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaAbrangencia() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (Abrangencia enumeration : Abrangencia.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaTipoLancamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (TipoLancamento enumeration : TipoLancamento.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaIncrementoClonagemLancamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (IncrementoClonagemLancamento enumeration : IncrementoClonagemLancamento.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaTipoCategoria() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (TipoCategoria enumeration : TipoCategoria.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaFormaPagamentoFatura() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (FormaPagamentoFatura enumeration : FormaPagamentoFatura.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaTipoPessoa() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (TipoPessoa enumeration : TipoPessoa.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaStatusDivida() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (StatusDivida enumeration : StatusDivida.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaCadastroSistema() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (CadastroSistema enumeration : CadastroSistema.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaCadastroSistemaSemMeioPagamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (CadastroSistema enumeration : CadastroSistema.values()) {
			if (enumeration.equals(CadastroSistema.MEIOPAGAMENTO)) continue;
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}		
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaStatusLancamentoConta() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (StatusLancamentoConta enumeration : StatusLancamentoConta.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaTipoDado() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (TipoDado enumeration : TipoDado.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaTipoInvestimento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (TipoInvestimento enumeration : TipoInvestimento.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaTipoTermoDividaTerceiro() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (TipoTermoDividaTerceiro enumeration : TipoTermoDividaTerceiro.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaTipoTratamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (TipoTratamento enumeration : TipoTratamento.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
}