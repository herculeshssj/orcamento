/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

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
@Table(name="banco")
@SuppressWarnings("serial")
public class ItemGrupoLancamento extends EntityPersistence {

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
	
	public ItemGrupoLancamento() {
		ativo = true;
	}
	
	private ItemGrupoLancamento(Builder builder) {
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
		
		public ItemGrupoLancamento build() {
			return new ItemGrupoLancamento(this);
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
}