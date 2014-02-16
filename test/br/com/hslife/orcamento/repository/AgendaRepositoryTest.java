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

package br.com.hslife.orcamento.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Agenda;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoAgendamento;
import br.com.hslife.orcamento.enumeration.TipoUsuario;
import br.com.hslife.orcamento.util.Util;

public class AgendaRepositoryTest extends AbstractTestRepositories {
	
	private Usuario usuario = new Usuario();
	private Agenda agenda = new Agenda();
	
	@Autowired
	private AgendaRepository agendaRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Before
	public void initializeEntities() {
		// Cria um novo usuário
		usuario.setEmail("contato@hslife.com.br");
		usuario.setLogin("usuario_" + Util.formataDataHora(new Date(), Util.DATAHORA));
		usuario.setNome("Usuário de Teste - PessoalRepository");
		usuario.setSenha(Util.SHA1("teste"));
		usuario.setTipoUsuario(TipoUsuario.ROLE_USER);
		usuarioRepository.save(usuario);
		
		// Cria uma nova informação pessoal
		agenda = new Agenda();
		agenda.setUsuario(usuario);
		agenda.setDescricao("Compromisso de teste");
		agenda.setInicio(new Date());
		agenda.setFim(new Date());
		agenda.setTipoAgendamento(TipoAgendamento.COMPROMISSO);
	}

	@Test
	public void testFindByUsuario() {		
		agendaRepository.save(agenda);
		
		// Testa o método em questão		
		List<Agenda> listaAgenda = agendaRepository.findByUsuario(usuario);
		assertEquals(1, listaAgenda.size());
		assertEquals(agenda, listaAgenda.get(0));
	}

	@Test
	public void testSave() {
		agendaRepository.save(agenda);
		
		// Testa o método em questão
		assertNotNull(agenda.getId());
	}

	@Test
	public void testUpdate() {
		agendaRepository.save(agenda);
		
		agenda.setDescricao("Tarefa de teste");
		agenda.setInicio(new Date());
		agenda.setFim(new Date());
		agenda.setTipoAgendamento(TipoAgendamento.TAREFA);
		agenda.setEmitirAlerta(true);
		
		// Testa o método em questão
		agendaRepository.update(agenda);
		
		Agenda agendaTest = agendaRepository.findById(agenda.getId());
		
		assertEquals(agenda.getDescricao(), agendaTest.getDescricao());
		assertEquals(agenda.getInicio(), agendaTest.getInicio());
		assertEquals(agenda.getFim(), agendaTest.getFim());
		assertEquals(agenda.getTipoAgendamento(), agendaTest.getTipoAgendamento());
		assertEquals(agenda.isEmitirAlerta(), agendaTest.isEmitirAlerta());
		assertEquals(agenda.getUsuario(), agendaTest.getUsuario());
	}

	@Test
	public void testDelete() {
		agendaRepository.save(agenda);
		
		// Testa o método em questão
		agendaRepository.delete(agenda);
		
		Agenda agendaTest = agendaRepository.findById(agenda.getId());
		assertNull(agendaTest);
	}
	
	@Test
	public void testFindById() {
		agendaRepository.save(agenda);
		
		// Testa o método em questão
		Agenda agendaTest = agendaRepository.findById(agenda.getId());
		assertEquals(agenda.getId(), agendaTest.getId());
	}
}