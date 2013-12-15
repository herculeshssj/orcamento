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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.hslife.orcamento.enumeration.StatusFaturaCartao;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="faturacartao")
public class FaturaCartao extends EntityPersistence {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6519549583113012538L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(precision=18, scale=2)
	private double valorFatura;
	
	@Column(precision=18, scale=2)
	private double valorMinimo;
	
	@Temporal(TemporalType.DATE)
	private Date dataFechamento;
	
	@Temporal(TemporalType.DATE)
	private Date dataVencimento;
	
	@Temporal(TemporalType.DATE)
	private Date dataPagamento;
	
	@Column(precision=18, scale=2)
	private double valorPago;
	
	@Column
	private boolean parcelado;
	
	@Column(precision=18, scale=2)
	private double saldoDevedor;
	
	@Column(length=10, nullable=false)
	@Enumerated(EnumType.STRING)
	private StatusFaturaCartao statusFaturaCartao;
	
	@ManyToOne
	@JoinColumn(name="idConta", nullable=false)
	private Conta conta;
	
	@ManyToOne
	@JoinColumn(name="idMoeda", nullable=false)
	private Moeda moeda;
	
	@OneToOne(fetch=FetchType.EAGER, orphanRemoval=false)
	@JoinColumn(name="idLancamento", nullable=true)
	private LancamentoConta lancamentoPagamento;
	
	@OneToMany(mappedBy="faturaCartao", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<ConversaoMoeda> conversoesMoeda;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=false)
	@JoinTable(name="detalhefatura", joinColumns={@JoinColumn(name="idFaturaCartao", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="idLancamento", referencedColumnName="id")})
	private Set<LancamentoConta> detalheFatura;
	
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="idArquivo", nullable=true)
	private Arquivo arquivo;
	
	public FaturaCartao() {
		conversoesMoeda = new ArrayList<ConversaoMoeda>();
		arquivo = new Arquivo();
		detalheFatura = new HashSet<LancamentoConta>();
	}
	
	@Override
	public String getLabel() {
		switch (this.statusFaturaCartao) {
			case ABERTA : return "Vence em " + Util.formataDataHora(dataVencimento, Util.DATA);
			case FECHADA : return "Vence em " + Util.formataDataHora(dataVencimento, Util.DATA) + " (FECHADA)";
			case FUTURA : return "À vencer em " + Util.formataDataHora(dataVencimento, Util.DATA);
			case QUITADA : return "Quitada em " + Util.formataDataHora(dataPagamento, Util.DATA);
			case VENCIDA : return "Vencida em " + Util.formataDataHora(dataVencimento, Util.DATA);
			default : return "";
		}
	}
	
	@Override
	public void validate() throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getValorFatura() {
		return valorFatura;
	}

	public void setValorFatura(double valorFatura) {
		this.valorFatura = valorFatura;
	}

	public double getValorMinimo() {
		return valorMinimo;
	}

	public void setValorMinimo(double valorMinimo) {
		this.valorMinimo = valorMinimo;
	}

	public Date getDataFechamento() {
		return dataFechamento;
	}

	public void setDataFechamento(Date dataFechamento) {
		this.dataFechamento = dataFechamento;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public double getValorPago() {
		return valorPago;
	}

	public void setValorPago(double valorPago) {
		this.valorPago = valorPago;
	}

	public boolean isParcelado() {
		return parcelado;
	}

	public void setParcelado(boolean parcelado) {
		this.parcelado = parcelado;
	}

	public StatusFaturaCartao getStatusFaturaCartao() {
		return statusFaturaCartao;
	}

	public void setStatusFaturaCartao(StatusFaturaCartao statusFaturaCartao) {
		this.statusFaturaCartao = statusFaturaCartao;
	}

	public Moeda getMoeda() {
		return moeda;
	}

	public void setMoeda(Moeda moeda) {
		this.moeda = moeda;
	}

	public LancamentoConta getLancamentoPagamento() {
		return lancamentoPagamento;
	}

	public void setLancamentoPagamento(LancamentoConta lancamentoPagamento) {
		this.lancamentoPagamento = lancamentoPagamento;
	}

	public List<ConversaoMoeda> getConversoesMoeda() {
		return conversoesMoeda;
	}

	public void setConversoesMoeda(List<ConversaoMoeda> conversoesMoeda) {
		this.conversoesMoeda = conversoesMoeda;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public Set<LancamentoConta> getDetalheFatura() {
		return detalheFatura;
	}

	public void setDetalheFatura(Set<LancamentoConta> detalheFatura) {
		this.detalheFatura = detalheFatura;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public double getSaldoDevedor() {
		return saldoDevedor;
	}

	public void setSaldoDevedor(double saldoDevedor) {
		this.saldoDevedor = saldoDevedor;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}
}