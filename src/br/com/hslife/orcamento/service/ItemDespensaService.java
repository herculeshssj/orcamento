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
	public void validar(ItemDespensa entity) throws BusinessException {
		
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
				
				listaDespensas.add(new ItemDespensa(despensa.getDescricao(), 
						despensa.getCaracteristicas(),
						despensa.getDespensa(),
						despensa.getUnidadeMedida(), 
						despensa.getQuantidadeVerde() - despensa.getQuantidadeAtual(),
						despensa.getValor() * (despensa.getQuantidadeVerde() - despensa.getQuantidadeAtual())
				));
				
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
				
				listaDespensas.add(new ItemDespensa(despensa.getDescricao(), 
						despensa.getCaracteristicas(),
						despensa.getDespensa(),
						despensa.getUnidadeMedida(), 
						despensa.getQuantidadeVerde(),
						despensa.getValor() * despensa.getQuantidadeVerde()
				));
				
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
	
/*
	@Autowired
	private ItemDespensaRepository repository;
	
	@Override
	public void cadastrar(ItemDespensa entity) throws BusinessException {
		getRepository().save(entity);		
	}

	@Override
	public void alterar(ItemDespensa entity) throws BusinessException {
		getRepository().update(entity);
		
	}

	@Override
	public void excluir(ItemDespensa entity) throws BusinessException {
		getRepository().delete(entity);
		
	}

	@Override
	public ItemDespensa buscar(Long id) throws BusinessException {
		return (ItemDespensa) getRepository().findById(id);
	}

	@Override
	public List<ItemDespensa> buscarTodos() throws BusinessException {
		return getRepository().findAll();
	}
	
	@Override
	public List<ItemDespensa> buscarTodosAtivosPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findAllEnabledByUsuario(usuario);
	}

	@Override
	public List<ItemDespensa> buscarPorDescricaoEUsuario(String descricao,	Usuario usuario) throws BusinessException {
		return getRepository().findByDescricaoAndUsuario(descricao, usuario);
	}
	
	@Override
	public void arquivar(ItemDespensa entity) throws BusinessException {
		if (!entity.isArquivado()) {
			entity.setArquivado(true);
			getRepository().update(entity);
		}		
	}
	
	@Override
	public void desarquivar(ItemDespensa entity) throws BusinessException {
		if (entity.isArquivado()) {
			entity.setArquivado(false);
			getRepository().update(entity);
		}
	}
	
	@Override
	public void registrarCompraConsumo(ItemDespensa entity, HistoricoDespensa historicoDespensa) throws BusinessException {
		if (historicoDespensa.getOperacaoDespensa().equals(OperacaoDespensa.CONSUMO) && (entity.getQuantidadeAtual() - historicoDespensa.getQuantidade()) < 0) {
			throw new BusinessException("Quantidade informada ultrapassa estoque disponível!");
		}
		entity.getHistorico().add(historicoDespensa);
		entity.setQuantidadeAtual(0);
		for (HistoricoDespensa hd : entity.getHistorico()) {
			if (hd.getOperacaoDespensa().equals(OperacaoDespensa.COMPRA)) {
				entity.setQuantidadeAtual(entity.getQuantidadeAtual() + hd.getQuantidade());
			} else {
				entity.setQuantidadeAtual(entity.getQuantidadeAtual() - hd.getQuantidade());
			}
		}
		getRepository().update(entity);
	}
	
	@Override
	public List<ItemDespensa> gerarListaCompras(Usuario usuario) throws BusinessException {
		List<ItemDespensa> listaDespensas = new ArrayList<ItemDespensa>();
		for (ItemDespensa despensa : this.buscarPorDescricaoEUsuario("", usuario)) {
			if (despensa.getQuantidadeAtual() <= despensa.getQuantidadeVermelho() && !despensa.isArquivado()) {
				listaDespensas.add(new ItemDespensa(despensa.getDescricao(), 
						despensa.getCaracteristicas(), 
						despensa.getUnidadeMedida(), 
						despensa.getQuantidadeVerde() - despensa.getQuantidadeAtual()));
			}
		}
		return listaDespensas;
	}
	
	@Override
	public List<CompraConsumoOperacaoDespensa> buscarCompraConsumoOperacaoDespensaPorDataInicioFim(OperacaoDespensa operacao, Date dataInicio, Date dataFim) throws BusinessException {
		return getRepository().findCompraConsumoOperacaoDespensaByDataInicioFim(operacao, dataInicio, dataFim);
	}
	
	@Override
	public List<CompraConsumoItemDespensa> buscarCompraConsumoItemDespensaPorDataInicioFim(ItemDespensa item, Date dataInicio, Date dataFim) throws BusinessException {
		return getRepository().findCompraConsumoItemDespensaByDataInicioFim(item, dataInicio, dataFim);
	}

	public ItemDespensaRepository getRepository() {
		return repository;
	}

	public void setRepository(ItemDespensaRepository repository) {
		this.repository = repository;
	}
	*/
}
