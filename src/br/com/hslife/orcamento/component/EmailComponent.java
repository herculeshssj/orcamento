/***
  
  	Copyright (c) 2012 - 2015 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou 

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como 

    publicada pela Fundação do Software Livre (FSF); na versão 2.1 da 

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, 

    mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a 
    
    qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública 
    
    Geral Menor GNU em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob o 

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento ou escreva 
    
    para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor, 
    
    Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor entre  

    em contato pelo e-mail herculeshssj@gmail.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.component;

import java.util.Map;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

@Component
public class EmailComponent {
	
	@Autowired
	private OpcaoSistemaComponent opcaoSistemaComponent;	

	public void setOpcaoSistemaComponent(OpcaoSistemaComponent opcaoSistemaComponent) {
		this.opcaoSistemaComponent = opcaoSistemaComponent;
	}
	
	void enviarEmailSendGrid(Map<String, Object> parametros, String destinatario, String emailDestinatario, String assunto, String mensagem) throws SendGridException {
		SendGrid sendGrid = new SendGrid((String)parametros.get("EMAIL_USUARIO"), (String)parametros.get("EMAIL_SENHA"));
		
		SendGrid.Email email = new SendGrid.Email();
		email.addTo(emailDestinatario, destinatario);
		email.setFrom((String)parametros.get("EMAIL_EMAIL_REMETENTE")); // remetente
		email.setFromName((String)parametros.get("EMAIL_REMETENTE"));
		email.setSubject(assunto);
		email.setText(mensagem);
		
		SendGrid.Response response = sendGrid.send(email);
		System.out.println(response.getMessage());
	}
	
	public void enviarEmail(String destinatario, String emailDestinatario, String assunto, String mensagem) throws EmailException, SendGridException {
		// Carrega as configurações de envio de e-mail
		Map<String, Object> parametros = opcaoSistemaComponent.buscarOpcoesGlobalAdminPorCDU("email");
		
		if (((String)parametros.get("EMAIL_METODO_ENVIO")).equals("SENDGRID")) {
			this.enviarEmailSendGrid(parametros, destinatario, emailDestinatario, assunto, mensagem);
			return;
		}
		
		// Instancia o objeto de e-mail
		SimpleEmail email = new SimpleEmail();
		
		// Atribui ao objeto os parâmetros passados ao método
		email.addTo(emailDestinatario, destinatario, (String)parametros.get("EMAIL_CHARSET"));
		email.setFrom((String)parametros.get("EMAIL_EMAIL_REMETENTE"), (String)parametros.get("EMAIL_REMETENTE"), (String)parametros.get("EMAIL_CHARSET")); // remetente
		email.setSubject(assunto);
		email.setMsg(mensagem);
		
		// Atribui os demais parâmetros vindos de opções do sistema
		email.setHostName((String)parametros.get("EMAIL_SERVIDOR"));
		email.setSmtpPort((Integer)parametros.get("EMAIL_PORTA"));
		email.setAuthentication((String)parametros.get("EMAIL_USUARIO"), (String)parametros.get("EMAIL_SENHA"));
		if ((Boolean)parametros.get("EMAIL_USAR_SSL")) {
			email.setSSLOnConnect(true);
			email.setSslSmtpPort(String.valueOf((Integer)parametros.get("EMAIL_PORTA")));
		} else {
			email.setSSLOnConnect(false);
		}
		email.setCharset((String)parametros.get("EMAIL_CHARSET"));
		email.setSSLCheckServerIdentity(false);
		email.send();
	}
	
	void enviarEmailSendGrid(Map<String, Object> parametros, String rementente, String emailRemetente, String destinatario, String emailDestinatario, String assunto, String mensagem) throws SendGridException {
		SendGrid sendGrid = new SendGrid((String)parametros.get("EMAIL_USUARIO"), (String)parametros.get("EMAIL_SENHA"));
		
		SendGrid.Email email = new SendGrid.Email();
		email.addTo(emailDestinatario, destinatario);
		email.setFrom(emailRemetente); // remetente
		email.setFromName(rementente);
		email.setSubject(assunto);
		email.setText(mensagem);
		
		SendGrid.Response response = sendGrid.send(email);
		System.out.println(response.getMessage());
	}
	
	public void enviarEmail(String remetente, String emailRemetente, String destinatario, String emailDestinatario, String assunto, String mensagem) throws EmailException, SendGridException {
		// Carrega as configurações de envio de e-mail
		Map<String, Object> parametros = opcaoSistemaComponent.buscarOpcoesGlobalAdminPorCDU("email");
		
		if (((String)parametros.get("EMAIL_METODO_ENVIO")).equals("SENDGRID")) {
			this.enviarEmailSendGrid(parametros, remetente, emailRemetente, destinatario, emailDestinatario, assunto, mensagem);
			return;
		}
		
		// Instancia o objeto de e-mail
		SimpleEmail email = new SimpleEmail();
		
		// Atribui ao objeto os parâmetros passados ao método
		email.addTo(emailDestinatario, destinatario, (String)parametros.get("EMAIL_CHARSET"));
		email.setFrom(emailRemetente, remetente, (String)parametros.get("EMAIL_CHARSET")); // remetente
		email.setSubject(assunto);
		email.setMsg(mensagem);
		
		// Atribui os demais parâmetros vindos de opções do sistema
		email.setHostName((String)parametros.get("EMAIL_SERVIDOR"));
		email.setSmtpPort((Integer)parametros.get("EMAIL_PORTA"));
		email.setAuthentication((String)parametros.get("EMAIL_USUARIO"), (String)parametros.get("EMAIL_SENHA"));
		if ((Boolean)parametros.get("EMAIL_USAR_SSL")) {
			email.setSSLOnConnect(true);
			email.setSslSmtpPort(String.valueOf((Integer)parametros.get("EMAIL_PORTA")));
		} else {
			email.setSSLOnConnect(false);
		}
		email.setCharset((String)parametros.get("EMAIL_CHARSET"));
		email.setSSLCheckServerIdentity(false);
		email.send();
	}
}