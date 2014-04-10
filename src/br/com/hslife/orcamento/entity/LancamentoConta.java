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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;

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
	private Date dataLancamento;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=true)
	private Date dataPagamento;
	
	@Column(length=100, nullable=false)
	private String descricao;
	
	@Column(nullable=true)
	private String historico;
	
	@Column(length=50, nullable=false)
	private String numeroDocumento;
	
	@Column(columnDefinition="text", nullable=true)
	private String observacao;
	
	@Column(nullable=false, precision=18, scale=2)
	private double valorPago;
	
	@Column(length=10)
	@Enumerated(EnumType.STRING)
	private TipoLancamento tipoLancamento;
	
	@Column(nullable=false)
	private boolean agendado;
	
	@Column(nullable=false)
	private boolean quitado;
	
	@Transient
	private boolean selecionado;
	
	@Column(length=32, nullable=true)
	private String hashImportacao;
	
	@Column(nullable=true)
	private String descricaoFavorecido;
	
	@ManyToOne
	@JoinColumn(name="idFavorecido", nullable=true)
	private Favorecido favorecido;
	
	@Column(nullable=true)
	private String descricaoMeioPagamento;
	
	@ManyToOne
	@JoinColumn(name="idConta", nullable=false)
	private Conta conta;
	
	@ManyToOne
	@JoinColumn(name="idCategoria", nullable=true)
	private Categoria categoria;
	
	@Column(nullable=true)
	private String descricaoCategoria;
	
	@ManyToOne
	@JoinColumn(name="idMeioPagamento", nullable=true)
	private MeioPagamento meioPagamento;
	
	@ManyToOne
	@JoinColumn(name="idMoeda", nullable=true)
	private Moeda moeda;
	
	@OneToOne(mappedBy="lancamentoConta")
	private PagamentoPeriodo pagamentoPeriodo;
	
	@Column(length=20, nullable=true)
	private String parcela;
		
	@OneToOne(fetch=FetchType.EAGER, orphanRemoval=true)
	@JoinColumn(name="idArquivo", nullable=true)
	@Cascade(CascadeType.ALL)
	private Arquivo arquivo;
	
	@ManyToOne(optional=true)
	@JoinTable(name="detalhefatura", joinColumns={@JoinColumn(name="idLancamento", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="idFaturaCartao", referencedColumnName="id")})
	private FaturaCartao faturaCartao;
	
	@Transient
	private LancamentoImportado lancamentoImportado;
	
	public LancamentoConta() {
		conta = new Conta();
	}
	
	public LancamentoConta(LancamentoConta lancamento) {
		descricao = lancamento.getDescricao();
		dataLancamento = lancamento.getDataLancamento();
		valorPago = lancamento.getValorPago();
		dataPagamento = lancamento.getDataPagamento();
		observacao = lancamento.getObservacao();
		conta = lancamento.getConta();
		categoria = lancamento.getCategoria();
		meioPagamento = lancamento.getMeioPagamento();
		favorecido = lancamento.getFavorecido();
		tipoLancamento = lancamento.getTipoLancamento();
		agendado = lancamento.isAgendado();
		moeda = lancamento.getMoeda();
		parcela = lancamento.getParcela();
	}

	@Override
	public String getLabel() {
		return this.descricao;
	}

	@Override
	public void validate() throws BusinessException {
		if (this.descricao == null || this.descricao.trim().isEmpty()) {
			throw new BusinessException("Informe uma descrição!");
		}
		
		if (this.descricao.length() > 100) {
			throw new BusinessException("Descrição do lançamento deve ser menor que 100 caracteres!");
		}
		
		if (this.tipoLancamento == null) {
			throw new BusinessException("Informe o tipo de lançamento!");
		}
		
		if (this.conta == null) {
			throw new BusinessException("Informe a conta!");
		}
	}

	public Long getId() {
		return id;
	}
	
	public void setValorPago(double valorPago) {
		this.valorPago = Math.abs(valorPago);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataLancamento() {
		return dataLancamento;
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

	public boolean isAgendado() {
		return agendado;
	}

	public void setAgendado(boolean agendado) {
		this.agendado = agendado;
	}

	public boolean isQuitado() {
		return quitado;
	}

	public void setQuitado(boolean quitado) {
		this.quitado = quitado;
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

	public String getDescricaoFavorecido() {
		return descricaoFavorecido;
	}

	public void setDescricaoFavorecido(String descricaoFavorecido) {
		this.descricaoFavorecido = descricaoFavorecido;
	}

	public String getDescricaoMeioPagamento() {
		return descricaoMeioPagamento;
	}

	public void setDescricaoMeioPagamento(String descricaoMeioPagamento) {
		this.descricaoMeioPagamento = descricaoMeioPagamento;
	}

	public String getDescricaoCategoria() {
		return descricaoCategoria;
	}

	public void setDescricaoCategoria(String descricaoCategoria) {
		this.descricaoCategoria = descricaoCategoria;
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

	public String getParcela() {
		return parcela;
	}

	public void setParcela(String parcela) {
		this.parcela = parcela;
	}

	public void setDataLancamento(Date dataLancamento) {
		this.dataLancamento = dataLancamento;
	}

	public FaturaCartao getFaturaCartao() {
		return faturaCartao;
	}

	public void setFaturaCartao(FaturaCartao faturaCartao) {
		this.faturaCartao = faturaCartao;
	}
}