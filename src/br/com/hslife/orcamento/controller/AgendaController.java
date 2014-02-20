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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Agenda;
import br.com.hslife.orcamento.enumeration.PrioridadeTarefa;
import br.com.hslife.orcamento.enumeration.TipoAgendamento;
import br.com.hslife.orcamento.facade.ICalendarioAtividades;

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
	
	@Autowired
	private ICalendarioAtividades service;
	
	public AgendaController() {
		super(new Agenda());
		moduleTitle = "Agendamentos";
	}
	
	@Override
	protected void initializeEntity() {
		
	}

	@Override
	public void find() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String save() {
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}
	
	public List<String> getListaHoras() {
		List<String> horas = new LinkedList<>();
		for (int i = 0; i < 24; i++) {
			horas.add(String.format("%02d", i));
		}
		return horas;
	}
	
	public List<String> getListaMinutos() {
		List<String> horas = new LinkedList<>();
		for (int i = 0; i < 60; i++) {
			horas.add(String.format("%02d", i));
		}
		return horas;
	}
	
	public List<SelectItem> getListaTipoAgendamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		listaSelectItem.add(new SelectItem(TipoAgendamento.COMPROMISSO, "Compromissos"));
		listaSelectItem.add(new SelectItem(TipoAgendamento.TAREFA, "Tarefas"));
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
	
	public ICalendarioAtividades getService() {
		return service;
	}

	public void setService(ICalendarioAtividades service) {
		this.service = service;
	}

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
}