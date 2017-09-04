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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.ModeloDocumento;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.facade.IModeloDocumento;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class ModeloDocumentoServiceTest extends AbstractTestServices {
	
	private ModeloDocumento modelo = new ModeloDocumento();
	private Usuario usuario = new Usuario();
	
	@Autowired
	private IUsuario usuarioService;
	
	@Autowired
	private IModeloDocumento modeloDocumentoService;
	
	@SuppressWarnings("deprecation")
	@Before
	public void initializeTestEnvironment() throws ApplicationException {
		usuario = EntityInitializerFactory.initializeUsuario();
		usuarioService.cadastrar(usuario);
		
		modelo = EntityInitializerFactory.initializeModeloDocumento(usuario);
	}
	
	@Test
	public void testCadastrar() throws ApplicationException {
		modeloDocumentoService.cadastrar(modelo);
		
		assertNotNull(modelo.getId());
	}
	
	@Test
	public void testAlterar() throws ApplicationException {
		modeloDocumentoService.cadastrar(modelo);
		
		modelo.setDescricao("nova descricao de teste");
		modelo.setConteudo("novo conteúdo de teste");
		
		// Testa o método em questão
		modeloDocumentoService.alterar(modelo);
		
		ModeloDocumento modeloTest = modeloDocumentoService.buscarPorID(modelo.getId());
		
		assertEquals(modelo.getDescricao(), modeloTest.getDescricao());
		assertEquals(modelo.getConteudo(), modeloTest.getConteudo());
	}
	
	@Test
	public void testExcluir() throws ApplicationException {
		modeloDocumentoService.cadastrar(modelo);
		
		// Testa o método em questão
		modeloDocumentoService.excluir(modelo);
		
		ModeloDocumento modeloTest = modeloDocumentoService.buscarPorID(modelo.getId());
		assertNull(modeloTest);
	}
	
	@Test
	public void testBuscarPorID() throws ApplicationException {
		modeloDocumentoService.cadastrar(modelo);
		
		// Testa o método em questão
		ModeloDocumento modeloTest = modeloDocumentoService.buscarPorID(modelo.getId());
		assertEquals(modelo.getId(), modeloTest.getId());
	}
	
	@Test
	public void testValidar() throws ApplicationException {
		// Verifica se a entidade está consistente para ser persistida
		modeloDocumentoService.validar(modelo);
		
		modeloDocumentoService.cadastrar(modelo);
		
		ModeloDocumento modeloTest = EntityInitializerFactory.initializeModeloDocumento(usuario);
		modeloDocumentoService.validar(modeloTest);
	}
	
	@Test
	public void testBuscarDescricao() throws ApplicationException {
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
	public void testBuscarAtivo() throws ApplicationException {
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
	public void testBuscarPorUsuario() throws ApplicationException {
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
}