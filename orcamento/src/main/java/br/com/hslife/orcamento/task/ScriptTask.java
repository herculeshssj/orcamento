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

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.component.OpcaoSistemaComponent;
import br.com.hslife.orcamento.entity.ResultadoScript;
import br.com.hslife.orcamento.entity.Script;
import br.com.hslife.orcamento.repository.ResultadoScriptRepository;
import br.com.hslife.orcamento.repository.ScriptRepository;
import groovy.lang.GroovyShell;

@Component
@Transactional(propagation=Propagation.REQUIRED)
public class ScriptTask {
	
	private static final Logger logger = LogManager.getLogger(ScriptTask.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private OpcaoSistemaComponent component;
	
	@Autowired
	private ScriptRepository repository;
	
	@Autowired
	private ResultadoScriptRepository resultadoScriptRepository;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public OpcaoSistemaComponent getComponent() {
		return component;
	}

	public ScriptRepository getRepository() {
		this.repository.setSessionFactory(sessionFactory);
		return repository;
	}

	public ResultadoScriptRepository getResultadoScriptRepository() {
		this.resultadoScriptRepository.setSessionFactory(sessionFactory);
		return resultadoScriptRepository;
	}

	@Scheduled(fixedDelay=3600000)
	public void executarScripts() {
		try {
			
			logger.debug("Agendador das rotinas automatizadas pronto.");
			
			// Obtém todos os scripts ativos e itera a lista de scripts, executando cada um em sequência
			for (Script script : getRepository().findByNomeAndAtivo("", true)) {
				
				logger.debug("Executando o script '" + script.getNome() + "'...");
				
				// Instancia um novo objeto ResultadoScript
				ResultadoScript resultadoScript = new ResultadoScript();
				
				// Seta os atributos antes da execução
				resultadoScript.setScript(script);
				resultadoScript.setInicioExecucao(new Date());
				
				try {
					GroovyShell shell = new GroovyShell();
					Object resultado = shell.evaluate(script.getScript());
					resultadoScript.setResultado(resultado.toString());
				} catch (Throwable t) {
					resultadoScript.setResultado(t.getMessage());
				}
				
				// Seta o tempo gasto após a execução
				resultadoScript.setTerminoExecucao(new Date());
				
				// Grava o resultado da execução do script
				getResultadoScriptRepository().save(resultadoScript);
			
				logger.debug("Execução do script '" + script.getNome() + "' concluída.");
			}
			
			logger.debug("Concluído a execução das rotinas automatizadas.");
			
		} catch (Exception e) {
			logger.catching(e);
			e.printStackTrace();
		}
	}


}
