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

import java.util.Date;
import java.util.List;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.model.AgrupamentoLancamento;
import br.com.hslife.orcamento.service.ICRUDService;
import br.com.hslife.orcamento.util.CriterioBuscaLancamentoConta;

public interface ILancamentoConta extends ICRUDService<LancamentoConta> {
	
	public List<LancamentoConta> buscarPorCriterioBusca(CriterioBuscaLancamentoConta criterioBusca) throws BusinessException;
	
	public double calcularSaldoLancamentos(List<LancamentoConta> lancamentos);
	
	public double saldoUltimoFechamento(Conta conta) throws BusinessException;

	public List<Categoria> organizarLancamentosPorCategoria(List<LancamentoConta> lancamentos) throws BusinessException;
	
	public List<Favorecido> organizarLancamentosPorFavorecido(List<LancamentoConta> lancamentos) throws BusinessException;
	
	public List<MeioPagamento> organizarLancamentosPorMeioPagamento(List<LancamentoConta> lancamentos) throws BusinessException;
	
	public List<Moeda> organizarLancamentosPorMoeda(List<LancamentoConta> lancamentos) throws BusinessException;
	
	public List<AgrupamentoLancamento> organizarLancamentosPorDebitoCredito(List<LancamentoConta> lancamentos) throws BusinessException;
	
	public List<LancamentoImportado> buscarLancamentoImportadoPorConta(Conta conta) throws BusinessException;
	
	public boolean existeVinculoFaturaCartao(LancamentoConta lancamento) throws BusinessException;
	
	public List<FechamentoPeriodo> buscarPorContaEOperacaoConta(Conta conta, OperacaoConta operacaoConta) throws BusinessException;
	
	public void fecharPeriodo(Date dataFechamento, Conta conta) throws BusinessException;
	
	public void fecharPeriodo(Date dataFechamento, Conta conta, List<LancamentoPeriodico> lancamentosPeriodicos) throws BusinessException;
	
	public void fecharPeriodo(FechamentoPeriodo fechamentoPeriodo, List<LancamentoPeriodico> lancamentosPeriodicos) throws BusinessException;
	
	public void reabrirPeriodo(FechamentoPeriodo entity) throws BusinessException;
	
	public FechamentoPeriodo buscarFechamentoPeriodoAnterior(FechamentoPeriodo fechamentoPeriodo) throws BusinessException;
	
	public FechamentoPeriodo buscarUltimoFechamentoConta(Conta conta) throws BusinessException;
	
	public List<FechamentoPeriodo> buscarTodosFechamentoPorConta(Conta conta) throws BusinessException;
	
	public FechamentoPeriodo buscarFechamentoPorID(Long id) throws BusinessException;
}
