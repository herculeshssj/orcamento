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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Agenda;
import br.com.hslife.orcamento.enumeration.PrioridadeTarefa;
import br.com.hslife.orcamento.enumeration.TipoAgendamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IAgenda;
import br.com.hslife.orcamento.model.CriterioAgendamento;

@Component("agendaMB")
@Scope("session")
public class AgendaController extends AbstractCRUDController<Agenda> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8085348187243579495L;
	
	private String descricaoAgendamento;
	private TipoAgendamento tipoAgendamento;
	private Date inicioAgendamento;
	private Date fimAgendamento;
	private CriterioAgendamento criterioBusca = new CriterioAgendamento();
	private Integer horaInicio, horaFim, minutoInicio, minutoFim;
	
	@Autowired
	private IAgenda service;
	
	public AgendaController() {
		super(new Agenda());
		moduleTitle = "Agendamentos";
	}
	
	@Override
	@PostConstruct
	public String startUp() {
		// Preenche os campos com a data atual
		criterioBusca.setInicio(new Date());
		criterioBusca.setFim(new Date());
		return super.startUp();
	}
	
	@Override
	protected void initializeEntity() {
		listEntity = new ArrayList<>();
		entity = new Agenda();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void find() {
		try {
			// Caso a data de início ou fim estejam preenchidos, 
			// seta o horário com o limite correspondente para
			// cada um
			if (criterioBusca.getInicio() != null) {
				criterioBusca.getInicio().setHours(0);
				criterioBusca.getInicio().setMinutes(0);
				criterioBusca.getInicio().setSeconds(0);
			}
			
			if (criterioBusca.getFim() != null) {
				criterioBusca.getFim().setHours(23);
				criterioBusca.getFim().setMinutes(59);
				criterioBusca.getFim().setSeconds(59);
			}
			criterioBusca.setUsuario(getUsuarioLogado());
			
			listEntity = getService().buscarPorCriterioAgendamento(criterioBusca);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}		
	}
	
	@Override
	public String create() {
		entity.setTipoAgendamento(TipoAgendamento.COMPROMISSO);
		return super.create();
	}
	
	@Override
	public String save() {
		if (entity.getInicio() != null) {
			entity.setInicio(entity.comporData(entity.getInicio(), horaInicio, minutoInicio));
		}
		if (entity.getFim() != null) {
			entity.setFim(entity.comporData(entity.getFim(), horaFim, minutoFim));
		}		
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}
	
	@Override
	public String edit() {		
		try {
			entity = (Agenda) getService().buscarPorID(idEntity);	
			
			if (entity.getInicio() != null) {
				horaInicio = entity.extrairHora(entity.getInicio());
				minutoInicio = entity.extrairMinuto(entity.getInicio());
			}
			if (entity.getFim() != null) {
				horaFim = entity.extrairHora(entity.getFim());
				minutoFim = entity.extrairMinuto(entity.getFim());
			}
			
			operation = "edit";
			actionTitle = " - Editar";
			return goToFormPage;
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public Long getAgendamentosDeHoje() {
		try {
			return getService().contarAgendamentosDeHojeComAlerta();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return 0l;
	}
	
	public String getAgendaDoDia() {
		Long quantCompromissos = this.getAgendamentosDeHoje();
		return "Agenda (" + quantCompromissos.toString() + ")";
	}
	
	public List<Agenda> getAgendamentosDoDia() {
		List<Agenda> agendas = new ArrayList<Agenda>();
		try {
			return getService().buscarAgendamentosDoDia();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return agendas;
	}
	
	public List<Integer> getListaHoras() {
		List<Integer> horas = new LinkedList<>();
		for (int i = 0; i < 24; i++) {
			horas.add(i);
		}
		return horas;
	}
	
	public List<Integer> getListaMinutos() {
		List<Integer> horas = new LinkedList<>();
		for (int i = 0; i < 60; i++) {
			horas.add(i);
		}
		return horas;
	}
	
	public List<SelectItem> getListaTipoAgendamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		listaSelectItem.add(new SelectItem(TipoAgendamento.COMPROMISSO, "Compromisso"));
		listaSelectItem.add(new SelectItem(TipoAgendamento.TAREFA, "Tarefa"));
		if (operation.equals("list"))
			listaSelectItem.add(new SelectItem(TipoAgendamento.PREVISAO, "Previsão"));
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaPrioridadeTarefa() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		listaSelectItem.add(new SelectItem(PrioridadeTarefa.NORMAL, "Normal"));
		listaSelectItem.add(new SelectItem(PrioridadeTarefa.BAIXA, "Baixa"));
		listaSelectItem.add(new SelectItem(PrioridadeTarefa.ALTA, "Alta"));
		return listaSelectItem;
	}

	/* Métodos Getters e Setters */

	public String getDescricaoAgendamento() {
		return descricaoAgendamento;
	}

	public void setDescricaoAgendamento(String descricaoAgendamento) {
		this.descricaoAgendamento = descricaoAgendamento;
	}

	public TipoAgendamento getTipoAgendamento() {
		return tipoAgendamento;
	}

	public void setTipoAgendamento(TipoAgendamento tipoAgendamento) {
		this.tipoAgendamento = tipoAgendamento;
	}

	public Date getInicioAgendamento() {
		return inicioAgendamento;
	}

	public void setInicioAgendamento(Date inicioAgendamento) {
		this.inicioAgendamento = inicioAgendamento;
	}

	public Date getFimAgendamento() {
		return fimAgendamento;
	}

	public void setFimAgendamento(Date fimAgendamento) {
		this.fimAgendamento = fimAgendamento;
	}

	public CriterioAgendamento getCriterioBusca() {
		return criterioBusca;
	}

	public void setCriterioBusca(CriterioAgendamento criterioBusca) {
		this.criterioBusca = criterioBusca;
	}

	public Integer getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Integer horaInicio) {
		this.horaInicio = horaInicio;
	}

	public Integer getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(Integer horaFim) {
		this.horaFim = horaFim;
	}

	public Integer getMinutoInicio() {
		return minutoInicio;
	}

	public void setMinutoInicio(Integer minutoInicio) {
		this.minutoInicio = minutoInicio;
	}

	public Integer getMinutoFim() {
		return minutoFim;
	}

	public void setMinutoFim(Integer minutoFim) {
		this.minutoFim = minutoFim;
	}

	public IAgenda getService() {
		return service;
	}

	public void setService(IAgenda service) {
		this.service = service;
	}
}