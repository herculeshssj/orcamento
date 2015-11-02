/***
  
  	Copyright (c) 2012 - 2016 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.RelatorioColuna;
import br.com.hslife.orcamento.entity.RelatorioCustomizado;
import br.com.hslife.orcamento.entity.RelatorioParametro;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoDado;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IRelatorioCustomizado;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class RelatorioCustomizadoServiceTest extends AbstractTestServices {
	
	@Autowired
	private IUsuario usuarioService;
	
	@Autowired
	private IRelatorioCustomizado relatorioCustomizadoService;
	
	private RelatorioCustomizado relatorio;
	
	@Before
	public void initializeTestEnvironment() throws BusinessException {
		Usuario usuario = EntityInitializerFactory.createUsuario();
		usuarioService.cadastrar(usuario);
		
		relatorio = EntityInitializerFactory.createRelatorioCustomizado(usuario);
	}
	
	@Test
	public void testCadastrar() throws BusinessException {
		relatorioCustomizadoService.cadastrar(relatorio);
		
		assertNotNull(relatorio.getId());
	}
	
	@Test
	public void testAlterar() throws BusinessException {
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
	public void testExcluir() throws BusinessException {
		relatorioCustomizadoService.cadastrar(relatorio);
		
		// Testa o método em questão
		relatorioCustomizadoService.excluir(relatorio);
		
		RelatorioCustomizado relatorioTest = relatorioCustomizadoService.buscarPorID(relatorio.getId());
		assertNull(relatorioTest);
	}
	
	@Test
	public void testBuscarPorID() throws BusinessException {
		relatorioCustomizadoService.cadastrar(relatorio);
		
		// Testa o método em questão
		RelatorioCustomizado relatorioTest = relatorioCustomizadoService.buscarPorID(relatorio.getId());
		
		assertEquals(relatorio.getId(), relatorioTest.getId());
	}
	
	@Test
	public void testValidar() throws BusinessException {
		// Verifica se a entidade está consistente para ser persistida
		relatorioCustomizadoService.validar(relatorio);
		
		relatorioCustomizadoService.cadastrar(relatorio);
		
		RelatorioCustomizado relatorioTest = EntityInitializerFactory.createRelatorioCustomizado(relatorio.getUsuario());
		relatorioCustomizadoService.validar(relatorioTest);
	}
	
	@Test
	public void testBuscarNome() throws BusinessException {
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
}