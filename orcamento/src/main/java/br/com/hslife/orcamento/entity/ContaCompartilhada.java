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
import java.util.Calendar;
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
import javax.persistence.Transient;

import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;
import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="contacompartilhada")
public class ContaCompartilhada extends EntityPersistence {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3344743833251916049L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="idConta", nullable=false)
	private Conta conta;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	@Column(length=64, nullable=true)
	private String hashAutorizacao;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=true)
	private Date dataGeracaoHash;
	
	@Transient
	private String loginUsuario;
	
	@Transient
	private String emailUsuario; 
	
	public ContaCompartilhada() {
		
	}
	
	public ContaCompartilhada(Conta conta, Usuario usuario) {
		this.conta = conta;
		this.usuario = usuario;
	}
	
	public void gerarHash() {
		dataGeracaoHash = new Date();
		hashAutorizacao = Util.SHA256(dataGeracaoHash.toString());
	}
	
	public boolean isHashExpirado() {
		// Se não houver hash e nem data definida significa que o
		// compartilhamento é válido
		if (this.dataGeracaoHash == null && this.hashAutorizacao == null) {
			return false;
		}
		
		Calendar temp = Calendar.getInstance();
		temp.setTime(this.dataGeracaoHash);
		temp.add(Calendar.DAY_OF_YEAR, 1);
		
		if (temp.getTime().before(new Date())) 
			return true;
		else
			return false;
	}
	
	public void validate() {
		EntityPersistenceUtil.validaCampoNulo("Conta", this.conta);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Login do usuário", this.loginUsuario, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("E-Mail do usuário", this.emailUsuario, 30);
		
		if (!Util.validaEmail(this.emailUsuario)) {
			throw new ValidationException("E-Mail informado é inválido!");
		}
	}
	
	@Override
	public String getLabel() {
		return "";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getHashAutorizacao() {
		return hashAutorizacao;
	}

	public void setHashAutorizacao(String hashAutorizacao) {
		this.hashAutorizacao = hashAutorizacao;
	}

	public Date getDataGeracaoHash() {
		return dataGeracaoHash;
	}

	public void setDataGeracaoHash(Date dataGeracaoHash) {
		this.dataGeracaoHash = dataGeracaoHash;
	}

	public String getLoginUsuario() {
		return loginUsuario;
	}

	public void setLoginUsuario(String loginUsuario) {
		this.loginUsuario = loginUsuario;
	}

	public String getEmailUsuario() {
		return emailUsuario;
	}

	public void setEmailUsuario(String emailUsuario) {
		this.emailUsuario = emailUsuario;
	}
}
