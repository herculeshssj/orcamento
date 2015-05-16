/***
  
  	Copyright (c) 2012 - 2015 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@gmail.com, ou ainda escreva para 

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

import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.model.PanoramaCadastro;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;

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
	
	@Column(length=10)
	@Enumerated(EnumType.STRING)
	private TipoConta tipoConta;
	
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
	
	@Transient
	private List<PanoramaCadastro> panoramasCadastro;
	
	public Conta() {
		ativo = true;
		dataFechamento = null;
		saldoFinal = 0;
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
	public void validate() throws BusinessException {
		if (this.descricao == null || this.descricao.trim().isEmpty()) {
			throw new BusinessException("Informe uma descrição!");
		}
		
		if (this.descricao.length() > 50) {
			throw new BusinessException("Descrição deve ter no máximo 50 caracteres!");
		}
		
		if (this.usuario == null) {
			throw new BusinessException("Informe o usuário!");
		}
		
		if (this.moeda == null) {
			throw new BusinessException("Informe a moeda!");
		}
		
		EntityPersistenceUtil.validaCampoNulo("Tipo de conta", this.tipoConta);
		EntityPersistenceUtil.validaCampoNulo("Data de abertura", this.dataAbertura);
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
}