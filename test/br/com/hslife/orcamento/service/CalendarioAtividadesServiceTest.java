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

package br.com.hslife.orcamento.service;

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
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoAgendamento;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoUsuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICalendarioAtividades;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.model.CriterioAgendamento;
import br.com.hslife.orcamento.util.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.util.Util;

public class CalendarioAtividadesServiceTest extends AbstractTestServices {
	
	private Usuario usuario = new Usuario();
	private Conta conta = new Conta();
	private Agenda agenda = new Agenda();
	private LancamentoConta lancamento = new LancamentoConta();
	private List<LancamentoConta> lancamentos = new ArrayList<>();
	
	@Autowired
	private ICalendarioAtividades calendarioAtividadesService;
	
	@Autowired
	private IUsuario usuarioService;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private ILancamentoConta lancamentoContaService;
	
	@Before
	public void initializeTestEnvironment() throws BusinessException {
		
		// Cria um novo usuário		
		usuario.setEmail("contato@hslife.com.br");
		usuario.setLogin("usuario_" + Util.formataDataHora(new Date(), Util.DATAHORA));
		usuario.setNome("Usuário de Teste - PessoalRepository");
		usuario.setSenha(Util.SHA1("teste"));
		usuario.setTipoUsuario(TipoUsuario.ROLE_USER);
		usuarioService.cadastrar(usuario);
		
		// Cria uma nova informação pessoal
		agenda = new Agenda();
		agenda.setUsuario(usuario);
		agenda.setDescricao("Compromisso de teste");
		agenda.setInicio(new Date());
		agenda.setFim(new Date());
		agenda.setTipoAgendamento(TipoAgendamento.COMPROMISSO);
		calendarioAtividadesService.cadastrar(agenda);
		
		// Cria uma nova conta para o usuário
		conta.setDescricao("Conta de teste - Calendario de atividades");
		conta.setDataAbertura(new Date());
		conta.setSaldoInicial(100);
		conta.setTipoConta(TipoConta.CORRENTE);
		conta.setUsuario(usuario);
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
			lancamentos.add(lancamento);
			lancamentoContaService.cadastrar(lancamento);
			lancamento = new LancamentoConta();
		}
	}
	
	@Test
	public void testBuscarAgendamentosLancamentosAgendados() throws BusinessException {
		CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
		criterioBusca.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.AGENDADO});
		int contador = 0;
		for (Agenda agenda : calendarioAtividadesService.buscarAgendamentoLancamentosAgendados(conta, new Date(), new Date())) {
			for (int i = 0; i < 3; i++) {
				if ((lancamentos.get(i).getConta().getLabel() + " - " + lancamentos.get(i).getDescricao()).equals(agenda.getDescricao()) /*&& agenda.getLancamentoAgendado().isAgendado()*/) {
					contador++;
				}
			}
		}
		assertEquals(3, contador);
	}
	
	public void testBuscarPorCriterioAgendamento() throws BusinessException {
		CriterioAgendamento criterioBusca = new CriterioAgendamento();
		criterioBusca.setDescricao("Compromisso de teste");
		criterioBusca.setInicio(new Date());
		criterioBusca.setFim(new Date());
		criterioBusca.setTipo(TipoAgendamento.COMPROMISSO);
		
		List<Agenda> listaAgenda = calendarioAtividadesService.buscarPorCriterioAgendamento(criterioBusca);
		assertEquals(1, listaAgenda.size());
		assertEquals(agenda, listaAgenda.get(0));
	}
	/*
	@Test
	@SuppressWarnings("deprecation")
	public void testSalvarDadosPessoais() throws BusinessException {
		// Cria uma nova informação pessoal
		Pessoal pessoal = new Pessoal();
		pessoal.setUsuario(usuario);
		pessoal.setDataNascimento(new Date(1980, 5, 15));
		pessoal.setEscolaridade("Superior completo");
		pessoal.setEstadoCivil("Solteiro");
		pessoal.setEtnia("Parda");
		pessoal.setFiliacaoMae("Mãe de teste");
		pessoal.setFiliacaoPai("Pai de teste");
		pessoal.setNacionalidade("Brasileira");
		pessoal.setNaturalidade("Rio de Janeiro");
		
		// Salva as informações pessoais do usuário
		informacaoPessoalService.salvarDadosPessoais(pessoal);
		assertNotNull(pessoal.getId());
		
		// Altera as informações pessoais do usuário
		pessoal.setUsuario(usuario);
		pessoal.setDataNascimento(new Date(1980, 5, 15));
		pessoal.setEscolaridade("Ensino médio completo");
		pessoal.setEstadoCivil("Casado");
		pessoal.setEtnia("Branco");
		pessoal.setFiliacaoMae("Mãe");
		pessoal.setFiliacaoPai("Pai");
		pessoal.setNacionalidade("Brasileira naturalizado");
		pessoal.setNaturalidade("São Paulo");
		
		// Salva as informações pessoais do usuário
		informacaoPessoalService.salvarDadosPessoais(pessoal);
		
		Pessoal pessoalTest = informacaoPessoalService.buscarDadosPessoais(usuario);
		
		assertEquals(pessoal.getUsuario(), pessoalTest.getUsuario());
		assertEquals(pessoal.getDataNascimento(), pessoalTest.getDataNascimento());
		assertEquals(pessoal.getEscolaridade(), pessoalTest.getEscolaridade());
		assertEquals(pessoal.getEstadoCivil(), pessoalTest.getEstadoCivil());
		assertEquals(pessoal.getEtnia(), pessoalTest.getEtnia());
		assertEquals(pessoal.getFiliacaoMae(), pessoalTest.getFiliacaoMae());
		assertEquals(pessoal.getFiliacaoPai(), pessoalTest.getFiliacaoPai());
		assertEquals(pessoal.getNacionalidade(), pessoalTest.getNacionalidade());
		assertEquals(pessoal.getNaturalidade(), pessoalTest.getNaturalidade());
	}

	@Test
	public void testSalvarEnderecos() throws BusinessException {
		List<Endereco> listaEndereco = new ArrayList<Endereco>();
		Endereco endereco = new Endereco();
		
		// Popula a lista de endereços
		for (int i = 0; i < 3; i++) {			
			endereco.setTipoLogradouro("Rua");
			endereco.setLogradouro("A" + i);
			endereco.setNumero(Integer.toString(i));
			endereco.setComplemento("Bloco " + i);
			endereco.setBairro("Centro");
			endereco.setCidade("Nova Iguaçu");
			endereco.setEstado("RJ");
			endereco.setCep("2626122" + i);
			endereco.setDescricao("Residencial");
			endereco.setUsuario(usuario);
			listaEndereco.add(endereco);
			endereco = new Endereco();
		}
		
		// Salva os endereços
		informacaoPessoalService.salvarEnderecos(listaEndereco, usuario);
		
		for (Endereco e : listaEndereco) {
			assertNotNull(e.getId());
		}
		
		// Altera as informações de endereço
		for (int i = 1; i < 3; i++) {			
			listaEndereco.get(i).setTipoLogradouro("Avenida");
			listaEndereco.get(i).setLogradouro("B" + i);
			listaEndereco.get(i).setNumero(Integer.toString(i));
			listaEndereco.get(i).setComplemento("Quadra " + i);
			listaEndereco.get(i).setBairro("Interior");
			listaEndereco.get(i).setCidade("Rio de Janeiro");
			listaEndereco.get(i).setEstado("RJ");
			listaEndereco.get(i).setCep("2006292" + i);
			listaEndereco.get(i).setDescricao("Comercial");
			listaEndereco.get(i).setUsuario(usuario);			
		}		
		
		// Salva as informações de endereço
		informacaoPessoalService.salvarEnderecos(listaEndereco, usuario);
		
		List<Endereco> listaEnderecoTest = informacaoPessoalService.buscarEnderecos(usuario);
		for (int i = 0; i < 3; i++) {			
			assertEquals(listaEndereco.get(i).getTipoLogradouro(), listaEnderecoTest.get(i).getTipoLogradouro());
			assertEquals(listaEndereco.get(i).getLogradouro(), listaEnderecoTest.get(i).getLogradouro());
			assertEquals(listaEndereco.get(i).getNumero(), listaEnderecoTest.get(i).getNumero());
			assertEquals(listaEndereco.get(i).getComplemento(), listaEnderecoTest.get(i).getComplemento());
			assertEquals(listaEndereco.get(i).getBairro(), listaEnderecoTest.get(i).getBairro());
			assertEquals(listaEndereco.get(i).getCidade(), listaEnderecoTest.get(i).getCidade());
			assertEquals(listaEndereco.get(i).getEstado(), listaEnderecoTest.get(i).getEstado());
			assertEquals(listaEndereco.get(i).getCep(), listaEnderecoTest.get(i).getCep());
			assertEquals(listaEndereco.get(i).getDescricao(), listaEnderecoTest.get(i).getDescricao());
			assertEquals(listaEndereco.get(i).getUsuario(), listaEnderecoTest.get(i).getUsuario());		
		}
	}

	@Test
	public void testSalvarTelefones() throws BusinessException {
		List<Telefone> listaTelefone = new ArrayList<Telefone>();
		Telefone telefone = new Telefone();
		
		// Popula a lista de telefones
		for (int i = 0; i < 3; i++) {
			telefone.setDescricao("Telefone " + i);
			telefone.setDdd(Integer.toString(i));
			telefone.setNumero(Integer.toString(i));
			telefone.setRamal("");
			telefone.setUsuario(usuario);
			listaTelefone.add(telefone);
			telefone = new Telefone();
		}
		
		// Salva os telefones
		informacaoPessoalService.salvarTelefones(listaTelefone, usuario);
		
		for (Telefone t : listaTelefone) {
			assertNotNull(t.getId());
		}
		
		// Altera as informações de endereço
		for (int i = 1; i < 3; i++) {
			listaTelefone.get(i).setDescricao("Telefone: " + i);
			listaTelefone.get(i).setDdd("00" + i);
			listaTelefone.get(i).setNumero("11111111" + i);
			listaTelefone.get(i).setRamal(Integer.toString(i));
			listaTelefone.get(i).setUsuario(usuario);						
		}		
		
		// Salva as informações de endereço
		informacaoPessoalService.salvarTelefones(listaTelefone, usuario);
		
		List<Telefone> listaTelefoneTest = informacaoPessoalService.buscarTelefones(usuario);
		for (int i = 0; i < 3; i++) {
			assertEquals(listaTelefone.get(i).getDescricao(), listaTelefoneTest.get(i).getDescricao());
			assertEquals(listaTelefone.get(i).getDdd(), listaTelefoneTest.get(i).getDdd());
			assertEquals(listaTelefone.get(i).getNumero(), listaTelefoneTest.get(i).getNumero());
			assertEquals(listaTelefone.get(i).getRamal(), listaTelefoneTest.get(i).getRamal());
			assertEquals(listaTelefone.get(i).getUsuario(), listaTelefoneTest.get(i).getUsuario());		
		}
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testBuscarDadosPessoais() throws BusinessException {
		// Cria uma nova informação pessoal
		Pessoal pessoal = new Pessoal();
		pessoal.setUsuario(usuario);
		pessoal.setDataNascimento(new Date(1980, 5, 15));
		pessoal.setEscolaridade("Superior completo");
		pessoal.setEstadoCivil("Solteiro");
		pessoal.setEtnia("Parda");
		pessoal.setFiliacaoMae("Mãe de teste");
		pessoal.setFiliacaoPai("Pai de teste");
		pessoal.setNacionalidade("Brasileira");
		pessoal.setNaturalidade("Rio de Janeiro");
		
		// Salva as informações pessoais do usuário
		informacaoPessoalService.salvarDadosPessoais(pessoal);
		
		// Busca as informações pessoais do usuário
		Pessoal pessoalTest = informacaoPessoalService.buscarDadosPessoais(usuario);
		
		assertEquals(pessoal.getId(), pessoalTest.getId());
	}

	@Test
	public void testBuscarEnderecos() throws BusinessException {
		List<Endereco> listaEndereco = new ArrayList<Endereco>();
		Endereco endereco = new Endereco();
		
		// Popula a lista de endereços
		for (int i = 0; i < 3; i++) {			
			endereco.setTipoLogradouro("Rua");
			endereco.setLogradouro("A" + i);
			endereco.setNumero(Integer.toString(i));
			endereco.setComplemento("Bloco " + i);
			endereco.setBairro("Centro");
			endereco.setCidade("Nova Iguaçu");
			endereco.setEstado("RJ");
			endereco.setCep("2626122" + i);
			endereco.setDescricao("Residencial");
			endereco.setUsuario(usuario);
			listaEndereco.add(endereco);
			endereco = new Endereco();
		}
		
		// Salva os endereços
		informacaoPessoalService.salvarEnderecos(listaEndereco, usuario);
		
		// Busca os endereços do usuário
		List<Endereco> listaEnderecoTest = informacaoPessoalService.buscarEnderecos(usuario);
		for (int i = 0; i < 3; i++) {
			assertEquals(listaEndereco.get(i).getId(), listaEnderecoTest.get(i).getId());
		}
	}

	@Test
	public void testBuscarTelefones() throws BusinessException {
		List<Telefone> listaTelefone = new ArrayList<Telefone>();
		Telefone telefone = new Telefone();
		
		// Popula a lista de telefones
		for (int i = 0; i < 3; i++) {
			telefone.setDescricao("Telefone " + i);
			telefone.setDdd(Integer.toString(i));
			telefone.setNumero(Integer.toString(i));
			telefone.setRamal("");
			telefone.setUsuario(usuario);
			listaTelefone.add(telefone);
			telefone = new Telefone();
		}
		
		// Salva os telefones
		informacaoPessoalService.salvarTelefones(listaTelefone, usuario);
		
		// Busca os telefones do usuário
		List<Telefone> listaTelefoneTest = informacaoPessoalService.buscarTelefones(usuario);
		for (int i = 0; i < 3; i++) {
			assertEquals(listaTelefone.get(i).getId(), listaTelefoneTest.get(i).getId());
		}
	}
	*/
}