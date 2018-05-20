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
package br.com.hslife.orcamento.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Seguro;
import br.com.hslife.orcamento.enumeration.EntityPersistenceEnum;
import br.com.hslife.orcamento.mock.EntityPersistenceMock;

public class SeguroRepositoryTest extends AbstractTestRepositories {
	
	private Seguro entity;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private SeguroRepository repository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private MoedaRepository moedaRepository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private LancamentoPeriodicoRepository lancamentoPeriodicoRepository;
	
	@Before
	public void initializeEntities() {
		// Seta o sessionFactory nos repositórios
		repository.setSessionFactory(sessionFactory);
		usuarioRepository.setSessionFactory(sessionFactory);
		moedaRepository.setSessionFactory(sessionFactory);
		contaRepository.setSessionFactory(sessionFactory);
		lancamentoPeriodicoRepository.setSessionFactory(sessionFactory);
		
		// Inicializa as entidades
		EntityPersistenceMock epm = new EntityPersistenceMock()
				.criarUsuario()
				.comFavorecido(true)
				.comMoedaPadrao()
				.comContaCorrente()
				.ePossuiSeguro();
		entity = (Seguro)epm.get(EntityPersistenceEnum.SEGURO);
		
		// Salva as entidades pertinentes antes de iniciar os testes
		usuarioRepository.save(entity.getConta().getUsuario());
		moedaRepository.save(entity.getConta().getMoeda());
		contaRepository.save(entity.getConta());
	}
	
	//@Test
	public void testFindById() {
		// FIXME corrigir teste unitário
		entity.gerarDespesaFixa();
		lancamentoPeriodicoRepository.save(entity.getLancamentoPeriodico());
		repository.save(entity);
		
		// Testa o método em questão
		Seguro entityTest = repository.findById(entity.getId());
		assertEquals(entity.getId(), entityTest.getId());
	}
	
	@Test
	public void testDelete() {
		entity.gerarDespesaFixa();
		lancamentoPeriodicoRepository.save(entity.getLancamentoPeriodico());
		repository.save(entity);
		
		// Testa o método em questão
		repository.delete(entity);
		
		assertNull(repository.findById(entity.getId()));
	}
	
	//@Test
	public void testUpdate() {
		// FIXME corrigir teste unitário
		entity.gerarDespesaFixa();
		lancamentoPeriodicoRepository.save(entity.getLancamentoPeriodico());
		repository.save(entity);
		
		// Altera as informações da entidade
		entity.setDescricao("Novo seguro de teste");
		
		// Testa o método em questão
		repository.update(entity);
		
		Seguro entityTest = repository.findById(entity.getId());
		assertEquals(entity.getDescricao(), entityTest.getDescricao());
	}
	
	@Test
	public void testSave() {
		entity.gerarDespesaFixa();
		lancamentoPeriodicoRepository.save(entity.getLancamentoPeriodico());
		repository.save(entity);
		
		// Testa o método em questão
		assertNotNull(entity.getId());
	}
	
	
}
