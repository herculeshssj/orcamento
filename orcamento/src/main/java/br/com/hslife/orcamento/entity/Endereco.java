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

para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor,

Boston, MA  02110-1301, USA.


Para mais informações sobre o programa Orçamento Doméstico e seu autor

entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/

package br.com.hslife.orcamento.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="endereco")
@SuppressWarnings("serial")
public class Endereco extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50, nullable=false)
	private String descricao;
	
	@Column(length=30, nullable=false)
	private String tipoLogradouro;
	
	@Column(length=150, nullable=false)
	private String logradouro;
	
	@Column(length=10, nullable=true)
	private String numero;
	
	@Column(length=50, nullable=true)
	private String complemento;
	
	@Column(length=50, nullable=false)
	private String bairro;
	
	@Column(length=100, nullable=false)
	private String cidade;
	
	@Column(length=2, nullable=false)
	private String estado;
	
	@Column(length=8, nullable=true)
	private String cep;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=true)
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name="idFavorecido", nullable=true)
	private Favorecido favorecido;
	
	public Endereco() {
		
	}
	
	private Endereco(Builder builder) {
		this.descricao = builder.descricao;
		this.tipoLogradouro = builder.tipoLogradouro;
		this.logradouro = builder.logradouro;
		this.complemento = builder.complemento;
		this.numero = builder.numero;
		this.bairro = builder.bairro;
		this.cidade = builder.cidade;
		this.estado = builder.estado;
		this.cep = builder.cep;
		this.usuario = builder.usuario;
	}

	@Override
	public String getLabel() {
		StringBuilder textoBuild = new StringBuilder();
		
		textoBuild.append(descricao);
		textoBuild.append(": ");
		textoBuild.append(tipoLogradouro);
		textoBuild.append(" ");
		textoBuild.append(logradouro);
		textoBuild.append(", ");
		textoBuild.append(numero);
		
		if (complemento != null && !complemento.trim().isEmpty()) {
			textoBuild.append(" - ");
			textoBuild.append(complemento);
		}
			
		textoBuild.append(" - ");
		textoBuild.append(bairro);
		textoBuild.append(" - ");
		textoBuild.append(cidade);
		textoBuild.append(", ");
		textoBuild.append(estado);
		
		if (cep != null && !cep.trim().isEmpty()) {
			textoBuild.append(" - CEP: ");
			textoBuild.append(cep);
		}
		
		return textoBuild.toString();
	}
	
	@Override
	public void validate() {		
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Tipo de logradouro", this.tipoLogradouro, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Logradouro", this.logradouro, 150);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Número", this.numero, 10);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Complemento", this.complemento, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Bairro", this.bairro, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Cidade", this.cidade, 100);
		EntityPersistenceUtil.validaTamanhoExatoCampoStringObrigatorio("Estado", this.estado, 2);
		EntityPersistenceUtil.validaTamanhoExatoCampoStringOpcional("CEP", this.cep, 8);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 50);
	}
	
	public static class Builder {

		private String descricao;
		private String tipoLogradouro;
		private String logradouro;
		private String bairro;
		private String cidade;
		private String estado;
		private Usuario usuario;
		private String numero = "";
		private String complemento = "";
		private String cep = "";
		
		public Builder(String descricao) {
			this.descricao = descricao;
		}
		
		public Builder tipoLogradouro(String tipoLogradouro) {
			this.tipoLogradouro = tipoLogradouro;
			return this;
		}
		
		public Builder logradouro(String logradouro) {
			this.logradouro = logradouro;
			return this;
		}
		
		public Builder bairro(String bairro) {
			this.bairro = bairro;
			return this;
		}
		
		public Builder cidade(String cidade) {
			this.cidade = cidade;
			return this;
		}
		
		public Builder estado(String estado) {
			this.estado = estado;
			return this;
		}
		
		public Builder numero(String numero) {
			this.numero = numero;
			return this;
		}
		
		public Builder complemento(String complemento) {
			this.complemento = complemento;
			return this;
		}
		
		public Builder cep(String cep) {
			this.cep = cep;
			return this;
		}
		
		public Builder usuario(Usuario usuario) {
			this.usuario = usuario;
			return this;
		}
		
		public Endereco build() {
			return new Endereco(this);
		}
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTipoLogradouro() {
		return tipoLogradouro;
	}

	public void setTipoLogradouro(String tipoLogradouro) {
		this.tipoLogradouro = tipoLogradouro;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Favorecido getFavorecido() {
		return favorecido;
	}

	public void setFavorecido(Favorecido favorecido) {
		this.favorecido = favorecido;
	}
}
