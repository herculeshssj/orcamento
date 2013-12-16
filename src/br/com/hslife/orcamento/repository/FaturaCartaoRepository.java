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
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FaturaCartao;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusFaturaCartao;

@Repository
public class FaturaCartaoRepository extends AbstractCRUDRepository<FaturaCartao> {
	
	public FaturaCartaoRepository() {
		super(new FaturaCartao());
	}
	
	@SuppressWarnings("unchecked")
	public List<FaturaCartao> findAllByCartaoCredito(Conta conta) {
		Criteria criteria = getSession().createCriteria(FaturaCartao.class);
		criteria.add(Restrictions.eq("conta.id", conta.getId()));
		return criteria.addOrder(Order.asc("dataVencimento")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<FaturaCartao> findAll() {
		Criteria criteria = getSession().createCriteria(FaturaCartao.class);
		return criteria.addOrder(Order.asc("dataVencimento")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	public boolean existsFaturaCartao(Conta conta) {
		boolean result = true;
		
		String sqlLancamento = "select count(id) from faturacartao where idConta = " + conta.getId();
		
		Query queryLancamento = getSession().createSQLQuery(sqlLancamento);
		
		BigInteger queryResultLancamento = (BigInteger)queryLancamento.uniqueResult();
		
		if (queryResultLancamento.longValue() == 0) {
			return false;
		}
		
		return result;
	}
	
	public FaturaCartao lastFaturaCartaoFechada(Conta conta) {
		StatusFaturaCartao status[] = {StatusFaturaCartao.ABERTA, StatusFaturaCartao.FUTURA};
		Criteria criteria = getSession().createCriteria(FaturaCartao.class);
		criteria.add(Restrictions.eq("conta.id", conta.getId()));
		criteria.add(Restrictions.not(Restrictions.in("statusFaturaCartao", status)));
		return (FaturaCartao)criteria.addOrder(Order.desc("dataVencimento")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).setMaxResults(1).uniqueResult();
	}
	
	public FaturaCartao findFaturaCartaoAberta(Conta conta) {		
		Criteria criteria = getSession().createCriteria(FaturaCartao.class);
		criteria.add(Restrictions.eq("conta.id", conta.getId()));
		criteria.add(Restrictions.eq("statusFaturaCartao", StatusFaturaCartao.ABERTA));
		return (FaturaCartao)criteria.addOrder(Order.desc("dataVencimento")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
	}
	
	public FaturaCartao findNextFaturaCartaoFutura(Conta conta) {		
		Criteria criteria = getSession().createCriteria(FaturaCartao.class);
		criteria.add(Restrictions.eq("conta.id", conta.getId()));
		criteria.add(Restrictions.eq("statusFaturaCartao", StatusFaturaCartao.FUTURA));
		return (FaturaCartao)criteria.addOrder(Order.asc("dataVencimento")).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).setMaxResults(1).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<FaturaCartao> findAllByUsuario(Usuario usuario) {
		String hql = "SELECT f FROM FaturaCartao f INNER JOIN  f.conta c WHERE c.usuario.id = :idUsuario ORDER BY f.dataVencimento ASC";
		Query query = getSession().createQuery(hql);
		query.setLong("idUsuario", usuario.getId());
		return query.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
}