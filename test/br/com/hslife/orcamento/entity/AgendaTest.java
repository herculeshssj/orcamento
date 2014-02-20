package br.com.hslife.orcamento.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.hslife.orcamento.enumeration.TipoAgendamento;
import br.com.hslife.orcamento.exception.BusinessException;

public class AgendaTest {

	private Agenda entity;

	@Before
	public void setUp() throws Exception {
		
		Usuario usuario = new Usuario();
		usuario.setNome("Usuário de teste");
		
		entity = new Agenda();
		entity.setUsuario(usuario);
		entity.setDescricao("Compromisso de teste");
		entity.setLocalAgendamento("Local do compromisso");
		entity.setInicio(new Date());
		entity.setFim(new Date());
		entity.setTipoAgendamento(TipoAgendamento.COMPROMISSO);
		//entity.setDataInicio(new Date());
		//entity.setDataFim(new Date());
		//entity.setHoraInicio(new Date());
		//entity.setHoraFim(new Date());
		
	}

	@Test
	public void testGetLabel() {
		assertEquals("Compromisso de teste", entity.getLabel());
	}

	@Test
	public void testValidateDescricao() {
		try {
			entity.setDescricao("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ");
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
	public void testValidateLocalAgendamento() {
		try {
			entity.setLocalAgendamento("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 200 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	/*
	@Test
	public void testValidateDataInicio() {
		try {
			entity.validate();
			entity.setDataInicio(null);
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Informe a data de início!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateHoraInicio() {
		try {
			entity.validate();
			entity.setHoraInicio(null);
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Informe a hora de início!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	*/
	
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
