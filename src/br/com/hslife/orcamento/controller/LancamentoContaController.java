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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import br.com.hslife.orcamento.entity.BuscaSalva;
import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.enumeration.Container;
import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoAgrupamentoBusca;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IBuscaSalva;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IFechamentoPeriodo;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.model.AgrupamentoLancamento;
import br.com.hslife.orcamento.util.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.util.Util;

@Component("lancamentoContaMB")
@Scope("session")
public class LancamentoContaController extends AbstractCRUDController<LancamentoConta> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4809986079850307573L;
	
	@Autowired
	private ILancamentoConta service;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private ICategoria categoriaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private IFechamentoPeriodo fechamentoPeriodoService;
	
	@Autowired
	private MovimentacaoLancamentoController movimentacaoLancamentoMB;
	
	@Autowired
	private IBuscaSalva buscaSalvaService;
	
	private CriterioBuscaLancamentoConta novoCriterioBusca = new CriterioBuscaLancamentoConta();
	private CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
	
	private String agrupamentoSelecionado;
	private TipoCategoria tipoCategoriaSelecionada;
	private boolean pesquisarTermoNoAgrupamento;	
	private boolean selecionarTodosLancamentos;
	private String vincularFatura;
	private FechamentoPeriodo fechamentoSelecionado;
	
	private List<Categoria> agrupamentoLancamentoPorCategoria = new ArrayList<Categoria>();
	private List<Favorecido> agrupamentoLancamentoPorFavorecido = new ArrayList<Favorecido>();
	private List<MeioPagamento> agrupamentoLancamentoPorMeioPagamento = new ArrayList<MeioPagamento>();
	private List<AgrupamentoLancamento> agrupamentoLancamentoPorDebitoCredito = new ArrayList<AgrupamentoLancamento>();
	private List<Moeda> agrupamentoLancamentoPorMoeda = new ArrayList<>();
	
	private BuscaSalva buscaSalva = new BuscaSalva();
	private List<BuscaSalva> buscasSalvas = new ArrayList<BuscaSalva>();
	
	public LancamentoContaController() {
		super(new LancamentoConta());
		moduleTitle = "Lançamentos da Conta";
	}

	@Override
	protected void initializeEntity() {
		entity = new LancamentoConta();
		listEntity = new ArrayList<LancamentoConta>();
		
		agrupamentoLancamentoPorCategoria = new ArrayList<Categoria>();
		agrupamentoLancamentoPorFavorecido = new ArrayList<Favorecido>();
		agrupamentoLancamentoPorMeioPagamento = new ArrayList<MeioPagamento>();
		agrupamentoLancamentoPorDebitoCredito = new ArrayList<AgrupamentoLancamento>();
		agrupamentoLancamentoPorMoeda = new ArrayList<>();
		
		selecionarTodosLancamentos = false;
		
		buscasSalvas.clear();
	}
	
	@Override
	public void find() {
		try {
			/* Rotina de migração de novoCriterioBusca para criterioBusca /*
			 * 
			 */
			// Seta a conta
			novoCriterioBusca.setConta(criterioBusca.getConta());
			
			// Seta o início
			novoCriterioBusca.setDataInicio(criterioBusca.getDataInicio());
			
			// Seta o fim
			novoCriterioBusca.setDataFim(criterioBusca.getDataFim());
			
			// Seta a descrição
			novoCriterioBusca.setDescricao(criterioBusca.getDescricao());
			
			/* Fim da rotina */
			
			if (novoCriterioBusca.getConta() == null) {
				warnMessage("Informe a conta!");
			} else {
				if (this.pesquisarTermoNoAgrupamento)
					novoCriterioBusca.setAgrupamento(this.agrupamentoSelecionado);					
				else
					// TODO resolver a ambiguidade da chamada do método
					novoCriterioBusca.setAgrupamento(null);
				
				// Seta o limite de resultado da pesquisa
				novoCriterioBusca.setLimiteResultado(getOpcoesSistema().getLimiteQuantidadeRegistros());
				
				listEntity = getService().buscarPorCriterioBusca(novoCriterioBusca);
				
				/*
				if (agrupamentoSelecionado.equals("CAT"))
					agrupamentoLancamentoPorCategoria = getService().organizarLancamentosPorCategoria(listEntity);
				if (agrupamentoSelecionado.equals("FAV"))
					agrupamentoLancamentoPorFavorecido = getService().organizarLancamentosPorFavorecido(listEntity);
				if (agrupamentoSelecionado.equals("PAG"))
					agrupamentoLancamentoPorMeioPagamento = getService().organizarLancamentosPorMeioPagamento(listEntity);
				if (agrupamentoSelecionado.equals("CD"))
					agrupamentoLancamentoPorDebitoCredito = getService().organizarLancamentosPorDebitoCredito(listEntity);
				if (agrupamentoSelecionado.equals("MOE"))
					agrupamentoLancamentoPorMoeda = getService().organizarLancamentosPorMoeda(listEntity);
				*/
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String search() {
		find();
		return super.search();
	}
	
	@Override
	public String create() {
		entity.setTipoLancamento(TipoLancamento.DESPESA);
		return super.create();
	}
	
	@Override
	public String edit() {
		String goToPage = super.edit();
		if (goToPage.equals(goToFormPage)) {
			atualizaComboCategorias();
		}
		return goToPage;
	}
	
	public void carregarArquivo(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (entity.getArquivo() == null) entity.setArquivo(new Arquivo());
			entity.getArquivo().setDados(event.getFile().getContents());
			entity.getArquivo().setNomeArquivo(event.getFile().getFileName().replace(" ", "."));
			entity.getArquivo().setContentType(event.getFile().getContentType());
			entity.getArquivo().setTamanho(event.getFile().getSize());	
			entity.getArquivo().setContainer(Container.LANCAMENTOCONTA);
			entity.getArquivo().setUsuario(getUsuarioLogado());
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
	
	public void atualizaComboLancamentosImportado() {
		this.getListaLancamentoImportado();
	}
	
	public void atualizaComboBuscasSalvas() {
		try {
			buscasSalvas = buscaSalvaService.buscarContaETipoContaEContaAtivaPorUsuario(novoCriterioBusca.getConta(), null, null, getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void selecionarTodos() {
		if (listEntity != null && listEntity.size() > 0)
		for (LancamentoConta l : listEntity) {
			if (selecionarTodosLancamentos) {
				l.setSelecionado(true);
			} else {
				l.setSelecionado(false);
			}
		}
	}
	
	private List<LancamentoConta> removerNaoSelecionados(List<LancamentoConta> lancamentos) {
		for (Iterator<LancamentoConta> i = lancamentos.iterator(); i.hasNext(); ) {
			if (!i.next().isSelecionado())
				i.remove();
		}
		return lancamentos;
	}
	
	public String mover() {
		if (listEntity == null){
			warnMessage("Nenhum lançamento selecionado!");
			return "";
		}
		movimentacaoLancamentoMB.setLancamentosSelecionados(this.removerNaoSelecionados(listEntity));
		initializeEntity();
		return movimentacaoLancamentoMB.moverView();		
	}
	
	public String duplicar() {
		if (listEntity == null){
			warnMessage("Nenhum lançamento selecionado!");
			return "";
		}
		movimentacaoLancamentoMB.setLancamentosSelecionados(this.removerNaoSelecionados(listEntity));
		initializeEntity();
		return movimentacaoLancamentoMB.duplicarView();
	}
	
	public String excluir() {
		if (listEntity == null){
			warnMessage("Nenhum lançamento selecionado!");
			return "";
		}
		movimentacaoLancamentoMB.setLancamentosSelecionados(this.removerNaoSelecionados(listEntity));
		initializeEntity();
		return movimentacaoLancamentoMB.excluirView();
	}
	
	public String alterarPropriedades() {
		if (listEntity == null){
			warnMessage("Nenhum lançamento selecionado!");
			return "";
		}
		movimentacaoLancamentoMB.setLancamentosSelecionados(this.removerNaoSelecionados(listEntity));
		initializeEntity();
		return movimentacaoLancamentoMB.alterarPropriedadesView();
	}
	
	public String mesclar() {
		errorMessage("Funcionalidade não implementada");
		return "";
	}
	
	public String dividir() {
		errorMessage("Funcionalidade não implementada");
		return "";
	}
	
	public String detalhar() {
		errorMessage("Funcionalidade não implementada");
		return "";
	}
	
	public String transferir() {
		initializeEntity();
		return movimentacaoLancamentoMB.transferirView();
	}
	
	public void quebrarVinculo() {		
		entity.setHashImportacao(null);
		warnMessage("Vínculo quebrado. Salve para efetivar as alterações.");
	}
	
	public void processarBuscaSalva() {
		if (buscaSalva != null) {
			novoCriterioBusca = new CriterioBuscaLancamentoConta();
			novoCriterioBusca.setConta(buscaSalva.getConta());
			novoCriterioBusca.setDescricao(buscaSalva.getTextoBusca());
			novoCriterioBusca.setDataInicio(buscaSalva.getDataInicio());
			novoCriterioBusca.setDataFim(buscaSalva.getDataFim());
			pesquisarTermoNoAgrupamento = buscaSalva.isPesquisarTermo();
			novoCriterioBusca.setIdAgrupamento(buscaSalva.getIdAgrupamento());
			switch (buscaSalva.getTipoAgrupamentoBusca()) {
				case DEBITO_CREDITO : agrupamentoSelecionado = "CD"; break;
				case CATEGORIA : agrupamentoSelecionado = "CAT"; break;
				case FAVORECIDO : agrupamentoSelecionado = "FAV"; break;
				case MEIOPAGAMENTO : agrupamentoSelecionado = "PAG"; break;
				case MOEDA : agrupamentoSelecionado = "MOE"; break;
				default : agrupamentoSelecionado = "";
			}
			
			/* Migração para criterioBusca */
			criterioBusca.setDataInicio(novoCriterioBusca.getDataInicio());
			criterioBusca.setDataFim(novoCriterioBusca.getDataFim());
			criterioBusca.setDescricao(novoCriterioBusca.getDescricao());
			criterioBusca.setConta(novoCriterioBusca.getConta());
			this.find();
		} else {
			warnMessage("Nenhuma busca selecionada!");
		}
	}
	
	public String salvarBuscaView() {
		buscaSalva = new BuscaSalva();
		
		buscaSalva.setConta(novoCriterioBusca.getConta());
		buscaSalva.setTextoBusca(novoCriterioBusca.getDescricao());
		buscaSalva.setDataInicio(novoCriterioBusca.getDataInicio());
		buscaSalva.setDataFim(novoCriterioBusca.getDataFim());
		buscaSalva.setPesquisarTermo(pesquisarTermoNoAgrupamento);
		buscaSalva.setIdAgrupamento(novoCriterioBusca.getIdAgrupamento());
		
		if (agrupamentoSelecionado.equals("CD")) buscaSalva.setTipoAgrupamentoBusca(TipoAgrupamentoBusca.DEBITO_CREDITO);
		if (agrupamentoSelecionado.equals("CAT")) buscaSalva.setTipoAgrupamentoBusca(TipoAgrupamentoBusca.CATEGORIA);
		if (agrupamentoSelecionado.equals("PAG")) buscaSalva.setTipoAgrupamentoBusca(TipoAgrupamentoBusca.MEIOPAGAMENTO);
		if (agrupamentoSelecionado.equals("FAV")) buscaSalva.setTipoAgrupamentoBusca(TipoAgrupamentoBusca.FAVORECIDO);
		if (agrupamentoSelecionado.equals("MOE")) buscaSalva.setTipoAgrupamentoBusca(TipoAgrupamentoBusca.MOEDA);
		
		actionTitle = " - Salvar busca";
		return "/pages/LancamentoConta/salvarBusca";
	}
	
	public String salvarBusca() {
		try {
			buscaSalvaService.cadastrar(buscaSalva);
			infoMessage("Busca cadastrada com sucesso!");
			buscasSalvas.clear();
			return list();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}		
		return "";
	}
	
	public String excluirBuscaView() {
		if (buscaSalva == null) {
			warnMessage("Nenhuma busca selecionada!");
			return "";
		}
		actionTitle = " - Excluir busca";
		return "/pages/LancamentoConta/excluirBusca";
	}
	
	public String excluirBusca() {
		try {
			buscaSalvaService.excluir(buscaSalva);
			infoMessage("Busca excluída com sucesso!");
			buscasSalvas.clear();
			return list();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}		
		return "";
	}
	
	public List<Conta> getListaConta() {
		try {
			if (getOpcoesSistema().getExibirContasInativas()) {
				return contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", null, getUsuarioLogado(), null);
			} else {
				return contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", null, getUsuarioLogado(), true);
			}			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public List<Conta> getListaContaAtivo() {
		try {
			return contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", null, getUsuarioLogado(), true);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public List<SelectItem> getListaLancamentoImportado() {
		List<SelectItem> listagem = new ArrayList<SelectItem>();
		try {
			// Traz a opção do sistema para suprimir texto
			OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_SUPRIMIR_TEXTO_MEIO", getUsuarioLogado());
			
			// Carrega os lançamentos importados
			for (LancamentoImportado importado : getService().buscarLancamentoImportadoPorConta(entity.getConta())) {
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {
					listagem.add(new SelectItem(importado, Util.suprimirTextoMeio(Util.formataDataHora(importado.getData(), Util.DATA) + " - " + importado.getMoeda() + " "+ importado.getValor() + " - " + importado.getHistorico(), 55)));
				} else {
					listagem.add(new SelectItem(importado, Util.suprimirTextoFim(Util.formataDataHora(importado.getData(), Util.DATA) + " - " + importado.getMoeda() + " " + importado.getValor() + " - " + importado.getHistorico(), 55)));
				}
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return listagem;
	}
	
	public List<BuscaSalva> getBuscasSalvas() {
		try {
			if (buscasSalvas.isEmpty()) {
				if (getOpcoesSistema().getExibirContasInativas())
					buscasSalvas = buscaSalvaService.buscarContaETipoContaEContaAtivaPorUsuario(novoCriterioBusca.getConta(), null, null, getUsuarioLogado());
				else
					buscasSalvas = buscaSalvaService.buscarContaETipoContaEContaAtivaPorUsuario(novoCriterioBusca.getConta(), null, true, getUsuarioLogado());
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return buscasSalvas;
	}
	
	public String getTotalCreditos() {
		double total = 0;
		if (listEntity != null) {
			for (LancamentoConta lancamento : listEntity) {
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					total += lancamento.getValorPago();
			}
		}
		return this.retornaSimboloMonetario() + " " + new DecimalFormat("#,##0.00").format(Util.arredondar(total));
	}
	
	public String getTotalDebitos() {
		double total = 0;
		if (listEntity != null) {
			for (LancamentoConta lancamento : listEntity) {
				if (lancamento.getTipoLancamento().equals(TipoLancamento.DESPESA))
					total += lancamento.getValorPago();
			}
		}
		return this.retornaSimboloMonetario() + " " + new DecimalFormat("#,##0.00").format(Util.arredondar(total));
	}
	
	public String getSaldoTotal() {
		double total = getService().calcularSaldoLancamentos(listEntity);		
		return this.retornaSimboloMonetario() + " " + new DecimalFormat("#,##0.00").format(Util.arredondar(total));
	}
	
	private String retornaSimboloMonetario() {
		// Verifica se a conta já foi selecionada. Se não, usa a moeda padrão.
		String simboloMonetario = "";
		if (novoCriterioBusca.getConta() == null) {
			simboloMonetario = this.getMoedaPadrao().getSimboloMonetario();
		} else {
			simboloMonetario = this.novoCriterioBusca.getConta().getMoeda().getSimboloMonetario();
		}
		return simboloMonetario;
	}
	
	public List<SelectItem> getListaTipoLancamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		listaSelectItem.add(new SelectItem(TipoLancamento.RECEITA, "Receita"));
		listaSelectItem.add(new SelectItem(TipoLancamento.DESPESA, "Despesa"));
		return listaSelectItem;
	}
	
	public void atualizarListaPesquisaAgrupamento() {
		this.getListaPesquisaAgrupamento();
	}
	
	public List<FechamentoPeriodo> getListaFechamentoPeriodo() {
		List<FechamentoPeriodo> fechamentos = new ArrayList<>();
		try {			
			if (criterioBusca.getConta() != null) {
				List<FechamentoPeriodo> resultado = fechamentoPeriodoService.buscarPorContaEOperacaoConta(criterioBusca.getConta(), OperacaoConta.FECHAMENTO);
				if (resultado != null) {
					if (resultado.size() >= getOpcoesSistema().getLimiteQuantidadeFechamentos()) {
						for (int i = 0; i < getOpcoesSistema().getLimiteQuantidadeFechamentos(); i++) {
							fechamentos.add(resultado.get(i));
						}
					} else {
						fechamentos.addAll(resultado);
					}
				}
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return fechamentos;
	}
	
	public void atualizaListaFechamentoPeriodo() {
		this.getListaFechamentoPeriodo();
	}
	
	public List<SelectItem> getListaPesquisaAgrupamento() {
		List<SelectItem> resultado = new ArrayList<SelectItem>();
		if (agrupamentoSelecionado != null && agrupamentoSelecionado.equals("CAT")) {
			for (Categoria c : this.getListaCategoriaSemTipoCategoria()) {
				resultado.add(new SelectItem(c.getId(), c.getTipoCategoria() + " - " + c.getDescricao()));
			}
		}
		if (agrupamentoSelecionado != null && agrupamentoSelecionado.equals("FAV")) {
			for (Favorecido f : this.getListaFavorecido()) {
				resultado.add(new SelectItem(f.getId(), f.getNome()));
			}
		}
		if (agrupamentoSelecionado != null && agrupamentoSelecionado.equals("PAG")) {
			for (MeioPagamento m : this.getListaMeioPagamento()) {
				resultado.add(new SelectItem(m.getId(), m.getDescricao()));
			}
		}
		return resultado;
	}
	
	public List<SelectItem> getListaStatusLancamentoConta() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (StatusLancamentoConta status : StatusLancamentoConta.values()) {
			listaSelectItem.add(new SelectItem(status, status.toString()));
		}
		return listaSelectItem;
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
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		} 
		return new ArrayList<Categoria>();
	}
	
	public List<Categoria> getListaCategoriaSemTipoCategoria() {
		try {
			 List<Categoria> resultado = categoriaService.buscarAtivosPorUsuario(getUsuarioLogado());				
			// Lógica para incluir a categoria inativa da entidade na combo
			if (resultado != null && entity.getCategoria() != null) {
				if (!resultado.contains(entity.getCategoria())) {
					entity.getCategoria().setAtivo(true);
					resultado.add(entity.getCategoria());
				}
			}
			return resultado;			
		} catch (BusinessException be) {
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
		} catch (BusinessException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<MeioPagamento> getListaMeioPagamento() {
		try {
			List<MeioPagamento> resultado = meioPagamentoService.buscarAtivosPorUsuario(getUsuarioLogado());
			// Lógica para incluir o meio de pagamento inativo da entidade na combo
			if (resultado != null && entity.getMeioPagamento() != null) {
				if (!resultado.contains(entity.getMeioPagamento())) {
					entity.getMeioPagamento().setAtivo(true);
					resultado.add(entity.getMeioPagamento());
				}
			}
			return resultado;
		} catch (BusinessException be) {
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
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}

	public ILancamentoConta getService() {
		return service;
	}

	public void setService(ILancamentoConta service) {
		this.service = service;
	}

	public IConta getContaService() {
		return contaService;
	}

	public void setContaService(IConta contaService) {
		this.contaService = contaService;
	}

	public ICategoria getCategoriaService() {
		return categoriaService;
	}

	public void setCategoriaService(ICategoria categoriaService) {
		this.categoriaService = categoriaService;
	}

	public IFavorecido getFavorecidoService() {
		return favorecidoService;
	}

	public void setFavorecidoService(IFavorecido favorecidoService) {
		this.favorecidoService = favorecidoService;
	}

	public IMeioPagamento getMeioPagamentoService() {
		return meioPagamentoService;
	}

	public void setMeioPagamentoService(IMeioPagamento meioPagamentoService) {
		this.meioPagamentoService = meioPagamentoService;
	}

	public MovimentacaoLancamentoController getMovimentacaoLancamentoMB() {
		return movimentacaoLancamentoMB;
	}

	public void setMovimentacaoLancamentoMB(
			MovimentacaoLancamentoController movimentacaoLancamentoMB) {
		this.movimentacaoLancamentoMB = movimentacaoLancamentoMB;
	}

	public IBuscaSalva getBuscaSalvaService() {
		return buscaSalvaService;
	}

	public void setBuscaSalvaService(IBuscaSalva buscaSalvaService) {
		this.buscaSalvaService = buscaSalvaService;
	}

	public CriterioBuscaLancamentoConta getNovoCriterioBusca() {
		return novoCriterioBusca;
	}

	public void setNovoCriterioBusca(CriterioBuscaLancamentoConta novoCriterioBusca) {
		this.novoCriterioBusca = novoCriterioBusca;
	}

	public String getAgrupamentoSelecionado() {
		return agrupamentoSelecionado;
	}

	public void setAgrupamentoSelecionado(String agrupamentoSelecionado) {
		this.agrupamentoSelecionado = agrupamentoSelecionado;
	}

	public TipoCategoria getTipoCategoriaSelecionada() {
		return tipoCategoriaSelecionada;
	}

	public void setTipoCategoriaSelecionada(TipoCategoria tipoCategoriaSelecionada) {
		this.tipoCategoriaSelecionada = tipoCategoriaSelecionada;
	}

	public boolean isPesquisarTermoNoAgrupamento() {
		return pesquisarTermoNoAgrupamento;
	}

	public void setPesquisarTermoNoAgrupamento(boolean pesquisarTermoNoAgrupamento) {
		this.pesquisarTermoNoAgrupamento = pesquisarTermoNoAgrupamento;
	}

	public boolean isSelecionarTodosLancamentos() {
		return selecionarTodosLancamentos;
	}

	public void setSelecionarTodosLancamentos(boolean selecionarTodosLancamentos) {
		this.selecionarTodosLancamentos = selecionarTodosLancamentos;
	}

	public List<Categoria> getAgrupamentoLancamentoPorCategoria() {
		return agrupamentoLancamentoPorCategoria;
	}

	public void setAgrupamentoLancamentoPorCategoria(
			List<Categoria> agrupamentoLancamentoPorCategoria) {
		this.agrupamentoLancamentoPorCategoria = agrupamentoLancamentoPorCategoria;
	}

	public List<Favorecido> getAgrupamentoLancamentoPorFavorecido() {
		return agrupamentoLancamentoPorFavorecido;
	}

	public void setAgrupamentoLancamentoPorFavorecido(
			List<Favorecido> agrupamentoLancamentoPorFavorecido) {
		this.agrupamentoLancamentoPorFavorecido = agrupamentoLancamentoPorFavorecido;
	}

	public List<MeioPagamento> getAgrupamentoLancamentoPorMeioPagamento() {
		return agrupamentoLancamentoPorMeioPagamento;
	}

	public void setAgrupamentoLancamentoPorMeioPagamento(
			List<MeioPagamento> agrupamentoLancamentoPorMeioPagamento) {
		this.agrupamentoLancamentoPorMeioPagamento = agrupamentoLancamentoPorMeioPagamento;
	}

	public List<AgrupamentoLancamento> getAgrupamentoLancamentoPorDebitoCredito() {
		return agrupamentoLancamentoPorDebitoCredito;
	}

	public void setAgrupamentoLancamentoPorDebitoCredito(
			List<AgrupamentoLancamento> agrupamentoLancamentoPorDebitoCredito) {
		this.agrupamentoLancamentoPorDebitoCredito = agrupamentoLancamentoPorDebitoCredito;
	}

	public List<Moeda> getAgrupamentoLancamentoPorMoeda() {
		return agrupamentoLancamentoPorMoeda;
	}

	public void setAgrupamentoLancamentoPorMoeda(
			List<Moeda> agrupamentoLancamentoPorMoeda) {
		this.agrupamentoLancamentoPorMoeda = agrupamentoLancamentoPorMoeda;
	}

	public BuscaSalva getBuscaSalva() {
		return buscaSalva;
	}

	public void setBuscaSalva(BuscaSalva buscaSalva) {
		this.buscaSalva = buscaSalva;
	}

	public void setBuscasSalvas(List<BuscaSalva> buscasSalvas) {
		this.buscasSalvas = buscasSalvas;
	}

	public String getVincularFatura() {
		return vincularFatura;
	}

	public void setVincularFatura(String vincularFatura) {
		this.vincularFatura = vincularFatura;
	}

	public IMoeda getMoedaService() {
		return moedaService;
	}

	public void setMoedaService(IMoeda moedaService) {
		this.moedaService = moedaService;
	}

	public CriterioBuscaLancamentoConta getCriterioBusca() {
		return criterioBusca;
	}

	public void setCriterioBusca(CriterioBuscaLancamentoConta criterioBusca) {
		this.criterioBusca = criterioBusca;
	}

	public FechamentoPeriodo getFechamentoSelecionado() {
		return fechamentoSelecionado;
	}

	public void setFechamentoSelecionado(FechamentoPeriodo fechamentoSelecionado) {
		this.fechamentoSelecionado = fechamentoSelecionado;
	}
}