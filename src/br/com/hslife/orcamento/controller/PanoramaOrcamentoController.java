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
import br.com.hslife.orcamento.exception.BusinessException;
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
	private TipoCategoria tipoCategoria;
	private BarChartModel orcamentoModel;
	private boolean exibirGrafico = false;

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
		exibirGrafico = false;
	}
	
	public void buscarEAtualizar() {
		atualizarValores();
		gerarGraficoDashboard();		
	}
	
	private void gerarGraficoDashboard() {
		orcamentoModel = new BarChartModel();
		
		int yMinimo = 0;
		int yMaximo = 0;
		
		BarChartSeries previstoSeries = new BarChartSeries();
		previstoSeries.setLabel("Previsto");
		
		for (DetalheOrcamento detalhe : listaDetalheOrcamento) {
			if (tipoCategoria != null && !detalhe.getTipoCategoria().equals(tipoCategoria))
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
			if (tipoCategoria != null && !detalhe.getTipoCategoria().equals(tipoCategoria))
				continue;
			
			realizadoSeries.set(detalhe.getDescricao(), detalhe.getRealizado());
			
			if (detalhe.getRealizado() >= 0) {
				if (detalhe.getRealizado() > yMaximo) yMaximo = new BigDecimal(detalhe.getRealizado() + 100).intValue();
			} else {
				if (detalhe.getRealizado() < yMinimo) yMinimo = new BigDecimal(detalhe.getRealizado() + 100).intValue();
			}
		}
 
		orcamentoModel.addSeries(previstoSeries);
		orcamentoModel.addSeries(realizadoSeries);
		
		orcamentoModel.setTitle(orcamentoSelecionado.getDescricao());
		orcamentoModel.setLegendPosition("s");
		orcamentoModel.setExtender("ext1");
		
		Axis xAxis = orcamentoModel.getAxis(AxisType.X);
		Axis yAxis = orcamentoModel.getAxis(AxisType.Y);
		
		xAxis.setTickAngle(-90);
		yAxis.setMin(yMinimo);
		yAxis.setMax(yMaximo);
		
		orcamentoModel.getAxes().put(AxisType.X, xAxis);
		orcamentoModel.getAxes().put(AxisType.Y, yAxis);
		
		exibirGrafico = true;
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
			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public List<Orcamento> getListaOrcamentoCategoria() {
		try {
			return orcamentoService.buscarAbrangeciaPorUsuario(AbrangenciaOrcamento.CATEGORIA, getUsuarioLogado());
		} catch (BusinessException be) {
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

	public TipoCategoria getTipoCategoria() {
		return tipoCategoria;
	}

	public void setTipoCategoria(TipoCategoria tipoCategoria) {
		this.tipoCategoria = tipoCategoria;
	}

	public BarChartModel getOrcamentoModel() {
		return orcamentoModel;
	}

	public void setOrcamentoModel(BarChartModel orcamentoModel) {
		this.orcamentoModel = orcamentoModel;
	}

	public boolean isExibirGrafico() {
		return exibirGrafico;
	}

	public void setExibirGrafico(boolean exibirGrafico) {
		this.exibirGrafico = exibirGrafico;
	}
}