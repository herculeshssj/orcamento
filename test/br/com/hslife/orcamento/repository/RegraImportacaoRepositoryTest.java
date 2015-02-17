/***
  
  	Copyright (c) 2012 - 2015 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou 

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como 

    publicada pela Fundação do Software Livre (FSF); na versão 2.1 da 

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, 

    mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a 
    
    qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública 
    
    Geral Menor GNU em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob o 

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento ou escreva 
    
    para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor, 
    
    Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor entre  

    em contato pelo e-mail herculeshssj@gmail.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

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
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private MoedaRepository moedaRepository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private RegraImportacaoRepository regraImportacaoRepository;
	
	@Before
	public void initializeEntities() {
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
}