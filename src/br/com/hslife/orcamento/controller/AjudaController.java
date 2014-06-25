/***
  
  	Copyright (c) 2012, 2013, 2014 Hércules S. S. José

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

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site HSlife
    
    no endereco www.hslife.com.br ou escreva para a Fundação do Software 
    
    Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor acesse o 

    endereço www.hslife.com.br, pelo e-mail contato@hslife.com.br ou escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
 ***/

package br.com.hslife.orcamento.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.component.EmailComponent;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.facade.IUsuario;

@Component("ajudaMB")
@Scope("session")
public class AjudaController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7392773085395093554L;
	
	@Autowired
	private EmailComponent emailComponent;
	
	@Autowired
	private IUsuario usuarioService;
	
	private String assuntoMensagem;
	private String mensagem;

	@Override
	protected void initializeEntity() {
		// TODO Auto-generated method stub
		assuntoMensagem = "";
		mensagem = "";
	}

	@Override
	public String startUp() {
		// TODO Auto-generated method stub
		return null;
	}

	public void enviarMensagem() {
		if (assuntoMensagem.isEmpty() || mensagem.isEmpty()) {
			warnMessage("Informe o assunto e o texto da mensagem!");
		} else {
			try {
				Usuario admin = usuarioService.buscarPorLogin("admin");
				emailComponent.enviarEmail(getUsuarioLogado().getNome(), getUsuarioLogado().getEmail(), admin.getNome(), admin.getEmail(), assuntoMensagem, mensagem);
				infoMessage("Mensagem enviada com sucesso!");
				initializeEntity();
			} catch (Exception e) {
				errorMessage(e.getMessage());
			}
		}
	}

	public String getAssuntoMensagem() {
		return assuntoMensagem;
	}

	public void setAssuntoMensagem(String assuntoMensagem) {
		this.assuntoMensagem = assuntoMensagem;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
}