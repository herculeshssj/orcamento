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

para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor,

Boston, MA  02110-1301, USA.


Para mais informações sobre o programa Orçamento Doméstico e seu autor

entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.CadastroSistema;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.model.LancamentoPanoramaCadastro;

@Repository
public class LancamentoContaRepository extends AbstractCRUDRepository<LancamentoConta> {
	
	public LancamentoContaRepository() {
		super(new LancamentoConta());
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoPanoramaCadastro> findLancamentoForPanoramaCadastro(Conta conta, CadastroSistema cadastro, Long idAgrupamento) {
		String agrupamento = null;
		switch (cadastro) {
		case CATEGORIA:
			agrupamento = "lancamento.categoria.id = :idAgrupamento";
			break;
		case FAVORECIDO:
			agrupamento = "lancamento.favorecido.id = :idAgrupamento";
			break;
		case MEIOPAGAMENTO:
			agrupamento = "lancamento.meioPagamento.id = :idAgrupamento";
			break;
		case MOEDA:
			agrupamento = "lancamento.moeda.id = :idAgrupamento";
			break;
		default:
			agrupamento = "1 = 1";
			break;
		}
		
		return getQueryNoType("SELECT lancamento.tipoLancamento as tipoLancamento, lancamento.dataPagamento as dataPagamento, lancamento.valorPago as valorPago, "
				+ "taxa.valorMoedaDestino as valorMoedaDestino FROM LancamentoConta lancamento LEFT JOIN lancamento.taxaConversao taxa WHERE "
				+ "lancamento.conta.id = :idConta AND " + agrupamento)
				.setLong("idConta", conta.getId())
				.setLong("idAgrupamento", idAgrupamento)
				.setResultTransformer(new AliasToBeanResultTransformer(LancamentoPanoramaCadastro.class))
				.list();		
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<LancamentoConta> findByCriterioBusca(CriterioBuscaLancamentoConta criterioBusca) {
		Criteria criteria = getSession().createCriteria(LancamentoConta.class, "lancamento")
				.createAlias("lancamento.conta", "con");
		
		for (Criterion criterion : criterioBusca.buildCriteria()) {
			criteria.add(criterion);
			
		}
		
		if (criterioBusca.getLimiteResultado() == 0)
			return criteria.addOrder(Order.asc("lancamento.dataPagamento")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		else
			return criteria.setMaxResults(criterioBusca.getLimiteResultado()).addOrder(Order.asc("lancamento.dataPagamento")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	public LancamentoConta findLastLancamentoContaByConta(Conta conta) {
		Criteria criteria = getSession().createCriteria(LancamentoConta.class);
		criteria.add(Restrictions.ne("statusLancamentoConta", StatusLancamentoConta.QUITADO));
		criteria.add(Restrictions.eq("conta.id", conta.getId()));
		return (LancamentoConta)criteria.addOrder(Order.desc("dataPagamento")).setMaxResults(1).uniqueResult();
	}
	
	public LancamentoConta findByHash(String hash) {
		return (LancamentoConta)getQueryNoType("FROM LancamentoConta lancamento WHERE lancamento.hashImportacao = :hash").setString("hash", hash).setMaxResults(1).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public void deleteAllLancamentoContaAfterDateByConta(Date dataPagamento, Conta conta) {
		Query query = getQueryNoType("FROM LancamentoConta lancamento WHERE lancamento.conta.id = :idConta AND lancamento.dataPagamento > :data")
				.setLong("idConta", conta.getId())
				.setDate("data", dataPagamento);
		
		// Itera o resultado apagando cada lançamento encontrado
		for (LancamentoConta l : (List<LancamentoConta>)query.list()) {
			this.delete(l);
		}
	}
		
	public boolean existsLinkageFaturaCartao(LancamentoConta lancamento) {
		boolean result = true;
		
		String sqlLancamento = "select count(*) from detalhefatura where idLancamento = " + lancamento.getId();
		
		Query queryLancamento = getSession().createSQLQuery(sqlLancamento);
		
		BigInteger queryResultLancamento = (BigInteger)queryLancamento.uniqueResult();
		
		if (queryResultLancamento.longValue() == 0) {
			return false;
		}
		
		return result;
	}
	
	public boolean existsLinkagePagamentoFaturaCartao(LancamentoConta lancamento) {
		boolean result = true;
		
		String sqlLancamento = "select count(*) from faturacartao where idLancamento = " + lancamento.getId();
		
		Query queryLancamento = getSession().createSQLQuery(sqlLancamento);
		
		BigInteger queryResultLancamento = (BigInteger)queryLancamento.uniqueResult();
		
		if (queryResultLancamento.longValue() == 0) {
			return false;
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findPagosByLancamentoPeriodico(LancamentoPeriodico lancamento) {
		return getQueryNoType("FROM LancamentoConta pagamento WHERE pagamento.lancamentoPeriodico.id = :idLancamento AND pagamento.statusLancamentoConta = 'QUITADO' ORDER BY pagamento.dataVencimento DESC")
				.setLong("idLancamento", lancamento.getId())
				.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findByLancamentoPeriodico(LancamentoPeriodico lancamento) {
		return getQueryNoType("FROM LancamentoConta pagamento WHERE pagamento.lancamentoPeriodico.id = :idLancamento ORDER BY pagamento.dataVencimento DESC")
				.setLong("idLancamento", lancamento.getId())
				.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findNotPagosByLancamentoPeriodico(LancamentoPeriodico lancamento) {
		return getQueryNoType("FROM LancamentoConta pagamento WHERE pagamento.lancamentoPeriodico.id = :idLancamento AND pagamento.statusLancamentoConta <> 'QUITADO' ORDER BY pagamento.dataVencimento DESC")
				.setLong("idLancamento", lancamento.getId())
				.list();
	}
	
	public LancamentoConta findLastGeneratedPagamentoPeriodo(LancamentoPeriodico lancamentoPeriodico) {
		return (LancamentoConta)getQueryNoType("FROM LancamentoConta pagamento WHERE pagamento.lancamentoPeriodico.id = :idLancamento ORDER BY pagamento.dataVencimento DESC")
				.setLong("idLancamento", lancamentoPeriodico.getId())
				.setMaxResults(1)
				.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findAllPagamentosPagosActivedLancamentosByTipoLancamentoAndUsuario(TipoLancamentoPeriodico tipo, Usuario usuario) {
		return getQueryNoType("FROM LancamentoConta pagamento WHERE pagamento.lancamentoPeriodico.tipoLancamentoPeriodico = :tipo AND pagamento.lancamentoPeriodico.statusLancamento = :status AND pagamento.lancamentoPeriodico.usuario.id = :idUsuario AND pagamento.statusLancamentoConta = 'QUITADO' ORDER BY pagamento.dataVencimento DESC")
				.setParameter("status", StatusLancamento.ATIVO)
				.setParameter("tipo", tipo)
				.setLong("idUsuario", usuario.getId())
				.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findPagamentosByLancamentoPeriodicoAndPago(LancamentoPeriodico lancamento, StatusLancamentoConta pago) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM LancamentoConta pagamento LEFT JOIN FETCH pagamento.faturaCartao WHERE ");
		if (pago != null) {
			hql.append("pagamento.statusLancamentoConta = :pago AND ");
		}
		
		hql.append("pagamento.lancamentoPeriodico.id = :idLancamento ORDER BY pagamento.dataVencimento DESC");
		
		Query hqlQuery = getQueryNoType(hql.toString());
		if (pago != null) {
			hqlQuery.setParameter("pago", pago);
		}
		
		hqlQuery.setParameter("idLancamento", lancamento.getId());
		
		return hqlQuery.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findPagamentosByTipoLancamentoAndUsuarioAndPago(TipoLancamentoPeriodico tipo, Usuario usuario, StatusLancamentoConta pago) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM LancamentoConta pagamento LEFT JOIN FETCH pagamento.faturaCartao WHERE ");
		if (pago != null) {
			hql.append("pagamento.statusLancamentoConta = :pago AND ");
		}
		if (tipo != null) {
			hql.append("pagamento.lancamentoPeriodico.tipoLancamentoPeriodico = :tipo AND ");
		}
		
		hql.append("pagamento.lancamentoPeriodico.usuario.id = :idUsuario ORDER BY pagamento.dataVencimento DESC");
		
		Query hqlQuery = getQueryNoType(hql.toString());
		if (pago != null) {
			hqlQuery.setParameter("pago", pago);
		}
		if (tipo != null) {
			hqlQuery.setParameter("tipo", tipo);
		}
		
		hqlQuery.setParameter("idUsuario", usuario.getId());
		
		return hqlQuery.list();
	}

	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findPagamentosByTipoLancamentoAndContaAndPago(TipoLancamentoPeriodico tipo, Conta conta, StatusLancamentoConta pago) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM LancamentoConta pagamento LEFT JOIN FETCH pagamento.faturaCartao WHERE ");
		if (pago != null) {
			hql.append("pagamento.statusLancamentoConta = :pago AND ");
		}
		if (tipo != null) {
			hql.append("pagamento.lancamentoPeriodico.tipoLancamentoPeriodico = :tipo AND ");
		}
		
		hql.append("pagamento.lancamentoPeriodico.conta.id = :idConta ORDER BY pagamento.dataVencimento DESC");
		
		Query hqlQuery = getQueryNoType(hql.toString());
		if (pago != null) {
			hqlQuery.setParameter("pago", pago);
		}
		if (tipo != null) {
			hqlQuery.setParameter("tipo", tipo);
		}
		
		hqlQuery.setParameter("idConta", conta.getId());
		
		return hqlQuery.list();
	}

	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findPagamentosByTipoLancamentoAndTipoContaAndPago(TipoLancamentoPeriodico tipo, TipoConta tipoConta, StatusLancamentoConta pago) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM LancamentoConta pagamento LEFT JOIN FETCH pagamento.faturaCartao WHERE ");
		if (pago != null) {
			hql.append("pagamento.statusLancamentoConta = :pago AND ");
		}
		if (tipo != null) {
			hql.append("pagamento.lancamentoPeriodico.tipoLancamentoPeriodico = :tipo AND ");
		}
		
		hql.append("pagamento.lancamentoPeriodico.conta.tipoConta = :tipoConta ORDER BY pagamento.dataVencimento DESC");
		
		Query hqlQuery = getQueryNoType(hql.toString());
		if (pago != null) {
			hqlQuery.setParameter("pago", pago);
		}
		if (tipo != null) {
			hqlQuery.setParameter("tipo", tipo);
		}
		
		hqlQuery.setParameter("tipoConta", tipoConta);
		
		return hqlQuery.list();
	}
	
	public LancamentoConta findLancamentoByParcelaAndLancamentoPeriodico(int parcela, LancamentoPeriodico lancamentoPeriodico) {
		return (LancamentoConta)getQueryNoType("FROM LancamentoConta lancamento WHERE lancamento.parcela = :parcela AND lancamento.lancamentoPeriodico.id = :idLancamento")
				.setInteger("parcela", parcela)
				.setLong("idLancamento", lancamentoPeriodico.getId())
				.uniqueResult();
	}
	
	public LancamentoConta findLancamentoByPeriodoAndAnoAndLancamentoPeriodico(int periodo, int ano, LancamentoPeriodico lancamentoPeriodico) {
		return (LancamentoConta)getQueryNoType("FROM LancamentoConta lancamento WHERE lancamento.periodo = :periodo AND lancamento.ano = :ano AND lancamento.lancamentoPeriodico.id = :idLancamento")
				.setInteger("periodo", periodo)
				.setInteger("ano", ano)
				.setLong("idLancamento", lancamentoPeriodico.getId())
				.setMaxResults(1)
				.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findAllWithoutFatura(Conta conta) {
		return getQueryNoType("FROM LancamentoConta lancamento WHERE lancamento.conta.id = :idConta AND lancamento.faturaCartao IS NULL")
				.setLong("idConta", conta.getId())
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findByDescricaoAndUsuario(String descricao, Usuario usuarioLogado) {
		return getQueryNoType("FROM LancamentoConta lancamento WHERE lancamento.descricao LIKE '%" + descricao + "%' AND lancamento.conta.usuario.id = :idUsuario")
				.setLong("idUsuario", usuarioLogado.getId())
				.list();
	}
	
	/**
	 * Obtém o saldo do período informado para a conta. Pode-se definir quais
	 * status de lançamentos podem ser considerados no fechamento.
	 * 
	 * Este método substituiu a entidade FechamentoPeriodo que fazia o papel
	 * de guardar informações de períodos anteriores.
	 * 
	 * @param conta
	 * @param dataInicio
	 * @param dataFim
	 * @param statusLancamento
	 * @return o saldo do período
	 */
	public BigDecimal getSaldoPeriodoByContaAndPeriodoAndStatusLancamento(Conta conta, Date dataInicio, Date dataFim, StatusLancamentoConta[] statusLancamento) {
		// Obtém a soma dos créditos
		StringBuilder hqlCreditos = new StringBuilder();
		hqlCreditos.append("SELECT SUM(lancamento.valorPago) FROM LancamentoConta lancamento WHERE lancamento.conta.id = :idConta AND lancamento.tipoLancamento = :tipo");
		
		if (dataInicio != null) {
			hqlCreditos.append(" AND lancamento.dataPagamento >= :dataInicio");
		}
		
		if (dataFim != null) {
			hqlCreditos.append(" AND lancamento.dataPagamento <= :dataFim");
		}
		
		if (statusLancamento != null && statusLancamento.length != 0) {
			hqlCreditos.append(" AND lancamento.statusLancamentoConta IN (:status)");
		}
		
		Query hqlQueryCredito = getQueryNoType(hqlCreditos.toString());
		hqlQueryCredito.setLong("idConta", conta.getId());
		hqlQueryCredito.setParameter("tipo", TipoLancamento.RECEITA);
		
		if (dataInicio != null) {
			hqlQueryCredito.setDate("dataInicio", dataInicio);
		}
		
		if (dataFim != null) {
			hqlQueryCredito.setDate("dataFim", dataFim);
		}
		
		if (statusLancamento != null && statusLancamento.length != 0) {
			hqlQueryCredito.setParameterList("status",statusLancamento);
		}
		
		// Executa a query e armazena o valor total de créditos
		Double totalCreditos = (Double)hqlQueryCredito.uniqueResult();
		
		// Obtém a soma dos débitos
		StringBuilder hqlDebitos = new StringBuilder();
		hqlDebitos.append("SELECT SUM(lancamento.valorPago) FROM LancamentoConta lancamento WHERE lancamento.conta.id = :idConta AND lancamento.tipoLancamento = :tipo");
		
		if (dataInicio != null) {
			hqlDebitos.append(" AND lancamento.dataPagamento >= :dataInicio");
		}
		
		if (dataFim != null) {
			hqlDebitos.append(" AND lancamento.dataPagamento <= :dataFim");
		}
		
		if (statusLancamento != null && statusLancamento.length != 0) {
			hqlDebitos.append(" AND lancamento.statusLancamentoConta IN (:status)");
		}
		
		Query hqlQueryDebito = getQueryNoType(hqlDebitos.toString());
		hqlQueryDebito.setLong("idConta", conta.getId());
		hqlQueryDebito.setParameter("tipo", TipoLancamento.DESPESA);
		
		if (dataInicio != null) {
			hqlQueryDebito.setDate("dataInicio", dataInicio);
		}
		
		if (dataFim != null) {
			hqlQueryDebito.setDate("dataFim", dataFim);
		}
		
		if (statusLancamento != null && statusLancamento.length != 0) {
			hqlQueryDebito.setParameterList("status",statusLancamento);
		}
		
		// Executa a query e armazena o valor total de créditos
		Double totalDebitos = (Double)hqlQueryDebito.uniqueResult();
		
		// Verifica se os valores são nulos
		if (totalCreditos == null)
			totalCreditos = 0.0;
		if (totalDebitos == null)
			totalDebitos = 0.0;
		
		// Retorna a subtração do total de créditos pelo total de débitos
		return new BigDecimal(conta.getSaldoInicial() + totalCreditos - totalDebitos);
	}
}
