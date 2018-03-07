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

package br.com.hslife.orcamento.entity;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@SuppressWarnings("serial")
@Entity
@Table(name="dividendo")
public class Dividendo extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="idInvestimento", nullable=false)
	private Investimento investimento;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date dataFechamento;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date dataPagamento;
	
	@Column(precision=18, scale=2)
	private double valorUnitario;
	
	@Column(precision=18, scale=2)
	private double valorPago;
	
	@Column(precision=18, scale=2)
	private double ir;
	
	@Column
	private int quantAcoesApuradas;
	
	public Dividendo() {
		valorPago = 0.0;
		ir = 0.0;
		quantAcoesApuradas = 0;
	}

	@Override
	public String getLabel() {
		return "Rendimento sobre " + quantAcoesApuradas + " ação(ões)/cota(s)";
	}

	@Override
	public void validate() {
		EntityPersistenceUtil.validaCampoNulo("Investimento", this.investimento);
		EntityPersistenceUtil.validaCampoNulo("Data de fechamento", this.dataFechamento);
		EntityPersistenceUtil.validaCampoNulo("Data de pagamento", this.dataPagamento);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Investimento getInvestimento() {
		return investimento;
	}

	public void setInvestimento(Investimento investimento) {
		this.investimento = investimento;
	}

	public void setValorPago(double valorPago) {
		this.valorPago = valorPago;
	}

	public double getIr() {
		return ir;
	}

	public void setIr(double ir) {
		this.ir = ir;
	}

	public int getQuantAcoesApuradas() {
		return quantAcoesApuradas;
	}

	public void setQuantAcoesApuradas(int quantAcoesApuradas) {
		this.quantAcoesApuradas = quantAcoesApuradas;
	}

	public Date getDataFechamento() {
		return dataFechamento;
	}

	public void setDataFechamento(Date dataFechamento) {
		this.dataFechamento = dataFechamento;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public double getValorPago() {
		return valorPago;
	}
}