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
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.PrevisaoLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoAgrupamentoBusca;

@Repository
@Transactional
public class PrevisaoLancamentoContaRepository extends AbstractRepository {
	
	public void save(PrevisaoLancamentoConta entity) {
		getSession().persist(entity);
	}
	
	public void deletePrevisaoLancamentoConta(Conta conta, int ano, TipoAgrupamentoBusca agrupamento) {
		String sql = "delete from previsaolancamentoconta where idConta = " + conta.getId() + " and ano = " + ano + " and agrupamento = '" +  agrupamento + "'";
		Query query = getSession().createSQLQuery(sql);
		query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public List<PrevisaoLancamentoConta> findByContaAnoAndAgrupamento(Conta conta, int ano, TipoAgrupamentoBusca agrupamento) {
		Criteria criteria = getSession().createCriteria(PrevisaoLancamentoConta.class);
		criteria.add(Restrictions.eq("conta.id", conta.getId()));
		criteria.add(Restrictions.eq("ano", ano));
		criteria.add(Restrictions.eq("agrupamento", agrupamento));
		criteria.addOrder(Order.asc("indice"));
		return criteria.list();
	}
}