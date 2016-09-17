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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Despensa;
import br.com.hslife.orcamento.entity.ItemDespensa;
import br.com.hslife.orcamento.entity.MovimentoItemDespensa;
import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.entity.UnidadeMedida;
import br.com.hslife.orcamento.enumeration.OperacaoDespensa;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IDespensa;
import br.com.hslife.orcamento.facade.IItemDespensa;
import br.com.hslife.orcamento.facade.IUnidadeMedida;
import br.com.hslife.orcamento.util.ItemDespensaComparator;
import br.com.hslife.orcamento.util.Util;

@Component("itemDespensaMB")
@Scope("session")
public class ItemDespensaController extends AbstractCRUDController<ItemDespensa> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 885283739776409043L;

	@Autowired
	private IItemDespensa service;
	
	@Autowired
	private IDespensa despensaService;
	
	@Autowired
	private IUnidadeMedida unidadeMedidaService;
	
	/* Campos de busca */
	private Despensa despensa;
	private ItemDespensa itemDespensa;
	private boolean arquivado;
	
	private MovimentoItemDespensa movimentoItemDespensa;
	private Date validadeItemDespensa;
	private double valorItemDespensa;
	private int quantidadeItemDespensa = 1;
	
	private boolean exibirCamposCompra;
	
	private List<ItemDespensa> listaCompras;
	private int totalItensListaCompra = 0;
	private double totalValorListaCompra = 0.0;
	private int totalItens;
	
	public ItemDespensaController() {
		super(new ItemDespensa());
		moduleTitle = "Itens de Despensa";
	}

	@Override
	protected void initializeEntity() {
		entity = new ItemDespensa();
		listEntity = new ArrayList<ItemDespensa>();
		movimentoItemDespensa = new MovimentoItemDespensa();
		itemDespensa = new ItemDespensa();
		validadeItemDespensa = new Date();
		quantidadeItemDespensa = 1;
	}
	
	@Override
	public void find() {
		try {
			if (despensa == null) {
				listEntity = getService().buscarPorUsuarioEArquivado(getUsuarioLogado(), arquivado);
			} else {
				listEntity = getService().buscarPorDespensaUsuarioEArquivado(despensa, getUsuarioLogado(), arquivado);
			}
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String registrarCompraConsumoView() {
		try {
			entity = getService().buscarPorID(idEntity);
			actionTitle = " - Registra Compra/Consumo";
			movimentoItemDespensa = new MovimentoItemDespensa();
			validadeItemDespensa = new Date();
			return "/pages/ItemDespensa/registrarCompraConsumo";
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String registrarCompraConsumo() {
		try {
			if (movimentoItemDespensa.getOperacaoDespensa().equals(OperacaoDespensa.COMPRA)) {
				entity.setValidade(validadeItemDespensa);
				entity.setValor(movimentoItemDespensa.getValor());
			}
			getService().registrarCompraConsumo(entity, movimentoItemDespensa);
			infoMessage("Registro salvo com sucesso!");
			// Verifica se a listagem de resultados está nula ou não para poder efetuar novamente a busca
			if (listEntity != null && !listEntity.isEmpty()) {
				// Inicializa os objetos
				initializeEntity();
				
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", getUsuarioLogado());
							
				// Determina se a busca será executada novamente
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {					
					find();
				}
			} else {
				initializeEntity();
			}
			return list();
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String desfazerRegistroCompraConsumoView() {
		actionTitle = " - Desfazer última movimentação";
		return "/pages/ItemDespensa/desfazerRegistroCompraConsumo";
	}
	
	public String desfazerRegistroCompraConsumo() {
		try {
			getService().desfazerRegistroCompraConsumo(itemDespensa);
			infoMessage("Última movimentação desfeita com sucesso!");
			
			// Verifica se a listagem de resultados está nula ou não para poder efetuar novamente a busca
			if (listEntity != null && !listEntity.isEmpty()) {
				// Inicializa os objetos
				initializeEntity();
				
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", getUsuarioLogado());
							
				// Determina se a busca será executada novamente
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {					
					find();
				}
			} else {
				initializeEntity();
			}
			return list();
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String arquivar() {
		try {
			getService().arquivarItemDespensa(entity);
			infoMessage("Item de despensa arquivado com sucesso!");
			// Verifica se a listagem de resultados está nula ou não para poder efetuar novamente a busca
			if (listEntity != null && !listEntity.isEmpty()) {
				// Inicializa os objetos
				initializeEntity();
				
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", getUsuarioLogado());
							
				// Determina se a busca será executada novamente
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {					
					find();
				}
			} else {
				initializeEntity();
			}
			return list();
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String desarquivar() {
		try {
			getService().desarquivarItemDespensa(entity);
			infoMessage("Item de despensa restaurado com sucesso!");
			// Verifica se a listagem de resultados está nula ou não para poder efetuar novamente a busca
			if (listEntity != null && !listEntity.isEmpty()) {
				// Inicializa os objetos
				initializeEntity();
				
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", getUsuarioLogado());
							
				// Determina se a busca será executada novamente
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {					
					find();
				}
			} else {
				initializeEntity();
			}
			return list();
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String apagarHistorico() {
		try {
			getService().apagarHistorico(entity);
			infoMessage("Histórico de movimentações apagado com sucesso!");
			// Verifica se a listagem de resultados está nula ou não para poder efetuar novamente a busca
			if (listEntity != null && !listEntity.isEmpty()) {
				// Inicializa os objetos
				initializeEntity();
				
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", getUsuarioLogado());
							
				// Determina se a busca será executada novamente
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {					
					find();
				}
			} else {
				initializeEntity();
			}
			return list();
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String gerarListaCompras() {
		try {
			if (getOpcoesSistema().getControlarEstoqueItemDespensa()) {
				listaCompras = getService().gerarListaCompras(getUsuarioLogado());
				this.calcularTotaisListaCompra();
			} else {
				listaCompras = new ArrayList<>();
			}
			actionTitle = " - Lista de Compras";
			operation = "list";
			return "/pages/ItemDespensa/listaCompraItemDespensa";
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public void adicionarItemListaCompra() {
		if (itemDespensa != null) {
			
			// Verifica se o item existe na lista de compras
			// Caso não exista, adiciona. Se existir, incrementa a
			// quantidade
			if (listaCompras.contains(itemDespensa)) {
				int quantidade = listaCompras.get(listaCompras.indexOf(itemDespensa)).getQuantidadeAtual();
				double valor = listaCompras.get(listaCompras.indexOf(itemDespensa)).getValor();
				
				quantidade = quantidade + quantidadeItemDespensa;
				valor = itemDespensa.getValor() * quantidade;
				
				listaCompras.get(listaCompras.indexOf(itemDespensa)).setQuantidadeAtual(quantidade);
				listaCompras.get(listaCompras.indexOf(itemDespensa)).setValor(valor);
			} else {
				itemDespensa.setQuantidadeAtual(quantidadeItemDespensa);
				itemDespensa.setValor(itemDespensa.getValor() * quantidadeItemDespensa);
				listaCompras.add(itemDespensa);
				listaCompras.sort(new ItemDespensaComparator());				
			}
			
			this.calcularTotaisListaCompra();
			itemDespensa = new ItemDespensa();
		}
	}
	
	public void removerItemListaCompra() {
		listaCompras.remove(itemDespensa);
		listaCompras.sort(new ItemDespensaComparator());
		this.calcularTotaisListaCompra();
	}
	
	private void calcularTotaisListaCompra() {
		// Calcula o total de itens e o valor de cada um
		totalItensListaCompra = 0;
		totalValorListaCompra = 0.0;
		for (ItemDespensa item : listaCompras) {
			totalItensListaCompra += item.getQuantidadeAtual();
			totalValorListaCompra += item.getValor();
			totalValorListaCompra = Util.arredondar(totalValorListaCompra);
		}
		totalItens = listaCompras.size();
	}
	
	public void exibirCamposValidadeValor() {
		if (movimentoItemDespensa.getOperacaoDespensa().equals(OperacaoDespensa.COMPRA)) {
			exibirCamposCompra = true;			
		} else {
			exibirCamposCompra = false;
		}
	}
	
	public List<Despensa> getListaDespensa() {
		try {
			return despensaService.buscarTodosPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Despensa>();
	}
	
	public List<ItemDespensa> getListaItemDespensa() {
		try {
			if (despensa != null) {
				return getService().buscarPorDespensaUsuarioEArquivado(despensa, getUsuarioLogado(), false);
			}
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<UnidadeMedida> getListaUnidadeMedida() {
		try {
			return unidadeMedidaService.buscarPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<UnidadeMedida>();
	}
	
	public List<SelectItem> getListaOperacaoDespensa() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		listaSelectItem.add(new SelectItem(OperacaoDespensa.COMPRA, "Compra"));
		listaSelectItem.add(new SelectItem(OperacaoDespensa.CONSUMO, "Consumo"));
		return listaSelectItem;
	}

	public IItemDespensa getService() {
		return service;
	}

	public void setService(IItemDespensa service) {
		this.service = service;
	}

	public Despensa getDespensa() {
		return despensa;
	}

	public void setDespensa(Despensa despensa) {
		this.despensa = despensa;
	}

	public boolean isArquivado() {
		return arquivado;
	}

	public void setArquivado(boolean arquivado) {
		this.arquivado = arquivado;
	}

	public IDespensa getDespensaService() {
		return despensaService;
	}

	public void setDespensaService(IDespensa despensaService) {
		this.despensaService = despensaService;
	}

	public IUnidadeMedida getUnidadeMedidaService() {
		return unidadeMedidaService;
	}

	public void setUnidadeMedidaService(IUnidadeMedida unidadeMedidaService) {
		this.unidadeMedidaService = unidadeMedidaService;
	}

	public MovimentoItemDespensa getMovimentoItemDespensa() {
		return movimentoItemDespensa;
	}

	public void setMovimentoItemDespensa(MovimentoItemDespensa movimentoItemDespensa) {
		this.movimentoItemDespensa = movimentoItemDespensa;
	}

	public Date getValidadeItemDespensa() {
		return validadeItemDespensa;
	}

	public void setValidadeItemDespensa(Date validadeItemDespensa) {
		this.validadeItemDespensa = validadeItemDespensa;
	}

	public boolean isExibirCamposCompra() {
		return exibirCamposCompra;
	}

	public void setExibirCamposCompra(boolean exibirCamposCompra) {
		this.exibirCamposCompra = exibirCamposCompra;
	}

	public double getValorItemDespensa() {
		return valorItemDespensa;
	}

	public void setValorItemDespensa(double valorItemDespensa) {
		this.valorItemDespensa = valorItemDespensa;
	}

	public List<ItemDespensa> getListaCompras() {
		return listaCompras;
	}

	public void setListaCompras(List<ItemDespensa> listaCompras) {
		this.listaCompras = listaCompras;
	}

	public ItemDespensa getItemDespensa() {
		return itemDespensa;
	}

	public void setItemDespensa(ItemDespensa itemDespensa) {
		this.itemDespensa = itemDespensa;
	}

	public int getQuantidadeItemDespensa() {
		return quantidadeItemDespensa;
	}

	public void setQuantidadeItemDespensa(int quantidadeItemDespensa) {
		this.quantidadeItemDespensa = quantidadeItemDespensa;
	}

	public int getTotalItensListaCompra() {
		return totalItensListaCompra;
	}

	public void setTotalItensListaCompra(int totalItensListaCompra) {
		this.totalItensListaCompra = totalItensListaCompra;
	}

	public double getTotalValorListaCompra() {
		return totalValorListaCompra;
	}

	public void setTotalValorListaCompra(double totalValorListaCompra) {
		this.totalValorListaCompra = totalValorListaCompra;
	}

	public int getTotalItens() {
		return totalItens;
	}

	public void setTotalItens(int totalItens) {
		this.totalItens = totalItens;
	}
}