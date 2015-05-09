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

package br.com.hslife.orcamento.facade;

import java.util.List;

import br.com.hslife.orcamento.entity.CartaoCredito;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoCartao;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.service.ICRUDService;

public interface ICartaoCredito extends ICRUDService<CartaoCredito> {
	
	public List<CartaoCredito> buscarPorDescricaoEUsuario(String descricao, Usuario usuario) throws BusinessException;
	
	public List<CartaoCredito> buscarPorUsuario(Usuario usuario) throws BusinessException;
	
	public void desativarCartoes() throws BusinessException;
	
	public void substituirCartao(CartaoCredito entity) throws BusinessException;
	
	public List<CartaoCredito> buscarSomenteCreditoPorUsuario(Usuario usuario) throws BusinessException;
	
	public List<CartaoCredito> buscarAtivosSomenteCreditoPorUsuario(Usuario usuario) throws BusinessException;
	
	public void ativarCartao(CartaoCredito entity) throws BusinessException;
	
	public void desativarCartao(CartaoCredito entity) throws BusinessException;
	
	public List<CartaoCredito> buscarDescricaoOuTipoCartaoOuAtivoPorUsuario(String descricao, TipoCartao tipoCartao, Usuario usuario, Boolean ativo) throws BusinessException;
}