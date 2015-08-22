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

package br.com.hslife.orcamento.listener;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class AccessLoginPageListener implements PhaseListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 594620250995415552L;


	@Override
	public void afterPhase(PhaseEvent event) {
		// Obtém o contexto atual
		FacesContext contexto = event.getFacesContext();
		
		// Obtém o caminho do contexto
		String caminho = contexto.getExternalContext().getRequestServletPath();
		
		try {
			
			// Verifica se o usuário está tentando acessar a página de login
			if (caminho.indexOf("login") > -1) {
				// Se o usuário estiver setado, redireciona para o menu principal
				if (contexto.getExternalContext().getSessionMap().get("usuarioLogado") != null) {
					NavigationHandler nh = contexto.getApplication().getNavigationHandler();
					nh.handleNavigation(contexto, null, "/pages/menu/dashboard?faces-redirect=true");
				}
			}
			
		} catch (Exception e) {
			// Em caso de exceção redireciona para a página de login
			NavigationHandler nh = contexto.getApplication().getNavigationHandler();
			nh.handleNavigation(contexto, null, "login");
		}
	}

	@Override
	public void beforePhase(PhaseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

}
