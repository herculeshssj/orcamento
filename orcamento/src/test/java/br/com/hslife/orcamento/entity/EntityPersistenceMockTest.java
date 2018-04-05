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

import org.junit.Test;

import br.com.hslife.orcamento.enumeration.EntityPersistenceEnum;
import br.com.hslife.orcamento.mock.EntityPersistenceMock;
import static org.junit.Assert.assertEquals;

public class EntityPersistenceMockTest {

	@Test
	public void testCriarUsuario() {
		
		EntityPersistenceMock epm = new EntityPersistenceMock().criarUsuario();
		
		if (epm.get(EntityPersistenceEnum.USUARIO) instanceof Usuario) {
			Usuario usuario = (Usuario) epm.get(EntityPersistenceEnum.USUARIO);
			usuario.validate();
		}
		
	}
	
	@Test
	public void testComMoedaPadrao() {
		
		EntityPersistenceMock epm = new EntityPersistenceMock()
				.criarUsuario()
				.comMoedaPadrao();
		
		if (epm.get(EntityPersistenceEnum.MOEDA) instanceof Moeda) {
			Moeda moeda = (Moeda) epm.get(EntityPersistenceEnum.MOEDA);
			moeda.validate();
		}
		
		if (epm.get(EntityPersistenceEnum.USUARIO) instanceof Usuario) {
			Usuario usuario = (Usuario) epm.get(EntityPersistenceEnum.USUARIO);
			usuario.validate();
		}
	}
	
	@Test
	public void testComFavorecido() {
		
		EntityPersistenceMock epm = new EntityPersistenceMock()
				.criarUsuario()
				.comFavorecido(false);
		
		if (epm.get(EntityPersistenceEnum.FAVORECIDO) instanceof Favorecido) {
			Favorecido favorecido = (Favorecido)epm.get(EntityPersistenceEnum.FAVORECIDO);
			favorecido.validate();
		}
	}
	
	@Test
	public void testComArquivoEmMaos() {
		EntityPersistenceMock epm = new EntityPersistenceMock()
				.criarUsuario()
				.comArquivoEmMaos();
		
		if (epm.get(EntityPersistenceEnum.ARQUIVO) instanceof Arquivo) {
			Arquivo arquivo = (Arquivo)epm.get(EntityPersistenceEnum.ARQUIVO);
			assertEquals(8192l, arquivo.getTamanho());
			assertEquals("text/html", arquivo.getContentType());
		}
	}
	
	public void testEPossuiImovel() {
		EntityPersistenceMock epm = new EntityPersistenceMock()
				.criarUsuario()
				.comFavorecido(true)
				.comMoedaPadrao()
				.ePossuiImovel();
		
		if (epm.get(EntityPersistenceEnum.PATRIMONIO) instanceof Patrimonio) {
			Patrimonio patrimonio = (Patrimonio)epm.get(EntityPersistenceEnum.PATRIMONIO);
			patrimonio.validate();
		}
	}
}
