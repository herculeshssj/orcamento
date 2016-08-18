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

package br.com.hslife.orcamento.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.enumeration.IncrementoClonagemLancamento;
import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IFechamentoPeriodo;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.repository.FechamentoPeriodoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoPeriodicoRepository;
import br.com.hslife.orcamento.util.LancamentoContaUtil;
import br.com.hslife.orcamento.util.Util;

@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor={BusinessException.class})
public class FechamentoPeriodoService implements IFechamentoPeriodo {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private FechamentoPeriodoRepository repository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private LancamentoPeriodicoRepository lancamentoPeriodicoRepository;
	
	public FechamentoPeriodoRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}

	public LancamentoContaRepository getLancamentoContaRepository() {
		this.lancamentoContaRepository.setSessionFactory(this.sessionFactory);
		return lancamentoContaRepository;
	}

	public LancamentoPeriodicoRepository getLancamentoPeriodicoRepository() {
		this.lancamentoPeriodicoRepository.setSessionFactory(this.sessionFactory);
		return lancamentoPeriodicoRepository;
	}

	public FechamentoPeriodo buscarUltimoFechamentoPeriodoPorConta(Conta conta) throws BusinessException {
		return getRepository().findUltimoFechamentoByConta(conta);
	}

	public List<FechamentoPeriodo> buscarPorContaEOperacaoConta(Conta conta, OperacaoConta operacaoConta) throws BusinessException {
		return getRepository().findByContaAndOperacaoConta(conta, operacaoConta);
	}
	
	public void fecharPeriodo(Date dataFechamento, Conta conta) throws BusinessException {
		this.fecharPeriodo(dataFechamento, conta, null, null);
	}
	
	public void fecharPeriodo(Date dataFechamento, Conta conta, List<LancamentoPeriodico> lancamentosPeriodicos) throws BusinessException {
		this.fecharPeriodo(dataFechamento, conta, null, lancamentosPeriodicos);
	}
	
	public void fecharPeriodo(FechamentoPeriodo fechamentoPeriodo, List<LancamentoPeriodico> lancamentosPeriodicos) throws BusinessException {
		this.fecharPeriodo(fechamentoPeriodo.getData(), fechamentoPeriodo.getConta(), fechamentoPeriodo, lancamentosPeriodicos);
	}
	
	@SuppressWarnings("deprecation")
	public void fecharPeriodo(Date dataFechamento, Conta conta, FechamentoPeriodo fechamentoReaberto, List<LancamentoPeriodico> lancamentosPeriodicos)  throws BusinessException {
		// Obtém-se o último fechamento realizado
		FechamentoPeriodo fechamentoAnterior;
		if (fechamentoReaberto == null)
			fechamentoAnterior = getRepository().findUltimoFechamentoByConta(conta);
		else 
			fechamentoAnterior = getRepository().findFechamentoPeriodoAnterior(fechamentoReaberto);
		
		double saldoFechamentoAnterior = 0;
		
		if (fechamentoAnterior == null) {
			saldoFechamentoAnterior = conta.getSaldoInicial();
		} else {
			saldoFechamentoAnterior = fechamentoAnterior.getSaldo();
		}
		
		// Incrementa a data do fechamento anterior
		Calendar temp = Calendar.getInstance();
		if (fechamentoAnterior != null) {
			temp.setTime(fechamentoAnterior.getData());
			temp.add(Calendar.DAY_OF_YEAR, 1);
		} else
			temp.setTime(conta.getDataAbertura());		
		
		// Calcula o saldo do período
		CriterioBuscaLancamentoConta criterio = new CriterioBuscaLancamentoConta();
		criterio.setConta(conta);
		criterio.setDescricao("");
		criterio.setDataInicio(temp.getTime());
		criterio.setDataFim(dataFechamento);
		criterio.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.REGISTRADO, StatusLancamentoConta.QUITADO});
		double saldoFechamento = LancamentoContaUtil.calcularSaldoLancamentos(getLancamentoContaRepository().findByCriterioBusca(criterio));
		
		// Cria o novo fechamento para a conta
		FechamentoPeriodo novoFechamento = new FechamentoPeriodo();
		if (fechamentoReaberto == null) {
			// Antes de prosseguir, verifica se não existem períodos reabertos
			List<FechamentoPeriodo> fechamentosReabertos = getRepository().findByContaAndOperacaoConta(conta, OperacaoConta.REABERTURA);
			if (fechamentosReabertos != null && !fechamentosReabertos.isEmpty()) {
				// TODO refatorara para uma especificação que valide a possibilidade de fechamento
				throw new BusinessException("Não é possível fechar! Existem períodos anteriores reabertos!");
			}
			
			novoFechamento.setConta(conta);
			novoFechamento.setData(dataFechamento);
			novoFechamento.setOperacao(OperacaoConta.FECHAMENTO);
			novoFechamento.setDataAlteracao(new Date());
			novoFechamento.setSaldo(saldoFechamentoAnterior + saldoFechamento);
			
			// Obtém o mês e ano da data de fechamento
			temp.setTime(dataFechamento);
			novoFechamento.setMes(temp.getTime().getMonth() + 1);
			novoFechamento.setAno(temp.get(Calendar.YEAR));
			
			// Salva o fechamento
			getRepository().save(novoFechamento);
		} else {
			// Antes de prosseguir, verifica se o período selecionado não contém
			// períodos reabertos anteriores
			List<FechamentoPeriodo> fechamentosAnterioresReabertos = getRepository().findFechamentosAnterioresReabertos(fechamentoReaberto);
			if (fechamentosAnterioresReabertos != null && !fechamentosAnterioresReabertos.isEmpty()) {
				// TODO refatorar para uma especificação que valide a possibilidade de fechamento
				throw new BusinessException("Não é possível fechar! Existem períodos anteriores reabertos!");
			}
			
			// Altera os dados do fechamento já existente
			fechamentoReaberto.setOperacao(OperacaoConta.FECHAMENTO);
			fechamentoReaberto.setDataAlteracao(new Date());
			fechamentoReaberto.setSaldo(saldoFechamentoAnterior + saldoFechamento);
			
			// Salva o fechamento
			getRepository().update(fechamentoReaberto);
		}
		
		// Quita os lançamentos do período
		for (LancamentoConta l : getLancamentoContaRepository().findByCriterioBusca(criterio)) {
			l.setStatusLancamentoConta(StatusLancamentoConta.QUITADO);
			if (fechamentoReaberto == null)
				l.setFechamentoPeriodo(novoFechamento);
			else
				l.setFechamentoPeriodo(fechamentoReaberto);

			if (lancamentosPeriodicos != null && lancamentosPeriodicos.contains(l.getLancamentoPeriodico())) {
				this.registrarPagamento(l);
			} else {
				getLancamentoContaRepository().update(l);
			}
		}
	}
	
	public void reabrirPeriodo(FechamentoPeriodo entity) {
		// Busca os fechamentos posteriores ao fechamento selecionado
		List<FechamentoPeriodo> fechamentosPosteriores = getRepository().findFechamentosPosteriores(entity);
		
		// Itera a lista de fechamentos realizando a reabertura dos mesmos e dos lançamentos vinculados
		for (FechamentoPeriodo fechamentoPeriodo : fechamentosPosteriores) {
			FechamentoPeriodo fechamento = getRepository().findById(fechamentoPeriodo.getId());
			fechamento.setOperacao(OperacaoConta.REABERTURA);
			fechamento.setDataAlteracao(new Date());
			getRepository().update(fechamento);
			
			for (LancamentoConta l : fechamento.getLancamentos()) {
				l.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
				getLancamentoContaRepository().update(l);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void registrarPagamento(LancamentoConta pagamentoPeriodo) {		
		pagamentoPeriodo.setStatusLancamentoConta(StatusLancamentoConta.QUITADO);
		
		// Atualiza o pagamento
		getLancamentoContaRepository().update(pagamentoPeriodo);
		
		// Gera o próximo pagamento para os lançamentos fixos
		if (pagamentoPeriodo.getLancamentoPeriodico().getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.FIXO)
				&& pagamentoPeriodo.getLancamentoPeriodico().getStatusLancamento().equals(StatusLancamento.ATIVO)) {
			
			// Busca o pagamento mais recente
			LancamentoConta ultimaMensalidade = getLancamentoContaRepository().findLastGeneratedPagamentoPeriodo(pagamentoPeriodo.getLancamentoPeriodico());
			
			LancamentoConta proximaMensalidade;
			
			if (ultimaMensalidade == null) {
				proximaMensalidade = new LancamentoConta(pagamentoPeriodo);
				proximaMensalidade.setDataVencimento(new Date());
			} else {
				proximaMensalidade = ultimaMensalidade.clonarLancamentos(1, IncrementoClonagemLancamento.NENHUM).get(0);
			}
			
			proximaMensalidade.setLancamentoPeriodico(pagamentoPeriodo.getLancamentoPeriodico());
			
			// Incrementa a data de vencimento independente do valor informado
			proximaMensalidade.setDataVencimento(proximaMensalidade.getLancamentoPeriodico().getPeriodoLancamento().getDataPeriodo(proximaMensalidade.getDataVencimento()));
			// Corrige o dia de vencimento
			proximaMensalidade.getDataVencimento().setDate(proximaMensalidade.getLancamentoPeriodico().getDiaVencimento());
						
			proximaMensalidade.setAno(proximaMensalidade.getDataVencimento().getYear() + 1900);
			proximaMensalidade.setPeriodo(proximaMensalidade.getDataVencimento().getMonth() + 1);
			proximaMensalidade.setDataPagamento(proximaMensalidade.getDataVencimento());
			proximaMensalidade.setValorPago(proximaMensalidade.getLancamentoPeriodico().getValorParcela());
			
			// Define a descrição definitiva do lançamento a ser criado
			proximaMensalidade.setDescricao(proximaMensalidade.getLancamentoPeriodico().getDescricao() + " - Período " + proximaMensalidade.getPeriodo() + " / " + proximaMensalidade.getAno() + ", vencimento para " + Util.formataDataHora(proximaMensalidade.getDataVencimento(), Util.DATA));
			
			getLancamentoContaRepository().save(proximaMensalidade);
			
		} else {
			// Verifica se o lançamento periódico vinculado já pode ser encerrado.
			pagamentoPeriodo.getLancamentoPeriodico().setPagamentos(getLancamentoContaRepository().findByLancamentoPeriodico(pagamentoPeriodo.getLancamentoPeriodico()));
			if (pagamentoPeriodo.getLancamentoPeriodico().podeEncerrar()) {
				pagamentoPeriodo.getLancamentoPeriodico().setStatusLancamento(StatusLancamento.ENCERRADO);
				getLancamentoPeriodicoRepository().update(pagamentoPeriodo.getLancamentoPeriodico());
			}
		}
	}

	@Override
	public FechamentoPeriodo buscarFechamentoPeriodoAnterior(FechamentoPeriodo fechamentoPeriodo) throws BusinessException {
		return getRepository().findFechamentoPeriodoAnterior(fechamentoPeriodo);
	}
	
	@Override
	public FechamentoPeriodo buscarUltimoFechamentoConta(Conta conta) throws BusinessException {
		return getRepository().findUltimoFechamentoByConta(conta);
	}
	
	@Override
	public List<FechamentoPeriodo> buscarTodosFechamentoPorConta(Conta conta) throws BusinessException {
		return getRepository().findAllByConta(conta);
	}
	
	@Override
	public FechamentoPeriodo buscarFechamentoPorID(Long id) throws BusinessException {
		return getRepository().findById(id);
	}
	
	public double saldoUltimoFechamento(Conta conta) throws BusinessException {
		FechamentoPeriodo ultimoFechamento = getRepository().findUltimoFechamentoByConta(conta);
		if (ultimoFechamento == null) {
			return conta.getSaldoInicial();
		} else {
			return ultimoFechamento.getSaldo();
		}
	}
	
	@Override
	public FechamentoPeriodo buscarUltimoFechamentoPeriodoAntesDataPorContaEOperacao(Conta conta, Date data, OperacaoConta operacao) throws BusinessException {
		return getRepository().findLastFechamentoPeriodoBeforeDateByContaAndOperacao(conta, data, operacao);
	}
}