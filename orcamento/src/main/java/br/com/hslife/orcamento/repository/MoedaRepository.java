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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Usuario;

@Repository
public class MoedaRepository extends AbstractCRUDRepository<Moeda> {
	
	public MoedaRepository() {
		super(new Moeda());
	}
	
	@SuppressWarnings("unchecked")
	public List<Moeda> findByNomeAndUsuario(String nome, Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Moeda.class);
		criteria.add(Restrictions.ilike("nome", nome, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Moeda> findByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Moeda.class);
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.list();
	}
	
	public Moeda findDefaultByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Moeda.class);
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		criteria.add(Restrictions.eq("padrao", true));
		return (Moeda)criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Moeda> findByNomeUsuarioAndAtivo(String nome, Usuario usuario, boolean ativo) {
		String hql = "FROM Moeda moeda WHERE moeda.nome like '%" + nome + "%' AND moeda.usuario.id = :idUsuario AND moeda.ativo = :ativo ORDER BY moeda.nome ASC";
		Query query = getSession().createQuery(hql)
				.setLong("idUsuario", usuario.getId())
				.setBoolean("ativo", ativo);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Moeda> findActiveByUsuario(Usuario usuario) {
		return getQueryNoType("FROM Moeda moeda WHERE moeda.usuario.id = :idUsuario AND moeda.ativo = true ORDER BY moeda.nome ASC")
				.setLong("idUsuario", usuario.getId())
				.list();
	}
	
	public Moeda findCodigoMoedaByUsuario(String codigo, Usuario usuario) {
		return (Moeda)getQuery("FROM Moeda moeda WHERE moeda.codigoMonetario = :codigo AND moeda.usuario.id = :idUsuario")
				.setString("codigo", codigo)
				.setLong("idUsuario", usuario.getId())
				.setMaxResults(1)
				.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findAllCodigoMonetarioByUsuario(Usuario usuario) {
		return getSession().createSQLQuery("select distinct codigoMonetario from orcamento.moeda where codigoMonetario is not null and idUsuario = " 
				+ usuario.getId()).list();				
	}
}
