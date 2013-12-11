package br.com.hslife.orcamento.util;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class TestData {
	
	@Test
	public void testData() {
		System.out.println(Util.primeiroDiaMesAnterior());
		System.out.println(Util.ultimoDiaMesAnterior());
		System.out.println("");
		System.out.println(Util.primeiroDiaAno(2013));
		System.out.println(Util.ultimoDiaAno(2013));
		System.out.println("");
		System.out.println(Util.primeiroDiaMes(Calendar.DECEMBER, 2016));
		System.out.println(Util.ultimoDiaMes(Calendar.FEBRUARY, 2016));
		System.out.println("");
		System.out.println(Util.primeiroDiaMes(new Date()));
	}

}
