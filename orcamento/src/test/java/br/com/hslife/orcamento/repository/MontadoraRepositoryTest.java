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

import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Montadora;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class MontadoraRepositoryTest extends AbstractTestRepositories {
	
	private Montadora entity;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private MontadoraRepository repository;
	
	@Before
	public void initializeEntities() {
		// Seta o sessionFactory nos repositórios
		repository.setSessionFactory(sessionFactory);
		
		// Inicializa as entidades
		entity = EntityInitializerFactory.createMontadora();
		
		// Salva as entidades pertinentes antes de iniciar os testes
	}
	
	@Test
	public void testFindById() {
		repository.save(entity);
		
		// Testa o método em questão
		Montadora entityTest = repository.findById(entity.getId());
		assertEquals(entity.getId(), entityTest.getId());
	}
	
	@Test
	public void testDelete() {
		repository.save(entity);
		
		// Testa o método em questão
		repository.delete(entity);
		
		Montadora entityTest = repository.findById(entity.getId());
		assertNull(entityTest);
	}
	
	@Test
	public void testUpdate() {
		repository.save(entity);
		
		// Altera as informações da entidade
		entity.setDescricao("Nova montadora");
		
		// Testa o método em questão
		repository.update(entity);
		
		Montadora entityTest = repository.findById(entity.getId());
		assertEquals(entity.getDescricao(), entityTest.getDescricao());
	}
	
	@Test
	public void testSave() {
		repository.save(entity);
		
		// Testa o método em questão
		assertNotNull(entity.getId());
	}
	
	@Test
	public void testFindByDescricao() {
		for (int i = 0; i < 10; i++) {
			entity = EntityInitializerFactory.createMontadora();
			entity.setDescricao(entity.getDescricao() + " - " + i);
			repository.save(entity);
		}
		
		// Testa o método em questão
		List<Montadora> listEntity = repository.findbyDescricao("teste");
		assertEquals(10, listEntity.size());
		
		listEntity = repository.findbyDescricao("1");
		assertEquals(1, listEntity.size());
	}
}
