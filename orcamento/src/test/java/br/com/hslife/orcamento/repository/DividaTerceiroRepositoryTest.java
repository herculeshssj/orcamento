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

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth

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
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.List;

import org.hibernate.SessionFactory;
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
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class DividaTerceiroRepositoryTest extends AbstractTestRepositories {
	
	private Usuario usuario = new Usuario();
	private Favorecido favorecido = new Favorecido();
	private Moeda moeda = new Moeda();
	private DividaTerceiro dividaTerceiro = new DividaTerceiro();
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private MoedaRepository moedaRepository;
	
	@Autowired
	private FavorecidoRepository favorecidoRepository;
	
	@Autowired
	private DividaTerceiroRepository dividaTerceiroRepository;
	
	@SuppressWarnings("deprecation")
	@Before
	public void initializeEntities() {
		usuarioRepository.setSessionFactory(sessionFactory);
		moedaRepository.setSessionFactory(sessionFactory);
		favorecidoRepository.setSessionFactory(sessionFactory);
		dividaTerceiroRepository.setSessionFactory(sessionFactory);
		
		usuario = EntityInitializerFactory.initializeUsuario();
		usuarioRepository.save(usuario);
		
		moeda = EntityInitializerFactory.initializeMoeda(usuario);
		moedaRepository.save(moeda);
		
		favorecido = EntityInitializerFactory.initializeFavorecido(usuario, TipoPessoa.FISICA, false);
		favorecidoRepository.save(favorecido);
		
		dividaTerceiro = EntityInitializerFactory.initializeDividaTerceiro(usuario, favorecido, moeda);
	}
	
	@Test
	public void testFindById() {
		dividaTerceiroRepository.save(dividaTerceiro);
		
		DividaTerceiro dividaTest = dividaTerceiroRepository.findById(dividaTerceiro.getId());
		assertEquals(dividaTerceiro.getId(), dividaTest.getId());
		
		for (int i = 0; i < dividaTerceiro.getPagamentos().size(); i++) {
			assertEquals(dividaTerceiro.getPagamentos().get(i).getId(), dividaTest.getPagamentos().get(i).getId());
		}
	}
	
	@Test
	public void testDelete() {
		dividaTerceiroRepository.save(dividaTerceiro);
		
		dividaTerceiroRepository.delete(dividaTerceiro);
		
		DividaTerceiro dividaTest = dividaTerceiroRepository.findById(dividaTerceiro.getId());
		assertNull(dividaTest);
	}
	
	@Test
	public void testUpdate() {
		dividaTerceiroRepository.save(dividaTerceiro);
		
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
		
		dividaTerceiroRepository.update(dividaTerceiro);
		
		DividaTerceiro dividaTest = dividaTerceiroRepository.findById(dividaTerceiro.getId());
		
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
	public void testSave() {
		dividaTerceiroRepository.save(dividaTerceiro);
		
		assertNotNull(dividaTerceiro.getId());
		
		for (int i = 0; i < dividaTerceiro.getPagamentos().size(); i++) {
			assertNotNull(dividaTerceiro.getPagamentos().get(i).getId());
		}
	}
	
	@Test
	public void testFindFavorecido() {
		dividaTerceiroRepository.save(dividaTerceiro);
		
		List<DividaTerceiro> listaDividas = dividaTerceiroRepository.findFavorecidoOrTipoCategoriaOrStatusDividaByUsuario(favorecido, null, null, usuario);
		
		if (listaDividas == null || listaDividas.isEmpty()) {
			fail("Lista vazia.");
		} else {
			if (!listaDividas.contains(dividaTerceiro)) {
				fail("Objeto não encontrado.");
			}
		}
	}
	
	@Test
	public void testFindTipoCategoria() {
		dividaTerceiroRepository.save(dividaTerceiro);
		
		List<DividaTerceiro> listaDividas = dividaTerceiroRepository.findFavorecidoOrTipoCategoriaOrStatusDividaByUsuario(null, dividaTerceiro.getTipoCategoria(), null, usuario);
		
		if (listaDividas == null || listaDividas.isEmpty()) {
			fail("Lista vazia.");
		} else {
			if (!listaDividas.contains(dividaTerceiro)) {
				fail("Objeto não encontrado.");
			}
		}
	}
	
	@Test
	public void testFindStatusDivida() {
		dividaTerceiroRepository.save(dividaTerceiro);
		
		List<DividaTerceiro> listaDividas = dividaTerceiroRepository.findFavorecidoOrTipoCategoriaOrStatusDividaByUsuario(null, null, dividaTerceiro.getStatusDivida(), usuario);
		
		if (listaDividas == null || listaDividas.isEmpty()) {
			fail("Lista vazia.");
		} else {
			if (!listaDividas.contains(dividaTerceiro)) {
				fail("Objeto não encontrado.");
			}
		}
	}
	
	@Test
	public void testFindByUsuario() {
		dividaTerceiroRepository.save(dividaTerceiro);
		
		List<DividaTerceiro> listaDividas = dividaTerceiroRepository.findFavorecidoOrTipoCategoriaOrStatusDividaByUsuario(null, null, null, usuario);
		
		if (listaDividas == null || listaDividas.isEmpty()) {
			fail("Lista vazia.");
		} else {
			if (!listaDividas.contains(dividaTerceiro)) {
				fail("Objeto não encontrado.");
			}
		}
	}
}
