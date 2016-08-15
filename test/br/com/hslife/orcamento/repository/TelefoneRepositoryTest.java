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

package br.com.hslife.orcamento.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Telefone;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoUsuario;
import br.com.hslife.orcamento.util.EntityInitializerFactory;
import br.com.hslife.orcamento.util.Util;

public class TelefoneRepositoryTest extends AbstractTestRepositories {
	
	private Usuario usuario = new Usuario();
	private Telefone telefone = new Telefone();
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Before
	public void initializeEntities() {
		telefoneRepository.setSessionFactory(sessionFactory);
		usuarioRepository.setSessionFactory(sessionFactory);
		
		// Cria um novo usuário
		usuario.setEmail("contato@hslife.com.br");
		usuario.setLogin("usuario_" + Util.formataDataHora(new Date(), Util.DATAHORA));
		usuario.setNome("Usuário de Teste - PessoalRepository");
		usuario.setSenha(Util.SHA1("teste"));
		usuario.setTipoUsuario(TipoUsuario.ROLE_USER);
		usuarioRepository.save(usuario);
		
		// Cria uma nova instância de endereço para testar os recursos do CRUD
		telefone = EntityInitializerFactory.initializeTelefone(usuario);
	}

	@Test
	public void testFindByUsuario() {		
		telefoneRepository.save(telefone);
		
		// Testa o método em questão		
		List<Telefone> listaTelefone = telefoneRepository.findByUsuario(usuario);
		assertEquals(1, listaTelefone.size());
		assertEquals(telefone, listaTelefone.get(0));
	}

	@Test
	public void testSave() {
		telefoneRepository.save(telefone);
		
		// Testa o método em questão
		assertNotNull(telefone.getId());
	}

	@Test
	public void testUpdate() {
		telefoneRepository.save(telefone);
		
		telefone.setDdd("21");
		telefone.setDescricao("Residencial");
		telefone.setNumero("26698131");
		telefone.setRamal(null);
		telefone.setUsuario(usuario);
		
		// Testa o método em questão
		telefoneRepository.update(telefone);
		
		Telefone telefoneTest = telefoneRepository.findById(telefone.getId());
		
		assertEquals(telefone.getDescricao(), telefoneTest.getDescricao());
		assertEquals(telefone.getDdd(), telefoneTest.getDdd());
		assertEquals(telefone.getNumero(), telefoneTest.getNumero());
		assertEquals(telefone.getRamal(), telefoneTest.getRamal());
		assertEquals(telefone.getUsuario(), telefoneTest.getUsuario());
	}

	@Test
	public void testDelete() {
		telefoneRepository.save(telefone);
				
		// Testa o método em questão
		telefoneRepository.delete(telefone);
		
		Telefone telefoneTest = telefoneRepository.findById(telefone.getId());
		assertNull(telefoneTest);
	}

	@Test
	public void testFindById() {
		telefoneRepository.save(telefone);
		
		// Testa o método em questão
		Telefone telefoneTest = telefoneRepository.findById(telefone.getId());
		assertEquals(telefone.getId(), telefoneTest.getId());
	}

}