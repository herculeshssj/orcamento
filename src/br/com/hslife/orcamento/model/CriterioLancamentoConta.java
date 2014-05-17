/***
  
  	Copyright (c) 2012, 2013, 2014 Hércules S. S. José

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

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site HSlife
    
    no endereco www.hslife.com.br ou escreva para a Fundação do Software 
    
    Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor acesse o 

    endereço www.hslife.com.br, pelo e-mail contato@hslife.com.br ou escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
 ***/

package br.com.hslife.orcamento.model;

import java.util.Date;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.LancamentoAgendado;
import br.com.hslife.orcamento.enumeration.TipoLancamento;

public class CriterioLancamentoConta {
	
	private Conta conta;
	
	private String descricao;
	
	private Date dataInicio;
	
	private Date dataFim;
	
	private String parcela;
	
	private Moeda moeda;
	
	private TipoLancamento tipo;
	
	private Double valor;
	
	private LancamentoAgendado lancamentoAgendado;
	
	private LancamentoAgendado lancamentoQuitado;
	
	public CriterioLancamentoConta() {
		lancamentoAgendado = LancamentoAgendado.COM;
		lancamentoQuitado = LancamentoAgendado.COM;
	}
	
	public int getAgendado() {
		switch (lancamentoAgendado) {
			case COM : return 0;
			case SEM : return -1;
			case SOMENTE : return 1;
			default : return 0;
		}
	}
	
	public int getQuitado() {
		switch (lancamentoQuitado) {
			case COM : return 0;
			case SEM : return -1;
			case SOMENTE : return 1;
			default : return 0;
		}
	}
	
	public Boolean getAgendadoBoolean() {
		switch (lancamentoAgendado) {
			case SEM : return false;
			case SOMENTE : return true;
			default : return false;
		}
	}
	
	public Boolean getQuitadoBoolean() {
		switch (lancamentoQuitado) {
			case SEM : return false;
			case SOMENTE : return true;
			default : return false;
		}
	}
	
	public void setAgendado(int agendado) {
		if (agendado == 0) {
			lancamentoAgendado = LancamentoAgendado.COM;
		} else if (agendado == 1) {
			lancamentoAgendado = LancamentoAgendado.SOMENTE;
		} else {
			lancamentoAgendado = LancamentoAgendado.SEM;
		}
	}
	
	public void setQuitado(int quitado) {
		if (quitado == 0) {
			lancamentoQuitado = LancamentoAgendado.COM;
		} else if (quitado == 1) {
			lancamentoQuitado = LancamentoAgendado.SOMENTE;
		} else {
			lancamentoQuitado = LancamentoAgendado.SEM;
		}
	}
	
	public void setAgendado(LancamentoAgendado agendado) {
		this.lancamentoAgendado = agendado;
	}
	
	public void setQuitado(LancamentoAgendado quitado) {
		this.lancamentoQuitado = quitado;
	}
	
	public void setAgendado(boolean agendado) {
		lancamentoAgendado = LancamentoAgendado.COM;
		if (agendado)
			lancamentoAgendado = LancamentoAgendado.SOMENTE;
		else
			lancamentoAgendado = LancamentoAgendado.SEM;
	}
	
	public void setQuitado(boolean quitado) {
		lancamentoQuitado = LancamentoAgendado.COM;
		if (quitado)
			lancamentoQuitado = LancamentoAgendado.SOMENTE;
		else
			lancamentoQuitado = LancamentoAgendado.SEM;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public String getParcela() {
		return parcela;
	}

	public void setParcela(String parcela) {
		this.parcela = parcela;
	}

	public Moeda getMoeda() {
		return moeda;
	}

	public void setMoeda(Moeda moeda) {
		this.moeda = moeda;
	}

	public TipoLancamento getTipo() {
		return tipo;
	}

	public void setTipo(TipoLancamento tipo) {
		this.tipo = tipo;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}
}
