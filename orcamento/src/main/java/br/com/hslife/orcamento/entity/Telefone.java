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
@Table(name="telefone")
@SuppressWarnings("serial")
public class Telefone extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50, nullable=false)
	private String descricao;
	
	@Column(length=5, nullable=true)
	private String ddd;
	
	@Column(length=15, nullable=false)
	private String numero;
	
	@Column(length=5, nullable=true)
	private String ramal;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	public Telefone() {
	}
	
	private Telefone(Builder builder) {
		this.descricao = builder.descricao;
		this.ddd = builder.ddd;
		this.numero = builder.numero;
		this.ramal = builder.ramal;
		this.usuario = builder.usuario;		
	}

	@Override
	public String getLabel() {		
		StringBuilder textoBuilder = new StringBuilder();
		
		textoBuilder.append(this.descricao + ":");
		
		if (this.ddd != null && !this.ddd.trim().isEmpty()) {
			textoBuilder.append(" (" + this.ddd + ")");
		}
		
		textoBuilder.append(" ");
		textoBuilder.append(this.numero);
		
		if (this.ramal != null && !this.ramal.trim().isEmpty()) {
			textoBuilder.append(", Ramal: " + this.ramal);
		}
		
		return textoBuilder.toString();
	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("DDD", this.ddd, 5);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Número", this.numero, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Ramal", this.ramal, 5);
	}
	
	public static class Builder {
		private String descricao;
		private String ddd;
		private String numero;
		private String ramal;
		private Usuario usuario;
		
		public Builder descricao(String descricao) {
			this.descricao = descricao;
			return this;
		}
		
		public Builder ddd(String ddd) {
			this.ddd = ddd;
			return this;
		}
		
		public Builder numero(String numero) {
			this.numero = numero;
			return this;
		}
		
		public Builder ramal(String ramal) {
			this.ramal = ramal;
			return this;
		}
		
		public Builder usuario(Usuario usuario) {
			this.usuario = usuario;
			return this;
		}
		
		public Telefone build() {
			return new Telefone(this);
		}
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getDdd() {
		return ddd;
	}

	public void setDdd(String ddd) {
		this.ddd = ddd;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getRamal() {
		return ramal;
	}

	public void setRamal(String ramal) {
		this.ramal = ramal;
	}
}