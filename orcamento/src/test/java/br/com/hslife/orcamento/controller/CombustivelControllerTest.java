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
package br.com.hslife.orcamento.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Combustivel;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class CombustivelControllerTest extends AbstractTestControllers {
	
	@Autowired
	private CombustivelController controller;
	
	@Before
	public void initializeEntities() {		
		// Inicializa as entidades		
		controller.setCurrentFacesContext(FacesContextMock.mockFacesContext());
		controller.setEntity(EntityInitializerFactory.createCombustivel());
		
		// Salva as entidades pertinentes antes de iniciar os testes
	}
	
	/*
	 * Salva uma instância e verifica se ela está salva na base
	 */
	@Test
	public void salvarEBuscarTodos() {
		// Salva a entidade instanciada e verifica se a listagem de resultados
		// retorna mais de um resultado 
		controller.save();
		
		controller.setDescricaoCombustivel("");
		controller.find();
		if (controller.getListEntity() == null || controller.getListEntity().size() == 0) {
			Assert.fail("Entidade não salva e tabela vazia!");
		}
	}
	
	/*
	 * Salva 10 instâncias e verifica se todas foram gravadas na base
	 */
	@Test
	public void listarTodos() {
		for (int i = 0; i < 10; i++) {
			Combustivel c = EntityInitializerFactory.createCombustivel();
			c.setDescricao(c.getDescricao() + i);
			controller.setEntity(c);
			controller.save();
		}
		
		controller.setDescricaoCombustivel("");
		controller.find();
		if (controller.getListEntity() == null || controller.getListEntity().size() != 10) {
			Assert.fail("Nem todas as entidades foram salvas na base!");
		}
	}
	
	/*
	 * Salva 10 instâncias e verifica se uma instância em particular foi salva
	 */
	@Test
	public void buscarUmCombustivel() {
		for (int i = 0; i < 10; i++) {
			Combustivel c = EntityInitializerFactory.createCombustivel();
			c.setDescricao(c.getDescricao() + i);
			controller.setEntity(c);
			controller.save();
		}
		
		controller.setDescricaoCombustivel("5");
		controller.find();
		if (controller.getListEntity() == null || controller.getListEntity().size() != 1) {
			Assert.fail("Entidade não salva e tabela vazia!");
		}
	}
}