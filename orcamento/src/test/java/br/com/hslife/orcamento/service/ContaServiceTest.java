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
package br.com.hslife.orcamento.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.mock.EntityPersistenceMock;

public class ContaServiceTest extends AbstractTestServices {
	
	private Conta entity;
	
	@Autowired
	private IConta service;
	
	@Autowired
	private IUsuario usuarioService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Before
	public void initializeEntities() {		
		// Inicializa as entidades
		entity = EntityPersistenceMock.mockConta();
		
		// Salva as entidades pertinentes antes de iniciar os testes
		usuarioService.cadastrar(entity.getUsuario());
		moedaService.cadastrar(entity.getMoeda());
		service.cadastrar(entity);
	}
	
	@Test
	public void testFindDescricaoOrTipoContaOrAtivoByUsuario() {
		List<Conta> listaConta = service.buscarDescricaoOuTipoContaOuAtivoPorUsuario(null, null, entity.getUsuario(), null);
		assertEquals(1, listaConta.size());
		
		listaConta = service.buscarDescricaoOuTipoContaOuAtivoPorUsuario(null, null, entity.getUsuario(), false);
		assertEquals(0, listaConta.size());
		
		listaConta = service.buscarDescricaoOuTipoContaOuAtivoPorUsuario(null, new TipoConta[]{TipoConta.CORRENTE, TipoConta.POUPANCA}, entity.getUsuario(), true);
		assertEquals(1, listaConta.size());
		
		listaConta = service.buscarDescricaoOuTipoContaOuAtivoPorUsuario(null, new TipoConta[]{TipoConta.CARTAO, TipoConta.POUPANCA}, entity.getUsuario(), true);
		assertEquals(0, listaConta.size());
		
		listaConta = service.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", null, entity.getUsuario(), true);
		assertEquals(1, listaConta.size());
		
		listaConta = service.buscarDescricaoOuTipoContaOuAtivoPorUsuario(entity.getDescricao(), null, entity.getUsuario(), true);
		assertEquals(1, listaConta.size());
		
		listaConta = service.buscarDescricaoOuTipoContaOuAtivoPorUsuario(entity.getDescricao(), null, entity.getUsuario(), false);
		assertEquals(0, listaConta.size());
	}
	
	@Test(expected=Exception.class)
	public void testFindDescricaoOrTipoContaOrAtivoByUsuarioNull() {
		service.buscarDescricaoOuTipoContaOuAtivoPorUsuario(null, null, null, null);
	}
}