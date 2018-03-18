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

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Classe responsável por realizar os testes unitários do controller em 
 * questão. As rotinas são padronizadas pois ele herda diretamente de
 * AbstractCRUDController.
 * 
 * @author herculeshssj
 *
 */
public class LancamentoContaControllerTest extends AbstractTestControllers {
	
	/*
	 * Controller que está sendo testado
	 */
	@Autowired
	private LancamentoContaController controller;
	
	/*
	 * Inicializa as variáveis e objetos para executar ao longo dos testes
	 */
	@Before
	public void initializeEntities() throws Exception {
		// Inicializa o FacesContext
		controller.setCurrentFacesContext(FacesContextMock.mockFacesContext());
				
		// Efetua o login do usuário
		login();
		
		// Inicializa as entidades		
		
		
		// Salva as entidades pertinentes antes de iniciar os testes
	}
	
	/***
	 
	Funcionalidade: Lançamentos da Conta/Cartão

	Cenário de teste: Obter a lista de contas ativas a fim de selecionar os lançamentos de uma determinada conta

	Dado que um usuário se registrou no sistema
	E configurou para não exibir as contas inativas
	E cadastrou uma moeda padrão
	E cadastrou uma conta que não é do tipo cartão
	Quando acessar o módulo Lançamentos da Conta/Cartão
	Então exibir na lista de contas a conta cadastrada.
	
	 */
	@Test
	public void listarContaAtivas() {
		
	}
}