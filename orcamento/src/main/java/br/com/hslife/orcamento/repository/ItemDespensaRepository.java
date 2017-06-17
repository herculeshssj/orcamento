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

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Despensa;
import br.com.hslife.orcamento.entity.ItemDespensa;
import br.com.hslife.orcamento.entity.UnidadeMedida;
import br.com.hslife.orcamento.entity.Usuario;

@Repository
public class ItemDespensaRepository extends AbstractCRUDRepository<ItemDespensa> {
	
	public ItemDespensaRepository() {
		super(new ItemDespensa());
	}
	
	@Override
	public ItemDespensa findById(Long id) {
		Criteria criteria = getSession().createCriteria(ItemDespensa.class).setFetchMode("movimentacao", FetchMode.JOIN);
		criteria.add(Restrictions.eq("id", id));
		return (ItemDespensa)criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemDespensa> findByDespensaUsuarioAndArquivado(Despensa despensa, Usuario usuario, boolean arquivado) {
		String hql = "select item from ItemDespensa as item inner join item.despensa as des where des.id = :idDespensa and des.usuario.id = :idUsuario and item.arquivado = :arquivado order by item.descricao asc";
		Query query = getSession().createQuery(hql);
		query.setLong("idDespensa", despensa.getId());
		query.setLong("idUsuario", usuario.getId());
		query.setBoolean("arquivado", arquivado);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemDespensa> findByUsuarioAndArquivado(Usuario usuario, boolean arquivado) {
		String hql = "select item from ItemDespensa as item inner join item.despensa as des where des.usuario.id = :idUsuario and item.arquivado = :arquivado order by item.descricao asc";
		Query query = getSession().createQuery(hql);
		query.setLong("idUsuario", usuario.getId());
		query.setBoolean("arquivado", arquivado);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemDespensa> findAllPerecivelByUsuario(Usuario usuario) {
		String hql = "select item from ItemDespensa as item inner join item.despensa as des where des.usuario.id = :idUsuario and item.perecivel = :perecivel and item.arquivado = :arquivado order by item.descricao asc";
		Query query = getSession().createQuery(hql);
		query.setLong("idUsuario", usuario.getId());
		query.setBoolean("perecivel", true);
		query.setBoolean("arquivado", false);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemDespensa> findByDespensa(Despensa despensa) {
		Criteria criteria = getSession().createCriteria(ItemDespensa.class);
		criteria.add(Restrictions.eq("despensa.id", despensa.getId()));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemDespensa> findByUnidadeMedida(UnidadeMedida unidadeMedida) {
		Criteria criteria = getSession().createCriteria(ItemDespensa.class);
		criteria.add(Restrictions.eq("unidadeMedida.id", unidadeMedida.getId()));
		return criteria.list();
	}
}
