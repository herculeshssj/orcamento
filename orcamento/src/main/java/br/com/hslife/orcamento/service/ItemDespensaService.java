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
package br.com.hslife.orcamento.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.component.OpcaoSistemaComponent;
import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Despensa;
import br.com.hslife.orcamento.entity.ItemDespensa;
import br.com.hslife.orcamento.entity.MovimentoItemDespensa;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.OperacaoDespensa;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IItemDespensa;
import br.com.hslife.orcamento.repository.ItemDespensaRepository;

@Service("itemDespensaService")
public class ItemDespensaService extends AbstractCRUDService<ItemDespensa> implements IItemDespensa {
	
	@Autowired
	private ItemDespensaRepository repository;
	
	@Autowired
	private UsuarioComponent usuarioComponent;
	
	@Autowired
	public OpcaoSistemaComponent opcaoSistemaComponent;

	public ItemDespensaRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}
	
	public UsuarioComponent getUsuarioComponent() {
		return usuarioComponent;
	}

	public OpcaoSistemaComponent getOpcaoSistemaComponent() {
		return opcaoSistemaComponent;
	}

	@Override
	public void registrarCompraConsumo(ItemDespensa entity, MovimentoItemDespensa movimentoItemDespensa) {
		if (movimentoItemDespensa.getOperacaoDespensa().equals(OperacaoDespensa.CONSUMO) && (entity.getQuantidadeAtual() - movimentoItemDespensa.getQuantidade()) < 0) {
			throw new BusinessException("Quantidade informada ultrapassa estoque disponível!");
		}
		entity.getMovimentacao().add(movimentoItemDespensa);
		entity.setQuantidadeAtual(0);
		for (MovimentoItemDespensa mid : entity.getMovimentacao()) {
			if (mid.getOperacaoDespensa().equals(OperacaoDespensa.COMPRA)) {
				entity.setQuantidadeAtual(entity.getQuantidadeAtual() + mid.getQuantidade());
			} else {
				entity.setQuantidadeAtual(entity.getQuantidadeAtual() - mid.getQuantidade());
			}
		}
		getRepository().update(entity);
	}
	
	@Override
	public void desfazerRegistroCompraConsumo(ItemDespensa entity) {
		if (entity.getMovimentacao() != null && entity.getMovimentacao().size() > 0) {
			Collections.reverse(entity.getMovimentacao());
		} else {
			throw new BusinessException("Impossível desfazer! Item de despensa sem movimentação!");
		}
		MovimentoItemDespensa movimentoItemDespensa = entity.getMovimentacao().get(0);
		if (movimentoItemDespensa.getOperacaoDespensa().equals(OperacaoDespensa.COMPRA) && (entity.getQuantidadeAtual() - movimentoItemDespensa.getQuantidade()) < 0) {
			throw new BusinessException("Impossível desfazer! Quantidade informada ultrapassa estoque disponível!");
		}
		entity.getMovimentacao().remove(movimentoItemDespensa);
		entity.setQuantidadeAtual(0);
		for (MovimentoItemDespensa mid : entity.getMovimentacao()) {
			if (mid.getOperacaoDespensa().equals(OperacaoDespensa.COMPRA)) {
				entity.setQuantidadeAtual(entity.getQuantidadeAtual() + mid.getQuantidade());
			} else {
				entity.setQuantidadeAtual(entity.getQuantidadeAtual() - mid.getQuantidade());
			}
		}
		getRepository().update(entity);
	}
	
	@Override
	public void arquivarItemDespensa(ItemDespensa entity) {
		if (!entity.isArquivado()) {
			entity.setArquivado(true);
			getRepository().update(entity);
		}		
	}
	
	@Override
	public void desarquivarItemDespensa(ItemDespensa entity) {
		if (entity.isArquivado()) {
			entity.setArquivado(false);
			getRepository().update(entity);
		}
	}
	
	@Override
	public List<ItemDespensa> gerarListaCompras(Usuario usuario) {
		List<ItemDespensa> listaDespensas = new ArrayList<ItemDespensa>();
		for (ItemDespensa item : getRepository().findByUsuarioAndArquivado(usuario, false)) {
			if (item.getQuantidadeAtual() <= item.getQuantidadeVermelho()) {
				item.setQuantidadeAtual(item.getQuantidadeVerde() - item.getQuantidadeAtual());
				item.setValor(item.getValor() * (item.getQuantidadeVerde() - item.getQuantidadeAtual()));
				listaDespensas.add(item);
			} else if (item.getValidade() != null && item.getValidade().before(new Date())) { 
				item.setQuantidadeAtual(item.getQuantidadeVerde());
				item.setValor(item.getValor() * item.getQuantidadeVerde());
				listaDespensas.add(item);
			}
		}
		return listaDespensas;
	}
	
	@Override
	public List<ItemDespensa> buscarPorDespensaUsuarioEArquivado(Despensa despensa, Usuario usuario, boolean arquivado) {
		return getRepository().findByDespensaUsuarioAndArquivado(despensa, usuario, arquivado);
	}
	
	@Override
	public List<ItemDespensa> buscarPorUsuarioEArquivado(Usuario usuario, boolean arquivado) {
		return getRepository().findByUsuarioAndArquivado(usuario, arquivado);
	}
	
	public void apagarHistorico(ItemDespensa entity) {
		entity.getMovimentacao().clear();
		entity.setQuantidadeAtual(0);
		entity.setValidade(null);
		entity.setValor(0);
		getRepository().update(entity);
	}
	
	@Override
	public List<ItemDespensa> buscarItensDespesaVencidos() {
		// Retorna todos os itens de despensa do usuário que são perecíveis e não estão arquivados
		List<ItemDespensa> itens = getRepository().findAllPerecivelByUsuario(getUsuarioComponent().getUsuarioLogado());
		
		// Se a opção de estoque estiver ativada, verifica se o item já venceu pela data de validade
		if (getOpcaoSistemaComponent().getControlarEstoqueItemDespensa()) {
			
			List<ItemDespensa> itensVencidos = new ArrayList<>();
			
			for (ItemDespensa item : itens) {
				if (item.getValidade() != null && item.getValidade().before(new Date())) {
					itensVencidos.add(item);
				}
			}
			return itensVencidos;
		}
		
		
		return new ArrayList<>();
	}
}
