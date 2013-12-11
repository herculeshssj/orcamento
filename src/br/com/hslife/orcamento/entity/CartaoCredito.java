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

import javax.persistence.CascadeType;
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
import br.com.hslife.orcamento.exception.BusinessException;

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
	
	@Column(length=30)
	private String numeroCartao;
	
	@Column(length=3)
	private String codigoSeguranca;
	
	@Column(nullable=false)
	@Temporal(TemporalType.DATE)
	private Date validade;
	
	@Column(length=50)
	private String nomeCliente;
	
	@Column(length=10)
	@Enumerated(EnumType.STRING)
	private TipoCartao tipoCartao;
	
	@Column(length=15)
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
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="idCartaoSubstituto", nullable=true)
	private CartaoCredito cartaoSubstituto;
	
	@Column
	private boolean ativo;
	
	@Transient
	private double valorUltimaFatura;
	
	@Transient
	private Date dataUltimaFatura;
	
	@Transient
	private boolean faturaQuitada;
	
	public CartaoCredito() {
		ativo = true;
	}
	
	@Override
	public String getLabel() {		
		return descricao + " - " + tipoCartao.toString() + " - " + bandeira.toString();
	}
	
	@Override
	public void validate() throws BusinessException {
		if (this.tipoCartao.equals(TipoCartao.CREDITO)) {
			if (diaVencimentoFatura < 1 || diaVencimentoFatura > 31) throw new BusinessException("Dia de vencimento deve estar entre 1 e 31!");
			if (diaFechamentoFatura < 1 || diaFechamentoFatura > 31) throw new BusinessException("Dia de fechamento deve estar entre 1 e 31!");
		}
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
	
	public void setValorUltimaFatura(double valorUltimaFatura) {
		this.valorUltimaFatura = Math.abs(valorUltimaFatura);
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

	public String getNumeroCartao() {
		return numeroCartao;
	}

	public void setNumeroCartao(String numeroCartao) {
		this.numeroCartao = numeroCartao;
	}

	public String getCodigoSeguranca() {
		return codigoSeguranca;
	}

	public void setCodigoSeguranca(String codigoSeguranca) {
		this.codigoSeguranca = codigoSeguranca;
	}

	public Date getValidade() {
		return validade;
	}

	public void setValidade(Date validade) {
		this.validade = validade;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
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

	public double getValorUltimaFatura() {
		return valorUltimaFatura;
	}

	public Date getDataUltimaFatura() {
		return dataUltimaFatura;
	}

	public void setDataUltimaFatura(Date dataUltimaFatura) {
		this.dataUltimaFatura = dataUltimaFatura;
	}

	public boolean isFaturaQuitada() {
		return faturaQuitada;
	}

	public void setFaturaQuitada(boolean faturaQuitada) {
		this.faturaQuitada = faturaQuitada;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}
}