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

import br.com.hslife.orcamento.entity.Banco;
import br.com.hslife.orcamento.entity.Usuario;

@Repository
public class BancoRepository extends AbstractCRUDRepository<Banco> {
	
	public BancoRepository() {
		super(new Banco());
	}
	
	@SuppressWarnings("unchecked")
	public List<Banco> findByNomeAndUsuario(String nome, Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Banco.class);
		criteria.add(Restrictions.ilike("nome", nome, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("nome")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Banco> findByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Banco.class);
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("nome")).list();
	}
	
	public boolean existsLinkages(Banco banco) {
		boolean result = true;
		
		String sqlConta = "select count(id) from conta where idBanco = " + banco.getId();
		String sqlCartao = "select count(id) from cartaocredito where idBanco = " + banco.getId();
		
		Query queryConta = getSession().createSQLQuery(sqlConta);
		Query queryCartao = getSession().createSQLQuery(sqlCartao);
		
		BigInteger queryResultConta = (BigInteger)queryConta.uniqueResult();
		BigInteger queryResultCartao = (BigInteger)queryCartao.uniqueResult();
		
		if (queryResultConta.longValue() == 0 && queryResultCartao.longValue() == 0) {
			return false;
		}
		
		return result;
	}
	
	public void updateAllToNotDefault(Usuario usuario) {
		String sql = "update banco set padrao = false where idUsuario = " + usuario.getId();
		
		Query query = getSession().createSQLQuery(sql);
		
		query.executeUpdate();
	}
}