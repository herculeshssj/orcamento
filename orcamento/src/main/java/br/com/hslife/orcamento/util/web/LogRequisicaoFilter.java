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

package br.com.hslife.orcamento.util.web;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.hslife.orcamento.util.Util;

@WebFilter(urlPatterns="/*")
public class LogRequisicaoFilter implements Filter {
	
	private static final Logger logger = LogManager.getLogger(LogRequisicaoFilter.class);

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		System.out.println("Iniciado log da requisição...");
		
	}
	
	@Override
	public void destroy() {
		System.out.println("Finalizado o log da requisição.");
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			// A gravação será somente do request. Não iremos monitorar o response
			if (request instanceof HttpServletRequest) {
				HttpServletRequest req = (HttpServletRequest)request;
				
				// Pega as informações do usuário para validar
				String url = req.getRequestURL().toString();
				String queryString = req.getQueryString();
				
				if (url == null) 
					url = "";
				
				if (queryString == null)
					queryString = "";
				
				HttpSession sessao = req.getSession();
				String sessaoID = "";
				if (sessao != null) {
					sessaoID = sessao.getId();
				}
				
				// Grava as informações do usuário na base
				System.out.println("IP: " + request.getRemoteAddr());
				System.out.println("Método: " + req.getMethod());
				System.out.println("URL: " + url);
				System.out.println("Query string: " + queryString);
				System.out.println(Util.gerarJsonArray(request.getParameterMap()));
				System.out.println("Sessão ID:" + sessaoID);
				System.out.println("Sessão criada em:" + new Date(sessao.getCreationTime()).toString());
				System.out.println("/********************************************************************/");
			}
			
			// Retorna o controle do fluxo da requisição
			chain.doFilter(request, response);
		
		} catch (ServletException se) {
			logger.catching(se);
			throw new ServletException(se);
		} catch (IOException ie) {
			logger.catching(ie);
			throw new IOException(ie);
		} catch (Exception e) {
			logger.catching(e);
		}
	}	
}