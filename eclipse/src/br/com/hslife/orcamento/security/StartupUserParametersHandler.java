/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

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

package br.com.hslife.orcamento.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.component.OpcaoSistemaComponent;
import br.com.hslife.orcamento.exception.ApplicationException;

@Component("startupUserParametersHandler")
public class StartupUserParametersHandler implements AuthenticationSuccessHandler {
	
	private static final Logger logger = LogManager.getLogger(StartupUserParametersHandler.class);
	
	@Autowired
	private OpcaoSistemaComponent opcaoSistemaComponent;

	public OpcaoSistemaComponent getOpcaoSistemaComponent() {
		return opcaoSistemaComponent;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		
		try {
			// Carrega as opções do sistema do usuário autenticado
			getOpcaoSistemaComponent().atualizarCacheOpcoesSistema();			
		} catch (ApplicationException be) {
			logger.catching(be);
		} catch (Exception e) {
			logger.catching(e);
		}
		
		// Vai para o dashboard do sistema
		response.sendRedirect(request.getContextPath() + "/pages/menu/dashboard.faces");
	}
}