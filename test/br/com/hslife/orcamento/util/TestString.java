package br.com.hslife.orcamento.util;

import junit.framework.Assert;

public class TestString {

	//@Test
	public void testSuprimirFim() {
		System.out.println(Util.suprimirTextoFim("abcdefghijklmnopqrstuvwxyz", 20));
	}

	//@Test
	public void testSuprimirMeio() {
		System.out.println(Util.suprimirTextoMeio("abcdefghijklmnopqrstuvwxyz", 20));
	}
	
	//@Test
	public void testStringVazia() {
		String obj = null;
		Assert.assertTrue(Util.eVazio(obj));
		
		obj = "";
		Assert.assertTrue(Util.eVazio(obj));
		
		obj = " oi ";
		Assert.assertFalse(Util.eVazio(obj));
	}
	
}
