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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.hslife.orcamento.enumeration.TipoUsuario;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="usuario")
@SuppressWarnings("serial")
public class Usuario extends EntityPersistence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=100, nullable=false)
	private String nome;
	
	@Column(length=50, nullable=false, unique=true)
	private String login;
	
	@Column(length=64, nullable=false)
	private String senha;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCriacao;
	
	@Column(length=40, nullable=true)
	private String email;
	
	@Column(length=10)
	@Enumerated(EnumType.STRING)
	private TipoUsuario tipoUsuario;
	
	@Column
	private boolean ativo;
	
	@Column(length=128, nullable=true)
	private String tokenID;
	
	@Transient
	private boolean logado;
	
	@Transient
	private String confirmaSenha;
	
	public Usuario() {
		dataCriacao = new Date();
		ativo = true;
		login = "";
		tipoUsuario = TipoUsuario.ROLE_USER;
		logado = false;
	}
	
	private Usuario(Builder builder) {
		this.nome = builder.nome;
		this.login = builder.login;
		this.senha = builder.senha;
		this.dataCriacao = builder.dataCriacao;
		this.email = builder.email;
		this.tipoUsuario = builder.tipoUsuario;
		this.ativo = builder.ativo;
		this.tokenID = builder.tokenID;
	}
	
	@Override
	public String getLabel() {
		return this.nome;
	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Nome", this.nome, 100);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Login", this.login, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("E-Mail", this.email, 40);
	}
	
	public static class Builder {
		private String nome;
		private String login;
		private String senha;
		private Date dataCriacao = new Date();
		private String email;
		private TipoUsuario tipoUsuario = TipoUsuario.ROLE_USER;
		private boolean ativo = true;
		private String tokenID;
		
		public Builder nome(String nome) {
			this.nome = nome;
			return this;
		}
		
		public Builder login(String login) {
			this.login = login;
			return this;
		}
		
		public Builder senha(String senha) {
			this.senha = senha;
			return this;
		}
		
		public Builder dataCriacao(Date dataCriacao) {
			this.dataCriacao = dataCriacao;
			return this;
		}
		
		public Builder email(String email) {
			this.email = email;
			return this;
		}
		
		public Builder tipoUsuario(TipoUsuario tipoUsuario) {
			this.tipoUsuario = tipoUsuario;
			return this;
		}
		
		public Builder ativo(boolean ativo) {
			this.ativo = ativo;
			return this;
		}
		
		public Builder tokenID(String tokenID) {
			this.tokenID = tokenID;
			return this;
		}
		
		public Usuario build() {
			return new Usuario(this);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public TipoUsuario getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(TipoUsuario tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isLogado() {
		return logado;
	}

	public void setLogado(boolean logado) {
		this.logado = logado;
	}

	public String getConfirmaSenha() {
		return confirmaSenha;
	}

	public void setConfirmaSenha(String confirmaSenha) {
		this.confirmaSenha = confirmaSenha;
	}

	public String getTokenID() {
		return tokenID;
	}

	public void setTokenID(String tokenID) {
		this.tokenID = tokenID;
	}
}