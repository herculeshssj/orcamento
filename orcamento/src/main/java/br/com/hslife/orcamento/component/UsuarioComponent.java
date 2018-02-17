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
package br.com.hslife.orcamento.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.facade.IUsuario;

@Component
public class UsuarioComponent {
	
	private static final Logger logger = LogManager.getLogger(UsuarioComponent.class);
	
	@Autowired
	private IUsuario usuarioService;
	
	@Autowired
	private IMoeda moedaService;
	
	private IUsuario getService() {
		return this.usuarioService;
	}
	
	public IMoeda getMoedaService() {
		return moedaService;
	}

	public Usuario getUsuarioLogado() {
		try {
			String login = "";
			SecurityContext context = SecurityContextHolder.getContext();
	        if (context instanceof SecurityContext) {
	            Authentication authentication = context.getAuthentication();
	            if (authentication instanceof Authentication) {
	            	login = ((User)authentication.getPrincipal()).getUsername();
	            }
	        }
	        Usuario u = getService().buscarPorLogin(login); 
	        return u;
		} catch (Throwable t) {
        	logger.catching(t);
        	throw new RuntimeException(t);
        }
	}
	
	/**
	 * Inicializa uma moeda padrão para o usuário recém cadastrado
	 */
	public void initializeMoedaPadrao(Usuario usuario) {
		Moeda moedaPadrao = new Moeda();
		moedaPadrao.setCodigoMonetario("BRL");
		moedaPadrao.setNome("Real");
		moedaPadrao.setPadrao(true);
		moedaPadrao.setPais("Brasil");
		moedaPadrao.setSiglaPais("BR");
		moedaPadrao.setSimboloMonetario("R$");
		moedaPadrao.setUsuario(usuario);
		getMoedaService().cadastrar(moedaPadrao);
	}
}
