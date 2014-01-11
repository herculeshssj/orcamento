package br.com.hslife.orcamento.entity;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.hslife.orcamento.exception.BusinessException;

public class PessoalTest {
	
	private Pessoal entity;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		Usuario usuario = new Usuario();
		usuario.setNome("Usuário de teste");
		
		entity = new Pessoal();
		entity.setUsuario(usuario);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetLabel() {
		assertEquals("Usuário de teste", entity.getLabel());
	}

	@Test
	public void testValidateGenero() {
		try {
			entity.setGenero('G');
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Gênero inexistente!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}

	@Test
	public void testValidateEtnia() {
		try {
			entity.setEtnia("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateTipoSanguineo() {
		try {
			entity.setTipoSanguineo("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 5 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateNacionalidade() {
		try {
			entity.setNacionalidade("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateNaturalidade() {
		try {
			entity.setNaturalidade("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateEscolaridade() {
		try {
			entity.setEscolaridade("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateFiliacaoPai() {
		try {
			entity.setFiliacaoPai("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 100 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateFiliacaoMae() {
		try {
			entity.setFiliacaoMae("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 100 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateEstadoCivil() {
		try {
			entity.setEstadoCivil("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateUsuario() {
		try {
			entity.setUsuario(null);
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Informe o usuário!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
}
