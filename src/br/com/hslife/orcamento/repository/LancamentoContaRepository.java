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

package br.com.hslife.orcamento.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.model.CriterioLancamentoConta;
import br.com.hslife.orcamento.util.Util;

@Repository
public class LancamentoContaRepository extends AbstractCRUDRepository<LancamentoConta> {
	
	public LancamentoContaRepository() {
		super(new LancamentoConta());
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findByCriterioLancamentoConta(CriterioLancamentoConta criterio) {
		Criteria criteria = getSession().createCriteria(LancamentoConta.class);
		
		if (criterio.getConta() != null && criterio.getConta().getId() != null) {
			criteria.add(Restrictions.eq("conta.id", criterio.getConta().getId()));
		}
		
		if (criterio.getDescricao() != null && !criterio.getDescricao().isEmpty()) {
			criteria.add(Restrictions.ilike("descricao", criterio.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if (criterio.getDataInicio() != null) {
			criteria.add(Restrictions.ge("dataPagamento", criterio.getDataInicio()));			
		}
		
		if (criterio.getDataFim() != null) {
			criteria.add(Restrictions.le("dataPagamento", criterio.getDataFim()));
		}
		
		if (criterio.getAgendado() == 1 || criterio.getAgendado() == -1) {
			criteria.add(Restrictions.eq("agendado", criterio.getAgendadoBoolean()));
		}
		
		if (criterio.getQuitado() == 1 || criterio.getQuitado() == -1) {
			criteria.add(Restrictions.eq("quitado", criterio.getQuitadoBoolean()));
		}
		
		if (criterio.getMoeda() != null) {
			criteria.add(Restrictions.eq("moeda.id", criterio.getMoeda().getId()));
		}
		
		if (criterio.getTipo() != null) {
			criteria.add(Restrictions.eq("tipoLancamento", criterio.getTipo()));
		}
		
		if (criterio.getValor() != null && criterio.getValor().doubleValue() != 0) {
			criteria.add(Restrictions.ge("valorPago", criterio.getValor()));
		}
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(Order.asc("dataPagamento")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findByCriterioLancamentoCartao(CriterioLancamentoConta criterio) {
		Criteria criteria = getSession().createCriteria(LancamentoConta.class, "lancamento")
				.createAlias("lancamento.faturaCartao", "fatura");
		
		if (criterio.getConta() != null && criterio.getConta().getId() != null) {
			criteria.add(Restrictions.eq("lancamento.conta.id", criterio.getConta().getId()));
		}
		
		if (criterio.getDescricao() != null && !criterio.getDescricao().isEmpty()) {
			criteria.add(Restrictions.ilike("lancamento.descricao", criterio.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if (criterio.getDataInicio() != null) {
			criteria.add(Restrictions.ge("fatura.dataVencimento", criterio.getDataInicio()));			
		}
		
		if (criterio.getDataFim() != null) {
			criteria.add(Restrictions.le("fatura.dataVencimento", criterio.getDataFim()));
		}
		
		if (criterio.getAgendado() == 1 || criterio.getAgendado() == -1) {
			criteria.add(Restrictions.eq("lancamento.agendado", criterio.getAgendadoBoolean()));
		}
		
		if (criterio.getQuitado() == 1 || criterio.getQuitado() == -1) {
			criteria.add(Restrictions.eq("lancamento.quitado", criterio.getQuitadoBoolean()));
		}
		
		if (criterio.getMoeda() != null) {
			criteria.add(Restrictions.eq("lancamento.moeda.id", criterio.getMoeda().getId()));
		}
		
		//if (criterio.getParcela() != null && !criterio.getParcela().isEmpty()) {
		//	criteria.add(Restrictions.ilike("lancamento.parcela", criterio.getParcela(), MatchMode.ANYWHERE));
		//}
		
		if (criterio.getTipo() != null) {
			criteria.add(Restrictions.eq("lancamento.tipoLancamento", criterio.getTipo()));
		}
		
		if (criterio.getValor() != null && criterio.getValor().doubleValue() != 0) {
			criteria.add(Restrictions.ge("lancamento.valorPago", criterio.getValor()));
		}
		
		return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(Order.asc("dataPagamento")).list();
	}
	
	public LancamentoConta findLastLancamentoContaByConta(Conta conta) {
		// TODO migrar para HQL
		Criteria criteria = getSession().createCriteria(LancamentoConta.class);
		criteria.add(Restrictions.eq("agendado", false));
		criteria.add(Restrictions.eq("conta.id", conta.getId()));
		return (LancamentoConta)criteria.addOrder(Order.desc("dataPagamento")).setMaxResults(1).uniqueResult();
	}
	
	public LancamentoConta findByHash(String hash) {
		// TODO migrar para HQL
		Criteria criteria = getSession().createCriteria(LancamentoConta.class);
		criteria.add(Restrictions.eq("hashImportacao", hash));
		return (LancamentoConta)criteria.setMaxResults(1).uniqueResult();
	}
	
	public void deleteAllLancamentoContaAfterDateByConta(Date dataPagamento, Conta conta) {
		String sql = "delete from lancamentoconta where idConta = " + conta.getId() + " and dataPagamento > '" + Util.formataDataHora(dataPagamento, Util.DATABASE) + "'";
		Query query = getSession().createSQLQuery(sql);
		query.executeUpdate();
	}
		
	public boolean existsLinkageFaturaCartao(LancamentoConta lancamento) {
		// TODO migrar para HQL
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
		// TODO migrar para HQL
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
		return getQuery("FROM LancamentoConta pagamento WHERE pagamento.lancamentoPeriodico.id = :idLancamento AND pagamento.quitado = true ORDER BY pagamento.dataVencimento DESC")
				.setLong("idLancamento", lancamento.getId())
				.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findByLancamentoPeriodico(LancamentoPeriodico lancamento) {
		return getQuery("FROM LancamentoConta pagamento WHERE pagamento.lancamentoPeriodico.id = :idLancamento ORDER BY pagamento.dataVencimento DESC")
				.setLong("idLancamento", lancamento.getId())
				.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findNotPagosByLancamentoPeriodico(LancamentoPeriodico lancamento) {
		return getQuery("FROM LancamentoConta pagamento WHERE pagamento.lancamentoPeriodico.id = :idLancamento AND pagamento.quitado = false ORDER BY pagamento.dataVencimento DESC")
				.setLong("idLancamento", lancamento.getId())
				.list();
	}
	
	public LancamentoConta findLastGeneratedPagamentoPeriodo(LancamentoPeriodico lancamentoPeriodico) {
		return (LancamentoConta)getQuery("FROM LancamentoConta pagamento WHERE pagamento.lancamentoPeriodico.id = :idLancamento ORDER BY pagamento.dataVencimento DESC")
				.setLong("idLancamento", lancamentoPeriodico.getId())
				.setMaxResults(1)
				.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findAllPagamentosPagosActivedLancamentosByTipoLancamentoAndUsuario(TipoLancamentoPeriodico tipo, Usuario usuario) {
		return getQuery("FROM LancamentoConta pagamento WHERE pagamento.lancamentoPeriodico.tipoLancamentoPeriodico = :tipo AND pagamento.lancamentoPeriodico.statusLancamento = :status AND pagamento.lancamentoPeriodico.usuario.id = :idUsuario AND pagamento.quitado = true ORDER BY pagamento.dataVencimento DESC")
				.setParameter("status", StatusLancamento.ATIVO)
				.setParameter("tipo", tipo)
				.setLong("idUsuario", usuario.getId())
				.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findPagamentosByLancamentoPeriodicoAndPago(LancamentoPeriodico lancamento, Boolean pago) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM LancamentoConta pagamento WHERE ");
		if (pago != null) {
			hql.append("pagamento.quitado = :pago AND ");
		}
		
		hql.append("pagamento.lancamentoPeriodico.id = :idLancamento ORDER BY pagamento.dataVencimento DESC");
		
		Query hqlQuery = getQuery(hql.toString());
		if (pago != null) {
			hqlQuery.setParameter("pago", pago);
		}
		
		hqlQuery.setParameter("idLancamento", lancamento.getId());
		
		return hqlQuery.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoConta> findPagamentosByTipoLancamentoAndUsuarioAndPago(TipoLancamentoPeriodico tipo, Usuario usuario, Boolean pago) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM LancamentoConta pagamento WHERE ");
		if (pago != null) {
			hql.append("pagamento.quitado = :pago AND ");
		}
		if (tipo != null) {
			hql.append("pagamento.lancamentoPeriodico.tipoLancamentoPeriodico = :tipo AND ");
		}
		
		hql.append("pagamento.lancamentoPeriodico.usuario.id = :idUsuario ORDER BY pagamento.dataVencimento DESC");
		
		Query hqlQuery = getQuery(hql.toString());
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
	public List<LancamentoConta> findPagamentosByTipoLancamentoAndContaAndPago(TipoLancamentoPeriodico tipo, Conta conta, Boolean pago) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM LancamentoConta pagamento WHERE ");
		if (pago != null) {
			hql.append("pagamento.quitado = :pago AND ");
		}
		if (tipo != null) {
			hql.append("pagamento.lancamentoPeriodico.tipoLancamentoPeriodico = :tipo AND ");
		}
		
		hql.append("pagamento.lancamentoPeriodico.conta.id = :idConta ORDER BY pagamento.dataVencimento DESC");
		
		Query hqlQuery = getQuery(hql.toString());
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
	public List<LancamentoConta> findPagamentosByTipoLancamentoAndTipoContaAndPago(TipoLancamentoPeriodico tipo, TipoConta tipoConta, Boolean pago) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM LancamentoConta pagamento WHERE ");
		if (pago != null) {
			hql.append("pagamento.quitado = :pago AND ");
		}
		if (tipo != null) {
			hql.append("pagamento.lancamentoPeriodico.tipoLancamentoPeriodico = :tipo AND ");
		}
		
		hql.append("pagamento.lancamentoPeriodico.conta.tipoConta = :tipoConta ORDER BY pagamento.dataVencimento DESC");
		
		Query hqlQuery = getQuery(hql.toString());
		if (pago != null) {
			hqlQuery.setParameter("pago", pago);
		}
		if (tipo != null) {
			hqlQuery.setParameter("tipo", tipo);
		}
		
		hqlQuery.setParameter("tipoConta", tipoConta);
		
		return hqlQuery.list();
	}
}