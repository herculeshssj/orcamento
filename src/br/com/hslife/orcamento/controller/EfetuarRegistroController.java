/***
  
  	Copyright (c) 2012, 2013, 2014 Hércules S. S. José

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

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site HSlife
    
    no endereco www.hslife.com.br ou escreva para a Fundação do Software 
    
    Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor acesse o 

    endereço www.hslife.com.br, pelo e-mail contato@hslife.com.br ou escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
 ***/

package br.com.hslife.orcamento.controller;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.facade.IUsuario;

@Component("efetuarRegistroMB")
@Scope("request")
public class EfetuarRegistroController {

	@Autowired
	private IUsuario service;
	
	private Usuario entity;
	
	public EfetuarRegistroController() {
		entity = new Usuario();
	}
	
	public String efetuarRegistro() {
		try {
			getService().efetuarRegistro(entity);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Usuário registrado com sucesso!"));
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Senha de acesso foi enviada para o e-mail informado."));
			entity = new Usuario();
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Falha ao efetuar o registro. Entre em contato com o Administrador do Sistema.", null));
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(),null));
		}
		return "";
	}

	public IUsuario getService() {
		return service;
	}

	public void setService(IUsuario service) {
		this.service = service;
	}

	public Usuario getEntity() {
		return entity;
	}

	public void setEntity(Usuario entity) {
		this.entity = entity;
	}
}