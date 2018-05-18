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

import br.com.hslife.orcamento.entity.Montadora;
import br.com.hslife.orcamento.entity.Seguro;
import br.com.hslife.orcamento.enumeration.EntityPersistenceEnum;
import br.com.hslife.orcamento.facade.*;
import br.com.hslife.orcamento.mock.EntityPersistenceMock;
import br.com.hslife.orcamento.util.EntityInitializerFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class SeguroServiceTest extends AbstractTestServices {
	
	private Seguro entity;
	
	@Autowired
	private ISeguro service;

	@Autowired
	private IUsuario usuarioService;

	@Autowired
	private IFavorecido favorecidoService;

	@Autowired
	private IMoeda moedaService;

	@Autowired
	private IConta contaService;

	@Autowired
	private ILancamentoPeriodico lancamentoPeriodicoService;

	@Before
	public void initializeEntities() {
		// Inicializa as entidades
		EntityPersistenceMock epm = new EntityPersistenceMock()
				.criarUsuario()
				.comFavorecido(true)
				.comMoedaPadrao()
				.comContaCorrente()
				.ePossuiSeguro();
		entity = (Seguro)epm.get(EntityPersistenceEnum.SEGURO);

		// Salva as entidades pertinentes antes de iniciar os testes
		usuarioService.cadastrar(entity.getConta().getUsuario());
		favorecidoService.cadastrar(entity.getFavorecido());
		moedaService.cadastrar(entity.getMoeda());
		contaService.cadastrar(entity.getConta());
	}
	
	@Test
	public void testFindById() {
		entity.gerarDespesaFixa();
		lancamentoPeriodicoService.cadastrar(entity.getLancamentoPeriodico());
		service.cadastrar(entity);
		
		// Testa o método em questão
		Seguro entityTest = service.buscarPorID(entity.getId());
		assertEquals(entity.getId(), entityTest.getId());
	}
	
	@Test
	public void testDelete() {
		entity.gerarDespesaFixa();
		lancamentoPeriodicoService.cadastrar(entity.getLancamentoPeriodico());
		service.cadastrar(entity);
		
		// Testa o método em questão
		service.excluir(entity);
		
		Seguro entityTest = service.buscarPorID(entity.getId());
		assertNull(entityTest);
	}
	
	@Test
	public void testUpdate() {
		entity.gerarDespesaFixa();
		lancamentoPeriodicoService.cadastrar(entity.getLancamentoPeriodico());
		service.cadastrar(entity);
		
		// Altera as informações da entidade
		entity.setDescricao("Novo seguro de testes");
		
		// Testa o método em questão
		service.alterar(entity);
		
		Seguro entityTest = service.buscarPorID(entity.getId());
		assertEquals(entity.getDescricao(), entityTest.getDescricao());
	}
	
	@Test
	public void testSave() {
		entity.gerarDespesaFixa();
		lancamentoPeriodicoService.cadastrar(entity.getLancamentoPeriodico());
		service.cadastrar(entity);

		// Testa o método em questão
		assertNotNull(entity.getId());
	}
}
