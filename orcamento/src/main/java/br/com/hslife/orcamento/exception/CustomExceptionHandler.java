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

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth

	Floor, Boston, MA  02110-1301, USA.


	Para mais informações sobre o programa Orçamento Doméstico e seu autor

	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

	para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/package br.com.hslife.orcamento.exception;

import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * Tutorial: http://wmarkito.wordpress.com/2012/04/05/adding-global-exception-handling-using-jsf-2-x-exceptionhandler/
 */
public class CustomExceptionHandler extends ExceptionHandlerWrapper {

	private static final Logger logger = LogManager.getLogger(CustomExceptionHandler.class);
    private ExceptionHandler wrapped;
 
    CustomExceptionHandler(ExceptionHandler exception) {
        this.wrapped = exception;
    }
 
    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }
 
    @Override
    public void handle() throws FacesException {
 
        final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
        while (i.hasNext()) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context =
                    (ExceptionQueuedEventContext) event.getSource();
 
            // get the exception from context
            Throwable t = context.getException();
            
            // Verifica se a exceção é ViewExpiredException
            boolean sessaoExpirada = false;
            if (t instanceof ViewExpiredException) sessaoExpirada = true;
 
            final FacesContext fc = FacesContext.getCurrentInstance();
            final Map<String, Object> requestMap = fc.getExternalContext().getRequestMap();
            final NavigationHandler nav = fc.getApplication().getNavigationHandler();
 
            //here you do what ever you want with exception
            try {
 
                //log error ?
            	logger.catching(t);
                //log.log(Level.SEVERE, "Critical Exception!", t);
                
                // Iterate stackTrace to print on error page
                StringBuilder stackTraceBuilder = new StringBuilder();
                
                stackTraceBuilder.append(t.getMessage());
                stackTraceBuilder.append("\n\n");                
                
                if (t.getCause() != null && t.getCause().getStackTrace() != null) {
	                for (StackTraceElement ex : t.getCause().getStackTrace()) {
	                	stackTraceBuilder.append(ex.toString());
	                	stackTraceBuilder.append("\n");
	                }
                }
 
                if (sessaoExpirada) {
                	//redirect login page
                    requestMap.put("exceptionMessage", stackTraceBuilder.toString());
                    nav.handleNavigation(fc, null, "/login");
                    fc.renderResponse();
                } else {
                	//redirect error page
                    requestMap.put("exceptionMessage", stackTraceBuilder.toString());
                    nav.handleNavigation(fc, null, "/excecao");
                    fc.renderResponse();
                }
 
                // remove the comment below if you want to report the error in a jsf error message
                //JsfUtil.addErrorMessage(t.getMessage());
 
            } finally {
                //remove it from queue
                i.remove();
            }
        }
        //parent hanle
        getWrapped().handle();
    }	
}
