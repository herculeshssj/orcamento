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
package br.com.hslife.orcamento.service;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.LogRequisicao;
import br.com.hslife.orcamento.entity.Logs;
import br.com.hslife.orcamento.facade.ILog;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class LogServiceTest extends AbstractTestServices {
	
	private Logs logs;
	private LogRequisicao logRequisicao;
	
	@Autowired
	private ILog service;
	
	@Before
	public void initializeEntities() {
		// Inicializa as entidades
		logs = EntityInitializerFactory.createLog();
		logRequisicao = EntityInitializerFactory.createLogRequisicao();
		
		// Exclui todos os registros existentes
		service.excluirTodosLogs();
		service.excluirTodosLogRequisicao();
		
		// Salva as entidades pertinentes antes de iniciar os testes
		Calendar temp = Calendar.getInstance();
		temp.add(Calendar.DAY_OF_YEAR, -180);
		for (int i = 0; i < 1000; i++) {
			logRequisicao.setDataHora(temp.getTime());
			logs.setLogDate(temp.getTime());
			service.salvarLogRequisicao(logRequisicao);
			service.salvarLogs(logs);
		}
	}
	
	@Test
	public void testFindByID() {
		List<Logs> logs = service.buscarTodosLogs();
		Logs logsTest = logs.get(0);
		
		this.logs = service.buscarPorID(logsTest.getId());
		assertEquals(logsTest, this.logs);
	}
	
	@Test
	public void testFindAllLevels() {
		List<String> levels = service.buscarTodosNiveis();
		assertEquals(1, levels.size());
	}
	
	@Test
	public void testFindAllLoggers() {
		List<String> levels = service.buscarTodosLoggers();
		assertEquals(1, levels.size());
	}
	
	@Test
	public void testFindMostRecentException() {
		Logs logsTest = service.buscarExcecaoMaisRecente();
		assertEquals("ERROR", logsTest.getLogLevel());
		assertEquals(false, logsTest.isSendToAdmin());
	}
 	
	@Test
	public void testExcluirLogs() {
		Calendar temp = Calendar.getInstance();
		temp.add(Calendar.DAY_OF_YEAR, -180);
		
		service.excluirLogs(temp);
		
		assertEquals(0, service.buscarTodosLogs().size());
		
		service.excluirLogRequisicao(temp);
		
		assertEquals(0, service.buscarTodosLogRequisicao().size());
	}
}