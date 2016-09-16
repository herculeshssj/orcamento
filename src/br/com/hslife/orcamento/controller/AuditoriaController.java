/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

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

import br.com.hslife.orcamento.entity.Auditoria;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IAuditoria;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.model.CriterioAuditoria;

@Component("auditoriaMB")
@Scope("session")
public class AuditoriaController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8085348187243579495L;

	@Autowired
	private IAuditoria service;
	
	@Autowired
	private IUsuario usuarioService;
	
	private CriterioAuditoria criterio;
	
	private Auditoria entity;
	private List<Auditoria> listEntity;
	private Long idEntity;
	
	public AuditoriaController() {
		criterio = new CriterioAuditoria();
		
		moduleTitle = "Auditoria do Sistema";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Auditoria();
		listEntity = new ArrayList<Auditoria>();
	}
	
	@Override
	public String startUp() {		
		return "/pages/Auditoria/listAuditoria";
	}
	
	public void find() {
		try {
			listEntity = getService().buscarPorCriterios(criterio);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String view() {
		try {
			entity = getService().buscarPorId(idEntity);
			return "/pages/Auditoria/viewAuditoria";
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public void delete() {
		try {
			entity = getService().buscarPorId(idEntity);
			getService().excluir(entity);
			infoMessage("Registro excluído com sucesso!");
			initializeEntity();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
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
	
	public int getQuantRegistros() {
		return listEntity == null || listEntity.isEmpty() ? 0 : listEntity.size();
	}
	
	/* Métodos Getters e Setters */

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
}