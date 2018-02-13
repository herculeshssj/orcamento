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
package br.com.hslife.orcamento.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.hslife.orcamento.exception.ValidationException;

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
	
	@Test(expected=ValidationException.class)
	public void testValidateTipoLogradouro() {
		entity.setTipoLogradouro("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateLogradouro() {
		entity.setLogradouro("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ           ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ           ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateNumero() {
		entity.setNumero(null);
		entity.validate();
		entity.setNumero("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ    ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateComplemento() {
		entity.setComplemento(null);
		entity.validate();
		entity.setComplemento("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ    ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateBairro() {
		entity.setBairro("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateCidade() {
		entity.setCidade("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ           ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ           ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateEstado() {
		entity.setEstado("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ    ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateCep() {
		entity.setCep(null);
		entity.validate();
		entity.setCep("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ    ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateDescricao() {
		entity.setDescricao("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
		entity.validate();
	}
}
