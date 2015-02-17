/***
  
  	Copyright (c) 2012 - 2015 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@gmail.com, ou ainda escreva para 

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
public abstract class AbstractSimpleCRUDController<E extends EntityPersistence> extends AbstractController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6155378577207243614L;
	
	protected E entity;
	protected Long idEntity;
	protected List<E> listEntity;
	
	protected String goToPage;
	protected String goToModule;
	
	public AbstractSimpleCRUDController(E entity) {
		this.entity = entity;
		goToPage = "/pages/" + entity.getClass().getSimpleName() + "/form" + entity.getClass().getSimpleName();
		goToModule = "/pages/menu/inicio.faces";
	}
	
	protected abstract ICRUDService getService();
	
	protected void validate() throws BusinessException {
		entity.validate();
		getService().validar(entity);
	}

	public String startUp() {
		return goToPage;
	}
	
	@Override
	public String getModuleTitle() {		
		return super.getModuleTitle() + actionTitle;
	}
	
	public String goToModule() {
		return goToModule;
	}
	
	public void create() {
		actionTitle = "";
		initializeEntity();
	}
	
	public void save() {
		try {
			if (entity.getId() == null) {
				validate();
				getService().cadastrar(entity);
				infoMessage("Registro cadastrado com sucesso!");
			} else {
				validate();
				getService().alterar(entity);
				infoMessage("Registro alterado com sucesso!");
			}
			actionTitle = "";
			
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
	}
	
	public void edit() {
		try {
			entity = (E) getService().buscarPorID(idEntity);
			actionTitle = " - Editar";
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void delete() {
		try {
			entity = (E) getService().buscarPorID(idEntity);
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
						
			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public abstract void find();
	
	public int getQuantRegistros() {
		if (listEntity == null || listEntity.isEmpty()) {
			return 0;
		} else {
			return listEntity.size();
		}
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