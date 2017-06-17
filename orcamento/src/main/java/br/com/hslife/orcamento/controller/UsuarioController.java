/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor
	
	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

    para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 - 
	
	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/

package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.util.Util;

@Component("usuarioMB")
@Scope("session")
public class UsuarioController extends AbstractCRUDController<Usuario> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8492558979966203540L;

	@Autowired
	private IUsuario service;
	
	@Autowired
	private UsuarioComponent usuarioComponent;
	
	private String loginUsuario;
	
	private String senhaAtual;
	private String novaSenha;
	private String confirmaSenha;
	
	private Map<String, Long> mapAtividadeUsuario = new HashMap<String, Long>();

	public UsuarioController() {
		super(new Usuario());
		moduleTitle = "Usuários";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Usuario();
		listEntity = new ArrayList<Usuario>();
	}
	
	public void find() {
		try {
			listEntity = getService().buscarTodosPorLogin(loginUsuario);
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String list() {
		operation = "list";
		actionTitle = "";  
		return "/pages/" + entity.getClass().getSimpleName() + "/list" + entity.getClass().getSimpleName(); 
	}
	
	public String create() {
		operation = "create";
		actionTitle = " - Novo";
		initializeEntity();
		return "/pages/" + entity.getClass().getSimpleName() + "/form" + entity.getClass().getSimpleName();
	}
	
	@Override
	public String save() {
		if (entity.getId() == null) {
			entity.setSenha(Util.SHA256(entity.getConfirmaSenha()));
		} else {
			if (entity.getConfirmaSenha() != null && !entity.getConfirmaSenha().isEmpty()) {
				entity.setSenha(Util.SHA256(entity.getConfirmaSenha()));
			}		
		}
		return super.save();
	}
	
	public void saveUser() {
		try {
			getService().alterar(entity);
			infoMessage("Dados do usuário alterados com sucesso!");
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String minhaConta() {
		try {
			entity = getService().buscarPorLogin(getUsuarioLogado().getLogin());
			actionTitle = " - Minha Conta";
			return "/pages/" + entity.getClass().getSimpleName() + "/minhaConta";
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String edit() {
		try {
			entity = getService().buscarPorID(idEntity);
			operation = "edit";
			actionTitle = " - Editar";
			return "/pages/" + entity.getClass().getSimpleName() + "/form" + entity.getClass().getSimpleName();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public void logarComo() {
		try {
			Usuario u = getService().buscarPorID(idEntity);
			u.setNome(u.getNome() + "(admin)");
			u.setLogado(true);
			
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuarioLogado", u);
			
			infoMessage("Operação realizada com sucesso. Logado como " + getUsuarioLogado().getNome());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void deslogarComo() {
		try {
			Usuario u = getService().buscarPorID(idEntity);
			
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuarioLogado", usuarioComponent.getUsuarioLogado());
			
			infoMessage("Operação realizada com sucesso. Deslogado do usuário " + u.getNome());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}

	public void efetuarRegistro() {
		try {
			getService().efetuarRegistro(entity);
			infoMessage("Usuário registrado com sucesso!");
			infoMessage("Senha de acesso foi enviada para o e-mail informado.");
			initializeEntity();
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void recuperarSenha() {
		try {
			getService().recuperarSenha(entity);
			infoMessage("Senha alterada com sucesso!");
			infoMessage("Senha de acesso foi envada para o e-mail cadastrado.");
			initializeEntity();
		} catch (ApplicationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String alterarSenhaView() {
		try {
			entity = getService().buscarPorLogin(getUsuarioLogado().getLogin());
			actionTitle = " - Alterar Senha";
			return "/pages/" + entity.getClass().getSimpleName() + "/alterarSenha";
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public void alterarSenha() {
		try {
			// Traz as credenciais do usuário da base
			Usuario u = getService().buscarPorLogin(getUsuarioLogado().getLogin());
			
			// Verifica se a senha atual informada coincide com a armazenada na base
			if (!Util.SHA256(senhaAtual).equals(u.getSenha())) {
				warnMessage("Senha atual não confere!");
				return;
			}
			
			// Verifica se a nova senha é idêntica a atual
			if (Util.SHA256(confirmaSenha).equals(u.getSenha())) {
				warnMessage("Nova senha não pode ser igual a senha atual!");
				return;
			}
			
			// Verifica se as senhas digitadas são iguais
			if (!Util.SHA256(confirmaSenha).equals(Util.SHA256(novaSenha))) {
				warnMessage("As senhas não coincidem!");
				return;
			}
			
			// Seta a nova senha na entidade e salva na base
			entity.setSenha(Util.SHA256(confirmaSenha));
			getService().alterar(entity);
			infoMessage("Senha alterada com sucesso!");
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String gerarTokenID() {
		try {
			getService().gerarTokenID(getUsuarioLogado());
			infoMessage("Token gerado com sucesso!");
			return this.minhaConta();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String getLoginUsuario() {
		return loginUsuario;
	}

	public void setLoginUsuario(String loginUsuario) {
		this.loginUsuario = loginUsuario;
	}

	public IUsuario getService() {
		return service;
	}

	public void setService(IUsuario service) {
		this.service = service;
	}

	public void setUsuarioComponent(UsuarioComponent usuarioComponent) {
		this.usuarioComponent = usuarioComponent;
	}

	public Map<String, Long> getMapAtividadeUsuario() {
		return mapAtividadeUsuario;
	}

	public void setMapAtividadeUsuario(Map<String, Long> mapAtividadeUsuario) {
		this.mapAtividadeUsuario = mapAtividadeUsuario;
	}

	public String getSenhaAtual() {
		return senhaAtual;
	}

	public void setSenhaAtual(String senhaAtual) {
		this.senhaAtual = senhaAtual;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String getConfirmaSenha() {
		return confirmaSenha;
	}

	public void setConfirmaSenha(String confirmaSenha) {
		this.confirmaSenha = confirmaSenha;
	}
}