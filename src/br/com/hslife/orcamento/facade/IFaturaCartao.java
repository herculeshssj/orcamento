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

package br.com.hslife.orcamento.facade;

import java.util.Date;
import java.util.List;

import br.com.hslife.orcamento.entity.CartaoCredito;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FaturaCartao;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusFaturaCartao;
import br.com.hslife.orcamento.exception.BusinessException;

public interface IFaturaCartao extends ICRUDService<FaturaCartao>{ 	
	
	public List<FaturaCartao> buscarTodosPorCartaoCredito(Conta conta) throws BusinessException;
	
	public List<FaturaCartao> buscarTodos() throws BusinessException;
	
	public void fecharFatura(FaturaCartao fatura, List<Moeda> conversoes) throws BusinessException;
	
	public void reabrirFatura(FaturaCartao faturaCartao) throws BusinessException;
	
	public double saldoDevedorUltimaFatura(CartaoCredito cartao) throws BusinessException;
	
	public void quitarFaturaDebitoConta(FaturaCartao faturaCartao, Conta conta, double valorAQuitar, Date dataPagamento) throws BusinessException;
	
	public void quitarFaturaParcelamento(FaturaCartao faturaCartao, int quantParcelas, Date dataParcelamento) throws BusinessException;
	
	public void quitarFaturaLancamentoSelecionado(FaturaCartao faturaCartao, LancamentoConta lancamentoConta) throws BusinessException;
	
	public List<FaturaCartao> buscarTodosPorUsuario(Usuario usuario) throws BusinessException;
	
	public List<FaturaCartao> buscarPorCartaoCreditoEStatusFatura(CartaoCredito cartao, StatusFaturaCartao statusFatura) throws BusinessException;
	
	public List<FaturaCartao> buscarTodosPorContaOrdenadoPorMesEAno(Conta conta) throws BusinessException;
}
