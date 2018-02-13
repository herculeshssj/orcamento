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

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth

	Floor, Boston, MA  02110-1301, USA.


	Para mais informações sobre o programa Orçamento Doméstico e seu autor

	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

	para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/package br.com.hslife.orcamento.repository;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoOpcaoSistema;

@Repository
public class OpcaoSistemaRepository extends AbstractCRUDRepository<OpcaoSistema> {
	
	public OpcaoSistemaRepository() {
		super(new OpcaoSistema());
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
	public List<OpcaoSistema> findOpcoesUserByCasoUso(String casoDeUso, Usuario usuario) {
		Criteria criteria = getSession().createCriteria(OpcaoSistema.class);
		criteria.add(Restrictions.eq("tipoOpcaoSistema", TipoOpcaoSistema.USER));
		criteria.add(Restrictions.eq("casoDeUso", casoDeUso));
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.list();
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
