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

package br.com.hslife.orcamento.aspect;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Auditoria;
import br.com.hslife.orcamento.entity.EntityPersistence;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.facade.IAuditoria;

@Aspect
@Component
public class AuditoriaAspect {
	
	private static final Logger logger = LogManager.getLogger(AuditoriaAspect.class);
	
	@Autowired
	private UsuarioComponent usuarioComponent;
	
	@Autowired
	private IAuditoria auditoriaService;

	/**
	 * @return the usuarioComponent
	 */
	public final UsuarioComponent getUsuarioComponent() {
		return usuarioComponent;
	}

	/**
	 * @return the auditoriaService
	 */
	public final IAuditoria getAuditoriaService() {
		return auditoriaService;
	}

	@AfterReturning(pointcut="execution(public void br.com.hslife.orcamento.repository..save(..)) && args(entity)")
	public void afterSave(final EntityPersistence entity) {
		try {
			Auditoria auditoria = new Auditoria();
			auditoria.setTransacao("INSERT");
			auditoria.setClasse(entity.getClass().getSimpleName());
			auditoria.setDadosAuditados(entity.generateJsonValues());
			
			// IP e Browser padrão
			auditoria.setIp("127.0.0.1");
			auditoria.setBrowser("Internal Browser");
			
			// Pega as informações sobre IP e Browser do usuário que está acessando
			if (FacesContext.getCurrentInstance() != null) {
				// Verifica se existe request válido para ser
				if (FacesContext.getCurrentInstance().getExternalContext().getRequest() != null) {
					// Pega o IP e o browser do cliente
					HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
					auditoria.setIp(request.getRemoteAddr());
					auditoria.setBrowser(request.getHeader("User-Agent"));
				}
			}
			
			// Pega o usuário logado
			Usuario usuario = getUsuarioComponent().getUsuarioLogado();
			if (usuario == null) {
				// Não existe sessão para o usuário, então registra-se o IP de onde o usuário está acessando
				auditoria.setUsuario(auditoria.getIp());
			} else if (usuario.getLogin() != null) {
				auditoria.setUsuario(usuario.getLogin());
			}
			
			getAuditoriaService().salvar(auditoria);
			
		} catch (Throwable t) {
			logger.catching(t);
			t.printStackTrace();
		}
	}
	
	@AfterReturning("execution(public void br.com.hslife.orcamento.repository..update(..)) && args(entity)")
	public void afterUpdate(final EntityPersistence entity) {
		try {
			Auditoria auditoria = new Auditoria();
			auditoria.setTransacao("UPDATE");
			auditoria.setClasse(entity.getClass().getSimpleName());
			auditoria.setDadosAuditados(entity.generateJsonValues());
			
			// IP e Browser padrão
			auditoria.setIp("127.0.0.1");
			auditoria.setBrowser("Internal Browser");
			
			// Pega as informações sobre IP e Browser do usuário que está acessando
			if (FacesContext.getCurrentInstance() != null) {
				// Verifica se existe request válido para ser
				if (FacesContext.getCurrentInstance().getExternalContext().getRequest() != null) {
					// Pega o IP e o browser do cliente
					HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
					auditoria.setIp(request.getRemoteAddr());
					auditoria.setBrowser(request.getHeader("User-Agent"));
				}
			}
			
			// Pega o usuário logado
			Usuario usuario = getUsuarioComponent().getUsuarioLogado();
			if (usuario == null) {
				// Não existe sessão para o usuário, então registra-se o IP de onde o usuário está acessando
				auditoria.setUsuario(auditoria.getIp());
			} else if (usuario.getLogin() != null) {
				auditoria.setUsuario(usuario.getLogin());
			}
			
			getAuditoriaService().salvar(auditoria);
			
		} catch (Throwable t) {
			logger.catching(t);
			t.printStackTrace();
		}
	}
	
	@AfterReturning(pointcut="execution(public void br.com.hslife.orcamento.repository..delete(..)) && args(entity)")
	public void beforeDelete(final EntityPersistence entity) {
		try {
			Auditoria auditoria = new Auditoria();
			auditoria.setTransacao("DELETE");
			auditoria.setClasse(entity.getClass().getSimpleName());
			auditoria.setDadosAuditados(entity.generateJsonValues());
			
			// IP e Browser padrão
			auditoria.setIp("127.0.0.1");
			auditoria.setBrowser("Internal Browser");
			
			// Pega as informações sobre IP e Browser do usuário que está acessando
			if (FacesContext.getCurrentInstance() != null) {
				// Verifica se existe request válido para ser
				if (FacesContext.getCurrentInstance().getExternalContext().getRequest() != null) {
					// Pega o IP e o browser do cliente
					HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
					auditoria.setIp(request.getRemoteAddr());
					auditoria.setBrowser(request.getHeader("User-Agent"));
				}
			}
			
			// Pega o usuário logado
			Usuario usuario = getUsuarioComponent().getUsuarioLogado();
			if (usuario == null) {
				// Não existe sessão para o usuário, então registra-se o IP de onde o usuário está acessando
				auditoria.setUsuario(auditoria.getIp());
			} else if (usuario.getLogin() != null) {
				auditoria.setUsuario(usuario.getLogin());
			}
			
			getAuditoriaService().salvar(auditoria);
			
		} catch (Throwable t) {
			logger.catching(t);
			t.printStackTrace();
		}
	}
}