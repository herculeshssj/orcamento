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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.hslife.orcamento.rest.json.BancoJson;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="banco")
@SuppressWarnings("serial")
public class Banco extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=100, nullable=false)
	private String nome;
	
	@Column(length=5, nullable=false)
	private String numero;
	
	@Column
	private boolean padrao;
	
	@Column 
	private boolean ativo;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	@Transient
	private Long usuarioID;
	
	public Banco() {
		ativo = true;
	}
	
	private Banco(Builder builder) {
		this.nome = builder.nome;
		this.numero = builder.numero;
		this.padrao = builder.padrao;
		this.ativo = builder.ativo;
		this.usuario = builder.usuario;
	}

	public static class Builder {
		private String nome;
		private String numero;
		private boolean padrao;
		private boolean ativo;
		private Usuario usuario;
		
		public Builder nome(String nome) {
			this.nome = nome;
			return this;
		}
		
		public Builder numero(String numero) {
			this.numero = numero;
			return this;
		}
		
		public Builder padrao(boolean padrao) {
			this.padrao = padrao;
			return this;
		}
		
		public Builder ativo(boolean ativo) {
			this.ativo = ativo;
			return this;
		}
		
		public Builder usuario(Usuario usuario) {
			this.usuario = usuario;
			return this;
		}
		
		public Banco build() {
			return new Banco(this);
		}
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return this.nome;
	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Nome", this.nome, 100);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Número", this.numero, 5);
	}
	
	@Override
	public BancoJson toJson() {
		BancoJson json = new BancoJson();
		json.setAtivo(this.ativo);
		json.setId(this.getId());
		json.setNome(this.nome);
		json.setNumero(this.getNumero());
		json.setPadrao(this.padrao);
		json.setUsuario(this.usuario.getLogin());
		json.setUsuarioId(this.usuario.getId());
		return json;
	}
	
	public static List<BancoJson> toListJson(List<Banco> listEntity) {
		List<BancoJson> listJson = new ArrayList<>();
		for (Banco banco : listEntity) {			
			listJson.add(banco.toJson());
		}
		return listJson;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public boolean isPadrao() {
		return padrao;
	}

	public void setPadrao(boolean padrao) {
		this.padrao = padrao;
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

	public Long getUsuarioID() {
		return usuarioID;
	}

	public void setUsuarioID(Long usuarioID) {
		this.usuarioID = usuarioID;
	}
}