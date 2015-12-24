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
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.RelatorioCustomizado;
import br.com.hslife.orcamento.entity.Usuario;

@Repository
public class RelatorioCustomizadoRepository extends AbstractCRUDRepository<RelatorioCustomizado> {
	
	public RelatorioCustomizadoRepository() {
		super(new RelatorioCustomizado());
	}
	
	@SuppressWarnings("unchecked")
	public List<RelatorioCustomizado> findNomeByUsuario(String nome, Usuario usuario) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM RelatorioCustomizado relatorio WHERE ");
		if (nome != null && !nome.isEmpty()) {
			hql.append("relatorio.nome LIKE '%");
			hql.append(nome);
			hql.append("%' AND ");
		}
		
		hql.append("relatorio.usuario.id = :idUsuario ORDER BY relatorio.nome ASC");
		
		Query hqlQuery = getQuery(hql.toString());
		
		hqlQuery.setParameter("idUsuario", usuario.getId());
		
		return hqlQuery.list();
	}
	
	/*
	 * Executa via native SQL a consulta
	 */
	@SuppressWarnings("unchecked")
	public List<List<Object>> executeCustomNativeSQL(String nativeSQL) {
		List<List<Object>> rowList = getSession().createSQLQuery(nativeSQL).setResultTransformer(Transformers.TO_LIST).list();
		return rowList;
	}
}