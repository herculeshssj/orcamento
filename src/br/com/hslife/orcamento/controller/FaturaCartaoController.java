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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import br.com.hslife.orcamento.enumeration.StatusFaturaCartao;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICartaoCredito;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFaturaCartao;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.model.CriterioLancamentoConta;
import br.com.hslife.orcamento.util.Util;

@Component("faturaCartaoMB")
@Scope("session")
public class FaturaCartaoController extends AbstractController {
	
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
	
	private FaturaCartao entity;
	private Moeda moedaPadrao;
	private boolean faturaFechada;
	private double totalFatura;
	private boolean exibirParcelarFatura;
	private int formaPagamentoFatura; // 1 - débito em conta; -1 - parcelar fatura
	private double valorAQuitar;
	private Date dataPagamento;
	
	private CartaoCredito cartaoSelecionado;
	private FaturaCartao faturaSelecionada;
	private CriterioLancamentoConta criterioBusca = new CriterioLancamentoConta();
	private LancamentoConta lancamento;
	private Conta contaSelecionada;
	
	private List<LancamentoConta> listEntity = new ArrayList<>();
	private List<Moeda> moedas = new ArrayList<Moeda>();
	private List<LancamentoConta> lancamentosEncontrados = new ArrayList<LancamentoConta>();
	private List<LancamentoConta> lancamentosAdicionados = new ArrayList<LancamentoConta>();
	
	public FaturaCartaoController() {		
		moduleTitle = "Fatura do Cartão";
	}
	
	@Override
	public String getModuleTitle() {
		return super.getModuleTitle() + actionTitle;
	}

	@Override
	protected void initializeEntity() {
		listEntity.clear();
		entity = new FaturaCartao();
		lancamentosEncontrados.clear();
		moedas = new ArrayList<Moeda>();
	}

