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
	
	public boolean existsLinkages(MeioPagamento meioPagamento) {
		boolean result = true;
		
		String sqlLancamento = "select count(id) from lancamentoconta where idMeioPagamento = " + meioPagamento.getId();
		
		Query queryLancamento = getSession().createSQLQuery(sqlLancamento);
		
		BigInteger queryResultLancamento = (BigInteger)queryLancamento.uniqueResult();
		
		if (queryResultLancamento.longValue() == 0) {
			return false;
		}
		
		return result;
	}
	
	public void updateAllToNotDefault(Usuario usuario) {
		String sql = "update meiopagamento set padrao = false where idUsuario = " + usuario.getId();
		
		Query query = getSession().createSQLQuery(sql);
		
		query.executeUpdate();
	}
	
	public MeioPagamento findDefaultByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(MeioPagamento.class);
		criteria.add(Restrictions.eq("padrao", true));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return (MeioPagamento)criteria.uniqueResult();
	}
	
	/*	@Autowired
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
	public void save(MeioPagamento entity) {
		getSession().persist(entity);
	}
	
	public void update(MeioPagamento entity) {
		getSession().merge(entity);
	}
	
	public void delete(MeioPagamento entity) {
		getSession().delete(entity);
	}
	
	public MeioPagamento findById(Long id) {
		return (MeioPagamento)getSession().get(MeioPagamento.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<MeioPagamento> findAll() {
		return getSession().createCriteria(MeioPagamento.class).addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<MeioPagamento> findByDescricao(String descricao) throws BusinessException {
		Criteria criteria = getSession().createCriteria(MeioPagamento.class);
		criteria.add(Restrictions.ilike("descricao", descricao, MatchMode.ANYWHERE));		
		return criteria.addOrder(Order.asc("descricao")).list();
	}

	public MeioPagamento findDefault() throws BusinessException {
		Criteria criteria = getSession().createCriteria(MeioPagamento.class);
		criteria.add(Restrictions.eq("padrao", true));
		criteria.setMaxResults(1);
		return (MeioPagamento)criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<MeioPagamento> findByUsuario(Usuario usuario) throws BusinessException {
		Criteria criteria = getSession().createCriteria(MeioPagamento.class);
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("descricao")).list();
	}

	public MeioPagamento findDefaultByUsuario(Long idUsuario) throws BusinessException {
		Criteria criteria = getSession().createCriteria(MeioPagamento.class);
		criteria.add(Restrictions.eq("padrao", true));
		criteria.add(Restrictions.eq("usuario.id", idUsuario));
		criteria.setMaxResults(1);
		return (MeioPagamento)criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<MeioPagamento> findByDescricaoAndUsuario(String descricao, Usuario usuario) throws BusinessException {
		Criteria criteria = getSession().createCriteria(MeioPagamento.class);
		criteria.add(Restrictions.ilike("descricao", descricao, MatchMode.ANYWHERE));
		if (!usuario.getLogin().equals("admin")) {
			criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		}		
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	*/
}