package br.com.hslife.orcamento.util;

import org.apache.poi.ss.formula.functions.FinanceLib;
import org.junit.Test;

public class FinanceTest {

	double taxaJuros = 0.730;
	int parcelas = 6;
	double principal = 671.64;
	double valorFuturo = 0;
	int parcelasRestantes = 5;
	int parcelasAPagas = 1;
	
	private double getPrestacao() {
		return FinanceLib.pmt(taxaJuros/100, parcelas, -principal, valorFuturo, false);
	}
	
//	private double getPrestacaoPaga() {
//		return FinanceLib.pmt(taxaJuros/100, parcelas, -principal, valorFuturo, false);
//	}
	
	private double getSaldoDevedor() {
		return FinanceLib.pv(taxaJuros/100, parcelasAPagas, -getPrestacao(), valorFuturo, false);
	}
	
	@Test
	public void testCalcularPrestacao() {
		System.out.print("Prestação: ");
		System.out.println(FinanceLib.pmt(taxaJuros/100, parcelas, -principal, valorFuturo, false));
	}
	
	@Test
	public void testCalcularSaldoDevedor() {
		System.out.print("Saldo devedor: ");
		System.out.println(FinanceLib.pv(taxaJuros/100, parcelasRestantes, -getPrestacao(), 0, false));
	}
	
	@Test
	public void testCalcularPrestacaoArredondado() {
		System.out.print("Prestação: R$ ");
		System.out.println(Util.arredondar(FinanceLib.pmt(taxaJuros/100, parcelas, -principal, valorFuturo, false)));
	}
	
	@Test
	public void testCalcularSaldoDevedorArredondado() {
		System.out.print("Saldo devedor: R$ ");
		System.out.println(Util.arredondar(FinanceLib.pv(taxaJuros/100, parcelasRestantes, -getPrestacao(), 0, false)));
	}
	
	@Test
	public void testCalcularValorAPagarAntecipado() {
		System.out.print("Quitação antecipada, valor a pagar: R$ ");
		System.out.println(Util.arredondar(FinanceLib.pv(taxaJuros/100, parcelasRestantes-parcelasAPagas, 0, -getSaldoDevedor(), false)));
	}
}
