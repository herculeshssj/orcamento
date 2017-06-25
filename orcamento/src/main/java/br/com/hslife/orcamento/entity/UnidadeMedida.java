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

import br.com.hslife.orcamento.rest.json.AbstractJson;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="unidademedida")
@SuppressWarnings("serial")
public class UnidadeMedida extends EntityPersistence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false, length=50)
	private String descricao;
	
	@Column(nullable=false, length=10)
	private String sigla;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;

	public UnidadeMedida() {
		
	}
	
	private UnidadeMedida(Builder builder) {
		this.descricao = builder.descricao;
		this.sigla = builder.sigla;
		this.usuario = builder.usuario;
	}
	
	@Override
	public String getLabel() {
		return this.descricao + " (" + sigla + ")";
	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Sigla", this.sigla, 10);
	}
	
	public static class Builder {
		private String descricao;
		private String sigla;
		private Usuario usuario;
		
		public Builder descricao(String descricao) {
			this.descricao = descricao;
			return this;
		}
		
		public Builder sigla(String sigla) {
			this.sigla = sigla;
			return this;
		}
		
		public Builder usuario(Usuario usuario) {
			this.usuario = usuario;
			return this;
		}
		
		public UnidadeMedida build() {
			return new UnidadeMedida(this);
		}
	}
	
	@Override
	public AbstractJson toJson() {
		// TODO Auto-generated method stub
		return null;
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

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}