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

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.CadastroSistema;
import br.com.hslife.orcamento.enumeration.Container;
import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.util.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.util.LancamentoContaComparator;
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
	private MovimentacaoLancamentoController movimentacaoLancamentoMB;
	
	@Autowired
	private ImportacaoLancamentoController importacaoLancamentoMB;
	
	private CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
	
	private CadastroSistema agrupamentoSelecionado;
	private TipoCategoria tipoCategoriaSelecionada;
	private boolean pesquisarTermoNoAgrupamento;	
	private boolean selecionarTodosLancamentos;
	private FechamentoPeriodo fechamentoPeriodo;
	private Date dataFechamento;
	
	private List<LancamentoConta> lancamentosPeriodicos = new ArrayList<>();
	
	public LancamentoContaController() {
		super(new LancamentoConta());
		moduleTitle = "Lançamentos da Conta/Cartão";
	}

	@Override
	protected void initializeEntity() {
		entity = new LancamentoConta();
		listEntity = new ArrayList<LancamentoConta>();
		
		selecionarTodosLancamentos = false;
		dataFechamento = null;
	}
	
	@Override
	public void find() {
		try {			
			if (criterioBusca.getConta() == null) {
				warnMessage("Informe a conta!");
			} else {
				if (this.pesquisarTermoNoAgrupamento)
					criterioBusca.setCadastro(this.agrupamentoSelecionado);					
				
				// Seta o limite de resultado da pesquisa
				criterioBusca.setLimiteResultado(getOpcoesSistema().getLimiteQuantidadeRegistros());
				
				listEntity = getService().buscarPorCriterioBusca(criterioBusca);
				
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void findByPeriodo() {
		try {
			if (criterioBusca.getConta() == null) {
				warnMessage("Informe a conta!");
				return;
			}
			
			criterioBusca.limparCriterios();
			
			// Busca o fechamento do período anterior
			FechamentoPeriodo fechamentoAnterior;
			if (fechamentoPeriodo != null) 
				fechamentoAnterior = getService().buscarFechamentoPeriodoAnterior(fechamentoPeriodo);
			else
				fechamentoAnterior = getService().buscarUltimoFechamentoConta(criterioBusca.getConta());
			
			// Determina a data de início do período
			if (fechamentoAnterior == null) {
				criterioBusca.setDataInicio(criterioBusca.getConta().getDataAbertura());
				criterioBusca.setDataFim(new Date());
			} else {
				Calendar temp = Calendar.getInstance();
				temp.setTime(fechamentoAnterior.getData());
				temp.add(Calendar.DAY_OF_YEAR, 1);
				criterioBusca.setDataInicio(temp.getTime());
			}
			
			// Determina a data de fim do período
			if (fechamentoPeriodo == null) {
				criterioBusca.setDataFim(new Date());
			} else {
				criterioBusca.setDataFim(fechamentoPeriodo.getData());
			}

			// Correção do problema da dataInício ser maior que dataFim
			if (criterioBusca.getDataInicio().after(criterioBusca.getDataFim())) {
				criterioBusca.setDataFim(null);
			}
			
			// Seta o limite de resultado da pesquisa
			criterioBusca.setLimiteResultado(getOpcoesSistema().getLimiteQuantidadeRegistros());
			
			listEntity = getService().buscarPorCriterioBusca(criterioBusca);
			
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
	
	@SuppressWarnings("resource")
	public void exportarLancamentos() {	
		if (listEntity == null || listEntity.isEmpty()) {
			warnMessage("Listagem vazio. Nada a exportar.");
		}
		
		try {
		
			HSSFWorkbook excel = new HSSFWorkbook(); 
			HSSFSheet planilha = excel.createSheet("lancamentoConta");
			
			HSSFRow linha = planilha.createRow(0);
			
			HSSFCell celula = linha.createCell(0);
			celula.setCellValue("Data");
			celula = linha.createCell(1);
			celula.setCellValue("Histórico");
			celula = linha.createCell(2);
			celula.setCellValue("Valor");
			
			int linhaIndex = 1;
			for (LancamentoConta l : listEntity) {
				linha = planilha.createRow(linhaIndex);
				
				celula = linha.createCell(0);
				celula.setCellValue(Util.formataDataHora(l.getDataPagamento(), Util.DATA));
				
				celula = linha.createCell(1);
				celula.setCellValue(l.getDescricao());
				
				celula = linha.createCell(2);
				celula.setCellValue(l.getValorPago());
				
				linhaIndex++;
			}
			
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition","attachment; filename=lancamentoConta.xls");
			response.setContentLength(excel.getBytes().length);
			ServletOutputStream output = response.getOutputStream();
			output.write(excel.getBytes(), 0, excel.getBytes().length);
			FacesContext.getCurrentInstance().responseComplete();
		
		} catch (IOException e) {
			errorMessage(e.getMessage());
		}
	}
	
	public String importarLancamentos() {
		importacaoLancamentoMB.setContaSelecionada(criterioBusca.getConta());
		importacaoLancamentoMB.setGoToListPage(goToListPage);
		return importacaoLancamentoMB.create();
	}
	
	public String fecharPeriodoView() {
		if (fechamentoPeriodo != null && fechamentoPeriodo.getOperacao().equals(OperacaoConta.FECHAMENTO)) {
			warnMessage("Período selecionado encontra-se fechado.");
			return "";
		}
		actionTitle = " - Fechar período";
		lancamentosPeriodicos = new ArrayList<>();
		findByPeriodo();
		for (LancamentoConta l : listEntity) {
			if (l.getLancamentoPeriodico() != null && l.getLancamentoPeriodico().getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.FIXO)) {
				l.setSelecionado(true);
				lancamentosPeriodicos.add(l);
			}
		}
		if (fechamentoPeriodo != null && fechamentoPeriodo.getOperacao().equals(OperacaoConta.REABERTURA)) {
			dataFechamento = fechamentoPeriodo.getData();
		}
		return "/pages/LancamentoConta/fecharPeriodo";
	}
	
	public String fecharPeriodo() {
		try {
			if (dataFechamento == null) {
				warnMessage("Informe a data de fechamento!");
				return "";
			}
			
			// Remove os lançamentos não selecionados
			for (Iterator<LancamentoConta> i = lancamentosPeriodicos.iterator(); i.hasNext(); ) {
				if (!i.next().isSelecionado()) {
					i.remove();
				}
			}
			
			if (fechamentoPeriodo != null) 
				getService().fecharPeriodo(fechamentoPeriodo, lancamentosPeriodicos);
			else 
				getService().fecharPeriodo(dataFechamento, criterioBusca.getConta(), lancamentosPeriodicos);
			
			infoMessage("Período fechado com sucesso.");
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return list();
	}
	
	public String reabrirPeriodoView() {
		if (fechamentoPeriodo == null) {
			warnMessage("Selecione um fechamento de período!");
			return "";
		}
		if (fechamentoPeriodo.getOperacao().equals(OperacaoConta.REABERTURA)) {
			warnMessage("O período selecionado já foi reaberto.");
			return "";
		}
		Collections.sort(fechamentoPeriodo.getLancamentos(), new LancamentoContaComparator());
		actionTitle = " - Reabrir período";
		return "/pages/LancamentoConta/reabrirPeriodo";
	}
	
	public String reabrirPeriodo() {
		try {
			getService().reabrirPeriodo(fechamentoPeriodo);
			infoMessage("Período reaberto com sucesso.");
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return list();
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
		movimentacaoLancamentoMB.setContaSelecionada(criterioBusca.getConta());
		initializeEntity();
		return movimentacaoLancamentoMB.excluirView();
	}
	
	public String alterarPropriedades() {
		if (listEntity == null){
			warnMessage("Nenhum lançamento selecionado!");
			return "";
		}
		movimentacaoLancamentoMB.setLancamentosSelecionados(this.removerNaoSelecionados(listEntity));
		movimentacaoLancamentoMB.setContaSelecionada(criterioBusca.getConta());
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
	
	public String vinculos() {		
		if (listEntity == null){
			warnMessage("Nenhum lançamento selecionado!");
			return "";
		}
		movimentacaoLancamentoMB.setLancamentosSelecionados(this.removerNaoSelecionados(listEntity));
		movimentacaoLancamentoMB.setContaSelecionada(criterioBusca.getConta());
		initializeEntity();
		return movimentacaoLancamentoMB.removerVinculosView();
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
			for (LancamentoImportado importado : getService().buscarLancamentoImportadoPorConta(entity.getConta())) {
				listagem.add(new SelectItem(importado, Util.formataDataHora(importado.getData(), Util.DATA) + " - " + importado.getMoeda() + " "+ importado.getValor() + " - " + importado.getHistorico()));
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return listagem;
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
		if (criterioBusca.getConta() == null) {
			simboloMonetario = this.getMoedaPadrao().getSimboloMonetario();
		} else {
			simboloMonetario = this.criterioBusca.getConta().getMoeda().getSimboloMonetario();
		}
		return simboloMonetario;
	}
	
	public void atualizarListaPesquisaAgrupamento() {
		if (agrupamentoSelecionado == null) {
			pesquisarTermoNoAgrupamento = false;
		} else {
			pesquisarTermoNoAgrupamento = true;
		}
		this.getListaPesquisaAgrupamento();
	}
	
	public List<FechamentoPeriodo> getListaFechamentoPeriodo() {
		List<FechamentoPeriodo> fechamentos = new ArrayList<>();
		try {			
			if (criterioBusca.getConta() != null) {
				for (FechamentoPeriodo fechamento : getService().buscarTodosFechamentoPorConta(criterioBusca.getConta())) {
					fechamentos.add(fechamento);
					if (fechamentos.size() >= getOpcoesSistema().getLimiteQuantidadeFechamentos()) {
						break;
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
		if (agrupamentoSelecionado != null) {
			switch (agrupamentoSelecionado) {
				case CATEGORIA :
					for (Categoria c : this.getListaCategoriaSemTipoCategoria()) {
						resultado.add(new SelectItem(c.getId(), c.getTipoCategoria() + " - " + c.getDescricao()));
					}
					break;
				case FAVORECIDO :
					for (Favorecido f : this.getListaFavorecido()) {
						resultado.add(new SelectItem(f.getId(), f.getNome()));
					}
					break;
				case MEIOPAGAMENTO : 
					for (MeioPagamento m : this.getListaMeioPagamento()) {
						resultado.add(new SelectItem(m.getId(), m.getDescricao()));
					}
					break;
				case MOEDA :
					for (Moeda m : this.getListaMoeda()) {
						resultado.add(new SelectItem(m.getId(), m.getLabel()));
					}
					break;
				default :
			}
		}
		return resultado;
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

	public FechamentoPeriodo getFechamentoPeriodo() {
		return fechamentoPeriodo;
	}

	public void setFechamentoPeriodo(FechamentoPeriodo fechamentoPeriodo) {
		this.fechamentoPeriodo = fechamentoPeriodo;
	}

	public CadastroSistema getAgrupamentoSelecionado() {
		return agrupamentoSelecionado;
	}

	public void setAgrupamentoSelecionado(CadastroSistema agrupamentoSelecionado) {
		this.agrupamentoSelecionado = agrupamentoSelecionado;
	}

	public Date getDataFechamento() {
		return dataFechamento;
	}

	public void setDataFechamento(Date dataFechamento) {
		this.dataFechamento = dataFechamento;
	}

	public List<LancamentoConta> getLancamentosPeriodicos() {
		return lancamentosPeriodicos;
	}

	public void setLancamentosPeriodicos(List<LancamentoConta> lancamentosPeriodicos) {
		this.lancamentosPeriodicos = lancamentosPeriodicos;
	}
}