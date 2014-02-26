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

import java.util.Date;

import javax.faces.event.ActionEvent;

import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.ScheduleEntrySelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Agenda;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICalendarioAtividades;
import br.com.hslife.orcamento.model.CriterioAgendamento;

@Component("calendarioAtividadesMB")
@Scope("session")
public class CalendarioAtividadesController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8085348187243579495L;

	@Autowired	
	private ICalendarioAtividades service;
	
	private ScheduleModel calendario;
	private ScheduleEvent event = new DefaultScheduleEvent(); 
	
	public CalendarioAtividadesController() {
		moduleTitle = "Calendário de Atividades";
	}
	
	@Override
	protected void initializeEntity() {
		
	}
	
	@Override
	public String startUp() {		
		// Carrega todos os eventos da data e hora atual em diante
		calendario = new DefaultScheduleModel();
		CriterioAgendamento criterioBusca = new CriterioAgendamento();
		criterioBusca.setInicio(new Date());
		try {
			for (Agenda agenda : service.buscarPorCriterioAgendamento(criterioBusca)) {
				if (agenda.getFim() == null)
					calendario.addEvent(new DefaultScheduleEvent(agenda.getDescricao(), agenda.getInicio(), agenda.getInicio(), agenda.isDiaInteiro()));
				else
					calendario.addEvent(new DefaultScheduleEvent(agenda.getDescricao(), agenda.getInicio(), agenda.getFim(), agenda.isDiaInteiro()));
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}		
		return "/pages/CalendarioAtividades/listCalendarioAtividades";
	}
	
	public void onDateSelect(DateSelectEvent selectEvent) {  
        event = new DefaultScheduleEvent("", selectEvent.getDate(), selectEvent.getDate());  
    }
	
	public void onEventSelect(ScheduleEntrySelectEvent selectEvent) {  
        event = selectEvent.getScheduleEvent(); 
    }
	
	public void onEventMove(ScheduleEntryMoveEvent event) {  
          
    }  
      
    public void onEventResize(ScheduleEntryResizeEvent event) {  
          
    } 

    public void addEvent(ActionEvent actionEvent) {  
        if(event.getId() == null)  
            calendario.addEvent(event);  
        else  
            calendario.updateEvent(event);  
          
        event = new DefaultScheduleEvent();  
    } 
    
	public ScheduleModel getCalendario() {
		return calendario;
	}

	public void setCalendario(ScheduleModel calendario) {
		this.calendario = calendario;
	}

	public ScheduleEvent getEvent() {
		return event;
	}

	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}

	public ICalendarioAtividades getService() {
		return service;
	}

	public void setService(ICalendarioAtividades service) {
		this.service = service;
	}
}
