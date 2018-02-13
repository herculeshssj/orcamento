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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.ModeloDocumento;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class ModeloDocumentoRepositoryTest extends AbstractTestRepositories {
	
	private Usuario usuario = new Usuario();
	private ModeloDocumento modelo = new ModeloDocumento();
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ModeloDocumentoRepository modeloDocumentoRepository;
	
	@SuppressWarnings("deprecation")
	@Before
	public void initializeEntities() {
		usuarioRepository.setSessionFactory(this.sessionFactory);
		modeloDocumentoRepository.setSessionFactory(this.sessionFactory);
		
		usuario = EntityInitializerFactory.initializeUsuario();
		usuarioRepository.save(usuario);
		
		modelo = EntityInitializerFactory.initializeModeloDocumento(usuario);
	}
	
	@Test
	public void testFindById() {
		modeloDocumentoRepository.save(modelo);
		
		// Testa o método em questão
		ModeloDocumento modeloTest = modeloDocumentoRepository.findById(modelo.getId());
		assertEquals(modelo.getId(), modeloTest.getId());
	}
	
	@Test
	public void testDelete() {
		modeloDocumentoRepository.save(modelo);
		
		// Testa o método em questão
		modeloDocumentoRepository.delete(modelo);
		
		ModeloDocumento modeloTest = modeloDocumentoRepository.findById(modelo.getId());
		assertNull(modeloTest);
	}
	
	@Test
	public void testUpdate() {
		modeloDocumentoRepository.save(modelo);
		
		modelo.setDescricao("nova descricao de teste");
		modelo.setConteudo("novo conteúdo de teste");
		
		// Testa o método em questão
		modeloDocumentoRepository.update(modelo);
		
		ModeloDocumento modeloTest = modeloDocumentoRepository.findById(modelo.getId());
		
		assertEquals(modelo.getDescricao(), modeloTest.getDescricao());
		assertEquals(modelo.getConteudo(), modeloTest.getConteudo());

	}
	
	@Test
	public void testSave() {
		modeloDocumentoRepository.save(modelo);
		
		// Testa o método em questão
		assertNotNull(modelo.getId());
	}
	
	@Test
	public void testFindDescricao() {
		modeloDocumentoRepository.save(modelo);
		
		List<ModeloDocumento> listaModelos = modeloDocumentoRepository.findDescricaoOrAtivoByUsuario("teste", null, usuario);
		
		if (listaModelos == null || listaModelos.isEmpty()) {
			fail("Lista vazia.");
		} else {
			if (!listaModelos.contains(modelo)) {
				fail("Objeto não encontrado.");
			}
		}
	}
	
	@Test
	public void testFindAtivo() {
		modeloDocumentoRepository.save(modelo);
		
		List<ModeloDocumento> listaModelos = modeloDocumentoRepository.findDescricaoOrAtivoByUsuario(null, true, usuario);
		
		if (listaModelos == null || listaModelos.isEmpty()) {
			fail("Lista vazia.");
		} else {
			for (ModeloDocumento modeloTest : listaModelos) {
				assertTrue(modeloTest.isAtivo());
			}
		}
	}
	
	@Test
	public void testFindByUsuario() {
		modeloDocumentoRepository.save(modelo);
		
		List<ModeloDocumento> listaModelos = modeloDocumentoRepository.findDescricaoOrAtivoByUsuario(null, null, usuario);
		
		if (listaModelos == null || listaModelos.isEmpty()) {
			fail("Lista vazia.");
		} else {
			for (ModeloDocumento modeloTest : listaModelos) {
				assertEquals(usuario, modeloTest.getUsuario());
			}
		}
	}
}
