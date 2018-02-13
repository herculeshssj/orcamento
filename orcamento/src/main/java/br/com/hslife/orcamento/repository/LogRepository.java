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

***/
package br.com.hslife.orcamento.repository;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.LogRequisicao;
import br.com.hslife.orcamento.entity.Logs;
import br.com.hslife.orcamento.model.CriterioLog;
import br.com.hslife.orcamento.model.UsuarioLogado;

@Repository
public class LogRepository extends AbstractRepository {
	
	@SuppressWarnings("unchecked")
	public List<Logs> findByCriteriosLog(CriterioLog criterioBusca) {
		Criteria criteria = getSession().createCriteria(Logs.class);
		
		for (Criterion criterion : criterioBusca.buildCriteria()) {
			criteria.add(criterion);
		}
		
		return criteria.addOrder(Order.desc("logDate")).list();
	}
	
	public Logs findByID(Long id) {
		return (Logs)getQuery("FROM Logs log WHERE log.id = :idLog")
				.setLong("idLog", id)
				.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findAllLevel() {
		return getQuery("SELECT DISTINCT log.logLevel FROM Logs log").list();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findAllLogger() {
		return getQuery("SELECT DISTINCT log.logger FROM Logs log").list();
	}
	
	public void delete(Logs entity) {
		getSession().delete(entity);
	}
	
	public void update(Logs entity) {
		getSession().update(entity);
	}
	
	public void saveLog(LogRequisicao entity) {
		getSession().save(entity);
	}
	
	public Logs findMostRecentException() {
		return (Logs)getQuery("FROM Logs log WHERE log.logLevel = 'ERROR' AND log.sendToAdmin = false ORDER BY log.logDate DESC").setMaxResults(1).uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<UsuarioLogado> findAllUsuarioLogado() {
		// Data atual menos 30 minutos
		Calendar dataAtual = Calendar.getInstance();
		dataAtual.add(Calendar.MINUTE, -30);
		
		return getQuery("SELECT DISTINCT log.usuario as usuario, log.ip as ip, log.sessaoCriadaEm as dataEntrada, log.sessaoID as sessaoID "
				+ "FROM LogRequisicao log WHERE log.dataHora >= :dataEntrada AND log.usuario <> ''")
				.setDate("dataEntrada", dataAtual.getTime())
				.setResultTransformer(new AliasToBeanResultTransformer(UsuarioLogado.class))
				.list();
	}
}
