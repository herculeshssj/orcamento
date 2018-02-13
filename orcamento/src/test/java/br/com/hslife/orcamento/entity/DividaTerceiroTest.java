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

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth

	Floor, Boston, MA  02110-1301, USA.


	Para mais informações sobre o programa Orçamento Doméstico e seu autor

	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

	para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/package br.com.hslife.orcamento.entity;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.exception.ValidationException;

public class DividaTerceiroTest {

	private DividaTerceiro entity;

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
		entity.setTipoCategoria(TipoCategoria.CREDITO);
		entity.setUsuario(usuario);
		entity.setValorDivida(1000);
		entity.setMoeda(moeda);
		
		PagamentoDividaTerceiro pagamento;
		for (int i = 0; i < 3; ++i) {
			pagamento = new PagamentoDividaTerceiro();
			pagamento.setComprovantePagamento("Comprovante de pagamento da dívida de teste " + i);
			pagamento.setDataPagamento(new Date());
			pagamento.setDividaTerceiro(entity);
			pagamento.setValorPago(100);
			entity.getPagamentos().add(pagamento);
		}
	}

	@Test(expected=ValidationException.class)
	public void testValidateDataNegociacao() {
		entity.setDataNegociacao(null);
		entity.validate();
	}

	@Test(expected=ValidationException.class)
	public void testValidateJustificativa() {
		entity.setJustificativa(null);
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateTamanhoJustificativa() {
		StringBuilder s = new StringBuilder(10000);
		for (int i = 0; i < 10000; ++i) 
			s.append("a");
		entity.setJustificativa(s.toString());
		entity.validate();
	}

	@Test(expected=ValidationException.class)
	public void testValidateCategoria() {
		entity.setTipoCategoria(null);
		entity.validate();
	}

	@Test(expected=ValidationException.class)
	public void testValidateFavorecido() {
		entity.setFavorecido(null);
		entity.validate();
	}

	@Test(expected=ValidationException.class)
	public void testValidateMoeda() {
		entity.setMoeda(null);
		entity.validate();
	}

	@Test
	public void testLabel() {
		assertEquals("Crédito com Favorecido de teste no valor de R$ 1000.0 - Registrado", entity.getLabel());
	}

	@Test
	public void testTotalPago() {
		assertEquals(300.0, entity.getTotalPago(), 0);
	}

	@Test
	public void testTotalAPagar() {
		assertEquals(700.0, entity.getTotalAPagar(), 0);
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateDataPagamento() {
		for (PagamentoDividaTerceiro pagamento : entity.getPagamentos()) { 
			pagamento.setDataPagamento(null);
			pagamento.validate();
		}		
	}
}
