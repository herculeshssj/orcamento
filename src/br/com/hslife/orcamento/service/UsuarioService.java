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

package br.com.hslife.orcamento.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.component.EmailComponent;
import br.com.hslife.orcamento.component.OpcaoSistemaComponent;
import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Identidade;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoUsuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.repository.BancoRepository;
import br.com.hslife.orcamento.repository.CategoriaRepository;
import br.com.hslife.orcamento.repository.FavorecidoRepository;
import br.com.hslife.orcamento.repository.IdentidadeRepository;
import br.com.hslife.orcamento.repository.MeioPagamentoRepository;
import br.com.hslife.orcamento.repository.MoedaRepository;
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
	private IdentidadeRepository identidadeRepository;
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private FavorecidoRepository favorecidoRepository;
	
	@Autowired
	private MeioPagamentoRepository meioPagamentoRepository;
	
	@Autowired
	private MoedaRepository moedaRepository;
	
	@Autowired
	private OpcaoSistemaComponent opcaoSistemaComponent;

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
	
	public void setIdentidadeRepository(IdentidadeRepository identidadeRepository) {
		this.identidadeRepository = identidadeRepository;
	}

	public void setBancoRepository(BancoRepository bancoRepository) {
		this.bancoRepository = bancoRepository;
	}

	public void setCategoriaRepository(CategoriaRepository categoriaRepository) {
		this.categoriaRepository = categoriaRepository;
	}

	public void setFavorecidoRepository(FavorecidoRepository favorecidoRepository) {
		this.favorecidoRepository = favorecidoRepository;
	}

	public void setMeioPagamentoRepository(
			MeioPagamentoRepository meioPagamentoRepository) {
		this.meioPagamentoRepository = meioPagamentoRepository;
	}

	public void setMoedaRepository(MoedaRepository moedaRepository) {
		this.moedaRepository = moedaRepository;
	}

	@Override
	public void excluir(Usuario entity) throws BusinessException {
		// Conta os registros de atividade do usuário.
		long totalAtividade = 0;
		Map<String, Long> atividadeUsuarioMap = this.buscarAtividadeUsuario(entity);
		atividadeUsuarioMap.remove("IDENTIDADE");
		atividadeUsuarioMap.remove("OPCAO_SISTEMA");
		for (Long valor : atividadeUsuarioMap.values()) {
			totalAtividade += valor;
		}
		
		// Verifica se o total é igual a 6. Se for, não é possível excluir, caso contrário exclui os
		// registros que faltam e logo em seguida exclui o usuário.
		if (totalAtividade <= 6) {
			// Exclui os documentos de identidade do usuário
			for (Identidade identidade : identidadeRepository.findByUsuario(entity)) {
				identidadeRepository.delete(identidade);
			}
		
			// Exclui as opções do sistema do usuário
			opcaoSistemaComponent.excluirOpcoesUsuario(entity);
			
			// Exclui o banco padrão
			if (bancoRepository.findDefaultByUsuario(entity) == null)
				System.out.println("Não existe banco padrão.");
			else 				
				bancoRepository.delete(bancoRepository.findDefaultByUsuario(entity));
			
			// Exclui o favorecido padrão
			if (favorecidoRepository.findDefaultByUsuario(entity) == null)
				System.out.println("Não existe favorecido padrão.");
			else
				favorecidoRepository.delete(favorecidoRepository.findDefaultByUsuario(entity));
				
			// Exclui o meio de pagamento padrão
			if (meioPagamentoRepository.findDefaultByUsuario(entity) == null)
				System.out.println("Não existe meio de pagamento padrão.");
			else
				meioPagamentoRepository.delete(meioPagamentoRepository.findDefaultByUsuario(entity));	
			
			// Exclui a moeda padrão
			if (moedaRepository.findDefaultByUsuario(entity) == null)
				System.out.println("Não existe moeda padrão.");
			else
				moedaRepository.delete(moedaRepository.findDefaultByUsuario(entity));
			
			// Exclui as categoria
			if (categoriaRepository.findDefaultByTipoCategoriaAndUsuario(entity, TipoCategoria.CREDITO) == null 
					&& categoriaRepository.findDefaultByTipoCategoriaAndUsuario(entity, TipoCategoria.DEBITO) == null)
				System.out.println("Não existem categorias padrão.");
			else {
				categoriaRepository.delete(categoriaRepository.findDefaultByTipoCategoriaAndUsuario(entity, TipoCategoria.CREDITO));
				categoriaRepository.delete(categoriaRepository.findDefaultByTipoCategoriaAndUsuario(entity, TipoCategoria.DEBITO));
			}								
			
			// Exclui o usuário
			super.excluir(entity);
			
			// Envia mensagem de e-mail avisando da exclusão da conta
			StringBuilder mensagemEmail = new StringBuilder();
			
			mensagemEmail.append("Prezado " + entity.getNome() + "\n\n");
			mensagemEmail.append("Sua conta foi excluída em virtude de inatividade no sistema.\n\n");
			mensagemEmail.append("Caso queira voltar a utilizar o sistema, por favor efetuar novamente seu registro.\n\n");
			mensagemEmail.append("Atenciosamente,\n\n");
			mensagemEmail.append("Administrador do Sistema.\n");
			
			try {
				emailComponent.setDestinatario(entity.getNome());
				emailComponent.setEmailDestinatario(entity.getEmail());
				emailComponent.setAssunto("Orçamento Doméstico - Exclusão de conta de usuário");
				emailComponent.setMensagem(mensagemEmail.toString());
				emailComponent.enviarEmail();
			} catch (Exception e) {
				e.printStackTrace();				
			}
		}
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
				entity.setSenha(Util.SHA256(confirmaSenha));
				getRepository().save(entity);
				opcaoSistemaComponent.setarOpcoesPadraoUsuario(entity);
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
			if (u.getSenha().equals(Util.SHA256(confirmaSenha))) {
				throw new BusinessException("Nova senha não pode ser igual a senha atual!");
			} else 
				entity.setSenha(Util.SHA256(confirmaSenha));			
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
		String senha = hash.substring(0, 16);
		
		// invoca o método de cadastrar usuário
		this.cadastrar(entity, senha, senha);
		
		// Efetua o envio do e-mail com a senha
		StringBuilder mensagemEmail = new StringBuilder();
		
		mensagemEmail.append("Prezado " + entity.getNome() + "\n\n");
		mensagemEmail.append("Segue abaixo a senha gerada:\n\n");
		mensagemEmail.append("Senha: " + senha + "\n\n");
		mensagemEmail.append("Caso tenha dificuldade de acesso entre em contato com o administrador para alterar sua senha.\n\n\n");
		mensagemEmail.append("Administrador do Sistema");
		
		try {
			emailComponent.setDestinatario(entity.getNome());
			emailComponent.setEmailDestinatario(entity.getEmail());
			emailComponent.setAssunto("Orçamento Doméstico - Registro de Usuário");
			emailComponent.setMensagem(mensagemEmail.toString());
			emailComponent.enviarEmail();
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
			// Antes de proceder com a alteração da senha, verifica se o usuário não se encontra inativo no sistema
			if (!u.isAtivo()) {
				// Envia mensagem de e-mail avisando que a conta está inativa
				StringBuilder mensagemEmail = new StringBuilder();
				
				mensagemEmail.append("Prezado " + u.getNome() + "\n\n");
				mensagemEmail.append("Sua conta encontra-se inativa.\n\n");
				mensagemEmail.append("Caso queira voltar a utilizar o sistema e acessar novamente seus dados, por favor entre em contato.\n\n");
				mensagemEmail.append("Atenciosamente,\n\n");
				mensagemEmail.append("Administrador do Sistema.\n");
				
				try {
					emailComponent.setDestinatario(u.getNome());
					emailComponent.setEmailDestinatario(u.getEmail());
					emailComponent.setAssunto("Orçamento Doméstico - Conta inativa");
					emailComponent.setMensagem(mensagemEmail.toString());
					emailComponent.enviarEmail();
				} catch (Exception e) {
					e.printStackTrace();
					throw new BusinessException(e);
				}
				return;
			}
			
			
			// Gera hash da data atual para extrair a senha
			String hash = Util.MD5((new Date().toString()));
			
			// Extrai do hash a senha aleatória do usuário
			String senha = hash.substring(0, 16);
			
			// invoca o método de alterar usuário
			this.alterar(u, senha, senha);
			
			// Efetua o envio do e-mail com a senha
			StringBuilder mensagemEmail = new StringBuilder();
			
			mensagemEmail.append("Prezado " + u.getNome() + "\n\n");
			mensagemEmail.append("Segue abaixo a senha gerada:\n\n");
			mensagemEmail.append("Senha: " + senha + "\n\n");
			mensagemEmail.append("Caso tenha dificuldade de acesso entre em contato com o administrador para alterar sua senha.\n\n\n");
			mensagemEmail.append("Administrador do Sistema");
			
			try {
				emailComponent.setDestinatario(u.getNome());
				emailComponent.setEmailDestinatario(u.getEmail());
				emailComponent.setAssunto("Orçamento Doméstico - Recuperação de senha");
				emailComponent.setMensagem(mensagemEmail.toString());
				emailComponent.enviarEmail();
			} catch (Exception e) {
				throw new BusinessException(e);
			}
		} else {
			throw new BusinessException("E-Mail informado não confere com o cadastrado!");
		}		
	}
	
	public void enviarMensagemParaAdmin(String assuntoMensagem, String mensagem) throws BusinessException {
		try {
			Usuario admin = getRepository().findByLogin("admin");
			emailComponent.setRemetente(getComponent().getUsuarioLogado().getNome());
			emailComponent.setEmailRemetente(getComponent().getUsuarioLogado().getEmail());
			emailComponent.setDestinatario(admin.getNome());
			emailComponent.setEmailDestinatario(admin.getEmail());
			emailComponent.setAssunto(assuntoMensagem);
			emailComponent.setMensagem(mensagem.toString());
			emailComponent.enviarEmail();
		} catch (Exception e) {
			throw new BusinessException("Erro ao enviar e-mail:", e);
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
	public Map<String, Long> buscarAtividadeUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findUserActivity(usuario);
	}
	
	
}