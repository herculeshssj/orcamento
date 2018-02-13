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

import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="taxaconversao")
public class TaxaConversao extends EntityPersistence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6634243511386828754L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="idMoedaOrigem", nullable=false)
	private Moeda moedaOrigem;
	
	@Column(nullable=false, precision=18, scale=2)
	private double valorMoedaOrigem;
	
	@Column(nullable=false, precision=18, scale=4)
	private double taxaConversao;
	
	@ManyToOne
	@JoinColumn(name="idMoedaDestino", nullable=false)
	private Moeda moedaDestino;
	
	@Column(nullable=false, precision=18, scale=2)
	private double valorMoedaDestino;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date dataConversao;
	
	public TaxaConversao() {
		taxaConversao = 1.0000;
		dataConversao = new Date();
	}
	
	public TaxaConversao(Moeda moedaOrigem, double valorMoedaOrigem, Moeda moedaDestino, double taxaConversao) {
		this.moedaOrigem = moedaOrigem;
		this.valorMoedaOrigem = valorMoedaOrigem;
		this.taxaConversao = taxaConversao;
		this.moedaDestino = moedaDestino;
		this.valorMoedaDestino = Util.arredondar(valorMoedaOrigem * taxaConversao);
		this.dataConversao = new Date();
	}
	
	public void atualizaTaxaConversao(double valorMoedaOrigem) {
		this.valorMoedaOrigem = valorMoedaOrigem;
		this.valorMoedaDestino = Util.arredondar(valorMoedaOrigem * taxaConversao);
		this.dataConversao = new Date();
	}
	
	public void atualizaTaxaConversao(double valorMoedaOrigem, double taxaConversao) {
		this.valorMoedaOrigem = valorMoedaOrigem;
		this.taxaConversao = taxaConversao;
		this.valorMoedaDestino = Util.arredondar(valorMoedaOrigem * taxaConversao);
		this.dataConversao = new Date();
	}
	
	public void atualizaTaxaConversao(Moeda moedaOrigem, double valorMoedaOrigem, Moeda moedaDestino, double taxaConversao) {
		this.moedaOrigem = moedaOrigem;
		this.valorMoedaOrigem = valorMoedaOrigem;
		this.taxaConversao = taxaConversao;
		this.moedaDestino = moedaDestino;
		this.valorMoedaDestino = Util.arredondar(valorMoedaOrigem * taxaConversao);
		this.dataConversao = new Date();
	}
	
	@Override
	public String getLabel() {
		return "";
	}

	@Override
	public void validate() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Moeda getMoedaOrigem() {
		return moedaOrigem;
	}

	public void setMoedaOrigem(Moeda moedaOrigem) {
		this.moedaOrigem = moedaOrigem;
	}

	public double getValorMoedaOrigem() {
		return valorMoedaOrigem;
	}

	public void setValorMoedaOrigem(double valorMoedaOrigem) {
		this.valorMoedaOrigem = valorMoedaOrigem;
	}

	public double getTaxaConversao() {
		return taxaConversao;
	}

	public void setTaxaConversao(double taxaConversao) {
		this.taxaConversao = taxaConversao;
	}

	public Moeda getMoedaDestino() {
		return moedaDestino;
	}

	public void setMoedaDestino(Moeda moedaDestino) {
		this.moedaDestino = moedaDestino;
	}

	public double getValorMoedaDestino() {
		return valorMoedaDestino;
	}

	public void setValorMoedaDestino(double valorMoedaDestino) {
		this.valorMoedaDestino = valorMoedaDestino;
	}

	public Date getDataConversao() {
		return dataConversao;
	}

	public void setDataConversao(Date dataConversao) {
		this.dataConversao = dataConversao;
	}
}
