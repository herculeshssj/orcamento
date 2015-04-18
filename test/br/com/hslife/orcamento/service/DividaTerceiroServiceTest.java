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

package br.com.hslife.orcamento.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.DividaTerceiro;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.ModeloDocumento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.RegraImportacao;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoPessoa;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IDividaTerceiro;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.facade.IRegraImportacao;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class DividaTerceiroServiceTest extends AbstractTestServices {
	
	private Usuario usuario = new Usuario();
	private Favorecido favorecido = new Favorecido();
	private Moeda moeda = new Moeda();
	private DividaTerceiro dividaTerceiro = new DividaTerceiro();
	
	@Autowired
	private IUsuario usuarioService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IDividaTerceiro dividaTerceiroService;
	
	@Before
	public void initializeTestEnvironment() throws BusinessException {
		usuario = EntityInitializerFactory.initializeUsuario();
		usuarioService.cadastrar(usuario);
		
		moeda = EntityInitializerFactory.initializeMoeda(usuario);
		moedaService.cadastrar(moeda);
		
		favorecido = EntityInitializerFactory.initializeFavorecido(usuario, TipoPessoa.FISICA, false);
		favorecidoService.cadastrar(favorecido);
		
		dividaTerceiro = EntityInitializerFactory.initializeDividaTerceiro(usuario, favorecido, moeda);
	}
	
	@Test
	public void testCadastrar() throws BusinessException {
		fail("Não implementado!");
		/*
		// Realiza o cadastro da regra
		regraImportacaoService.cadastrar(regra);
		
		// Verifica se a regra foi cadastrada com sucesso
		assertNotNull(regra.getId());
		*/
	}
	
	@Test
	public void testAlterar() throws BusinessException {
		fail("Não implementado!");
		/*
		// Realiza o cadastro da regra
		regraImportacaoService.cadastrar(regra);
		
		// Altera as informações da regra
		regra.setIdCategoria(10l);
		regra.setIdFavorecido(20l);
		regra.setIdMeioPagamento(30l);
		regra.setTexto("texto a pesquisar");
		
		// Altera a regra
		regraImportacaoService.alterar(regra);
		
		// Verifica se a regra foi alterada com sucesso
		RegraImportacao regraTest = regraImportacaoService.buscarPorID(regra.getId());
		
		assertEquals(regra.getTexto(), regraTest.getTexto());
		assertEquals(regra.getIdCategoria(), regraTest.getIdCategoria());
		assertEquals(regra.getIdFavorecido(), regraTest.getIdFavorecido());
		assertEquals(regra.getIdMeioPagamento(), regraTest.getIdMeioPagamento());
		*/
	}
	
	@Test
	public void testExcluir() throws BusinessException {
		fail("Não implementado!");
		/*
		// Realiza o cadastro da regra
		regraImportacaoService.cadastrar(regra);
		
		// Exclui a regra
		regraImportacaoService.excluir(regra);
		
		// Verifica se a regra foi excluída
		RegraImportacao regraTest = regraImportacaoService.buscarPorID(regra.getId());
		assertNull(regraTest);
		*/
	}
	
	@Test
	public void testBuscarPorID() throws BusinessException {
		fail("Não implementado!");
		/*
		// Realiza o cadastro da regra
		regraImportacaoService.cadastrar(regra);
		
		// Verifica se a regra existe na base
		RegraImportacao regraTest = regraImportacaoService.buscarPorID(regra.getId());
		
		assertNotNull(regraTest);
		*/
	}
	
	@Test
	public void testValidar() throws BusinessException {
		fail("Não implementado!");
		/*
		// Verifica se a entidade está consistente para ser persistida
		regraImportacaoService.validar(regra);
		
		// Cadastra a regra
		regraImportacaoService.cadastrar(regra);
		
		RegraImportacao regraTest = EntityInitializerFactory.initializeRegraImportacao(conta);
		regraImportacaoService.validar(regraTest);
		*/
	}
	
	@Test
	public void testBuscarFavorecido() {
		fail("Não implementado!");
	}
	
	@Test
	public void testBuscarTipoCategoria() {
		fail("Não implementado!");
	}
	
	@Test
	public void testBuscarStatusDivida() {
		fail("Não implementado!");
	}
	
	@Test
	public void testBuscarPorUsuario() {
		fail("Não implementado!");
	}
	
	/*
	@Test
	public void testBuscarDescricao() throws BusinessException {
		modeloDocumentoService.cadastrar(modelo);
		
		List<ModeloDocumento> listaModelos = modeloDocumentoService.buscarDescricaoOuAtivoPorUsuario("teste", null, usuario);
		
		if (listaModelos == null || listaModelos.isEmpty()) {
			fail("Lista vazia.");
		} else {
			if (!listaModelos.contains(modelo)) {
				fail("Objeto não encontrado.");
			}
		}
	}
	
	@Test
	public void testBuscarAtivo() throws BusinessException {
		modeloDocumentoService.cadastrar(modelo);
		
		List<ModeloDocumento> listaModelos = modeloDocumentoService.buscarDescricaoOuAtivoPorUsuario(null, true, usuario);
		
		if (listaModelos == null || listaModelos.isEmpty()) {
			fail("Lista vazia.");
		} else {
			for (ModeloDocumento modeloTest : listaModelos) {
				assertTrue(modeloTest.isAtivo());
			}
		}
	}
	
	@Test
	public void testBuscarPorUsuario() throws BusinessException {
		modeloDocumentoService.cadastrar(modelo);
		
		List<ModeloDocumento> listaModelos = modeloDocumentoService.buscarDescricaoOuAtivoPorUsuario(null, null, usuario);
		
		if (listaModelos == null || listaModelos.isEmpty()) {
			fail("Lista vazia.");
		} else {
			for (ModeloDocumento modeloTest : listaModelos) {
				assertEquals(usuario, modeloTest.getUsuario());
			}
		}
	}
	 */
}