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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.DetalheOrcamento;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.Orcamento;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoOrcamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.IResumoEstatistica;
import br.com.hslife.orcamento.model.ResumoMensalContas;

@Component("resumoMensalContasMB")
@Scope("session")
public class ResumoMensalContasController extends AbstractController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1202530011221580347L;

	private boolean lancamentoAgendado;
	private double saldoTotalContas;
	
	@Autowired
	private IResumoEstatistica service;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private ILancamentoConta lancamentoContaService;
	
	@Autowired
	private OrcamentoController orcamentoMB;
	
	private Conta contaSelecionada;
	private FechamentoPeriodo fechamentoSelecionado;
	private ResumoMensalContas resumoMensal;
	
	private PieChartModel pieCategoriaCredito;
	private PieChartModel pieCategoriaDebito;
	
	private BarChartModel barComparativo = new BarChartModel();  
	
	private Integer maxValueBarComparativo = 100;
	
	private String styleCorrectionCredito = "width:820px;height:600px";
	private String styleCorrectionDebito = "width:820px;height:600px";
	
	public ResumoMensalContasController() {
		// Inicializa os gráficos com um valor default
		pieCategoriaCredito = new PieChartModel();
		pieCategoriaCredito.set("Sem dados", 1);
		
		pieCategoriaDebito = new PieChartModel();
		pieCategoriaDebito.set("Sem dados", 1);
		
		moduleTitle = "Resumo Mensal das Contas";
	}

	@Override
	protected void initializeEntity() {}
	
	@Override
	public String startUp() {		
		return "/pages/ResumoEstatistica/resumoMensalContas";
	}
	
	public String find() {
		try {
			resumoMensal = getService().gerarRelatorioResumoMensalContas(contaSelecionada, fechamentoSelecionado);
			this.gerarGraficoCreditoDebito();
			this.gerarGraficoComparativo();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	private void gerarGraficoCreditoDebito() {
		pieCategoriaCredito = new PieChartModel();
		pieCategoriaDebito = new PieChartModel();
		int pieSizeC = 1; // POG para realizar a correção no style do Pie Chart >:(
		int pieSizeD = 1; // POG para realizar a correção no style do Pie Chart >:(
		
		pieCategoriaCredito.setTitle("Receitas do período");
		pieCategoriaCredito.setLegendPosition("s");
		pieCategoriaCredito.setShowDataLabels(true);
		pieCategoriaCredito.setExtender("ext1");
		
		pieCategoriaDebito.setTitle("Despesas do período");
		pieCategoriaDebito.setLegendPosition("s");
		pieCategoriaDebito.setShowDataLabels(true);
		pieCategoriaDebito.setExtender("ext1");
		
		for (Categoria categoria : resumoMensal.getCategorias()) {
			if (!categoria.getDescricao().equals("Saldo atual")) {
				if (categoria.getTipoCategoria().equals(TipoCategoria.CREDITO)) {
					pieCategoriaCredito.set(categoria.getDescricao(), categoria.getSaldoPago());
					pieSizeC++;
				} else {
					pieCategoriaDebito.set(categoria.getDescricao(), categoria.getSaldoPago());
					pieSizeD++;
				}
			}			
		}
		
		// POG para realizar a correção no style do Pie Chart >:(
		styleCorrectionCredito = "height:" + (600 + (30 * pieSizeC)) + "px";
		styleCorrectionDebito = "height:" + (600 + (30 * pieSizeD)) + "px";
	}
	
	private void gerarGraficoComparativo() {
		barComparativo = new BarChartModel();
		barComparativo.setLegendPosition("s");
		barComparativo.setTitle("Comparativo Receitas X Despesas");
		barComparativo.setExtender("ext1");
		double receitas = 0;
		double despesas = 0;
		
		for (Categoria categoria : resumoMensal.getCategorias()) {
			if (!categoria.getDescricao().equals("Saldo atual")) {
				if (categoria.getTipoCategoria().equals(TipoCategoria.CREDITO)) {
					receitas += categoria.getSaldoPago();
				} else {
					despesas += categoria.getSaldoPago();
				}
			}			
		}
		
		ChartSeries receitaSeries = new ChartSeries();
		receitaSeries.setLabel("Receitas");		
		receitaSeries.set(fechamentoSelecionado == null ? "Periodo atual" : fechamentoSelecionado.getLabel(), Math.abs(receitas));
		
		
		ChartSeries despesaSeries = new ChartSeries();
		despesaSeries.setLabel("Despesas");
		despesaSeries.set(fechamentoSelecionado == null ? "Periodo atual" : fechamentoSelecionado.getLabel(), Math.abs(despesas));
		
        barComparativo.addSeries(receitaSeries);  
        barComparativo.addSeries(despesaSeries);
        
        if (Math.abs(receitas) >= Math.abs(despesas)) {
        	maxValueBarComparativo = new BigDecimal(Math.abs(receitas + (receitas * 0.10))).intValue();
        } else {
        	maxValueBarComparativo = new BigDecimal(Math.abs(despesas + (despesas * 0.10))).intValue();
        }
        
	}
	
	public void registrarOrcamento() {
		if (resumoMensal == null) {
			warnMessage("Gere antes um novo resumo!");
			return;
		}
		
		// Instancia um novo objeto Orcamento
		Orcamento orcamentoAGerar = new Orcamento();
		
		// Seta os atributos do orcamento
		orcamentoAGerar.setTipoOrcamento(TipoOrcamento.CONTA);
		orcamentoAGerar.setConta(contaSelecionada);
		orcamentoAGerar.setDescricao(fechamentoSelecionado == null ? "Orçamento - Período atual" : "Orçamento - " + fechamentoSelecionado.getLabel());
		orcamentoAGerar.setInicio(resumoMensal.getInicio());
		orcamentoAGerar.setFim(resumoMensal.getFim());
		orcamentoAGerar.setUsuario(getUsuarioLogado());
		
		// Seta os detalhes do orçamento
		for (Categoria c : resumoMensal.getCategorias()) {
			if (c.getDescricao().equals("Saldo anterior") || c.getDescricao().equals("Saldo atual")) {
				// Faz nada
			} else {
				orcamentoAGerar.getDetalhes().add(new DetalheOrcamento(c.getId(), c.getDescricao(), c.getTipoCategoria(), Math.abs(c.getSaldoPago())));
			}
		}
		
		// Seta a entidade em orcamentoMB e manda salvar
		orcamentoMB.salvarOrcamentoGerado(orcamentoAGerar);
	}
	
	public List<Conta> getListaConta() {
		try {
			if (contaSelecionada == null) {
				List<Conta> contas;
				contas = contaService.buscarAtivosPorUsuario(getUsuarioLogado());
				if (contas != null && contas.size() != 0) {
					contaSelecionada = contas.get(0);
				}
				return contas;
			} else {
				return contaService.buscarAtivosPorUsuario(getUsuarioLogado());
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<FechamentoPeriodo> getListaFechamentoPeriodo() {
		List<FechamentoPeriodo> fechamentos = new ArrayList<>();
		try {			
			
			List<FechamentoPeriodo> resultado = lancamentoContaService.buscarTodosFechamentoPorConta(contaSelecionada);
			if (resultado != null) {
				if (resultado.size() >= getOpcoesSistema().getLimiteQuantidadeFechamentos()) {
					for (int i = 0; i < getOpcoesSistema().getLimiteQuantidadeFechamentos(); i++) {
						fechamentos.add(resultado.get(i));
					}
				} else {
					fechamentos.addAll(resultado);
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

	public boolean isLancamentoAgendado() {
		return lancamentoAgendado;
	}

	public void setLancamentoAgendado(boolean lancamentoAgendado) {
		this.lancamentoAgendado = lancamentoAgendado;
	}

	public IResumoEstatistica getService() {
		return service;
	}

	public void setService(IResumoEstatistica service) {
		this.service = service;
	}

	public double getSaldoTotalContas() {
		return saldoTotalContas;
	}

	public void setSaldoTotalContas(double saldoTotalContas) {
		this.saldoTotalContas = saldoTotalContas;
	}

	public Conta getContaSelecionada() {
		return contaSelecionada;
	}
	public void setContaSelecionada(Conta contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}
	public void setContaService(IConta contaService) {
		this.contaService = contaService;
	}

	public ResumoMensalContas getResumoMensal() {
		return resumoMensal;
	}

	public void setResumoMensal(ResumoMensalContas resumoMensal) {
		this.resumoMensal = resumoMensal;
	}

	public FechamentoPeriodo getFechamentoSelecionado() {
		return fechamentoSelecionado;
	}

	public void setFechamentoSelecionado(FechamentoPeriodo fechamentoSelecionado) {
		this.fechamentoSelecionado = fechamentoSelecionado;
	}

	public PieChartModel getPieCategoriaCredito() {
		return pieCategoriaCredito;
	}

	public void setPieCategoriaCredito(PieChartModel pieCategoriaCredito) {
		this.pieCategoriaCredito = pieCategoriaCredito;
	}

	public PieChartModel getPieCategoriaDebito() {
		return pieCategoriaDebito;
	}

	public void setPieCategoriaDebito(PieChartModel pieCategoriaDebito) {
		this.pieCategoriaDebito = pieCategoriaDebito;
	}

	public String getStyleCorrectionCredito() {
		return styleCorrectionCredito;
	}

	public void setStyleCorrectionCredito(String styleCorrectionCredito) {
		this.styleCorrectionCredito = styleCorrectionCredito;
	}

	public String getStyleCorrectionDebito() {
		return styleCorrectionDebito;
	}

	public void setStyleCorrectionDebito(String styleCorrectionDebito) {
		this.styleCorrectionDebito = styleCorrectionDebito;
	}

	public Integer getMaxValueBarComparativo() {
		return maxValueBarComparativo;
	}

	public void setMaxValueBarComparativo(Integer maxValueBarComparativo) {
		this.maxValueBarComparativo = maxValueBarComparativo;
	}

	public BarChartModel getBarComparativo() {
		return barComparativo;
	}

	public void setBarComparativo(BarChartModel barComparativo) {
		this.barComparativo = barComparativo;
	}
}