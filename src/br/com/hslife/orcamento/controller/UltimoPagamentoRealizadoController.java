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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ILancamentoPeriodico;
import br.com.hslife.orcamento.facade.IMoeda;

@Component("ultimoPagamentoRealizadoMB")
@Scope("session")
public class UltimoPagamentoRealizadoController extends AbstractController {
	
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
	private String dataAConsiderar;
	private boolean agrupar;
	private Integer periodo;
	private boolean exibirGrafico;
	private Double maxValueBarPagamentos = 1.0;
	
	private CartesianChartModel ultimosPagamentosModel;
	
	public UltimoPagamentoRealizadoController() {
		moduleTitle = "Últimos Pagamentos Realizados";
	}

	@Override
	protected void initializeEntity() {
	}

	@Override
	public String startUp() {
		exibirGrafico = false;
		this.carregarMoedaPadrao();
		return "/pages/ResumoEstatistica/ultimoPagamentoRealizado";
	}

	public void find() {
		try {
			if (lancamentoSelecionado == null) {
				warnMessage("Selecione um lançamento!");
				return;
			}
			// Traz os pagamentos quitados do lançamento fixo selecionado
			List<PagamentoPeriodo> pagamentos = lancamentoPeriodicoService.buscarPagamentosPagosPorLancamentoPeriodico(lancamentoSelecionado);
			if (pagamentos == null || pagamentos.size() == 0) {
				warnMessage("O lançamento selecionado não contém pagamentos registrados!");
				return;
			}
			gerarGrafico(pagamentos);
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
	
	private void gerarGrafico(List<PagamentoPeriodo> pagamentos) {
		// Instancia o Map que irá gravar os dados que irão gerar o gráfico
		Map<String, Double> dadosPagamento = new HashMap<String, Double>();
		String dataKey = "";
		int quantIteracoes = 0;
		
		// Converte o valor do pagamento para a moeda padrão
		for (PagamentoPeriodo pagamento : pagamentos) {
			if (!pagamento.getLancamentoPeriodico().getMoeda().equals(moedaPadrao)) {
				pagamento.setValorPago(pagamento.getValorPago() * pagamento.getLancamentoPeriodico().getMoeda().getValorConversao());
			}
		}
						
		if (agrupar) {
			// Gera as entradas de acordo com a quantidade de períodos
			Calendar dataAtual = Calendar.getInstance();
			for (Integer i = 1; i <= periodo; i++) {
				dataKey = new SimpleDateFormat("MM/yyyy").format(dataAtual.getTime());
				dadosPagamento.put(dataKey, 0.0);
				dataAtual.add(Calendar.MONTH, -1);
			}
			
			// Itera a lista de pagamentos para somar no mês/ano correspondente
			for (PagamentoPeriodo pagamento : pagamentos) {
				// Determina qual data será usada
				if (dataAConsiderar.equals("VENCIMENTO")) {
					dataKey = new SimpleDateFormat("MM/yyyy").format(pagamento.getDataVencimento());
				} else {
					dataKey = new SimpleDateFormat("MM/yyyy").format(pagamento.getDataPagamento());
				}	
				// Busca a entrada no Map e soma o valor do pagamento do período
				if (dadosPagamento.get(dataKey) != null) {
					dadosPagamento.put(dataKey, dadosPagamento.get(dataKey) + pagamento.getValorPago());
					
					// Determina o valor máximo do eixo Y
					maxValueBarPagamentos = dadosPagamento.get(dataKey) + 100;
				}	
				
				quantIteracoes++;
				if (quantIteracoes == periodo) break;
			}
		} else {
			for (PagamentoPeriodo pagamento : pagamentos) {
				if (dataAConsiderar.equals("VENCIMENTO")) {
					dataKey = new SimpleDateFormat("dd/MM/yyyy").format(pagamento.getDataVencimento());
				} else {
					dataKey = new SimpleDateFormat("dd/MM/yyyy").format(pagamento.getDataPagamento());
				}	
				
				// Busca a entrada no Map e soma o valor do pagamento do período
				dadosPagamento.put(dataKey, pagamento.getValorPago());
				
				// Determina o valor máximo do eixo Y
				if (pagamento.getValorPago() > maxValueBarPagamentos)
					maxValueBarPagamentos = dadosPagamento.get(dataKey) + 100;
				
				quantIteracoes++;
				if (quantIteracoes == periodo) break;
			}
		}
		
		// Popula o gráfico com os dados obtido e habilita a exibição
		ultimosPagamentosModel = new CartesianChartModel();
		
		ChartSeries pagamentosSerie = new ChartSeries();
		pagamentosSerie.setLabel("Pagamento efetuado");
		for (String key : dadosPagamento.keySet()) {
			pagamentosSerie.set(key, dadosPagamento.get(key));
		}
		
		ultimosPagamentosModel.addSeries(pagamentosSerie);
		
		exibirGrafico = true;
	}
	
	public List<LancamentoPeriodico> getListaLancamentoPeriodico() {
		try {
			return lancamentoPeriodicoService.buscarPorTipoLancamentoContaEStatusLancamento(TipoLancamentoPeriodico.FIXO, null, StatusLancamento.ATIVO);
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

	public String getDataAConsiderar() {
		return dataAConsiderar;
	}

	public void setDataAConsiderar(String dataAConsiderar) {
		this.dataAConsiderar = dataAConsiderar;
	}

	public boolean isAgrupar() {
		return agrupar;
	}

	public void setAgrupar(boolean agrupar) {
		this.agrupar = agrupar;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public boolean isExibirGrafico() {
		return exibirGrafico;
	}

	public void setExibirGrafico(boolean exibirGrafico) {
		this.exibirGrafico = exibirGrafico;
	}

	public CartesianChartModel getUltimosPagamentosModel() {
		return ultimosPagamentosModel;
	}

	public void setUltimosPagamentosModel(CartesianChartModel ultimosPagamentosModel) {
		this.ultimosPagamentosModel = ultimosPagamentosModel;
	}

	public Double getMaxValueBarPagamentos() {
		return maxValueBarPagamentos;
	}

	public void setMaxValueBarPagamentos(Double maxValueBarPagamentos) {
		this.maxValueBarPagamentos = maxValueBarPagamentos;
	}

	public Moeda getMoedaPadrao() {
		return moedaPadrao;
	}

	public void setMoedaPadrao(Moeda moedaPadrao) {
		this.moedaPadrao = moedaPadrao;
	}
}