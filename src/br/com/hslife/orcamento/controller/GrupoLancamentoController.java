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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.GrupoLancamento;
import br.com.hslife.orcamento.entity.ItemGrupoLancamento;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.CadastroSistema;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IGrupoLancamento;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.ILancamentoPeriodico;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;

@Component
@Scope("session")
public class GrupoLancamentoController extends AbstractCRUDController<GrupoLancamento> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5508989331062227746L;

	@Autowired
	private IGrupoLancamento service; 
	
	@Autowired
	private ILancamentoConta lancamentoContaService;
	
	@Autowired
	private ILancamentoPeriodico lancamentoPeriodicoService;
	
	@Autowired
	private IMoeda moedaService;
	
	private String descricao;
	private boolean somenteAtivos;
	private String selecaoLancamento;
	private CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
	private List<ItemGrupoLancamento> itensEncontrados = new ArrayList<>();
	private ItemGrupoLancamento itemSelecionado = new ItemGrupoLancamento();
	
	public GrupoLancamentoController() {
		super(new GrupoLancamento());
		moduleTitle = "Grupos de Lançamentos";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new GrupoLancamento();
		listEntity = new ArrayList<>();
		itemSelecionado = new ItemGrupoLancamento();
		criterioBusca = new CriterioBuscaLancamentoConta();
	}
	
	@Override
	public void find() {
		try {
			listEntity = getService().buscarTodosDescricaoEAtivoPorUsuario(descricao, somenteAtivos, getUsuarioLogado());
		} catch (ValidationException | BusinessException e) {
			errorMessage(e.getMessage());
		}
	}
	
	public void buscarLancamentos() {
		itensEncontrados = new ArrayList<>();
		List<LancamentoConta> listaLancamentoConta = null;
		List<LancamentoPeriodico> listaLancamentoPeriodico = null;
		
		if (criterioBusca.quantCriteriosDefinidos() == 0) {
			warnMessage("Refine sua busca!");
			return;
		}
		
		try {
			
			switch (selecaoLancamento) {
				case "LANCAMENTO_CONTA" :
					criterioBusca.setCadastro(CadastroSistema.MOEDA);
					criterioBusca.setIdAgrupamento(entity.getMoeda().getId());
					listaLancamentoConta = getLancamentoContaService().buscarPorCriterioBusca(criterioBusca);
					break;
				case "LANCAMENTO_PERIODICO" :
					listaLancamentoPeriodico = getLancamentoPeriodicoService()
						.buscarDescricaoEDataAquisicaoPorUsuario(criterioBusca.getDescricao(), 
								criterioBusca.getDataInicio(), 
								criterioBusca.getDataFim(), 
								getUsuarioLogado());
					break;
			}
			
			if (listaLancamentoConta != null && !listaLancamentoConta.isEmpty()) {
				for (LancamentoConta lancamento : listaLancamentoConta) {
					itensEncontrados.add(new ItemGrupoLancamento(
							lancamento.getId(), 
							lancamento.getDescricao(), 
							lancamento.getTipoLancamento(), 
							lancamento.getDataPagamento(),
							lancamento.getValorPago()));
				}
			}
			
			if (listaLancamentoPeriodico != null && !listaLancamentoPeriodico.isEmpty()) {
				for (LancamentoPeriodico lancamento : listaLancamentoPeriodico) {
					itensEncontrados.add(new ItemGrupoLancamento(
							lancamento.getId(), 
							lancamento.getDescricao(), 
							lancamento.getTipoLancamento(), 
							lancamento.getDataAquisicao(),
							getLancamentoContaService().buscarUltimoPagamentoPeriodoGerado(lancamento).getValorPago()));
				}
			}
			
		} catch (ValidationException | BusinessException e) {
			errorMessage(e.getMessage());
		}
	}
	
	public List<Moeda> getListaMoeda() {
		try {
			return moedaService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}

	public IGrupoLancamento getService() {
		return service;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isSomenteAtivos() {
		return somenteAtivos;
	}

	public void setSomenteAtivos(boolean somenteAtivos) {
		this.somenteAtivos = somenteAtivos;
	}

	public CriterioBuscaLancamentoConta getCriterioBusca() {
		return criterioBusca;
	}

	public void setCriterioBusca(CriterioBuscaLancamentoConta criterioBusca) {
		this.criterioBusca = criterioBusca;
	}

	public String getSelecaoLancamento() {
		return selecaoLancamento;
	}

	public void setSelecaoLancamento(String selecaoLancamento) {
		this.selecaoLancamento = selecaoLancamento;
	}

	public List<ItemGrupoLancamento> getItensEncontrados() {
		return itensEncontrados;
	}

	public void setItensEncontrados(List<ItemGrupoLancamento> itensEncontrados) {
		this.itensEncontrados = itensEncontrados;
	}

	public ItemGrupoLancamento getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(ItemGrupoLancamento itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public ILancamentoConta getLancamentoContaService() {
		return lancamentoContaService;
	}

	public ILancamentoPeriodico getLancamentoPeriodicoService() {
		return lancamentoPeriodicoService;
	}

	public IMoeda getMoedaService() {
		return moedaService;
	}
}