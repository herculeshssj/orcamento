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

package br.com.hslife.orcamento.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Agenda;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.facade.IAgenda;
import br.com.hslife.orcamento.model.CriterioAgendamento;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.repository.AgendaRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;

@Service("agendaService")
public class AgendaService extends AbstractCRUDService<Agenda> implements IAgenda {

	@Autowired
	private AgendaRepository repository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private UsuarioComponent usuarioComponent;
	
	public AgendaRepository getRepository() {
		repository.setSessionFactory(this.sessionFactory);
		return repository;
	}
	
	public LancamentoContaRepository getLancamentoContaRepository() {
		lancamentoContaRepository.setSessionFactory(this.sessionFactory);
		return lancamentoContaRepository;
	}

	public UsuarioComponent getUsuarioComponent() {
		return usuarioComponent;
	}

	@Override
	public List<Agenda> buscarAgendamentoLancamentosAgendados(Conta conta, Date dataInicio, Date dataFim) {
		CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
		List<Agenda> agendamentos = new ArrayList<>();
		Agenda agenda = new Agenda();
		criterioBusca.setConta(conta);
		criterioBusca.setDataInicio(dataInicio);
		criterioBusca.setDataFim(dataFim);
		criterioBusca.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.AGENDADO});
		
		for (LancamentoConta lancamento : getLancamentoContaRepository().findByCriterioBusca(criterioBusca)) {
			agenda.setDataInicio(lancamento.getDataPagamento());
			agenda.setDescricao(lancamento.getConta().getLabel() + " - " + lancamento.getDescricao());
			agenda.setUsuario(lancamento.getConta().getUsuario());
			agendamentos.add(agenda);
			agenda = new Agenda();
		}		
		return agendamentos;
	}
	
	@Override
	public List<Agenda> buscarPorCriterioAgendamento(CriterioAgendamento criterioBusca) {
		return getRepository().findByCriterioAgendamento(criterioBusca);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public Long contarAgendamentosDeHojeComAlerta() {
		Date inicio = new Date();
		inicio.setHours(0);
		inicio.setMinutes(0);
		inicio.setSeconds(0);

		Date fim = new Date(inicio.getTime());
		fim.setHours(23);
		fim.setMinutes(59);
		fim.setSeconds(59);
		return getRepository().countAgendamentoByDataInicioOrDataFimAndAlerta(inicio, fim, true, getUsuarioComponent().getUsuarioLogado());
	}
	
	@SuppressWarnings("deprecation")
	public List<Agenda> buscarAgendamentosDoDia() {
		CriterioAgendamento criterioBusca = new CriterioAgendamento();
		Date inicio = new Date();
		inicio.setHours(0);
		inicio.setMinutes(0);
		inicio.setSeconds(0);

		Date fim = new Date(inicio.getTime());
		fim.setHours(23);
		fim.setMinutes(59);
		fim.setSeconds(59);
		criterioBusca.setInicio(inicio);
		criterioBusca.setFim(fim);
		criterioBusca.setUsuario(getUsuarioComponent().getUsuarioLogado());
		return getRepository().findByCriterioAgendamento(criterioBusca);
	}
	
	@Override
	public List<Agenda> buscarAgendamentoPorOuDataInicioOuDataFimEAlerta(Date inicio, Date fim, boolean emiteAlerta) {
		return getRepository().findAgendamentoByOrDataInicioOrDataFimAndAlerta(inicio, fim, emiteAlerta);
	}
}