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

package br.com.hslife.orcamento.controller;

import java.util.List;

import br.com.hslife.orcamento.entity.EntityPersistence;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.facade.ICRUDService;

@SuppressWarnings({"rawtypes","unchecked"})
public abstract class AbstractCRUDController<E extends EntityPersistence> extends AbstractController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6155378577207243614L;
	
	protected E entity;
	protected Long idEntity;
	protected List<E> listEntity;
	
	protected String goToListPage;
	protected String goToFormPage;
	protected String goToViewPage;
	protected String goToSearchPage;
	
	protected String operation = "list";
	
	public AbstractCRUDController(E entity) {
		this.entity = entity;
		goToListPage = "/pages/" + entity.getClass().getSimpleName() + "/list" + entity.getClass().getSimpleName();
		goToFormPage = "/pages/" + entity.getClass().getSimpleName() + "/form" + entity.getClass().getSimpleName();
		goToViewPage = "/pages/" + entity.getClass().getSimpleName() + "/view" + entity.getClass().getSimpleName();
		goToSearchPage = "/pages/" + entity.getClass().getSimpleName() + "/search" + entity.getClass().getSimpleName();
	}
	
	protected abstract ICRUDService getService();
	
	protected void validate(String action) throws ApplicationException {
		entity.validate();
		getService().validar(entity);
	}

	public String startUp() {
		operation = "list";
		return goToListPage;
	}
	
	@Override
	public String getModuleTitle() {		
		return super.getModuleTitle() + actionTitle;
	}
	
	public String list() {
		reprocessarBusca();
		operation = "list";
		actionTitle = "";
		return goToListPage;
	}
	
	public String create() {
		operation = "create";
		actionTitle = " - Novo";
		return goToFormPage;
	}
	
	protected void reprocessarBusca() {
		// Reprocessa a busca caso a opção do sistema esteja ativada
		initializeEntity();
		if (getOpcoesSistema().getExibirBuscasRealizadas())		
			find();
	}
	
	public String save() {
		try {
			validate(operation);
			if (entity.getId() != null) {
				getService().alterar(entity);
				infoMessage("Registro alterado com sucesso!");
			} else {
				getService().cadastrar(entity);
				infoMessage("Registro cadastrado com sucesso!");
			}
			return list();
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String view() {
		try {
			entity = (E) getService().buscarPorID(idEntity);
			operation = "delete";
			actionTitle = " - Excluir";
			return goToViewPage;
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String edit() {
		try {
			entity = (E) getService().buscarPorID(idEntity);
			operation = "edit";
			actionTitle = " - Editar";
			return goToFormPage;
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String delete() {
		try {
			validate(operation);
			getService().excluir(entity);
			infoMessage("Registro excluído com sucesso!");						
			return list();
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public abstract void find();
	
	public String advancedSearch() {
		operation = "search";
		actionTitle = " - Busca avançada";
		return goToSearchPage;
	}
	
	public String search() {
		actionTitle = "";
		return goToListPage;
	}
	
	public String cancel() {
		return list();
	}
	
	public int getQuantRegistros() {
		return listEntity == null || listEntity.isEmpty() ? 0 : listEntity.size();
	}

	public E getEntity() {
		return entity;
	}

	public void setEntity(E entity) {
		this.entity = entity;
	}

	public Long getIdEntity() {
		return idEntity;
	}

	public void setIdEntity(Long idEntity) {
		this.idEntity = idEntity;
	}

	public List<E> getListEntity() {
		return listEntity;
	}

	public void setListEntity(List<E> listEntity) {
		this.listEntity = listEntity;
	}
	
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
}