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
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Banco;
import br.com.hslife.orcamento.entity.CartaoCredito;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.exception.BusinessException;

@Repository
public class ContaRepository extends AbstractCRUDRepository<Conta> {
	
	public ContaRepository() {
		super(new Conta());
	}
	
	@SuppressWarnings("unchecked")
	public List<Conta> findAll() throws BusinessException {
		Criteria criteria = getSession().createCriteria(Conta.class);
		criteria.add(Restrictions.ne("tipoConta", TipoConta.CARTAO));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Conta> findByBanco(Long idBanco) {
		Criteria criteria = getSession().createCriteria(Conta.class);
		criteria.add(Restrictions.eq("banco.id", idBanco));
		criteria.add(Restrictions.ne("tipoConta", TipoConta.CARTAO));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Conta> findByDescricao(String descricao) {
		Criteria criteria = getSession().createCriteria(Conta.class);
		criteria.add(Restrictions.ilike("descricao", descricao, MatchMode.ANYWHERE));
		criteria.add(Restrictions.ne("tipoConta", TipoConta.CARTAO));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Conta> findByUsuario(Long idUsuario) {
		Criteria criteria = getSession().createCriteria(Conta.class);
		criteria.add(Restrictions.eq("usuario.id", idUsuario));
		criteria.add(Restrictions.ne("tipoConta", TipoConta.CARTAO));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Conta> findAllAtivos() throws BusinessException {
		Criteria criteria = getSession().createCriteria(Conta.class);
		criteria.add(Restrictions.eq("ativo", true));
		criteria.add(Restrictions.ne("tipoConta", TipoConta.CARTAO));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Conta> findAllAtivosByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Conta.class);
		criteria.add(Restrictions.eq("ativo", true));
		if (!usuario.getLogin().equals("admin")) {
			criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		}
		criteria.add(Restrictions.ne("tipoConta", TipoConta.CARTAO));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Conta> findByDescricaoAndUsuario(String descricao, Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Conta.class);
		criteria.add(Restrictions.ilike("descricao", descricao, MatchMode.ANYWHERE));
		if (!usuario.getLogin().equals("admin")) {
			criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		}		
		criteria.add(Restrictions.ne("tipoConta", TipoConta.CARTAO));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Conta> findByBancoAndUsuario(Banco banco, Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Conta.class);
		criteria.add(Restrictions.eq("banco.id", banco.getId()));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		criteria.add(Restrictions.ne("tipoConta", TipoConta.CARTAO));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Conta> findByTipoContaAndUsuario(TipoConta tipoConta, Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Conta.class);
		criteria.add(Restrictions.eq("tipoConta", tipoConta));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		criteria.add(Restrictions.ne("tipoConta", TipoConta.CARTAO));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Conta> findEnabledByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Conta.class);
		criteria.add(Restrictions.eq("ativo", true));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		criteria.add(Restrictions.ne("tipoConta", TipoConta.CARTAO));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Conta> findOnlyTipoCartaoEnabledByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Conta.class);
		criteria.add(Restrictions.eq("ativo", true));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		criteria.add(Restrictions.eq("tipoConta", TipoConta.CARTAO));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Conta> findOnlyTipoCartaoByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Conta.class);
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		criteria.add(Restrictions.eq("tipoConta", TipoConta.CARTAO));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	public Conta findByCartaoCredito(CartaoCredito cartaoCredito) {
		Criteria criteria = getSession().createCriteria(Conta.class);
		criteria.add(Restrictions.eq("cartaoCredito.id", cartaoCredito.getId()));
		return (Conta)criteria.uniqueResult();
	}
	
	public boolean existsLinkages(Conta conta) {
		boolean result = false;
		
		BigInteger resultFechamentoPeriodo = (BigInteger)getSession().createSQLQuery("select count(id) from fechamentoperiodo where operacao = 'FECHAMENTO' and idContaBancaria = " + conta.getId()).uniqueResult();
		BigInteger resultLancamento = (BigInteger)getSession().createSQLQuery("select count(id) from lancamentoconta where idConta = " + conta.getId()).uniqueResult();
		
		if (resultFechamentoPeriodo.longValue() != 0) {
			return true;
		}
		
		if (resultLancamento.longValue() != 0) {
			return true;
		}
				
		return result;
		
	}
	
	public boolean existsLinkagesCartao(Conta conta) {
		boolean result = false;
		/*
		BigInteger resultFechamentoPeriodo = (BigInteger)getSession().createSQLQuery("select count(id) from fechamentoperiodo where operacao = 'FECHAMENTO' and idContaBancaria = " + conta.getId()).uniqueResult();
		BigInteger resultLancamento = (BigInteger)getSession().createSQLQuery("select count(id) from lancamentoconta where idConta = " + conta.getId()).uniqueResult();
		
		if (resultFechamentoPeriodo.longValue() != 0) {
			return true;
		}
		
		if (resultLancamento.longValue() != 0) {
			return true;
		}
		*/	
		return result;		
	}
}