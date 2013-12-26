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

import br.com.hslife.orcamento.entity.BuscaSalva;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Usuario;

@Repository
public class BuscaSalvaRepository extends AbstractCRUDRepository<BuscaSalva> {
	
	public BuscaSalvaRepository() {
		super(new BuscaSalva());
	}
	
	@SuppressWarnings("unchecked")
	public List<BuscaSalva> findByConta(Conta conta) {
		Criteria criteria = getSession().createCriteria(BuscaSalva.class);
		criteria.add(Restrictions.eq("conta.id", conta.getId()));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<BuscaSalva> findAllByUsuario(Usuario usuario) {
		String sql = "select b.* from buscasalva b inner join conta c on c.id = b.idConta where c.idUsuario = :idUsuario and not c.tipoConta = 'CARTAO' order by b.descricao asc";
		Query query = getSession().createSQLQuery(sql).addEntity(BuscaSalva.class).setLong("idUsuario", usuario.getId());
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<BuscaSalva> findAllEnabledContaByUsuario(Usuario usuario) {
		String sql = "select b.* from buscasalva b inner join conta c on c.id = b.idConta where c.idUsuario = :idUsuario and c.ativo = true and not c.tipoConta = 'CARTAO' order by b.descricao asc";
		Query query = getSession().createSQLQuery(sql).addEntity(BuscaSalva.class).setLong("idUsuario", usuario.getId());
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<BuscaSalva> findAllByContaAndUsuario(Conta conta, Usuario usuario) {
		String sql = "select b.* from buscasalva b inner join conta c on c.id = b.idConta and c.id = :idConta where c.idUsuario = :idUsuario order by b.descricao asc";
		Query query = getSession().createSQLQuery(sql).addEntity(BuscaSalva.class).setLong("idUsuario", usuario.getId()).setLong("idConta", conta.getId());
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<BuscaSalva> findAllEnabledContaByContaAndUsuario(Conta conta, Usuario usuario) {
		String sql = "select b.* from buscasalva b inner join conta c on c.id = b.idConta and c.id = :idConta where c.idUsuario = :idUsuario and c.ativo = true order by b.descricao asc";
		Query query = getSession().createSQLQuery(sql).addEntity(BuscaSalva.class).setLong("idUsuario", usuario.getId()).setLong("idConta", conta.getId());
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<BuscaSalva> findAllContaCartaoByUsuario(Usuario usuario) {
		String sql = "select b.* from buscasalva b inner join conta c on c.id = b.idConta where c.idUsuario = :idUsuario and c.tipoConta = 'CARTAO' order by b.descricao asc";
		Query query = getSession().createSQLQuery(sql).addEntity(BuscaSalva.class).setLong("idUsuario", usuario.getId());
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<BuscaSalva> findAllEnabledContaCartaoByUsuario(Usuario usuario) {
		String sql = "select b.* from buscasalva b inner join conta c on c.id = b.idConta where c.idUsuario = :idUsuario and c.tipoConta = 'CARTAO' and c.ativo = true order by b.descricao asc";
		Query query = getSession().createSQLQuery(sql).addEntity(BuscaSalva.class).setLong("idUsuario", usuario.getId());
		return query.list();
	}
}