/**
  
  	Copyright (c) 2015 Hércules S. S. José

    Este arquivo é parte do programa Votenaweb-extrator.
    

    Votenaweb-extrator é um software livre; você pode redistribui-lo e/ou 

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como 

    publicada pela Fundação do Software Livre (FSF); na versão 2.1 da 

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, 

    mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a 
    
    qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública 
    
    Geral Menor GNU em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob o 

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/votenaweb-extrator 
    
    ou escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, 
    
    Fifth Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor entre  

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.

 */

package br.com.hslife.orcamento.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Extrator {

	public  void main(String[] args) throws IOException {
		// Parse HTML String using JSoup library
        String HTMLSTring = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>JSoup Example</title>"
                + "</head>"
                + "<body>"
                + "<table><tr><td><h1>HelloWorld</h1></tr>"
                + "</table>"
                + "</body>"
                + "</html>";
 
        Document html = Jsoup.parse(HTMLSTring);
        String title = html.title();
        String h1 = html.body().getElementsByTag("h1").text();
 
        System.out.println("Input HTML String to JSoup :" + HTMLSTring);
        System.out.println("After parsing, Title : " + title);
        System.out.println("Afte parsing, Heading : " + h1);
 
        // JSoup Example 2 - Reading HTML page from URL
//        Document doc;
//        try {
//            doc = Jsoup.connect("http://www.votenaweb.com.br/?ordem=&page=1").get();
//            title = doc.html();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
 
        //System.out.println("Jsoup Can read HTML page from URL, title : " + title);
 
        // JSoup Example 3 - Parsing an HTML file in Java
        //Document htmlFile = Jsoup.parse("login.html", "ISO-8859-1"); // wrong
        Document htmlFile = null;
        try {
            htmlFile = Jsoup.parse(new File("c:/Apps/texto.html"), "UTF-8");
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // right
        title = htmlFile.title();
        //Element div = htmlFile.getElementById("bill_7731");
        Elements div = htmlFile.getElementsByClass("cards");
        
        for (Iterator<Element> iterator = div.iterator(); iterator.hasNext(); ) {
        	Element element = iterator.next();
        	System.out.println(element.html());
        	System.out.println("---------------------------------------------------------------------------");
        }
        
        //System.out.println(div.html());
        
        //String cssClass = div.className(); // getting class form HTML element
        
        // Extrai todo o HTML obtido a partir da classe CSS
        //String cssClass = div.html();
        
//        for (Iterator<Element> iterator = div.iterator(); iterator.hasNext(); ) {
//        	Element e = (Element)iterator.next();
//        	
//        	String aTag = e.text();
//        	System.out.println(aTag);
//        	
//        }
 
        /*
        System.out.println("Jsoup can also parse HTML file directly");
        System.out.println("title : " + title);
        //System.out.println("class of div tag : " + cssClass);
        System.out.println(div.size());
		*/
	}
}
