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

package br.com.hslife.orcamento.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	public ItemDespensaRepository getRepository() {
		return repository;
	}

	public void setRepository(ItemDespensaRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public void registrarCompraConsumo(ItemDespensa entity, MovimentoItemDespensa movimentoItemDespensa) throws BusinessException {
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
	public void desfazerRegistroCompraConsumo(ItemDespensa entity) throws BusinessException {
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
	public void arquivarItemDespensa(ItemDespensa entity) throws BusinessException {
		if (!entity.isArquivado()) {
			entity.setArquivado(true);
			getRepository().update(entity);
		}		
	}
	
	@Override
	public void desarquivarItemDespensa(ItemDespensa entity) throws BusinessException {
		if (entity.isArquivado()) {
			entity.setArquivado(false);
			getRepository().update(entity);
		}
	}
	
	@Override
	public List<ItemDespensa> gerarListaCompras(Usuario usuario) throws BusinessException {
		List<ItemDespensa> listaDespensas = new ArrayList<ItemDespensa>();
		for (ItemDespensa despensa : getRepository().findByUsuarioAndArquivado(usuario, false)) {
			if (despensa.getQuantidadeAtual() <= despensa.getQuantidadeVermelho()) {
				/* Usando o Builder de ItemDespensa *
				listaDespensas.add(new ItemDespensa.Builder()
					.descricao(despensa.getDescricao())
					.caracteristicas(despensa.getCaracteristicas())
					.despensa(despensa.getDespensa())
					.unidadeMedida(despensa.getUnidadeMedida())
					.quantidade(despensa.getQuantidadeVerde() - despensa.getQuantidadeAtual())
					.valor(despensa.getValor() * (despensa.getQuantidadeVerde() - despensa.getQuantidadeAtual()))
					.build()
				);	*/			
				
				ItemDespensa item = new ItemDespensa();
				item.setDescricao(despensa.getDescricao());
				item.setCaracteristicas(despensa.getCaracteristicas());
				item.setDespensa(despensa.getDespensa());
				item.setUnidadeMedida(despensa.getUnidadeMedida());
				item.setQuantidadeAtual(despensa.getQuantidadeVerde() - despensa.getQuantidadeAtual());
				item.setValor(despensa.getValor() * (despensa.getQuantidadeVerde() - despensa.getQuantidadeAtual()));
				
				listaDespensas.add(item);
				
			} else if (despensa.getValidade() != null && despensa.getValidade().before(new Date())) { /*
				listaDespensas.add(new ItemDespensa.Builder()
					.descricao(despensa.getDescricao())
					.caracteristicas(despensa.getCaracteristicas())
					.despensa(despensa.getDespensa())
					.unidadeMedida(despensa.getUnidadeMedida())
					.quantidade(despensa.getQuantidadeVerde())
					.valor(despensa.getValor() * despensa.getQuantidadeVerde())
					.build()
				);*/
				
				ItemDespensa item = new ItemDespensa();
				item.setDescricao(despensa.getDescricao());
				item.setCaracteristicas(despensa.getCaracteristicas());
				item.setDespensa(despensa.getDespensa());
				item.setUnidadeMedida(despensa.getUnidadeMedida());
				item.setQuantidadeAtual(despensa.getQuantidadeVerde());
				item.setValor(despensa.getValor() * despensa.getQuantidadeVerde());
				
				listaDespensas.add(item);
				
			}
		}
		return listaDespensas;
	}
	
	@Override
	public List<ItemDespensa> buscarPorDespensaUsuarioEArquivado(Despensa despensa, Usuario usuario, boolean arquivado) throws BusinessException {
		return getRepository().findByDespensaUsuarioAndArquivado(despensa, usuario, arquivado);
	}
	
	@Override
	public List<ItemDespensa> buscarPorUsuarioEArquivado(Usuario usuario, boolean arquivado) throws BusinessException {
		return getRepository().findByUsuarioAndArquivado(usuario, arquivado);
	}
	
	public void apagarHistorico(ItemDespensa entity) throws BusinessException {
		entity.getMovimentacao().clear();
		getRepository().update(entity);
	}
}
