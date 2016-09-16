/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

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

import br.com.hslife.orcamento.entity.DividaTerceiro;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusDivida;
import br.com.hslife.orcamento.enumeration.TipoCategoria;

@Repository
public class DividaTerceiroRepository extends AbstractCRUDRepository<DividaTerceiro> {
	
	public DividaTerceiroRepository() {
		super(new DividaTerceiro());
	}
	
	@SuppressWarnings("unchecked")
	public List<DividaTerceiro> findFavorecidoOrTipoCategoriaOrStatusDividaByUsuario(Favorecido favorecido, TipoCategoria tipoCategoria, StatusDivida statusDivida, Usuario usuario) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM DividaTerceiro divida WHERE ");
		
		if (favorecido != null) {
			hql.append("divida.favorecido.id = :idFavorecido AND ");
		}
		
		if (tipoCategoria != null) {
			hql.append("divida.tipoCategoria = :tipo AND ");
		}
		
		if (statusDivida != null) {
			hql.append("divida.statusDivida = :status AND ");
		}
		
		hql.append("divida.usuario.id = :idUsuario ORDER BY divida.dataNegociacao DESC");
		
		Query hqlQuery = getQuery(hql.toString());
		
		if (favorecido != null) {
			hqlQuery.setLong("idFavorecido", favorecido.getId());
		}
		
		if (tipoCategoria != null) {
			hqlQuery.setParameter("tipo", tipoCategoria);
		}
		
		if (statusDivida != null) {
			hqlQuery.setParameter("status", statusDivida);
		}
		
		hqlQuery.setLong("idUsuario", usuario.getId());
		
		return hqlQuery.list();
	}
}