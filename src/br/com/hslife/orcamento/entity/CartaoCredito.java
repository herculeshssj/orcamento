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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import br.com.hslife.orcamento.enumeration.Abrangencia;
import br.com.hslife.orcamento.enumeration.Bandeira;
import br.com.hslife.orcamento.enumeration.TipoCartao;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="cartaocredito")
@SuppressWarnings("serial")
public class CartaoCredito extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50, nullable=false)
	private String descricao;
	
	@Column(nullable=false, precision=18, scale=2)
	private double limiteCartao;
	
	@Column
	private double juros;
	
	@Column
	private double multa;
	
	@Column(nullable=false)
	private int diaVencimentoFatura;
	
	@Column(nullable=false)
	private int diaFechamentoFatura;
	
	@Column(nullable=false, precision=18, scale=2)
	private double limiteSaque;
	
	@Column(nullable=false)
	@Temporal(TemporalType.DATE)
	private Date validade;
	
	@Column(length=10, nullable=false)
	@Enumerated(EnumType.STRING)
	private TipoCartao tipoCartao;
	
	@Column(length=15, nullable=false)
	@Enumerated(EnumType.STRING)
	private Abrangencia abrangencia;
	
	@Column(length=30)
	@Enumerated(EnumType.STRING)
	private Bandeira bandeira;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name="idBanco", nullable=true)
	private Banco banco;
	
	@OneToOne(mappedBy="cartaoCredito")
	private Conta conta;
	
	@OneToOne
	@JoinColumn(name="idCartaoSubstituto", nullable=true)
	private CartaoCredito cartaoSubstituto;
	
	@Column
	private boolean ativo;
	
	@Column(length=40, nullable=true)
	private String numeroCartao;
	
	@Column(length=40, nullable=true)
	private String numeroCartaoDebito;
	
	@Transient
	private int mesValidade;
	
	@Transient
	private int anoValidade;
	
	@Transient
	private Moeda moeda;
	
	public CartaoCredito() {
		ativo = true;
	}
	
	@Override
	public String getLabel() {		
		return descricao + " - " + tipoCartao.toString() + " - " + (this.bandeira == null ? "Nenhuma" : this.bandeira.toString());
	}
	
	@Override
	public void validate() throws ApplicationException {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 50);
		EntityPersistenceUtil.validaCampoNulo("Tipo de Cartão", this.tipoCartao);
		EntityPersistenceUtil.validaCampoNulo("Abrangência", this.abrangencia);
		
		if (this.tipoCartao.equals(TipoCartao.CREDITO)) {
			EntityPersistenceUtil.validaCampoInteiroZerado("Dia de vencimento da fatura", this.diaVencimentoFatura);
			EntityPersistenceUtil.validaCampoInteiroZerado("Dia de fechamento da fatura", this.diaFechamentoFatura);
			
			if (diaVencimentoFatura < 1 || diaVencimentoFatura > 31) throw new ApplicationException("Dia de vencimento deve estar entre 1 e 31!");
			if (diaFechamentoFatura < 1 || diaFechamentoFatura > 31) throw new ApplicationException("Dia de fechamento deve estar entre 1 e 31!");
		}
	}
	
	public Conta createConta() {
		// Cria uma nova conta para o cartão de crédito
		Conta conta = new Conta();
		conta.setBanco(this.getBanco());
		conta.setCartaoCredito(this);
		conta.setDataAbertura(new Date());
		conta.setDescricao(this.getDescricao());
		conta.setSaldoInicial(0);
		conta.setTipoConta(TipoConta.CARTAO);
		conta.setUsuario(this.getUsuario());
		conta.setMoeda(this.getMoeda());
		this.setConta(conta);
		return this.getConta();
	}

	public Long getId() {
		return id;
	}
	
	public void setLimiteSaque(double limiteSaque) {
		this.limiteSaque = Math.abs(limiteSaque);
	}
	
	public void setLimiteCartao(double limiteCartao) {
		this.limiteCartao = Math.abs(limiteCartao);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getLimiteCartao() {
		return limiteCartao;
	}

	public double getJuros() {
		return juros;
	}

	public void setJuros(double juros) {
		this.juros = juros;
	}

	public double getMulta() {
		return multa;
	}

	public void setMulta(double multa) {
		this.multa = multa;
	}

	public double getLimiteSaque() {
		return limiteSaque;
	}

	public Date getValidade() {
		return validade;
	}

	public void setValidade(Date validade) {
		this.validade = validade;
	}

	public TipoCartao getTipoCartao() {
		return tipoCartao;
	}

	public void setTipoCartao(TipoCartao tipoCartao) {
		this.tipoCartao = tipoCartao;
	}

	public Abrangencia getAbrangencia() {
		return abrangencia;
	}

	public void setAbrangencia(Abrangencia abrangencia) {
		this.abrangencia = abrangencia;
	}

	public Bandeira getBandeira() {
		return bandeira;
	}

	public void setBandeira(Bandeira bandeira) {
		this.bandeira = bandeira;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public int getDiaVencimentoFatura() {
		return diaVencimentoFatura;
	}

	public void setDiaVencimentoFatura(int diaVencimentoFatura) {
		this.diaVencimentoFatura = diaVencimentoFatura;
	}

	public int getDiaFechamentoFatura() {
		return diaFechamentoFatura;
	}

	public void setDiaFechamentoFatura(int diaFechamentoFatura) {
		this.diaFechamentoFatura = diaFechamentoFatura;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	public CartaoCredito getCartaoSubstituto() {
		return cartaoSubstituto;
	}

	public void setCartaoSubstituto(CartaoCredito cartaoSubstituto) {
		this.cartaoSubstituto = cartaoSubstituto;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public int getMesValidade() {
		return mesValidade;
	}

	public void setMesValidade(int mesValidade) {
		this.mesValidade = mesValidade;
	}

	public int getAnoValidade() {
		return anoValidade;
	}

	public void setAnoValidade(int anoValidade) {
		this.anoValidade = anoValidade;
	}

	public String getNumeroCartao() {
		return numeroCartao;
	}

	public void setNumeroCartao(String numeroCartao) {
		this.numeroCartao = numeroCartao;
	}

	public String getNumeroCartaoDebito() {
		return numeroCartaoDebito;
	}

	public void setNumeroCartaoDebito(String numeroCartaoDebito) {
		this.numeroCartaoDebito = numeroCartaoDebito;
	}

	public Moeda getMoeda() {
		return moeda;
	}

	public void setMoeda(Moeda moeda) {
		this.moeda = moeda;
	}
}