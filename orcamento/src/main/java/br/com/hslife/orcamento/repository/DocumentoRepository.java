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

***/package br.com.hslife.orcamento.repository;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.CategoriaDocumento;
import br.com.hslife.orcamento.entity.Documento;
import br.com.hslife.orcamento.entity.Usuario;

@Repository
public class DocumentoRepository extends AbstractCRUDRepository<Documento>{
	
	public DocumentoRepository() {
		super(new Documento());
	}
	
	@SuppressWarnings("unchecked")
	public List<Documento> findByNomeAndUsuario(String nome, Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Documento.class, "documento")
				.createAlias("documento.categoriaDocumento", "categoria", JoinType.INNER_JOIN);
		criteria.add(Restrictions.ilike("documento.nome", nome, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("categoria.usuario.id", usuario.getId()));				
		return criteria.addOrder(Order.asc("documento.nome")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Documento> findByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Documento.class, "documento")
				.createAlias("documento.categoriaDocumento", "categoria", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("categoria.usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("documento.nome")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Documento> findByCategoriaDocumento(CategoriaDocumento categoriaDocumento) {
		Criteria criteria = getSession().createCriteria(Documento.class);
		criteria.add(Restrictions.eq("categoriaDocumento.id", categoriaDocumento.getId()));				
		return criteria.addOrder(Order.asc("nome")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Documento> findByCategoriaDocumentoAndUsuario(CategoriaDocumento categoriaDocumento, Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Documento.class, "documento")
				.createAlias("documento.categoriaDocumento", "categoria", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("documento.categoriaDocumento.id", categoriaDocumento.getId()));
		criteria.add(Restrictions.eq("categoria.usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("documento.nome")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Documento> findByNomeAndCategoriaDocumentoByUsuario(String nome, CategoriaDocumento categoriaDocumento, Usuario usuario) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM Documento documento WHERE ");
		if (categoriaDocumento != null) {
			hql.append("documento.categoriaDocumento = :tipo AND ");
		}
		if (nome != null && !nome.isEmpty()) {
			hql.append("documento.nome LIKE '%");
			hql.append(nome);
			hql.append("%' AND ");
		}
		
		hql.append("documento.categoriaDocumento.usuario.id = :idUsuario ORDER BY documento.nome ASC");
		
		Query hqlQuery = getQuery(hql.toString());
		if (categoriaDocumento != null) {
			hqlQuery.setParameter("tipo", categoriaDocumento);
		}
		
		hqlQuery.setParameter("idUsuario", usuario.getId());
		
		return hqlQuery.list();
	}
}
