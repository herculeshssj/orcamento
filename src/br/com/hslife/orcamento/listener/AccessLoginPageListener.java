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
