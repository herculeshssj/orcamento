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

package br.com.hslife.orcamento.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Agenda;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoAgendamento;
import br.com.hslife.orcamento.model.CriterioAgendamento;
import br.com.hslife.orcamento.model.CriterioLancamentoConta;
import br.com.hslife.orcamento.repository.AgendaRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;

@Component
public class AgendamentoTask {
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private AgendaRepository agendaRepository;

	public void setLancamentoContaRepository(
			LancamentoContaRepository lancamentoContaRepository) {
		this.lancamentoContaRepository = lancamentoContaRepository;
	}

	public void setAgendaRepository(AgendaRepository agendaRepository) {
		this.agendaRepository = agendaRepository;
	}
	
	@Scheduled(fixedDelay=3600000)
	public void executarTarefa() {
		CriterioAgendamento criterioAgendamento = new CriterioAgendamento();
		criterioAgendamento.setTipo(TipoAgendamento.PREVISAO);
		
		List<Agenda> agendamentos = agendaRepository.findByCriterioAgendamento(criterioAgendamento);
		List<LancamentoConta> lancamentosAtualizados = new ArrayList<>();
		LancamentoConta lancamento;
		
		for (Agenda agenda : agendamentos) {
			switch (agenda.getEntity()) {
			case "LancamentoConta":
				lancamento = lancamentoContaRepository.findById(agenda.getIdEntity());
				if (lancamento.isAgendado()) {
					lancamentosAtualizados.add(lancamento);
				} else {
					agendaRepository.delete(agenda);
				}
				break;
			default:
				break;
			}
		}
						
		CriterioLancamentoConta criterioLancamentoConta = new CriterioLancamentoConta();
		criterioLancamentoConta.setAgendado(true);
		criterioLancamentoConta.setDataInicio(new Date());
		criterioLancamentoConta.setQuitado(false);
		
		List<LancamentoConta> lancamentos = lancamentoContaRepository.findByCriterioLancamentoConta(criterioLancamentoConta);
		lancamentos.removeAll(lancamentosAtualizados);
		
		for (LancamentoConta l : lancamentos) {
			Agenda agenda = new Agenda();
			agenda.setDescricao(l.getDescricao());
			agenda.setInicio(l.getDataPagamento());
			agenda.setFim(l.getDataPagamento());
			agenda.setDiaInteiro(true);
			agenda.setTipoAgendamento(TipoAgendamento.PREVISAO);
			agenda.setNotas(l.getObservacao());
			agenda.setUsuario(l.getConta().getUsuario());
			agenda.setIdEntity(l.getId());
			agenda.setEntity(l.getClass().getSimpleName());
			
			agendaRepository.save(agenda);
		}		
	}
}