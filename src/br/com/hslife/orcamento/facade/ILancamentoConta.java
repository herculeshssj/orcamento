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
import java.util.Map;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.model.AgrupamentoLancamento;
import br.com.hslife.orcamento.model.CriterioLancamentoConta;
import br.com.hslife.orcamento.service.ICRUDService;

public interface ILancamentoConta extends ICRUDService<LancamentoConta> {
	
	public List<LancamentoConta> buscarPorCriterioLancamentoConta(CriterioLancamentoConta criterio) throws BusinessException;
	
	public double calcularSaldoLancamentos(List<LancamentoConta> lancamentos);
	
	public double saldoUltimoFechamento(Conta conta) throws BusinessException;

	public List<Categoria> organizarLancamentosPorCategoria(List<LancamentoConta> lancamentos) throws BusinessException;
	
	public List<Favorecido> organizarLancamentosPorFavorecido(List<LancamentoConta> lancamentos) throws BusinessException;
	
	public List<MeioPagamento> organizarLancamentosPorMeioPagamento(List<LancamentoConta> lancamentos) throws BusinessException;
	
	public List<AgrupamentoLancamento> organizarLancamentosPorDebitoCredito(List<LancamentoConta> lancamentos) throws BusinessException;
	
	public void moverLancamentos(List<LancamentoConta> lancamentos, Map<String, Object> parametros) throws BusinessException;
	
	public void excluirLancamentos(List<LancamentoConta> lancamentos, Map<String, Object> parametros) throws BusinessException;
	
	public void copiarLancamentos(List<LancamentoConta> lancamentos, Map<String, Object> parametros) throws BusinessException;
	
	public void duplicarLancamentos(List<LancamentoConta> lancamentos, Map<String, Object> parametros) throws BusinessException;
	
	public void transferirLancamentos(LancamentoConta lancamentoATransferir, Map<String, Object> parametros) throws BusinessException;
	
	public List<LancamentoImportado> buscarLancamentoImportadoPorConta(Conta conta) throws BusinessException;
	
	public void vincularAFaturaAtual(LancamentoConta lancamento) throws BusinessException;
	
	public void vincularAProximaFatura(LancamentoConta lancamento) throws BusinessException;
}
