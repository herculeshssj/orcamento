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

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 

	Floor, Boston, MA  02110-1301, USA.


	Para mais informações sobre o programa Orçamento Doméstico e seu autor

	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

	para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.component.OpcaoFaturaComponent;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IOpcaoSistema;

@Component("opcaoSistemaMB")
@Scope("session")
public class OpcaoSistemaController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5408730995837147806L;

	@Autowired
	private IOpcaoSistema service;
	
	@Autowired
	private OpcaoFaturaComponent opcaoFaturaComponent;
	
	private Map<String, Object> opcoesGlobalAdmin = new HashMap<String, Object>();
	private Map<String, Object> opcoesUser = new HashMap<String, Object>();

	public OpcaoSistemaController() {
		
	}

	@Override
	protected void initializeEntity() {
				
	}

	@Override
	@PostConstruct
	public String startUp() {
		moduleTitle = "Opções do Sistema";
		carregarOpcoesSistema();
		return "/pages/OpcaoSistema/formOpcaoSistema";
	}
	
	private void carregarOpcoesSistema() {
		if (getUsuarioLogado().getLogin().equals("admin")) {
			carregarOpcoesGlobalAdmin();
			carregarOpcoesUser();
		} else {
			carregarOpcoesUser();
		}
	}
	
	private void carregarOpcoesUser() {
		try {
			opcoesUser = getService().buscarMapOpcoesUser(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}

	private void carregarOpcoesGlobalAdmin() {
		try {
			opcoesGlobalAdmin = getService().buscarMapOpcoesGlobalAdmin();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}

	public void save() {
		try {
			getService().salvarOpcoesGlobalAdmin(opcoesGlobalAdmin);
			getService().salvarOpcoesUser(opcoesUser, getUsuarioLogado());
			getOpcoesSistema().atualizarCacheOpcoesSistema();
			infoMessage("Opções salvas com sucesso!");
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
	}

	public IOpcaoSistema getService() {
		return service;
	}
	
	public OpcaoFaturaComponent getOpcaoFaturaComponent() {
		return opcaoFaturaComponent;
	}

	public void setService(IOpcaoSistema service) {
		this.service = service;
	}

	public Map<String, Object> getOpcoesGlobalAdmin() {
		return opcoesGlobalAdmin;
	}

	public void setOpcoesGlobalAdmin(Map<String, Object> opcoesGlobalAdmin) {
		this.opcoesGlobalAdmin = opcoesGlobalAdmin;
	}

	public Map<String, Object> getOpcoesUser() {
		return opcoesUser;
	}

	public void setOpcoesUser(Map<String, Object> opcoesUser) {
		this.opcoesUser = opcoesUser;
	}	
}
