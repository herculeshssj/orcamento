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
	
	@Autowired
	private OpcaoSistemaComponent opcaoSistemaComponent;	

	public void setOpcaoSistemaComponent(OpcaoSistemaComponent opcaoSistemaComponent) {
		this.opcaoSistemaComponent = opcaoSistemaComponent;
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

	private void populateParameters() {
		Map<String, Object> parametros = opcaoSistemaComponent.buscarOpcoesGlobalAdminPorCDU("email");
		servidor = (String)parametros.get("EMAIL_SERVIDOR");
		porta = (Integer)parametros.get("EMAIL_PORTA");
		usuario = (String)parametros.get("EMAIL_USUARIO");
		senha = (String)parametros.get("EMAIL_SENHA");
		usarSSL = (Boolean)parametros.get("EMAIL_USAR_SSL");
		charset = (String)parametros.get("EMAIL_CHARSET");
		metodoEnvio = (String)parametros.get("EMAIL_METODO_ENVIO");
		remetente = (String)parametros.get("EMAIL_REMETENTE");
		emailRemetente = (String)parametros.get("EMAIL_EMAIL_REMETENTE");
	}
	
	private void enviarEmailSendGrid() throws SendGridException {
		SendGrid sendGrid = new SendGrid(usuario, senha);
		
		SendGrid.Email email = new SendGrid.Email();
		email.addTo(emailDestinatario, destinatario);
		email.setFrom(emailRemetente); // remetente
		email.setFromName(remetente);
		email.setSubject(assunto);
		email.setText(mensagem);
		
		SendGrid.Response response = sendGrid.send(email);
		System.out.println(response.getMessage());
	}
	
	public void enviarEmail() throws EmailException, SendGridException {
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
	}
}