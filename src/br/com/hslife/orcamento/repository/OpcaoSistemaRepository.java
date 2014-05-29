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

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoOpcaoSistema;

@Repository
@Transactional
public class OpcaoSistemaRepository extends AbstractRepository implements IRepository<OpcaoSistema>{
	
	public void save(OpcaoSistema entity) {
		getSession().persist(entity);
	}
	
	public void update(OpcaoSistema entity) {
		getSession().merge(entity);
	}
	
	public void delete(OpcaoSistema entity) {
		getSession().delete(entity);
	}
	
	public OpcaoSistema findById(Long id) {
		return (OpcaoSistema)getSession().get(OpcaoSistema.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<OpcaoSistema> findOpcoesGlobalAdminByCDU(String cdu) {
		Criteria criteria = getSession().createCriteria(OpcaoSistema.class);
		criteria.add(Restrictions.eq("tipoOpcaoSistema", TipoOpcaoSistema.GLOBAL_ADMIN));
		criteria.add(Restrictions.eq("casoDeUso", cdu));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<OpcaoSistema> findOpcoesGlobalAdmin() {
		Criteria criteria = getSession().createCriteria(OpcaoSistema.class);
		criteria.add(Restrictions.eq("tipoOpcaoSistema", TipoOpcaoSistema.GLOBAL_ADMIN));
		return criteria.list();
	}
	
	public OpcaoSistema findOpcaoGlobalAdminByChave(String chave) {
		Criteria criteria = getSession().createCriteria(OpcaoSistema.class);
		criteria.add(Restrictions.eq("tipoOpcaoSistema", TipoOpcaoSistema.GLOBAL_ADMIN));
		criteria.add(Restrictions.eq("chave", chave));
		return (OpcaoSistema)criteria.uniqueResult();
	}
	
	public OpcaoSistema findOpcaoUserByChave(String chave, Usuario usuario) {
		Criteria criteria = getSession().createCriteria(OpcaoSistema.class);
		criteria.add(Restrictions.eq("tipoOpcaoSistema", TipoOpcaoSistema.USER));
		criteria.add(Restrictions.eq("chave", chave));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return (OpcaoSistema)criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<OpcaoSistema> findOpcoesUser(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(OpcaoSistema.class);
		criteria.add(Restrictions.eq("tipoOpcaoSistema", TipoOpcaoSistema.USER));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<OpcaoSistema> findByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(OpcaoSistema.class);
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.list();
	}
}
