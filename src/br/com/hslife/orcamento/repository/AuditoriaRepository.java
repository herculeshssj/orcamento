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

import java.math.BigInteger;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.entity.Auditoria;
import br.com.hslife.orcamento.model.CriterioAuditoria;

@Repository
@Transactional
public class AuditoriaRepository extends AbstractRepository {
	
	public void delete(Auditoria entity) {
		getSession().delete(entity);
	}
	
	public Auditoria findById(Long id) {
		Criteria criteria = getSession().createCriteria(Auditoria.class).setFetchMode("dadosAuditoria", FetchMode.JOIN);
		criteria.add(Restrictions.eq("id", id));
		return (Auditoria)criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Auditoria> findByCriteriosAuditoria(CriterioAuditoria criterio) {
		Criteria criteria = getSession().createCriteria(Auditoria.class);
		
		if (criterio.getUsuario() != null && !criterio.getUsuario().isEmpty()) {
			criteria.add(Restrictions.eq("usuario", criterio.getUsuario()));
		}
		
		if (criterio.getClasse() != null && !criterio.getClasse().isEmpty()) {
			criteria.add(Restrictions.eq("classe", criterio.getClasse()));
		}
		
		if (criterio.getTransacao() != null && !criterio.getTransacao().isEmpty()) {
			criteria.add(Restrictions.eq("transacao", criterio.getTransacao()));
		}
		
		if (criterio.getInicio() != null) {
			criteria.add(Restrictions.ge("data", criterio.getInicio()));
		}
		
		if (criterio.getFim() != null) {
			criteria.add(Restrictions.le("data", criterio.getFim()));
		}
		
		return criteria.addOrder(Order.asc("dataHora")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findClasses() {
		return getSession().createSQLQuery("select distinct classe from orcamento.auditoria order by classe asc").list();
	}
	
	public long countRegistroAuditoriaByUsuario(String usuario) {
		String sql = "select count(*) from orcamento.auditoria where usuario = '" + usuario + "'";		
		Query query = getSession().createSQLQuery(sql);		
		BigInteger queryResult = (BigInteger)query.uniqueResult();
		return queryResult.longValue();
	}
}