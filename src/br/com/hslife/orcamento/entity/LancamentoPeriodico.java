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
import java.util.List;

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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import br.com.hslife.orcamento.enumeration.PeriodoLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;

@Entity
@Table(name="lancamentoperiodico")
public class LancamentoPeriodico extends EntityPersistence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2880755953845455507L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
	@Temporal(TemporalType.DATE)
	private Date dataAquisicao;
	
	@Column(length=50, nullable=false)
	private String descricao;
	
	@Column(columnDefinition="text", nullable=true)
	private String observacao;
	
	@Column(nullable=false, precision=18, scale=2)
	private double valorParcela;
	
	@Column(nullable=true, precision=18, scale=2)
	private Double valorCompra;
	
	@Column(length=10, nullable=false)
	@Enumerated(EnumType.STRING)
	private TipoLancamento tipoLancamento;
	
	@Column(length=15, nullable=false)
	@Enumerated(EnumType.STRING)
	private StatusLancamento statusLancamento;
	
	@Column(nullable=true)
	private Integer totalParcela;
	
	@Column
	private int diaVencimento;
	
	@Column(length=10, nullable=false)
	@Enumerated(EnumType.STRING)
	private TipoLancamentoPeriodico tipoLancamentoPeriodico;
	
	@Column(length=10, nullable=false)
	@Enumerated(EnumType.STRING)
	private PeriodoLancamento periodoLancamento;
	
	@Column(nullable=true)
	private Integer parcelasPagas;
	
	@ManyToOne
	@JoinColumn(name="idConta", nullable=false)
	private Conta conta;
	
	@ManyToOne
	@JoinColumn(name="idCategoria", nullable=true)
	private Categoria categoria;
	
	@ManyToOne
	@JoinColumn(name="idFavorecido", nullable=true)
	private Favorecido favorecido;
	
	@ManyToOne
	@JoinColumn(name="idMeioPagamento", nullable=true)
	private MeioPagamento meioPagamento;
	
	@ManyToOne
	@JoinColumn(name="idMoeda", nullable=true)
	private Moeda moeda;
	
	@OneToOne(fetch=FetchType.EAGER, orphanRemoval=true)
	@JoinColumn(name="idArquivo", nullable=true)
	@Cascade(CascadeType.ALL)
	private Arquivo arquivo;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	@OneToMany(mappedBy="lancamentoPeriodico", fetch=FetchType.EAGER)
	private List<LancamentoConta> lancamentos;
	
	public LancamentoPeriodico() {
		
	}
	
	@Override
	public String getLabel() {
		return this.descricao;
	}

	@Override
	public void validate() throws BusinessException {
		
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataAquisicao() {
		return dataAquisicao;
	}

	public void setDataAquisicao(Date dataAquisicao) {
		this.dataAquisicao = dataAquisicao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public double getValorParcela() {
		return valorParcela;
	}

	public void setValorParcela(double valorParcela) {
		this.valorParcela = valorParcela;
	}

	public Double getValorCompra() {
		return valorCompra;
	}

	public void setValorCompra(Double valorCompra) {
		this.valorCompra = valorCompra;
	}

	public TipoLancamento getTipoLancamento() {
		return tipoLancamento;
	}

	public void setTipoLancamento(TipoLancamento tipoLancamento) {
		this.tipoLancamento = tipoLancamento;
	}

	public StatusLancamento getStatusLancamento() {
		return statusLancamento;
	}

	public void setStatusLancamento(StatusLancamento statusLancamento) {
		this.statusLancamento = statusLancamento;
	}

	public Integer getTotalParcela() {
		return totalParcela;
	}

	public void setTotalParcela(Integer totalParcela) {
		this.totalParcela = totalParcela;
	}

	public int getDiaVencimento() {
		return diaVencimento;
	}

	public void setDiaVencimento(int diaVencimento) {
		this.diaVencimento = diaVencimento;
	}

	public TipoLancamentoPeriodico getTipoLancamentoPeriodico() {
		return tipoLancamentoPeriodico;
	}

	public void setTipoLancamentoPeriodico(
			TipoLancamentoPeriodico tipoLancamentoPeriodico) {
		this.tipoLancamentoPeriodico = tipoLancamentoPeriodico;
	}

	public PeriodoLancamento getPeriodoLancamento() {
		return periodoLancamento;
	}

	public void setPeriodoLancamento(PeriodoLancamento periodoLancamento) {
		this.periodoLancamento = periodoLancamento;
	}

	public Integer getParcelasPagas() {
		return parcelasPagas;
	}

	public void setParcelasPagas(Integer parcelasPagas) {
		this.parcelasPagas = parcelasPagas;
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

	public Moeda getMoeda() {
		return moeda;
	}

	public void setMoeda(Moeda moeda) {
		this.moeda = moeda;
	}

	public Arquivo getArquivo() {
		return arquivo;
	}

	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<LancamentoConta> getLancamentos() {
		return lancamentos;
	}

	public void setLancamentos(List<LancamentoConta> lancamentos) {
		this.lancamentos = lancamentos;
	}
}