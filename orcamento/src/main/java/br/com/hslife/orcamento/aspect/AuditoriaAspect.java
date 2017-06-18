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

package br.com.hslife.orcamento.aspect;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Auditoria;
import br.com.hslife.orcamento.entity.EntityPersistence;
import br.com.hslife.orcamento.entity.Usuario;

@Aspect
@Component
@Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor={Throwable.class})
public class AuditoriaAspect {
	
	private static final Logger logger = LogManager.getLogger(AuditoriaAspect.class);
	
	@Autowired 
	private SessionFactory sessionFactory;
	
	@Autowired
	private UsuarioComponent usuarioComponent;
	
	public UsuarioComponent getUsuarioComponent() {
		return usuarioComponent;
	}

	@AfterReturning(pointcut="execution(public void br.com.hslife.orcamento.repository..save(..)) && args(entity)")
	public void afterSave(EntityPersistence entity) {
		try {
		Auditoria auditoria = new Auditoria();
		
		auditoria.setClasse(entity.getClass().getSimpleName());
		auditoria.setTransacao("INSERT");
		//auditoria.setUsuario(((Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioLogado")).getLogin());
		
		/*
		 * Mudança realizada em virtude do caso de uso de registro de usuário. Ainda é necessário pensar em uma forma de registrar
		 * quem está efetuando o registro para gravar na auditoria. Por enquanto será gravado o mesmo IP do computador que efetuou
		 * o registro
		 */
		if (FacesContext.getCurrentInstance() != null) { // Linha incluída para viabilizar os testes com os repositórios
			if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioLogado") == null) {
				auditoria.setUsuario(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRemoteAddr());
			} else {
				Usuario u = (Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioLogado");
				if (u.isLogado())
					auditoria.setUsuario(getUsuarioComponent().getUsuarioLogado().getLogin());
				else
					auditoria.setUsuario(((Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioLogado")).getLogin());
			}
			
			auditoria.setIp(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRemoteAddr());
			auditoria.setBrowser(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getHeader("User-Agent"));
		} else {
			auditoria.setUsuario("desenvolvedor");
			auditoria.setIp("127.0.0.1");
			auditoria.setBrowser("Internal Browser");
		}
	
		//auditoria.setVersionAuditedEntity(entity.getVersionEntity());
		auditoria.setDadosAuditados(entity.generateJsonValues());
		
		sessionFactory.getCurrentSession().persist(auditoria);
		} catch (Throwable t) {
			logger.catching(t);
			t.printStackTrace();
		}
	}
	
	@AfterReturning("execution(public void br.com.hslife.orcamento.repository..update(..)) && args(entity)")
	public void afterUpdate(EntityPersistence entity) {
		try {
			Auditoria auditoria  = new Auditoria();
			
			auditoria.setClasse(entity.getClass().getSimpleName());
			auditoria.setTransacao("UPDATE");
			//auditoria.setUsuario(((Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioLogado")).getLogin());
			
			/*
			 * Mudança realizada em virtude do caso de uso de registro de usuário. Ainda é necessário pensar em uma forma de registrar
			 * quem está efetuando o registro para gravar na auditoria. Por enquanto será gravado o mesmo IP do computador que efetuou
			 * o registro
			 */
			if (FacesContext.getCurrentInstance() != null) { // Linha incluída para viabilizar os testes com os repositórios
				if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioLogado") == null) {
					auditoria.setUsuario(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRemoteAddr());
				} else {
					Usuario u = (Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioLogado");
					if (u.isLogado())
						auditoria.setUsuario(getUsuarioComponent().getUsuarioLogado().getLogin());
					else
						auditoria.setUsuario(((Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioLogado")).getLogin());
				}
				
				auditoria.setIp(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRemoteAddr());
				auditoria.setBrowser(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getHeader("User-Agent"));
			} else {
				auditoria.setUsuario("desenvolvedor");
				auditoria.setIp("127.0.0.1");
				auditoria.setBrowser("Internal Browser");
			}
			
			//auditoria.setVersionAuditedEntity(entity.getVersionEntity());
			auditoria.setDadosAuditados(entity.generateJsonValues());
			
			sessionFactory.getCurrentSession().persist(auditoria);
		} catch (Throwable t) {
			logger.catching(t);
			t.printStackTrace();
		}
	}
	
	@AfterReturning(pointcut="execution(public void br.com.hslife.orcamento.repository..delete(..)) && args(entity)")
	public void beforeDelete(EntityPersistence entity) {
		try {
		Auditoria auditoria = new Auditoria();
		
		auditoria.setClasse(entity.getClass().getSimpleName());
		auditoria.setTransacao("DELETE");		
				
		/*
		 * Mudança realizada em virtude do caso de uso de registro de usuário. Ainda é necessário pensar em uma forma de registrar
		 * quem está efetuando o registro para gravar na auditoria. Por enquanto será gravado o mesmo IP do computador que efetuou
		 * o registro
		 */
		if (FacesContext.getCurrentInstance() != null) { // Linha incluída para viabilizar os testes com os repositórios
			if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioLogado") == null) {
				auditoria.setUsuario(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRemoteAddr());
			} else {
				Usuario u = (Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioLogado");
				if (u.isLogado())
					auditoria.setUsuario(getUsuarioComponent().getUsuarioLogado().getLogin());
				else
					auditoria.setUsuario(((Usuario)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioLogado")).getLogin());
			}
			
			auditoria.setIp(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRemoteAddr());
			auditoria.setBrowser(((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getHeader("User-Agent"));
		} else {
			auditoria.setUsuario("desenvolvedor");
			auditoria.setIp("127.0.0.1");
			auditoria.setBrowser("Internal Browser");
		}
		
		//auditoria.setVersionAuditedEntity(entity.getVersionEntity());
		auditoria.setDadosAuditados(entity.generateJsonValues());
		
		sessionFactory.getCurrentSession().persist(auditoria);
		} catch (Throwable t) {
			logger.catching(t);
			t.printStackTrace();
		}
	}
}