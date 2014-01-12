package br.com.hslife.orcamento.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Pessoal;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoUsuario;
import br.com.hslife.orcamento.util.Util;

public class PessoalRepositoryTest extends AbstractTestRepositories {
	
	private Usuario usuario = new Usuario();
	private Pessoal pessoal = new Pessoal();
	
	@Autowired
	private PessoalRepository pessoalRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	public void setPessoalRepository(PessoalRepository pessoalRepository) {
		this.pessoalRepository = pessoalRepository;
	}
	
	@SuppressWarnings("deprecation")
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
		pessoal.setUsuario(usuario);
		pessoal.setDataNascimento(new Date(1980, 5, 15));
		pessoal.setEscolaridade("Superior completo");
		pessoal.setEstadoCivil("Solteiro");
		pessoal.setEtnia("Parda");
		pessoal.setFiliacaoMae("Mãe de teste");
		pessoal.setFiliacaoPai("Pai de teste");
		pessoal.setNacionalidade("Brasileira");
		pessoal.setNaturalidade("Rio de Janeiro");
	}

	@Test
	public void testFindByUsuario() {		
		pessoalRepository.save(pessoal);
		
		// Testa o método em questão
		Pessoal pessoalTest = new Pessoal();
		pessoalTest = pessoalRepository.findByUsuario(usuario);
		assertEquals(pessoal, pessoalTest);
	}

	@Test
	public void testSave() {
		pessoalRepository.save(pessoal);
		
		// Testa o método em questão
		assertNotNull(pessoal.getId());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testUpdate() {
		pessoalRepository.save(pessoal);
		
		pessoal.setUsuario(usuario);
		pessoal.setDataNascimento(new Date(1980, 5, 15));
		pessoal.setEscolaridade("Ensino médio completo");
		pessoal.setEstadoCivil("Casado");
		pessoal.setEtnia("Branco");
		pessoal.setFiliacaoMae("Mãe");
		pessoal.setFiliacaoPai("Pai");
		pessoal.setNacionalidade("Brasileira naturalizado");
		pessoal.setNaturalidade("São Paulo");
		
		// Testa o método em questão
		pessoalRepository.update(pessoal);
		
		Pessoal pessoalTest = pessoalRepository.findById(pessoal.getId());
		
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
	public void testDelete() {
		pessoalRepository.save(pessoal);
		
		// Testa o método em questão
		pessoalRepository.delete(pessoal);
		
		Pessoal pessoalTest = pessoalRepository.findById(pessoal.getId());
		assertNull(pessoalTest);
	}

	@Test
	public void testFindById() {
		pessoalRepository.save(pessoal);
		
		// Testa o método em questão
		Pessoal pessoalTest = pessoalRepository.findById(pessoal.getId());
		assertEquals(pessoal.getId(), pessoalTest.getId());
	}

}