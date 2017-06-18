/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

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

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.RegraImportacao;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class RegraImportacaoRepositoryTest extends AbstractTestRepositories {
	
	private Usuario usuario = new Usuario();
	private Conta conta = new Conta();
	private Moeda moeda = new Moeda();
	private RegraImportacao regra = new RegraImportacao();
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private MoedaRepository moedaRepository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private RegraImportacaoRepository regraImportacaoRepository;
	
	@SuppressWarnings("deprecation")
	@Before
	public void initializeEntities() {
		usuarioRepository.setSessionFactory(sessionFactory);
		moedaRepository.setSessionFactory(sessionFactory);
		contaRepository.setSessionFactory(sessionFactory);
		regraImportacaoRepository.setSessionFactory(sessionFactory);
		
		usuario = EntityInitializerFactory.initializeUsuario();
		usuarioRepository.save(usuario);
		
		moeda = EntityInitializerFactory.initializeMoeda(usuario);
		moedaRepository.save(moeda);
		
		conta = EntityInitializerFactory.initializeConta(usuario, moeda);
		contaRepository.save(conta);
		
		regra = EntityInitializerFactory.initializeRegraImportacao(conta);
	}
	
	@Test
	public void testFindById() {
		regraImportacaoRepository.save(regra);
		
		// Testa o método em questão
		RegraImportacao regraTest = regraImportacaoRepository.findById(regra.getId());
		assertEquals(regra.getId(), regraTest.getId());
	}
	
	@Test
	public void testDelete() {
		regraImportacaoRepository.save(regra);
		
		// Testa o método em questão
		regraImportacaoRepository.delete(regra);
		
		RegraImportacao regraTest = regraImportacaoRepository.findById(regra.getId());
		assertNull(regraTest);
	}
	
	@Test
	public void testUpdate() {
		regraImportacaoRepository.save(regra);
		
		regra.setTexto("texto a pesquisar");
		regra.setIdCategoria(10l);
		regra.setIdFavorecido(20l);
		regra.setIdMeioPagamento(30l);
		
		// Testa o método em questão
		regraImportacaoRepository.update(regra);
		
		RegraImportacao regraTest = regraImportacaoRepository.findById(regra.getId());
		
		assertEquals(regra.getTexto(), regraTest.getTexto());
		assertEquals(regra.getIdCategoria(), regraTest.getIdCategoria());
		assertEquals(regra.getIdFavorecido(), regraTest.getIdFavorecido());
		assertEquals(regra.getIdMeioPagamento(), regraTest.getIdMeioPagamento());
		assertEquals(regra.getConta(), regraTest.getConta());
	}
	
	public void testSave() {
		regraImportacaoRepository.save(regra);
		
		// Testa o método em questão
		assertNotNull(regra.getId());
	}
	
	public void testFindEqualEntity() {
		regraImportacaoRepository.save(regra);
		
		RegraImportacao regraTest = regraImportacaoRepository.findEqualEntity(regra);
	
		assertEquals(regra.getTexto(), regraTest.getTexto());
		assertEquals(regra.getIdCategoria(), regraTest.getIdCategoria());
		assertEquals(regra.getIdFavorecido(), regraTest.getIdFavorecido());
		assertEquals(regra.getIdMeioPagamento(), regraTest.getIdMeioPagamento());
		assertEquals(regra.getConta(), regraTest.getConta());
		
		regra.setTexto("texto a pesquisar");
		regra.setIdCategoria(10l);
		regra.setIdFavorecido(20l);
		regra.setIdMeioPagamento(30l);
		
		// Testa o método em questão
		regraImportacaoRepository.update(regra);
		
		regraTest = regraImportacaoRepository.findById(regra.getId());
		
		assertEquals(regra.getTexto(), regraTest.getTexto());
		assertEquals(regra.getIdCategoria(), regraTest.getIdCategoria());
		assertEquals(regra.getIdFavorecido(), regraTest.getIdFavorecido());
		assertEquals(regra.getIdMeioPagamento(), regraTest.getIdMeioPagamento());
		assertEquals(regra.getConta(), regraTest.getConta());
	}
}