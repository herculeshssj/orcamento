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
package br.com.hslife.orcamento.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import br.com.hslife.orcamento.util.VOInitializerFactory;

public class InfoOFXTest {

	private InfoOFX model;
	private String jsonObject;
	
	@Before
	public void initialize() {
		model = VOInitializerFactory.createInfoOFX();
		jsonObject = model.gerarJson();
	}
	
	@Test
	public void testGerarJson() {
		String json = model.gerarJson();
		
		InfoOFX modelTest = new InfoOFX();
		modelTest.lerJson(json);
		
		assertEquals(model, modelTest);
	}
	
	@Test
	public void testObjetosIguais() {
		InfoOFX modelTest = new InfoOFX();
		modelTest.lerJson(jsonObject);
		
		assertTrue(modelTest.equals(model));
	}
	
	@Test
	public void testHashCode() {
		assertFalse(new Object().hashCode() == model.hashCode());
	}
}