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

package br.com.hslife.orcamento.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.component.EmailComponent;
import br.com.hslife.orcamento.entity.Agenda;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoAgendamento;
import br.com.hslife.orcamento.model.CriterioAgendamento;
import br.com.hslife.orcamento.repository.AgendaRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.util.CriterioBuscaLancamentoConta;

@Component
public class AgendamentoTask {
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private AgendaRepository agendaRepository;
	
	@Autowired
	private EmailComponent emailComponent;

	public void setLancamentoContaRepository(
			LancamentoContaRepository lancamentoContaRepository) {
		this.lancamentoContaRepository = lancamentoContaRepository;
	}

	public void setAgendaRepository(AgendaRepository agendaRepository) {
		this.agendaRepository = agendaRepository;
	}
	
	@Scheduled(fixedDelay=3600000)
	@SuppressWarnings("deprecation")
	public void enviarEmailNotificacao() {
		Date inicio = new Date();
		inicio.setHours(0);
		inicio.setMinutes(0);
		inicio.setSeconds(0);

		Date fim = new Date(inicio.getTime());
		fim.setHours(23);
		fim.setMinutes(59);
		fim.setSeconds(59);
		List<Agenda> agendamentos =  agendaRepository.findAgendamentoByDataInicioAndDataFimAndAlerta(inicio, fim, true);
		
		// Itera a lista de agendamentos encontrados, e para cada uma envia um e-mail para o usuário
		for (Agenda a : agendamentos) {
			StringBuilder mensagemEmail = new StringBuilder();
			
			mensagemEmail.append("Prezado " + a.getUsuario().getNome() + ",\n\n");
			mensagemEmail.append("O seguinte agendamento foi marcado para notificá-lo:\n\n");
			mensagemEmail.append(a.getDescricao() + "\n");
			mensagemEmail.append("Tipo: " + a.getTipoAgendamento() + "\n");
			mensagemEmail.append("Período: \n" + a.getDateLabel() + "\n");
			mensagemEmail.append("Dia inteiro: " + (a.isDiaInteiro() ? "SIM" : "NÃO") + "\n");
			mensagemEmail.append("Notas: " + (a.getNotas() == null ? "-" : a.getNotas()) + "\n\n");
			mensagemEmail.append("Caso não queira mais receber notificações a respeito desse evento, desmarque a caixa 'Emitir Alerta' nas propriedades do agendamento.\n\n\n");
			mensagemEmail.append("Administrador do Sistema");
			
			try {
				emailComponent.enviarEmail(a.getUsuario().getNome(), a.getUsuario().getEmail(), "Orçamento Doméstico - Lembrete de agendamento", mensagemEmail.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Scheduled(fixedDelay=3600000)
	@SuppressWarnings("deprecation")
	//@Scheduled(fixedDelay=120000)
	public void executarTarefa() {
		CriterioAgendamento criterioAgendamento = new CriterioAgendamento();
		criterioAgendamento.setTipo(TipoAgendamento.PREVISAO);
		
		List<Agenda> agendamentos = agendaRepository.findByCriterioAgendamento(criterioAgendamento);
		List<LancamentoConta> lancamentosAtualizados = new ArrayList<>();
		LancamentoConta lancamento;
		Date dataLancamento;
		
		for (Agenda agenda : agendamentos) {
			Date dataAgenda = new Date(agenda.getInicio().getYear()+1900, agenda.getInicio().getMonth(), agenda.getInicio().getDate(), 0, 0, 0);
			switch (agenda.getEntity()) {
			case "LancamentoConta":
				lancamento = lancamentoContaRepository.findById(agenda.getIdEntity());
				if (lancamento != null) {
					dataLancamento = new Date(lancamento.getDataPagamento().getYear()+1900, lancamento.getDataPagamento().getMonth(), lancamento.getDataPagamento().getDate(), 0, 0, 0);
					if (lancamento.getStatusLancamentoConta().equals(StatusLancamentoConta.AGENDADO) && dataLancamento.equals(dataAgenda)) {
						lancamentosAtualizados.add(lancamento);
					} else {
						agendaRepository.delete(agenda);
					}					
				} else {
					agendaRepository.delete(agenda);
				}
				break;
			default:
				break;
			}
		}
						
		CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
		criterioBusca.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.AGENDADO});
		criterioBusca.setDataInicio(new Date());
		
		List<LancamentoConta> lancamentos = lancamentoContaRepository.findByCriterioBusca(criterioBusca);
		lancamentos.removeAll(lancamentosAtualizados);
		
		for (LancamentoConta l : lancamentos) {
			Agenda agenda = new Agenda();
			agenda.setDescricao(l.getDescricao());
			agenda.setInicio(l.getDataPagamento());
			agenda.setFim(l.getDataPagamento());
			agenda.setDiaInteiro(true);
			agenda.setEmitirAlerta(true);
			agenda.setTipoAgendamento(TipoAgendamento.PREVISAO);
			agenda.setNotas(l.getObservacao());
			agenda.setUsuario(l.getConta().getUsuario());
			agenda.setIdEntity(l.getId());
			agenda.setEntity(l.getClass().getSimpleName());
			
			agendaRepository.save(agenda);
		}		
	}
}
