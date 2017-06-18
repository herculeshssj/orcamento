/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
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

package br.com.hslife.orcamento.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.component.EmailComponent;
import br.com.hslife.orcamento.component.OpcaoSistemaComponent;
import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoUsuario;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.repository.UsuarioRepository;
import br.com.hslife.orcamento.util.Util;

@Service("usuarioService")
public class UsuarioService extends AbstractCRUDService<Usuario> implements IUsuario {
	
	private static final Logger logger = LogManager.getLogger(UsuarioService.class);
	
	@Autowired
	private UsuarioRepository repository;
	
	@Autowired
	private UsuarioComponent component;
	
	@Autowired
	private EmailComponent emailComponent;
	
	@Autowired
	private OpcaoSistemaComponent opcaoSistemaComponent;

	public UsuarioRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}

	public UsuarioComponent getComponent() {
		return component;
	}

	public EmailComponent getEmailComponent() {
		return emailComponent;
	}

	public OpcaoSistemaComponent getOpcaoSistemaComponent() {
		return opcaoSistemaComponent;
	}

	@Override
	public void cadastrar(Usuario entity){		
		// Verifica se o login informado já consta na base
		Usuario u = getRepository().findByLogin(entity.getLogin());
		if (u != null) {
			throw new BusinessException("Login já cadastrado para outro usuário!");
		}
		
		getRepository().save(entity);
		
		// Inicializa os valores padrão para o usuário recém cadastrado
		getOpcaoSistemaComponent().setarOpcoesPadraoUsuario(entity);
		getComponent().initializeMoedaPadrao(entity);		
	}
	
	@Override
	public void efetuarRegistro(Usuario entity) throws ApplicationException {
		// Gera hash da data atual para poder gerar a senha aleatório do usuário
		String hash = Util.MD5(new Date().toString());
		String senha = hash.substring(0, 16);
		entity.setSenha(Util.SHA256(senha));
		
		this.cadastrar(entity);
		
		// Efetua o envio do e-mail com a senha
		StringBuilder mensagemEmail = new StringBuilder();
		
		mensagemEmail.append("Prezado " + entity.getNome() + "\n\n");
		mensagemEmail.append("Segue abaixo a senha gerada:\n\n");
		mensagemEmail.append("Senha: " + senha + "\n\n");
		mensagemEmail.append("Caso tenha dificuldade de acesso entre em contato com o administrador para alterar sua senha.\n\n\n");
		mensagemEmail.append("Administrador do Sistema");
		
		try {
			getEmailComponent().setDestinatario(entity.getNome());
			getEmailComponent().setEmailDestinatario(entity.getEmail());
			getEmailComponent().setAssunto("Orçamento Doméstico - Registro de Usuário");
			getEmailComponent().setMensagem(mensagemEmail.toString());
			getEmailComponent().enviarEmail();
		} catch (Exception e) {
			logger.catching(e);
			e.printStackTrace();
			throw new ApplicationException(e);
		}
	}
	
	@Override
	public void recuperarSenha(Usuario entity) throws ApplicationException, BusinessException {
		Usuario u = getRepository().findByLogin(entity.getLogin());
		
		// Verifica se o usuário informado existe, se não é o usuário admin, e se o e-mail informado coincide com o 
		// cadastrado
		if (u == null || u.getLogin().equals("admin") || !u.getEmail().equals(entity.getEmail())) {
			throw new BusinessException("Login não encontrado!");
		}
		
		// Verifica se o usuário encontra-se ativo no sistema
		if (!u.isAtivo()) {
			// Envia mensagem de e-mail avisando que a conta está inativa
			StringBuilder mensagemEmail = new StringBuilder();
			
			mensagemEmail.append("Prezado " + u.getNome() + "\n\n");
			mensagemEmail.append("Sua conta encontra-se inativa.\n\n");
			mensagemEmail.append("Caso queira voltar a utilizar o sistema e acessar novamente seus dados, por favor entre em contato.\n\n");
			mensagemEmail.append("Atenciosamente,\n\n");
			mensagemEmail.append("Administrador do Sistema.\n");
			
			try {
				getEmailComponent().setDestinatario(u.getNome());
				getEmailComponent().setEmailDestinatario(u.getEmail());
				getEmailComponent().setAssunto("Orçamento Doméstico - Conta inativa");
				getEmailComponent().setMensagem(mensagemEmail.toString());
				getEmailComponent().enviarEmail();
			} catch (Exception e) {
				logger.catching(e);
				e.printStackTrace();
				throw new ApplicationException(e);
			}
			return;
		}
			
		// Gera hash da data atual para extrair a senha
		String hash = Util.MD5((new Date().toString()));
		
		// Extrai do hash a senha aleatória do usuário
		String senha = hash.substring(0, 16);
		u.setSenha(Util.SHA256(senha));
		u.setConfirmaSenha(u.getSenha());
		
		// invoca o método de alterar usuário
		this.alterar(u);
		
		// Efetua o envio do e-mail com a senha
		StringBuilder mensagemEmail = new StringBuilder();
		
		mensagemEmail.append("Prezado " + u.getNome() + "\n\n");
		mensagemEmail.append("Segue abaixo a senha gerada:\n\n");
		mensagemEmail.append("Senha: " + senha + "\n\n");
		mensagemEmail.append("Caso tenha dificuldade de acesso entre em contato com o administrador para alterar sua senha.\n\n\n");
		mensagemEmail.append("Administrador do Sistema");
		
		try {
			getEmailComponent().setDestinatario(u.getNome());
			getEmailComponent().setEmailDestinatario(u.getEmail());
			getEmailComponent().setAssunto("Orçamento Doméstico - Recuperação de senha");
			getEmailComponent().setMensagem(mensagemEmail.toString());
			getEmailComponent().enviarEmail();
		} catch (Exception e) {
			logger.catching(e);
			throw new ApplicationException(e);
		}		
	}
	
	public void enviarMensagemParaAdmin(String assuntoMensagem, String mensagem) throws ApplicationException{
		try {
			Usuario admin = getRepository().findByLogin("admin");
			getEmailComponent().setRemetente(getComponent().getUsuarioLogado().getNome());
			getEmailComponent().setEmailRemetente(getComponent().getUsuarioLogado().getEmail());
			getEmailComponent().setDestinatario(admin.getNome());
			getEmailComponent().setEmailDestinatario(admin.getEmail());
			getEmailComponent().setAssunto(assuntoMensagem);
			getEmailComponent().setMensagem(mensagem.toString());
			getEmailComponent().enviarEmail();
		} catch (Exception e) {
			logger.catching(e);
			throw new ApplicationException("Erro ao enviar e-mail:", e);
		}
	}
	
	public void enviarMensagemParaAdmin(String rementente, String emailRemetente, String assuntoMensagem, String mensagem) throws ApplicationException{
		try {
			Usuario admin = getRepository().findByLogin("admin");
			getEmailComponent().setRemetente(rementente);
			getEmailComponent().setEmailRemetente(emailRemetente);
			getEmailComponent().setDestinatario(admin.getNome());
			getEmailComponent().setEmailDestinatario(admin.getEmail());
			getEmailComponent().setAssunto(assuntoMensagem);
			getEmailComponent().setMensagem(mensagem.toString());
			getEmailComponent().enviarEmail();
		} catch (Exception e) {
			logger.catching(e);
			throw new ApplicationException("Erro ao enviar e-mail:", e);
		}
	}
	
	@Override
	public List<Usuario> getListaUsuarios() {
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
	public Usuario buscarPorLogin(String login) {
		return getRepository().findByLogin(login);
	}
	
	@Override
	public List<Usuario> buscarTodosPorLogin(String login) {
		return getRepository().findAllByLogin(login);
	}
	
	@Override
	public Usuario buscarPorTokenID(String token) {
		return getRepository().findByTokenID(token);
	}
	
	@Override
	public void gerarTokenID(Usuario usuario) {
		Usuario u = getRepository().findByLogin(usuario.getLogin());
		u.setTokenID(Util.SHA256(new Date().toString()));
		getRepository().update(u);
	}
}