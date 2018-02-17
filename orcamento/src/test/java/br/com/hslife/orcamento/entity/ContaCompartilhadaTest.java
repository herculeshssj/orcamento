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

para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor,

Boston, MA  02110-1301, USA.


Para mais informações sobre o programa Orçamento Doméstico e seu autor

entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
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
