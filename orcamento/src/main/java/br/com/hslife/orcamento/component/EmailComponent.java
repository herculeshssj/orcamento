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
package br.com.hslife.orcamento.component;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.facade.IOpcaoSistema;

@Component
public class EmailComponent {
	
	private static final Logger logger = LogManager.getLogger(EmailComponent.class);
	
	private String servidor;
	private Integer porta;
	private String usuario;
	private String senha;
	private String remetente;
	private String emailRemetente;
	private String destinatario;
	private String emailDestinatario;
	private String assunto;
	private String mensagem;
	private boolean usarSSL;
	private String charset;
	private String metodoEnvio;
	private String apiKey;
	
	@Autowired
	private IOpcaoSistema opcaoSistemaService;
	
	public IOpcaoSistema getOpcaoSistemaService() {
		return opcaoSistemaService;
	}

	public void setRemetente(String remetente) {
		this.remetente = remetente;
	}

	public void setEmailRemetente(String emailRemetente) {
		this.emailRemetente = emailRemetente;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

	public void setEmailDestinatario(String emailDestinatario) {
		this.emailDestinatario = emailDestinatario;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	/*** Nova forma de construir uma classe para envio de e-mail 
	 * 
	 * 
	 * 
	 * // using SendGrid's Java Library
// https://github.com/sendgrid/sendgrid-java
import com.sendgrid.*;
import java.io.IOException;

public class Example {
  public static void main(String[] args) throws IOException {
    Email from = new Email("test@example.com");
    String subject = "Sending with SendGrid is Fun";
    Email to = new Email("test@example.com");
    Content content = new Content("text/plain", "and easy to do anywhere, even with Java");
    Mail mail = new Mail(from, subject, to, content);

    SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
    Request request = new Request();
    try {
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      Response response = sg.api(request);
      System.out.println(response.getStatusCode());
      System.out.println(response.getBody());
      System.out.println(response.getHeaders());
    } catch (IOException ex) {
      throw ex;
    }
  }
}
	 * @throws ApplicationException
	 */
	
	private void populateParameters() throws ApplicationException {
		Map<String, Object> parametros = getOpcaoSistemaService().buscarOpcoesGlobalAdminPorCDU("email");
		servidor = (String)parametros.get("EMAIL_SERVIDOR");
		porta = Integer.valueOf((String)parametros.get("EMAIL_PORTA"));
		usuario = (String)parametros.get("EMAIL_USUARIO");
		senha = (String)parametros.get("EMAIL_SENHA");
		usarSSL = (Boolean)parametros.get("EMAIL_USAR_SSL");
		charset = (String)parametros.get("EMAIL_CHARSET");
		metodoEnvio = (String)parametros.get("EMAIL_METODO_ENVIO");
		remetente = (String)parametros.get("EMAIL_REMETENTE");
		emailRemetente = (String)parametros.get("EMAIL_EMAIL_REMETENTE");
		apiKey = (String)parametros.get("EMAIL_APIKEY");
	}
	
	private void enviarEmailSendGrid() {
		/*
		SendGrid sendGrid;
		// Verifica se o API KEY está preenchido
		if (apiKey != null && !apiKey.trim().isEmpty()) {
			sendGrid = new SendGrid(apiKey);
		} else {
			sendGrid = new SendGrid(usuario, senha);
		}
		
		SendGrid.Email email = new SendGrid.Email();
		email.addTo(emailDestinatario, destinatario);
		email.setFrom(emailRemetente); // remetente
		email.setFromName(remetente);
		email.setSubject(assunto);
		email.setText(mensagem);
		
		SendGrid.Response response = sendGrid.send(email);
		logger.info("E-Mail enviado com código " + response.getCode() + ".\n\n" + response.getMessage());
		*/
	}
	
	public void enviarEmail() throws ApplicationException {
		/*
		// Carrega as configurações de envio de e-mail
		this.populateParameters();
		
		if (metodoEnvio.equals("SENDGRID")) {
			this.enviarEmailSendGrid();
			return;
		}
		
		// Instancia o objeto de e-mail
		SimpleEmail email = new SimpleEmail();
		
		// Atribui ao objeto os parâmetros passados ao método
		email.addTo(emailDestinatario, destinatario, charset);
		email.setFrom(emailRemetente, remetente, charset); // remetente
		email.setSubject(assunto);
		email.setMsg(mensagem);
		
		// Atribui os demais parâmetros vindos de opções do sistema
		email.setHostName(servidor);
		email.setSmtpPort(porta);
		email.setAuthentication(usuario, senha);
		if (usarSSL) {
			email.setSSLOnConnect(true);
			email.setSslSmtpPort(String.valueOf(porta));
		} else {
			email.setSSLOnConnect(false);
		}
		email.setCharset(charset);
		email.setSSLCheckServerIdentity(false);
		email.send();
		*/
	}
}
