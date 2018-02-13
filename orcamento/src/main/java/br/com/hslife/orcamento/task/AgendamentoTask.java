/***

Copyright (c) 2012 - 2021 Hércules S. S. José

	Este arquivo é parte do programa Orçamento Doméstico.


	Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

	modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

	publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

	Licença.


	Este programa é distribuído na esperança que possa ser útil, mas SEM

	NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer

	MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor

	GNU em português para maiores detalhes.


	Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob

	o nome de "LICENSE" junto com este programa, se não, acesse o site do

	projeto no endereco https://github.com/herculeshssj/orcamento ou escreva

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth

	Floor, Boston, MA  02110-1301, USA.


	Para mais informações sobre o programa Orçamento Doméstico e seu autor

	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

	para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.component.EmailComponent;
import br.com.hslife.orcamento.component.OpcaoSistemaComponent;
import br.com.hslife.orcamento.entity.Agenda;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoAgendamento;
import br.com.hslife.orcamento.facade.IAgenda;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.model.CriterioAgendamento;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;

@Component
@Transactional(propagation=Propagation.SUPPORTS)
public class AgendamentoTask {
	
	private static final Logger logger = LogManager.getLogger(AgendamentoTask.class);
	
	@Autowired
	private EmailComponent emailComponent;
	
	@Autowired
	private OpcaoSistemaComponent opcaoSistemaComponent;
	
	@Autowired
	private IAgenda service;
	
	@Autowired
	private ILancamentoConta lancamentoContaService;
	
	public ILancamentoConta getLancamentoContaService() {
		return lancamentoContaService;
	}

	public EmailComponent getEmailComponent() {
		return emailComponent;
	}

	public OpcaoSistemaComponent getOpcaoSistemaComponent() {
		return opcaoSistemaComponent;
	}

	public IAgenda getService() {
		return service;
	}

	@Scheduled(fixedDelay=3600000)
	@SuppressWarnings("deprecation")
	public void enviarEmailNotificacao() {
		try {
			
			Date inicio = new Date();
			inicio.setHours(0);
			inicio.setMinutes(0);
			inicio.setSeconds(0);
	
			Date fim = new Date(inicio.getTime());
			fim.setHours(23);
			fim.setMinutes(59);
			fim.setSeconds(59);
			List<Agenda> agendamentos = getService().buscarAgendamentoPorOuDataInicioOuDataFimEAlerta(inicio, fim, true);
			
			
			// Itera a lista de agendamentos encontrados, e para cada uma envia um e-mail para o usuário
			for (Agenda a : agendamentos) {
				// Se o usuário marcou para não receber notificação o e-mail não será enviado
				if (getOpcaoSistemaComponent().getNotificarAgendamentosEmail(a.getUsuario())) {
				
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
					
					emailComponent.setDestinatario(a.getUsuario().getNome());
					emailComponent.setEmailDestinatario(a.getUsuario().getEmail());
					emailComponent.setAssunto("Orçamento Doméstico - Lembrete de agendamento");
					emailComponent.setMensagem(mensagemEmail.toString());
					emailComponent.enviarEmail();					
				}
			}
		
		} catch (Exception e) {
			logger.catching(e);
			e.printStackTrace();
		}
	}
	
	@Scheduled(fixedDelay=3600000)
	@SuppressWarnings("deprecation")
	public void executarTarefa() {
		try {
			
			CriterioAgendamento criterioAgendamento = new CriterioAgendamento();
			criterioAgendamento.setTipo(TipoAgendamento.PREVISAO);
			
			List<Agenda> agendamentos = getService().buscarPorCriterioAgendamento(criterioAgendamento);
			List<LancamentoConta> lancamentosAtualizados = new ArrayList<>();
			LancamentoConta lancamento;
			Date dataLancamento;
			
			for (Agenda agenda : agendamentos) {
				Date dataAgenda = new Date(agenda.getInicio().getYear()+1900, agenda.getInicio().getMonth(), agenda.getInicio().getDate(), 0, 0, 0);
				switch (agenda.getEntity()) {
				case "LancamentoConta":
					lancamento = getLancamentoContaService().buscarPorID(agenda.getIdEntity());
					if (lancamento != null) {
						dataLancamento = new Date(lancamento.getDataPagamento().getYear()+1900, lancamento.getDataPagamento().getMonth(), lancamento.getDataPagamento().getDate(), 0, 0, 0);
						if (lancamento.getStatusLancamentoConta().equals(StatusLancamentoConta.AGENDADO) && dataLancamento.equals(dataAgenda)) {
							lancamentosAtualizados.add(lancamento);
						} else {
							getService().excluir(agenda);							
						}					
					} else {
						getService().excluir(agenda);
					}
					break;
				default:
					break;
				}
			}
							
			CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
			criterioBusca.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.AGENDADO});
			criterioBusca.setDataInicio(new Date());
			
			List<LancamentoConta> lancamentos = getLancamentoContaService().buscarPorCriterioBusca(criterioBusca);
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
				
				getService().cadastrar(agenda);
			}
		
		} catch (Exception e) {
			logger.catching(e);
			e.printStackTrace();
		}
	}
}
