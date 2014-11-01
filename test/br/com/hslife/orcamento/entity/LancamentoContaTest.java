package br.com.hslife.orcamento.entity;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.hslife.orcamento.enumeration.IncrementoClonagemLancamento;
import br.com.hslife.orcamento.util.Util;

public class LancamentoContaTest {

	LancamentoConta lancamento = new LancamentoConta();
	
	@Before
	public void setUp() {
		lancamento.setDataPagamento(new Date());
		
	}
	
	
	@Test
	public void testClonarLancamentosBimestre() {
		for (LancamentoConta l : lancamento.clonarLancamentos(12, IncrementoClonagemLancamento.BIMESTRE)) {
			System.out.println("Data do lançamento clonado: " + Util.formataDataHora(l.getDataPagamento(), Util.DATA));
		}
	}

}
