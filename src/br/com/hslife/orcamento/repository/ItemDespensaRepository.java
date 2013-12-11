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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Despensa;
import br.com.hslife.orcamento.entity.ItemDespensa;
import br.com.hslife.orcamento.entity.UnidadeMedida;
import br.com.hslife.orcamento.entity.Usuario;

@Repository
public class ItemDespensaRepository extends AbstractCRUDRepository<ItemDespensa> {
	
	public ItemDespensaRepository() {
		super(new ItemDespensa());
	}
	
	@Override
	public ItemDespensa findById(Long id) {
		Criteria criteria = getSession().createCriteria(ItemDespensa.class).setFetchMode("movimentacao", FetchMode.JOIN);
		criteria.add(Restrictions.eq("id", id));
		return (ItemDespensa)criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemDespensa> findByDespensaUsuarioAndArquivado(Despensa despensa, Usuario usuario, boolean arquivado) {
		String hql = "select item from ItemDespensa as item inner join item.despensa as des where des.id = :idDespensa and des.usuario.id = :idUsuario and item.arquivado = :arquivado order by item.descricao asc";
		Query query = getSession().createQuery(hql);
		query.setLong("idDespensa", despensa.getId());
		query.setLong("idUsuario", usuario.getId());
		query.setBoolean("arquivado", arquivado);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemDespensa> findByUsuarioAndArquivado(Usuario usuario, boolean arquivado) {
		String hql = "select item from ItemDespensa as item inner join item.despensa as des where des.usuario.id = :idUsuario and item.arquivado = :arquivado order by item.descricao asc";
		Query query = getSession().createQuery(hql);
		query.setLong("idUsuario", usuario.getId());
		query.setBoolean("arquivado", arquivado);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemDespensa> findByDespensa(Despensa despensa) {
		Criteria criteria = getSession().createCriteria(ItemDespensa.class);
		criteria.add(Restrictions.eq("despensa.id", despensa.getId()));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemDespensa> findByUnidadeMedida(UnidadeMedida unidadeMedida) {
		Criteria criteria = getSession().createCriteria(ItemDespensa.class);
		criteria.add(Restrictions.eq("unidadeMedida.id", unidadeMedida.getId()));
		return criteria.list();
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
	
	public void save(ItemDespensa entity) {
		getSession().persist(entity);
	}
	
	public void update(ItemDespensa entity) {
		getSession().merge(entity);
	}
	
	public void delete(ItemDespensa entity) {
		getSession().delete(entity);
	}
	
	public ItemDespensa findById(Long id) {
		return (ItemDespensa)getSession().get(ItemDespensa.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemDespensa> findAll() {
		return getSession().createCriteria(ItemDespensa.class).addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemDespensa> findByDescricaoAndUsuario(String descricao, Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Despensa.class);
		criteria.add(Restrictions.ilike("descricao", descricao, MatchMode.ANYWHERE));
		if (!usuario.getTipoUsuario().equals(TipoUsuario.ROLE_ADMIN)) {
			criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		}
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemDespensa> findByUnidadeMedida(UnidadeMedida unidadeMedida) {
		Criteria criteria = getSession().createCriteria(ItemDespensa.class);
		criteria.add(Restrictions.eq("unidadeMedida.id", unidadeMedida.getId()));		
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemDespensa> findAllEnabledByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(ItemDespensa.class);
		criteria.add(Restrictions.eq("arquivado", false));
		if (!usuario.getTipoUsuario().equals(TipoUsuario.ROLE_ADMIN)) {
			criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		}
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<CompraConsumoOperacaoDespensa> findCompraConsumoOperacaoDespensaByDataInicioFim(OperacaoDespensa operacao, Date dataInicio, Date dataFim) {
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append("select d.descricao as item, sum(h.quantidade) as quantidade from despensa d ");
		sqlQuery.append("inner join despensa_historicodespensa dh on dh.despensa_id = d.id ");
		sqlQuery.append("inner join historicodespensa h on h.id = dh.historico_id where d.arquivado = false ");
		sqlQuery.append("and h.operacaoDespensa = '" + operacao.toString() +  "' ");
		if (dataInicio != null) {
			sqlQuery.append("and h.dataOperacao >= '" + Util.formataDataHora(dataInicio, Util.DATABASE) + "' ");
		}
		if (dataFim != null) {
			sqlQuery.append("and h.dataOperacao <= '" + Util.formataDataHora(dataFim, Util.DATABASE) + "' ");
		}
		sqlQuery.append("group by d.descricao");
		
		Query query = getSession().createSQLQuery(sqlQuery.toString()).addEntity(CompraConsumoOperacaoDespensa.class);
		return query.list();		
	}
	
	@SuppressWarnings("unchecked")
	public List<CompraConsumoItemDespensa> findCompraConsumoItemDespensaByDataInicioFim(ItemDespensa item, Date dataInicio, Date dataFim) {
		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append("select h.id, h.dataOperacao as data, h.quantidade as quantidade, h.operacaoDespensa as operacao, d.descricao as item from despensa d ");
		sqlQuery.append("inner join despensa_historicodespensa dh on dh.despensa_id = d.id ");
		sqlQuery.append("inner join historicodespensa h on h.id = dh.historico_id where d.arquivado = false ");
		sqlQuery.append("and d.id = :idItemDespensa ");
		if (dataInicio != null) {
			sqlQuery.append("and h.dataOperacao >= '" + Util.formataDataHora(dataInicio, Util.DATABASE) + "' ");
		}
		if (dataFim != null) {
			sqlQuery.append("and h.dataOperacao <= '" + Util.formataDataHora(dataFim, Util.DATABASE) + "' ");
		}
		Query query = getSession().createSQLQuery(sqlQuery.toString()).addEntity(CompraConsumoItemDespensa.class);
		query.setLong("idItemDespensa", item.getId());		
		return query.list();
		
	}
	*/
}
