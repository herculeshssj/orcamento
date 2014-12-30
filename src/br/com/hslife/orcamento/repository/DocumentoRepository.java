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
import org.hibernate.FetchMode;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.CategoriaDocumento;
import br.com.hslife.orcamento.entity.Documento;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.BusinessException;

@Repository
public class DocumentoRepository extends AbstractCRUDRepository<Documento>{
	
	public DocumentoRepository() {
		super(new Documento());
	}
	
	@Override
	public Documento findById(Long id) {
		 Criteria criteria = getSession().createCriteria(Documento.class).setFetchMode("arquivo", FetchMode.JOIN);
		 criteria.add(Restrictions.eq("id", id));
		 return (Documento)criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Documento> findByNomeAndUsuario(String nome, Usuario usuario) throws BusinessException {
		Criteria criteria = getSession().createCriteria(Documento.class, "documento")
				.createAlias("documento.categoriaDocumento", "categoria", JoinType.INNER_JOIN);
		criteria.add(Restrictions.ilike("documento.nome", nome, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("categoria.usuario.id", usuario.getId()));				
		return criteria.addOrder(Order.asc("documento.nome")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Documento> findByNomeAndCategoriaDocumentoByUsuario(String nome, CategoriaDocumento categoriaDocumento, Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Documento.class, "documento")
				.createAlias("documento.categoriaDocumento", "categoria", JoinType.INNER_JOIN);
		criteria.add(Restrictions.ilike("documento.nome", nome, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("documento.categoriaDocumento.id", categoriaDocumento.getId()));
		criteria.add(Restrictions.eq("categoria.usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("documento.nome")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Documento> findByUsuario(Usuario usuario) throws BusinessException {
		Criteria criteria = getSession().createCriteria(Documento.class, "documento")
				.createAlias("documento.categoriaDocumento", "categoria", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("categoria.usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("documento.nome")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Documento> findByCategoriaDocumento(CategoriaDocumento categoriaDocumento) throws BusinessException {
		Criteria criteria = getSession().createCriteria(Documento.class);
		criteria.add(Restrictions.eq("categoriaDocumento.id", categoriaDocumento.getId()));				
		return criteria.addOrder(Order.asc("nome")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Documento> findByCategoriaDocumentoAndUsuario(CategoriaDocumento categoriaDocumento, Usuario usuario) throws BusinessException {
		Criteria criteria = getSession().createCriteria(Documento.class, "documento")
				.createAlias("documento.categoriaDocumento", "categoria", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("documento.categoriaDocumento.id", categoriaDocumento.getId()));
		criteria.add(Restrictions.eq("categoria.usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("documento.nome")).list();
	}
}