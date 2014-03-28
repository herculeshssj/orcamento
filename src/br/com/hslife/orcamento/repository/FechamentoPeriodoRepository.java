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

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.enumeration.OperacaoConta;

@Repository
public class FechamentoPeriodoRepository extends AbstractCRUDRepository<FechamentoPeriodo> {
	
	public FechamentoPeriodoRepository() {
		super(new FechamentoPeriodo());
	}
	
	@SuppressWarnings("unchecked")
	public List<FechamentoPeriodo> findByContaAndOperacaoConta(Conta conta, OperacaoConta operacaoConta) {
		Criteria criteria = getSession().createCriteria(FechamentoPeriodo.class);
		criteria.add(Restrictions.eq("conta.id", conta.getId()));
		criteria.add(Restrictions.eq("operacao", operacaoConta));
		if (operacaoConta.equals(OperacaoConta.FECHAMENTO)) {
			criteria.addOrder(Order.desc("data"));
		} else if (operacaoConta.equals(OperacaoConta.REABERTURA)){
			criteria.addOrder(Order.asc("dataAlteracao"));
		} else {
			criteria.addOrder(Order.asc("data"));
		}
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public FechamentoPeriodo findUltimoFechamentoByConta(Conta conta) {
		Criteria criteria = getSession().createCriteria(FechamentoPeriodo.class);
		criteria.add(Restrictions.eq("conta.id", conta.getId()));
		criteria.add(Restrictions.eq("operacao", OperacaoConta.FECHAMENTO));
		List<FechamentoPeriodo> resultado = criteria.addOrder(Order.desc("data")).setMaxResults(1).list();
		if (resultado == null || resultado.size() != 1) {
			return null;
		} else {
			return resultado.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<FechamentoPeriodo> findFechamentosPosteriores(FechamentoPeriodo fechamento) {
		Criteria criteria = getSession().createCriteria(FechamentoPeriodo.class);
		criteria.add(Restrictions.eq("conta.id", fechamento.getConta().getId()));
		criteria.add(Restrictions.eq("operacao", OperacaoConta.FECHAMENTO));
		criteria.add(Restrictions.ge("data", fechamento.getData()));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public FechamentoPeriodo findFechamentoPeriodoAnterior(FechamentoPeriodo fechamentoPeriodo) {
		Criteria criteria = getSession().createCriteria(FechamentoPeriodo.class);
		criteria.add(Restrictions.lt("id", fechamentoPeriodo.getId()));
		criteria.add(Restrictions.eq("operacao", fechamentoPeriodo.getOperacao()));
		criteria.add(Restrictions.eq("conta.id", fechamentoPeriodo.getConta().getId()));		
		List<FechamentoPeriodo> resultado = criteria.addOrder(Order.desc("id")).setMaxResults(1).list(); 
		if (resultado == null || resultado.size() != 1) {
			return null;
		} else {
			return resultado.get(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public FechamentoPeriodo findLastFechamentoPeriodoBeforeDateByContaAndOperacao(Conta conta, Date data, OperacaoConta operacao) {
		Criteria criteria = getSession().createCriteria(FechamentoPeriodo.class);
		criteria.add(Restrictions.le("data", data));
		criteria.add(Restrictions.eq("operacao", operacao));
		criteria.add(Restrictions.eq("conta.id", conta.getId()));		
		List<FechamentoPeriodo> resultado = criteria.addOrder(Order.desc("id")).setMaxResults(1).list(); 
		if (resultado == null || resultado.size() != 1) {
			return null;
		} else {
			return resultado.get(0);
		}
	}
}