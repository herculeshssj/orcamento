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

package br.com.hslife.orcamento.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

public class TestDouble {

	//@Test
	public void testArredondamento() {
		System.out.println(29.302);
		System.out.println();
		System.out.println(29.302 - 3.209);
		System.out.println();
		System.out.println(-32.230);
		System.out.println();
		System.out.println(Util.arredondar(29.302));
		System.out.println();
		System.out.println(Util.arredondar(29.302944444 - 3.209333));
		System.out.println();
		System.out.println(Util.arredondar(-32.2303939));
	}
	
	//@Test
	public void testBigDecimal() {
	      System.out.println();
	        BigDecimal a = new BigDecimal(0.001).setScale(2,RoundingMode.CEILING);  
	        BigDecimal b = new BigDecimal(0.11).setScale(2,RoundingMode.CEILING);  
	        BigDecimal c = new BigDecimal(333.3334545345).setScale(2,RoundingMode.CEILING);  
	        BigDecimal d = new BigDecimal(1.11).setScale(2,RoundingMode.CEILING);  
	        double ai = a.doubleValue();  
	        double bi = b.doubleValue();  
	        double ci = c.doubleValue();  
	        double di = d.doubleValue();  
	        System.out.println(a);  
	        System.out.println(b);  
	        System.out.println(c);  
	        System.out.println(d);  
	        System.out.println(ai);  
	        System.out.println(bi);  
	        System.out.println(ci);  
	        System.out.println(di);       
	      
	}
	
	//@Test
	public void testCasaDecimal() {
		System.out.println(Math.floor(5.97));
		System.out.println(Math.ceil(5.97));
		System.out.println(Math.round(5.97));
	}
}
