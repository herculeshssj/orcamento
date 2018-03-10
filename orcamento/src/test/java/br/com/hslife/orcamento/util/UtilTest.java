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
package br.com.hslife.orcamento.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.com.hslife.orcamento.mock.UtilsMock;

public class UtilTest {
	
	@Test
	public void testEhVazio() {
		
		String string1 = null;
		String string2 = "";
		String string3 = "STRING";
		
		assertEquals(true, Util.eVazio(string1));
		assertEquals(true, Util.eVazio(string2));
		assertEquals(false, Util.eVazio(string3));
	}
	
	@Test
	public void testMockString() {
		String example = UtilsMock.mockString(50);
		assertEquals(50, example.length());
		
		String sample = UtilsMock.mockString(200);
		assertEquals(200, sample.length());
	}
	
	@Test
	public void testFormatarCPF() {
		assertEquals("864.734.305-04", Util.formatarCPF("86473430504"));
	}

	@Test
	public void testFormatarCNPJ() {
		assertEquals("23.476.757/0001-51", Util.formatarCNPJ("23476757000151"));
	}
	
	@Test
	public void testRemoverAcentos() {
		String acentos = "áéíóúàèìòùâêîôûãõäëïöüç";
		String semAcentos = "aeiouaeiouaeiouaoaeiouc";
		assertEquals(semAcentos, Util.removerAcentos(acentos));
	}
}