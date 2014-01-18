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

import br.com.hslife.orcamento.entity.Endereco;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoUsuario;
import br.com.hslife.orcamento.util.Util;

public class EnderecoRepositoryTest extends AbstractTestRepositories {
	
	private Usuario usuario = new Usuario();
	private Endereco endereco = new Endereco();
	
	@Autowired
	private PessoalRepository pessoalRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
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
		
		// Cria uma nova instância de endereço para testar os recursos do CRUD
		endereco.setTipoLogradouro("Avenida");
		endereco.setLogradouro("Ministro Lafaeyte de Andrade");
		endereco.setNumero("1683");
		endereco.setComplemento("Bl. 3 Apt. 404");
		endereco.setBairro("Marco II");
		endereco.setCidade("Nova Iguaçu");
		endereco.setEstado("RJ");
		endereco.setCep("26261220");
		endereco.setDescricao("Residencial");
		endereco.setUsuario(usuario);
	}

	@Test
	public void testFindByUsuario() {				
		enderecoRepository.save(endereco);
		
		// Testa o método em questão		
		List<Endereco> listaEndereco = enderecoRepository.findByUsuario(usuario);
		assertEquals(1, listaEndereco.size());
		assertEquals(endereco, listaEndereco.get(0));
	}

	@Test
	public void testSave() {
		enderecoRepository.save(endereco);
		
		// Testa o método em questão
		assertNotNull(endereco.getId());
	}

	@Test
	public void testUpdate() {
		enderecoRepository.save(endereco);
		
		endereco.setUsuario(usuario);
		endereco.setTipoLogradouro("Rua");
		endereco.setLogradouro("Okir");
		endereco.setNumero("64");
		endereco.setComplemento("");
		endereco.setBairro("Marco II");
		endereco.setCidade("Nova Iguaçu");
		endereco.setEstado("RJ");
		endereco.setCep("26261220");
		endereco.setDescricao("Comercial");
		
		// Testa o método em questão
		enderecoRepository.update(endereco);
		
		Endereco enderecoTest = enderecoRepository.findById(endereco.getId());
		
		assertEquals(endereco.getUsuario(),enderecoTest.getUsuario());
		assertEquals(endereco.getTipoLogradouro(),enderecoTest.getTipoLogradouro());
		assertEquals(endereco.getLogradouro(),enderecoTest.getLogradouro());
		assertEquals(endereco.getNumero(),enderecoTest.getNumero());
		assertEquals(endereco.getComplemento(),enderecoTest.getComplemento());
		assertEquals(endereco.getBairro(),enderecoTest.getBairro());
		assertEquals(endereco.getCidade(),enderecoTest.getCidade());
		assertEquals(endereco.getEstado(),enderecoTest.getEstado());
		assertEquals(endereco.getCep(),enderecoTest.getCep());
		assertEquals(endereco.getDescricao(),enderecoTest.getDescricao());
	}

	@Test
	public void testDelete() {
		enderecoRepository.save(endereco);
				
		// Testa o método em questão
		enderecoRepository.delete(endereco);
		
		Endereco enderecoTest = enderecoRepository.findById(endereco.getId());
		assertNull(enderecoTest);
	}

	@Test
	public void testFindById() {
		enderecoRepository.save(endereco);
		
		// Testa o método em questão
		Endereco enderecoTest = enderecoRepository.findById(endereco.getId());
		assertEquals(endereco.getId(), enderecoTest.getId());
	}

}