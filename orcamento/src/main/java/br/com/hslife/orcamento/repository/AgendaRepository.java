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

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Agenda;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.model.CriterioAgendamento;

@Repository
public class AgendaRepository extends AbstractCRUDRepository<Agenda> {
	
	public AgendaRepository() {
		super(new Agenda());
	}
	
	public List<Agenda> findByUsuario(Usuario usuario) {
		Criteria criteria = getSession().createCriteria(Agenda.class);
		criteria.add(Restrictions.eq("usuario.id", usuario.getId()));
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	public List<Agenda> findByCriterioAgendamento(CriterioAgendamento criterioBusca) {
		Criteria criteria = getSession().createCriteria(Agenda.class);
		
		if (criterioBusca.getDescricao() != null && !criterioBusca.getDescricao().trim().isEmpty()) {
			criteria.add(Restrictions.ilike("descricao", criterioBusca.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if (criterioBusca.getInicio() != null) {
			criteria.add(Restrictions.ge("inicio", criterioBusca.getInicio()));
		}
		
		if (criterioBusca.getFim() != null) {
			criteria.add(Restrictions.le("fim", criterioBusca.getFim()));
		}
		
		if (criterioBusca.getTipo() != null) {
			criteria.add(Restrictions.eq("tipoAgendamento", criterioBusca.getTipo()));
		}
		
		if (criterioBusca.getUsuario() != null) {
			criteria.add(Restrictions.eq("usuario.id", criterioBusca.getUsuario().getId()));
		}
		
		return criteria.addOrder(Order.asc("descricao")).list();
	}
	
	public long countAgendamentoByDataInicioAndDataFimAndAlertaAndUsuario(Date inicio, Date fim, boolean emiteAlerta, Usuario usuario) {
		return (Long)getQueryNoType("SELECT COUNT(*) FROM Agenda agenda WHERE agenda.inicio >= :inicio AND agenda.fim <= :fim AND agenda.emitirAlerta = :alerta AND agenda.usuario.id = :idUsuario")
				.setTimestamp("inicio", inicio)
				.setTimestamp("fim", fim)
				.setBoolean("alerta", emiteAlerta)
				.setLong("idUsuario", usuario.getId())
				.uniqueResult();
	}
	
	public long countAgendamentoByDataInicioOrDataFimAndAlerta(Date inicio, Date fim, boolean emiteAlerta, Usuario usuario) {
		return (Long)getQueryNoType("SELECT COUNT(*) FROM Agenda agenda WHERE ((agenda.inicio BETWEEN :inicio AND :fim) OR (agenda.fim BETWEEN :inicio AND :fim)) AND agenda.emitirAlerta = :alerta AND agenda.usuario.id = :idUsuario")
				.setTimestamp("inicio", inicio)
				.setTimestamp("fim", fim)
				.setBoolean("alerta", emiteAlerta)
				.setLong("idUsuario", usuario.getId())
				.uniqueResult();
	}
	
	public List<Agenda> findAgendamentoByDataInicioAndDataFimAndAlerta(Date inicio, Date fim, boolean emiteAlerta) {
		return (List<Agenda>)getQueryNoType("FROM Agenda agenda WHERE agenda.inicio >= :inicio AND agenda.fim <= :fim AND agenda.emitirAlerta = :alerta")
				.setTimestamp("inicio", inicio)
				.setTimestamp("fim", fim)
				.setBoolean("alerta", emiteAlerta)
				.list();
	}
	
	public List<Agenda> findAgendamentoByOrDataInicioOrDataFimAndAlerta(Date inicio, Date fim, boolean emiteAlerta) {
		return (List<Agenda>)getQueryNoType("FROM Agenda agenda WHERE ((agenda.inicio BETWEEN :inicio AND :fim) OR (agenda.fim BETWEEN :inicio AND :fim)) AND agenda.emitirAlerta = :alerta")
				.setTimestamp("inicio", inicio)
				.setTimestamp("fim", fim)
				.setBoolean("alerta", emiteAlerta)
				.list();
	}	
}
