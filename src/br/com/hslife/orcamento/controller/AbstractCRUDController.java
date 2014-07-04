/***
  
  	Copyright (c) 2012, 2013, 2014 Hércules S. S. José

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

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site HSlife
    
    no endereco www.hslife.com.br ou escreva para a Fundação do Software 
    
    Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor acesse o 

    endereço www.hslife.com.br, pelo e-mail contato@hslife.com.br ou escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
 ***/

package br.com.hslife.orcamento.controller;

import java.util.List;

import br.com.hslife.orcamento.entity.EntityPersistence;
import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.service.ICRUDService;

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
	
	public AbstractCRUDController(E entity) {
		this.entity = entity;
		goToListPage = "/pages/" + entity.getClass().getSimpleName() + "/list" + entity.getClass().getSimpleName() + "?faces-redirect=true";
		goToFormPage = "/pages/" + entity.getClass().getSimpleName() + "/form" + entity.getClass().getSimpleName() + "?faces-redirect=true";
		goToViewPage = "/pages/" + entity.getClass().getSimpleName() + "/view" + entity.getClass().getSimpleName() + "?faces-redirect=true";
	}
	
	protected abstract ICRUDService getService();
	
	protected void validate(String action) throws BusinessException {
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
		operation = "list";
		actionTitle = "";
		return goToListPage;
	}
	
	public String create() {
		operation = "create";
		actionTitle = " - Novo";
		return goToFormPage;
	}
	
	public String save() {
		try {
			if (entity.getId() == null) {
				validate(operation);
				getService().cadastrar(entity);
				infoMessage("Registro cadastrado com sucesso!");
			} else {
				validate(operation);
				getService().alterar(entity);
				infoMessage("Registro alterado com sucesso!");
			}
			
			// Verifica se a listagem de resultados está nula ou não para poder efetuar novamente a busca
			if (listEntity != null && !listEntity.isEmpty()) {
				// Inicializa os objetos
				initializeEntity();
				
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", getUsuarioLogado());
							
				// Determina se a busca será executada novamente
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {					
					find();
				}
			} else {
				initializeEntity();
			}
			
			return list();
		} catch (BusinessException be) {
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
		} catch (BusinessException be) {
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
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String delete() {
		try {
			validate(operation);
			getService().excluir(entity);
			infoMessage("Registro excluído com sucesso!");
			
			// Verifica se a listagem de resultados está nula ou não para poder efetuar novamente a busca
			if (listEntity != null && !listEntity.isEmpty()) {
				// Inicializa os objetos
				initializeEntity();
				
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", getUsuarioLogado());
							
				// Determina se a busca será executada novamente
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {					
					find();
				}
			} else {
				initializeEntity();
			}
						
			return list();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public abstract void find();
	
	public String cancel() {
		try {
			// Verifica se a listagem de resultados está nula ou não para poder efetuar novamente a busca
			if (listEntity != null && !listEntity.isEmpty()) {
				// Inicializa os objetos
				initializeEntity();
				
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", getUsuarioLogado());
							
				// Determina se a busca será executada novamente
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {					
					find();
				}
			} else {
				initializeEntity();
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return list();
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
}