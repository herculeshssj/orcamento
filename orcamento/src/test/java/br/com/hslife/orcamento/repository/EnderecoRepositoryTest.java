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
package br.com.hslife.orcamento.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Endereco;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class EnderecoRepositoryTest extends AbstractTestRepositories {
	
	private Usuario usuario = new Usuario();
	private Endereco endereco = new Endereco();
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@SuppressWarnings("deprecation")
	@Before
	public void initializeEntities() {
		enderecoRepository.setSessionFactory(sessionFactory);
		usuarioRepository.setSessionFactory(sessionFactory);
		
		// Cria um novo usuário
		usuario = EntityInitializerFactory.initializeUsuario();
		usuarioRepository.save(usuario);
		
		// Cria uma nova instância de endereço para testar os recursos do CRUD
		endereco = new Endereco.Builder("Residencial")
			.tipoLogradouro("Avenida")
			.logradouro("Ministro Lafaeyte de Andrade")
			.numero("1683")
			.complemento("Bl. 3 Apt. 404")
			.bairro("Marco II")
			.cidade("Nova Iguaçu")
			.estado("RJ")
			.cep("26261220")
			.usuario(usuario)
			.build();
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
