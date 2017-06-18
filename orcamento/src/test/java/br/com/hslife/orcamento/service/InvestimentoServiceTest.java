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

package br.com.hslife.orcamento.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Banco;
import br.com.hslife.orcamento.entity.Investimento;
import br.com.hslife.orcamento.entity.MovimentacaoInvestimento;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoInvestimento;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.facade.IBanco;
import br.com.hslife.orcamento.facade.IInvestimento;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class InvestimentoServiceTest extends AbstractTestServices {
	
	private Usuario usuario;
	private Banco banco;
	private Investimento investimento;
	
	@Autowired
	private IBanco bancoService;
	
	@Autowired
	private IUsuario usuarioService;
	
	@Autowired
	private IInvestimento investimentoService;
	
	@Before
	public void initializeTestEnvironment() throws ApplicationException {
		usuario = EntityInitializerFactory.createUsuario();
		banco = EntityInitializerFactory.createBanco(usuario);
		
		usuarioService.cadastrar(usuario);
		bancoService.cadastrar(banco);
		
		investimento = EntityInitializerFactory.createInvestimento(banco, usuario);
	}
	
	@Test
	public void testBuscarPorID() {
		investimentoService.cadastrar(investimento);
		
		Investimento investimentoTest = investimentoService.buscarPorID(investimento.getId());
		assertEquals(investimento.getId(), investimentoTest.getId());
		
		List<MovimentacaoInvestimento> listaMovimentacao = new LinkedList<>(investimento.getMovimentacoesInvestimento());
		List<MovimentacaoInvestimento> listaMovimentacaoTest = new LinkedList<>(investimentoTest.getMovimentacoesInvestimento());
		
		for (int i = 0; i < listaMovimentacao.size(); i++) {
			assertEquals(listaMovimentacao.get(i).getId(), listaMovimentacaoTest.get(i).getId());
		}
		
		assertEquals(investimento.getResumosInvestimento().get(0).getId(), investimentoTest.getResumosInvestimento().get(0).getId());
	}
	
	@Test
	public void testDelete() {
		investimentoService.cadastrar(investimento);
		
		investimentoService.excluir(investimento);
		
		Investimento investimentoTest = investimentoService.buscarPorID(investimento.getId());
		assertNull(investimentoTest);
	}
	
	@Test
	public void testUpdate() {
		investimentoService.cadastrar(investimento);
		
		investimento.setCnpj("19911916000121");
		investimento.setDescricao("Investimento de teste alterado");
		investimento.setInicioInvestimento(new Date());
		investimento.setTerminoInvestimento(new Date());
		investimento.setTipoInvestimento(TipoInvestimento.ACOES);
		
		investimento.movimentarInvestimento(TipoLancamento.RECEITA, "Movimentação de receita alterado", new Date(), 1000);
		investimento.movimentarInvestimento(TipoLancamento.DESPESA, "Movimentação de despesa alterado", new Date(), 1000);
		
		investimento.criaResumoInvestimento(12, 2016);
		
		investimentoService.alterar(investimento);
		
		Investimento investimentoTest = investimentoService.buscarPorID(investimento.getId());
		
		assertEquals(investimento.getCnpj(), investimentoTest.getCnpj());
		assertEquals(investimento.getDescricao(), investimentoTest.getDescricao());
		assertEquals(investimento.getInicioInvestimento(), investimentoTest.getInicioInvestimento());
		assertEquals(investimento.getTerminoInvestimento(), investimentoTest.getTerminoInvestimento());
		assertEquals(investimento.getTipoInvestimento(), investimentoTest.getTipoInvestimento());
		
		List<MovimentacaoInvestimento> listaMovimentacao = new LinkedList<>(investimento.getMovimentacoesInvestimento());
		List<MovimentacaoInvestimento> listaMovimentacaoTest = new LinkedList<>(investimentoTest.getMovimentacoesInvestimento());
		
		for (int i = 0; i < listaMovimentacao.size(); i++) {
			assertEquals(listaMovimentacao.get(i).getCompensacaoImpostoRenda(), listaMovimentacaoTest.get(i).getCompensacaoImpostoRenda(),0);
			assertEquals(listaMovimentacao.get(i).getCotas(), listaMovimentacaoTest.get(i).getCotas(),0);
			assertEquals(listaMovimentacao.get(i).getData(), listaMovimentacaoTest.get(i).getData());
			assertEquals(listaMovimentacao.get(i).getDocumento(), listaMovimentacaoTest.get(i).getDocumento());
			assertEquals(listaMovimentacao.get(i).getHistorico(), listaMovimentacaoTest.get(i).getHistorico());
			assertEquals(listaMovimentacao.get(i).getImpostoRenda(), listaMovimentacaoTest.get(i).getImpostoRenda(),0);
			assertEquals(listaMovimentacao.get(i).getIof(), listaMovimentacaoTest.get(i).getIof(),0);
			assertEquals(listaMovimentacao.get(i).getSaldoCotas(), listaMovimentacaoTest.get(i).getSaldoCotas(),0);
			assertEquals(listaMovimentacao.get(i).getTipoLancamento(), listaMovimentacaoTest.get(i).getTipoLancamento());
			assertEquals(listaMovimentacao.get(i).getValor(), listaMovimentacaoTest.get(i).getValor(),0);
			assertEquals(listaMovimentacao.get(i).getValorCota(), listaMovimentacaoTest.get(i).getValorCota(),0);
		}
		
		for (int i = 0; i < investimento.getResumosInvestimento().size(); i++) {
			assertEquals(investimento.getResumosInvestimento().get(i).getAno(), investimentoTest.getResumosInvestimento().get(i).getAno(),0);
			assertEquals(investimento.getResumosInvestimento().get(i).getAplicacao(), investimentoTest.getResumosInvestimento().get(i).getAplicacao(),0);
			assertEquals(investimento.getResumosInvestimento().get(i).getImpostoRenda(), investimentoTest.getResumosInvestimento().get(i).getImpostoRenda(),0);
			assertEquals(investimento.getResumosInvestimento().get(i).getIof(), investimentoTest.getResumosInvestimento().get(i).getIof(),0);
			assertEquals(investimento.getResumosInvestimento().get(i).getMes(), investimentoTest.getResumosInvestimento().get(i).getMes());
			assertEquals(investimento.getResumosInvestimento().get(i).getMes(), investimentoTest.getResumosInvestimento().get(i).getMes());
			assertEquals(investimento.getResumosInvestimento().get(i).getRendimentoBruto(), investimentoTest.getResumosInvestimento().get(i).getRendimentoBruto(),0);
			assertEquals(investimento.getResumosInvestimento().get(i).getRendimentoLiquido(), investimentoTest.getResumosInvestimento().get(i).getRendimentoLiquido(),0);
			assertEquals(investimento.getResumosInvestimento().get(i).getResgate(), investimentoTest.getResumosInvestimento().get(i).getResgate(),0);
		}
	}
	
	@Test
	public void testSave() {
		investimentoService.cadastrar(investimento);
		
		assertNotNull(investimento.getId()); 
		
		List<MovimentacaoInvestimento> listaMovimentacao = new LinkedList<>(investimento.getMovimentacoesInvestimento());
		
		for (int i = 0; i < listaMovimentacao.size(); i++) {
			assertNotNull(listaMovimentacao.get(i).getId());
		}
		
		for (int i = 0; i < investimento.getResumosInvestimento().size(); i++) {
			assertNotNull(investimento.getResumosInvestimento().get(i).getId());
		}
	}
	
	@Test
	public void testBuscarPorUsuario() {
		investimentoService.cadastrar(investimento);
		
		List<Investimento> investimentoTest = investimentoService.buscarPorUsuario(usuario);
		
		assertNotNull(investimentoTest);
		
		if (investimentoTest.isEmpty()) {
			fail("List vazia!");
		}
		
		if (!investimentoTest.contains(investimento)) {
			fail("Objeto não encontrado!");
		}
	}
	
	@Test
	public void testBuscarPorTipoInvestimentoEUsuario() {
		investimentoService.cadastrar(investimento);
		
		List<Investimento> investimentoTest = investimentoService.buscarPorTipoInvestimentoEUsuario(investimento.getTipoInvestimento(), investimento.getUsuario());
		
		assertNotNull(investimentoTest);
		
		if (investimentoTest.isEmpty()) {
			fail("List vazia!");
		}
		
		if (!investimentoTest.contains(investimento)) {
			fail("Objeto não encontrado!");
		}
	}
}