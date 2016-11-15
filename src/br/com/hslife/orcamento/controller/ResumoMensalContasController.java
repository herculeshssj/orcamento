/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

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
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.ContaCompartilhada;
import br.com.hslife.orcamento.entity.DetalheOrcamento;
import br.com.hslife.orcamento.entity.FaturaCartao;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.Orcamento;
import br.com.hslife.orcamento.enumeration.TipoCartao;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoOrcamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IContaCompartilhada;
import br.com.hslife.orcamento.facade.IFaturaCartao;
import br.com.hslife.orcamento.facade.IFechamentoPeriodo;
import br.com.hslife.orcamento.facade.IResumoEstatistica;
import br.com.hslife.orcamento.model.ResumoMensalContas;
import br.com.hslife.orcamento.util.Util;

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
	private IContaCompartilhada contaCompartilhadaService;
	
	@Autowired
	private OrcamentoController orcamentoMB;
	
	@Autowired
	private IFaturaCartao faturaCartaoService;
	
	@Autowired
	private IFechamentoPeriodo fechamentoPeriodoService;
	
	private Conta contaSelecionada;
	private FechamentoPeriodo fechamentoSelecionado;
	private FaturaCartao faturaCartao;
	private ResumoMensalContas resumoMensal;
	
	private PieChartModel pieCategoriaCredito;
	private PieChartModel pieCategoriaDebito;
	
	private BarChartModel barComparativo = new BarChartModel();  
	
	private Integer maxValueBarComparativo = 100;
	
	private String styleCorrectionCredito = "width:820px;height:600px";
	private String styleCorrectionDebito = "width:820px;height:600px";
	
	private boolean exibirPieCategoriaDebito = true;
	private boolean exibirPieCategoriaCredito = true;
	private boolean exibirBarComparativo = true;
	
	private String mesAno;
	
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
		if (contaSelecionada == null) {
			warnMessage("Informe a conta!");
			return "";
		}
		try {
			if (contaSelecionada.getTipoConta().equals(TipoConta.CARTAO))
				if (contaSelecionada.getCartaoCredito().getTipoCartao().equals(TipoCartao.CREDITO))
					resumoMensal = getService().gerarRelatorioResumoMensalContas(contaSelecionada, faturaCartao);
				else {
					if (mesAno == null || mesAno.isEmpty()) {
						// Preguiça...
						mesAno = (Calendar.getInstance().get(Calendar.MONTH) + 1) + "/" + Calendar.getInstance().get(Calendar.YEAR);
					}
					
					String[] dataParticionada = mesAno.split("/");
					 
					resumoMensal = getService().gerarRelatorioResumoMensalContas(contaSelecionada, 
							Util.primeiroDiaMes(Integer.valueOf(dataParticionada[0]) - 1, Integer.valueOf(dataParticionada[1])), // Data inicial
							Util.ultimoDiaMes(Integer.valueOf(dataParticionada[0]) - 1, Integer.valueOf(dataParticionada[1]))); // Data final
				}
			else
				resumoMensal = getService().gerarRelatorioResumoMensalContas(contaSelecionada, fechamentoSelecionado);
			this.gerarGraficoCreditoDebito();
			this.gerarGraficoComparativo();
		} catch (ValidationException | BusinessException be) {
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
			if (!categoria.getDescricao().equals("Saldo atual") & !categoria.getDescricao().equals("Sem categoria")) {
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
		
		// Verifica se as abas de crédito e débito devem ser exibidas
		if (pieCategoriaCredito.getData().isEmpty()) {
			exibirPieCategoriaCredito = false;
		} else {
			exibirPieCategoriaCredito = true;
		}
		
		if (pieCategoriaDebito.getData().isEmpty()) {
			exibirPieCategoriaDebito = false;
		} else {
			exibirPieCategoriaDebito = true;
		}
	}
	
	private void gerarGraficoComparativo() {
		barComparativo = new BarChartModel();
		barComparativo.setLegendPosition("s");
		barComparativo.setTitle("Comparativo Receitas X Despesas");
		barComparativo.setExtender("ext1");
		double receitas = 0;
		double despesas = 0;
		
		for (Categoria categoria : resumoMensal.getCategorias()) {
			if (!categoria.getDescricao().equals("Saldo atual") & !categoria.getDescricao().equals("Sem categoria")) {
				if (categoria.getTipoCategoria().equals(TipoCategoria.CREDITO)) {
					receitas += categoria.getSaldoPago();
				} else {
					despesas += categoria.getSaldoPago();
				}
			}			
		}
		
		ChartSeries receitaSeries = new ChartSeries();
		receitaSeries.setLabel("Receitas");		

		if (contaSelecionada.getTipoConta().equals(TipoConta.CARTAO)) {
			receitaSeries.set(faturaCartao == null ? "Próximas faturas / Lançamento registrados" : faturaCartao.getLabel(), Math.abs(receitas));
		} else {
			receitaSeries.set(fechamentoSelecionado == null ? "Periodo atual" : fechamentoSelecionado.getLabel(), Math.abs(receitas));
		}
		
		ChartSeries despesaSeries = new ChartSeries();
		despesaSeries.setLabel("Despesas");
		if (contaSelecionada.getTipoConta().equals(TipoConta.CARTAO)) {
			despesaSeries.set(faturaCartao == null ? "Próximas faturas / Lançamento registrados" : faturaCartao.getLabel(), Math.abs(despesas));
		} else {
			despesaSeries.set(fechamentoSelecionado == null ? "Periodo atual" : fechamentoSelecionado.getLabel(), Math.abs(despesas));
		}
		
        barComparativo.addSeries(receitaSeries);  
        barComparativo.addSeries(despesaSeries);
        
        if (Math.abs(receitas) >= Math.abs(despesas)) {
        	maxValueBarComparativo = new BigDecimal(Math.abs(receitas + (receitas * 0.10))).intValue();
        } else {
        	maxValueBarComparativo = new BigDecimal(Math.abs(despesas + (despesas * 0.10))).intValue();
        } 
        
        // Verifica se a aba deverá ser exibida
        exibirBarComparativo = exibirPieCategoriaCredito & exibirPieCategoriaDebito;
	}
	
	public void registrarOrcamento() {
		if (resumoMensal == null) {
			warnMessage("Gere antes um novo resumo!");
			return;
		}
		
		// Impede a criação de um orçamento no caso de conta compartilhada
		if (!contaSelecionada.getUsuario().equals(getUsuarioLogado())) {
			warnMessage("Usuário da conta difere do usuário atualmente logado!");
			return;
		}
		
		// Instancia um novo objeto Orcamento
		Orcamento orcamentoAGerar = new Orcamento();
		
		// Seta os atributos do orcamento
		orcamentoAGerar.setTipoOrcamento(TipoOrcamento.CONTA);
		orcamentoAGerar.setConta(contaSelecionada);
		if (contaSelecionada.getTipoConta().equals(TipoConta.CARTAO)) {
			orcamentoAGerar.setDescricao(faturaCartao == null ? "Orçamento - Próximas faturas" : "Orçamento - " + faturaCartao.getLabel());
		} else {
			orcamentoAGerar.setDescricao(fechamentoSelecionado == null ? "Orçamento - Período atual" : "Orçamento - " + fechamentoSelecionado.getLabel());
		}
		
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
		Set<Conta> contas = new HashSet<>();
		try {			
			if (getOpcoesSistema().getExibirContasInativas()) {
				contas.addAll(contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", new TipoConta[]{TipoConta.CORRENTE, TipoConta.CARTAO, TipoConta.POUPANCA, TipoConta.OUTROS}, getUsuarioLogado(), null));				
			} else {
				contas.addAll(contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", new TipoConta[]{TipoConta.CORRENTE, TipoConta.CARTAO, TipoConta.POUPANCA, TipoConta.OUTROS}, getUsuarioLogado(), true));
			}
			
			// Traz as contas compartilhadas para com o usuário atualmente logado
			List<ContaCompartilhada> contasCompartilhadas = getContaCompartilhadaService().buscarTodosPorUsuario(getUsuarioLogado());
						
			// Acrescenta no Set as contas compartilhadas dos demais usuários
			for (ContaCompartilhada contaCompartilhada : contasCompartilhadas) {
				if (!contaCompartilhada.getConta().isAtivo() && getOpcoesSistema().getExibirContasInativas()) {
					contas.add(contaCompartilhada.getConta());
				} else if (contaCompartilhada.getConta().isAtivo()) {
					contas.add(contaCompartilhada.getConta());
				} else {
					continue;
				}
			}
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>(contas);
	}
	
	public List<FechamentoPeriodo> getListaFechamentoPeriodo() {
		List<FechamentoPeriodo> fechamentos = new ArrayList<>();
		try {			
			if (contaSelecionada != null) {
				for (FechamentoPeriodo fechamento : fechamentoPeriodoService.buscarTodosFechamentoPorConta(contaSelecionada)) {
					fechamentos.add(fechamento);
					if (fechamentos.size() >= getOpcoesSistema().getLimiteQuantidadeFechamentos()) {
						break;
					}
				}
			}
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return fechamentos;
	}
	
	public List<FaturaCartao> getListaFaturaCartao() {
		List<FaturaCartao> faturas = new ArrayList<>();
		
		try {
			if (contaSelecionada != null) {
				for (FaturaCartao fatura : faturaCartaoService.buscarTodosPorCartaoCredito(contaSelecionada)) {
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
	
	public List<String> getListaMesAno() {
		List<String> mesAno = new LinkedList<>();
		Calendar data = Calendar.getInstance();
		for (int i = 0; i < 12; i++) {
			data.add(Calendar.MONTH, -1);
			String temp = data.get(Calendar.MONTH) + 1 + "/" + data.get(Calendar.YEAR);
			mesAno.add(temp);
		}
		return mesAno;
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

	public FaturaCartao getFaturaCartao() {
		return faturaCartao;
	}

	public void setFaturaCartao(FaturaCartao faturaCartao) {
		this.faturaCartao = faturaCartao;
	}

	public BarChartModel getBarComparativo() {
		return barComparativo;
	}

	public void setBarComparativo(BarChartModel barComparativo) {
		this.barComparativo = barComparativo;
	}

	public boolean isExibirPieCategoriaDebito() {
		return exibirPieCategoriaDebito;
	}

	public void setExibirPieCategoriaDebito(boolean exibirPieCategoriaDebito) {
		this.exibirPieCategoriaDebito = exibirPieCategoriaDebito;
	}

	public boolean isExibirPieCategoriaCredito() {
		return exibirPieCategoriaCredito;
	}

	public void setExibirPieCategoriaCredito(boolean exibirPieCategoriaCredito) {
		this.exibirPieCategoriaCredito = exibirPieCategoriaCredito;
	}

	public boolean isExibirBarComparativo() {
		return exibirBarComparativo;
	}

	public void setExibirBarComparativo(boolean exibirBarComparativo) {
		this.exibirBarComparativo = exibirBarComparativo;
	}

	public String getMesAno() {
		return mesAno;
	}

	public void setMesAno(String mesAno) {
		this.mesAno = mesAno;
	}

	public IContaCompartilhada getContaCompartilhadaService() {
		return contaCompartilhadaService;
	}
}