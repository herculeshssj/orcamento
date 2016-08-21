/***
  
  	Copyright (c) 2012 - 2016 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.LineChartSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.DetalheOrcamento;
import br.com.hslife.orcamento.entity.Orcamento;
import br.com.hslife.orcamento.enumeration.AbrangenciaOrcamento;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.facade.IOrcamento;
import br.com.hslife.orcamento.util.DetalheOrcamentoComparator;

@Component("panoramaOrcamentoMB")
@Scope("session")
public class PanoramaOrcamentoController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2448702914903556797L;
	
	@Autowired
	private IOrcamento orcamentoService;
	
	private List<DetalheOrcamento> listaDetalheOrcamento = new ArrayList<DetalheOrcamento>();
	private Orcamento orcamentoSelecionado;
	
	private BarChartModel orcamentoModel;
	private BarChartModel orcamentoModelCredito;
	private BarChartModel orcamentoModelDebito;
	
	private boolean exibirGraficoCredito = false;
	private boolean exibirGraficoDebito = false;
	private boolean mostrarInformacao = false;
	
	public PanoramaOrcamentoController() {
		moduleTitle = "Panorama do Orçamento";
	}
	
	@PostConstruct
	public String startUp() {
		return "/pages/ResumoEstatistica/panoramaOrcamento";
	}
	
	@Override
	protected void initializeEntity() {
		orcamentoSelecionado = null;
		listaDetalheOrcamento = new ArrayList<DetalheOrcamento>();
	}
	
	public void buscarEAtualizar() {
		atualizarValores();
		gerarGrafico();
		mostrarInformacao = true;
	}
	
	private void gerarGrafico() {
		this.gerarGraficoCreditos();
		this.gerarGraficoDebitos();
	}
	
	private void gerarGraficoCreditos() {
		orcamentoModelCredito = new BarChartModel();
		
		int yMinimo = 0;
		int yMaximo = 0;
		
		BarChartSeries previstoSeries = new BarChartSeries();
		previstoSeries.setLabel("Previsto");
		
		for (DetalheOrcamento detalhe : listaDetalheOrcamento) {
			if (detalhe.getTipoCategoria().equals(TipoCategoria.DEBITO))
				continue;
						
			previstoSeries.set(detalhe.getDescricao(), detalhe.getPrevisao());
			
			if (detalhe.getPrevisao() >= 0) {
				if (detalhe.getPrevisao() > yMaximo) yMaximo = new BigDecimal(detalhe.getPrevisao() + 100).intValue();
			} else {
				if (detalhe.getPrevisao() < yMinimo) yMinimo = new BigDecimal(detalhe.getPrevisao() + 100).intValue();
			}
		}
		
		LineChartSeries realizadoSeries = new LineChartSeries();
		realizadoSeries.setLabel("Realizado");
		
		for (DetalheOrcamento detalhe : listaDetalheOrcamento) {
			if (detalhe.getTipoCategoria().equals(TipoCategoria.DEBITO))
				continue;
			
			realizadoSeries.set(detalhe.getDescricao(), detalhe.getRealizado());
			
			if (detalhe.getRealizado() >= 0) {
				if (detalhe.getRealizado() > yMaximo) yMaximo = new BigDecimal(detalhe.getRealizado() + 100).intValue();
			} else {
				if (detalhe.getRealizado() < yMinimo) yMinimo = new BigDecimal(detalhe.getRealizado() + 100).intValue();
			}
		}
 
		orcamentoModelCredito.addSeries(previstoSeries);
		orcamentoModelCredito.addSeries(realizadoSeries);
		
		orcamentoModelCredito.setTitle(orcamentoSelecionado.getDescricao());
		orcamentoModelCredito.setLegendPosition("s");
		orcamentoModelCredito.setExtender("ext1");
		
		Axis xAxis = orcamentoModelCredito.getAxis(AxisType.X);
		Axis yAxis = orcamentoModelCredito.getAxis(AxisType.Y);
		
		xAxis.setTickAngle(-90);
		yAxis.setMin(yMinimo);
		yAxis.setMax(yMaximo);
		
		orcamentoModelCredito.getAxes().put(AxisType.X, xAxis);
		orcamentoModelCredito.getAxes().put(AxisType.Y, yAxis);
		
		exibirGraficoCredito = true;
	}
	
	private void gerarGraficoDebitos() {
		orcamentoModelDebito = new BarChartModel();
		
		int yMinimo = 0;
		int yMaximo = 0;
		
		BarChartSeries previstoSeries = new BarChartSeries();
		previstoSeries.setLabel("Previsto");
		
		for (DetalheOrcamento detalhe : listaDetalheOrcamento) {
			if (detalhe.getTipoCategoria().equals(TipoCategoria.CREDITO))
				continue;
						
			previstoSeries.set(detalhe.getDescricao(), detalhe.getPrevisao());
			
			if (detalhe.getPrevisao() >= 0) {
				if (detalhe.getPrevisao() > yMaximo) yMaximo = new BigDecimal(detalhe.getPrevisao() + 100).intValue();
			} else {
				if (detalhe.getPrevisao() < yMinimo) yMinimo = new BigDecimal(detalhe.getPrevisao() + 100).intValue();
			}
		}
		
		LineChartSeries realizadoSeries = new LineChartSeries();
		realizadoSeries.setLabel("Realizado");
		
		for (DetalheOrcamento detalhe : listaDetalheOrcamento) {
			if (detalhe.getTipoCategoria().equals(TipoCategoria.CREDITO))
				continue;
			
			realizadoSeries.set(detalhe.getDescricao(), detalhe.getRealizado());
			
			if (detalhe.getRealizado() >= 0) {
				if (detalhe.getRealizado() > yMaximo) yMaximo = new BigDecimal(detalhe.getRealizado() + 100).intValue();
			} else {
				if (detalhe.getRealizado() < yMinimo) yMinimo = new BigDecimal(detalhe.getRealizado() + 100).intValue();
			}
		}
 
		orcamentoModelDebito.addSeries(previstoSeries);
		orcamentoModelDebito.addSeries(realizadoSeries);
		
		orcamentoModelDebito.setTitle(orcamentoSelecionado.getDescricao());
		orcamentoModelDebito.setLegendPosition("s");
		orcamentoModelDebito.setExtender("ext1");
		
		Axis xAxis = orcamentoModelDebito.getAxis(AxisType.X);
		Axis yAxis = orcamentoModelDebito.getAxis(AxisType.Y);
		
		xAxis.setTickAngle(-90);
		yAxis.setMin(yMinimo);
		yAxis.setMax(yMaximo);
		
		orcamentoModelDebito.getAxes().put(AxisType.X, xAxis);
		orcamentoModelDebito.getAxes().put(AxisType.Y, yAxis);
		
		exibirGraficoDebito = true;
	}
	
	private void find() {
		if (orcamentoSelecionado != null) {
			listaDetalheOrcamento.clear();
			listaDetalheOrcamento.addAll(orcamentoSelecionado.getDetalhes());			
			Collections.sort(listaDetalheOrcamento, new DetalheOrcamentoComparator());
		}
	}
	
	private void atualizarValores() {
		try {
			orcamentoService.atualizarValores(orcamentoSelecionado);
			infoMessage("Valores do orçamento atualizados com sucesso!");
			
			if (getOpcoesSistema().getExibirBuscasRealizadas()) {
				find();
			} else {
				initializeEntity();
			}
			
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public List<Orcamento> getListaOrcamentoCategoria() {
		try {
			return orcamentoService.buscarAbrangeciaPorUsuario(AbrangenciaOrcamento.CATEGORIA, getUsuarioLogado());
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Orcamento>();
	}

	public Orcamento getOrcamentoSelecionado() {
		return orcamentoSelecionado;
	}

	public void setOrcamentoSelecionado(Orcamento orcamentoSelecionado) {
		this.orcamentoSelecionado = orcamentoSelecionado;
	}

	public List<DetalheOrcamento> getListaDetalheOrcamento() {
		return listaDetalheOrcamento;
	}

	public void setListaDetalheOrcamento(
			List<DetalheOrcamento> listaDetalheOrcamento) {
		this.listaDetalheOrcamento = listaDetalheOrcamento;
	}

	public BarChartModel getOrcamentoModel() {
		return orcamentoModel;
	}

	public void setOrcamentoModel(BarChartModel orcamentoModel) {
		this.orcamentoModel = orcamentoModel;
	}

	public boolean isMostrarInformacao() {
		return mostrarInformacao;
	}

	public void setMostrarInformacao(boolean mostrarInformacao) {
		this.mostrarInformacao = mostrarInformacao;
	}

	public BarChartModel getOrcamentoModelCredito() {
		return orcamentoModelCredito;
	}

	public void setOrcamentoModelCredito(BarChartModel orcamentoModelCredito) {
		this.orcamentoModelCredito = orcamentoModelCredito;
	}

	public BarChartModel getOrcamentoModelDebito() {
		return orcamentoModelDebito;
	}

	public void setOrcamentoModelDebito(BarChartModel orcamentoModelDebito) {
		this.orcamentoModelDebito = orcamentoModelDebito;
	}

	public boolean isExibirGraficoCredito() {
		return exibirGraficoCredito;
	}

	public void setExibirGraficoCredito(boolean exibirGraficoCredito) {
		this.exibirGraficoCredito = exibirGraficoCredito;
	}

	public boolean isExibirGraficoDebito() {
		return exibirGraficoDebito;
	}

	public void setExibirGraficoDebito(boolean exibirGraficoDebito) {
		this.exibirGraficoDebito = exibirGraficoDebito;
	}
}