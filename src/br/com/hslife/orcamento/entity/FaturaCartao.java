/***
  
  	Copyright (c) 2012 - 2016 Hércules S. S. José

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

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento ou escreva 
    
    para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor, 
    
    Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor entre  

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.entity;

import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.hslife.orcamento.enumeration.StatusFaturaCartao;
import br.com.hslife.orcamento.exception.BusinessException;

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
	
	@Column(nullable=false)
	private int mes;
	
	@Column(nullable=false)
	private int ano;
	
	@Column(precision=18, scale=2)
	private double saldoDevedor;
	
	@Column(length=10, nullable=false)
	@Enumerated(EnumType.STRING)
	private StatusFaturaCartao statusFaturaCartao;
	
	@ManyToOne
	@JoinColumn(name="idConta", nullable=false)
	private Conta conta;
	
	@OneToOne(fetch=FetchType.EAGER, orphanRemoval=false)
	@JoinColumn(name="idLancamento", nullable=true)
	private LancamentoConta lancamentoPagamento;
	
	@OneToMany(mappedBy="faturaCartao", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<ConversaoMoeda> conversoesMoeda;
	
	@OneToMany(mappedBy="faturaCartao", fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=false)
	private Set<LancamentoConta> detalheFatura;
	
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="idArquivo", nullable=true)
	private Arquivo arquivo;
	
	@Transient
	private Categoria categoriaSelecionada;
	
	@Transient
	private Favorecido favorecidoSelecionado;
	
	@Transient
	private MeioPagamento meioPagamentoSelecionado;
	
	public FaturaCartao() {
		conversoesMoeda = new ArrayList<ConversaoMoeda>();
		detalheFatura = new HashSet<LancamentoConta>();
		statusFaturaCartao = StatusFaturaCartao.ABERTA;
	}
	
	@Override
	public String getLabel() {
		switch (this.statusFaturaCartao) {
			case ABERTA : return "Fatura " + mes + "/" + ano + " (ATUAL)";
			case FECHADA : return "Fatura " + mes + "/" + ano + " (FECHADA)";
			case FUTURA : return "Fatura " + mes + "/" + ano + " (FUTURA)";
			case QUITADA : return "Fatura " + mes + "/" + ano;
			case VENCIDA : return "Fatura " + mes + "/" + ano + " (VENCIDA)";
			default : return "";
		}
	}
	
	@Override
	public void validate() throws BusinessException {
		//TODO implementar as validações da entidade
	}
	
	public void adicionarTodosLancamentos(Collection<LancamentoConta> lancamentos) {
		List<LancamentoConta> detalhes = new ArrayList<LancamentoConta>(detalheFatura);
		
		// Itera a lista de detalhes para setar a fatura como nulo
		for (LancamentoConta l : detalhes) {
			l.setFaturaCartao(null);
		}
		
		// Itera a lista de lançamentos recebidos
		for (LancamentoConta l : lancamentos) {
			// Verifca se a lista de detalhes contém o lançamento
			if (detalhes.contains(l)) {
				// Seta a fatura no lançamentos
				detalhes.get(detalhes.indexOf(l)).setFaturaCartao(this);
			} else {
				// Seta a fatura no lançamento e adiciona na lista de detalhes
				l.setFaturaCartao(this);
				detalhes.add(l);
			}
		}
		
		// Atualiza o Set detalheFatura
		detalheFatura.clear();
		detalheFatura.addAll(detalhes);
	}
	
	public boolean isPossuiAnexo() {
		return this.getArquivo() != null && this.getArquivo().getDados() != null && this.getArquivo().getDados().length != 0;
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

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public Categoria getCategoriaSelecionada() {
		return categoriaSelecionada;
	}

	public void setCategoriaSelecionada(Categoria categoriaSelecionada) {
		this.categoriaSelecionada = categoriaSelecionada;
	}

	public Favorecido getFavorecidoSelecionado() {
		return favorecidoSelecionado;
	}

	public void setFavorecidoSelecionado(Favorecido favorecidoSelecionado) {
		this.favorecidoSelecionado = favorecidoSelecionado;
	}

	public MeioPagamento getMeioPagamentoSelecionado() {
		return meioPagamentoSelecionado;
	}

	public void setMeioPagamentoSelecionado(MeioPagamento meioPagamentoSelecionado) {
		this.meioPagamentoSelecionado = meioPagamentoSelecionado;
	}
}