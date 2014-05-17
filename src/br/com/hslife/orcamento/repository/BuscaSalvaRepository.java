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

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.BuscaSalva;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoConta;

@Repository
public class BuscaSalvaRepository extends AbstractCRUDRepository<BuscaSalva> {
	
	public BuscaSalvaRepository() {
		super(new BuscaSalva());
	}
	
	@SuppressWarnings("unchecked")
	public List<BuscaSalva> findContaAndTipoContaAndContaAtivaByUsuario(Conta conta, TipoConta[] tipoConta, Boolean contaAtiva, Usuario usuario) {
		StringBuilder hql = new StringBuilder();
		
		hql.append("FROM BuscaSalva busca WHERE ");
		
		repositoryUtil.validAndAddToHQL(hql, "busca.conta.id = :idConta AND ", conta);
		repositoryUtil.validAndAddToHQL(hql, "busca.conta.ativo = :contaAtiva AND ", contaAtiva);
		repositoryUtil.validAndAddToHQL(hql, "busca.conta.tipoConta IN (:tipoConta) AND ", tipoConta);
				
		hql.append("busca.conta.usuario.id = :idUsuario ORDER BY busca.descricao ASC");
		
		Query hqlQuery = getQuery(hql.toString());
		
		if (conta != null) {
			hqlQuery.setLong("idConta", conta.getId());
		}
		if (tipoConta != null && tipoConta.length != 0) {
			hqlQuery.setParameterList("tipoConta", tipoConta);
		}
		if (contaAtiva != null) {
			hqlQuery.setBoolean("contaAtiva", contaAtiva);
		}
		
		hqlQuery.setLong("idUsuario", usuario.getId());
		
		return hqlQuery.list();
	}
}