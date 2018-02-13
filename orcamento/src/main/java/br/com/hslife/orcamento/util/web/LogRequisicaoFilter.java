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

***/
package br.com.hslife.orcamento.util.web;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import br.com.hslife.orcamento.entity.LogRequisicao;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.facade.ILog;
import br.com.hslife.orcamento.util.Util;

@WebFilter(urlPatterns="/*")
public class LogRequisicaoFilter implements Filter {
	
	@Autowired
	private ILog logService;
	
	private static final Logger logger = LogManager.getLogger(LogRequisicaoFilter.class);

	@Override
	public void init(FilterConfig config) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
		System.out.println("Iniciado log das requisições...");
	}
	
	@Override
	public void destroy() {
		System.out.println("Finalizado o log das requisições.");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			
			LogRequisicao logRequisicao = new LogRequisicao();
			
			long inicioRequisicao = System.currentTimeMillis();
			
			// A gravação será somente do request. Não iremos monitorar o response
			if (request instanceof HttpServletRequest) {
				
				HttpServletRequest req = (HttpServletRequest)request;
				HttpSession sessao = req.getSession();
				
				// Pega as informações do usuário para validar
				logRequisicao.setUrl(req.getRequestURL().toString());
				logRequisicao.setQueryString(req.getQueryString());
				
				if (logRequisicao.getUrl() == null) 
					logRequisicao.setUrl("");
				
				if (logRequisicao.getQueryString() == null)
					logRequisicao.setQueryString("");
				
				if (sessao != null) {
					logRequisicao.setSessaoID(sessao.getId());
				}
				
				if (sessao != null) {
					Usuario u = (Usuario)sessao.getAttribute("usuarioLogado");
					if (u != null) {
						logRequisicao.setUsuario(u.getLogin());
					} else {
						logRequisicao.setUsuario("");
					}
				}
				
				logRequisicao.setIp(request.getRemoteAddr());
				logRequisicao.setMetodo(req.getMethod());
				logRequisicao.setParams(Util.gerarJsonArray(request.getParameterMap()));
				logRequisicao.setSessaoCriadaEm(new Date(sessao.getCreationTime()));
				logRequisicao.setDataHora(new Date());
				logRequisicao.setUuid(UUID.randomUUID().toString());
				logRequisicao.setUserAgent(req.getHeader("User-Agent"));
			}
			
			// Retorna o controle do fluxo da requisição
			chain.doFilter(request, response);
			
			logRequisicao.setTempo((int)(System.currentTimeMillis() - inicioRequisicao));
			
			getLogService().salvarLogRequisicao(logRequisicao);
		
		} catch (ServletException se) {
			logger.catching(se);
			se.printStackTrace();
			throw new ServletException(se);
		} catch (IOException ie) {
			logger.catching(ie);
			ie.printStackTrace();
			throw new IOException(ie);
		} catch (Exception e) {
			e.printStackTrace();
			logger.catching(e);
		}
	}

	public ILog getLogService() {
		return logService;
	}

	public void setLogService(ILog logService) {
		this.logService = logService;
	}
}
