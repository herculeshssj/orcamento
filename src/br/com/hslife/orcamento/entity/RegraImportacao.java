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
@Table(name="regraimportacao")
public class RegraImportacao extends EntityPersistence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2880755953845455507L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=100, nullable=false)
	private String texto;
	
	@Column(nullable=true)
	private Long idCategoria;
	
	@Column(nullable=true)
	private Long idFavorecido;
	
	@Column(nullable=true)
	private Long idMeioPagamento;
	
	@ManyToOne
	@JoinColumn(name="idConta", nullable=false)
	private Conta conta;
	
	public RegraImportacao() {
		
	}
	
	private RegraImportacao(Builder builder) {
		this.texto = builder.texto;
		this.idCategoria = builder.idCategoria;
		this.idFavorecido = builder.idFavorecido;
		this.idMeioPagamento = builder.idMeioPagamento;
		this.conta = builder.conta;
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Texto a pesquisar", this.texto, 100);
		EntityPersistenceUtil.validaCampoNulo("Conta", this.conta);
	}
	
	public static class Builder {
		private String texto;
		private Long idCategoria;
		private Long idFavorecido;
		private Long idMeioPagamento;
		private Conta conta;
		
		public Builder texto(String texto) {
			this.texto = texto;
			return this;
		}
		
		public Builder idCategoria(Long idCategoria) {
			this.idCategoria = idCategoria;
			return this;
		}
		
		public Builder idFavorecido(Long idFavorecido) {
			this.idFavorecido = idFavorecido;
			return this;
		}
		
		public Builder idMeioPagamento(Long idMeioPagamento) {
			this.idMeioPagamento = idMeioPagamento;
			return this;
		}
		
		public Builder conta(Conta conta) {
			this.conta = conta;
			return this;
		}
		
		public RegraImportacao build() {
			return new RegraImportacao(this);
		}
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

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Long getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(Long idCategoria) {
		this.idCategoria = idCategoria;
	}

	public Long getIdFavorecido() {
		return idFavorecido;
	}

	public void setIdFavorecido(Long idFavorecido) {
		this.idFavorecido = idFavorecido;
	}

	public Long getIdMeioPagamento() {
		return idMeioPagamento;
	}

	public void setIdMeioPagamento(Long idMeioPagamento) {
		this.idMeioPagamento = idMeioPagamento;
	}
}