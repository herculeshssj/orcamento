/***

Copyright (c) 2012 - 2021 Hércules S. S. José

	Este arquivo é parte do programa Orçamento Doméstico.


	Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

	modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

	publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

	Licença.


	Este programa é distribuído na esperança que possa ser útil, mas SEM

	NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer

	MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor

	GNU em português para maiores detalhes.


	Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob

	o nome de "LICENSE" junto com este programa, se não, acesse o site do

	projeto no endereco https://github.com/herculeshssj/orcamento ou escreva

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.ItemMeta;
import br.com.hslife.orcamento.entity.Meta;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IMeta;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;

@Component
@Scope("session")
public class MetaController extends AbstractCRUDController<Meta> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5508989331062227746L;

	@Autowired
	private IMeta service; 
	
	@Autowired
	private IMoeda moedaService;
	
	private String descricao;
	private Long idLancamento;
	private CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
	private List<ItemMeta> itensEncontrados = new ArrayList<>();
	private ItemMeta itemSelecionado = new ItemMeta();
	private ItemMeta itemMeta = new ItemMeta();
	
	public MetaController() {
		super(new Meta());
		moduleTitle = "Metas";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Meta();
		listEntity = new ArrayList<>();
		itemSelecionado = new ItemMeta();
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
			warnMessage("Inclua pelo menos um item na meta!");
			return "";
		}
		
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}
	
	public void adicionarItemMeta() {
		// Valida o preenchimento da entidade
		try {
			itemMeta.validate();
		} catch (ValidationException e) {
			errorMessage(e.getMessage());
			return;
		}
		
		itemMeta.setMeta(entity);
		entity.getItens().add(itemMeta);
		
		entity.recalculaTotais();
		
		itemMeta = new ItemMeta();
	}
	
	public void excluirItemMeta() {
		// Verifica se o item já existe
		for (Iterator<ItemMeta> i = entity.getItens().iterator(); i.hasNext(); ) {
			ItemMeta item = i.next();
			
			// Remove pelo ID da entidade
			if (item.getId() != null) 
				if (item.getId().equals(itemSelecionado.getId()))
					i.remove();
		}
		
		entity.recalculaTotais();
		
		itemSelecionado = null;
	}
	
	public List<Moeda> getListaMoeda() {
		try {
			return moedaService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}

	public IMeta getService() {
		return service;
	}

	public void setService(IMeta service) {
		this.service = service;
	}

	public IMoeda getMoedaService() {
		return moedaService;
	}

	public void setMoedaService(IMoeda moedaService) {
		this.moedaService = moedaService;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getIdLancamento() {
		return idLancamento;
	}

	public void setIdLancamento(Long idLancamento) {
		this.idLancamento = idLancamento;
	}

	public CriterioBuscaLancamentoConta getCriterioBusca() {
		return criterioBusca;
	}

	public void setCriterioBusca(CriterioBuscaLancamentoConta criterioBusca) {
		this.criterioBusca = criterioBusca;
	}

	public List<ItemMeta> getItensEncontrados() {
		return itensEncontrados;
	}

	public void setItensEncontrados(List<ItemMeta> itensEncontrados) {
		this.itensEncontrados = itensEncontrados;
	}

	public ItemMeta getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(ItemMeta itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public ItemMeta getItemMeta() {
		return itemMeta;
	}

	public void setItemMeta(ItemMeta itemMeta) {
		this.itemMeta = itemMeta;
	}
}
