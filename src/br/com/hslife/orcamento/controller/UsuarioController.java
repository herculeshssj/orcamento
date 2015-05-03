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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoUsuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IUsuario;

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
		novaSenha = "";
		confirmaSenha = "";
	}
	
	public void find() {
		try {
			if (getUsuarioLogado().getTipoUsuario().equals(TipoUsuario.ROLE_ADMIN)) {
				listEntity = getService().buscarTodosPorLogin(loginUsuario);
			} else {
				listEntity = new ArrayList<Usuario>();
				listEntity.add(getUsuarioLogado());
			}			
		} catch (BusinessException be) {
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
	
	public String save() {
		try {
			if (entity.getId() == null) {
				getService().cadastrar(entity, novaSenha, confirmaSenha);
				infoMessage("Usuário cadastrado com sucesso!");
			} else {
				getService().alterar(entity, novaSenha, confirmaSenha);
				infoMessage("Usuário alterado com sucesso!");
			}			
			// Verifica se a listagem de resultados está nula ou não para poder efetuar novamente a busca
			if (listEntity != null && !listEntity.isEmpty()) {
				// Inicializa os objetos
				initializeEntity();
				
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", getUsuarioLogado());
							
				// Determina se a busca será executada novamente
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {					
					find();
				}
			} else {
				initializeEntity();
			}
			return list();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public void saveUser() {
		try {
			getService().alterar(entity, novaSenha, confirmaSenha);
			infoMessage("Dados do usuário alterados com sucesso!");					
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@PostConstruct
	public String startUpUser() {
		try {
			entity = getService().buscarPorLogin(getUsuarioLogado().getLogin());
			operation = "edit";
			actionTitle = " - Editar";
			return "/pages/" + entity.getClass().getSimpleName() + "/form" + entity.getClass().getSimpleName(); 
		} catch (BusinessException be) {
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
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	@Override
	public String view() {
		try {
			// Obtém o relatório de atividades do usuário
			mapAtividadeUsuario = getService().buscarAtividadeUsuario(getService().buscarPorID(idEntity));
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return super.view();
	}
	
	public void logarComo() {
		try {
			Usuario u = getService().buscarPorID(idEntity);
			u.setNome(u.getNome() + "(admin)");
			u.setLogado(true);
			
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuarioLogado", u);
			
			infoMessage("Operação realizada com sucesso. Logado como " + getUsuarioLogado().getNome());
			
		} catch (Exception e) {
			errorMessage(e.getMessage());
		}
	}
	
	public void deslogarComo() {
		try {
			Usuario u = getService().buscarPorID(idEntity);
			
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuarioLogado", usuarioComponent.getUsuarioLogado());
			
			infoMessage("Operação realizada com sucesso. Deslogado do usuário " + u.getNome());
		} catch (Exception e) {
			errorMessage(e.getMessage());
		}
	}

	public String getLoginUsuario() {
		return loginUsuario;
	}

	public void setLoginUsuario(String loginUsuario) {
		this.loginUsuario = loginUsuario;
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
}