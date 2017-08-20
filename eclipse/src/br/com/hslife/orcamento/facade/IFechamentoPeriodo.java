/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

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

public interface IFechamentoPeriodo {
	
	public List<FechamentoPeriodo> buscarPorContaEOperacaoConta(Conta conta, OperacaoConta operacaoConta);
	
	public void fecharPeriodo(Date dataFechamento, Conta conta);
	
	public void fecharPeriodo(Date dataFechamento, Conta conta, List<LancamentoPeriodico> lancamentosPeriodicos);
	
	public void fecharPeriodo(FechamentoPeriodo fechamentoPeriodo, List<LancamentoPeriodico> lancamentosPeriodicos);
	
	public void fecharPeriodo(Date dataFechamento, Conta conta, FechamentoPeriodo fechamentoReaberto, List<LancamentoPeriodico> lancamentosPeriodicos) ;
	
	public void reabrirPeriodo(FechamentoPeriodo entity);
	
	public void registrarPagamento(LancamentoConta pagamentoPeriodo);
	
	public FechamentoPeriodo buscarFechamentoPeriodoAnterior(FechamentoPeriodo fechamentoPeriodo);
	
	public FechamentoPeriodo buscarUltimoFechamentoConta(Conta conta);
	
	public List<FechamentoPeriodo> buscarTodosFechamentoPorConta(Conta conta);
	
	public FechamentoPeriodo buscarFechamentoPorID(Long id);
	
	public double saldoUltimoFechamento(Conta conta);
	
	public FechamentoPeriodo buscarUltimoFechamentoPeriodoAntesDataPorContaEOperacao(Conta conta, Date data, OperacaoConta operacao);
	
	public FechamentoPeriodo buscarUltimoFechamentoPeriodoPorConta(Conta conta);
}
