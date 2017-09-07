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

package br.com.hslife.orcamento.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import br.com.hslife.orcamento.util.rest.WadlApplication;
import br.com.hslife.orcamento.util.rest.WadlDoc;
import br.com.hslife.orcamento.util.rest.WadlMethod;
import br.com.hslife.orcamento.util.rest.WadlParam;
import br.com.hslife.orcamento.util.rest.WadlParamStyle;
import br.com.hslife.orcamento.util.rest.WadlRepresentation;
import br.com.hslife.orcamento.util.rest.WadlRequest;
import br.com.hslife.orcamento.util.rest.WadlResource;
import br.com.hslife.orcamento.util.rest.WadlResources;
import br.com.hslife.orcamento.util.rest.WadlResponse;

/**
 * Classe que gera automaticamente o arquivo WADL com os serviços REST do projeto.
 * 
 * A ideia de gerar este arquivo foi criar um cliente REST em linha de comando no 
 * NetBeans. Os serviços REST só são registrados na IDE a partir da descrição dos
 * serviços contidos no arquivo WADL.
 * 
 * Referências:
 * 
 * Teoria: https://en.wikipedia.org/wiki/Web_Application_Description_Language
 * Ponto de partida: https://jira.spring.io/browse/SPR-8705 
 * Código fonte: http://tuxgalaxy.blogspot.com.br/2012/03/spring3-et-wadl-generation.html
 * Versão para Spring 4: https://javattitude.com/2014/05/26/wadl-generator-for-spring-rest/
 * Não foi utilizada, mas ajudou: https://github.com/autentia/wadl-tools
 * 
 * @author herculeshssj
 *
 */

@Controller
@RequestMapping("/application.wadl")
public class WADLController {
	
	@Autowired
	private RequestMappingHandlerMapping handlerMapping;
 
    @RequestMapping(method=RequestMethod.GET, produces={"application/xml"} ) 
    public @ResponseBody WadlApplication generateWadl(HttpServletRequest request) {
    	WadlApplication result = new WadlApplication();
    	WadlDoc doc = new WadlDoc();
    	doc.setTitle("REST Service WADL");
    	result.getDoc().add(doc);
    	WadlResources wadResources = new WadlResources();
		wadResources.setBase(getBaseUrl(request));
		
    	Map<RequestMappingInfo, HandlerMethod> handletMethods = handlerMapping.getHandlerMethods();
    	for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handletMethods.entrySet()) {
    		WadlResource wadlResource = new WadlResource();
    		
    		HandlerMethod handlerMethod = entry.getValue();
    		RequestMappingInfo mappingInfo = entry.getKey();
    		
    		Set<String> pattern =  mappingInfo.getPatternsCondition().getPatterns();
    		Set<RequestMethod> httpMethods =  mappingInfo.getMethodsCondition().getMethods();
    		ProducesRequestCondition producesRequestCondition = mappingInfo.getProducesCondition();
    		Set<MediaType> mediaTypes = producesRequestCondition.getProducibleMediaTypes();
    		
    		for (RequestMethod httpMethod : httpMethods) {
    			WadlMethod wadlMethod = new WadlMethod();
    			
    			for (String uri : pattern) {
    				wadlResource.setPath(uri);		
				}
    			
    			wadlMethod.setName(httpMethod.name());
    			Method javaMethod = handlerMethod.getMethod();
    			wadlMethod.setId(javaMethod.getName());
    			WadlDoc wadlDocMethod = new WadlDoc();
    			wadlDocMethod.setTitle(javaMethod.getDeclaringClass().getName()+"."+javaMethod.getName());
    			wadlMethod.getDoc().add(wadlDocMethod);
    			
    			// Request
    			WadlRequest wadlRequest = new WadlRequest();
    			
    			Annotation[][] annotations = javaMethod.getParameterAnnotations();
    			for (Annotation[] annotation : annotations) {
    				for (Annotation annotation2 : annotation) {
    					if (annotation2 instanceof RequestParam ) {
	    					RequestParam param2 = (RequestParam)annotation2;
	    					WadlParam waldParam = new WadlParam();
	            			waldParam.setName(param2.value());
	            			waldParam.setStyle(WadlParamStyle.QUERY);
	            			waldParam.setRequired(param2.required());
	            			String defaultValue = cleanDefault(param2.defaultValue());
	            			if ( !defaultValue.equals("") ) {
	            				waldParam.setDefault(defaultValue);
	            			}
	            			wadlRequest.getParam().add(waldParam);
    					} else if ( annotation2 instanceof PathVariable ) {
    						PathVariable param2 = (PathVariable)annotation2;
	    					WadlParam waldParam = new WadlParam();
	            			waldParam.setName(param2.value());
	            			waldParam.setStyle(WadlParamStyle.TEMPLATE);
	            			waldParam.setRequired(true);
	            			wadlRequest.getParam().add(waldParam);
    					}
					}
				}
    			if ( ! wadlRequest.getParam().isEmpty() ) {
    				wadlMethod.setRequest(wadlRequest);
    			}
    			
    			// Response
    			if ( !mediaTypes.isEmpty() ) {
	    			WadlResponse wadlResponse = new WadlResponse();
					wadlResponse.getStatus().add(200l);
	    			for (MediaType mediaType : mediaTypes) {
	    				WadlRepresentation wadlRepresentation = new WadlRepresentation();
	    				wadlRepresentation.setMediaType(mediaType.toString());
	    				wadlResponse.getRepresentation().add(wadlRepresentation);
					}
	    			wadlMethod.getResponse().add(wadlResponse);
    			}
    			
    			wadlResource.getMethodOrResource().add(wadlMethod);
    			
			}
    		
    		wadResources.getResource().add(wadlResource);
    		
		}
    	result.getResources().add(wadResources);
    	
    	return result;
    }
    
    
    private String getBaseUrl (HttpServletRequest request) {
    	String requestUri = request.getRequestURI();
		return request.getScheme()+"://"+ request.getServerName()+":"+ request.getServerPort() + requestUri;
	}
    
    private String cleanDefault(String value) {
    	value = value.replaceAll("\t", "");
    	value = value.replaceAll("\n", "");
    	value = value.replaceAll("", "");
    	value = value.replaceAll("", "");
    	value = value.replaceAll("", "");
    	return value;
    }
 
}
