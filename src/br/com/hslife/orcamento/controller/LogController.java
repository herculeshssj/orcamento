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
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
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
		try {
			listEntity = getService().buscarPorCriterios(criterioBusca);
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String cancel() {
		initializeEntity();
		return startUp();
	}
	
	public String view() {
		try {
			entity = getService().buscarPorID(idEntity);
			return "/pages/Logs/viewLogs";
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public void delete() {
		try {
			entity = getService().buscarPorID(idEntity);
			getService().excluir(entity);
			infoMessage("Registro excluído com sucesso!");
			initializeEntity();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void excluirLog() {
		try {
			if (listEntity == null || listEntity.isEmpty()) {
				warnMessage("Nenhum resultado encontrado!");
				return;
			}
			
			for (Logs log : listEntity) {
				entity = getService().buscarPorID(log.getId());
				getService().excluir(log);
			}
			infoMessage("Registros excluídos com sucesso!");
			initializeEntity();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public List<String> getListaNivel() {
		try {
			List<String> result = getService().buscarTodosNiveis();
			if (result == null) result = new ArrayList<>();
			return result;
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<String> getListaLogger() {
		try {
			List<String> result = getService().buscarTodosLoggers();
			if (result == null) result = new ArrayList<>();
			return result;
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
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