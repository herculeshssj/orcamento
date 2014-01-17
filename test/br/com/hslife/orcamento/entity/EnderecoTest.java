/***
  
  	Copyright (c) 2012, 2013, 2014 Hércules S. S. José

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

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site HSlife
    
    no endereco www.hslife.com.br ou escreva para a Fundação do Software 
    
    Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor acesse o 

    endereço www.hslife.com.br, pelo e-mail contato@hslife.com.br ou escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
 ***/

package br.com.hslife.orcamento.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import br.com.hslife.orcamento.exception.BusinessException;

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
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateLogradouro() {
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateNumero() {
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateComplemento() {
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateBairro() {
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateCidade() {
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateEstado() {
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateCep() {
		fail("Falha no teste!");
	}
	
/*
	@Test
	public void testValidateGenero() {
		try {
			entity.setGenero('G');
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Gênero inexistente!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}

	@Test
	public void testValidateEtnia() {
		try {
			entity.setEtnia("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateTipoSanguineo() {
		try {
			entity.setTipoSanguineo("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 5 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateNacionalidade() {
		try {
			entity.setNacionalidade("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateNaturalidade() {
		try {
			entity.setNaturalidade("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateEscolaridade() {
		try {
			entity.setEscolaridade("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateFiliacaoPai() {
		try {
			entity.setFiliacaoPai("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 100 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateFiliacaoMae() {
		try {
			entity.setFiliacaoMae("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 100 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateEstadoCivil() {
		try {
			entity.setEstadoCivil("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 50 caracteres!", be.getMessage());
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
		} catch (BusinessException be) {
			assertEquals("Informe o usuário!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	*/
}
