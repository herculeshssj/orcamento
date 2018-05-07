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
import br.com.hslife.orcamento.util.Util;

@SuppressWarnings("serial")
@Entity
@Table(name="administradorinvestimento")
public class AdministradorInvestimento extends EntityPersistence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=200, nullable=false)
	private String nome;
	
	@Column(length=14, nullable=false)
	private String cnpj;
	
	@Column(name="endereco_completo", length=300)
	private String enderecoCompleto;
	
	@Column(length=100)
	private String telefone;
	
	@Column(length=100)
	private String site;
	
	@Column(length=50)
	private String email;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;

	public AdministradorInvestimento() {
		
	}
	
	public String getCnpjFormatado() {
		return Util.formatarCNPJ(this.cnpj);
	}	
	
	@Override
	public String getLabel() {
		return this.nome;
	}
		
	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Nome", this.nome, 200);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("CNPJ", this.cnpj, 14);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Endereço completo", this.enderecoCompleto, 300);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Telefone", this.telefone, 100);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Site", this.site, 100);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("E-Mail", this.email, 50);
		EntityPersistenceUtil.validaCampoNulo("Usuário", this.usuario);
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
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the cnpj
	 */
	public String getCnpj() {
		return cnpj;
	}

	/**
	 * @param cnpj the cnpj to set
	 */
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	/**
	 * @return the enderecoCompleto
	 */
	public String getEnderecoCompleto() {
		return enderecoCompleto;
	}

	/**
	 * @param enderecoCompleto the enderecoCompleto to set
	 */
	public void setEnderecoCompleto(String enderecoCompleto) {
		this.enderecoCompleto = enderecoCompleto;
	}

	/**
	 * @return the telefone
	 */
	public String getTelefone() {
		return telefone;
	}

	/**
	 * @param telefone the telefone to set
	 */
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	/**
	 * @return the site
	 */
	public String getSite() {
		return site;
	}

	/**
	 * @param site the site to set
	 */
	public void setSite(String site) {
		this.site = site;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
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