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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="seguro")
public class Seguro extends EntityPersistence {
	
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
	 * @return the descricao
	 */
	public final String getDescricao() {
		return descricao;
	}

	/**
	 * @param descricao the descricao to set
	 */
	public final void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return the dataAquisicao
	 */
	public final Calendar getDataAquisicao() {
		return dataAquisicao;
	}

	/**
	 * @param dataAquisicao the dataAquisicao to set
	 */
	public final void setDataAquisicao(Calendar dataAquisicao) {
		this.dataAquisicao = dataAquisicao;
	}

	/**
	 * @return the validade
	 */
	public final Calendar getValidade() {
		return validade;
	}

	/**
	 * @param validade the validade to set
	 */
	public final void setValidade(Calendar validade) {
		this.validade = validade;
	}

	/**
	 * @return the cobertura
	 */
	public final String getCobertura() {
		return cobertura;
	}

	/**
	 * @param cobertura the cobertura to set
	 */
	public final void setCobertura(String cobertura) {
		this.cobertura = cobertura;
	}

	/**
	 * @return the valorCobertura
	 */
	public final double getValorCobertura() {
		return valorCobertura;
	}

	/**
	 * @param valorCobertura the valorCobertura to set
	 */
	public final void setValorCobertura(double valorCobertura) {
		this.valorCobertura = valorCobertura;
	}

	/**
	 * @return the valorSeguro
	 */
	public final double getValorSeguro() {
		return valorSeguro;
	}

	/**
	 * @param valorSeguro the valorSeguro to set
	 */
	public final void setValorSeguro(double valorSeguro) {
		this.valorSeguro = valorSeguro;
	}

	/**
	 * @return the observacao
	 */
	public final String getObservacao() {
		return observacao;
	}

	/**
	 * @param observacao the observacao to set
	 */
	public final void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/**
	 * @return the usuario
	 */
	public final Usuario getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario the usuario to set
	 */
	public final void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}