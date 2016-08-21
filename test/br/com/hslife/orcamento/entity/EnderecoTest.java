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

package br.com.hslife.orcamento.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import br.com.hslife.orcamento.exception.ApplicationException;

public class EnderecoTest {
	
	private Endereco entity;

	@Before
	public void setUp() throws Exception {
		
		Usuario usuario = new Usuario();
		usuario.setNome("Usuário de teste");
		
		entity = new Endereco();
		entity.setUsuario(usuario);
		
		entity.setTipoLogradouro("Avenida");
		entity.setLogradouro("Ministro Lafaeyte de Andrade");
		entity.setNumero("1683");
		entity.setComplemento("Bl. 3 Apt. 404");
		entity.setBairro("Marco II");
		entity.setCidade("Nova Iguaçu");
		entity.setEstado("RJ");
		entity.setCep("26261220");
		entity.setDescricao("Residencial");
	}

	@Test
	public void testGetLabel() {
		assertEquals("Residencial: Avenida Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt. 404 - Marco II - Nova Iguaçu, RJ - CEP: 26261220", entity.getLabel());
	}
	
	@Test
	public void testValidateTipoLogradouro() {
		try {
			entity.setTipoLogradouro("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (ApplicationException be) {
			assertEquals("Campo 'Tipo de logradouro' aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateLogradouro() {
		try {
			entity.setLogradouro("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ           ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ           ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (ApplicationException be) {
			assertEquals("Campo 'Logradouro' aceita no máximo 150 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateNumero() {
		try {
			entity.setNumero(null);
			entity.validate();
			entity.setNumero("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ    ");
			entity.validate();
		} catch (ApplicationException be) {
			assertEquals("Campo 'Número' aceita no máximo 10 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateComplemento() {
		try {
			entity.setComplemento(null);
			entity.validate();
			entity.setComplemento("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ    ");
			entity.validate();
		} catch (ApplicationException be) {
			assertEquals("Campo 'Complemento' aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateBairro() {
		try {
			entity.setBairro("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (ApplicationException be) {
			assertEquals("Campo 'Bairro' aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateCidade() {
		try {
			entity.setCidade("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ           ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ           ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (ApplicationException be) {
			assertEquals("Campo 'Cidade' aceita no máximo 100 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateEstado() {
		try {
			entity.setEstado("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ    ");
			entity.validate();
		} catch (ApplicationException be) {
			assertEquals("Campo 'Estado' aceita exatamente 2 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateCep() {
		try {
			entity.setCep(null);
			entity.validate();
			entity.setCep("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ    ");
			entity.validate();
		} catch (ApplicationException be) {
			assertEquals("Campo 'CEP' aceita exatamente 8 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateUsuario() {
		try {
			entity.setUsuario(null);
			entity.validate();			
		} catch (ApplicationException be) {
			assertEquals("Campo 'Usuário' não pode ser nulo.", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateDescricao() {
		try {
			entity.setDescricao("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (ApplicationException be) {
			assertEquals("Campo 'Descrição' aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
}
