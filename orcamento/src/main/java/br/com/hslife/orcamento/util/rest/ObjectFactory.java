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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.02.28 at 11:52:44 AM CET 
//


package br.com.hslife.orcamento.util.rest;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.java.dev.wadl._2009._02 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.java.dev.wadl._2009._02
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link WadlMethod }
     * 
     */
    public WadlMethod createWadlMethod() {
        return new WadlMethod();
    }

    /**
     * Create an instance of {@link WadlOption }
     * 
     */
    public WadlOption createWadlOption() {
        return new WadlOption();
    }

    /**
     * Create an instance of {@link WadlInclude }
     * 
     */
    public WadlInclude createWadlInclude() {
        return new WadlInclude();
    }

    /**
     * Create an instance of {@link WadlRequest }
     * 
     */
    public WadlRequest createWadlRequest() {
        return new WadlRequest();
    }

    /**
     * Create an instance of {@link WadlDoc }
     * 
     */
    public WadlDoc createWadlDoc() {
        return new WadlDoc();
    }

    /**
     * Create an instance of {@link WadlResources }
     * 
     */
    public WadlResources createWadlResources() {
        return new WadlResources();
    }

    /**
     * Create an instance of {@link WadlResponse }
     * 
     */
    public WadlResponse createWadlResponse() {
        return new WadlResponse();
    }

    /**
     * Create an instance of {@link WadlParam }
     * 
     */
    public WadlParam createWadlParam() {
        return new WadlParam();
    }

    /**
     * Create an instance of {@link WadlGrammars }
     * 
     */
    public WadlGrammars createWadlGrammars() {
        return new WadlGrammars();
    }

    /**
     * Create an instance of {@link WadlResource }
     * 
     */
    public WadlResource createWadlResource() {
        return new WadlResource();
    }

    /**
     * Create an instance of {@link WadlRepresentation }
     * 
     */
    public WadlRepresentation createWadlRepresentation() {
        return new WadlRepresentation();
    }

    /**
     * Create an instance of {@link WadlApplication }
     * 
     */
    public WadlApplication createWadlApplication() {
        return new WadlApplication();
    }

    /**
     * Create an instance of {@link WadlResourceType }
     * 
     */
    public WadlResourceType createWadlResourceType() {
        return new WadlResourceType();
    }

    /**
     * Create an instance of {@link WadlLink }
     * 
     */
    public WadlLink createWadlLink() {
        return new WadlLink();
    }

}