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

package br.com.hslife.orcamento.repository;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Usuario;

@Repository
public class MeioPagamentoRepository extends AbstractCRUDRepository<MeioPagamento> {
	
	public MeioPagamentoRepository() {
		super(new MeioPagamento());
	}
	
	@SuppressWarnings("unchecked")
	public List<MeioPagamento> findByDescricaoAndUsuario(String descricao, Usuario usuario) {
		Criteria criteria = getSession().createCriteria(MeioPagamento.class);
		criteria.add(Restrictions.ilike("descricao", descricao, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<MeioPagamento> findByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(MeioPagamento.class);
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<MeioPagamento> findEnabledByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(MeioPagamento.class);
		criteria.add(Restrictions.eq("ativo", true));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	public void updateAllToNotDefault(Usuario usuario) {
		String sql = "update meiopagamento set padrao = false where idUsuario = " + usuario.getId();
		
		Query query = getSession().createSQLQuery(sql);
		
		query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public MeioPagamento findDefaultByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(MeioPagamento.class);
		criteria.add(Restrictions.eq("padrao", true));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		List<MeioPagamento> resultado = criteria.list();
		if (resultado != null && resultado.size() >= 1) {
			return resultado.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<MeioPagamento> findByDescricaoUsuarioAndAtivo(String descricao, Usuario usuario, boolean ativo) {
		String hql = "FROM MeioPagamento meioPagamento WHERE meioPagamento.descricao LIKE '%" + descricao + "%' AND meioPagamento.usuario.id = :idUsuario AND meioPagamento.ativo = :ativo ORDER BY meioPagamento.descricao ASC";
		Query query = getSession().createQuery(hql).setLong("idUsuario", usuario.getId()).setBoolean("ativo", ativo);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<MeioPagamento> findDescricaoAndAtivoByUsuario(String descricao, Boolean ativo, Usuario usuario) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM MeioPagamento meioPagamento WHERE ");
		
		if (descricao != null && !descricao.isEmpty()) {
			hql.append("meioPagamento.descricao LIKE '%");
			hql.append(descricao);
			hql.append("%' AND ");
		}
		if (ativo != null) {
			hql.append("meioPagamento.ativo = :ativo AND ");
		}
		
		hql.append("meioPagamento.usuario.id = :idUsuario ORDER BY meioPagamento.descricao ASC");
		
		Query hqlQuery = getQuery(hql.toString());
		
		if (ativo != null) {
			hqlQuery.setParameter("ativo", ativo);
		}
		
		hqlQuery.setParameter("idUsuario", usuario.getId());
		
		return hqlQuery.list();
	}
}