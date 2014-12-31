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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.DetalheOrcamento;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Orcamento;
import br.com.hslife.orcamento.enumeration.AbrangenciaOrcamento;
import br.com.hslife.orcamento.enumeration.PeriodoLancamento;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoOrcamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.facade.IOrcamento;
import br.com.hslife.orcamento.util.Util;

@Component("orcamentoMB")
@Scope("session")
public class OrcamentoController extends AbstractCRUDController<Orcamento> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5508989331062227746L;

	@Autowired
	private IOrcamento service; 
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private ICategoria categoriaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;
	
	@Autowired
	private IMoeda moedaService;
	
	private Orcamento orcamentoSelecionado;
	private DetalheOrcamento detalheOrcamento;
	private List<DetalheOrcamento> listaDetalheOrcamento = new ArrayList<DetalheOrcamento>();
	private List<DetalheOrcamento> listaItemDetalheOrcamento = new ArrayList<DetalheOrcamento>();
	
	private double previsao;
	private double previsaoCredito;
	private double previsaoDebito;
	
	private boolean mostrarInformacao;
	
	public OrcamentoController() {
		super(new Orcamento());
		moduleTitle = "Orçamento do Período";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Orcamento();
		orcamentoSelecionado = null;
		listEntity = new ArrayList<Orcamento>();
		listaDetalheOrcamento = new ArrayList<DetalheOrcamento>();
		listaItemDetalheOrcamento = new ArrayList<DetalheOrcamento>();
	}
	
	@Override
	public void find() {
		if (orcamentoSelecionado != null) {
			listaDetalheOrcamento.clear();
			listaDetalheOrcamento.addAll(orcamentoSelecionado.getDetalhes());
			mostrarInformacao = true;
		}
	}
	
	@Override
	public List<Orcamento> getListEntity() {
		try {
			return getService().buscarTodosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		mostrarInformacao = false;
		return new ArrayList<Orcamento>();
	}
	
	@Override
	public String create() {
		initializeEntity();
		return super.create();
	}
	
	@Override
	public String edit() {
		if (idEntity == null) {
			warnMessage("Selecione um orçamento!");
			return "";
		}
		super.edit();
		listaItemDetalheOrcamento.clear();
		listaItemDetalheOrcamento.addAll(entity.getDetalhes());
		return goToFormPage;
	}
	
	@Override
	public String view() {
		if (idEntity == null) {
			warnMessage("Selecione um orçamento!");
			return "";
		}
		super.view();
		listaItemDetalheOrcamento.clear();
		listaItemDetalheOrcamento.addAll(entity.getDetalhes());
		return goToViewPage;
	}
	
	@Override
	public String cancel() {
		mostrarInformacao = false;
		return super.cancel();
	}
	
	@Override
	public String save() {
		// Preenche a data de fim do período
		if (!entity.getPeriodoLancamento().equals(PeriodoLancamento.FIXO)) {
			Calendar temp = Calendar.getInstance();
			temp.setTime(entity.getInicio());
			
			switch (entity.getPeriodoLancamento()) {
				case MENSAL : temp.add(Calendar.MONTH, 1); temp.add(Calendar.DAY_OF_YEAR, -1); break;
				case BIMESTRAL : temp.add(Calendar.MONTH, 2); temp.add(Calendar.DAY_OF_YEAR, -1); break;
				case TRIMESTRAL : temp.add(Calendar.MONTH, 3); temp.add(Calendar.DAY_OF_YEAR, -1); break;
				case QUADRIMESTRAL : temp.add(Calendar.MONTH, 4); temp.add(Calendar.DAY_OF_YEAR, -1); break;
				case SEMESTRAL : temp.add(Calendar.MONTH, 6); temp.add(Calendar.DAY_OF_YEAR, -1); break;
				case ANUAL : temp.add(Calendar.YEAR, 1); temp.add(Calendar.DAY_OF_YEAR, -1); break;
				default : errorMessage("Opção inválida!"); return "";
			}
			
			entity.setFim(temp.getTime());
		}
		entity.setDetalhes(listaItemDetalheOrcamento);
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}
	
	public void atualizarValores() {
		try {
			getService().atualizarValores(orcamentoSelecionado);
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
	
	public String gerarNovoOrcamento() {
		try {
			getService().gerarOrcamento(entity);
			infoMessage("Orçamento gerado com sucesso!");
			
			if (getOpcoesSistema().getExibirBuscasRealizadas()) {
				find();
			} else {
				initializeEntity();
			}
			
			return goToListPage;
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		return "";
	}
	
	public void atualizaCampoContaTipoConta() {
		entity.setTipoConta(null);
		entity.setConta(null);
	}
	
	public void atualizaListaItens() {
		listaItemDetalheOrcamento.clear();
		this.getListaItensDetalheOrcamento();
	}
	
	public void adicionarItem() {
		if (detalheOrcamento != null) {
			detalheOrcamento.setPrevisao(previsao);
			detalheOrcamento.setPrevisaoCredito(previsaoCredito);
			detalheOrcamento.setPrevisaoDebito(previsaoDebito);
			listaItemDetalheOrcamento.add(detalheOrcamento);
		}
	}
	
	public void removerItem() {
		for (Iterator<DetalheOrcamento> i = listaItemDetalheOrcamento.iterator(); i.hasNext();) {
			if ((i.next()).isSelecionado()) {
				i.remove();
			}
		}
	}
	
	public List<DetalheOrcamento> getListaItensDetalheOrcamento() {
		List<DetalheOrcamento> resultado = new ArrayList<DetalheOrcamento>();
		if (entity.getAbrangenciaOrcamento() != null) {
			try {
				switch (entity.getAbrangenciaOrcamento()) {
					case CATEGORIA :						
						for (Categoria c : categoriaService.buscarAtivosPorUsuario(getUsuarioLogado())) {
							boolean encontrado = false;
							for (DetalheOrcamento detalhe : listaItemDetalheOrcamento) {
								if (detalhe.getIdEntity().equals(c.getId())) {
									encontrado = true;
									break;
								}							
							}
							if (!encontrado) 
								resultado.add(new DetalheOrcamento(c.getId(), c.getDescricao(), c.getTipoCategoria()));
						}						
						break;
					case FAVORECIDO :
						for (Favorecido f : favorecidoService.buscarAtivosPorUsuario(getUsuarioLogado())) {
							boolean encontrado = false;
							for (DetalheOrcamento detalhe : listaItemDetalheOrcamento) {
								if (detalhe.getIdEntity().equals(f.getId())) {
									encontrado = true;
									break;
								}							
							}
							if (!encontrado) 
								resultado.add(new DetalheOrcamento(f.getId(), f.getNome()));
						}
						break;
					case MEIOPAGAMENTO :
						for (MeioPagamento m : meioPagamentoService.buscarAtivosPorUsuario(getUsuarioLogado())) {
							boolean encontrado = false;
							for (DetalheOrcamento detalhe : listaItemDetalheOrcamento) {
								if (detalhe.getIdEntity().equals(m.getId())) {
									encontrado = true;
									break;
								}							
							}
							if (!encontrado) 
								resultado.add(new DetalheOrcamento(m.getId(), m.getDescricao()));
						}
						break;
				}
			} catch (BusinessException be) {
				errorMessage(be.getMessage());
			}
		}
		
		//resultado.removeAll(listaItemDetalheOrcamento);
		return resultado;
	}
	
	private enum TipoSaldoDetalheOrcamento {
		PREVISAO, REALIZADO, PREVISAO_DEBITO, REALIZADO_DEBITO, PREVISAO_CREDITO, REALIZADO_CREDITO;
	}
	
	private double getSaldoDetalheOrcamento(TipoSaldoDetalheOrcamento tipoSaldo) {
		double resultado = 0.0;
		
		for (DetalheOrcamento detalhe : listaDetalheOrcamento) {
			switch(tipoSaldo) {
				case PREVISAO :
					if (detalhe.getTipoCategoria().equals(TipoCategoria.CREDITO)) {
						resultado += detalhe.getPrevisao();
					} else {
						resultado -= detalhe.getPrevisao();
					}
					break;
				case REALIZADO :
					if (detalhe.getTipoCategoria().equals(TipoCategoria.CREDITO)) {
						resultado += detalhe.getRealizado();
					} else {
						resultado -= detalhe.getRealizado();
					}
					break;
				case PREVISAO_CREDITO : resultado += detalhe.getPrevisaoCredito(); break;
				case PREVISAO_DEBITO : resultado += detalhe.getPrevisaoDebito(); break;
				case REALIZADO_CREDITO : resultado += detalhe.getRealizadoCredito(); break;
				case REALIZADO_DEBITO : resultado += detalhe.getRealizadoDebito(); break;
			}
		}
		
		return Util.arredondar(resultado);
	}
	
	public String getSaldoPrevistoDetalheOrcamento() {
		return this.getMoedaPadrao().getSimboloMonetario() + " " + new DecimalFormat("#,##0.##").format(getSaldoDetalheOrcamento(TipoSaldoDetalheOrcamento.PREVISAO));
	}
	
	public String getSaldoRealizadoDetalheOrcamento() {
		return this.getMoedaPadrao().getSimboloMonetario() + " " + new DecimalFormat("#,##0.##").format(getSaldoDetalheOrcamento(TipoSaldoDetalheOrcamento.REALIZADO));
	}
	
	public String getPorcentagemSaldoDetalheOrcamento() {
		return NumberFormat.getNumberInstance()
				.format(Math.abs(Util.arredondar((getSaldoDetalheOrcamento(TipoSaldoDetalheOrcamento.REALIZADO) / getSaldoDetalheOrcamento(TipoSaldoDetalheOrcamento.PREVISAO)) * 100)))
				+ " %";
	}
	
	public String getSaldoCreditoPrevistoDetalheOrcamento() {
		return this.getMoedaPadrao().getSimboloMonetario() + " " + new DecimalFormat("#,##0.##").format(getSaldoDetalheOrcamento(TipoSaldoDetalheOrcamento.PREVISAO_CREDITO));
	}
	
	public String getSaldoCreditoRealizadoDetalheOrcamento() {
		return this.getMoedaPadrao().getSimboloMonetario() + " " + new DecimalFormat("#,##0.##").format(getSaldoDetalheOrcamento(TipoSaldoDetalheOrcamento.REALIZADO_CREDITO));
	}
	
	public String getPorcentagemSaldoCreditoDetalheOrcamento() {
		return NumberFormat.getNumberInstance()
				.format(Math.abs(Util.arredondar((getSaldoDetalheOrcamento(TipoSaldoDetalheOrcamento.REALIZADO_CREDITO) / getSaldoDetalheOrcamento(TipoSaldoDetalheOrcamento.PREVISAO_CREDITO)) * 100)))
		+ " %";
	}
	
	public String getSaldoDebitoPrevistoDetalheOrcamento() {
		return this.getMoedaPadrao().getSimboloMonetario() + " " + new DecimalFormat("#,##0.##").format(getSaldoDetalheOrcamento(TipoSaldoDetalheOrcamento.PREVISAO_DEBITO));
	}
	
	public String getSaldoDebitoRealizadoDetalheOrcamento() {
		return this.getMoedaPadrao().getSimboloMonetario() + " " + new DecimalFormat("#,##0.##").format(getSaldoDetalheOrcamento(TipoSaldoDetalheOrcamento.REALIZADO_DEBITO));
	}
	
	public String getPorcentagemSaldoDebitoDetalheOrcamento() {
		return NumberFormat.getNumberInstance()
				.format(Math.abs(Util.arredondar((getSaldoDetalheOrcamento(TipoSaldoDetalheOrcamento.REALIZADO_DEBITO) / getSaldoDetalheOrcamento(TipoSaldoDetalheOrcamento.PREVISAO_DEBITO)) * 100)))
				+ " %";
	}
	
	public Moeda getMoedaPadrao() {
		try {
			return moedaService.buscarPadraoPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return null;
	}
	
	public List<Conta> getListaConta() {
		try {
			return contaService.buscarPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<SelectItem> getListaTipoOrcamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (TipoOrcamento orcamento : TipoOrcamento.values()) {
			listaSelectItem.add(new SelectItem(orcamento, orcamento.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaTipoConta() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (TipoConta tipo : TipoConta.values()) {
			listaSelectItem.add(new SelectItem(tipo, tipo.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaPeriodoLancamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (PeriodoLancamento periodo : PeriodoLancamento.values()) {
			listaSelectItem.add(new SelectItem(periodo, periodo.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaAbrangenciaOrcamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (AbrangenciaOrcamento abrangencia : AbrangenciaOrcamento.values()) {
			listaSelectItem.add(new SelectItem(abrangencia, abrangencia.toString()));
		}
		return listaSelectItem;
	}

	public IOrcamento getService() {
		return service;
	}

	public void setService(IOrcamento service) {
		this.service = service;
	}

	public DetalheOrcamento getDetalheOrcamento() {
		return detalheOrcamento;
	}

	public void setDetalheOrcamento(DetalheOrcamento detalheOrcamento) {
		this.detalheOrcamento = detalheOrcamento;
	}

	public List<DetalheOrcamento> getListaDetalheOrcamento() {
		return listaDetalheOrcamento;
	}

	public void setListaDetalheOrcamento(
			List<DetalheOrcamento> listaDetalheOrcamento) {
		this.listaDetalheOrcamento = listaDetalheOrcamento;
	}

	public double getPrevisao() {
		return previsao;
	}

	public void setPrevisao(double previsao) {
		this.previsao = previsao;
	}

	public double getPrevisaoCredito() {
		return previsaoCredito;
	}

	public void setPrevisaoCredito(double previsaoCredito) {
		this.previsaoCredito = previsaoCredito;
	}

	public double getPrevisaoDebito() {
		return previsaoDebito;
	}

	public void setPrevisaoDebito(double previsaoDebito) {
		this.previsaoDebito = previsaoDebito;
	}

	public List<DetalheOrcamento> getListaItemDetalheOrcamento() {
		return listaItemDetalheOrcamento;
	}

	public void setListaItemDetalheOrcamento(
			List<DetalheOrcamento> listaItemDetalheOrcamento) {
		this.listaItemDetalheOrcamento = listaItemDetalheOrcamento;
	}

	public boolean isMostrarInformacao() {
		return mostrarInformacao;
	}

	public void setMostrarInformacao(boolean mostrarInformacao) {
		this.mostrarInformacao = mostrarInformacao;
	}

	public Orcamento getOrcamentoSelecionado() {
		return orcamentoSelecionado;
	}

	public void setOrcamentoSelecionado(Orcamento orcamentoSelecionado) {
		this.orcamentoSelecionado = orcamentoSelecionado;
	}
}