/***
  
  	Copyright (c) 2012 - 2016 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.repository;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Logs;
import br.com.hslife.orcamento.model.CriterioLog;

@Repository
public class LogRepository extends AbstractRepository {
	
	@SuppressWarnings("unchecked")
	public List<Logs> findByCriteriosLog(CriterioLog criterioBusca) {
		Criteria criteria = getSession().createCriteria(Logs.class);
		
		for (Criterion criterion : criterioBusca.buildCriteria()) {
			criteria.add(criterion);
		}
		
		return criteria.addOrder(Order.desc("logDate")).list();
	}
	
	public Logs findByID(Long id) {
		return (Logs)getQuery("FROM Logs log WHERE log.id = :idLog")
				.setLong("idLog", id)
				.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findAllLevel() {
		return getQuery("SELECT DISTINCT log.logLevel FROM Logs log").list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findAllLogger() {
		return getQuery("SELECT DISTINCT log.logger FROM Logs log").list();
	}
	
	public void delete(Logs entity) {
		getSession().delete(entity);
	}
}