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

import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Meta;
import br.com.hslife.orcamento.mock.EntityPersistenceMock;

public class MetaRepositoryTest extends AbstractTestRepositories {
	
	private Meta entity;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private MetaRepository repository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private MoedaRepository moedaRepository;
	
	@Before
	public void initializeEntities() {
		// Seta o sessionFactory nos repositórios
		repository.setSessionFactory(sessionFactory);
		usuarioRepository.setSessionFactory(sessionFactory);
		moedaRepository.setSessionFactory(sessionFactory);
		
		// Inicializa as entidades
		entity = EntityPersistenceMock.mockMeta();
		
		// Salva as entidades pertinentes antes de iniciar os testes
		usuarioRepository.save(entity.getUsuario());
		moedaRepository.save(entity.getMoeda());
		repository.save(entity);
	}
	
	@Test
	public void testFindAllDescricaoAndAtivoByUsuario() {
		List<Meta> listaMeta = repository.findAllDescricaoAndAtivoByUsuario("", true, entity.getUsuario());
		assertEquals(1, listaMeta.size());
		
		listaMeta = repository.findAllDescricaoAndAtivoByUsuario("", false, entity.getUsuario());
		assertEquals(0, listaMeta.size());
		
		listaMeta = repository.findAllDescricaoAndAtivoByUsuario(entity.getDescricao(), true, entity.getUsuario());
		assertEquals(1, listaMeta.size());
		
		listaMeta = repository.findAllDescricaoAndAtivoByUsuario(null, true, entity.getUsuario());
		assertEquals(1, listaMeta.size());
	}
}