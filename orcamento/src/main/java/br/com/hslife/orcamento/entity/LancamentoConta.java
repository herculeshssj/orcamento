/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a qualquer
    
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

package br.com.hslife.orcamento.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

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

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import br.com.hslife.orcamento.enumeration.IncrementoClonagemLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.util.DetalheLancamentoComparator;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;


@Entity
@Table(name="lancamentoconta")
public class LancamentoConta extends EntityPersistence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2880755953845455507L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=true)
	private Date dataPagamento;
	
	@Column(length=200, nullable=false)
	private String descricao;
	
	@Column(nullable=true)
	private String historico;
	
	@Column(length=50, nullable=true)
	private String numeroDocumento;
	
	@Column(columnDefinition="text", nullable=true)
	private String observacao;
	
	@Column(nullable=false, precision=18, scale=2)
	private double valorPago;
	
	@Column(length=10, nullable=false)
	@Enumerated(EnumType.STRING)
	private TipoLancamento tipoLancamento;
	
	@Column(length=15, nullable=false)
	@Enumerated(EnumType.STRING)
	private StatusLancamentoConta statusLancamentoConta;
	
	@Column
	private int periodo;
	
	@Column
	private int ano;
	
	@Column(nullable=true)
	@Temporal(TemporalType.DATE)
	private Date dataVencimento;
	
	@Transient
	private boolean selecionado;
	
	@Column(length=32, nullable=true)
	private String hashImportacao;
	
	@ManyToOne
	@JoinColumn(name="idFavorecido", nullable=true)
	private Favorecido favorecido;
	
	@ManyToOne
	@JoinColumn(name="idConta", nullable=false)
	private Conta conta;
	
	@ManyToOne
	@JoinColumn(name="idCategoria", nullable=true)
	private Categoria categoria;
	
	@ManyToOne
	@JoinColumn(name="idMeioPagamento", nullable=true)
	private MeioPagamento meioPagamento;
	
	@ManyToOne
	@JoinColumn(name="idMoeda", nullable=false)
	private Moeda moeda;
	
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="idTaxaConversao", nullable=true)
	private TaxaConversao taxaConversao;
	
	@Column
	private int parcela;
		
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL , orphanRemoval=true)
	@JoinColumn(name="idArquivo", nullable=true)
	private Arquivo arquivo;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="idFaturaCartao", nullable=true)
	private FaturaCartao faturaCartao;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="idLancamentoPeriodico", nullable=true)
	private LancamentoPeriodico lancamentoPeriodico;
	
	@ManyToOne
	@JoinColumn(name="idFechamentoPeriodo", nullable=true)
	private FechamentoPeriodo fechamentoPeriodo;
	
	@Transient
	private LancamentoImportado lancamentoImportado;
	
	@OneToMany(fetch=FetchType.EAGER, orphanRemoval=true, cascade=CascadeType.ALL)	
	@Sort(type=SortType.COMPARATOR, comparator=DetalheLancamentoComparator.class)
	private SortedSet<DetalheLancamento> detalhes;
	
	public LancamentoConta() {
		conta = new Conta();
		tipoLancamento = TipoLancamento.DESPESA;
		statusLancamentoConta = StatusLancamentoConta.REGISTRADO;
	}
	
	public LancamentoConta(LancamentoConta lancamento) {
		descricao = lancamento.getDescricao();
		valorPago = lancamento.getValorPago();
		dataPagamento = lancamento.getDataPagamento();
		dataVencimento = lancamento.getDataVencimento();
		observacao = lancamento.getObservacao();
		conta = lancamento.getConta();
		categoria = lancamento.getCategoria();
		meioPagamento = lancamento.getMeioPagamento();
		favorecido = lancamento.getFavorecido();
		tipoLancamento = lancamento.getTipoLancamento();
		moeda = lancamento.getMoeda();
		if (lancamento.getDataPagamento().before(new Date())) {
			statusLancamentoConta = StatusLancamentoConta.REGISTRADO;
		} else {
			statusLancamentoConta = StatusLancamentoConta.AGENDADO;
		}
		
		// Taxa de conversao
		if (lancamento.getTaxaConversao() != null) {
			taxaConversao = new TaxaConversao(lancamento.getMoeda(), 
					lancamento.getValorPago(), 
					lancamento.getTaxaConversao().getMoedaDestino(), 
					lancamento.getTaxaConversao().getTaxaConversao());
		}
	}

	@Override
	public String getLabel() {
		return this.descricao;
	}

	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 100);
		EntityPersistenceUtil.validaCampoNulo("Tipo de lançamento", this.tipoLancamento);
		EntityPersistenceUtil.validaCampoNulo("Conta", this.conta);
		EntityPersistenceUtil.validaCampoNulo("Data de pagamento", this.dataPagamento);
	}

	public Long getId() {
		return id;
	}
	
	public List<LancamentoConta> clonarLancamentos(int quantidade, IncrementoClonagemLancamento incremento) {
		List<LancamentoConta> lancamentos = new ArrayList<LancamentoConta>();
		LancamentoConta lancamentoDestino;
		for (int i = 1; i <= quantidade; i++) {
			lancamentoDestino = new LancamentoConta(this);
			Calendar temp = Calendar.getInstance();				
			temp.setTime(lancamentoDestino.getDataPagamento());
			switch (incremento) {
				case DIA : temp.add(Calendar.DAY_OF_YEAR, i); break;
				case MES : temp.add(Calendar.MONTH, i); break;
				case ANO : temp.add(Calendar.YEAR, i); break;
				case BIMESTRE : temp.add(Calendar.MONTH, i * 2); break;
				case TRIMESTRE : temp.add(Calendar.MONTH, i * 3); break;
				case QUADRIMESTRE : temp.add(Calendar.MONTH, i *  4); break;
				case SEMESTRE : temp.add(Calendar.MONTH, i * 6); break;
				default : // faz nada com a data de pagamento
			}
			lancamentoDestino.setDataPagamento(temp.getTime());
			if (lancamentoDestino.getDataPagamento().before(new Date())) {
				lancamentoDestino.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
			} else {
				lancamentoDestino.setStatusLancamentoConta(StatusLancamentoConta.AGENDADO);
			}
			lancamentos.add(lancamentoDestino);
		}
		return lancamentos;
	}
	
	public void setValorPago(double valorPago) {
		this.valorPago = Math.abs(valorPago);
	}
	
	public double getTotalDetalhado() {
		double resultado = 0.0;
		if (detalhes != null && !detalhes.isEmpty()) {
			for (DetalheLancamento detalhe : detalhes) {
				resultado += detalhe.getValor();
			}
		}		
		return resultado;
	}
	
	public double getTotalADetalhar() {
		double resultado = 0.0;
		if (detalhes != null && !detalhes.isEmpty()) {
			for (DetalheLancamento detalhe : detalhes) {
				resultado += detalhe.getValor();
			}
		}		
		BigDecimal result = new BigDecimal(this.valorPago - resultado).setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return result.doubleValue();
	}
	
	public double getTotalCredito() {
		double resultado = 0.0;
		if (detalhes != null && !detalhes.isEmpty()) {
			for (DetalheLancamento detalhe : detalhes) {
				if (detalhe.getValor() > 0) {
					resultado += detalhe.getValor();
				}
			}
		}
		return resultado;
	}
	
	public double getTotalDebito() {
		double resultado = 0.0;
		if (detalhes != null && !detalhes.isEmpty()) {
			for (DetalheLancamento detalhe : detalhes) {
				if (detalhe.getValor() <= 0) {
					resultado += detalhe.getValor();
				}
			}
		}
		return resultado;
	}
	
	public boolean isPossuiAnexo() {
		return this.getArquivo() != null && this.getArquivo().getDados() != null && this.getArquivo().getDados().length != 0;
	}
	
	public boolean isHasDetalhes() {
		return this.detalhes != null && !this.detalhes.isEmpty();
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getHistorico() {
		return historico;
	}

	public void setHistorico(String historico) {
		this.historico = historico;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public double getValorPago() {
		return valorPago;
	}

	public TipoLancamento getTipoLancamento() {
		return tipoLancamento;
	}

	public void setTipoLancamento(TipoLancamento tipoLancamento) {
		this.tipoLancamento = tipoLancamento;
	}

	public String getHashImportacao() {
		return hashImportacao;
	}

	public void setHashImportacao(String hashImportacao) {
		this.hashImportacao = hashImportacao;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Favorecido getFavorecido() {
		return favorecido;
	}

	public void setFavorecido(Favorecido favorecido) {
		this.favorecido = favorecido;
	}

	public MeioPagamento getMeioPagamento() {
		return meioPagamento;
	}

	public void setMeioPagamento(MeioPagamento meioPagamento) {
		this.meioPagamento = meioPagamento;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public LancamentoImportado getLancamentoImportado() {
		return lancamentoImportado;
	}

	public void setLancamentoImportado(LancamentoImportado lancamentoImportado) {
		this.lancamentoImportado = lancamentoImportado;
	}

	public Moeda getMoeda() {
		return moeda;
	}

	public void setMoeda(Moeda moeda) {
		this.moeda = moeda;
	}

	public FaturaCartao getFaturaCartao() {
		return faturaCartao;
	}

	public void setFaturaCartao(FaturaCartao faturaCartao) {
		this.faturaCartao = faturaCartao;
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

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public int getParcela() {
		return parcela;
	}

	public void setParcela(int parcela) {
		this.parcela = parcela;
	}

	public LancamentoPeriodico getLancamentoPeriodico() {
		return lancamentoPeriodico;
	}

	public void setLancamentoPeriodico(LancamentoPeriodico lancamentoPeriodico) {
		this.lancamentoPeriodico = lancamentoPeriodico;
	}

	public StatusLancamentoConta getStatusLancamentoConta() {
		return statusLancamentoConta;
	}

	public void setStatusLancamentoConta(StatusLancamentoConta statusLancamentoConta) {
		this.statusLancamentoConta = statusLancamentoConta;
	}

	public FechamentoPeriodo getFechamentoPeriodo() {
		return fechamentoPeriodo;
	}

	public void setFechamentoPeriodo(FechamentoPeriodo fechamentoPeriodo) {
		this.fechamentoPeriodo = fechamentoPeriodo;
	}

	public SortedSet<DetalheLancamento> getDetalhes() {
		return detalhes;
	}

	public void setDetalhes(SortedSet<DetalheLancamento> detalhes) {
		this.detalhes = detalhes;
	}

	public TaxaConversao getTaxaConversao() {
		return taxaConversao;
	}

	public void setTaxaConversao(TaxaConversao taxaConversao) {
		this.taxaConversao = taxaConversao;
	}
}