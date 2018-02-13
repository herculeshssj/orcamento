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
package br.com.hslife.orcamento.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.component.OpcaoSistemaComponent;
import br.com.hslife.orcamento.enumeration.PeriodoLogs;
import br.com.hslife.orcamento.exception.ApplicationException;

@Component
@Transactional(propagation=Propagation.REQUIRED, isolation=Isolation.DEFAULT, rollbackFor={ApplicationException.class}, readOnly=false)
public class DeleteLogTask {
	
	private static final Logger logger = LogManager.getLogger(DeleteLogTask.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private OpcaoSistemaComponent component;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public OpcaoSistemaComponent getComponent() {
		return component;
	}

	@Scheduled(fixedDelay=3600000)
	public void excluirLogs() {
		try {
			// Recupera o período setado nas opções do sistema
			PeriodoLogs periodo = getComponent().getLogPeriodo();
			
			// Recupera a quantidade do período
			Integer quantidade = getComponent().getLogNumPeriodo();
			
			StatelessSession session = getSessionFactory().openStatelessSession();
			// FIXME mover as consultas para o repositório. Aqui no Task, injetar o LogService para realizar esta operação
			// FIXME atualizar as interfaces. Query e Criteria estão depreciadas
			// Definição dos deletes
			Query hqlAuditoria = session.createQuery("DELETE FROM Auditoria auditoria WHERE auditoria.dataHora <= :periodo");
			Query hqlLogs = session.createQuery("DELETE FROM Logs log WHERE log.logDate <= :periodo");
			Query hqlLogRequisicao = session.createQuery("DELETE FROM LogRequisicao log WHERE log.dataHora <= :periodo");
			Query hqlResultadoScript = session.createQuery("DELETE FROM ResultadoScript script WHERE script.terminoExecucao <= :periodo");
			
			// Exclui o log de auditoria
			hqlAuditoria
				.setDate("periodo", periodo.getDataPeriodo(quantidade))
				.executeUpdate();
			
			// Exclui os logs de erros
			hqlLogs
				.setDate("periodo", periodo.getDataPeriodo(quantidade))
				.executeUpdate();
			
			// Exclui os logs de requisição
			hqlLogRequisicao
				.setDate("periodo", periodo.getDataPeriodo(quantidade))
				.executeUpdate();
			
			// Exclui os logs de execução dos scripts
			hqlResultadoScript
				.setDate("periodo", periodo.getDataPeriodo(quantidade))
				.executeUpdate();
			
		} catch (Exception e) {
			logger.catching(e);
			e.printStackTrace();
		}
	}
}
