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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Logs;
import br.com.hslife.orcamento.facade.ILog;
import br.com.hslife.orcamento.model.CriterioLog;

@Component("logMB")
@Scope("session")
public class LogController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8085348187243579495L;

	@Autowired
	private ILog service;
	
	private Logs entity;
	private List<Logs> listEntity;
	private Long idEntity;
	
	private CriterioLog criterioBusca = new CriterioLog();
	
	public LogController() {
		moduleTitle = "Logs do Sistema";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Logs();
		listEntity = new ArrayList<Logs>();
	}
	
	@Override
	public String startUp() {		
		return "/pages/Logs/listLogs";
	}
	
	public void find() {
		listEntity = getService().buscarPorCriterios(criterioBusca);	
	}
	/*
	public String view() {
		try {
			entity = getService().buscarPorId(idEntity);
			return "/pages/Auditoria/viewAuditoria";
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String delete() {
		try {
			entity = getService().buscarPorId(idEntity);
			getService().excluir(entity);
			infoMessage("Registro excluído com sucesso!");
			initializeEntity();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String excluirLog() {
		if (listEntity == null || listEntity.isEmpty()) {
			infoMessage("Nenhum resultado encontrado!");
		} else {
			try {
				for (Auditoria auditoria : listEntity) {
					entity = getService().buscarPorId(auditoria.getId());
					getService().excluir(entity);
				}				
				infoMessage("Registros excluídos com sucesso!");
				initializeEntity();
			} catch (BusinessException be) {
				errorMessage(be.getMessage());
			}			
		}
		return "";
	}
	
	public String cancel() {
		initializeEntity();
		return startUp();
	}
	
	public List<Usuario> getListaUsuarios() {
		try {
			return usuarioService.getListaUsuarios();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Usuario>();
	}
	
	public List<String> getListaClasses() {
		try {
			return getService().buscarClasses();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<String>();
	}
	
	
	
	/* Métodos Getters e Setters *

	public CriterioAuditoria getCriterio() {
		return criterio;
	}

	public void setCriterio(CriterioAuditoria criterio) {
		this.criterio = criterio;
	}

	public void setService(IAuditoria service) {
		this.service = service;
	}

	public IAuditoria getService() {
		return service;
	}

	public void setUsuarioService(IUsuario usuarioService) {
		this.usuarioService = usuarioService;
	}

	public Auditoria getEntity() {
		return entity;
	}

	public void setEntity(Auditoria entity) {
		this.entity = entity;
	}

	public List<Auditoria> getListEntity() {
		return listEntity;
	}

	public void setListEntity(List<Auditoria> listEntity) {
		this.listEntity = listEntity;
	}

	public Long getIdEntity() {
		return idEntity;
	}

	public void setIdEntity(Long idEntity) {
		this.idEntity = idEntity;
	}
	*/
	
	public int getQuantRegistros() {
		if (listEntity == null || listEntity.isEmpty()) {
			return 0;
		} else {
			return listEntity.size();
		}
	}

	public Logs getEntity() {
		return entity;
	}

	public void setEntity(Logs entity) {
		this.entity = entity;
	}

	public List<Logs> getListEntity() {
		return listEntity;
	}

	public void setListEntity(List<Logs> listEntity) {
		this.listEntity = listEntity;
	}

	public Long getIdEntity() {
		return idEntity;
	}

	public void setIdEntity(Long idEntity) {
		this.idEntity = idEntity;
	}

	public ILog getService() {
		return service;
	}

	public void setService(ILog service) {
		this.service = service;
	}

	public CriterioLog getCriterioBusca() {
		return criterioBusca;
	}

	public void setCriterioBusca(CriterioLog criterioBusca) {
		this.criterioBusca = criterioBusca;
	}
}