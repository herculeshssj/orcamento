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

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.LogRequisicao;
import br.com.hslife.orcamento.entity.Logs;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class LogRepositoryTest extends AbstractTestRepositories {
	
	private Logs logs;
	private LogRequisicao logRequisicao;
	
	@Autowired
	private LogRepository repository;
	
	@Before
	public void initializeEntities() {
		// Seta o sessionFactory nos repositórios
		repository.setSessionFactory(getSessionFactory());
		
		// Inicializa as entidades
		logs = EntityInitializerFactory.createLog();
		logRequisicao = EntityInitializerFactory.createLogRequisicao();
		
		// Exclui todos os registros existentes
		repository.deleteAllLogs();
		repository.deleteAllLogRequisicao();
		
		// Salva as entidades pertinentes antes de iniciar os testes
		Calendar temp = Calendar.getInstance();
		temp.add(Calendar.DAY_OF_YEAR, -180);
		for (int i = 0; i < 1000; i++) {
			logRequisicao.setDataHora(temp.getTime());
			logs.setLogDate(temp.getTime());
			repository.saveLog(logRequisicao);
			repository.saveLogs(logs);
		}
	}
	
	@Test
	public void testFindByID() {
		List<Logs> logs = repository.findAllLogs();
		Logs logsTest = logs.get(0);
		
		this.logs = repository.findByID(logsTest.getId());
		assertEquals(logsTest, this.logs);
	}
	
	@Test
	public void testFindAllLevels() {
		List<String> levels = repository.findAllLevel();
		assertEquals(1, levels.size());
	}
	
	@Test
	public void testFindAllLoggers() {
		List<String> levels = repository.findAllLogger();
		assertEquals(1, levels.size());
	}
	
	@Test
	public void testFindMostRecentException() {
		Logs logsTest = repository.findMostRecentException();
		assertEquals("ERROR", logsTest.getLogLevel());
		assertEquals(false, logsTest.isSendToAdmin());
	}
 	
	@Test
	public void testExcluirLogs() {
		Calendar temp = Calendar.getInstance();
		temp.add(Calendar.DAY_OF_YEAR, -180);
		
		repository.deleteLogs(temp);
		
		assertEquals(0, repository.findAllLogs().size());
		
		repository.deleteLogRequisicao(temp);
		
		assertEquals(0, repository.findAllLogRequisicao().size());
	}
}