	@Override
	public String startUp() {
		// Pega a moeda padrão do usuário
		try {
			moedaPadrao = moedaService.buscarPadraoPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		actionTitle = "";
		return "/pages/FaturaCartao/listFaturaCartao";
	}
	
	public String find() {
		try {
			if (faturaSelecionada == null) {
				warnMessage("Selecione uma fatura!");
			} else {
				listEntity.clear();
				listEntity.addAll(faturaSelecionada.getDetalheFatura());
				this.calcularSaldoCompraSaqueParceladoPorMoeda();
			}
		} catch(BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	private void calcularSaldoCompraSaqueParceladoPorMoeda() throws BusinessException {

		/* Pegando os totais para mostrar na fatura */
		moedas = moedaService.buscarPorUsuario(getUsuarioLogado());
		
		for (Moeda moeda : moedas) {
			moeda.setLancamentos(new ArrayList<LancamentoConta>());
			for (LancamentoConta lancamento : listEntity) {
				if (lancamento.getMoeda().equals(moeda)) {
					// Adiciona o lançamento
					moeda.getLancamentos().add(lancamento);
					
					// Verifica e calcula a compra a vista
					if (lancamento.getParcela() == null || lancamento.getParcela().trim().isEmpty()) {
						if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
							moeda.setCompraSaque(moeda.getCompraSaque() + lancamento.getValorPago());
						else
							moeda.setCompraSaque(moeda.getCompraSaque() - lancamento.getValorPago());
					}
					
					// Verifica e calcula a compra parcelada
					if (lancamento.getParcela() != null && !lancamento.getParcela().trim().isEmpty()) {
						if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
							moeda.setParcelado(moeda.getParcelado() + lancamento.getValorPago());
						else
							moeda.setParcelado(moeda.getParcelado() - lancamento.getValorPago());							
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
	
	public String editarLancamentos() {
		try {
			if (faturaSelecionada == null) {
				warnMessage("Selecione uma fatura!");
			} else {
				// Verifica se a fatura selecionada pode ser editada.
				if (faturaSelecionada.getStatusFaturaCartao().equals(StatusFaturaCartao.ABERTA) || faturaSelecionada.getStatusFaturaCartao().equals(StatusFaturaCartao.FUTURA)) {
					entity = getService().buscarPorID(faturaSelecionada.getId());
					lancamentosAdicionados.clear();
					lancamentosAdicionados.addAll(entity.getDetalheFatura());
					actionTitle = " - Editar lançamentos";				
					return "/pages/FaturaCartao/formFaturaCartao";
				} else {
					warnMessage("Não é possível editar os lançamentos desta fatura!");
					return "";
				}		
			}
		} catch (Exception be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String excluirFatura() {
		if (faturaSelecionada == null) {
			warnMessage("Selecione uma fatura!");
		} else {		
			if (faturaSelecionada.getStatusFaturaCartao().equals(StatusFaturaCartao.FUTURA)) {
				try {
					getService().excluir(faturaSelecionada);
					infoMessage("Fatura excluída com sucesso!");
					initializeEntity();
				} catch (BusinessException be) {
					errorMessage(be.getMessage());
				}
			} else {
				warnMessage("Não é possível excluir esta fatura!");
			}
		}
		return "";
	}
	
	public String save() {
		try {
			// Vincula as modificações na fatura do cartão
			entity.getDetalheFatura().clear();
			entity.getDetalheFatura().addAll(lancamentosAdicionados);
			
			// Salva a fatura
			getService().validar(entity);
			getService().alterar(entity);
			infoMessage("Fatura salva com sucesso!");
			initializeEntity();
			return "/pages/FaturaCartao/listFaturaCartao";
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String cancel() {
		actionTitle = "";
		initializeEntity();
		return "/pages/FaturaCartao/listFaturaCartao";
	}
	
	public void carregarArquivo(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (event.getFile().getSize() > 16777216) {
				errorMessage("Arquivo excedeu o tamanho máximo de 16 MB!");
			} else {
				if (entity.getArquivo() == null) entity.setArquivo(new Arquivo());
				entity.getArquivo().setDados(event.getFile().getContents());
				entity.getArquivo().setNomeArquivo(event.getFile().getFileName().replace(" ", "."));
				entity.getArquivo().setContentType(event.getFile().getContentType());
				entity.getArquivo().setTamanho(event.getFile().getSize());
				//entity.setArquivo(arquivo);
				infoMessage("Arquivo anexado com sucesso!");
			}
		} else {
			infoMessage("Nenhum arquivo anexado!");
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
		if (lancamento == null || lancamentosAdicionados.contains(lancamento)) {
			warnMessage("Lançamento já foi adicionado na fatura!");
		} else {
			lancamentosAdicionados.add(lancamento);
			lancamentosEncontrados.removeAll(lancamentosAdicionados);
		}
		lancamento = new LancamentoConta();
	}
	
	public void incluirLancamento() {
		if (criterioBusca.getLancadoEm() == null || criterioBusca.getDescricao() == null || criterioBusca.getDescricao().trim().isEmpty()) {
			warnMessage("Preencha a data de lançamento e informe uma descrição!");
		} else {			
			try {
				lancamento = new LancamentoConta();
				lancamento.setConta(contaService.buscarPorID(entity.getConta().getId()));
				lancamento.setDataLancamento(criterioBusca.getLancadoEm());
				lancamento.setDataPagamento(criterioBusca.getLancadoEm());
				lancamento.setDescricao(criterioBusca.getDescricao());
				lancamento.setMoeda(criterioBusca.getMoeda());
				lancamento.setParcela(criterioBusca.getParcela());
				lancamento.setTipoLancamento(criterioBusca.getTipo());
				lancamento.setValorPago(criterioBusca.getValor());
				
				// Salva o lançamento
				lancamentoContaService.cadastrar(lancamento);
				lancamentosAdicionados.add(lancamento);
				infoMessage("Lançamento cadastrado com sucesso e adicionado à fatura.");
				criterioBusca = new CriterioLancamentoConta();
			} catch (BusinessException be) {
				errorMessage(be.getMessage());
			}
		}		
	}
	
	public void excluirLancamento() {
		lancamentosAdicionados.remove(lancamento);
	}
	
	public void pesquisarLancamento() {
		try {
			criterioBusca.setConta(contaService.buscarPorID(entity.getConta().getId()));
			lancamentosEncontrados.clear();
			lancamentosEncontrados.addAll(lancamentoContaService.buscarPorCriterioLancamentoConta(criterioBusca));
			lancamentosEncontrados.removeAll(lancamentosAdicionados);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public double getSaldoDevedor() {
		try {
			if (cartaoSelecionado != null)
				return getService().saldoDevedorUltimaFatura(cartaoSelecionado);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return 0.0;
	}
	
	public String fecharFaturaView() {
		if (moedas == null || moedas.isEmpty()) {
			warnMessage("Selecione uma fatura para fechar.");
		} else if (faturaSelecionada == null || !faturaSelecionada.getStatusFaturaCartao().equals(StatusFaturaCartao.ABERTA)) {
			warnMessage("Somente faturas com status ABERTA podem ser fechada!");
		} 
		else {
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
		}
		return "";
	}
	
	public String fecharFatura() {
		try {
			getService().fecharFatura(faturaSelecionada, moedas);
			infoMessage("Fatura fechada com sucesso!");
			initializeEntity();
			return "/pages/FaturaCartao/listFaturaCartao";			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public void reabrirFatura() {
		try {
			if (faturaSelecionada == null || !faturaSelecionada.getStatusFaturaCartao().equals(StatusFaturaCartao.FECHADA)) {
				warnMessage("Somente fatura com status FECHADA podem ser reabertas!");
				return;
			}
			getService().reabrirFatura(faturaSelecionada);
			infoMessage("Fatura reaberta com sucesso!");
			initializeEntity();			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String quitarFaturaView() {
		if (faturaSelecionada == null || !faturaSelecionada.getStatusFaturaCartao().equals(StatusFaturaCartao.FECHADA)) {
			warnMessage("Somente faturas com status FECHADA podem ser quitadas!");
		} else {
			criterioBusca = new CriterioLancamentoConta();
			actionTitle = " - Quitar Fatura";
			return "/pages/FaturaCartao/quitarFatura";
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
				lancamentosEncontrados.addAll(lancamentoContaService.buscarPorCriterioLancamentoConta(criterioBusca));
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String quitarFatura() {
		try {
			if (formaPagamentoFatura == 3) {
				// Verifica se um lançamento foi selecionado na listagem
				for (LancamentoConta l : lancamentosEncontrados) {
					if (l.isSelecionado()) {
						lancamento = l;
						break;
					}
				}
				
				if(lancamento ==  null || lancamento.getConta() == null) {
					warnMessage("Selecione um lançamento!");
					return "";
				}
			}
			
			// Determina a forma de pagamento da fatura
			switch(formaPagamentoFatura) {
				case 1 : getService().quitarFaturaDebitoConta(faturaSelecionada, contaSelecionada, valorAQuitar, dataPagamento); break;
				case 2 : getService().quitarFaturaParcelamento(); break;
				case 3 : getService().quitarFaturaLancamentoSelecionado(faturaSelecionada, lancamento); break;
				default : throw new BusinessException("Opção inválida!");
			}			
			infoMessage("Fatura quitada com sucesso!");
			initializeEntity();
			return "/pages/FaturaCartao/listFaturaCartao";
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String detalheFatura() {
		try {
			if (faturaSelecionada == null) {
				warnMessage("Selecione uma fatura!");
			} else {
				entity = getService().buscarPorID(faturaSelecionada.getId());
				listEntity.clear(); 
				listEntity.addAll(entity.getDetalheFatura());
				this.calcularSaldoCompraSaqueParceladoPorMoeda();
				for (ConversaoMoeda conversao : entity.getConversoesMoeda()) {
					moedas.get(moedas.indexOf(conversao.getMoeda())).setTaxaConversao(conversao.getTaxaConversao());
				}
				this.calculaValorConversao();
				actionTitle = " - Detalhes";
				return "/pages/FaturaCartao/detalheFatura";
			}
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
	
	public void atualizaListaFatura() {
		this.getListaFaturaCartao();
	}
	
	public void exibirOpcaoFormaPagamento() {
		if (formaPagamentoFatura == 1) {
			exibirParcelarFatura = false;
		} else {
			exibirParcelarFatura = true;
		}
	}
	
	public List<CartaoCredito> getListaCartaoSoCredito() {
		try {
			return cartaoCreditoService.buscarSomenteCreditoPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<Moeda> getListaMoeda() {
		try {
			return moedaService.buscarPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<FaturaCartao> getListaFaturaCartao() {
		try {
			if (cartaoSelecionado == null) {
				return getService().buscarTodos();
			} else {
				return getService().buscarTodosPorCartaoCredito(cartaoSelecionado.getConta());
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<FaturaCartao>();
	}
	
	public List<Conta> getListaConta() {
		try {
			return contaService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public List<SelectItem> getListaTipoLancamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();		
		listaSelectItem.add(new SelectItem(TipoLancamento.DESPESA, "Despesa"));
		listaSelectItem.add(new SelectItem(TipoLancamento.RECEITA, "Receita"));
		return listaSelectItem;
	}

	public Moeda getMoedaPadrao() {
		return moedaPadrao;
	}

	public void setMoedaPadrao(Moeda moedaPadrao) {
		this.moedaPadrao = moedaPadrao;
	}

	public List<LancamentoConta> getListEntity() {
		return listEntity;
	}

	public void setListEntity(List<LancamentoConta> listEntity) {
		this.listEntity = listEntity;
	}

	public void setMoedaService(IMoeda moedaService) {
		this.moedaService = moedaService;
	}

	public List<Moeda> getMoedas() {
		return moedas;
	}

	public void setMoedas(List<Moeda> moedas) {
		this.moedas = moedas;
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

	public void setCartaoCreditoService(ICartaoCredito cartaoCreditoService) {
		this.cartaoCreditoService = cartaoCreditoService;
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

	public IFaturaCartao getService() {
		return service;
	}

	public void setService(IFaturaCartao service) {
		this.service = service;
	}

	public FaturaCartao getEntity() {
		return entity;
	}

	public void setEntity(FaturaCartao entity) {
		this.entity = entity;
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

	public CriterioLancamentoConta getCriterioBusca() {
		return criterioBusca;
	}

	public void setCriterioBusca(CriterioLancamentoConta criterioBusca) {
		this.criterioBusca = criterioBusca;
	}

	public void setLancamentoContaService(ILancamentoConta lancamentoContaService) {
		this.lancamentoContaService = lancamentoContaService;
	}

	public LancamentoConta getLancamento() {
		return lancamento;
	}

	public void setLancamento(LancamentoConta lancamento) {
		this.lancamento = lancamento;
	}

	public boolean isExibirParcelarFatura() {
		return exibirParcelarFatura;
	}

	public void setExibirParcelarFatura(boolean exibirParcelarFatura) {
		this.exibirParcelarFatura = exibirParcelarFatura;
	}

	public int getFormaPagamentoFatura() {
		return formaPagamentoFatura;
	}

	public void setFormaPagamentoFatura(int formaPagamentoFatura) {
		this.formaPagamentoFatura = formaPagamentoFatura;
	}

	public Conta getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(Conta contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
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
}