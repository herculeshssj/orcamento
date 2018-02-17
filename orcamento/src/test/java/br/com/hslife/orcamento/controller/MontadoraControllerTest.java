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

para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor,

Boston, MA  02110-1301, USA.


Para mais informações sobre o programa Orçamento Doméstico e seu autor

entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class MontadoraControllerTest extends AbstractTestControllers {
	
	@Autowired
	private MontadoraController controller;
	
	@Before
	public void initializeEntities() throws Exception {
		// Inicializa o FacesContext
		controller.setCurrentFacesContext(FacesContextMock.mockFacesContext());
				
		// Efetua o login do usuário
		login();
		
		// Inicializa as entidades		
		controller.setEntity(EntityInitializerFactory.createMontadora());
		
		// Salva as entidades pertinentes antes de iniciar os testes
	}
	
	/*
	 * Entra no módulo
	 */
	@Test
	public void startUp() {
		String result = controller.startUp();
		
		assertEquals(controller.goToListPage, result);
	}
	
	/*
	 * Pesquisa todos os registros cadastrados
	 */
	@Test
	public void buscarTodosCadastrados() {
		controller.setDescricaoMontadora("");
		
		controller.find();
		
		assertEquals(true, controller.getListEntity().isEmpty());
	}
	
	/*
	 * Realiza o cadastro de 10 registros
	 */
	@Test
	public void cadastrarRegistros() {
		for (int i = 0; i < 10; i++) {
			String result = controller.create();
			
			assertEquals(controller.goToFormPage, result);
			
			controller.setEntity(EntityInitializerFactory.createMontadora());
			controller.getEntity().setDescricao(controller.getEntity().getDescricao() + i);
			result = controller.save();
			
			assertEquals(controller.goToListPage, result);
		}
	}
	
	/*
	 * Realiza buscas pelos registros cadastrados
	 */
	@Test
	public void buscarRegistros() {
		this.cadastrarRegistros();
		
		controller.setDescricaoMontadora("");
		controller.find();
		
		assertEquals(10, controller.getListEntity().size());
		
		controller.setDescricaoMontadora("5");
		controller.find();
		
		assertEquals(1, controller.getListEntity().size());
	}
	
	/*
	 * Edita um registro em particular
	 */
	@Test
	public void editarRegistro() {
		this.cadastrarRegistros();
		
		controller.setDescricaoMontadora("5");
		controller.find();
		assertEquals(1, controller.getListEntity().size());
		
		controller.setIdEntity(controller.getListEntity().get(0).getId());
		String result = controller.edit();
		assertEquals(controller.goToFormPage, result);
		
		controller.getEntity().setDescricao(controller.getEntity().getDescricao() + "[ALTERADO]");
		result = controller.save();
		assertEquals(controller.goToListPage, result);
		
		controller.setDescricaoMontadora("[ALTERADO]");
		controller.find();
		
		assertEquals(1, controller.getListEntity().size());
	}
	
	/*
	 * Exclui um registro em particular
	 */
	@Test
	public void excluirRegistro() {
		this.editarRegistro();
		
		controller.setDescricaoMontadora("[ALTERADO]");
		controller.find();
		
		assertEquals(1, controller.getListEntity().size());
		
		controller.setIdEntity(controller.getListEntity().get(0).getId());
		String result = controller.view();
		assertEquals(controller.goToViewPage, result);
		
		result = controller.delete();
		assertEquals(controller.goToListPage, result);
		
		controller.setDescricaoMontadora("[ALTERADO]");
		controller.find();
		
		assertEquals(0, controller.getListEntity().size());
	}
}
