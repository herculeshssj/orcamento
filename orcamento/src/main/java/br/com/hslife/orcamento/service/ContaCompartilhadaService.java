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

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth

	Floor, Boston, MA  02110-1301, USA.


	Para mais informações sobre o programa Orçamento Doméstico e seu autor

	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

	para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/package br.com.hslife.orcamento.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.component.EmailComponent;
import br.com.hslife.orcamento.entity.ContaCompartilhada;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IContaCompartilhada;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.repository.ContaCompartilhadaRepository;

@Service("contaCompartilhadaService")
@Transactional(propagation=Propagation.REQUIRED, rollbackFor={ApplicationException.class})
public class ContaCompartilhadaService implements IContaCompartilhada {
	
	private static final Logger logger = LogManager.getLogger(ContaCompartilhadaService.class);
	
	@Autowired
	public SessionFactory sessionFactory;
	
	@Autowired
	private ContaCompartilhadaRepository repository;
	
	@Autowired
	private IUsuario usuarioService;
	
	@Autowired
	private EmailComponent emailComponent;

	public ContaCompartilhadaRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}

	public IUsuario getUsuarioService() {
		return usuarioService;
	}

	public EmailComponent getEmailComponent() {
		return emailComponent;
	}

	@Override
	public void compartilharConta(ContaCompartilhada conta) throws ApplicationException {
		// Verifica se o usuário informado existe e se o e-mail informado é
		// idêntico ao cadastrado
		Usuario u = getUsuarioService().buscarPorLogin(conta.getLoginUsuario());
		
		if (u == null) {
			throw new BusinessException("Usuário inexistente!");
		} else if (!u.getEmail().equalsIgnoreCase(conta.getEmailUsuario())) {
			throw new BusinessException("Usuário inexistente!");
		} else if (u.getLogin().equalsIgnoreCase(conta.getConta().getUsuario().getLogin())) {
			throw new BusinessException("Você já é dono desta conta!");
		}
		
		// Seta o usuário
		conta.setUsuario(u);
		
		// Gera o hash do compartilhamento e salva os dados na base
		conta.gerarHash();
		getRepository().save(conta);
		
		// Envia e-mail para o usuário informado do compartilhamento

		StringBuilder mensagemEmail = new StringBuilder();
		
		mensagemEmail.append("Prezado " + u.getNome() + "\n\n");
		mensagemEmail.append("O usuário " + conta.getConta().getUsuario().getNome() +  " compartilhou as informações da seguinte conta:\n\n");
		mensagemEmail.append(conta.getConta().getLabel() + " \n\n");
		mensagemEmail.append("Este compartilhamento dá acesso a visualizar os dados da referida conta nas seguintes funcionalidades: \n\n");
		mensagemEmail.append("- Panorama dos Cadastros;\n\n");
		mensagemEmail.append("- Panorama dos Lançamentos da Conta;\n\n");
		mensagemEmail.append("- Panorama dos Lançamentos do Cartão;\n\n");
		mensagemEmail.append("- Resumo Mensal das Contas;\n\n");
		mensagemEmail.append("- Saldo Atual das Contas.\n\n");
		mensagemEmail.append("Para confirmar a operação, acesse o link abaixo em um navegador Web de sua preferência: \n\n");
		mensagemEmail.append("https://hslife.com.br/confirmar.faces?operacao=compartilhamento&hash=" + conta.getHashAutorizacao() + "\n\n");
		mensagemEmail.append("Caso não deseje acessar os dados desta conta, ignore esta mensagem.\n\n\n");
		mensagemEmail.append("Administrador do Sistema");
		
		try {
			getEmailComponent().setDestinatario(u.getNome());
			getEmailComponent().setEmailDestinatario(u.getEmail());
			getEmailComponent().setAssunto("Orçamento Doméstico - Compartilhamento de Conta");
			getEmailComponent().setMensagem(mensagemEmail.toString());
			getEmailComponent().enviarEmail();
		} catch (Exception e) {
			logger.catching(e);
			e.printStackTrace();
			throw new ApplicationException(e);
		}
	}
	
	@Override
	public void habilitarCompartilhamento(String hash) {
		// Verifica se o hash existe
		ContaCompartilhada conta = getRepository().findByHash(hash);
		
		if (conta == null) {
			throw new BusinessException("Este link é inválido ou expirou!");
		}
		
		// Verifica se o hash ainda é válido
		if (conta.isHashExpirado()) {
			throw new BusinessException("Este link expirou! Solicite um novo convite ao usuário " + conta.getUsuario().getNome());
		}
		
		// Atualiza os dados da conta
		conta.setDataGeracaoHash(null);
		conta.setHashAutorizacao(null);
		getRepository().update(conta);
	}
	
	public List<ContaCompartilhada> buscarTodosPorUsuarioLogado(Usuario usuarioLogado) {
		return getRepository().findAllByUsuarioLogado(usuarioLogado);
	}
	
	public List<ContaCompartilhada> buscarTodosPorUsuario(Usuario usuario) {
		return getRepository().findAllByUsuario(usuario);
	}
	
	@Override
	public void reenviarConvite(ContaCompartilhada conta) throws ApplicationException {
		ContaCompartilhada contaCompartilhada = getRepository().findById(conta.getId());
		Usuario u = getUsuarioService().buscarPorID(contaCompartilhada.getUsuario().getId());
		
		// Gera o novo hash para o compartilhamento
		contaCompartilhada.gerarHash();
		
		// Salva na base
		getRepository().update(contaCompartilhada);
		
		// Envia e-mail para o usuário informado do compartilhamento

		StringBuilder mensagemEmail = new StringBuilder();
		
		mensagemEmail.append("Prezado " + u.getNome() + "\n\n");
		mensagemEmail.append("O usuário " + conta.getConta().getUsuario().getNome() +  " compartilhou as informações da seguinte conta:\n\n");
		mensagemEmail.append(conta.getConta().getLabel() + " \n\n");
		mensagemEmail.append("Este compartilhamento dá acesso a visualizar os dados da referida conta nas seguintes funcionalidades: \n\n");
		mensagemEmail.append("- Panorama dos Cadastros;\n\n");
		mensagemEmail.append("- Panorama dos Lançamentos da Conta;\n\n");
		mensagemEmail.append("- Panorama dos Lançamentos do Cartão;\n\n");
		mensagemEmail.append("- Resumo Mensal das Contas;\n\n");
		mensagemEmail.append("- Saldo Atual das Contas.\n\n");
		mensagemEmail.append("Para confirmar a operação, acesse o link abaixo em um navegador Web de sua preferência: \n\n");
		mensagemEmail.append("https://hslife.com.br/confirmar.faces?operacao=compartilhamento&hash=" + conta.getHashAutorizacao() + "\n\n");
		mensagemEmail.append("Caso não deseje acessar os dados desta conta, ignore esta mensagem.\n\n\n");
		mensagemEmail.append("Administrador do Sistema");
		
		try {
			getEmailComponent().setDestinatario(u.getNome());
			getEmailComponent().setEmailDestinatario(u.getEmail());
			getEmailComponent().setAssunto("Orçamento Doméstico - Compartilhamento de Conta");
			getEmailComponent().setMensagem(mensagemEmail.toString());
			getEmailComponent().enviarEmail();
		} catch (Exception e) {
			logger.catching(e);
			e.printStackTrace();
			throw new ApplicationException(e);
		}		
	}
	
	@Override
	public void excluirCompartilhamento(ContaCompartilhada conta) {
		ContaCompartilhada contaCompartilhada = getRepository().findById(conta.getId());
		getRepository().delete(contaCompartilhada);
	}
}
