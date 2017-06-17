/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a qualquer
    
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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.RelatorioColuna;
import br.com.hslife.orcamento.entity.RelatorioCustomizado;
import br.com.hslife.orcamento.entity.RelatorioParametro;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoDado;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.facade.IRelatorioCustomizado;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class RelatorioCustomizadoServiceTest extends AbstractTestServices {
	
	@Autowired
	private IUsuario usuarioService;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private ILancamentoConta lancamentoContaService;
	
	@Autowired
	private IRelatorioCustomizado relatorioCustomizadoService;
	
	private RelatorioCustomizado relatorio;
	private Usuario usuario;
	
	@Before
	public void initializeTestEnvironment() throws ApplicationException {
		usuario = EntityInitializerFactory.createUsuario();
		usuarioService.cadastrar(usuario);
		
		relatorio = EntityInitializerFactory.createRelatorioCustomizado(usuario);
	}
	
	@Test
	public void testCadastrar() throws ApplicationException {
		relatorioCustomizadoService.cadastrar(relatorio);
		
		assertNotNull(relatorio.getId());
	}
	
	//@Test
	public void testAlterar() throws ApplicationException {
		relatorioCustomizadoService.cadastrar(relatorio);
		
		relatorio.setNome("Relatório de teste alterado");
		relatorio.setDescricao("Relatório customizado para testes alterado");
		relatorio.setConsultaSQL("SELECT * FROM lancamentoconta ORDER BY datapagamento DESC");
		
		int i = 0;
		for (RelatorioColuna coluna : relatorio.getColunasRelatorio()) {
			coluna.setFormatar(true);
			coluna.setMascaraFormatacao("99/99/9999");
			coluna.setNomeColuna("colunaalterada" + i);
			coluna.setTextoExibicao("Coluna Alterada " + i);
			coluna.setTipoDado(TipoDado.DATE);
			i++;
		}
		
		i = 0;
		for (RelatorioParametro parametro : relatorio.getParametrosRelatorio()) {
			parametro.setNomeParametro("parametroalterada" + i);
			parametro.setTextoExibicao("Parâmetro alterado " + i);
			parametro.setTipoDado(TipoDado.DATE);
			i++;
		}
		
		// Testa o método em questão
		relatorioCustomizadoService.alterar(relatorio);
		
		RelatorioCustomizado relatorioTest = relatorioCustomizadoService.buscarPorID(relatorio.getId());
		
		assertEquals(relatorio.getNome(), relatorioTest.getNome());
		assertEquals(relatorio.getDescricao(), relatorioTest.getDescricao());
		assertEquals(relatorio.getConsultaSQL(), relatorioTest.getConsultaSQL());
		
		i = 0;
		for (RelatorioColuna coluna : relatorio.getColunasRelatorio()) {
			if (relatorioTest.getColunasRelatorio().contains(coluna)) {
				for (Iterator<RelatorioColuna> iterator = relatorioTest.getColunasRelatorio().iterator(); iterator.hasNext();) {
					RelatorioColuna colunaTest = iterator.next();
					if (colunaTest.equals(coluna)) {
						assertTrue(colunaTest.isFormatar());
						assertEquals(coluna.getMascaraFormatacao(), colunaTest.getMascaraFormatacao());
						assertEquals(coluna.getNomeColuna(), colunaTest.getNomeColuna());
						assertEquals(coluna.getTextoExibicao(), colunaTest.getTextoExibicao());
						assertEquals(coluna.getTipoDado(), colunaTest.getTipoDado());
					}
				}
			} else {
				fail("Coluna não encontrada!");
			}
			i++;
		}
		assertEquals(3, i);
		
		i = 0;
		for (RelatorioParametro parametro : relatorio.getParametrosRelatorio()) {
			if (relatorioTest.getParametrosRelatorio().contains(parametro)) {
				for (Iterator<RelatorioParametro> iterator = relatorioTest.getParametrosRelatorio().iterator(); iterator.hasNext();) {
					RelatorioParametro parametroTest = iterator.next();
					if (parametroTest.equals(parametro)) {
						assertEquals(parametro.getNomeParametro(), parametroTest.getNomeParametro());
						assertEquals(parametro.getTextoExibicao(), parametroTest.getTextoExibicao());
						assertEquals(parametro.getTipoDado(), parametroTest.getTipoDado());
					}
				}
			} else {
				fail("Parâmetro não encontrado!");
			}
			i++;
		}
		assertEquals(3, i);
	}
	
	@Test
	public void testExcluir() throws ApplicationException {
		relatorioCustomizadoService.cadastrar(relatorio);
		
		// Testa o método em questão
		relatorioCustomizadoService.excluir(relatorio);
		
		RelatorioCustomizado relatorioTest = relatorioCustomizadoService.buscarPorID(relatorio.getId());
		assertNull(relatorioTest);
	}
	
	@Test
	public void testBuscarPorID() throws ApplicationException {
		relatorioCustomizadoService.cadastrar(relatorio);
		
		// Testa o método em questão
		RelatorioCustomizado relatorioTest = relatorioCustomizadoService.buscarPorID(relatorio.getId());
		
		assertEquals(relatorio.getId(), relatorioTest.getId());
	}
	
	@Test
	public void testValidar() throws ApplicationException {
		// Verifica se a entidade está consistente para ser persistida
		relatorioCustomizadoService.validar(relatorio);
		
		relatorioCustomizadoService.cadastrar(relatorio);
		
		RelatorioCustomizado relatorioTest = EntityInitializerFactory.createRelatorioCustomizado(relatorio.getUsuario());
		relatorioCustomizadoService.validar(relatorioTest);
	}
	
	@Test
	public void testBuscarNome() throws ApplicationException {
		relatorioCustomizadoService.cadastrar(relatorio);
		
		List<RelatorioCustomizado> listaRelatorios = relatorioCustomizadoService.buscarNomePorUsuario("teste", relatorio.getUsuario());
		
		if (listaRelatorios == null || listaRelatorios.isEmpty()) {
			fail("Lista vazia.");
		} else {
			if (!listaRelatorios.contains(relatorio)) {
				fail("Objeto não encontrado.");
			}
		}
	}
	
	@Test
	public void testProcessarRelatorioCustomizado() throws ApplicationException {
		// Instancia as colunas
		SortedSet<RelatorioColuna> colunas = new TreeSet<>();
		
		// Nome da conta
		RelatorioColuna coluna = new RelatorioColuna();
		coluna.setNomeColuna("nomeConta");
		coluna.setOrdem(1);
		coluna.setTextoExibicao("Conta");
		coluna.setTipoDado(TipoDado.STRING);
		coluna.setFormatar(false);
		colunas.add(coluna);
		
		// tipo de lançamento
		coluna = new RelatorioColuna();
		coluna.setNomeColuna("tipoLancamento");
		coluna.setOrdem(2);
		coluna.setTextoExibicao("Tipo");
		coluna.setTipoDado(TipoDado.STRING);
		coluna.setFormatar(false);
		colunas.add(coluna);
		
		// descrição do lançamento
		coluna = new RelatorioColuna();
		coluna.setNomeColuna("descricao");
		coluna.setOrdem(3);
		coluna.setTextoExibicao("Descrição");
		coluna.setTipoDado(TipoDado.STRING);
		coluna.setFormatar(false);
		colunas.add(coluna);
		
		// Data de pagamento
		coluna = new RelatorioColuna();
		coluna.setNomeColuna("dataPagamento");
		coluna.setOrdem(4);
		coluna.setTextoExibicao("Pago em");
		coluna.setTipoDado(TipoDado.STRING);
		coluna.setFormatar(false);
		colunas.add(coluna);
		
		// valor pago
		coluna = new RelatorioColuna();
		coluna.setNomeColuna("valorPago");
		coluna.setOrdem(5);
		coluna.setTextoExibicao("Valor");
		coluna.setTipoDado(TipoDado.STRING);
		coluna.setFormatar(false);
		colunas.add(coluna);
		
		// Instancia os parâmetros
		Set<RelatorioParametro> parametros = new LinkedHashSet<>();
		RelatorioParametro parametro = new RelatorioParametro();
		
		// Data inicial
		parametro.setNomeParametro("dataInicio");
		parametro.setTextoExibicao("Data Inicial");
		parametro.setTipoDado(TipoDado.DATE);
		parametros.add(parametro);
		
		// Data final
		parametro = new RelatorioParametro();
		parametro.setNomeParametro("dataFim");
		parametro.setTextoExibicao("Data Fim");
		parametro.setTipoDado(TipoDado.DATE);
		parametros.add(parametro);
		
		relatorio = EntityInitializerFactory.createRelatorioCustomizado(usuario, "select c.descricao as nomeConta, l.tipoLancamento, l.descricao, l.dataPagamento, l.valorPago from lancamentoconta l inner join conta c on c.id = l.idConta where l.dataPagamento >= :dataInicio and l.dataPagamento <= :dataFim and c.idUsuario = " + usuario.getId(), colunas, parametros);
		relatorioCustomizadoService.cadastrar(relatorio);
		
		Moeda moeda = EntityInitializerFactory.createMoeda(usuario);
		moedaService.cadastrar(moeda);
		
		Conta conta = EntityInitializerFactory.createConta(usuario, moeda);
		contaService.cadastrar(conta);
		
		for (int i = 1; i <= 5; i++) {
			LancamentoConta lancamento = new LancamentoConta();
			lancamento.setConta(conta);
			lancamento.setMoeda(moeda);
			lancamento.setDataPagamento(new Date());
			lancamento.setDescricao("Lançamento de teste " + i);
			lancamento.setValorPago(i * 100);
			lancamentoContaService.cadastrar(lancamento);
		}
		
		// Seta os parâmetros
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("dataInicio", new Date());
		parameterValues.put("dataFim", new Date());
		
		List<Map<String, Object>> resultado = relatorioCustomizadoService.processarRelatorioCustomizado(relatorio, parameterValues);
		
		//Itera as linhas
		for (Map<String, Object> linhas : resultado) {
			// Itera as colunas
			System.out.println("Nome da conta: " + linhas.get("nomeConta"));
			System.out.println("Tipo: " + linhas.get("tipoLancamento"));
			System.out.println("Descrição: " + linhas.get("descricao"));
			System.out.println("Pago em: " + linhas.get("dataPagamento"));
			System.out.println("Valor: " + linhas.get("valorPago"));
			System.out.println();
		}
	}
}