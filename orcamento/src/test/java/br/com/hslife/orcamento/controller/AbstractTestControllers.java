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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.HttpSession;

import br.com.hslife.orcamento.component.UsuarioComponent;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Responsável por prover métodos comuns a todas as classes de teste.
 * 
 * @author herculeshssj
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring-config-junit.xml" })
@WebAppConfiguration
public abstract class AbstractTestControllers
		extends AbstractTransactionalJUnit4SpringContextTests {

	/*
	 * Mock do Spring MVC
	 */
	private MockMvc mockMvc;

	/*
	 * Filtro do Spring Security
	 */
	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	/*
	 * Mock de Servlet para tratar as requisições
	 */
	@Autowired
	private MockHttpServletRequest request;

	/*
	 * Contexto do Spring para uma aplicação Web
	 */
	@Autowired
	private WebApplicationContext webAppContext;

	/*
	 * Componente para obter as informações do usuário logado em sessão
	 */
	@Autowired
	private UsuarioComponent usuarioComponent;

	/*
	 * Inicializa o contexto do Spring Security para disponibilizar para as
	 * classes de teste.
	 */
	protected void login() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext)
				.addFilters(springSecurityFilterChain).build();

		HttpSession session = mockMvc
				.perform(post("/login").param("username", "admin")
						.param("password", "admin"))
				.andDo(print()).andExpect(status().isFound())
				.andExpect(redirectedUrl("/")).andReturn().getRequest()
				.getSession();

		request.setSession(session);

		SecurityContext securityContext = (SecurityContext) session
				.getAttribute(
						HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		SecurityContextHolder.setContext(securityContext);
	}


	public UsuarioComponent getUsuarioComponent() {
		return usuarioComponent;
	}
}
