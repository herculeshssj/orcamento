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

package br.com.hslife.orcamento.facade;

import java.util.List;

import br.com.hslife.orcamento.entity.Despensa;
import br.com.hslife.orcamento.entity.ItemDespensa;
import br.com.hslife.orcamento.entity.MovimentoItemDespensa;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.service.ICRUDService;

public interface IItemDespensa extends ICRUDService<ItemDespensa> {
	
	public List<ItemDespensa> buscarPorDespensaUsuarioEArquivado(Despensa despensa, Usuario usuario, boolean arquivado) throws BusinessException;
	
	public List<ItemDespensa> buscarPorUsuarioEArquivado(Usuario usuario, boolean arquivado) throws BusinessException;
	
	public void registrarCompraConsumo(ItemDespensa entity, MovimentoItemDespensa movimentoItemDespensa) throws BusinessException;
	
	public void arquivarItemDespensa(ItemDespensa entity) throws BusinessException;
	
	public void desarquivarItemDespensa(ItemDespensa entity) throws BusinessException;
	
	public List<ItemDespensa> gerarListaCompras(Usuario usuario) throws BusinessException;
	
	/*

public void cadastrar(ItemDespensa entity) throws BusinessException;
	
	public void alterar(ItemDespensa entity) throws BusinessException;
	
	public void excluir(ItemDespensa entity) throws BusinessException;
	
	public ItemDespensa buscar(Long id) throws BusinessException;
	
	public List<ItemDespensa> buscarTodos() throws BusinessException;
	
	public List<ItemDespensa> buscarTodosAtivosPorUsuario(Usuario usuario) throws BusinessException;
	
	public List<ItemDespensa> buscarPorDescricaoEUsuario(String descricao, Usuario usuario) throws BusinessException;
	
	public void arquivar(ItemDespensa entity) throws BusinessException;
	
	public void desarquivar(ItemDespensa entity) throws BusinessException;
	
	public void registrarCompraConsumo(ItemDespensa entity, HistoricoDespensa historicoDespensa) throws BusinessException;
	
	public List<ItemDespensa> gerarListaCompras(Usuario usuario) throws BusinessException;
	
	public List<CompraConsumoOperacaoDespensa> buscarCompraConsumoOperacaoDespensaPorDataInicioFim (OperacaoDespensa operacao, Date dataInicio, Date dataFim) throws BusinessException;
	
	public List<CompraConsumoItemDespensa> buscarCompraConsumoItemDespensaPorDataInicioFim(ItemDespensa item, Date dataInicio, Date dataFim) throws BusinessException;
*/	
}
