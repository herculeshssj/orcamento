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

import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Investimento;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoInvestimento;

@Repository
public class InvestimentoRepository extends AbstractCRUDRepository<Investimento> {
	
	public InvestimentoRepository() {
		super(new Investimento());
	}
	
	@SuppressWarnings("unchecked")
	public List<Investimento> findByUsuario(Usuario usuario) {
		return getQuery("FROM Investimento investimento WHERE investimento.usuario.id = :idUsuario")
				.setLong("idUsuario", usuario.getId())
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<Investimento> findByTipoInvestimentoAndUsuario(TipoInvestimento tipoInvestimento, Usuario usuario) {
		return getQuery("FROM Investimento investimento WHERE investimento.tipoInvestimento = :tipoInvestimento AND investimento.usuario.id = :idUsuario")
				.setLong("idUsuario", usuario.getId())
				.setParameter("tipoInvestimento", tipoInvestimento)
				.list();
	}
}