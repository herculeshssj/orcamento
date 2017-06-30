/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

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
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFechamentoPeriodo;

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
		
			// Busca todas as contas configuradas para fechamento automático
			List<Conta> contas = getContaService().buscarTodosUsuarioPorFechamentoAutomatico(true);
			
			// Itera as contas para determinar se ela pode ser fechada ou não. Se puder,
			// realiza o fechamento do período
			for (Conta conta : contas) {
				
				// Obtém a data atual
				Calendar dataAtual = Calendar.getInstance();
				
				// Define a data de fechamento
				Date dataFechamento = null;
				
				// Verifica se a conta já possui um fechamento existente
				FechamentoPeriodo fechamentoAtual = getFechamentoPeriodoService().buscarUltimoFechamentoConta(conta);
				if (fechamentoAtual == null) {
					// Usa a data de abertura da conta como referência
					dataFechamento = conta.getDataAbertura();
				} else {
					// Usa a data de fechamento do último período
					dataFechamento = fechamentoAtual.getData();
				}
				
				// Incrementa a data de fechamento de acordo com a periodicidade definida na conta
				dataFechamento = conta.getPeriodicidade().getDataPeriodo(dataFechamento);
				
				// Verifica se a data atual é posterior a data de fechamento
				if (dataAtual.getTime().after(dataFechamento)) {
					// Realiza o fechamento usando a data atual, decrementado em um dia
					dataAtual.add(Calendar.DAY_OF_YEAR, -1);
					// Executa o fechamento do período
					getFechamentoPeriodoService().fecharPeriodo(dataAtual.getTime(), conta);
					logger.info("Fechamento automático da conta " + conta.getLabel() + " realizada com sucesso!");
				} else {
					// Faz nada
				}
			}
			
		} catch (Exception e) {
			logger.catching(e);
			e.printStackTrace();
		}
	}
}
