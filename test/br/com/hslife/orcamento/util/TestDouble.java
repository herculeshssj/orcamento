package br.com.hslife.orcamento.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
}
