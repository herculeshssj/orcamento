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
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Banco;
import br.com.hslife.orcamento.entity.Usuario;

@Repository
public class BancoRepository extends AbstractCRUDRepository<Banco> {
	
	public BancoRepository() {
		super(new Banco());
	}
	
	@SuppressWarnings("unchecked")
	public List<Banco> findByNomeAndUsuario(String nome, Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Banco.class);
		criteria.add(Restrictions.ilike("nome", nome, MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("nome")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Banco> findByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Banco.class);
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("nome")).list();
	}
	
	public void updateAllToNotDefault(Usuario usuario) {
		String sql = "update banco set padrao = false where idUsuario = " + usuario.getId();
		
		Query query = getSession().createSQLQuery(sql);
		
		query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public Banco findDefaultByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Banco.class);
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		criteria.add(Restrictions.eq("padrao", true));
		List<Banco> resultado = criteria.list();
		if (resultado != null && resultado.size() >= 1) {
			return resultado.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Banco> findByNomeUsuarioAndAtivo(String nome, Usuario usuario, boolean ativo) {
		String hql = "FROM Banco banco WHERE banco.nome LIKE '%" + nome + "%' AND banco.usuario.id = :idUsuario AND banco.ativo = :ativo ORDER BY banco.nome ASC";
		Query query = getSession().createQuery(hql).setLong("idUsuario", usuario.getId()).setBoolean("ativo", ativo);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Banco> findActiveByUsuario(Usuario usuario) {
		return getQuery("FROM Banco banco WHERE banco.usuario.id = :idUsuario AND banco.ativo = true ORDER BY banco.nome ASC")
				.setLong("idUsuario", usuario.getId())
				.list();
	}
}