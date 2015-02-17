/***
  
  	Copyright (c) 2012 - 2015 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@gmail.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.controller;

import java.io.Serializable;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.component.OpcaoSistemaComponent;
import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.component.VersaoComponent;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.entity.Versao;

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
	private VersaoComponent versaoComponent;
	
	@Autowired
	private OpcaoSistemaComponent opcaoSistemaComponent;
	
	protected abstract void initializeEntity();
	
	public abstract String startUp();
	
	public String getModuleTitle() {
		return moduleTitle;
	}
	
	public Usuario getUsuarioLogado() {
		if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioLogado") == null) {
			Usuario u = new Usuario();
			try {
				u = usuarioComponent.getUsuarioLogado();
				u.setSenha(null);
				FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuarioLogado", u);
			} catch (Exception e) {
				errorMessage(e.getMessage());
			}
		}
		
		// Aplica as configurações de locale para pt_BR
		// Tentativa de definir o pt_BR para a aplicação inteira independente das configurações do servidor de aplicação
		FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale("pt", "BR")); 
		
		return (Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioLogado");
	}
	
	public Versao getVersaoAtual() {
		if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("versao") == null) {
			Versao v = new Versao();
			try {
				v = versaoComponent.versaoAtual();				
				FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("versao", v);
			} catch (Exception e) {
				errorMessage(e.getMessage());
			}
		}
		return (Versao)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("versao");
	}
	
	public OpcaoSistemaComponent getOpcoesSistema() {
		return opcaoSistemaComponent;
	}
	
	public void infoMessage(String mensage) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, mensage, null));
	}
	
	public void warnMessage(String mensage) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, mensage, null));
	}
	
	public void errorMessage(String mensage) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensage, null));
	}
	
	public void fatalErrorMessage(String mensage) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, mensage, null));
	}

	public void setUsuarioComponent(UsuarioComponent usuarioComponent) {
		this.usuarioComponent = usuarioComponent;
	}

	public void setVersaoComponent(VersaoComponent versaoComponent) {
		this.versaoComponent = versaoComponent;
	}

	public void setOpcaoSistemaComponent(OpcaoSistemaComponent opcaoSistemaComponent) {
		this.opcaoSistemaComponent = opcaoSistemaComponent;
	}
}