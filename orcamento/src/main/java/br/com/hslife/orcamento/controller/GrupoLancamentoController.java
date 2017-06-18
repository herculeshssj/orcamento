/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor
	
	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

    para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 - 
	
	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/

package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.GrupoLancamento;
import br.com.hslife.orcamento.entity.ItemGrupoLancamento;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.CadastroSistema;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IGrupoLancamento;
import br.com.hslife.orcamento.facade.ILancamentoConta;
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
	private IMoeda moedaService;
	
	private String descricao;
	private Long idLancamento;
	private CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
	private List<ItemGrupoLancamento> itensEncontrados = new ArrayList<>();
	private ItemGrupoLancamento itemSelecionado = new ItemGrupoLancamento();
	private ItemGrupoLancamento itemGrupoLancamento = new ItemGrupoLancamento();
	
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
		itensEncontrados = new ArrayList<>();
	}
	
	@Override
	public void find() {
		try {
			listEntity = getService().buscarTodosDescricaoEAtivoPorUsuario(descricao, true, getUsuarioLogado());
		} catch (ValidationException | BusinessException e) {
			errorMessage(e.getMessage());
		}
	}
	
	@Override
	public String save() {
		// Verifica se o grupo possui pelo menos um lançamento adicionado
		if (entity.getItens().isEmpty()) {
			warnMessage("Inclua pelo menos um lançamento no grupo!");
			return "";
		}
		
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}
	
	public void buscarLancamentos() {
		itensEncontrados = new ArrayList<>();
		List<LancamentoConta> listaLancamentoConta = null;
		
		if (criterioBusca.quantCriteriosDefinidos() == 0) {
			warnMessage("Refine sua busca!");
			return;
		}
		
		try {
		
			criterioBusca.setCadastro(CadastroSistema.MOEDA);
			criterioBusca.setIdAgrupamento(entity.getMoeda().getId());
			listaLancamentoConta = getLancamentoContaService().buscarPorCriterioBusca(criterioBusca);
			
			if (listaLancamentoConta != null && !listaLancamentoConta.isEmpty()) {
				for (LancamentoConta lancamento : listaLancamentoConta) {
					itensEncontrados.add(new ItemGrupoLancamento(
							lancamento, 
							lancamento.getDescricao(), 
							lancamento.getTipoLancamento(), 
							lancamento.getDataPagamento(),
							lancamento.getValorPago()));
				}
			}
			
		} catch (ValidationException | BusinessException e) {
			errorMessage(e.getMessage());
		}
	}
	
	public void excluirLancamento() {
		// Verifica se o lançamento já existe
		for (Iterator<ItemGrupoLancamento> i = entity.getItens().iterator(); i.hasNext(); ) {
			ItemGrupoLancamento item = i.next();
			
			// Remove pelo ID da entidade
			if (item.getId() != null) 
				if (item.getId().equals(itemSelecionado.getId()))
					i.remove();
		
			// Remove pelo ID do lançamento da conta
			if (item.getId() == null && item.getLancamentoConta() != null)
				if (item.getLancamentoConta().equals(itemSelecionado.getLancamentoConta()))
					i.remove();
			
			// Remove pelo UUID do item
			if (item.getId() == null && item.getLancamentoConta() == null)
				if (item.getUuid().equals(itemSelecionado.getUuid()))
					i.remove();
		}
		
		entity.recalculaTotais();
		
		itemSelecionado = null;
	}
	
	public void incluirLancamento() {
		// Verifica se existe algum item selecionado
		boolean temSelecao = false;
		for (ItemGrupoLancamento item : itensEncontrados) {
			if (item.isSelecionado()) {
				temSelecao = true;
				break;
			}
		}
		if (!temSelecao) {
			warnMessage("Selecione um item para adicionar!");
			return;
		}
		
		// Adiciona os itens que foram selecionados e que ainda não foram adicionados
		for (ItemGrupoLancamento itemGrupo : itensEncontrados) {
			if (itemGrupo.isSelecionado()) {
				boolean itemPresente = false;
				// Verifica se o lançamento já existe
				for (ItemGrupoLancamento item : entity.getItens()) {
					if (item.getLancamentoConta() != null && item.getLancamentoConta().equals(itemGrupo.getLancamentoConta())) {
						itemPresente = true;
						break;
					}
				}
				if (!itemPresente) {
					itemGrupo.setGrupoLancamento(entity);
					entity.getItens().add(itemGrupo);
				}	
			}
		}
		
		entity.recalculaTotais();
		
		// Exclui da busca os itens que já foram adicionados.
		for (Iterator<ItemGrupoLancamento> iterator = itensEncontrados.iterator(); iterator.hasNext(); ) {
			ItemGrupoLancamento item = iterator.next();
			if (item.isSelecionado())
				iterator.remove();
		}
	}
	
	public void adicionarItemGrupo() {
		// Valida o preenchimento da entidade
		try {
			itemGrupoLancamento.validate();
		} catch (ValidationException e) {
			errorMessage(e.getMessage());
			return;
		}
		
		itemGrupoLancamento.setUuid(UUID.randomUUID().toString());
		itemGrupoLancamento.setGrupoLancamento(entity);
		entity.getItens().add(itemGrupoLancamento);
		
		entity.recalculaTotais();
		
		itemGrupoLancamento = new ItemGrupoLancamento();
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

	public CriterioBuscaLancamentoConta getCriterioBusca() {
		return criterioBusca;
	}

	public void setCriterioBusca(CriterioBuscaLancamentoConta criterioBusca) {
		this.criterioBusca = criterioBusca;
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

	public IMoeda getMoedaService() {
		return moedaService;
	}

	public Long getIdLancamento() {
		return idLancamento;
	}

	public void setIdLancamento(Long idLancamento) {
		this.idLancamento = idLancamento;
	}

	public ItemGrupoLancamento getItemGrupoLancamento() {
		return itemGrupoLancamento;
	}

	public void setItemGrupoLancamento(ItemGrupoLancamento itemGrupoLancamento) {
		this.itemGrupoLancamento = itemGrupoLancamento;
	}
}