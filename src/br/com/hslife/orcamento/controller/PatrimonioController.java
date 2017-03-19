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

import br.com.hslife.orcamento.entity.CategoriaDocumento;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.GrupoLancamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Patrimonio;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.ICategoriaDocumento;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IGrupoLancamento;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.facade.IPatrimonio;

@Component
@Scope("session")
public class PatrimonioController extends AbstractCRUDController<Patrimonio> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5919353747340308697L;
	
	@Autowired
	private IPatrimonio service;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private ICategoriaDocumento categoriaDocumentoService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private IGrupoLancamento grupoLancamentoService;
	
	public PatrimonioController() {
		super(new Patrimonio());
		moduleTitle = "Patrimônio";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Patrimonio();
		listEntity = new ArrayList<>();
	}
	
	@Override
	public String startUp() {
		find();
		return super.startUp();
	}
	
	@Override
	public void find() {
		try {
			listEntity = getService().buscarTodosPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException e) {
			errorMessage(e.getMessage());
		}
	}
	
	@Override
	public String save() {
		entity.setUsuario(getUsuarioLogado()); // TODO setar o usuário da entidade via Reflection, no AbstractCRUDController
		return super.save();
	}
	
	public List<Favorecido> getListaFavorecido() {
		try {
			return getFavorecidoService().buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<CategoriaDocumento> getListaCategoriaDocumento() {
		try {
			return getCategoriaDocumentoService().buscarPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<Moeda> getListaMoeda() {
		try {
			return getMoedaService().buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<GrupoLancamento> getListaGrupoLancamento() {
		try {
			return getGrupoLancamentoService().buscarTodosDescricaoEAtivoPorUsuario(null, true, getUsuarioLogado());
		} catch (ValidationException | BusinessException e) {
			errorMessage(e.getMessage());
		}
		return new ArrayList<>();
	}

	public IPatrimonio getService() {
		return service;
	}

	public IFavorecido getFavorecidoService() {
		return favorecidoService;
	}

	public ICategoriaDocumento getCategoriaDocumentoService() {
		return categoriaDocumentoService;
	}

	public IMoeda getMoedaService() {
		return moedaService;
	}

	public IGrupoLancamento getGrupoLancamentoService() {
		return grupoLancamentoService;
	}
	
	/**
	 * 
	 *
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
	
	public PatrimonioController() {
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
					if (item.getLancamentoConta().equals(itemGrupo.getLancamentoConta())) {
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
	*/
}