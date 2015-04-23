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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.CartaoCredito;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.ConversaoMoeda;
import br.com.hslife.orcamento.entity.FaturaCartao;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.Container;
import br.com.hslife.orcamento.enumeration.FormaPagamentoFatura;
import br.com.hslife.orcamento.enumeration.StatusFaturaCartao;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICartaoCredito;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFaturaCartao;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.util.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.util.DetalheFaturaComparator;
import br.com.hslife.orcamento.util.Util;

@Component("faturaCartaoMB")
@Scope("session")
public class FaturaCartaoController extends AbstractCRUDController<FaturaCartao> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3631419225220275295L;

	@Autowired
	private IFaturaCartao service;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private ICartaoCredito cartaoCreditoService;
	
	@Autowired
	private ILancamentoConta lancamentoContaService;
	
	@Autowired
	private IConta contaService;
	
	private Moeda moedaPadrao;
	private boolean faturaFechada;
	private double totalFatura;
	private boolean exibirParcelarFatura;
	private double valorAQuitar;
	private Date dataPagamento;
	private int quantParcelas;
	private Date dataParcelamento;	
	private CartaoCredito cartaoSelecionado;
	private FaturaCartao faturaSelecionada;
	private CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
	private LancamentoConta lancamento;
	private Conta contaSelecionada;
	private StatusFaturaCartao statusFatura;
	private boolean selecionarTodosLancamentos;
	private FormaPagamentoFatura formaPagamento;
	private boolean prontoParaQuitar = false;
	
	private List<LancamentoConta> detalhesFaturaCartao = new ArrayList<>();
	private List<Moeda> moedas = new ArrayList<Moeda>();
	private List<LancamentoConta> lancamentosEncontrados = new ArrayList<LancamentoConta>();
	private List<LancamentoConta> lancamentosAdicionados = new ArrayList<LancamentoConta>();
	private Map<String, List<LancamentoConta>> mapFaturasEncontradas = new HashMap<>();
	private Map<String, List<Moeda>> mapMoedasEncontradas = new HashMap<>();
	
	public FaturaCartaoController() {
		super(new FaturaCartao());
		
		moduleTitle = "Fatura do Cartão";
	}

	@Override
	protected void initializeEntity() {
		detalhesFaturaCartao = new ArrayList<LancamentoConta>();
		entity = new FaturaCartao();
		lancamentosEncontrados.clear();
		lancamentosAdicionados.clear();
		moedas = new ArrayList<Moeda>();
		criterioBusca = new CriterioBuscaLancamentoConta();
	}

	
	@Override
	public String startUp() {
		// Pega a moeda padrão do usuário
		try {
			moedaPadrao = moedaService.buscarPadraoPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return super.startUp();
	}
	
	@Override
	public void find() {
		try {
			
			if (statusFatura == null) {
			
				int contFaturas = 1;
				
				listEntity = new ArrayList<FaturaCartao>();
				mapFaturasEncontradas.clear();
				
				for (FaturaCartao fatura : getService().buscarTodosPorContaOrdenadoPorMesEAno(cartaoSelecionado.getConta())) {
					if (contFaturas <= 5) {
						listEntity.add(fatura);
						
						// Adiciona os detalhes da fatura no Map						
						mapFaturasEncontradas.put(fatura.getLabel(), new ArrayList<LancamentoConta>(fatura.getDetalheFatura()));
						
						// Ordena os detalhes da fatura
						Collections.sort(mapFaturasEncontradas.get(fatura.getLabel()), new DetalheFaturaComparator());
						
						// Adiciona os detalhes da fatura no List detalhesFaturaCartao para realizar o 
						// cálculo dos totais
						detalhesFaturaCartao.clear();
						detalhesFaturaCartao.addAll(fatura.getDetalheFatura());
						this.calcularSaldoCompraSaqueParceladoPorMoeda();
						
						// Determina a partir do status da fatura se será usado o valor de conversão da moeda ou 
						// o valor registrado durante o fechamento da fatura
						if (fatura.getStatusFaturaCartao().equals(StatusFaturaCartao.ABERTA) 
								|| fatura.getStatusFaturaCartao().equals(StatusFaturaCartao.FUTURA)
								|| fatura.getStatusFaturaCartao().equals(StatusFaturaCartao.ANTIGA)) {
							for (Moeda m : moedas) {
								m.setTaxaConversao(m.getValorConversao());
							}
						} else {
							for (ConversaoMoeda conversao : fatura.getConversoesMoeda()) {
								moedas.get(moedas.indexOf(conversao.getMoeda())).setTaxaConversao(conversao.getTaxaConversao());							
							}	
						}						
						
						this.calculaValorConversao();
						
						// Adiciona as moedas com seus totais no map correspondente
						mapMoedasEncontradas.put(fatura.getLabel(), new ArrayList<Moeda>(moedas));
						
						contFaturas++;
					} 
				}
			
			} else {
				
				listEntity = getService().buscarPorCartaoCreditoEStatusFatura(cartaoSelecionado, statusFatura);
				
			}

		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String create() {
		entity.setStatusFaturaCartao(StatusFaturaCartao.ANTIGA);
		return super.create();
	}
	
	@Override
	public String edit() {
		try {
			entity = getService().buscarPorID(idEntity);
			lancamentosAdicionados.clear();
			lancamentosAdicionados.addAll(entity.getDetalheFatura());
			detalhesFaturaCartao.clear();
			detalhesFaturaCartao.addAll(entity.getDetalheFatura());
			this.calcularSaldoCompraSaqueParceladoPorMoeda();
			operation = "edit";
			actionTitle = " - Editar";
			return goToFormPage;
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	@Override
	public String view() {
		try {
			entity = getService().buscarPorID(idEntity);
			lancamentosAdicionados.clear();
			lancamentosAdicionados.addAll(entity.getDetalheFatura());
			detalhesFaturaCartao.clear();
			detalhesFaturaCartao.addAll(entity.getDetalheFatura());
			this.calcularSaldoCompraSaqueParceladoPorMoeda();
			operation = "delete";
			actionTitle = " - Excluir";
			return goToViewPage;
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	@SuppressWarnings("deprecation")
	public String save() {
		if (entity.getId() == null) {
			entity.setConta(cartaoSelecionado.getConta());
		}
		
		entity.adicionarTodosLancamentos(lancamentosAdicionados);
		
		entity.setMes(entity.getDataVencimento().getMonth() + 1);
		entity.setAno(entity.getDataVencimento().getYear() + 1900);
		
		return super.save();
	}
	
	private void calcularSaldoCompraSaqueParceladoPorMoeda() throws BusinessException {

		/* Pegando os totais para mostrar na fatura */
		moedas = moedaService.buscarPorUsuario(getUsuarioLogado());
		
		for (Moeda moeda : moedas) {
			moeda.setLancamentos(new ArrayList<LancamentoConta>());
			for (LancamentoConta lancamento : detalhesFaturaCartao) {
				if (lancamento.getMoeda().equals(moeda)) {
					// Adiciona o lançamento
					moeda.getLancamentos().add(lancamento);
					
					// Verifica e calcula as compras à vista e parceladas
					if (lancamento.getLancamentoPeriodico() != null && 
							lancamento.getLancamentoPeriodico().getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.PARCELADO)) {
						// Calcula as compras parceladas						
						if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
							moeda.setParcelado(moeda.getParcelado() + lancamento.getValorPago());
						else
							moeda.setParcelado(moeda.getParcelado() - lancamento.getValorPago());
					} else {
						// Calcula as compras à vista e mensalidades
						if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
							moeda.setCompraSaque(moeda.getCompraSaque() + lancamento.getValorPago());
						else
							moeda.setCompraSaque(moeda.getCompraSaque() - lancamento.getValorPago());
					}					
				}
			}
						
			// Calcula o total da compra e dos parcelamentos
			moeda.setTotal(moeda.getCompraSaque() + moeda.getParcelado());
			
			// Remove o sinal negativo
			moeda.setParcelado(Math.abs(moeda.getParcelado()));
			moeda.setCompraSaque(Math.abs(moeda.getCompraSaque()));
			moeda.setTotal(Math.abs(moeda.getTotal()));
		}
		
		// Remove as moedas que não contém lançamentos
		for (Iterator<Moeda> i = moedas.iterator(); i.hasNext();) {
			if (i.next().getLancamentos().isEmpty()) {
				i.remove();
			}
		}
	}
	
	public void carregarArquivo(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (entity.getArquivo() == null) entity.setArquivo(new Arquivo());
			entity.getArquivo().setDados(event.getFile().getContents());
			entity.getArquivo().setNomeArquivo(event.getFile().getFileName().replace(" ", "."));
			entity.getArquivo().setContentType(event.getFile().getContentType());
			entity.getArquivo().setTamanho(event.getFile().getSize());	
			entity.getArquivo().setContainer(Container.FATURACARTAO);
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
			entity.setArquivo(new Arquivo());
			infoMessage("Arquivo excluído! Salve para confirmar as alterações.");
		}
	}
	
	public void adicionarLancamento() {
		try {
			for (LancamentoConta l : lancamentosEncontrados) {
				if (l.isSelecionado() && l.getFaturaCartao() == null) {
					lancamentosAdicionados.add(l);
				}
			}
			lancamentosEncontrados.removeAll(lancamentosAdicionados);
			detalhesFaturaCartao.clear();
			detalhesFaturaCartao.addAll(lancamentosAdicionados);
			this.calcularSaldoCompraSaqueParceladoPorMoeda();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void excluirLancamento() {
		try {
			for (Iterator<LancamentoConta> i = lancamentosAdicionados.iterator(); i.hasNext();) {
				if (i.next().isSelecionado()) {
					i.remove();
				}
			}
			detalhesFaturaCartao.clear();
			detalhesFaturaCartao.addAll(lancamentosAdicionados);
			this.calcularSaldoCompraSaqueParceladoPorMoeda();			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void pesquisarLancamento() {
		try {
			criterioBusca.setConta(contaService.buscarPorID(cartaoSelecionado.getConta().getId()));
			criterioBusca.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.AGENDADO, StatusLancamentoConta.REGISTRADO});
			lancamentosEncontrados.clear();
			lancamentosEncontrados.addAll(lancamentoContaService.buscarPorCriterioBusca(criterioBusca));
			lancamentosEncontrados.removeAll(lancamentosAdicionados);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void selecionarTodos() {
		if (lancamentosEncontrados != null && lancamentosEncontrados.size() > 0)
		for (LancamentoConta l : lancamentosEncontrados) {
			if (selecionarTodosLancamentos) {
				l.setSelecionado(true);
			} else {
				l.setSelecionado(false);
			}
		}
	}
	
	public double getSaldoDevedor() {
		try {
			if (cartaoSelecionado != null)
				if (getService().saldoDevedorUltimaFatura(cartaoSelecionado) == 0.0)
					return faturaSelecionada.getSaldoDevedor() + getService().saldoDevedorUltimaFatura(cartaoSelecionado);
				else
					return getService().saldoDevedorUltimaFatura(cartaoSelecionado);			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return 0.0;
	}
	
	public String fecharFaturaView() {
		try {
			faturaSelecionada = getService().buscarPorID(idEntity);
			detalhesFaturaCartao.clear();
			detalhesFaturaCartao.addAll(faturaSelecionada.getDetalheFatura());
			this.calcularSaldoCompraSaqueParceladoPorMoeda();
			
			Calendar fechamento = Calendar.getInstance();
			fechamento.setTime(faturaSelecionada.getDataVencimento());
			// Fechamento < Vencimento = mesmo mês; Fechamento >= Vencimento = mês seguinte
			if (faturaSelecionada.getConta().getCartaoCredito().getDiaFechamentoFatura() < faturaSelecionada.getConta().getCartaoCredito().getDiaVencimentoFatura()) {
				fechamento.set(Calendar.DAY_OF_MONTH, faturaSelecionada.getConta().getCartaoCredito().getDiaFechamentoFatura());
			} else {
				fechamento.set(Calendar.DAY_OF_MONTH, faturaSelecionada.getConta().getCartaoCredito().getDiaFechamentoFatura());
				fechamento.add(Calendar.MONTH, -1);
			}			
			faturaSelecionada.setDataFechamento(fechamento.getTime());
			this.calculaValorConversao();
			actionTitle = " - Fechar fatura";
			return "/pages/FaturaCartao/fecharFatura";
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		return "";
	}
	
	public String fecharFatura() {
		try {
			getService().fecharFatura(faturaSelecionada, moedas);
			infoMessage("Fatura fechada com sucesso!");
			
			this.reprocessarBusca();
			
			return "/pages/FaturaCartao/listFaturaCartao";			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String fecharFaturaAntiga() {
		try {
			// Verifica se um lançamento foi selecionado na listagem				
			if(lancamento ==  null || lancamento.getConta() == null) {
				warnMessage("Selecione um lançamento!");
				return "";
			}
			
			getService().fecharFaturaAntiga(faturaSelecionada, moedas, lancamento);
			infoMessage("Fatura antiga fechada e quitada com sucesso!");
			
			this.reprocessarBusca();
			
			return "/pages/FaturaCartao/listFaturaCartao";			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public void reabrirFatura() {
		try {
			getService().reabrirFatura(getService().buscarPorID(idEntity));
			
			infoMessage("Fatura reaberta com sucesso!");
			
			this.reprocessarBusca();
			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String quitarFaturaView() {
		try {
			if (!prontoParaQuitar) {
				faturaSelecionada = getService().buscarPorID(idEntity);
				criterioBusca = new CriterioBuscaLancamentoConta();
				contaSelecionada = null;
				valorAQuitar = 0;
				dataPagamento = null;
				quantParcelas = 0;
				dataPagamento = null;
				formaPagamento = null;
				lancamentosEncontrados = new ArrayList<LancamentoConta>();
			}
			actionTitle = " - Quitar Fatura";
			return "/pages/FaturaCartao/quitarFatura";
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public void pesquisarLancamentoQuitacao() {
		try {
			if (criterioBusca.getDataInicio() == null) {
				warnMessage("Informe a data de pagamento!");
			} else {				
				criterioBusca.setDataFim(criterioBusca.getDataInicio());
				lancamentosEncontrados.clear();
				lancamentosEncontrados.addAll(lancamentoContaService.buscarPorCriterioBusca(criterioBusca));
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String confirmarQuitacao() {
		if (formaPagamento.equals(FormaPagamentoFatura.LANCAMENTO)) {
			// Verifica se um lançamento foi selecionado na listagem				
			if(lancamento ==  null || lancamento.getConta() == null) {
				warnMessage("Selecione um lançamento!");
				return "";
			}
			
			lancamentosEncontrados.clear();
			lancamentosEncontrados.add(lancamento);
		}
		
		if (formaPagamento.equals(FormaPagamentoFatura.PARCELAMENTO)) {
			// Verifica se a quantidade de parcelas é superior a 1
			if (quantParcelas < 2) {
				warnMessage("Informe uma quantidade de parcelas maior de 1!");
				return "";
			}
			// Verifica se a data de parcelamento foi preenchida 
			if (dataParcelamento == null) {
				warnMessage("Informe a data de parcelamento!");
				return "";
			}
		}
		
		actionTitle = " - Confirmar quitação";
		prontoParaQuitar = true;
		return "/pages/FaturaCartao/confirmarQuitacao";
	}
	
	public String quitarFatura() {		
		try {
			// Determina a forma de pagamento da fatura
			switch(formaPagamento) {
				case DEBITO : getService().quitarFaturaDebitoConta(faturaSelecionada, contaSelecionada, valorAQuitar, dataPagamento); break;
				case PARCELAMENTO : getService().quitarFaturaParcelamento(faturaSelecionada, quantParcelas, dataParcelamento); break;
				case LANCAMENTO : getService().quitarFaturaLancamentoSelecionado(faturaSelecionada, lancamento); break;
				default : throw new BusinessException("Opção inválida!");
			}			
			infoMessage("Fatura quitada com sucesso!");

			this.reprocessarBusca();
			
			prontoParaQuitar = false;
			
			return "/pages/FaturaCartao/listFaturaCartao";
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String detalheFatura() {		
		try {			
			entity = getService().buscarPorID(idEntity);
			lancamentosEncontrados.clear();
			lancamentosEncontrados.addAll(entity.getDetalheFatura());
			detalhesFaturaCartao.clear(); 
			detalhesFaturaCartao.addAll(entity.getDetalheFatura());
			this.calcularSaldoCompraSaqueParceladoPorMoeda();
			// Determina a partir do status da fatura se será usado o valor de conversão da moeda ou 
			// o valor registrado durante o fechamento da fatura
			if (entity.getStatusFaturaCartao().equals(StatusFaturaCartao.ABERTA) 
					|| entity.getStatusFaturaCartao().equals(StatusFaturaCartao.FUTURA)
					|| entity.getStatusFaturaCartao().equals(StatusFaturaCartao.ANTIGA)) {
				for (Moeda m : moedas) {
					m.setTaxaConversao(m.getValorConversao());
				}
			} else {
				for (ConversaoMoeda conversao : entity.getConversoesMoeda()) {
					moedas.get(moedas.indexOf(conversao.getMoeda())).setTaxaConversao(conversao.getTaxaConversao());							
				}	
			}
			this.calculaValorConversao();
			actionTitle = " - Detalhes";
			return "/pages/FaturaCartao/detalheFatura";			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public void calculaValorConversao() {
		totalFatura = 0.0;
		for (Moeda moeda : moedas) {
			if (moeda.isPadrao()) 
				moeda.setTotalConvertido(Math.abs(Util.arredondar(moeda.getTotal())));
			else
				moeda.setTotalConvertido(Math.abs(Util.arredondar(moeda.getTotal() * moeda.getTaxaConversao())));
			totalFatura += moeda.getTotalConvertido();
		}
	} 

	public List<CartaoCredito> getListaCartaoSoCredito() {
		try {
			if (getOpcoesSistema().getExibirContasInativas()) {
				return cartaoCreditoService.buscarSomenteCreditoPorUsuario(getUsuarioLogado());
			} else {
				return cartaoCreditoService.buscarAtivosSomenteCreditoPorUsuario(getUsuarioLogado());
			}			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<Moeda> getListaMoeda() {
		try {
			List<Moeda> resultado = moedaService.buscarAtivosPorUsuario(getUsuarioLogado());
			// Lógica para incluir a moeda inativa na combo
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
	
	public List<Conta> getListaConta() {
		try {
			return contaService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public List<SelectItem> getListaStatusFaturaCartao() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		listaSelectItem.add(new SelectItem(null, "Fatura mais recentes"));
		listaSelectItem.add(new SelectItem(StatusFaturaCartao.ANTIGA, "Faturas antigas"));
		listaSelectItem.add(new SelectItem(StatusFaturaCartao.QUITADA, "Faturas quitadas"));
		listaSelectItem.add(new SelectItem(StatusFaturaCartao.VENCIDA, "Faturas vencidas"));
		return listaSelectItem;
	}

	public IFaturaCartao getService() {
		return service;
	}

	public void setService(IFaturaCartao service) {
		this.service = service;
	}

	public Moeda getMoedaPadrao() {
		return moedaPadrao;
	}

	public void setMoedaPadrao(Moeda moedaPadrao) {
		this.moedaPadrao = moedaPadrao;
	}

	public boolean isFaturaFechada() {
		return faturaFechada;
	}

	public void setFaturaFechada(boolean faturaFechada) {
		this.faturaFechada = faturaFechada;
	}

	public double getTotalFatura() {
		return totalFatura;
	}

	public void setTotalFatura(double totalFatura) {
		this.totalFatura = totalFatura;
	}

	public boolean isExibirParcelarFatura() {
		return exibirParcelarFatura;
	}

	public void setExibirParcelarFatura(boolean exibirParcelarFatura) {
		this.exibirParcelarFatura = exibirParcelarFatura;
	}

	public double getValorAQuitar() {
		return valorAQuitar;
	}

	public void setValorAQuitar(double valorAQuitar) {
		this.valorAQuitar = valorAQuitar;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public CartaoCredito getCartaoSelecionado() {
		return cartaoSelecionado;
	}

	public void setCartaoSelecionado(CartaoCredito cartaoSelecionado) {
		this.cartaoSelecionado = cartaoSelecionado;
	}

	public FaturaCartao getFaturaSelecionada() {
		return faturaSelecionada;
	}

	public void setFaturaSelecionada(FaturaCartao faturaSelecionada) {
		this.faturaSelecionada = faturaSelecionada;
	}

	public LancamentoConta getLancamento() {
		return lancamento;
	}

	public void setLancamento(LancamentoConta lancamento) {
		this.lancamento = lancamento;
	}

	public Conta getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(Conta contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}

	public StatusFaturaCartao getStatusFatura() {
		return statusFatura;
	}

	public void setStatusFatura(StatusFaturaCartao statusFatura) {
		this.statusFatura = statusFatura;
	}

	public List<LancamentoConta> getDetalhesFaturaCartao() {
		return detalhesFaturaCartao;
	}

	public void setDetalhesFaturaCartao(List<LancamentoConta> detalhesFaturaCartao) {
		this.detalhesFaturaCartao = detalhesFaturaCartao;
	}

	public List<Moeda> getMoedas() {
		return moedas;
	}

	public void setMoedas(List<Moeda> moedas) {
		this.moedas = moedas;
	}

	public List<LancamentoConta> getLancamentosEncontrados() {
		return lancamentosEncontrados;
	}

	public void setLancamentosEncontrados(
			List<LancamentoConta> lancamentosEncontrados) {
		this.lancamentosEncontrados = lancamentosEncontrados;
	}

	public List<LancamentoConta> getLancamentosAdicionados() {
		return lancamentosAdicionados;
	}

	public void setLancamentosAdicionados(
			List<LancamentoConta> lancamentosAdicionados) {
		this.lancamentosAdicionados = lancamentosAdicionados;
	}

	public boolean isSelecionarTodosLancamentos() {
		return selecionarTodosLancamentos;
	}

	public void setSelecionarTodosLancamentos(boolean selecionarTodosLancamentos) {
		this.selecionarTodosLancamentos = selecionarTodosLancamentos;
	}

	public CriterioBuscaLancamentoConta getCriterioBusca() {
		return criterioBusca;
	}

	public void setCriterioBusca(CriterioBuscaLancamentoConta criterioBusca) {
		this.criterioBusca = criterioBusca;
	}

	public Map<String, List<LancamentoConta>> getMapFaturasEncontradas() {
		return mapFaturasEncontradas;
	}

	public Map<String, List<Moeda>> getMapMoedasEncontradas() {
		return mapMoedasEncontradas;
	}

	public int getQuantParcelas() {
		return quantParcelas;
	}

	public void setQuantParcelas(int quantParcelas) {
		this.quantParcelas = quantParcelas;
	}

	public Date getDataParcelamento() {
		return dataParcelamento;
	}

	public void setDataParcelamento(Date dataParcelamento) {
		this.dataParcelamento = dataParcelamento;
	}

	public FormaPagamentoFatura getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(FormaPagamentoFatura formaPagamento) {
		this.formaPagamento = formaPagamento;
	}
}