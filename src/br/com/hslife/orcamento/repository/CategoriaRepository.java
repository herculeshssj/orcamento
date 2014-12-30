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

package br.com.hslife.orcamento.repository;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoCategoria;

@Repository
public class CategoriaRepository extends AbstractCRUDRepository<Categoria> {
	
	public CategoriaRepository() {
		super(new Categoria());
	}
	
	@SuppressWarnings("unchecked")
	public List<Categoria> findByDescricaoAndUsuario(String descricao, Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Categoria.class);
		criteria.add(Restrictions.ilike("descricao", descricao, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Categoria> findByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Categoria.class);
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Categoria> findEnabledByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Categoria.class);
		criteria.add(Restrictions.eq("ativo", true));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	public Categoria findDefaultByTipoCategoriaAndUsuario(Usuario usuario, TipoCategoria tipoCategoria) {
		Criteria criteria = getSession().createCriteria(Categoria.class);
		criteria.add(Restrictions.eq("padrao", true));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		criteria.add(Restrictions.eq("tipoCategoria", tipoCategoria));
		return (Categoria)criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Categoria> findByTipoCategoriaAndUsuario(TipoCategoria tipoCategoria, Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Categoria.class);
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		criteria.add(Restrictions.eq("tipoCategoria", tipoCategoria));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	public void updateAllToNotDefault(TipoCategoria tipoCategoria, Usuario usuario) {
		String sql = "update categoria set padrao = false where idUsuario = " + usuario.getId() + " and tipoCategoria = '" + tipoCategoria.toString() + "'";
		
		Query query = getSession().createSQLQuery(sql);
		
		query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public List<Categoria> findByDescricaoUsuarioAndAtivo(String descricao, Usuario usuario, boolean ativo) {
		String hql = "FROM Categoria categoria WHERE categoria.descricao LIKE '%" + descricao + "%' AND categoria.usuario.id = :idUsuario AND categoria.ativo = :ativo ORDER BY categoria.descricao ASC";
		Query query = getSession().createQuery(hql).setLong("idUsuario", usuario.getId()).setBoolean("ativo", ativo);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Categoria> findActiveByTipoCategoriaAndUsuario(TipoCategoria tipoCategoria, Usuario usuario) {
		return getQuery("FROM Categoria categoria WHERE categoria.tipoCategoria = :tipoCategoria AND categoria.usuario.id = :idUsuario AND categoria.ativo = true ORDER BY categoria.descricao ASC")
				.setParameter("tipoCategoria", tipoCategoria)
				.setLong("idUsuario", usuario.getId())
				.list();
	}
}