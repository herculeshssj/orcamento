/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a qualquer
    
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.entity.Logs;
import br.com.hslife.orcamento.facade.ILog;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.util.Util;

@Component
@Transactional(propagation=Propagation.SUPPORTS)
public class SendLogByMailTask {
	
	private static final Logger logger = LogManager.getLogger(SendLogByMailTask.class);
	
	@Autowired
	private IUsuario usuarioService;
	
	@Autowired
	private ILog logService;

	public IUsuario getUsuarioService() {
		return usuarioService;
	}

	public ILog getLogService() {
		return logService;
	}

	@Scheduled(fixedDelay=3600000)
	public void enviarExcecaoParaAdmin() {
		try {
			Logs logs = getLogService().buscarExcecaoMaisRecente();
			if (logs != null) {
				StringBuilder mensagem = new StringBuilder();
				mensagem.append("Ocorreu o seguinte erro no sistema:\n\n");
				mensagem.append("Data: " + Util.formataDataHora(logs.getLogDate(), Util.DATAHORA) + "\n\n");
				mensagem.append("Logger: " + logs.getLogger() + "\n\n");
				mensagem.append("Mensagem: " + logs.getLogMessage() + "\n\n");
				mensagem.append("Exceção disparada:\n\n\n");
				mensagem.append(logs.getLogException() + "\n\n\n");
				mensagem.append("Verifique os logs do sistema e do servidor de aplicação para saber a origem do comportamento inesperado.");
				
				getUsuarioService().enviarMensagemParaAdmin("HSlife Serviços de TI", "postmaster@mail.hslife.com.br", "Orçamento Doméstico - Erro no sistema", mensagem.toString());
				
				// Seta o log como enviado para o admin e salva as alterações
				logs.setSendToAdmin(true);
				getLogService().alterar(logs);
			}
		} catch (Exception e) {
			logger.catching(e);
			e.printStackTrace();
		}
	}
}
