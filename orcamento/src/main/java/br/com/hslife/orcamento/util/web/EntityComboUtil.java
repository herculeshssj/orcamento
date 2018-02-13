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

***/package br.com.hslife.orcamento.util.web;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.component.OpcaoSistemaComponent;
import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Banco;
import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IBanco;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IMoeda;

@Component("entityCombo")
@Scope("session")
public class EntityComboUtil {

	@Autowired
	private IBanco bancoService;
	
	@Autowired
	private ICategoria categoriaService;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private UsuarioComponent usuarioComponent;
	
	@Autowired
	private OpcaoSistemaComponent opcaoSistemaComponent;
	
	public List<Banco> getListaBanco() {
		try {
			return bancoService.buscarTodos();
		} catch (BusinessException | ValidationException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<Banco>();
	}
	
	public List<Categoria> getListaCategoria() {
		try {
			return categoriaService.buscarPorUsuario(usuarioComponent.getUsuarioLogado());
		} catch (BusinessException | ValidationException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<Categoria> getListaCategoriaCredito() {
		try {
			return categoriaService.buscarPorTipoCategoriaEUsuario(TipoCategoria.CREDITO, usuarioComponent.getUsuarioLogado());
		} catch (BusinessException | ValidationException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<Categoria> getListaCategoriaDebito() {
		try {
			return categoriaService.buscarPorTipoCategoriaEUsuario(TipoCategoria.DEBITO, usuarioComponent.getUsuarioLogado());
		} catch (BusinessException | ValidationException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<Conta> getListaConta() {
		try {
			if (opcaoSistemaComponent.getExibirContasInativas())
				return contaService.buscarPorUsuario(usuarioComponent.getUsuarioLogado());
			else
				return contaService.buscarAtivosPorUsuario(usuarioComponent.getUsuarioLogado());
		} catch (BusinessException | ValidationException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<Conta> getListaContaCartao() {
		try {
			if (opcaoSistemaComponent.getExibirContasInativas())
				return contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario(null, new TipoConta[]{TipoConta.CARTAO}, usuarioComponent.getUsuarioLogado(), null);
			else
				return contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario(null, new TipoConta[]{TipoConta.CARTAO}, usuarioComponent.getUsuarioLogado(), true);
		} catch (BusinessException | ValidationException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<Favorecido> getListaFavorecido() {
		try {
			return favorecidoService.buscarPorUsuario(usuarioComponent.getUsuarioLogado());
		} catch (BusinessException | ValidationException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<MeioPagamento> getListaMeioPagamento() {
		try {
			return meioPagamentoService.buscarPorUsuario(usuarioComponent.getUsuarioLogado());
		} catch (BusinessException | ValidationException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<Moeda> getListaMoeda() {
		try {
			return moedaService.buscarPorUsuario(usuarioComponent.getUsuarioLogado());
		} catch (BusinessException | ValidationException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<Moeda>();
	}
}
