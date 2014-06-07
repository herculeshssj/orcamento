/***
  
  	Copyright (c) 2012, 2013, 2014 Hércules S. S. José

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

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site HSlife
    
    no endereco www.hslife.com.br ou escreva para a Fundação do Software 
    
    Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor acesse o 

    endereço www.hslife.com.br, pelo e-mail contato@hslife.com.br ou escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
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

import javax.faces.model.SelectItem;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.ILancamentoPeriodico;
import br.com.hslife.orcamento.facade.IMoeda;

@Component("panoramaParcelamentoMB")
@Scope("session")
public class PanoramaParcelamentoController extends AbstractController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -330108799008412944L;

	@Autowired
	private ILancamentoPeriodico lancamentoPeriodicoService;
	
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
	
	private CartesianChartModel ultimosPagamentosDespesaModel;
	private CartesianChartModel ultimosPagamentosReceitaModel;
	
	private double saldoCredor;
	private double saldoDevedor;
	
	public PanoramaParcelamentoController() {
		moduleTitle = "Panorama dos Parcelamento";
	}

	@Override
	protected void initializeEntity() {
	}

	@Override
	public String startUp() {
		exibirGraficoDespesa = false;
		exibirGraficoReceita = false;
		saldoCredor = 0.0;
		saldoDevedor = 0.0;
		this.carregarMoedaPadrao();
		return "/pages/ResumoEstatistica/panoramaParcelamento";
	}

	public void find() {
		try {			
			// Traz os pagamentos quitados do lançamento fixo selecionado, ou todos se nenhum for selecionado
			List<LancamentoConta> pagamentos = new ArrayList<>();
			List<LancamentoConta> pagamentosReceita = new ArrayList<>();
			List<LancamentoConta> pagamentosDespesa = new ArrayList<>();
			
			switch (getOpcoesSistema().getFormaAgrupamentoPagamento()) {
				case "INDIVIDUAL" : 
					if (lancamentoSelecionado != null) {
						pagamentos = lancamentoPeriodicoService.buscarPagamentosPorLancamentoPeriodicoEPago(lancamentoSelecionado, null);
						break;
					}						
				case "CONTA": 
					if (contaSelecionada != null) {
						pagamentos = lancamentoPeriodicoService.buscarPagamentosPorTipoLancamentoEContaEPago(TipoLancamentoPeriodico.PARCELADO, contaSelecionada, null);
						break;
					}
				case "TIPO_CONTA": 
					if (tipoContaSelecionada != null) {
						pagamentos = lancamentoPeriodicoService.buscarPagamentosPorTipoLancamentoETipoContaEPago(TipoLancamentoPeriodico.PARCELADO, tipoContaSelecionada, null);
						break;
					}
				default: pagamentos = lancamentoPeriodicoService.buscarPagamentosPorTipoLancamentoEUsuarioEPago(TipoLancamentoPeriodico.PARCELADO, getUsuarioLogado(), null);
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
			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	private void carregarMoedaPadrao() {
		try {
			if (moedaPadrao == null) {
				moedaPadrao = moedaService.buscarPadraoPorUsuario(getUsuarioLogado());
			}
		} catch (BusinessException be) {
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
			if (pagamento.isQuitado()) {
				dataKey = new SimpleDateFormat("MM/yyyy").format(pagamento.getDataPagamento());
				if (dadosPagamento.get(dataKey) != null) {
					if (!pagamento.getLancamentoPeriodico().getMoeda().equals(moedaPadrao)) {
						dadosPagamento.put(dataKey, dadosPagamento.get(dataKey) + (pagamento.getValorPago() * pagamento.getLancamentoPeriodico().getMoeda().getValorConversao()));
					} else {
						dadosPagamento.put(dataKey, dadosPagamento.get(dataKey) + pagamento.getValorPago());
					}
				}
			} else {
				dataKey = new SimpleDateFormat("MM/yyyy").format(pagamento.getDataVencimento());
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
		ultimosPagamentosDespesaModel = new CartesianChartModel();
		
		ChartSeries pagamentosSerie = new ChartSeries();		
		ChartSeries aPagarSerie = new ChartSeries();
		
		pagamentosSerie.setLabel("Parcelas Pagas");
		aPagarSerie.setLabel("Parcelas À Pagar");
		
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
			if (pagamento.isQuitado()) {
				dataKey = new SimpleDateFormat("MM/yyyy").format(pagamento.getDataPagamento());
				if (dadosPagamento.get(dataKey) != null) {
					if (!pagamento.getLancamentoPeriodico().getMoeda().equals(moedaPadrao)) {
						dadosPagamento.put(dataKey, dadosPagamento.get(dataKey) + (pagamento.getValorPago() * pagamento.getLancamentoPeriodico().getMoeda().getValorConversao()));
					} else {
						dadosPagamento.put(dataKey, dadosPagamento.get(dataKey) + pagamento.getValorPago());
					}
				}
			} else {
				dataKey = new SimpleDateFormat("MM/yyyy").format(pagamento.getDataVencimento());
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
		ultimosPagamentosReceitaModel = new CartesianChartModel();
		
		ChartSeries pagamentosSerie = new ChartSeries();		
		ChartSeries aPagarSerie = new ChartSeries();
		
		pagamentosSerie.setLabel("Parcelas Pagas");
		aPagarSerie.setLabel("Parcelas À Pagar");
		
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
	
	public List<LancamentoPeriodico> getListaLancamentoPeriodico() {
		try {
			return lancamentoPeriodicoService.buscarPorTipoLancamentoEStatusLancamentoPorUsuario(TipoLancamentoPeriodico.PARCELADO, StatusLancamento.ATIVO, getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<Conta> getListaConta() {
		try {
			return contaService.buscarTodosAtivosPorUsuario(getUsuarioLogado());						
		} catch (BusinessException be) {
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

	public CartesianChartModel getUltimosPagamentosDespesaModel() {
		return ultimosPagamentosDespesaModel;
	}

	public void setUltimosPagamentosDespesaModel(
			CartesianChartModel ultimosPagamentosDespesaModel) {
		this.ultimosPagamentosDespesaModel = ultimosPagamentosDespesaModel;
	}

	public CartesianChartModel getUltimosPagamentosReceitaModel() {
		return ultimosPagamentosReceitaModel;
	}

	public void setUltimosPagamentosReceitaModel(
			CartesianChartModel ultimosPagamentosReceitaModel) {
		this.ultimosPagamentosReceitaModel = ultimosPagamentosReceitaModel;
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
}