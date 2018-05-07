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
package br.com.hslife.orcamento.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.AdministradorInvestimento;
import br.com.hslife.orcamento.facade.IAdministradorInvestimento;

@Component
@Scope("session")
public class AdministradorInvestimentoController extends AbstractCRUDController<AdministradorInvestimento>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -372533275028178301L;
	
	@Autowired
	private IAdministradorInvestimento service;
	
	private String nome;
	
	public AdministradorInvestimentoController() {
		super(new AdministradorInvestimento());
		
		moduleTitle = "Administradores de Investimento";
	}

	@Override
	protected void initializeEntity() {
		entity = new AdministradorInvestimento();
		listEntity = new ArrayList<>();
	}

	@Override
	public void find() {
		listEntity = getService().findAllByNomeAndUsuario(nome, getUsuarioLogado());
	}

	@Override
	public String save() {
		// Ajustar o CNPJ
		entity.setCnpj(entity.getCnpj().replace(".", "").replace("/", "").replace("-", ""));
		
		// Seta o usuário
		entity.setUsuario(getUsuarioLogado());
		
		return super.save();
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
	 * @return the service
	 */
	public IAdministradorInvestimento getService() {
		return service;
	}
}