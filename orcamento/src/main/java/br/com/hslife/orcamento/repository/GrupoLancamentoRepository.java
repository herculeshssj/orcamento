/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor
	
	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

    para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 - 
	
	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/

package br.com.hslife.orcamento.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.GrupoLancamento;
import br.com.hslife.orcamento.entity.Usuario;

@Repository
public class GrupoLancamentoRepository extends AbstractCRUDRepository<GrupoLancamento> {
	
	public GrupoLancamentoRepository() {
		super(new GrupoLancamento());
	}
	
	@SuppressWarnings("unchecked")
	public List<GrupoLancamento> findAllDescricaoAndAtivoByUsuario(String descricao, Boolean ativo, Usuario usuario) {
		hqlParameters.clear();
		
		StringBuilder hql = new StringBuilder().append("FROM GrupoLancamento grupo WHERE 1=1");
		
		if (descricao != null && !descricao.isEmpty()) {
			hql.append(" AND grupo.descricao LIKE :descricao");
			hqlParameters.put("descricao", "%"+descricao.toLowerCase()+"%");
		}
		
		if (ativo != null) {
			hql.append(" AND grupo.ativo = :ativo");
			hqlParameters.put("ativo", ativo);
		}
		
		hql.append(" AND grupo.usuario.id = :idUsuario ORDER BY grupo.descricao ASC");
		hqlParameters.put("idUsuario", usuario.getId());
		
		return getQueryApplyingParameters(hql.toString()).list();
	}
}