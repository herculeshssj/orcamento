/***

Copyright (c) 2012 - 2021 Hércules S. S. José

Este arquivo é parte do programa Orçamento Doméstico.


Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

Licença.


Este programa é distribuído na esperança que possa ser útil, mas SEM

NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer

MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor

GNU em português para maiores detalhes.


Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob

o nome de "LICENSE" junto com este programa, se não, acesse o site do

projeto no endereco https://github.com/herculeshssj/orcamento ou escreva

para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor,

Boston, MA  02110-1301, USA.


Para mais informações sobre o programa Orçamento Doméstico e seu autor

entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.repository;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.CategoriaInvestimento;
import br.com.hslife.orcamento.enumeration.TipoInvestimento;

@Repository
public class CategoriaInvestimentoRepository extends AbstractCRUDRepository<CategoriaInvestimento> {
	
	public CategoriaInvestimentoRepository() {
		super(new CategoriaInvestimento());
	}
	
	@SuppressWarnings("unchecked")
	public List<CategoriaInvestimento> findByDescricaoAndTipoAndAtivo(String descricao, TipoInvestimento tipo, Boolean ativo) {
		StringBuilder hql = new StringBuilder();
		hql.append("FROM CategoriaInvestimento categoria WHERE 1=1 ");
		if (tipo != null) {
			hql.append("AND categoria.tipoInvestimento = :tipo ");
		}
		if (descricao != null && !descricao.isEmpty()) {
			hql.append("AND categoria.descricao LIKE '%");
			hql.append(descricao);
			hql.append("%' ");
		}
		if (ativo != null) {
			hql.append("AND categoria.ativo = :ativo ");
		}
		
		hql.append("ORDER BY categoria.descricao ASC");
		
		Query hqlQuery = getQueryNoType(hql.toString());
		if (tipo != null) {
			hqlQuery.setParameter("tipo", tipo);
		}
		if (ativo != null) {
			hqlQuery.setParameter("ativo", ativo);
		}
		
		return hqlQuery.list();
	}
}
