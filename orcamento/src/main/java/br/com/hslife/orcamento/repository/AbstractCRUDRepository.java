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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.query.Query;

import br.com.hslife.orcamento.entity.EntityPersistence;

public abstract class AbstractCRUDRepository<E extends EntityPersistence> extends AbstractRepository implements IRepository<E>{
	
	protected Map<String, Object> hqlParameters = new HashMap<>();
	
	private E entity;
	private Class<E> clazz;
	
	public AbstractCRUDRepository(E entity) {
		this.entity = entity;
	}
	
	public AbstractCRUDRepository(E entity, Class<E> clazz) {
		this.entity = entity;
		this.clazz = clazz;
	}
	
	public void save(E entity) {
		getSession().persist(entity);
	}
	
	public void update(E entity) {
		getSession().merge(entity);
	}
	
	public void delete(E entity) {
		getSession().delete(entity);
	}
	
	@SuppressWarnings("unchecked")
	public E findById(Long id) {
		return (E)getSession().createQuery("SELECT e FROM " + entity.getClass().getSimpleName() + " e WHERE e.id = :idEntity")
				.setParameter("idEntity", id)
				.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<E> findAll() {
		return getSession().createQuery("SELECT e FROM " + entity.getClass().getSimpleName() + " e").getResultList();
	}
	
	protected Query<E> getQuery(String hql) {
		return getSession().createQuery(hql, clazz);
	}
	
	protected Query<E> getQueryApplyingParameters(StringBuilder hql) {
		return this.getQueryApplyingParameters(hql.toString());
	}
	
	protected Query<E> getQueryApplyingParameters(String hql) {
		Query<E> query = getSession().createQuery(hql, clazz);
		
		for (String s : hqlParameters.keySet()) {
			query.setParameter(s, hqlParameters.get(s));
		}
		
		hqlParameters.clear();
		
		return query;
	}
}