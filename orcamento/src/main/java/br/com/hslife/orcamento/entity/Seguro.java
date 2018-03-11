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
import java.util.Calendar;

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

import br.com.hslife.orcamento.util.EntityPersistenceUtil;


@Entity
@Table(name="seguro")
@SuppressWarnings("serial")
public class Seguro extends EntityPersistence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50, nullable=false)	
	private String descricao;

	@Column(nullable=false)
	@Temporal(TemporalType.DATE)	
	private Calendar dataAquisicao;
	
	@Column(nullable=true)
	@Temporal(TemporalType.DATE)	
	private Calendar validade;
	
	@Column(length=100, nullable=true)
	private String cobertura;
	
	@Column(nullable=false, precision=18, scale=2)
	private double valorCobertura;
	
	@Column(nullable=false, precision=18, scale=2)	
	private double valorSeguro;

	@Column(length=100, nullable=true)	
	private String observacao;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	public Seguro() {

	}
	
	private Seguro(Builder builder) {
		this.descricao = builder.descricao;
		this.dataAquisicao = builder.dataAquisicao;
		this.validade = builder.validade;
		this.cobertura = builder.cobertura;
		this.valorCobertura = builder.valorCobertura;
		this.valorSeguro = builder.valorSeguro;
		this.observacao = builder.observacao;
		this.usuario = builder.usuario;
	}
	
	public static class Builder {
		private String descricao;	
		private Calendar dataAquisicao;	
		private Calendar validade;
		private String cobertura;
		private double valorCobertura;	
		private double valorSeguro;	
		private String observacao;
		private Usuario usuario;
		
		public Builder descricao(String descricao) {
			this.descricao = descricao;
			return this;
		}
		
		public Builder dataAquisicao(Calendar dataAquisicao) {
			this.dataAquisicao = dataAquisicao;
			return this;
		}
		
		public Builder validade(Calendar validade) {
			this.validade = validade;
			return this;
		}
		
		public Builder cobertura(String cobertura) {
			this.cobertura = cobertura;
			return this;
		}
		
		public Builder valorCobertura(double valorCobertura) {
			this.valorCobertura = valorCobertura;
			return this;
		}
		
		public Builder valorSegura(double valorSeguro) {
			this.valorSeguro = valorSeguro;
			return this;
		}

		public Builder observacao(String observacao) {
			this.observacao = observacao;
			return this;
		}
		
		public Builder usuario(Usuario usuario) {
			this.usuario = usuario;
			return this;
		}
		
		public Seguro build() {
			return new Seguro(this);
		}
	}
	
	@Override
	public String getLabel() {
		return this.descricao;
	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", descricao, 50);
		EntityPersistenceUtil.validaCampoNulo("Data de aquisição", this.dataAquisicao);
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param descricao the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return the dataAquisicao
	 */
	public Calendar getDataAquisicao() {
		return dataAquisicao;
	}

	/**
	 * @param dataAquisicao the dataAquisicao to set
	 */
	public void setDataAquisicao(Calendar dataAquisicao) {
		this.dataAquisicao = dataAquisicao;
	}

	/**
	 * @return the validade
	 */
	public Calendar getValidade() {
		return validade;
	}

	/**
	 * @param validade the validade to set
	 */
	public void setValidade(Calendar validade) {
		this.validade = validade;
	}

	/**
	 * @return the cobertura
	 */
	public String getCobertura() {
		return cobertura;
	}

	/**
	 * @param cobertura the cobertura to set
	 */
	public void setCobertura(String cobertura) {
		this.cobertura = cobertura;
	}

	/**
	 * @return the valorCobertura
	 */
	public double getValorCobertura() {
		return valorCobertura;
	}

	/**
	 * @param valorCobertura the valorCobertura to set
	 */
	public void setValorCobertura(double valorCobertura) {
		this.valorCobertura = valorCobertura;
	}

	/**
	 * @return the valorSeguro
	 */
	public double getValorSeguro() {
		return valorSeguro;
	}

	/**
	 * @param valorSeguro the valorSeguro to set
	 */
	public void setValorSeguro(double valorSeguro) {
		this.valorSeguro = valorSeguro;
	}

	/**
	 * @return the observacao
	 */
	public String getObservacao() {
		return observacao;
	}

	/**
	 * @param observacao the observacao to set
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/**
	 * @return the usuario
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario the usuario to set
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}