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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFechamentoPeriodo;
import br.com.hslife.orcamento.util.Util;

@Component
@Transactional(propagation=Propagation.SUPPORTS)
public class FechamentoAutomaticoContaTask {
	
	private static final Logger logger = LogManager.getLogger(FechamentoAutomaticoContaTask.class);
	
	@Autowired
	private IFechamentoPeriodo fechamentoPeriodoService;
	
	@Autowired
	private IConta contaService;

	public IFechamentoPeriodo getFechamentoPeriodoService() {
		return fechamentoPeriodoService;
	}

	public IConta getContaService() {
		return contaService;
	}

	@Scheduled(fixedDelay=3600000)
	public void fecharPeriodo() {
		try {
			
			/*** Os períodos são fechados todo dia 01 de cada mês. ***/
		
			// Busca todas as contas configuradas para fechamento automático
			List<Conta> contas = getContaService().buscarTodosUsuarioPorFechamentoAutomatico(true);
			
			// Itera as contas para determinar se ela pode ser fechada ou não. Se puder,
			// realiza o fechamento do período
			for (Conta conta : contas) {
				
				// Obtém a data atual
				Calendar dataAtual = Calendar.getInstance();
				
				// Verifica se é o primeiro dia do mês
				if (dataAtual.get(Calendar.DAY_OF_MONTH) == 1) {
					
					// Decrementa em um dia a data atual
					dataAtual.add(Calendar.DAY_OF_YEAR, -1);
					
					// Declara as datas de início e término do fechamento
					Date dataFimFechamento = dataAtual.getTime();
					Date dataInicioFechamento = Util.primeiroDiaMes(dataFimFechamento);
					
					// Executa o fechamento para a conta selecionada
					getFechamentoPeriodoService().fecharPeriodo(conta, dataInicioFechamento, dataFimFechamento);
				
					// Registra o sucesso da operação no log
					logger.info("Fechamento automático da conta " + conta.getLabel() + " realizado com sucesso!");
				}
			}
			
		} catch (Exception e) {
			logger.catching(e);
			e.printStackTrace();
		}
	}
}
