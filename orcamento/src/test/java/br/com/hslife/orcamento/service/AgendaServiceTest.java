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

***/package br.com.hslife.orcamento.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Agenda;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoAgendamento;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoUsuario;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.facade.IAgenda;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.model.CriterioAgendamento;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.util.Util;

public class AgendaServiceTest extends AbstractTestServices {
	
	private Usuario usuario = new Usuario();
	private Moeda moeda = new Moeda();
	private Conta conta = new Conta();
	private Agenda agenda = new Agenda();
	private LancamentoConta lancamento = new LancamentoConta();
	private List<LancamentoConta> lancamentos = new ArrayList<>();
	
	@Autowired
	private IAgenda agendaService;
	
	@Autowired
	private IUsuario usuarioService;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private ILancamentoConta lancamentoContaService;
	
	@Before
	public void initializeTestEnvironment() throws ApplicationException {
		
		// Cria um novo usuário		
		usuario.setEmail("contato@hslife.com.br");
		usuario.setLogin("usuario_" + Util.formataDataHora(new Date(), Util.DATAHORA));
		usuario.setNome("Usuário de Teste - PessoalRepository");
		usuario.setSenha(Util.SHA1("teste"));
		usuario.setTipoUsuario(TipoUsuario.ROLE_USER);
		usuarioService.cadastrar(usuario);
		
		// Cria uma nova moeda para o usuário
		moeda.setAtivo(true);
		moeda.setCodigoMonetario("BRL");
		moeda.setNome("Real");
		moeda.setPadrao(true);
		moeda.setPais("Brasil");
		moeda.setSiglaPais("BR");
		moeda.setUsuario(usuario);
		moeda.setSimboloMonetario("R$");
		moedaService.cadastrar(moeda);
		
		// Cria uma nova informação pessoal
		agenda = new Agenda();
		agenda.setUsuario(usuario);
		agenda.setDescricao("Compromisso de teste");
		agenda.setInicio(new Date());
		agenda.setFim(new Date());
		agenda.setTipoAgendamento(TipoAgendamento.COMPROMISSO);
		agendaService.cadastrar(agenda);
		
		// Cria uma nova conta para o usuário
		conta.setDescricao("Conta de teste - Calendario de atividades");
		conta.setDataAbertura(new Date());
		conta.setSaldoInicial(100);
		conta.setTipoConta(TipoConta.CORRENTE);
		conta.setUsuario(usuario);
		conta.setMoeda(moeda);
		contaService.cadastrar(conta);
		
		// Cria três lançamentos agendados para a conta
		lancamentos = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			lancamento.setStatusLancamentoConta(StatusLancamentoConta.AGENDADO);
			lancamento.setConta(conta);
			lancamento.setDataPagamento(new Date());
			lancamento.setDescricao("Lançamento de teste 0" + i);
			lancamento.setTipoLancamento(TipoLancamento.RECEITA);
			lancamento.setValorPago(100);
			lancamento.setMoeda(moeda);
			lancamentos.add(lancamento);
			lancamentoContaService.cadastrar(lancamento);
			lancamento = new LancamentoConta();
		}
	}
	
	//@Test
	public void testBuscarAgendamentosLancamentosAgendados() throws ApplicationException {
		CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
		criterioBusca.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.AGENDADO});
		int contador = 0;
		//for (Agenda agenda : agendaService.buscarAgendamentoLancamentosAgendados(conta, new Date(), new Date())) {
		for (Agenda agenda : agendaService.buscarAgendamentoLancamentosAgendados(conta, null, null)) {
			for (int i = 0; i < 3; i++) {
				if ((lancamentos.get(i).getConta().getLabel() + " - " + lancamentos.get(i).getDescricao()).equals(agenda.getDescricao()) /*&& agenda.getLancamentoAgendado().isAgendado()*/) {
					contador++;
				}
			}
		}
		assertEquals(3, contador);
	}
	
	@Test
	public void testBuscarPorCriterioAgendamento() throws ApplicationException {
		CriterioAgendamento criterioBusca = new CriterioAgendamento();
		criterioBusca.setDescricao("Compromisso de teste");
		//criterioBusca.setInicio(new Date());
		//criterioBusca.setFim(new Date());
		criterioBusca.setTipo(TipoAgendamento.COMPROMISSO);
		
		List<Agenda> listaAgenda = agendaService.buscarPorCriterioAgendamento(criterioBusca);
		assertEquals(1, listaAgenda.size());
		assertEquals(agenda, listaAgenda.get(0));
	}
}
