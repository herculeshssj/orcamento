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
package br.com.hslife.orcamento.task;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.component.AbstractTestComponents;
import br.com.hslife.orcamento.entity.LogRequisicao;
import br.com.hslife.orcamento.entity.Logs;
import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.enumeration.PeriodoLogs;
import br.com.hslife.orcamento.facade.ILog;
import br.com.hslife.orcamento.facade.IOpcaoSistema;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class DeleteLogTaskTest extends AbstractTestComponents {
	
	@Autowired
	private DeleteLogTask task;
	
	@Autowired
	private ILog logService;
	
	@Autowired
	private IOpcaoSistema opcaoSistemaService;
	
	private Logs logs;
	private LogRequisicao logRequisicao;

	private void initializeEntities() {
		// Exclui todos os registros existentes
		logService.excluirTodosLogs();
		logService.excluirTodosLogRequisicao();
		
		// Salva as entidades pertinentes antes de iniciar os testes
		Calendar temp = Calendar.getInstance();
		temp.add(Calendar.MONTH, -6);
		for (int i = 0; i < 1000; i++) {
			// Inicializa as entidades
			logs = EntityInitializerFactory.createLog();
			logRequisicao = EntityInitializerFactory.createLogRequisicao();
			
			logRequisicao.setDataHora(temp.getTime());
			logs.setLogDate(temp.getTime());
			
			logService.salvarLogRequisicao(logRequisicao);
			logService.salvarLogs(logs);
		}
	} 
	
	@Test
	public void testExcluirLogs() {
		this.initializeEntities();
		
		// Seta o período de log
		OpcaoSistema opcaoPeriodo = opcaoSistemaService.buscarOpcaoGlobalAdminPorChave("LOGS_PERIODO");
		OpcaoSistema opcaoQuantidade = opcaoSistemaService.buscarOpcaoGlobalAdminPorChave("LOGS_NUM_PERIODO");
		opcaoPeriodo.setValor("MES");
		opcaoQuantidade.setValor("6");
		opcaoSistemaService.salvarOpcaoSistema(opcaoPeriodo);
		opcaoSistemaService.salvarOpcaoSistema(opcaoQuantidade);
		
		// Realiza a exclusão dos logos
		task.excluirLogs();
		
		// Verifica se os logs foram excluídos
		assertEquals(0, logService.buscarTodosLogs().size());
		assertEquals(0, logService.buscarTodosLogRequisicao().size());
	}
	
	@Test
	public void testPeriodoDia() {
		Calendar temp = Calendar.getInstance();
		Calendar tempTest = (Calendar) temp.clone();
		temp.add(Calendar.DAY_OF_YEAR, -1);
		assertEquals(temp, PeriodoLogs.DIA.getPeriodo(1, tempTest));
	}
	
	@Test
	public void testPeriodoMes() {
		Calendar temp = Calendar.getInstance();
		Calendar tempTest = (Calendar) temp.clone();
		temp.add(Calendar.MONTH, -1);
		assertEquals(temp, PeriodoLogs.MES.getPeriodo(1, tempTest));
	}
	
	@Test
	public void testPeriodoAno() {
		Calendar temp = Calendar.getInstance();
		Calendar tempTest = (Calendar) temp.clone();
		temp.add(Calendar.YEAR, -1);
		assertEquals(temp, PeriodoLogs.ANO.getPeriodo(1, tempTest));
	}
	
	@Test
	public void testPeriodoBimestre() {
		Calendar temp = Calendar.getInstance();
		Calendar tempTest = (Calendar) temp.clone();
		temp.add(Calendar.MONTH, -2);
		assertEquals(temp, PeriodoLogs.BIMESTRE.getPeriodo(1, tempTest));
	}
	
	@Test
	public void testPeriodoTrimestre() {
		Calendar temp = Calendar.getInstance();
		Calendar tempTest = (Calendar) temp.clone();
		temp.add(Calendar.MONTH, -3);
		assertEquals(temp, PeriodoLogs.TRIMESTRE.getPeriodo(1, tempTest));
	}
	
	@Test
	public void testPeriodoQuadrimestre() {
		Calendar temp = Calendar.getInstance();
		Calendar tempTest = (Calendar) temp.clone();
		temp.add(Calendar.MONTH, -4);
		assertEquals(temp, PeriodoLogs.QUADRIMESTRE.getPeriodo(1, tempTest));
	}
	
	@Test
	public void testPeriodoSemestre() {
		Calendar temp = Calendar.getInstance();
		Calendar tempTest = (Calendar) temp.clone();
		temp.add(Calendar.MONTH, -6);
		assertEquals(temp, PeriodoLogs.SEMESTRE.getPeriodo(1, tempTest));
	}
}