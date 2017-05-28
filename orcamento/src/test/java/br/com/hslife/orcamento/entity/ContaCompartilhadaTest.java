package br.com.hslife.orcamento.entity;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import br.com.hslife.orcamento.util.EntityInitializerFactory;
import br.com.hslife.orcamento.util.Util;

public class ContaCompartilhadaTest {
	
	private ContaCompartilhada entity;
	private Conta conta;
	private Usuario usuario;
	
	@Before
	public void setUp() {
		usuario = EntityInitializerFactory.createUsuario();
		Moeda moeda = EntityInitializerFactory.createMoeda(usuario);
		conta = EntityInitializerFactory.createConta(usuario, moeda);
		
		entity = EntityInitializerFactory.createContaCompartilhada(conta, usuario);
	}

	@Test
	public void testContaCompartilhadaContaUsuario() {
		entity = new ContaCompartilhada(conta, usuario);
		
		assertEquals(usuario, entity.getUsuario());
		assertEquals(conta, entity.getConta());
	}

	@Test
	public void testGerarHash() {
		Calendar dataAtual = Calendar.getInstance();
		
		entity.gerarHash();
		
		assertEquals(Util.SHA256(dataAtual.getTime().toString()), entity.getHashAutorizacao());
		
		Calendar dataAtualTest = Calendar.getInstance();
		dataAtualTest.setTime(entity.getDataGeracaoHash());
		assertEquals(dataAtual.get(Calendar.DAY_OF_YEAR), dataAtualTest.get(Calendar.DAY_OF_YEAR));
		assertEquals(dataAtual.get(Calendar.MONTH), dataAtualTest.get(Calendar.MONTH));
		assertEquals(dataAtual.get(Calendar.YEAR), dataAtualTest.get(Calendar.YEAR));
		assertEquals(dataAtual.get(Calendar.HOUR_OF_DAY), dataAtualTest.get(Calendar.HOUR_OF_DAY));
		assertEquals(dataAtual.get(Calendar.MINUTE), dataAtualTest.get(Calendar.MINUTE));
	}

}
