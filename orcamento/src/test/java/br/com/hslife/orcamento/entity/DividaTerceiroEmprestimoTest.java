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

para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor,

Boston, MA  02110-1301, USA.


Para mais informações sobre o programa Orçamento Doméstico e seu autor

entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.entity;

import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.exception.ValidationException;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DividaTerceiroEmprestimoTest {

	private DividaTerceiro entity;

	private static final double DELTA = 1e-15;

	@Before
	public void setUp() throws Exception {
		
		Usuario usuario = new Usuario();
		usuario.setNome("Usuário de teste");
		
		Favorecido favorecido = new Favorecido();
		favorecido.setNome("Favorecido de teste");
		
		Moeda moeda = new Moeda();
		moeda.setNome("Real");
		moeda.setSimboloMonetario("R$");
		
		entity = new DividaTerceiro();
		entity.setDataNegociacao(new Date());
		entity.setFavorecido(favorecido);
		entity.setJustificativa("Justificativa da dívida de teste");
		entity.setTermoDivida("Termo da dívida de teste");
		entity.setTermoQuitacao("Termo de quitação da dívida de teste");
		entity.setTipoCategoria(TipoCategoria.DEBITO);
		entity.setUsuario(usuario);
		entity.setValorDivida(1000);
		entity.setMoeda(moeda);
	}

	private void configurarEmprestimo() {
		entity.setEmprestimo(true);
		entity.setQuantParcelas(24);
		entity.setTaxaJuros(4.75);
		entity.calcularValorParcela();

		PagamentoDividaTerceiro pagamento;
		for (int i = 0; i < 12; ++i) {
			pagamento = new PagamentoDividaTerceiro();
			pagamento.setComprovantePagamento("Comprovante de pagamento da dívida de teste " + i);
			pagamento.setDataPagamento(new Date());
			pagamento.setDividaTerceiro(entity);
			if (i % 2 == 0)
				pagamento.setValorPago(entity.getValorParcela());
			else
				pagamento.setValorPago(75);
			entity.getPagamentos().add(pagamento);
		}
	}

	@Test
	public void testCalcularValorParcela() {
		assertEquals(0, entity.getValorParcela(), DELTA);
		entity.setEmprestimo(true);
		entity.calcularValorParcela();
		assertEquals(0, entity.getValorParcela(), DELTA);
		entity.setQuantParcelas(24);
		entity.setTaxaJuros(4.75);
		entity.calcularValorParcela();
		assertEquals(70.72, entity.getValorParcela(), DELTA);
	}

	@Test
	public void testQuantParcelasPagas() {
		configurarEmprestimo();
		assertEquals(6, entity.getQuantParcelasPagas());
	}

	@Test
	public void testQuantParcelasAPagar() {
		configurarEmprestimo();
		assertEquals(18, entity.getQuantParcelasAPagar());
	}

	@Test
	public void testSaldoDevedor() {
		configurarEmprestimo();
		assertEquals(843.07, entity.getSaldoDevedor(), DELTA);
	}
}
