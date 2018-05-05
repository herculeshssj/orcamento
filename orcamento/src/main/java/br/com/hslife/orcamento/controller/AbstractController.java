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

import java.io.Serializable;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.component.ArquivoComponent;
import br.com.hslife.orcamento.component.OpcaoSistemaComponent;
import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Usuario;

public abstract class AbstractController implements Serializable {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4309624158532539748L;
	
	protected String moduleTitle = "";
	protected String actionTitle = "";
	
	@Autowired
	private UsuarioComponent usuarioComponent;
	
	@Autowired
	private OpcaoSistemaComponent opcaoSistemaComponent;
	
	@Autowired
	private ArquivoComponent arquivoComponent;
	
	private FacesContext facesContext;
	
	protected abstract void initializeEntity();
	
	public abstract String startUp();
	
	public String getModuleTitle() {
		return moduleTitle;
	}
	
	public Usuario getUsuarioLogado() {
		if (getCurrentFacesContext().getExternalContext().getSessionMap().get("usuarioLogado") == null) {
			Usuario u = new Usuario();
			try {
				u = usuarioComponent.getUsuarioLogado();
				u.setSenha(null);
				getCurrentFacesContext().getExternalContext().getSessionMap().put("usuarioLogado", u);
			} catch (Exception e) {
				errorMessage(e.getMessage());
			}
		}
		
		// Aplica as configurações de locale para pt_BR
		// Tentativa de definir o pt_BR para a aplicação inteira independente das configurações do servidor de aplicação
		getCurrentFacesContext().getViewRoot().setLocale(new Locale("pt", "BR")); 
		
		return (Usuario)getCurrentFacesContext().getExternalContext().getSessionMap().get("usuarioLogado");
	}
	
	public OpcaoSistemaComponent getOpcoesSistema() {
		return opcaoSistemaComponent;
	}
	
	public Moeda getMoedaPadrao() {
		return getOpcoesSistema().getMoedaPadrao();
	}
	
	public ArquivoComponent getArquivoComponent() {
		return arquivoComponent;
	}
	
	public void infoMessage(String mensage) {		
		getCurrentFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, mensage, null));
		getCurrentFacesContext().getExternalContext().getFlash().setKeepMessages(true);
	}
	
	public void warnMessage(String mensage) {
		getCurrentFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, mensage, null));
		getCurrentFacesContext().getExternalContext().getFlash().setKeepMessages(true);
	}
	
	public void errorMessage(String mensage) {
		getCurrentFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensage, null));
		getCurrentFacesContext().getExternalContext().getFlash().setKeepMessages(true);
	}
	
	public void fatalErrorMessage(String mensage) {
		getCurrentFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, mensage, null));
		getCurrentFacesContext().getExternalContext().getFlash().setKeepMessages(true);
	}

	public void setUsuarioComponent(UsuarioComponent usuarioComponent) {
		this.usuarioComponent = usuarioComponent;
	}

	public void setOpcaoSistemaComponent(OpcaoSistemaComponent opcaoSistemaComponent) {
		this.opcaoSistemaComponent = opcaoSistemaComponent;
	}
	
	public FacesContext getCurrentFacesContext() {
		if (this.facesContext == null) {
			return FacesContext.getCurrentInstance();
		} else {
			return this.facesContext;
		}
	}
	
	public void setCurrentFacesContext(FacesContext context) {
		if (context == null) {
			this.facesContext = FacesContext.getCurrentInstance();
		} else {
			this.facesContext = context;
		}
	}
}
