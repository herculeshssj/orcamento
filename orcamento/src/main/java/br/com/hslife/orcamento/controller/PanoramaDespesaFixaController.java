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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.ILancamentoPeriodico;
import br.com.hslife.orcamento.facade.IMoeda;

@Component("panoramaDespesaFixaMB")
@Scope("session")
public class PanoramaDespesaFixaController extends AbstractController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7261234526051506146L;

	@Autowired
	private ILancamentoPeriodico lancamentoPeriodicoService;
	
	@Autowired
	private ILancamentoConta lancamentoContaService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private IConta contaService;
	
	private LancamentoPeriodico lancamentoSelecionado;
	private Conta contaSelecionada;
	private TipoConta tipoContaSelecionada;
	private Moeda moedaPadrao;
	private String periodoAConsiderar;
	private Integer periodo;
	
	private boolean exibirGraficoReceita;
	private boolean exibirGraficoDespesa;
	
	private Double maxValueBarPagamentosDespesa = 1.0;
	private Double maxValueBarPagamentosReceita = 1.0;
	
	private BarChartModel ultimosPagamentosDespesaModel;
	private BarChartModel ultimosPagamentosReceitaModel;
	
	private double saldoCredor;
	private double saldoDevedor;
	
	public PanoramaDespesaFixaController() {
		moduleTitle = "Panorama das Despesas Fixas";
	}

	@Override
	protected void initializeEntity() {
	}

	@Override
	@PostConstruct
	public String startUp() {
		exibirGraficoDespesa = false;
		exibirGraficoReceita = false;
		saldoCredor = 0.0;
		saldoDevedor = 0.0;
		this.carregarMoedaPadrao();
		return "/pages/ResumoEstatistica/panoramaDespesaFixa";
	}

	public void find() {
		try {
			// Verifica se uma opção foi selecionada de acordo com a opção do sistema
			switch (getOpcoesSistema().getFormaAgrupamentoPagamento()) {
				case "INDIVIDUAL" : 
					if (lancamentoSelecionado != null) 
						this.gerarGraficoLancamentoIndividual();
					else
						this.gerarGraficoTodosLancamentos();
					break;
				case "CONTA" : 
					if (contaSelecionada != null) 
						this.gerarGraficoConta();
					else
						this.gerarGraficoTodosLancamentos();
					break;
				case "TIPO_CONTA" : 
					if (tipoContaSelecionada != null) 
						this.gerarGraficoTipoConta(); 
					else
						this.gerarGraficoTodosLancamentos();
					break;
				default : 
					this.gerarGraficoTodosLancamentos();
			}
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
	}
	
	private void gerarGraficoTodosLancamentos() throws ApplicationException {
		List<LancamentoConta> pagamentos = new ArrayList<>();
		List<LancamentoPeriodico> lancamentos = new ArrayList<>();
		List<LancamentoConta> pagamentosReceita = new ArrayList<>();
		List<LancamentoConta> pagamentosDespesa = new ArrayList<>();
		
		if (periodoAConsiderar.equals("ANTERIOR")) {
			pagamentos = lancamentoContaService.buscarPagamentosPorTipoLancamentoEUsuarioEPago(TipoLancamentoPeriodico.FIXO, getUsuarioLogado(), null);			
		} else {
			lancamentos = lancamentoPeriodicoService.buscarPorTipoLancamentoEStatusLancamentoPorUsuario(TipoLancamentoPeriodico.FIXO, StatusLancamento.ATIVO, getUsuarioLogado());
			pagamentos = lancamentoContaService.buscarPagamentosPorTipoLancamentoEUsuarioEPago(TipoLancamentoPeriodico.FIXO, getUsuarioLogado(), null);
			for (LancamentoPeriodico lancamento : lancamentos) {
				pagamentos.addAll(lancamentoContaService.gerarPrevisaoProximosPagamentos(lancamento, periodo));
			}
		}
			
		// Separa os pagamento de lançamentos fixos de receita dos de despesa
		for (LancamentoConta pagamento : pagamentos) {
			if (pagamento.getLancamentoPeriodico().getTipoLancamento().equals(TipoLancamento.DESPESA)) {
				pagamentosDespesa.add(pagamento);
			} else {
				pagamentosReceita.add(pagamento);
			}
		}
		
		// Envia a lista de pagamento para os respectivos métodos para gerar o gráfico
		gerarGraficoDespesa(pagamentosDespesa);
		gerarGraficoReceita(pagamentosReceita);		
	}

	private void gerarGraficoTipoConta() throws ApplicationException {
		List<LancamentoConta> pagamentos = new ArrayList<>();
		List<LancamentoPeriodico> lancamentos = new ArrayList<>();
		List<LancamentoConta> pagamentosReceita = new ArrayList<>();
		List<LancamentoConta> pagamentosDespesa = new ArrayList<>();
		
		if (periodoAConsiderar.equals("ANTERIOR")) {
			pagamentos = lancamentoContaService.buscarPagamentosPorTipoLancamentoETipoContaEPago(TipoLancamentoPeriodico.FIXO, tipoContaSelecionada, null);
		} else {
			lancamentos = lancamentoPeriodicoService.buscarPorTipoLancamentoETipoContaEStatusLancamento(TipoLancamentoPeriodico.FIXO, tipoContaSelecionada, StatusLancamento.ATIVO);
			pagamentos = lancamentoContaService.buscarPagamentosPorTipoLancamentoETipoContaEPago(TipoLancamentoPeriodico.FIXO, tipoContaSelecionada, null);
			for (LancamentoPeriodico lancamento : lancamentos) {
				pagamentos.addAll(lancamentoContaService.gerarPrevisaoProximosPagamentos(lancamento, periodo));
			}
		}
			
		// Separa os pagamento de lançamentos fixos de receita dos de despesa
		for (LancamentoConta pagamento : pagamentos) {
			if (pagamento.getLancamentoPeriodico().getTipoLancamento().equals(TipoLancamento.DESPESA)) {
				pagamentosDespesa.add(pagamento);
			} else {
				pagamentosReceita.add(pagamento);
			}
		}
		
		// Envia a lista de pagamento para os respectivos métodos para gerar o gráfico
		gerarGraficoDespesa(pagamentosDespesa);
		gerarGraficoReceita(pagamentosReceita);		
	}

	private void gerarGraficoConta() throws ApplicationException {
		List<LancamentoConta> pagamentos = new ArrayList<>();
		List<LancamentoPeriodico> lancamentos = new ArrayList<>();
		List<LancamentoConta> pagamentosReceita = new ArrayList<>();
		List<LancamentoConta> pagamentosDespesa = new ArrayList<>();
		
		if (periodoAConsiderar.equals("ANTERIOR")) {
			pagamentos = lancamentoContaService.buscarPagamentosPorTipoLancamentoEContaEPago(TipoLancamentoPeriodico.FIXO, contaSelecionada, null);
		} else {
			pagamentos = lancamentoContaService.buscarPagamentosPorTipoLancamentoEContaEPago(TipoLancamentoPeriodico.FIXO, contaSelecionada, null);
			lancamentos = lancamentoPeriodicoService.buscarPorTipoLancamentoContaEStatusLancamento(TipoLancamentoPeriodico.FIXO, contaSelecionada, StatusLancamento.ATIVO);
			for (LancamentoPeriodico lancamento : lancamentos) {
				pagamentos.addAll(lancamentoContaService.gerarPrevisaoProximosPagamentos(lancamento, periodo));
			}
		}
			
		// Separa os pagamento de lançamentos fixos de receita dos de despesa
		for (LancamentoConta pagamento : pagamentos) {
			if (pagamento.getLancamentoPeriodico().getTipoLancamento().equals(TipoLancamento.DESPESA)) {
				pagamentosDespesa.add(pagamento);
			} else {
				pagamentosReceita.add(pagamento);
			}
		}
		
		// Envia a lista de pagamento para os respectivos métodos para gerar o gráfico
		gerarGraficoDespesa(pagamentosDespesa);
		gerarGraficoReceita(pagamentosReceita);		
	}

	private void gerarGraficoLancamentoIndividual() throws ApplicationException {
		List<LancamentoConta> pagamentos = new ArrayList<>();
		List<LancamentoConta> pagamentosReceita = new ArrayList<>();
		List<LancamentoConta> pagamentosDespesa = new ArrayList<>();
		
		if (periodoAConsiderar.equals("ANTERIOR")) {
			pagamentos = lancamentoContaService.buscarPagamentosPorLancamentoPeriodicoEPago(lancamentoSelecionado, null);
		} else {
			pagamentos = lancamentoContaService.buscarPagamentosPorLancamentoPeriodicoEPago(lancamentoSelecionado, null);
			pagamentos.addAll(lancamentoContaService.gerarPrevisaoProximosPagamentos(lancamentoSelecionado, periodo));
		}
			
		// Separa os pagamento de lançamentos fixos de receita dos de despesa
		for (LancamentoConta pagamento : pagamentos) {
			if (pagamento.getLancamentoPeriodico().getTipoLancamento().equals(TipoLancamento.DESPESA)) {
				pagamentosDespesa.add(pagamento);
			} else {
				pagamentosReceita.add(pagamento);
			}
		}
		
		// Envia a lista de pagamento para os respectivos métodos para gerar o gráfico
		gerarGraficoDespesa(pagamentosDespesa);
		gerarGraficoReceita(pagamentosReceita);
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
	
	private void gerarGraficoDespesa(List<LancamentoConta> pagamentos) {
		// Verifica se deve continuar com a geração do gráfico a partir da quantidade de lançamentos
		if (pagamentos == null || pagamentos.size() == 0) {
			exibirGraficoDespesa = false;
			return;
		}
		
		// Instancia o Map que irá gravar os dados para gerar o gráfico
		// É necessário manter o LinkedHashMap para poder preservar a ordenação dos meses e anos
		// no gráfico. Caso contrário, será necessário implementar um Comparator para realizar
		// a ordenação das chaves (em formato String) de acordo com a sequência de meses e anos
		Map<String, Double> dadosPagamento = new LinkedHashMap<String, Double>();
		Map<String, Double> dadosAPagar = new LinkedHashMap<String, Double>();
		String dataKey = "";
		maxValueBarPagamentosDespesa = 1.0;
		saldoDevedor = 0.0;
		
		// Gera as chaves e popula os Maps
		SortedSet<Date> chaves = new TreeSet<>();
		Calendar dataAtual = Calendar.getInstance();
		for (Integer i = 1; i <= periodo; i++) {
			chaves.add(dataAtual.getTime());
			if (periodoAConsiderar.equals("ANTERIOR"))
				dataAtual.add(Calendar.MONTH, -1);
			else
				dataAtual.add(Calendar.MONTH, 1);
		}
		
		// Popula os Maps com as chaves geradas
		for (Date data : chaves) {
			dataKey = new SimpleDateFormat("MM/yyyy").format(data);
			dadosPagamento.put(dataKey, 0.0);
			dadosAPagar.put(dataKey, 0.0);
		}
		
		// Itera a lista de pagamentos para somar no mês/ano correspondente
		for (LancamentoConta pagamento : pagamentos) {
			dataKey = new SimpleDateFormat("MM/yyyy").format(this.determinarChaveMesAnoPagamento(pagamento));
			if (pagamento.getStatusLancamentoConta().equals(StatusLancamentoConta.QUITADO)) {				
				if (dadosPagamento.get(dataKey) != null) {
					if (!pagamento.getLancamentoPeriodico().getMoeda().equals(moedaPadrao)) {
						dadosPagamento.put(dataKey, dadosPagamento.get(dataKey) + (pagamento.getValorPago() * pagamento.getLancamentoPeriodico().getMoeda().getValorConversao()));
					} else {
						dadosPagamento.put(dataKey, dadosPagamento.get(dataKey) + pagamento.getValorPago());
					}
				}
			} else {
				if (dadosAPagar.get(dataKey) != null) {
					if (!pagamento.getLancamentoPeriodico().getMoeda().equals(moedaPadrao)) {
						dadosAPagar.put(dataKey, dadosAPagar.get(dataKey) + (pagamento.getLancamentoPeriodico().getValorParcela() * pagamento.getLancamentoPeriodico().getMoeda().getValorConversao()));
					} else {
						dadosAPagar.put(dataKey, dadosAPagar.get(dataKey) + pagamento.getLancamentoPeriodico().getValorParcela());
					}
				} else {
					if (periodoAConsiderar.equals("ANTERIOR") && pagamento.getDataVencimento().before(new Date())) {
						saldoDevedor += pagamento.getLancamentoPeriodico().getValorParcela();
					}
				}
			}
		}
		
		// Popula o gráfico com os dados obtido e habilita a exibição
		ultimosPagamentosDespesaModel = new BarChartModel();
		ultimosPagamentosDespesaModel.setLegendPosition("s");
		ultimosPagamentosDespesaModel.setTitle("Panorama das Despesas Fixas - Despesa");
		ultimosPagamentosDespesaModel.setStacked(true);		
		ultimosPagamentosDespesaModel.setExtender("ext1");
		
		ChartSeries pagamentosSerie = new ChartSeries();		
		ChartSeries aPagarSerie = new ChartSeries();
		
		pagamentosSerie.setLabel("Despesas Pagas");
		aPagarSerie.setLabel("Despesas À Pagar");
		
		for (String key : dadosPagamento.keySet()) {
			pagamentosSerie.set(key, dadosPagamento.get(key));
			aPagarSerie.set(key, dadosAPagar.get(key));
			
			if ( (dadosPagamento.get(key) + dadosAPagar.get(key) + 100) > maxValueBarPagamentosDespesa) {
				maxValueBarPagamentosDespesa = dadosPagamento.get(key) + dadosAPagar.get(key) + 100;
			}
		}
		
		ultimosPagamentosDespesaModel.addSeries(pagamentosSerie);
		ultimosPagamentosDespesaModel.addSeries(aPagarSerie);
		
		exibirGraficoDespesa = true;
	}
	
	private void gerarGraficoReceita(List<LancamentoConta> pagamentos) {
		// Verifica se deve continuar com a geração do gráfico a partir da quantidade de lançamentos
		if (pagamentos == null || pagamentos.size() == 0) {
			exibirGraficoReceita = false;
			return;
		}
		
		// Instancia o Map que irá gravar os dados para gerar o gráfico
		// É necessário manter o LinkedHashMap para poder preservar a ordenação dos meses e anos
		// no gráfico. Caso contrário, será necessário implementar um Comparator para realizar
		// a ordenação das chaves (em formato String) de acordo com a sequência de meses e anos
		Map<String, Double> dadosPagamento = new LinkedHashMap<String, Double>();
		Map<String, Double> dadosAPagar = new LinkedHashMap<String, Double>();
		String dataKey = "";
		maxValueBarPagamentosReceita = 1.0;
		saldoCredor = 0.0;
		
		// Gera as chaves e popula os Maps
		SortedSet<Date> chaves = new TreeSet<>();
		Calendar dataAtual = Calendar.getInstance();
		for (Integer i = 1; i <= periodo; i++) {
			chaves.add(dataAtual.getTime());
			if (periodoAConsiderar.equals("ANTERIOR"))
				dataAtual.add(Calendar.MONTH, -1);
			else
				dataAtual.add(Calendar.MONTH, 1);
		}
		
		// Popula os Maps com as chaves geradas
		for (Date data : chaves) {
			dataKey = new SimpleDateFormat("MM/yyyy").format(data);
			dadosPagamento.put(dataKey, 0.0);
			dadosAPagar.put(dataKey, 0.0);
		}
		
		// Itera a lista de pagamentos para somar no mês/ano correspondente
		for (LancamentoConta pagamento : pagamentos) {
			dataKey = new SimpleDateFormat("MM/yyyy").format(this.determinarChaveMesAnoPagamento(pagamento));
			if (pagamento.getStatusLancamentoConta().equals(StatusLancamentoConta.QUITADO)) {
				if (dadosPagamento.get(dataKey) != null) {
					if (!pagamento.getLancamentoPeriodico().getMoeda().equals(moedaPadrao)) {
						dadosPagamento.put(dataKey, dadosPagamento.get(dataKey) + (pagamento.getValorPago() * pagamento.getLancamentoPeriodico().getMoeda().getValorConversao()));
					} else {
						dadosPagamento.put(dataKey, dadosPagamento.get(dataKey) + pagamento.getValorPago());
					}
				}
			} else {
				if (dadosAPagar.get(dataKey) != null) {
					if (!pagamento.getLancamentoPeriodico().getMoeda().equals(moedaPadrao)) {
						dadosAPagar.put(dataKey, dadosAPagar.get(dataKey) + (pagamento.getLancamentoPeriodico().getValorParcela() * pagamento.getLancamentoPeriodico().getMoeda().getValorConversao()));
					} else {
						dadosAPagar.put(dataKey, dadosAPagar.get(dataKey) + pagamento.getLancamentoPeriodico().getValorParcela());
					}
				} else {
					if (periodoAConsiderar.equals("ANTERIOR") && pagamento.getDataVencimento().before(new Date())) {
						saldoCredor += pagamento.getLancamentoPeriodico().getValorParcela();
					}
				}
			}
		}
		
		// Popula o gráfico com os dados obtido e habilita a exibição
		ultimosPagamentosReceitaModel = new BarChartModel();
		ultimosPagamentosReceitaModel.setLegendPosition("s");
		ultimosPagamentosReceitaModel.setTitle("Panorama das Despesas Fixas - Receita");
		ultimosPagamentosReceitaModel.setStacked(true);		
		ultimosPagamentosReceitaModel.setExtender("ext1");
		
		ChartSeries pagamentosSerie = new ChartSeries();		
		ChartSeries aPagarSerie = new ChartSeries();
		
		pagamentosSerie.setLabel("Receitas Pagas");
		aPagarSerie.setLabel("Receitas À Pagar");
		
		for (String key : dadosPagamento.keySet()) {
			pagamentosSerie.set(key, dadosPagamento.get(key));
			aPagarSerie.set(key, dadosAPagar.get(key));
			
			if ( (dadosPagamento.get(key) + dadosAPagar.get(key) + 100) > maxValueBarPagamentosReceita) {
				maxValueBarPagamentosReceita = dadosPagamento.get(key) + dadosAPagar.get(key) + 100;
			}
		}
		
		ultimosPagamentosReceitaModel.addSeries(pagamentosSerie);
		ultimosPagamentosReceitaModel.addSeries(aPagarSerie);
		
		exibirGraficoReceita = true;
	}
	
	private Date determinarChaveMesAnoPagamento(LancamentoConta pagamento) {
		if (pagamento.getConta().getTipoConta().equals(TipoConta.CARTAO)) {
			// Determina exatamente o mês/ano que um lançamento do cartão deve ser registrado
			// de acordo com a data de vencimento da fatura do cartão
			Calendar temp = Calendar.getInstance();
			if (pagamento.getStatusLancamentoConta().equals(StatusLancamentoConta.QUITADO)) {
				temp.setTime(pagamento.getDataPagamento());
			} else {
				temp.setTime(pagamento.getDataVencimento());
			}
			// Fechamento < Vencimento = mesmo mês; Fechamento >= Vencimento = mês seguinte
			if (temp.get(Calendar.DAY_OF_MONTH) <= pagamento.getConta().getCartaoCredito().getDiaFechamentoFatura()) {
				temp.set(Calendar.DAY_OF_MONTH, pagamento.getConta().getCartaoCredito().getDiaFechamentoFatura());
			} else {
				temp.set(Calendar.DAY_OF_MONTH, pagamento.getConta().getCartaoCredito().getDiaFechamentoFatura());
				temp.add(Calendar.MONTH, 1);
			}
			return temp.getTime();
		} else {
			if (pagamento.getStatusLancamentoConta().equals(StatusLancamentoConta.QUITADO)) {
				return pagamento.getDataPagamento();
			} else {
				return pagamento.getDataVencimento();
			}
		}
	}
	
	public List<LancamentoPeriodico> getListaLancamentoPeriodico() {
		try {
			return lancamentoPeriodicoService.buscarPorTipoLancamentoEStatusLancamentoPorUsuario(TipoLancamentoPeriodico.FIXO, StatusLancamento.ATIVO, getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<Conta> getListaConta() {
		try {
			return contaService.buscarTodosAtivosPorUsuario(getUsuarioLogado());						
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<SelectItem> getPeriodos() {
		List<SelectItem> periodos = new ArrayList<>();
		for (int i = 1; i <= 12; i++) {
			periodos.add(new SelectItem(Integer.valueOf(i), Integer.toString(i)));
		}
		return periodos;
	}
	
	public List<SelectItem> getListaTipoConta() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (TipoConta tipo : TipoConta.values()) {
			listaSelectItem.add(new SelectItem(tipo, tipo.toString()));
		}
		return listaSelectItem;
	}

	public LancamentoPeriodico getLancamentoSelecionado() {
		return lancamentoSelecionado;
	}

	public void setLancamentoSelecionado(LancamentoPeriodico lancamentoSelecionado) {
		this.lancamentoSelecionado = lancamentoSelecionado;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Moeda getMoedaPadrao() {
		return moedaPadrao;
	}

	public void setMoedaPadrao(Moeda moedaPadrao) {
		this.moedaPadrao = moedaPadrao;
	}

	public boolean isExibirGraficoReceita() {
		return exibirGraficoReceita;
	}

	public void setExibirGraficoReceita(boolean exibirGraficoReceita) {
		this.exibirGraficoReceita = exibirGraficoReceita;
	}

	public boolean isExibirGraficoDespesa() {
		return exibirGraficoDespesa;
	}

	public void setExibirGraficoDespesa(boolean exibirGraficoDespesa) {
		this.exibirGraficoDespesa = exibirGraficoDespesa;
	}

	public Double getMaxValueBarPagamentosDespesa() {
		return maxValueBarPagamentosDespesa;
	}

	public void setMaxValueBarPagamentosDespesa(Double maxValueBarPagamentosDespesa) {
		this.maxValueBarPagamentosDespesa = maxValueBarPagamentosDespesa;
	}

	public Double getMaxValueBarPagamentosReceita() {
		return maxValueBarPagamentosReceita;
	}

	public void setMaxValueBarPagamentosReceita(Double maxValueBarPagamentosReceita) {
		this.maxValueBarPagamentosReceita = maxValueBarPagamentosReceita;
	}

	public String getPeriodoAConsiderar() {
		return periodoAConsiderar;
	}

	public void setPeriodoAConsiderar(String periodoAConsiderar) {
		this.periodoAConsiderar = periodoAConsiderar;
	}

	public double getSaldoCredor() {
		return saldoCredor;
	}

	public void setSaldoCredor(double saldoCredor) {
		this.saldoCredor = saldoCredor;
	}

	public double getSaldoDevedor() {
		return saldoDevedor;
	}

	public void setSaldoDevedor(double saldoDevedor) {
		this.saldoDevedor = saldoDevedor;
	}

	public Conta getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(Conta contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}

	public TipoConta getTipoContaSelecionada() {
		return tipoContaSelecionada;
	}

	public void setTipoContaSelecionada(TipoConta tipoContaSelecionada) {
		this.tipoContaSelecionada = tipoContaSelecionada;
	}

	public BarChartModel getUltimosPagamentosDespesaModel() {
		return ultimosPagamentosDespesaModel;
	}

	public void setUltimosPagamentosDespesaModel(
			BarChartModel ultimosPagamentosDespesaModel) {
		this.ultimosPagamentosDespesaModel = ultimosPagamentosDespesaModel;
	}

	public BarChartModel getUltimosPagamentosReceitaModel() {
		return ultimosPagamentosReceitaModel;
	}

	public void setUltimosPagamentosReceitaModel(
			BarChartModel ultimosPagamentosReceitaModel) {
		this.ultimosPagamentosReceitaModel = ultimosPagamentosReceitaModel;
	}
}
