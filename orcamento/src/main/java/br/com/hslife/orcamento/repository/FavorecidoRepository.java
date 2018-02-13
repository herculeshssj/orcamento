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
package br.com.hslife.orcamento.repository;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoPessoa;

@Repository
public class FavorecidoRepository extends AbstractCRUDRepository<Favorecido> {
	
	public FavorecidoRepository() {
		super(new Favorecido());
	}
	
	@SuppressWarnings("unchecked")
	public List<Favorecido> findByNomeAndUsuario(String nome, Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Favorecido.class);
		criteria.add(Restrictions.ilike("nome", nome, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("nome")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Favorecido> findByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Favorecido.class);
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("nome")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Favorecido> findEnabledByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Favorecido.class);
		criteria.add(Restrictions.eq("ativo", true));
		criteria.add(Restrictions.eq("financeiro", true));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("nome")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Favorecido> findByNomeUsuarioAndAtivo(String nome, Usuario usuario, boolean ativo) {
		String hql = "FROM Favorecido favorecido WHERE favorecido.nome like '%" + nome + "%' AND favorecido.usuario.id = :idUsuario AND favorecido.ativo = :ativo ORDER BY favorecido.nome ASC";
		Query query = getSession().createQuery(hql).setLong("idUsuario", usuario.getId()).setBoolean("ativo", ativo);
		return query.list();
	}
	
	public void updateAllToNotDefault(Usuario usuario) {
		String sql = "update favorecido set padrao = false where idUsuario = " + usuario.getId();
		
		Query query = getSession().createSQLQuery(sql);
		
		query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public Favorecido findDefaultByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Favorecido.class);
		criteria.add(Restrictions.eq("padrao", true));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		List<Favorecido> resultado = criteria.list();
		if (resultado != null && resultado.size() >= 1) {
			return resultado.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Favorecido> findTipoPessoaAndNomeAndAtivoByUsuario(TipoPessoa tipoPessoa, String nome, Boolean ativo, Usuario usuario) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM Favorecido favorecido WHERE ");
		if (tipoPessoa != null) {
			hql.append("favorecido.tipoPessoa = :tipo AND ");
		}
		if (nome != null && !nome.isEmpty()) {
			hql.append("favorecido.nome LIKE '%");
			hql.append(nome);
			hql.append("%' AND ");
		}
		if (ativo != null) {
			hql.append("favorecido.ativo = :ativo AND ");
		}
		
		hql.append("favorecido.usuario.id = :idUsuario ORDER BY favorecido.nome ASC");
		
		Query hqlQuery = getQuery(hql.toString());
		if (tipoPessoa != null) {
			hqlQuery.setParameter("tipo", tipoPessoa);
		}
		if (ativo != null) {
			hqlQuery.setParameter("ativo", ativo);
		}
		
		hqlQuery.setParameter("idUsuario", usuario.getId());
		
		return hqlQuery.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Favorecido> findTipoPessoaAndNomeAndAtivoByUsuario(TipoPessoa tipoPessoa, String nome, Boolean ativo, List<Usuario> usuarios) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM Favorecido favorecido WHERE ");
		if (tipoPessoa != null) {
			hql.append("favorecido.tipoPessoa = :tipo AND ");
		}
		if (nome != null && !nome.isEmpty()) {
			hql.append("favorecido.nome LIKE '%");
			hql.append(nome);
			hql.append("%' AND ");
		}
		if (ativo != null) {
			hql.append("favorecido.ativo = :ativo AND ");
		}
		
		hql.append("favorecido.usuario.id IN (:idUsuario) ORDER BY favorecido.nome ASC");
		
		Query hqlQuery = getQuery(hql.toString());
		if (tipoPessoa != null) {
			hqlQuery.setParameter("tipo", tipoPessoa);
		}
		if (ativo != null) {
			hqlQuery.setParameter("ativo", ativo);
		}
		
		// Resgata os IDs dos usuários
		List<Long> idsUsuarios = new ArrayList<>();
		for (Usuario u : usuarios) {
			if (u != null && u.getId() != null) {
				idsUsuarios.add(u.getId());
			}
		}
		
		hqlQuery.setParameterList("idUsuario", idsUsuarios);
		
		return hqlQuery.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Favorecido> findTipoPessoaAndNomeAndAtivoIsFinanceiroByUsuario(TipoPessoa tipoPessoa, String nome, Boolean ativo, Boolean financeiro, Usuario usuario) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM Favorecido favorecido WHERE ");
		if (tipoPessoa != null) {
			hql.append("favorecido.tipoPessoa = :tipo AND ");
		}
		if (nome != null && !nome.isEmpty()) {
			hql.append("favorecido.nome LIKE '%");
			hql.append(nome);
			hql.append("%' AND ");
		}
		if (ativo != null) {
			hql.append("favorecido.ativo = :ativo AND ");
		}
		if (financeiro != null) {
			hql.append("favorecido.financeiro = :ativoFinanceiro AND ");
		}
		
		hql.append("favorecido.usuario.id = :idUsuario ORDER BY favorecido.nome ASC");
		
		Query hqlQuery = getQuery(hql.toString());
		if (tipoPessoa != null) {
			hqlQuery.setParameter("tipo", tipoPessoa);
		}
		if (ativo != null) {
			hqlQuery.setParameter("ativo", ativo);
		}
		if (financeiro != null) {
			hqlQuery.setParameter("ativoFinanceiro", financeiro);
		}
		
		hqlQuery.setParameter("idUsuario", usuario.getId());
		
		return hqlQuery.list();
	}
}
