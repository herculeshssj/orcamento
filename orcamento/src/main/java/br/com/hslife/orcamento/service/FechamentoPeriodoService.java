/***

Copyright (c) 2012 - 2021 Hércules S. S. José

	Este arquivo é parte do programa Orçamento Doméstico.


	Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

	modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

	publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

	Licença.


	Este programa é distribuído na esperança que possa ser útil, mas SEM

	NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer

	MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor

	GNU em português para maiores detalhes.


	Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob

	o nome de "LICENSE" junto com este programa, se não, acesse o site do

	projeto no endereco https://github.com/herculeshssj/orcamento ou escreva

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth

	Floor, Boston, MA  02110-1301, USA.


	Para mais informações sobre o programa Orçamento Doméstico e seu autor

	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

	para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.service;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.enumeration.IncrementoClonagemLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.facade.IFechamentoPeriodo;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoPeriodicoRepository;
import br.com.hslife.orcamento.util.Util;

@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor={ApplicationException.class})
public class FechamentoPeriodoService implements IFechamentoPeriodo {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private LancamentoPeriodicoRepository lancamentoPeriodicoRepository;

	public LancamentoContaRepository getLancamentoContaRepository() {
		this.lancamentoContaRepository.setSessionFactory(this.sessionFactory);
		return lancamentoContaRepository;
	}

	public LancamentoPeriodicoRepository getLancamentoPeriodicoRepository() {
		this.lancamentoPeriodicoRepository.setSessionFactory(this.sessionFactory);
		return lancamentoPeriodicoRepository;
	}
	
	@Override
	public void fecharPeriodo(Conta conta, Date dataInicio, Date dataFim) {
		this.fecharPeriodo(conta, dataInicio, dataFim, null);
	}

	@Override
	public void fecharPeriodo(Conta conta, Date dataInicio, Date dataFim, List<LancamentoPeriodico> lancamentosPeriodicos) {
		// Busca os lançamentos da conta para o período selecionado
		CriterioBuscaLancamentoConta criterio = new CriterioBuscaLancamentoConta();
		criterio.setConta(conta);
		criterio.setDataInicio(dataInicio);
		criterio.setDataFim(dataFim);
		criterio.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.REGISTRADO});
		
		// Quita os lançamentos do período
		for (LancamentoConta l : getLancamentoContaRepository().findByCriterioBusca(criterio)) {
			l.setStatusLancamentoConta(StatusLancamentoConta.QUITADO);

			if (lancamentosPeriodicos != null && lancamentosPeriodicos.contains(l.getLancamentoPeriodico())) {
				this.registrarPagamento(l);
			} else {
				getLancamentoContaRepository().update(l);
			}
		}
	}

	@Override
	public void reabrirPeriodo(Conta conta, Date dataInicio, Date dataFim) {
		// Busca os lançamentos da conta para o período informado
		CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
		criterioBusca.setConta(conta);
		criterioBusca.setDataInicio(dataInicio);
		criterioBusca.setDataFim(dataFim);
		criterioBusca.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.QUITADO});
		
		// Itera o resultado da busca, setando cada lançamento quitado para registrado
		for (LancamentoConta l : getLancamentoContaRepository().findByCriterioBusca(criterioBusca)) {
			l.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
			getLancamentoContaRepository().update(l);
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
			
			// Atualiza a taxa de conversão
			if (proximaMensalidade.getTaxaConversao() != null) {
				proximaMensalidade.getTaxaConversao().atualizaTaxaConversao(proximaMensalidade.getValorPago());
			}
			
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
}
