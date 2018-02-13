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

***/
package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.EntityPersistence;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.enumeration.Container;
import br.com.hslife.orcamento.enumeration.PeriodoLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.ILancamentoPeriodico;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;

@Component("lancamentoPeriodicoMB")
@Scope("session")
public class LancamentoPeriodicoController extends AbstractCRUDController<LancamentoPeriodico>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4809986079850307573L;
	
	@Autowired
	private ILancamentoPeriodico service;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private ICategoria categoriaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;
	
	@Autowired
	private ILancamentoConta lancamentoContaService;
	
	private Conta contaSelecionada;
	private LancamentoConta pagamentoPeriodo;
	private Moeda moedaPadrao;
	private StatusLancamento statusLancamento;
	private TipoCategoria tipoCategoriaSelecionada;
	private TipoLancamentoPeriodico tipoLancamentoPeriodico;
	private boolean parcelamento;
	private boolean selecionarTodosLancamentos;
	private boolean gerarLancamento;
	private CriterioBuscaLancamentoConta criterioBusca;
	private List<LancamentoConta> lancamentosEncontrados;
	private LancamentoConta lancamentoSelecionado;
	private LancamentoConta[] lancamentosSelecionados;
	private String tipoSelecao;
	private Categoria categoriaSelecionada;
	private Favorecido favorecidoSelecionado;
	private MeioPagamento meioPagamentoSelecionado;
	
	public LancamentoPeriodicoController() {
		super(new LancamentoPeriodico());

		moduleTitle = "Despesas fixas e parceladas";
	}

	@Override
	protected void initializeEntity() {
		entity = new LancamentoPeriodico();
		listEntity = new ArrayList<LancamentoPeriodico>();
		pagamentoPeriodo = new LancamentoConta();
		categoriaSelecionada = null;
		favorecidoSelecionado = null;
		meioPagamentoSelecionado = null;
	}
	
	@Override
	public void find() {
		try {
			listEntity = getService().buscarPorTipoLancamentoContaEStatusLancamento(tipoLancamentoPeriodico, contaSelecionada, statusLancamento);
			this.carregarMoedaPadrao();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String save() {
		if (!entity.getConta().getTipoConta().equals(TipoConta.CARTAO)) {
			entity.setMoeda(entity.getConta().getMoeda());
		}
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}
	
	@Override
	public String edit() {
		String goToPage = super.edit();
		if (goToPage.equals(goToFormPage)) {
			atualizaComboCategorias();
			atualizaPainelCadastro();
		}
		return goToPage;
	}
	
	private String alterarStatus(StatusLancamento novoStatus) {
		try {
			// Altera o status da entidade
			getService().alterarStatusLancamento(entity, novoStatus);
			
			infoMessage("Status alterado com sucesso!");
			
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
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String suspenderLancamento() {
		return this.alterarStatus(StatusLancamento.SUSPENSO);
	}
	
	public String ativarLancamento() {
		return this.alterarStatus(StatusLancamento.ATIVO);
	}
	
	public String encerrarLancamento() {
		return this.alterarStatus(StatusLancamento.ENCERRADO);
	}
	
	public String reativarLancamento() {
		return this.alterarStatus(StatusLancamento.ATIVO);
	}
	
	public String registrarPagamentoView() {
		try {
			entity = getService().buscarPorID(idEntity);
			actionTitle = " - Registrar pagamento";
			gerarLancamento = false;
			return "/pages/LancamentoPeriodico/registrarPagamento";
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String registrarPagamento() {
		try {
			getService().registrarPagamento(pagamentoPeriodo);
			infoMessage("Pagamento registrado com sucesso!");
			pagamentoPeriodo = new LancamentoConta();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public void gerarMensalidade() {
		try {
			getService().gerarMensalidade(entity);
			infoMessage("Mensalidade gerada com sucesso!");
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String vincularLancamentoView() {
		lancamentosEncontrados = new ArrayList<LancamentoConta>();
		criterioBusca = new CriterioBuscaLancamentoConta();
		actionTitle = " - Vincular Lançamento";
		tipoSelecao = "single";
		return "/pages/LancamentoPeriodico/vincularLancamentoConta";
	}
	
	public String vincularNovoLancamentoView() {
		lancamentosEncontrados = new ArrayList<LancamentoConta>();
		criterioBusca = new CriterioBuscaLancamentoConta();
		pagamentoPeriodo = null;
		actionTitle = " - Vincular Lançamento";
		tipoSelecao = "multiple";
		return "/pages/LancamentoPeriodico/vincularLancamentoConta";
	}
	
	public String vincularLancamento() {
		try {
			if (this.tipoSelecao.equals("single")) {
				if (lancamentoSelecionado == null) {
					warnMessage("Selecione um lançamento para vincular!");
					return "";
				}
			} else if (this.tipoSelecao.equals("multiple")) {
				if (lancamentosSelecionados == null || lancamentosSelecionados.length == 0) {
					warnMessage("Selecione pelo menos um lançamento para vincular!");
					return "";
				}
			} else {
				throw new ApplicationException("Opção inválida!");
			}
			
			if (this.tipoSelecao.equals("single")) {
				getService().mesclarLancamentos(pagamentoPeriodo, lancamentoSelecionado);
			} else if (this.tipoSelecao.equals("multiple")) {
				getService().vincularLancamentos(entity, Arrays.asList(lancamentosSelecionados));
			} else {
				throw new ApplicationException("Opção inválida!");
			}
			infoMessage("Vínculo realizado com sucesso!");
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
		return super.edit();
	}
	
	public String removerVinculoLancamentoView() {
		lancamentosEncontrados = entity.getPagamentos();
		lancamentosSelecionados = null;
		actionTitle = " - Remover Vínculos";
		return "/pages/LancamentoPeriodico/removerVinculoLancamento";
	}
	
	public String removerVinculoLancamento() {
		try {
			if (lancamentosSelecionados == null || lancamentosSelecionados.length == 0) {
				warnMessage("Selecione pelo menos um lançamento para remover!");
				return "";
			} else {
				getService().removerLancamentos(Arrays.asList(lancamentosSelecionados));
			}
			infoMessage("Vínculos removidos com sucesso!");
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return super.edit();
	}
	
	public String reclassificarLancamentoView() {
		actionTitle = "- Reclassificar";
		return "/pages/LancamentoPeriodico/reclassificarLancamento";
	}
	
	public String reclassificarLancamento() {
		try {
			Map<String, ? super EntityPersistence> parametros = new HashMap<>();
			parametros.put("CATEGORIA", categoriaSelecionada);
			parametros.put("FAVORECIDO", favorecidoSelecionado);
			parametros.put("MEIOPAGAMENTO", meioPagamentoSelecionado);
			getService().reclassificarLancamento(entity, parametros);
			infoMessage("Lançamento reclassificado com sucesso!");
			return list();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String retornarPaginaEdicao() {
		return super.edit();
	}
	
	public void pesquisarLancamento() {
		try {
			criterioBusca.setConta(entity.getConta());
			lancamentosEncontrados.clear();
			lancamentosEncontrados.addAll(lancamentoContaService.buscarPorCriterioBusca(criterioBusca));
			
			// Remove os lançamentos que já estão vinculados a outros lançamentos periódicos
			for (Iterator<LancamentoConta> i = lancamentosEncontrados.iterator(); i.hasNext(); ) {
				if (i.next().getLancamentoPeriodico() != null) {
					i.remove();
				}
			}
			
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	private void carregarMoedaPadrao() {
		try {
			if (moedaPadrao == null) {
				moedaPadrao = moedaService.buscarPadraoPorUsuario(getUsuarioLogado());
			}
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void carregarArquivo(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (entity.getArquivo() == null) entity.setArquivo(new Arquivo());
			entity.getArquivo().setDados(event.getFile().getContents());
			entity.getArquivo().setNomeArquivo(event.getFile().getFileName().replace(" ", "."));
			entity.getArquivo().setContentType(event.getFile().getContentType());
			entity.getArquivo().setTamanho(event.getFile().getSize());		
			entity.getArquivo().setContainer(Container.LANCAMENTOPERIODICO);
			entity.getArquivo().setUsuario(getUsuarioLogado());
			entity.getArquivo().setAttribute("arquivo");
		} 
	}
	
	public void baixarArquivo() {
		if (entity.getArquivo() == null || entity.getArquivo().getDados() == null || entity.getArquivo().getDados().length == 0) {
			warnMessage("Nenhum arquivo adicionado!");
		} else {
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			try {			
				response.setContentType(entity.getArquivo().getContentType());
				response.setHeader("Content-Disposition","attachment; filename=" + entity.getArquivo().getNomeArquivo());
				response.setContentLength(entity.getArquivo().getDados().length);
				ServletOutputStream output = response.getOutputStream();
				output.write(entity.getArquivo().getDados(), 0, entity.getArquivo().getDados().length);
				FacesContext.getCurrentInstance().responseComplete();
			} catch (Exception e) {
				errorMessage(e.getMessage());
			}
		}
	}
	
	public void excluirArquivo() {
		if (entity.getArquivo() == null || entity.getArquivo().getDados() == null || entity.getArquivo().getDados().length == 0) {
			warnMessage("Nenhum arquivo adicionado!");
		} else {
			entity.setArquivo(null);
			infoMessage("Arquivo excluído! Salve para confirmar as alterações.");
		}
	}
	
	public void atualizaPainelCadastro() {
		if (entity.getTipoLancamentoPeriodico() != null && entity.getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.FIXO)) {
			parcelamento = false;
		} else {
			parcelamento = true;
		}
	}
	
	public void atualizaComboCategorias() {
		if (entity.getTipoLancamento() != null) {
			if (entity.getTipoLancamento().equals(TipoLancamento.RECEITA)) {
				tipoCategoriaSelecionada = TipoCategoria.CREDITO;
			} else {
				tipoCategoriaSelecionada = TipoCategoria.DEBITO;
			}
		}
	}
	
	public void carregarPagamentoPeriodo() {
		// método criado para carregar os dados do pagamento do período
	}
	
	public List<LancamentoConta> getListaPagamentoPeriodo() {
		try {
			return lancamentoContaService.buscarPagamentosNaoPagosPorLancamentoPeriodico(entity);
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public double getTotalAPagar() {
		double valor = 0.0;
		if (entity.getPagamentos() != null && entity.getPagamentos().size() > 0) {
			for (LancamentoConta pagamento : entity.getPagamentos()) {
				if (!pagamento.getStatusLancamentoConta().equals(StatusLancamentoConta.QUITADO)) {
					valor += entity.getValorParcela();
				}
			}
		}
		return valor;
	}
	
	public double getTotalPago() {
		double valor = 0.0;
		if (entity.getPagamentos() != null && entity.getPagamentos().size() > 0) {
			for (LancamentoConta pagamento : entity.getPagamentos()) {
				if (pagamento.getStatusLancamentoConta().equals(StatusLancamentoConta.QUITADO)) {
					valor += pagamento.getValorPago();
				}
			}
		}
		return valor;
	}
	
	private List<LancamentoPeriodico> buscarTodosAtivos(TipoLancamentoPeriodico tipo) {
		try {
			return getService().buscarPorTipoLancamentoEStatusLancamentoPorUsuario(tipo, StatusLancamento.ATIVO, getUsuarioLogado());
		} catch (BusinessException | ValidationException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public double getTotalFixoReceita() {
		double valor = 0.0;
		
		for (LancamentoPeriodico lancamento : this.buscarTodosAtivos(TipoLancamentoPeriodico.FIXO)) {
			if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA)) {
				if (lancamento.getMoeda().equals(getOpcoesSistema().getMoedaPadrao())) {
					valor += lancamento.getValorParcela();
				} else {
					valor += lancamento.getValorParcela() * lancamento.getMoeda().getValorConversao();
				}
			}
		}
		
		return valor;
	}
	
	public double getTotalFixoDespesa() {
		double valor = 0.0;
		
		for (LancamentoPeriodico lancamento : this.buscarTodosAtivos(TipoLancamentoPeriodico.FIXO)) {
			if (lancamento.getTipoLancamento().equals(TipoLancamento.DESPESA)) {
				if (lancamento.getMoeda().equals(getOpcoesSistema().getMoedaPadrao())) {
					valor += lancamento.getValorParcela();
				} else {
					valor += lancamento.getValorParcela() * lancamento.getMoeda().getValorConversao();
				}
			}
		}
		
		return valor;
	}
	
	public double getTotalParceladoReceita() {
		double valor = 0.0;
		
		for (LancamentoPeriodico lancamento : this.buscarTodosAtivos(TipoLancamentoPeriodico.PARCELADO)) {
			if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA)) {
				if (lancamento.getMoeda().equals(getOpcoesSistema().getMoedaPadrao())) {
					valor += lancamento.getValorParcela();
				} else {
					valor += lancamento.getValorParcela() * lancamento.getMoeda().getValorConversao();
				}
			}
		}
		
		return valor;
	}
	
	public double getTotalParceladoDespesa() {
		double valor = 0.0;
		
		for (LancamentoPeriodico lancamento : this.buscarTodosAtivos(TipoLancamentoPeriodico.PARCELADO)) {
			if (lancamento.getTipoLancamento().equals(TipoLancamento.DESPESA)) {
				if (lancamento.getMoeda().equals(getOpcoesSistema().getMoedaPadrao())) {
					valor += lancamento.getValorParcela();
				} else {
					valor += lancamento.getValorParcela() * lancamento.getMoeda().getValorConversao();
				}
			}
		}
		
		return valor;
	}
	
	public double getTotalReceita() {
		return this.getTotalFixoReceita() + this.getTotalParceladoReceita();
	}
	
	public double getTotalDespesa() {
		return this.getTotalFixoDespesa() + this.getTotalParceladoDespesa();
	}
	
	public double getPercentualOrcamentoComprometido() {
		return ( this.getTotalDespesa() / (this.getTotalReceita() == 0 ? 1 : this.getTotalReceita()) ) * 100;
	}
	
	public List<Conta> getListaConta() {
		try {
			if (getOpcoesSistema().getExibirContasInativas()) {
				return contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", new TipoConta[]{TipoConta.CORRENTE, TipoConta.POUPANCA, TipoConta.OUTROS, TipoConta.CARTAO}, getUsuarioLogado(), null);
			} else {
				return contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", new TipoConta[]{TipoConta.CORRENTE, TipoConta.POUPANCA, TipoConta.OUTROS, TipoConta.CARTAO}, getUsuarioLogado(), true);
			}
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public List<Categoria> getListaCategoria() {
		try {
			 
			if (tipoCategoriaSelecionada != null) {
				List<Categoria> resultado = categoriaService.buscarAtivosPorTipoCategoriaEUsuario(tipoCategoriaSelecionada, getUsuarioLogado());				
				// Lógica para incluir a categoria inativa da entidade na combo
				if (resultado != null && entity.getCategoria() != null) {
					if (!resultado.contains(entity.getCategoria())) {
						entity.getCategoria().setAtivo(true);
						resultado.add(entity.getCategoria());
					}
				}
				return resultado;
			}
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		} 
		return new ArrayList<Categoria>();
	}
	
	public List<Favorecido> getListaFavorecido() {
		try {
			List<Favorecido> resultado = favorecidoService.buscarAtivosPorUsuario(getUsuarioLogado());
			// Lógica para incluir o favorecido inativo da entidade na combo
			if (resultado != null && entity.getFavorecido() != null) {
				if (!resultado.contains(entity.getFavorecido())) {
					entity.getFavorecido().setAtivo(true);
					resultado.add(entity.getFavorecido());
				}
			}
			return resultado;
		} catch (ValidationException | BusinessException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<MeioPagamento> getListaMeioPagamento() {
		try {
			List<MeioPagamento> resultado = meioPagamentoService.buscarAtivosPorUsuario(getUsuarioLogado());
			// Lógica para incluir o favorecido inativo da entidade na combo
			if (resultado != null && entity.getMeioPagamento() != null) {
				if (!resultado.contains(entity.getMeioPagamento())) {
					entity.getMeioPagamento().setAtivo(true);
					resultado.add(entity.getMeioPagamento());
				}
			}
			return resultado;
		} catch (ValidationException | BusinessException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<Moeda> getListaMoeda() {
		try {
			List<Moeda> resultado = moedaService.buscarAtivosPorUsuario(getUsuarioLogado());
			// Lógica para incluir o banco inativo da entidade na combo
			if (resultado != null && entity.getMoeda() != null) {
				if (!resultado.contains(entity.getMoeda())) {
					entity.getMoeda().setAtivo(true);
					resultado.add(entity.getMoeda());
				}
			}
			return resultado;
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<SelectItem> getListaStatusLancamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		listaSelectItem.add(new SelectItem(StatusLancamento.ATIVO, "Ativo"));
		listaSelectItem.add(new SelectItem(StatusLancamento.SUSPENSO, "Suspenso"));
		listaSelectItem.add(new SelectItem(StatusLancamento.ENCERRADO, "Encerrado"));
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaTipoLancamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		listaSelectItem.add(new SelectItem(TipoLancamento.RECEITA, "Receita"));
		listaSelectItem.add(new SelectItem(TipoLancamento.DESPESA, "Despesa"));
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaTipoLancamentoPeriodico() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		listaSelectItem.add(new SelectItem(TipoLancamentoPeriodico.FIXO, "Fixo"));
		listaSelectItem.add(new SelectItem(TipoLancamentoPeriodico.PARCELADO, "Parcelado"));
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaPeriodoLancamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (PeriodoLancamento periodo : PeriodoLancamento.values()) {
			if (!periodo.equals(PeriodoLancamento.FIXO))
				listaSelectItem.add(new SelectItem(periodo, periodo.toString()));
		}
		return listaSelectItem;
	}

	public ILancamentoPeriodico getService() {
		return service;
	}

	public void setService(ILancamentoPeriodico service) {
		this.service = service;
	}

	public Conta getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(Conta contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}

	public StatusLancamento getStatusLancamento() {
		return statusLancamento;
	}

	public void setStatusLancamento(StatusLancamento statusLancamento) {
		this.statusLancamento = statusLancamento;
	}

	public void setContaService(IConta contaService) {
		this.contaService = contaService;
	}

	public void setMoedaService(IMoeda moedaService) {
		this.moedaService = moedaService;
	}

	public boolean isParcelamento() {
		return parcelamento;
	}

	public void setParcelamento(boolean parcelamento) {
		this.parcelamento = parcelamento;
	}

	public TipoCategoria getTipoCategoriaSelecionada() {
		return tipoCategoriaSelecionada;
	}

	public void setTipoCategoriaSelecionada(TipoCategoria tipoCategoriaSelecionada) {
		this.tipoCategoriaSelecionada = tipoCategoriaSelecionada;
	}

	public void setCategoriaService(ICategoria categoriaService) {
		this.categoriaService = categoriaService;
	}

	public void setFavorecidoService(IFavorecido favorecidoService) {
		this.favorecidoService = favorecidoService;
	}

	public void setMeioPagamentoService(IMeioPagamento meioPagamentoService) {
		this.meioPagamentoService = meioPagamentoService;
	}

	public boolean isSelecionarTodosLancamentos() {
		return selecionarTodosLancamentos;
	}

	public void setSelecionarTodosLancamentos(boolean selecionarTodosLancamentos) {
		this.selecionarTodosLancamentos = selecionarTodosLancamentos;
	}

	public TipoLancamentoPeriodico getTipoLancamentoPeriodico() {
		return tipoLancamentoPeriodico;
	}

	public void setTipoLancamentoPeriodico(
			TipoLancamentoPeriodico tipoLancamentoPeriodico) {
		this.tipoLancamentoPeriodico = tipoLancamentoPeriodico;
	}

	public Moeda getMoedaPadrao() {
		return moedaPadrao;
	}

	public void setMoedaPadrao(Moeda moedaPadrao) {
		this.moedaPadrao = moedaPadrao;
	}

	public boolean isGerarLancamento() {
		return gerarLancamento;
	}

	public void setGerarLancamento(boolean gerarLancamento) {
		this.gerarLancamento = gerarLancamento;
	}

	public LancamentoConta getPagamentoPeriodo() {
		return pagamentoPeriodo;
	}

	public void setPagamentoPeriodo(LancamentoConta pagamentoPeriodo) {
		this.pagamentoPeriodo = pagamentoPeriodo;
	}

	public List<LancamentoConta> getLancamentosEncontrados() {
		return lancamentosEncontrados;
	}

	public void setLancamentosEncontrados(
			List<LancamentoConta> lancamentosEncontrados) {
		this.lancamentosEncontrados = lancamentosEncontrados;
	}

	public LancamentoConta[] getLancamentosSelecionados() {
		return lancamentosSelecionados;
	}

	public void setLancamentosSelecionados(LancamentoConta[] lancamentosSelecionados) {
		this.lancamentosSelecionados = lancamentosSelecionados;
	}

	public String getTipoSelecao() {
		return tipoSelecao;
	}

	public void setTipoSelecao(String tipoSelecao) {
		this.tipoSelecao = tipoSelecao;
	}

	public LancamentoConta getLancamentoSelecionado() {
		return lancamentoSelecionado;
	}

	public void setLancamentoSelecionado(LancamentoConta lancamentoSelecionado) {
		this.lancamentoSelecionado = lancamentoSelecionado;
	}

	public CriterioBuscaLancamentoConta getCriterioBusca() {
		return criterioBusca;
	}

	public void setCriterioBusca(CriterioBuscaLancamentoConta criterioBusca) {
		this.criterioBusca = criterioBusca;
	}

	public Categoria getCategoriaSelecionada() {
		return categoriaSelecionada;
	}

	public void setCategoriaSelecionada(Categoria categoriaSelecionada) {
		this.categoriaSelecionada = categoriaSelecionada;
	}

	public Favorecido getFavorecidoSelecionado() {
		return favorecidoSelecionado;
	}

	public void setFavorecidoSelecionado(Favorecido favorecidoSelecionado) {
		this.favorecidoSelecionado = favorecidoSelecionado;
	}

	public MeioPagamento getMeioPagamentoSelecionado() {
		return meioPagamentoSelecionado;
	}

	public void setMeioPagamentoSelecionado(MeioPagamento meioPagamentoSelecionado) {
		this.meioPagamentoSelecionado = meioPagamentoSelecionado;
	}
}
