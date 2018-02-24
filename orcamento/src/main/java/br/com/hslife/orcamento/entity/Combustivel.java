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
import javax.persistence.Table;

import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@SuppressWarnings("serial")
@Entity
@Table(name="combustivel")
public class Combustivel extends EntityPersistence {

	@Column(length=50, nullable=false)	
	protected String descricao;
	
	@Column(length=50, nullable=false)
	private String distribuidora;
	
	public Combustivel() {

	}	
	
	@Override
	public String getLabel() {
		return this.descricao + " [" + this.distribuidora + "]";
	}
		
	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Distribuidora", this.distribuidora, 50);
	}
	
	private Combustivel(Builder builder) {
		this.descricao = builder.descricao;
		this.distribuidora = builder.distribuidora;
	}
	
	public static class Builder {
		private String descricao;
		private String distribuidora;
		
		public Builder descricao(String descricao) {
			this.descricao = descricao;
			return this;
		}
		
		public Builder distribuidora(String distribuidora) {
			this.distribuidora = distribuidora;
			return this;
		}
		
		public Combustivel build() {
			return new Combustivel(this);
		}
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
	 * @return the distribuidora
	 */
	public String getDistribuidora() {
		return distribuidora;
	}

	/**
	 * @param distribuidora the distribuidora to set
	 */
	public void setDistribuidora(String distribuidora) {
		this.distribuidora = distribuidora;
	}	
}
