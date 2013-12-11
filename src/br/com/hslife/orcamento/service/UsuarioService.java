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

package br.com.hslife.orcamento.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.component.EmailComponent;
import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoUsuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.repository.AuditoriaRepository;
import br.com.hslife.orcamento.repository.UsuarioRepository;
import br.com.hslife.orcamento.util.Util;

@Service("usuarioService")
public class UsuarioService extends AbstractCRUDService<Usuario> implements IUsuario {
	
	@Autowired
	private UsuarioRepository repository;
	
	@Autowired
	private UsuarioComponent component;
	
	@Autowired
	private EmailComponent emailComponent;
	
	@Autowired
	private AuditoriaRepository auditoriaRepository;

	public UsuarioRepository getRepository() {
		return repository;
	}

	public void setRepository(UsuarioRepository repository) {
		this.repository = repository;
	}

	public UsuarioComponent getComponent() {
		return component;
	}

	public void setComponent(UsuarioComponent component) {
		this.component = component;
	}
	
	public void setEmailComponent(EmailComponent emailComponent) {
		this.emailComponent = emailComponent;
	}

	public void setAuditoriaRepository(AuditoriaRepository auditoriaRepository) {
		this.auditoriaRepository = auditoriaRepository;
	}
	
	@Override
	public void excluir(Usuario entity) throws BusinessException {
		if (auditoriaRepository.countRegistroAuditoriaByUsuario(entity.getLogin()) == 0)
			super.excluir(entity);
		else 
			throw new BusinessException("Não é possível excluir! Usuário possui atividade no sistema.");
	}

	@Override
	public void cadastrar(Usuario entity, String novaSenha, String confirmaSenha) throws BusinessException {
		if (novaSenha != null && confirmaSenha != null && !novaSenha.trim().isEmpty() && !confirmaSenha.trim().isEmpty() && novaSenha.equals(confirmaSenha)) {
			Usuario u = getRepository().findByLogin(entity.getLogin());
			if (u != null) {
				throw new BusinessException("Login já cadastrado para outro usuário!");
			} else {
				entity.setSenha(Util.SHA1(confirmaSenha));
				getRepository().save(entity);
			}
		} else {
			if (!novaSenha.equals(confirmaSenha)) {
				throw new BusinessException("As senhas não coincidem!");
			}
			throw new BusinessException("Informe a senha do usuário!");
		}
	}
	
	@Override
	public void alterar(Usuario entity, String novaSenha, String confirmaSenha) throws BusinessException {
		if (novaSenha != null && confirmaSenha != null && !novaSenha.trim().isEmpty() && !confirmaSenha.trim().isEmpty() && novaSenha.equals(confirmaSenha)) {
			Usuario u = getRepository().findById(entity.getId());
			if (u.getSenha().equals(Util.SHA1(confirmaSenha))) {
				throw new BusinessException("Nova senha não pode ser igual a senha atual!");
			} else 
				entity.setSenha(Util.SHA1(confirmaSenha));			
		} else {
			if (!novaSenha.equals(confirmaSenha)) {
				throw new BusinessException("As senhas não coincidem!");
			}			
		}
		getRepository().update(entity);
	}
	
	@Override
	public void efetuarRegistro(Usuario entity) throws BusinessException {
		// Gera hash da data atual para extrair a senha
		String hash = Util.MD5((new Date().toString()));
		
		// Extrai do hash a senha aleatória do usuário
		String senha = hash.substring(0, 8);
		
		// invoca o método de cadastrar usuário
		this.cadastrar(entity, senha, senha);
		
		// Efetua o envio do e-mail com a senha
		StringBuilder mensagemEmail = new StringBuilder();
		
		mensagemEmail.append("Prezado " + entity.getNome() + "\n\n");
		mensagemEmail.append("Segue abaixo a senha gerada:\n\n");
		mensagemEmail.append("Senha: " + senha + "\n\n");
		mensagemEmail.append("Caso tenha dificuldade de acesso entre em contato com o administrador para alterar sua senha.\n\n\n");
		mensagemEmail.append("HSlife Serviços de TI");
		
		try {
			emailComponent.enviarEmail(entity.getNome(), entity.getEmail(), "Orçamento Doméstico - Registro de Usuário", mensagemEmail.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(e);
		}
	}
	
	@Override
	public void recuperarSenha(Usuario entity) throws BusinessException {
		Usuario u = getRepository().findByLogin(entity.getLogin());
		
		if (u == null || u.getLogin().equals("admin")) {
			throw new BusinessException("Login não encontrado!");
		} else if (u.getEmail().equals(entity.getEmail())) {
			// Gera hash da data atual para extrair a senha
			String hash = Util.MD5((new Date().toString()));
			
			// Extrai do hash a senha aleatória do usuário
			String senha = hash.substring(0, 8);
			
			// invoca o método de alterar usuário
			this.alterar(u, senha, senha);
			
			// Efetua o envio do e-mail com a senha
			StringBuilder mensagemEmail = new StringBuilder();
			
			mensagemEmail.append("Prezado " + u.getNome() + "\n\n");
			mensagemEmail.append("Segue abaixo a senha gerada:\n\n");
			mensagemEmail.append("Senha: " + senha + "\n\n");
			mensagemEmail.append("Caso tenha dificuldade de acesso entre em contato com o administrador para alterar sua senha.\n\n\n");
			mensagemEmail.append("HSlife Serviços de TI");
			
			try {
				emailComponent.enviarEmail(entity.getNome(), entity.getEmail(), "Orçamento Doméstico - Recuperação de senha", mensagemEmail.toString());
			} catch (Exception e) {
				throw new BusinessException(e);
			}
		} else {
			throw new BusinessException("E-Mail informado não confere com o cadastrado!");
		}		
	}
	
	@Override
	public List<Usuario> getListaUsuarios() throws BusinessException {
		List<Usuario> usuarios = new ArrayList<Usuario>();
		if (getComponent().getUsuarioLogado().getTipoUsuario().equals(TipoUsuario.ROLE_ADMIN)) {
			for (Usuario u : getRepository().findAll()) {
				usuarios.add(u);
			}
		} else {
			usuarios.add(getComponent().getUsuarioLogado());
		}
		return usuarios;
	}
	
	@Override
	public Usuario buscarPorLogin(String login) throws BusinessException {
		return getRepository().findByLogin(login);
	}
	
	@Override
	public List<Usuario> buscarTodosPorLogin(String login) throws BusinessException {
		return getRepository().findAllByLogin(login);
	}

	@Override
	public void validar(Usuario entity) throws BusinessException {
		// TODO Auto-generated method stub
		
	}
}