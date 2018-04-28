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

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
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
import br.com.hslife.orcamento.entity.FaturaCartao;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.Container;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFaturaCartao;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IFechamentoPeriodo;
import br.com.hslife.orcamento.facade.IImportacaoLancamento;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.ILancamentoPeriodico;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.util.DetalheFaturaComparator;
import br.com.hslife.orcamento.util.LancamentoContaUtil;
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
	private IFaturaCartao faturaCartaoService;
	
	@Autowired
	private ILancamentoPeriodico lancamentoPeriodicoService;
	
	@Autowired
	private MovimentacaoLancamentoController movimentacaoLancamentoMB;
	
	@Autowired
	private ImportacaoLancamentoController importacaoLancamentoMB;
	
	@Autowired
	private IImportacaoLancamento importacaoLancamentoService;
	
	@Autowired
	private IFechamentoPeriodo fechamentoPeriodoService;
	
	private CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
	
	private TipoCategoria tipoCategoriaSelecionada;	
	private boolean selecionarTodosLancamentos;
	private FaturaCartao faturaCartao;
	private Date dataFechamento;
	private String mesAno;
	
	private List<LancamentoPeriodico> lancamentosPeriodicos = new ArrayList<>();
	
	public LancamentoContaController() {
		super(new LancamentoConta());
		moduleTitle = "Lançamentos da Conta / Cartão";
	}

	@Override
	public List<LancamentoConta> getListEntity() {
		if (listEntity != null)
			for (LancamentoConta l : listEntity) {
				if (l.isSaldoFatura())
					l.setEditavel(false);
			}
		return listEntity;
	}
	
	@Override
	protected void initializeEntity() {
		entity = new LancamentoConta();
		
		if (listEntity == null)
			listEntity = new ArrayList<>();
		else
			listEntity.clear();
		
		selecionarTodosLancamentos = false;
		dataFechamento = null;
	}
	
	@Override
	public void find() {
		try {			
			if (criterioBusca.getConta() == null) {
				warnMessage("Informe a conta!");
			} else if (criterioBusca.quantCriteriosDefinidos() == 1) {
				listEntity.clear();
				warnMessage("Define melhor sua busca!");
			} else {
				criterioBusca.setLimiteResultado(getOpcoesSistema().getLimiteQuantidadeRegistros());
				listEntity = getService().buscarPorCriterioBusca(criterioBusca);
			}
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void findByFatura() {
		try {
			if (criterioBusca.getConta() == null) {
				warnMessage("Informe a conta!");
				return;
			}
			
			criterioBusca.limparCriterios();
			criterioBusca.setDataInicio(null);
			criterioBusca.setDataFim(null);
			
			if (faturaCartao != null) {
				// Mostra os lançamentos que atualmente estão vinculados na fatura selecionada
				listEntity = new ArrayList<>();
				FaturaCartao fatura = faturaCartaoService.buscarPorID(faturaCartao.getId());
				listEntity.addAll(fatura.getDetalheFatura());
				listEntity.sort(new DetalheFaturaComparator());
				
				if (listEntity.size() > 0) {
					criterioBusca.setDataInicio(listEntity.get(0).getDataPagamento());
					criterioBusca.setDataFim(listEntity.get(listEntity.size()-1).getDataPagamento());
				}
				
			} else {
				// Mostra todos os lançamentos que não estão vinculados a uma fatura
				criterioBusca.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.AGENDADO, StatusLancamentoConta.REGISTRADO});
				
				criterioBusca.setLimiteResultado(getOpcoesSistema().getLimiteQuantidadeRegistros());
				listEntity = getService().buscarPorCriterioBusca(criterioBusca);
				
				if (listEntity != null && listEntity.size() > 0) {
					criterioBusca.setDataInicio(listEntity.get(0).getDataPagamento());
					criterioBusca.setDataFim(listEntity.get(listEntity.size()-1).getDataPagamento());
				}
			}
			
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void findByPeriodo() {
		if (criterioBusca.getConta() == null) {
			warnMessage("Informe a conta!");
			return;
		}
		try {
			
			criterioBusca.limparCriterios();
			criterioBusca.setDataInicio(null);
			criterioBusca.setDataFim(null);
			
			if (listEntity != null)
				listEntity.clear();
			else
				listEntity = new LinkedList<>();
			
			if (mesAno == null || mesAno.isEmpty()) {
				// Preguiça...
				mesAno = (Calendar.getInstance().get(Calendar.MONTH) + 1) + "/" + Calendar.getInstance().get(Calendar.YEAR);
			}
			
			String[] dataParticionada = mesAno.split("/");
			
			criterioBusca.setDataInicio(Util.primeiroDiaMes(Integer.valueOf(dataParticionada[0]) - 1, Integer.valueOf(dataParticionada[1])));
			criterioBusca.setDataFim(Util.ultimoDiaMes(Integer.valueOf(dataParticionada[0]) - 1, Integer.valueOf(dataParticionada[1])));
			criterioBusca.setLimiteResultado(getOpcoesSistema().getLimiteQuantidadeRegistros());
			
			// Adiciona um lançamento extra para exibir o saldo do período anterior
			LancamentoConta saldoAnterior = new LancamentoConta();
			Calendar dataPeriodoAnterior = Calendar.getInstance();
			dataPeriodoAnterior.setTime(criterioBusca.getDataInicio());
			dataPeriodoAnterior.add(Calendar.DAY_OF_YEAR, -1);
			
			BigDecimal valorSaldoAnterior = getService().buscarSaldoPeriodoByContaAndPeriodoAndStatusLancamento(criterioBusca.getConta(), null, dataPeriodoAnterior.getTime(), null);
			
			saldoAnterior.setDataPagamento(dataPeriodoAnterior.getTime());
			saldoAnterior.setDescricao("Saldo do período anterior - até " + Util.formataDataHora(dataPeriodoAnterior.getTime(), Util.DATA));
			saldoAnterior.setValorPago(valorSaldoAnterior.doubleValue());
			saldoAnterior.setMoeda(criterioBusca.getConta().getMoeda());
			if (valorSaldoAnterior.signum() == 1)
				saldoAnterior.setTipoLancamento(TipoLancamento.RECEITA);
			else 
				saldoAnterior.setTipoLancamento(TipoLancamento.DESPESA);
			saldoAnterior.setEditavel(false);
			
			// Adiciona o saldo anterior na listagem, e adiciona os demais lançamentos encontrados
			listEntity.add(saldoAnterior);
			for (LancamentoConta l : getService().buscarPorCriterioBusca(criterioBusca)) {
				listEntity.add(l);
			}
			
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String search() {
		find();
		return super.search();
	}
	
	@Override
	public String view() {
		String retorno = super.view();
		
		// Traz o arquivo anexado ao lançamento
		if (entity.getIdArquivo() != null)
			entity.setArquivo(getArquivoComponent().buscarArquivo(entity.getIdArquivo()));
		
		return retorno;
	}
	
	@Override
	public String create() {
		entity.setTipoLancamento(TipoLancamento.DESPESA);
		tipoCategoriaSelecionada = TipoCategoria.DEBITO;
		entity.setConta(criterioBusca.getConta());
		this.getListaCategoria();
		return super.create();
	}
	
	@Override
	public String edit() {
		// Evita problemas com definição de IDs inválidos
		// vindos de outros módulos
		if (idEntity == null || idEntity <= 0) {
			warnMessage("Lançamento informado é inválido!");
			return goToListPage;
		}
		String goToPage = super.edit();
		if (goToPage.equals(goToFormPage)) {
			atualizaComboCategorias();
		}
		return goToPage;
	}
	
	public void carregarArquivo(FileUploadEvent event) {
		if (event.getFile() != null) {
			Arquivo arquivo = new Arquivo();
			arquivo.setDados(event.getFile().getContents());
			arquivo.setNomeArquivo(event.getFile().getFileName().replace(" ", "."));
			arquivo.setContentType(event.getFile().getContentType());
			arquivo.setTamanho(event.getFile().getSize());	
			arquivo.setContainer(Container.LANCAMENTOCONTA);
			arquivo.setUsuario(getUsuarioLogado());
			arquivo.setAttribute("arquivo");
			if (entity.getIdArquivo() == null)
				entity.setIdArquivo(getArquivoComponent().carregarArquivo(arquivo));
			else
				entity.setIdArquivo(getArquivoComponent().substituirArquivo(arquivo, entity.getIdArquivo()));
		} 
	}
	
	public void baixarArquivo() {
		Arquivo arquivo = getArquivoComponent().buscarArquivo(entity.getIdArquivo()); 
		if (arquivo == null) {
			warnMessage("Nenhum arquivo adicionado!");
		} else {
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			try {			
				response.setContentType(arquivo.getContentType());
				response.setHeader("Content-Disposition","attachment; filename=" + arquivo.getNomeArquivo());
				response.setContentLength(arquivo.getDados().length);
				ServletOutputStream output = response.getOutputStream();
				output.write(arquivo.getDados(), 0, arquivo.getDados().length);
				FacesContext.getCurrentInstance().responseComplete();
			} catch (Exception e) {
				errorMessage(e.getMessage());
			}
		}
	}
	
	public void excluirArquivo() {
		if (entity.getIdArquivo() == null) {
			warnMessage("Nenhum arquivo adicionado!");
		} else {
			getArquivoComponent().excluirArquivo(entity.getIdArquivo());
			entity.setIdArquivo(null);
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
		// Verifica se a conta seleciona está ativa
		if (!criterioBusca.getConta().isAtivo()) {
			warnMessage("A conta encontra-se inativa!");
			return "";
		}
		
		importacaoLancamentoMB.setContaSelecionada(criterioBusca.getConta());
		importacaoLancamentoMB.setGoToListPage(goToListPage);
		return importacaoLancamentoMB.create();
	}
	
	public String fecharPeriodoView() {
		// Verifica se a conta seleciona está ativa
		if (!criterioBusca.getConta().isAtivo()) {
			warnMessage("A conta encontra-se inativa!");
			return "";
		}
		
		// Verifica se um período selecionado pode ser fechado
		if (mesAno == null || mesAno.isEmpty()) {
			warnMessage("Não se pode fechar o período atual!");
			return "";
		}
			
		try {
			actionTitle = " - Fechar período";
			lancamentosPeriodicos = lancamentoPeriodicoService
					.buscarPorTipoLancamentoContaEStatusLancamento(TipoLancamentoPeriodico.FIXO, criterioBusca.getConta(), StatusLancamento.ATIVO);
			for (LancamentoPeriodico lp : lancamentosPeriodicos) {
				lp.setSelecionado(true);
			}
			return "/pages/LancamentoConta/fecharPeriodo";
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		return "";
	}
	
	public String getPeriodoFechamento() {
		String[] dataParticionada = mesAno.split("/");
		
		return  Util.formataDataHora(Util.primeiroDiaMes(Integer.valueOf(dataParticionada[0]) - 1, Integer.valueOf(dataParticionada[1])), Util.DATA) 
				+ " à " 
				+ Util.formataDataHora(Util.ultimoDiaMes(Integer.valueOf(dataParticionada[0]) - 1, Integer.valueOf(dataParticionada[1])), Util.DATA);
	}
	
	public String fecharPeriodo() {
		try {
			// Remove os lançamentos não selecionados
			for (Iterator<LancamentoPeriodico> i = lancamentosPeriodicos.iterator(); i.hasNext(); ) {
				if (!i.next().isSelecionado()) {
					i.remove();
				}
			}
			
			String[] dataParticionada = mesAno.split("/");
			Date dataInicio = Util.primeiroDiaMes(Integer.valueOf(dataParticionada[0]) - 1, Integer.valueOf(dataParticionada[1]));
			Date dataFim = Util.ultimoDiaMes(Integer.valueOf(dataParticionada[0]) - 1, Integer.valueOf(dataParticionada[1]));
			
			getFechamentoPeriodoService().fecharPeriodo(criterioBusca.getConta(), dataInicio, dataFim, lancamentosPeriodicos);
			
			infoMessage("Período fechado com sucesso.");
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		return list();
	}
	
	public String reabrirPeriodoView() {
		// Verifica se a conta selecionada está ativa
		if (!criterioBusca.getConta().isAtivo()) {
			warnMessage("A conta encontra-se inativa!");
			return "";
		}
		
		// Verifica se um período selecionado pode ser fechado
		if (mesAno == null || mesAno.isEmpty()) {
			warnMessage("Não se pode reabrir o período atual!");
			return "";
		}
		
		try {
			String[] dataParticionada = mesAno.split("/");
			Date dataInicio = Util.primeiroDiaMes(Integer.valueOf(dataParticionada[0]) - 1, Integer.valueOf(dataParticionada[1]));
			Date dataFim = Util.ultimoDiaMes(Integer.valueOf(dataParticionada[0]) - 1, Integer.valueOf(dataParticionada[1]));
			
			criterioBusca.limparCriterios();
			criterioBusca.setDataInicio(dataInicio);
			criterioBusca.setDataFim(dataFim);
			criterioBusca.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.QUITADO});
			listEntity = getService().buscarPorCriterioBusca(criterioBusca);
			
			actionTitle = " - Reabrir período";
			return "/pages/LancamentoConta/reabrirPeriodo";
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		return "";
	}
	
	public String reabrirPeriodo() {
		try {
			String[] dataParticionada = mesAno.split("/");
			Date dataInicio = Util.primeiroDiaMes(Integer.valueOf(dataParticionada[0]) - 1, Integer.valueOf(dataParticionada[1]));
			Date dataFim = Util.ultimoDiaMes(Integer.valueOf(dataParticionada[0]) - 1, Integer.valueOf(dataParticionada[1]));
			
			getFechamentoPeriodoService().reabrirPeriodo(criterioBusca.getConta(), dataInicio, dataFim);
			infoMessage("Período reaberto com sucesso.");
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		return list();
	}
	
	public String mover() {
		if (listEntity == null){
			warnMessage("Nenhum lançamento selecionado!");
			return "";
		}
		movimentacaoLancamentoMB.setLancamentosSelecionados(this.removerNaoSelecionados(new ArrayList<>(listEntity)));
		initializeEntity();
		return movimentacaoLancamentoMB.moverView();		
	}
	
	public String duplicar() {
		if (listEntity == null){
			warnMessage("Nenhum lançamento selecionado!");
			return "";
		}
		movimentacaoLancamentoMB.setLancamentosSelecionados(this.removerNaoSelecionados(new ArrayList<>(listEntity)));
		movimentacaoLancamentoMB.setContaSelecionada(criterioBusca.getConta());
		initializeEntity();
		return movimentacaoLancamentoMB.duplicarView();
	}
	
	public String excluir() {
		if (listEntity == null){
			warnMessage("Nenhum lançamento selecionado!");
			return "";
		}
		movimentacaoLancamentoMB.setLancamentosSelecionados(this.removerNaoSelecionados(new ArrayList<>(listEntity)));
		movimentacaoLancamentoMB.setContaSelecionada(criterioBusca.getConta());
		initializeEntity();
		return movimentacaoLancamentoMB.excluirView();
	}
	
	public String alterarPropriedades() {
		if (listEntity == null){
			warnMessage("Nenhum lançamento selecionado!");
			return "";
		}
		movimentacaoLancamentoMB.setLancamentosSelecionados(this.removerNaoSelecionados(new ArrayList<>(listEntity)));
		movimentacaoLancamentoMB.setContaSelecionada(criterioBusca.getConta());
		initializeEntity();
		return movimentacaoLancamentoMB.alterarPropriedadesView();
	}
	
	public String mesclar() {
		if (listEntity == null){
			warnMessage("Nenhum lançamento selecionado!");
			return "";
		}
		movimentacaoLancamentoMB.setLancamentosSelecionados(this.removerNaoSelecionados(new ArrayList<>(listEntity)));
		movimentacaoLancamentoMB.setContaSelecionada(criterioBusca.getConta());
		initializeEntity();
		return movimentacaoLancamentoMB.mesclarView();
	}
	
	public String dividir() {
		if (listEntity == null){
			warnMessage("Nenhum lançamento selecionado!");
			return "";
		}
		movimentacaoLancamentoMB.setLancamentosSelecionados(this.removerNaoSelecionados(new ArrayList<>(listEntity)));
		movimentacaoLancamentoMB.setContaSelecionada(criterioBusca.getConta());
		initializeEntity();
		return movimentacaoLancamentoMB.dividirView();
	}
	
	public String detalhar() {
		if (listEntity == null){
			warnMessage("Nenhum lançamento selecionado!");
			return "";
		}
		movimentacaoLancamentoMB.setLancamentosSelecionados(this.removerNaoSelecionados(new ArrayList<>(listEntity)));
		movimentacaoLancamentoMB.setContaSelecionada(criterioBusca.getConta());
		initializeEntity();
		return movimentacaoLancamentoMB.detalharView();
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
		movimentacaoLancamentoMB.setLancamentosSelecionados(this.removerNaoSelecionados(new ArrayList<>(listEntity)));
		movimentacaoLancamentoMB.setContaSelecionada(criterioBusca.getConta());
		initializeEntity();
		return movimentacaoLancamentoMB.removerVinculosView();
	}
	
	public List<Conta> getListaConta() {
		List<Conta> contas = new ArrayList<>();
		try {
			if (getOpcoesSistema().getExibirContasInativas()) {
				contas = contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", null, getUsuarioLogado(), null);
			} else {
				contas = contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", null, getUsuarioLogado(), true);
			}
			if (contas != null && !contas.isEmpty() && criterioBusca.getConta() == null) {
				criterioBusca.setConta(contas.get(0));
			}
			return contas;
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public List<Conta> getListaContaAtivo() {
		try {
			return contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", null, getUsuarioLogado(), true);
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public List<SelectItem> getListaLancamentoImportado() {
		List<SelectItem> listagem = new ArrayList<SelectItem>();
		try {			
			for (LancamentoImportado importado : importacaoLancamentoService.buscarLancamentoImportadoPorConta(entity.getConta())) {
				listagem.add(new SelectItem(importado, Util.formataDataHora(importado.getData(), Util.DATA) + " - " + importado.getMoeda() + " "+ importado.getValor() + " - " + importado.getHistorico()));
			}
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return listagem;
	}
	
	public String getTotalCreditos() {
		double total = 0;
		if (listEntity != null) {
			for (LancamentoConta lancamento : listEntity) {
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					if (lancamento.getTaxaConversao() == null)
						total += lancamento.getValorPago();
					else
						total += lancamento.getTaxaConversao().getValorMoedaDestino();
			}
		}
		return this.retornaSimboloMonetario() + " " + new DecimalFormat("#,##0.00").format(Util.arredondar(total));
	}
	
	public String getTotalDebitos() {
		double total = 0;
		if (listEntity != null) {
			for (LancamentoConta lancamento : listEntity) {
				if (lancamento.getTipoLancamento().equals(TipoLancamento.DESPESA))
					if (lancamento.getTaxaConversao() == null)
						total += lancamento.getValorPago();
					else
						total += lancamento.getTaxaConversao().getValorMoedaDestino();
			}
		}
		return this.retornaSimboloMonetario() + " " + new DecimalFormat("#,##0.00").format(Util.arredondar(total));
	}
	
	public String getSaldoTotal() {
		double total = LancamentoContaUtil.calcularSaldoLancamentosComConversao(listEntity);		
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
		this.getListaPesquisaAgrupamento();
	}
	
	public List<FaturaCartao> getListaFaturaCartao() {
		List<FaturaCartao> faturas = new ArrayList<>();
		
		try {
			if (criterioBusca.getConta() != null) {
				for (FaturaCartao fatura : faturaCartaoService.buscarTodosPorCartaoCredito(criterioBusca.getConta())) {
					faturas.add(fatura);
					if (faturas.size() >= getOpcoesSistema().getLimiteQuantidadeFechamentos()) {
						break;
					}
				}
			}
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		return faturas;
	}
	
	public List<SelectItem> getListaPesquisaAgrupamento() {
		List<SelectItem> resultado = new ArrayList<SelectItem>();
		if (criterioBusca.getCadastro() != null) {
			switch (criterioBusca.getCadastro()) {
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
		} catch (ValidationException | BusinessException be) {
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
			// Lógica para incluir o meio de pagamento inativo da entidade na combo
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

	public List<SelectItem> getListaMesAno() {
		List<SelectItem> mesAno = new LinkedList<>();
		Calendar data = Calendar.getInstance();
		for (int i = 0; i < getOpcoesSistema().getLimiteQuantidadeFechamentos(); i++) {
			data.add(Calendar.MONTH, -1);
			String temp = data.get(Calendar.MONTH) + 1 + "/" + data.get(Calendar.YEAR);
			mesAno.add(new SelectItem(temp, "Período " + (data.get(Calendar.MONTH)+1) + " / " + data.get(Calendar.YEAR)));
		}
		return mesAno;
	}
	
	public IFechamentoPeriodo getFechamentoPeriodoService() {
		return fechamentoPeriodoService;
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

	public Date getDataFechamento() {
		return dataFechamento;
	}

	public void setDataFechamento(Date dataFechamento) {
		this.dataFechamento = dataFechamento;
	}

	public List<LancamentoPeriodico> getLancamentosPeriodicos() {
		return lancamentosPeriodicos;
	}

	public void setLancamentosPeriodicos(
			List<LancamentoPeriodico> lancamentosPeriodicos) {
		this.lancamentosPeriodicos = lancamentosPeriodicos;
	}

	public FaturaCartao getFaturaCartao() {
		return faturaCartao;
	}

	public void setFaturaCartao(FaturaCartao faturaCartao) {
		this.faturaCartao = faturaCartao;
	}

	public String getMesAno() {
		return mesAno;
	}

	public void setMesAno(String mesAno) {
		this.mesAno = mesAno;
	}
}
