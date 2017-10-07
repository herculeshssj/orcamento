/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.model.PanoramaCadastro;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;
import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="conta")
public class Conta extends EntityPersistence {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1397450215030944411L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50, nullable=false)
	private String descricao;
	
	@Column(length=10)
	private String agencia;
	
	@Column(length=15)
	private String contaCorrente;
	
	@Column(length=15)
	private String contaPoupanca;
	
	@Column(length=10)
	private String variacao;
	
	@Column
	private boolean ativo;
	
	@Column(nullable=false, precision=18, scale=2)
	private double saldoInicial;
	
	@Column(nullable=false, precision=18, scale=2)
	private double saldoFinal;
	
	@Temporal(TemporalType.DATE)
	private Date dataAbertura;
	
	@Temporal(TemporalType.DATE)
	private Date dataFechamento;
	
	@Column(length=15)
	@Enumerated(EnumType.STRING)
	private TipoConta tipoConta;
	
	@Column
	private boolean contaConjunta;
	
	@Column(length=255, nullable=true)
	private String dadosOFX;
	
	@Column
	private boolean fechamentoAutomatico;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name="idMoeda", nullable=false)
	private Moeda moeda;
	
	@ManyToOne
	@JoinColumn(name="idBanco", nullable=true)
	private Banco banco;
	
	@OneToOne
	@JoinColumn(name="idCartao", nullable=true)
	private CartaoCredito cartaoCredito;
	
	@OneToMany(mappedBy="conta", fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<ContaConjunta> contasConjunta = new TreeSet<>();
	
	/*** Atributos usado pelo Panorama dos Cadastros ***/
	@Transient
	private List<PanoramaCadastro> panoramasCadastro;
	
	@Transient
	private int quantPanoramaCadastro;
	
	@Transient
	private double totalPanoramaCadastro;
	
	@Transient
	private int quantDebitoPanoramaCadastro;
	
	@Transient
	private int quantCreditoPanoramaCadastro;
	
	@Transient
	private double totalDebitoPanoramaCadastro;
	
	@Transient
	private double totalCreditoPanoramaCadastro;
	/*** Atributos usado pelo Panorama dos Cadastros ***/
	
	/*** Atributos usados para a Carteira de Investimentos ***/
	
	@Transient
	private List<CategoriaInvestimento> categoriasInvestimento = new ArrayList<>();
	
	/*** Atributos usados para a Carteira de Investimentos ***/
	
	public Conta() {
		ativo = true;
		dataFechamento = null;
		saldoFinal = 0;
		fechamentoAutomatico = false;
	}
	
	@Override
	public String getLabel() {
		if (this.tipoConta.equals(TipoConta.CARTAO)) {
			return cartaoCredito.getLabel();
		} else {
			if (this.ativo) 
				return this.descricao;
			else
				return this.descricao + " [INATIVO]";
		}
	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 50);
		EntityPersistenceUtil.validaCampoNulo("Moeda", this.moeda);
		EntityPersistenceUtil.validaCampoNulo("Tipo de conta", this.tipoConta);
		EntityPersistenceUtil.validaCampoNulo("Data de abertura", this.dataAbertura);
	}
	
	public String getStringTotalPanoramaCadastro() { 
		return this.moeda.getSimboloMonetario() + " " + new DecimalFormat("#,##0.##").format(Util.arredondar(this.totalPanoramaCadastro));
	}
	
	public String getStringTotalCreditoPanoramaCadastro() { 
		return this.moeda.getSimboloMonetario() + " " + new DecimalFormat("#,##0.##").format(Util.arredondar(this.totalCreditoPanoramaCadastro));
	}
	
	public String getStringTotalDebitoPanoramaCadastro() { 
		return this.moeda.getSimboloMonetario() + " " + new DecimalFormat("#,##0.##").format(Util.arredondar(this.totalDebitoPanoramaCadastro));
	}
	
	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getContaCorrente() {
		return contaCorrente;
	}

	public void setContaCorrente(String contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	public String getContaPoupanca() {
		return contaPoupanca;
	}

	public void setContaPoupanca(String contaPoupanca) {
		this.contaPoupanca = contaPoupanca;
	}

	public String getVariacao() {
		return variacao;
	}

	public void setVariacao(String variacao) {
		this.variacao = variacao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public TipoConta getTipoConta() {
		return tipoConta;
	}

	public void setTipoConta(TipoConta tipoConta) {
		this.tipoConta = tipoConta;
	}

	public double getSaldoInicial() {
		return saldoInicial;
	}

	public void setSaldoInicial(double saldoInicial) {
		this.saldoInicial = saldoInicial;
	}

	public double getSaldoFinal() {
		return saldoFinal;
	}

	public void setSaldoFinal(double saldoFinal) {
		this.saldoFinal = saldoFinal;
	}

	public Date getDataAbertura() {
		return dataAbertura;
	}

	public void setDataAbertura(Date dataAbertura) {
		this.dataAbertura = dataAbertura;
	}

	public Date getDataFechamento() {
		return dataFechamento;
	}

	public void setDataFechamento(Date dataFechamento) {
		this.dataFechamento = dataFechamento;
	}

	public CartaoCredito getCartaoCredito() {
		return cartaoCredito;
	}

	public void setCartaoCredito(CartaoCredito cartaoCredito) {
		this.cartaoCredito = cartaoCredito;
	}

	public Moeda getMoeda() {
		return moeda;
	}

	public void setMoeda(Moeda moeda) {
		this.moeda = moeda;
	}

	public List<PanoramaCadastro> getPanoramasCadastro() {
		return panoramasCadastro;
	}

	public void setPanoramasCadastro(List<PanoramaCadastro> panoramasCadastro) {
		this.panoramasCadastro = panoramasCadastro;
	}

	public boolean isContaConjunta() {
		return contaConjunta;
	}

	public void setContaConjunta(boolean contaConjunta) {
		this.contaConjunta = contaConjunta;
	}

	public Set<ContaConjunta> getContasConjunta() {
		return contasConjunta;
	}

	public void setContasConjunta(Set<ContaConjunta> contasConjunta) {
		this.contasConjunta = contasConjunta;
	}

	public int getQuantPanoramaCadastro() {
		return quantPanoramaCadastro;
	}

	public void setQuantPanoramaCadastro(int quantPanoramaCadastro) {
		this.quantPanoramaCadastro = quantPanoramaCadastro;
	}

	public double getTotalPanoramaCadastro() {
		return totalPanoramaCadastro;
	}

	public void setTotalPanoramaCadastro(double totalPanoramaCadastro) {
		this.totalPanoramaCadastro = totalPanoramaCadastro;
	}

	public int getQuantDebitoPanoramaCadastro() {
		return quantDebitoPanoramaCadastro;
	}

	public void setQuantDebitoPanoramaCadastro(int quantDebitoPanoramaCadastro) {
		this.quantDebitoPanoramaCadastro = quantDebitoPanoramaCadastro;
	}

	public int getQuantCreditoPanoramaCadastro() {
		return quantCreditoPanoramaCadastro;
	}

	public void setQuantCreditoPanoramaCadastro(int quantCreditoPanoramaCadastro) {
		this.quantCreditoPanoramaCadastro = quantCreditoPanoramaCadastro;
	}

	public double getTotalDebitoPanoramaCadastro() {
		return totalDebitoPanoramaCadastro;
	}

	public void setTotalDebitoPanoramaCadastro(double totalDebitoPanoramaCadastro) {
		this.totalDebitoPanoramaCadastro = totalDebitoPanoramaCadastro;
	}

	public double getTotalCreditoPanoramaCadastro() {
		return totalCreditoPanoramaCadastro;
	}

	public void setTotalCreditoPanoramaCadastro(double totalCreditoPanoramaCadastro) {
		this.totalCreditoPanoramaCadastro = totalCreditoPanoramaCadastro;
	}

	public String getDadosOFX() {
		return dadosOFX;
	}

	public void setDadosOFX(String dadosOFX) {
		this.dadosOFX = dadosOFX;
	}

	public boolean isFechamentoAutomatico() {
		return fechamentoAutomatico;
	}

	public void setFechamentoAutomatico(boolean fechamentoAutomatico) {
		this.fechamentoAutomatico = fechamentoAutomatico;
	}

	public List<CategoriaInvestimento> getCategoriasInvestimento() {
		return categoriasInvestimento;
	}

	public void setCategoriasInvestimento(List<CategoriaInvestimento> categoriasInvestimento) {
		this.categoriasInvestimento = categoriasInvestimento;
	}
}