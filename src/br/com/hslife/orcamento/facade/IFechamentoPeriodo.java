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

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.exception.ApplicationException;

public interface IFechamentoPeriodo {
	
	public List<FechamentoPeriodo> buscarPorContaEOperacaoConta(Conta conta, OperacaoConta operacaoConta) throws ApplicationException;
	
	public void fecharPeriodo(Date dataFechamento, Conta conta) throws ApplicationException;
	
	public void fecharPeriodo(Date dataFechamento, Conta conta, List<LancamentoPeriodico> lancamentosPeriodicos) throws ApplicationException;
	
	public void fecharPeriodo(FechamentoPeriodo fechamentoPeriodo, List<LancamentoPeriodico> lancamentosPeriodicos) throws ApplicationException;
	
	public void fecharPeriodo(Date dataFechamento, Conta conta, FechamentoPeriodo fechamentoReaberto, List<LancamentoPeriodico> lancamentosPeriodicos)  throws ApplicationException;
	
	public void reabrirPeriodo(FechamentoPeriodo entity) throws ApplicationException;
	
	public void registrarPagamento(LancamentoConta pagamentoPeriodo) throws ApplicationException;
	
	public FechamentoPeriodo buscarFechamentoPeriodoAnterior(FechamentoPeriodo fechamentoPeriodo) throws ApplicationException;
	
	public FechamentoPeriodo buscarUltimoFechamentoConta(Conta conta) throws ApplicationException;
	
	public List<FechamentoPeriodo> buscarTodosFechamentoPorConta(Conta conta) throws ApplicationException;
	
	public FechamentoPeriodo buscarFechamentoPorID(Long id) throws ApplicationException;
	
	public double saldoUltimoFechamento(Conta conta) throws ApplicationException;
	
	public FechamentoPeriodo buscarUltimoFechamentoPeriodoAntesDataPorContaEOperacao(Conta conta, Date data, OperacaoConta operacao) throws ApplicationException;
	
	public FechamentoPeriodo buscarUltimoFechamentoPeriodoPorConta(Conta conta) throws ApplicationException;
}
