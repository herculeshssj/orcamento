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
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.DividaTerceiro;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusDivida;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoPessoa;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IDividaTerceiro;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IMoeda;
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
		dividaTerceiroService.cadastrar(dividaTerceiro);
		
		assertNotNull(dividaTerceiro.getId());
		
		for (int i = 0; i < dividaTerceiro.getPagamentos().size(); i++) {
			assertNotNull(dividaTerceiro.getPagamentos().get(i).getId());
		}
	}
	
	@Test
	public void testAlterar() throws BusinessException {
		dividaTerceiroService.cadastrar(dividaTerceiro);
		
		dividaTerceiro.setDataNegociacao(Calendar.getInstance().getTime());
		dividaTerceiro.setJustificativa("Justificativa da dívida de teste alterado");
		dividaTerceiro.setTermoDivida("Termo da dívida de teste alterado");
		dividaTerceiro.setTermoQuitacao("Termo de quitação da dívida de teste alterado");
		dividaTerceiro.setTipoCategoria(TipoCategoria.DEBITO);
		dividaTerceiro.setStatusDivida(StatusDivida.ENCERRADO);
		dividaTerceiro.setValorDivida(2000);
		
		for (int i = 0; i < dividaTerceiro.getPagamentos().size(); i++) {
			dividaTerceiro.getPagamentos().get(i).setComprovantePagamento("Comprovante de pagamento da dívida de teste alterado " + i);
			dividaTerceiro.getPagamentos().get(i).setDataPagamento(Calendar.getInstance().getTime());
			dividaTerceiro.getPagamentos().get(i).setValorPago(200);
		}
		
		dividaTerceiroService.alterar(dividaTerceiro);
		
		DividaTerceiro dividaTest = dividaTerceiroService.buscarPorID(dividaTerceiro.getId());
		
		assertEquals(dividaTerceiro.getDataNegociacao(), dividaTest.getDataNegociacao());
		assertEquals(dividaTerceiro.getJustificativa(), dividaTest.getJustificativa());
		assertEquals(dividaTerceiro.getTermoDivida(), dividaTest.getTermoDivida());
		assertEquals(dividaTerceiro.getTermoQuitacao(), dividaTest.getTermoQuitacao());
		assertEquals(dividaTerceiro.getTipoCategoria(), dividaTest.getTipoCategoria());
		assertEquals(dividaTerceiro.getStatusDivida(), dividaTest.getStatusDivida());
		
		for (int i = 0; i < dividaTerceiro.getPagamentos().size(); i++) {
			assertEquals(dividaTerceiro.getPagamentos().get(i).getComprovantePagamento(), dividaTest.getPagamentos().get(i).getComprovantePagamento());
			assertEquals(dividaTerceiro.getPagamentos().get(i).getDataPagamento(), dividaTest.getPagamentos().get(i).getDataPagamento());
			assertEquals(dividaTerceiro.getPagamentos().get(i).getValorPago(), dividaTest.getPagamentos().get(i).getValorPago(), 0);
		}
	}
	
	@Test
	public void testExcluir() throws BusinessException {
		dividaTerceiroService.cadastrar(dividaTerceiro);
		
		dividaTerceiroService.excluir(dividaTerceiro);
		
		DividaTerceiro dividaTest = dividaTerceiroService.buscarPorID(dividaTerceiro.getId());
		assertNull(dividaTest);
	}
	
	@Test
	public void testBuscarPorID() throws BusinessException {
		dividaTerceiroService.cadastrar(dividaTerceiro);
		
		DividaTerceiro dividaTest = dividaTerceiroService.buscarPorID(dividaTerceiro.getId());
		assertEquals(dividaTerceiro.getId(), dividaTest.getId());
		
		for (int i = 0; i < dividaTerceiro.getPagamentos().size(); i++) {
			assertEquals(dividaTerceiro.getPagamentos().get(i).getId(), dividaTest.getPagamentos().get(i).getId());
		}
	}
	
	@Test
	public void testValidar() throws BusinessException {
		dividaTerceiroService.validar(dividaTerceiro);
	}
	
	@Test
	public void testBuscarFavorecido() throws BusinessException {
		dividaTerceiroService.cadastrar(dividaTerceiro);
		
		List<DividaTerceiro> listaDividas = dividaTerceiroService.buscarFavorecidoOuTipoCategoriaOuStatusDividaPorUsuario(favorecido, null, null, usuario);
		
		if (listaDividas == null || listaDividas.isEmpty()) {
			fail("Lista vazia.");
		} else {
			if (!listaDividas.contains(dividaTerceiro)) {
				fail("Objeto não encontrado.");
			}
		}
	}
	
	@Test
	public void testBuscarTipoCategoria() throws BusinessException {
		dividaTerceiroService.cadastrar(dividaTerceiro);
		
		List<DividaTerceiro> listaDividas = dividaTerceiroService.buscarFavorecidoOuTipoCategoriaOuStatusDividaPorUsuario(null, dividaTerceiro.getTipoCategoria(), null, usuario);
		
		if (listaDividas == null || listaDividas.isEmpty()) {
			fail("Lista vazia.");
		} else {
			if (!listaDividas.contains(dividaTerceiro)) {
				fail("Objeto não encontrado.");
			}
		}
	}
	
	@Test
	public void testBuscarStatusDivida() throws BusinessException {
		dividaTerceiroService.cadastrar(dividaTerceiro);
		
		List<DividaTerceiro> listaDividas = dividaTerceiroService.buscarFavorecidoOuTipoCategoriaOuStatusDividaPorUsuario(null, null, dividaTerceiro.getStatusDivida(), usuario);
		
		if (listaDividas == null || listaDividas.isEmpty()) {
			fail("Lista vazia.");
		} else {
			if (!listaDividas.contains(dividaTerceiro)) {
				fail("Objeto não encontrado.");
			}
		}
	}
	
	@Test
	public void testBuscarPorUsuario() throws BusinessException {
		dividaTerceiroService.cadastrar(dividaTerceiro);
		
		List<DividaTerceiro> listaDividas = dividaTerceiroService.buscarFavorecidoOuTipoCategoriaOuStatusDividaPorUsuario(null, null, null, usuario);
		
		if (listaDividas == null || listaDividas.isEmpty()) {
			fail("Lista vazia.");
		} else {
			if (!listaDividas.contains(dividaTerceiro)) {
				fail("Objeto não encontrado.");
			}
		}
	}
}