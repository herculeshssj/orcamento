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

import br.com.hslife.orcamento.entity.CartaoCredito;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.service.ICRUDService;

public interface IConta extends ICRUDService<Conta> {
	
	public List<Conta> buscarTodos() throws BusinessException;
	
	public List<Conta> buscarPorDescricao(String descricao) throws BusinessException;
	
	public List<Conta> buscarTodosAtivos() throws BusinessException;
	
	public List<Conta> buscarTodosAtivosPorUsuario(Usuario usuario) throws BusinessException;
	
	public List<Conta> buscarPorUsuario(Usuario usuario) throws BusinessException;
	
	public List<Conta> buscarAtivosPorUsuario(Usuario usuario) throws BusinessException;
	
	public void ativarConta(Conta conta) throws BusinessException;
	
	public void desativarConta(Conta conta, String situacaoLancamentos) throws BusinessException;
	
	public List<Conta> buscarPorDescricaoEUsuario(String descricao, Usuario usuario) throws BusinessException;
	
	public List<Conta> buscarPorTipoContaEUsuario(TipoConta tipoConta, Usuario usuario) throws BusinessException;
	
	public List<Conta> buscarSomenteTipoCartaoPorUsuario(Usuario usuario) throws BusinessException;
	
	public List<Conta> buscarSomenteTipoCartaoAtivosPorUsuario(Usuario usuario) throws BusinessException;
	
	public Conta buscarPorCartaoCredito(CartaoCredito cartao) throws BusinessException;
	
	public List<Conta> buscarDescricaoOuTipoContaOuAtivoPorUsuario(String descricao, TipoConta tipoConta, Usuario usuario, Boolean ativo) throws BusinessException;
	
}