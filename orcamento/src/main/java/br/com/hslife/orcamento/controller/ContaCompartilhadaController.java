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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.ContaCompartilhada;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IContaCompartilhada;

@Component("contaCompartilhadaMB")
@Scope("session")
public class ContaCompartilhadaController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8842409792546739344L;
	
	@Autowired
	private IContaCompartilhada service;
	
	@Autowired
	private IConta contaService;
	
	private ContaCompartilhada entity;
	private List<ContaCompartilhada> listEntity;
	
	public ContaCompartilhadaController() {
		entity = new ContaCompartilhada();
	}

	@Override
	protected void initializeEntity() {
		listEntity = new ArrayList<>();
		entity = new ContaCompartilhada();
	}

	@Override
	public String startUp() {
		moduleTitle = "Contas Compartilhadas";
		find();
		return "/pages/ContaCompartilhada/listContaCompartilhada";
	}
	
	public String create() {
		moduleTitle = "Contas Compartilhadas - Compartilhar";
		return "/pages/ContaCompartilhada/formContaCompartilhada";
	}
	
	public void edit() {
		try {
			getService().reenviarConvite(entity);
			infoMessage("Convite enviado com sucesso!");
			initializeEntity();
			find();
		} catch (ApplicationException e) {
			errorMessage(e.getMessage());
		}
	}
	
	private void find() {
		listEntity = getService().buscarTodosPorUsuarioLogado(getUsuarioLogado());
	}
	
	public String save() {
		try {
			entity.validate();
			getService().compartilharConta(entity);
			infoMessage("Conta compartilhada com sucesso!");
			initializeEntity();
			return this.startUp();
		} catch (BusinessException | ValidationException | ApplicationException e) {
			errorMessage(e.getMessage());
		}
		return "";
	}
	
	public void delete() {
		getService().excluirCompartilhamento(entity);
		infoMessage("Compartilhamento excluído com sucesso!");
		initializeEntity();
		find();
	}
	
	public String cancel() {
		initializeEntity();
		return startUp();
	}
	
	public List<Conta> getListaConta() {
		try {
			if (getOpcoesSistema().getExibirContasInativas()) {
				return getContaService().buscarDescricaoOuTipoContaOuAtivoPorUsuario(null, null, getUsuarioLogado(), null);
			} else {
				return getContaService().buscarDescricaoOuTipoContaOuAtivoPorUsuario(null, null, getUsuarioLogado(), Boolean.TRUE);
			}
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public int getQuantRegistros() {
		return listEntity == null || listEntity.isEmpty() ? 0 : listEntity.size();
	}

	public ContaCompartilhada getEntity() {
		return entity;
	}

	public void setEntity(ContaCompartilhada entity) {
		this.entity = entity;
	}

	public IContaCompartilhada getService() {
		return service;
	}

	public IConta getContaService() {
		return contaService;
	}

	public List<ContaCompartilhada> getListEntity() {
		return listEntity;
	}

	public void setListEntity(List<ContaCompartilhada> listEntity) {
		this.listEntity = listEntity;
	}
}
