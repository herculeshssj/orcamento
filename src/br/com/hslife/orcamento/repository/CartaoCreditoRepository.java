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

import br.com.hslife.orcamento.entity.CartaoCredito;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoCartao;

@Repository
public class CartaoCreditoRepository extends AbstractCRUDRepository<CartaoCredito> {
	
	public CartaoCreditoRepository() {
		super(new CartaoCredito());
	}
	
	@SuppressWarnings("unchecked")
	public List<CartaoCredito> findByDescricaoAndUsuario(String descricao, Usuario usuario) {
		// TODO migrar para HQL
		Criteria criteria = getSession().createCriteria(CartaoCredito.class);
		criteria.add(Restrictions.ilike("descricao", descricao, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<CartaoCredito> findByDataValidade(Date data) {
		// TODO migrar para HQL
		Criteria criteria = getSession().createCriteria(CartaoCredito.class);		
		criteria.add(Restrictions.le("validade", data));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<CartaoCredito> findByUsuario(Usuario usuario) {
		// TODO migrar para HQL
		Criteria criteria = getSession().createCriteria(CartaoCredito.class);
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<CartaoCredito> findOnlyCartaoTipoCreditoByUsuario(Usuario usuario) {
		// TODO migrar para HQL
		Criteria criteria = getSession().createCriteria(CartaoCredito.class);
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		criteria.add(Restrictions.eq("tipoCartao", TipoCartao.CREDITO));
		criteria.add(Restrictions.eq("ativo", true));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	public boolean existsLinkages(CartaoCredito cartaoCredito) {		
		boolean result = true;
		
		String sqlFatura = "select count(id) from faturacartao where idConta = " + cartaoCredito.getConta().getId();
		String sqlLancamento = "select count(*) from lancamentoconta l inner join conta cc on cc.id = l.idConta inner join cartaocredito c on c.id = cc.idCartao where c.id = " + cartaoCredito.getId();
		
		Query queryFatura = getSession().createSQLQuery(sqlFatura);
		Query queryLancamento = getSession().createSQLQuery(sqlLancamento);
		
		BigInteger queryResultFatura = (BigInteger)queryFatura.uniqueResult();
		BigInteger queryResultLancamento = (BigInteger)queryLancamento.uniqueResult();
		
		if (queryResultFatura.longValue() == 0 && queryResultLancamento.longValue() == 0) {
			return false;
		}
		
		return result;
	}
	
	public boolean isSubstituto(CartaoCredito cartaoCredito) {
		// TODO migrar para HQL
		Criteria criteria = getSession().createCriteria(CartaoCredito.class);
		criteria.add(Restrictions.eq("cartaoSubstituto.id", cartaoCredito.getId()));
		if (criteria.list() != null && criteria.list().size() != 0) {
			return true;
		} else {
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<CartaoCredito> findDescricaoOrAtivoByUsuario(String descricao, Usuario usuario, Boolean ativo) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM CartaoCredito cartao WHERE ");
		if (descricao != null) {
			hql.append("cartao.descricao LIKE '%");
			hql.append(descricao);
			hql.append("%' AND ");
		}
		if (ativo != null) {
			hql.append("cartao.ativo = :ativo AND ");
		}
		
		hql.append("cartao.usuario.id = :idUsuario ORDER BY cartao.descricao ASC");
		
		Query hqlQuery = getQuery(hql.toString());
		
		if (ativo != null) {
			hqlQuery.setBoolean("ativo", ativo);
		}
		
		hqlQuery.setLong("idUsuario", usuario.getId());
		
		return hqlQuery.list();
	}
}