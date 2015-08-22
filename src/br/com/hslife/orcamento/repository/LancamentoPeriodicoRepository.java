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

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;

@Repository
public class LancamentoPeriodicoRepository extends AbstractCRUDRepository<LancamentoPeriodico> {
	
	public LancamentoPeriodicoRepository() {
		super(new LancamentoPeriodico());
	}
	
	@Override
	public LancamentoPeriodico findById(Long id) {
		return (LancamentoPeriodico) getQuery("FROM LancamentoPeriodico as entity LEFT JOIN FETCH entity.pagamentos WHERE entity.id = :idEntity")
				.setLong("idEntity", id)
				.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoPeriodico> findByTipoLancamentoContaAndStatusLancamento(TipoLancamentoPeriodico tipo, Conta conta, StatusLancamento statusLancamento) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM LancamentoPeriodico periodico WHERE ");
		if (tipo != null) {
			hql.append("periodico.tipoLancamentoPeriodico = :tipo AND ");
		}
		if (conta != null) {
			hql.append("periodico.conta.id = :idConta AND ");
		}
		hql.append("periodico.statusLancamento = :status ORDER BY periodico.descricao ASC");
		
		Query hqlQuery = getQuery(hql.toString());
		if (tipo != null) {
			hqlQuery.setParameter("tipo", tipo);
		}
		if (conta != null) {
			hqlQuery.setLong("idConta", conta.getId());
		}
		hqlQuery.setParameter("status", statusLancamento);
		
		return hqlQuery.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<LancamentoPeriodico> findByTipoLancamentoAndStatusLancamentoByUsuario(TipoLancamentoPeriodico tipo, StatusLancamento status, Usuario usuario) {
		return getQuery("FROM LancamentoPeriodico periodico WHERE periodico.tipoLancamentoPeriodico = :tipo AND periodico.statusLancamento = :status AND periodico.usuario.id = :idUsuario ORDER BY periodico.descricao ASC")
				.setParameter("tipo", tipo)
				.setParameter("status", status)
				.setLong("idUsuario", usuario.getId())
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<LancamentoPeriodico> findByTipoLancamentoAndTipoContaAndStatusLancamento(TipoLancamentoPeriodico tipo, TipoConta tipoConta,	StatusLancamento statusLancamento) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM LancamentoPeriodico periodico WHERE ");
		if (tipo != null) {
			hql.append("periodico.tipoLancamentoPeriodico = :tipo AND ");
		}
		if (tipoConta != null) {
			hql.append("periodico.conta.tipoConta = :tipoConta AND ");
		}
		hql.append("periodico.statusLancamento = :status ORDER BY periodico.descricao ASC");
		
		Query hqlQuery = getQuery(hql.toString());
		if (tipo != null) {
			hqlQuery.setParameter("tipo", tipo);
		}
		if (tipoConta != null) {
			hqlQuery.setParameter("tipoConta", tipoConta);
		}
		hqlQuery.setParameter("status", statusLancamento);
		
		return hqlQuery.list();
	}
}