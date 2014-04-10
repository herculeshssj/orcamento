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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.com.hslife.orcamento.exception.BusinessException;

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
	private Date dataPagamento;
	
	@Column(nullable=false, precision=18, scale=2)
	private double valorPago;
	
	@Column
	private int parcela;
	
	@OneToOne
	@JoinColumn(name="idLancamentoConta", nullable=true)
	private LancamentoConta lancamentoConta;
	
	@OneToOne
	@JoinColumn(name="idLancamentoPeriodico")
	private LancamentoPeriodico lancamentoPeriodico;
	
	public PagamentoPeriodo() {
	
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return "";
	}
	
	@Override
	public void validate() throws BusinessException {
		
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
}