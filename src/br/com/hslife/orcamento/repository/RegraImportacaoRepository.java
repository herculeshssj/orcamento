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
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.RegraImportacao;

@Repository
public class RegraImportacaoRepository extends AbstractCRUDRepository<RegraImportacao> {
		
	public RegraImportacaoRepository() {
		super(new RegraImportacao());
	}
	
	@SuppressWarnings("unchecked")
	public List<RegraImportacao> findAllByConta(Conta conta) {
		return getQuery("FROM RegraImportacao regra WHERE regra.conta.id = :idConta")
				.setLong("idConta", conta.getId())
				.list();
	}
	
	public RegraImportacao findEqualEntity(RegraImportacao regra) {
		Criteria criteria = getSession().createCriteria(RegraImportacao.class);
		criteria.add(Restrictions.eq("conta.id", regra.getConta().getId()));
		
		if (regra.getTexto() != null || !regra.getTexto().isEmpty()) {
			criteria.add(Restrictions.eq("texto", regra.getTexto()));
		}
		
		if (regra.getIdCategoria() != null) {
			criteria.add(Restrictions.eq("idCategoria", regra.getIdCategoria()));
		}
		
		if (regra.getIdFavorecido() != null) {
			criteria.add(Restrictions.eq("idFavorecido", regra.getIdFavorecido()));
		}
		
		if (regra.getIdMeioPagamento() != null) {
			criteria.add(Restrictions.eq("idMeioPagamento", regra.getIdMeioPagamento()));
		}
		
		return (RegraImportacao)criteria.setMaxResults(1).uniqueResult();
	}
}