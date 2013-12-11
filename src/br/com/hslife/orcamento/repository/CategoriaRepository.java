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
	
	public boolean existsLinkages(Categoria categoria) {
		boolean result = true;
		
		String sqlLancamento = "select count(id) from lancamentoconta where idCategoria = " + categoria.getId();
		
		Query queryLancamento = getSession().createSQLQuery(sqlLancamento);
		
		BigInteger queryResultLancamento = (BigInteger)queryLancamento.uniqueResult();
		
		if (queryResultLancamento.longValue() == 0) {
			return false;
		}
		
		return result;
	}
	
	public void updateAllToNotDefault(TipoCategoria tipoCategoria, Usuario usuario) {
		String sql = "update categoria set padrao = false where idUsuario = " + usuario.getId() + " and tipoCategoria = '" + tipoCategoria.toString() + "'";
		
		Query query = getSession().createSQLQuery(sql);
		
		query.executeUpdate();
	}
	/*
	@Autowired
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	public void save(final Categoria entity) {
		getSession().persist(entity);
	}
	
	public void update(final Categoria entity) {
		getSession().merge(entity);
	}
	
	public void delete(final Categoria entity) {
		getSession().delete(entity);
	}
	
	public Categoria findById(final Long id) {
		return (Categoria)getSession().get(Categoria.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Categoria> findAll() {
		return getSession().createCriteria(Categoria.class).addOrder(Order.asc("descricao")).list();
	}
		
	@SuppressWarnings("unchecked")
	public List<Categoria> findByDescricao(String descricao) throws BusinessException {
		Criteria criteria = getSession().createCriteria(Categoria.class);
		criteria.add(Restrictions.ilike("descricao", descricao, MatchMode.ANYWHERE)).addOrder(Order.asc("descricao"));		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public Categoria findDefault() throws BusinessException {
		Criteria criteria = getSession().createCriteria(Categoria.class);
		criteria.add(Restrictions.eq("padrao", true));		
		List<Categoria> result = criteria.list(); 
		if (result == null || result.size() == 0) {
			return null;
		} else {
			return result.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Categoria> findByUsuario(Long idUsuario)	throws BusinessException {
		Criteria criteria = getSession().createCriteria(Categoria.class);
		criteria.add(Restrictions.eq("usuario.id", idUsuario)).addOrder(Order.asc("descricao"));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public Categoria findDefaultByUsuario(Long idUsuario)	throws BusinessException {
		Criteria criteria = getSession().createCriteria(Categoria.class);
		criteria.add(Restrictions.eq("padrao", true));
		criteria.add(Restrictions.eq("usuario.id", idUsuario));
		List<Categoria> result = criteria.list();	 
		if (result == null || result.size() == 0) {
			throw new BusinessException("Usuário não possui categoria padrão!");
		} else {
			return result.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Categoria findDefaultByUsuarioAndTipoCategoria(TipoCategoria tipoCategoria, Long idUsuario) throws BusinessException {
		Criteria criteria = getSession().createCriteria(Categoria.class);
		criteria.add(Restrictions.eq("padrao", true));
		criteria.add(Restrictions.eq("usuario.id", idUsuario));
		criteria.add(Restrictions.eq("tipoCategoria", tipoCategoria));
		List<Categoria> result = criteria.list();	 
		if (result == null || result.size() == 0) {
			return null;
		} else {
			return result.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Categoria> findByDescricaoAndUsuario(String descricao,	Usuario usuario) throws BusinessException {
		Criteria criteria = getSession().createCriteria(Categoria.class);
		criteria.add(Restrictions.ilike("descricao", descricao, MatchMode.ANYWHERE));
		if (!usuario.getLogin().equals("admin")) {
			criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		}		
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Categoria> findByUsuarioAndTipo(Usuario usuario, TipoCategoria tipoCategoria) throws BusinessException {
		Criteria criteria = getSession().createCriteria(Categoria.class);
		criteria.add(Restrictions.eq("tipoCategoria", tipoCategoria));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));	
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	*/
}