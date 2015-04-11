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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

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
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ModeloDocumentoRepository modeloDocumentoRepository;
	
	@Before
	public void initializeEntities() {
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
		fail("Não implementado!");
	}
	
	@Test
	public void testFindAtivo() {
		fail("Não implementado!");
	}
	
	@Test
	public void testFindByUsuario() {
		fail("Não implementado!");
	}
}