package br.com.hslife.orcamento.entity;

import static org.junit.Assert.assertEquals;

import java.util.Date;

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
		Date dataAtual = new Date();
		
		entity.gerarHash();
		
		assertEquals(Util.SHA256(dataAtual.toString()), entity.getHashAutorizacao());
		assertEquals(dataAtual, entity.getDataGeracaoHash());
	}

}
