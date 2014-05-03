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

package br.com.hslife.orcamento.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="pagamentoperiodo")
@SuppressWarnings("serial")
public class PagamentoPeriodo extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private boolean pago;
	
	@Column
	private int periodo;
	
	@Column
	private int ano;
	
	@Column(nullable=false)
	@Temporal(TemporalType.DATE)
	private Date dataVencimento;
	
	@Column(nullable=true)
	@Temporal(TemporalType.DATE)
	private Date dataPagamento;
	
	@Column(nullable=false, precision=18, scale=2)
	private double valorPago;
	
	@Column
	private int parcela;
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="idLancamentoConta", nullable=true)
	private LancamentoConta lancamentoConta;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="idLancamentoPeriodico", nullable=false)
	private LancamentoPeriodico lancamentoPeriodico;
	
	@Transient
	private boolean gerarLancamento;
	
	public PagamentoPeriodo() {
	
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getLabel() {
		if (this.lancamentoPeriodico.getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.FIXO)) {
			return "Período " + this.periodo + " / " + this.ano + ", vencimento para " + Util.formataDataHora(this.dataVencimento, Util.DATA);
		} else {
			return "Parcela " + this.parcela + " / " + this.lancamentoPeriodico.getTotalParcela() + ", vencimento para " + Util.formataDataHora(this.dataVencimento, Util.DATA);
		}
	}
	
	@Override
	public void validate() throws BusinessException {
		
	}
	
	@Override
	public int compareTo(EntityPersistence o) {
		if (this.getId() != null) {
			return super.compareToNaturalOrder(o);  
		}
		return 0;
	}

	public boolean isPago() {
		return pago;
	}

	public void setPago(boolean pago) {
		this.pago = pago;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public double getValorPago() {
		return valorPago;
	}

	public void setValorPago(double valorPago) {
		this.valorPago = valorPago;
	}

	public int getParcela() {
		return parcela;
	}

	public void setParcela(int parcela) {
		this.parcela = parcela;
	}

	public LancamentoConta getLancamentoConta() {
		return lancamentoConta;
	}

	public void setLancamentoConta(LancamentoConta lancamentoConta) {
		this.lancamentoConta = lancamentoConta;
	}

	public LancamentoPeriodico getLancamentoPeriodico() {
		return lancamentoPeriodico;
	}

	public void setLancamentoPeriodico(LancamentoPeriodico lancamentoPeriodico) {
		this.lancamentoPeriodico = lancamentoPeriodico;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public boolean isGerarLancamento() {
		return gerarLancamento;
	}

	public void setGerarLancamento(boolean gerarLancamento) {
		this.gerarLancamento = gerarLancamento;
	}
}