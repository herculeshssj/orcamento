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

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.NotificacaoSistema;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.facade.INotificacaoSistema;
import br.com.hslife.orcamento.facade.IUsuario;

@Component
@Scope("session")
public class DashboardController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7392773085395093554L;
	
	@Autowired
	private IUsuario usuarioService;

	@Autowired
	private INotificacaoSistema notificacaoSistemaService;
	
	private String assuntoMensagem;
	private String mensagem;
	private NotificacaoSistema notificacaoSelecionada;
	private int quantNotificacoes;

	@Override
	protected void initializeEntity() {
		assuntoMensagem = "";
		mensagem = "";
	}

	@Override
	public String startUp() {
		return null;
	}

	@PostConstruct
	public List<NotificacaoSistema> getListaNotificacaoSistema() {
		quantNotificacoes = 0;

		List<NotificacaoSistema> listaNotificacoes = getNotificacaoSistemaService().buscarTodosPorUsuario(getUsuarioLogado());

		for (NotificacaoSistema notificacao : listaNotificacoes)
			if (!notificacao.isVisualizado())
				quantNotificacoes++;

		return listaNotificacoes;
	}

	public String verNotificacao() {
		getNotificacaoSistemaService().marcarComoVisualizado(notificacaoSelecionada);
		return "/pages/menu/verNotificacao";
	}

	public void eliminarNotificacoesVisualizadas() {
		getNotificacaoSistemaService().eliminarNotificacoesVisualizadas(getUsuarioLogado());
		infoMessage("Notificações antigas excluídas com sucesso!");
	}

	public void enviarMensagem() {
		try {
			if (assuntoMensagem.isEmpty() || mensagem.isEmpty()) {
				warnMessage("Informe o assunto e o texto da mensagem!");
			} else {			
				usuarioService.enviarMensagemParaAdmin(assuntoMensagem, mensagem);
				infoMessage("Mensagem enviada com sucesso!");
				initializeEntity();			
			}
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
	}

	public String getAssuntoMensagem() {
		return assuntoMensagem;
	}

	public void setAssuntoMensagem(String assuntoMensagem) {
		this.assuntoMensagem = assuntoMensagem;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public INotificacaoSistema getNotificacaoSistemaService() {
		return notificacaoSistemaService;
	}


	public NotificacaoSistema getNotificacaoSelecionada() {
		return notificacaoSelecionada;
	}

	public void setNotificacaoSelecionada(NotificacaoSistema notificacaoSelecionada) {
		this.notificacaoSelecionada = notificacaoSelecionada;
	}

	public int getQuantNotificacoes() {
		return quantNotificacoes;
	}

	public void setQuantNotificacoes(int quantNotificacoes) {
		this.quantNotificacoes = quantNotificacoes;
	}
}
