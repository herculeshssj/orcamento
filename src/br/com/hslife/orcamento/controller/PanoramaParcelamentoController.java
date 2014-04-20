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

import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.PagamentoPeriodo;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
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
	
	private LancamentoPeriodico lancamentoSelecionado;
	private Moeda moedaPadrao;
	private String periodoAConsiderar;
	private Integer periodo;
	private boolean exibirGraficoReceita;
	private boolean exibirGraficoDespesa;
	
	private Double maxValueBarPagamentosDespesa = 1.0;
	private Double maxValueBarPagamentosReceita = 1.0;
	
	private CartesianChartModel ultimosPagamentosDespesaModel;
	private CartesianChartModel ultimosPagamentosReceitaModel;
	
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
		this.carregarMoedaPadrao();
		return "/pages/ResumoEstatistica/panoramaParcelamento";
	}

	public void find() {
		try {			
			// Traz os pagamentos quitados do lançamento fixo selecionado, ou todos se nenhum for selecionado
			List<PagamentoPeriodo> pagamentos = new ArrayList<>();
			List<PagamentoPeriodo> pagamentosReceita = new ArrayList<>();
			List<PagamentoPeriodo> pagamentosDespesa = new ArrayList<>();
			if (lancamentoSelecionado == null) {
				pagamentos = lancamentoPeriodicoService.buscarPagamentosPorTipoLancamentoEUsuarioEPago(TipoLancamentoPeriodico.PARCELADO, getUsuarioLogado(), null);
			} else {
				pagamentos = lancamentoPeriodicoService.buscarPagamentosPorLancamentoPeriodicoEPago(lancamentoSelecionado, null);
			}
			if (pagamentos == null || pagamentos.size() == 0) {
				warnMessage("O lançamento selecionado não contém pagamentos registrados!");
				return;
			}
			
			// Separa os pagamento de lançamentos fixos de receita dos de despesa
			for (PagamentoPeriodo pagamento : pagamentos) {
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
	
	private void gerarGraficoDespesa(List<PagamentoPeriodo> pagamentos) {
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
			System.out.println(dataKey);
		}
		
		// Itera a lista de pagamentos para somar no mês/ano correspondente
		for (PagamentoPeriodo pagamento : pagamentos) {
			
			// Converte o valor do pagamento para a moeda padrão
			if (!pagamento.getLancamentoPeriodico().getMoeda().equals(moedaPadrao)) {
				pagamento.setValorPago(pagamento.getValorPago() * pagamento.getLancamentoPeriodico().getMoeda().getValorConversao());
			}
			
			// Verifica se o pagamento foi realizado ou não para colocar no Map correspondente
			if (pagamento.isPago()) {
				dataKey = new SimpleDateFormat("MM/yyyy").format(pagamento.getDataPagamento());
				
				// Busca a entrada no Map e soma o valor do pagamento do período
				if (dadosPagamento.get(dataKey) != null) {
					dadosPagamento.put(dataKey, dadosPagamento.get(dataKey) + pagamento.getValorPago());
					
					// Determina o valor máximo do eixo Y
					if (dadosPagamento.get(dataKey) > maxValueBarPagamentosDespesa)
						maxValueBarPagamentosDespesa = dadosPagamento.get(dataKey) + 100;
				}
			} else {
				dataKey = new SimpleDateFormat("MM/yyyy").format(pagamento.getDataVencimento());
				
				// Busca a entrada no Map e soma o valor do pagamento do período
				if (dadosAPagar.get(dataKey) != null) {
					dadosAPagar.put(dataKey, dadosAPagar.get(dataKey) + pagamento.getLancamentoPeriodico().getValorParcela());
					
					// Determina o valor máximo do eixo Y
					if (dadosAPagar.get(dataKey) > maxValueBarPagamentosDespesa)
						maxValueBarPagamentosDespesa = dadosAPagar.get(dataKey) + 100;
				}
			}	
		}
		
		// Popula o gráfico com os dados obtido e habilita a exibição
		ultimosPagamentosDespesaModel = new CartesianChartModel();
		
		ChartSeries pagamentosSerie = new ChartSeries();
		pagamentosSerie.setLabel("Parcelas Pagas");
		for (String key : dadosPagamento.keySet()) {
			pagamentosSerie.set(key, dadosPagamento.get(key));
		}
		
		ultimosPagamentosDespesaModel.addSeries(pagamentosSerie);
		
		ChartSeries aPagarSerie = new ChartSeries();
		aPagarSerie.setLabel("Parcelas À Pagar");
		for (String key : dadosAPagar.keySet()) {
			aPagarSerie.set(key, dadosAPagar.get(key));
		}
		
		ultimosPagamentosDespesaModel.addSeries(aPagarSerie);
		
		exibirGraficoDespesa = true;
	}
	
	private void gerarGraficoReceita(List<PagamentoPeriodo> pagamentos) {
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
			System.out.println(dataKey);
		}
		
		// Itera a lista de pagamentos para somar no mês/ano correspondente
		for (PagamentoPeriodo pagamento : pagamentos) {
			
			// Converte o valor do pagamento para a moeda padrão
			if (!pagamento.getLancamentoPeriodico().getMoeda().equals(moedaPadrao)) {
				pagamento.setValorPago(pagamento.getValorPago() * pagamento.getLancamentoPeriodico().getMoeda().getValorConversao());
			}
			
			// Verifica se o pagamento foi realizado ou não para colocar no Map correspondente
			if (pagamento.isPago()) {
				dataKey = new SimpleDateFormat("MM/yyyy").format(pagamento.getDataPagamento());
				
				// Busca a entrada no Map e soma o valor do pagamento do período
				if (dadosPagamento.get(dataKey) != null) {
					dadosPagamento.put(dataKey, dadosPagamento.get(dataKey) + pagamento.getValorPago());
					
					// Determina o valor máximo do eixo Y
					if (dadosPagamento.get(dataKey) > maxValueBarPagamentosReceita)
						maxValueBarPagamentosReceita = dadosPagamento.get(dataKey) + 100;
				}
			} else {
				dataKey = new SimpleDateFormat("MM/yyyy").format(pagamento.getDataVencimento());
				
				// Busca a entrada no Map e soma o valor do pagamento do período
				if (dadosAPagar.get(dataKey) != null) {
					dadosAPagar.put(dataKey, dadosAPagar.get(dataKey) + pagamento.getLancamentoPeriodico().getValorParcela());
					
					// Determina o valor máximo do eixo Y
					if (dadosAPagar.get(dataKey) > maxValueBarPagamentosReceita)
						maxValueBarPagamentosReceita = dadosAPagar.get(dataKey) + 100;
				}
			}	
		}
		
		// Popula o gráfico com os dados obtido e habilita a exibição
		ultimosPagamentosReceitaModel = new CartesianChartModel();
		
		ChartSeries pagamentosSerie = new ChartSeries();
		pagamentosSerie.setLabel("Parcelas Pagas");
		for (String key : dadosPagamento.keySet()) {
			pagamentosSerie.set(key, dadosPagamento.get(key));
		}
		
		ultimosPagamentosReceitaModel.addSeries(pagamentosSerie);
		
		ChartSeries aPagarSerie = new ChartSeries();
		aPagarSerie.setLabel("Parcelas À Pagar");
		for (String key : dadosAPagar.keySet()) {
			aPagarSerie.set(key, dadosAPagar.get(key));
		}
		
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
	
	public List<SelectItem> getPeriodos() {
		List<SelectItem> periodos = new ArrayList<>();
		for (int i = 1; i <= 12; i++) {
			periodos.add(new SelectItem(Integer.valueOf(i), Integer.toString(i)));
		}
		return periodos;
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
}