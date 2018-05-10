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

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import br.com.hslife.orcamento.enumeration.EntityPersistenceEnum;
import br.com.hslife.orcamento.mock.EntityPersistenceMock;
import org.junit.Before;
import org.junit.Test;

import br.com.hslife.orcamento.exception.ValidationException;

/**
 * Testes unitários da entidade Seguro
 * 
 * @author herculeshssj
 *
 */
public class SeguroTest {

	/*
	 * Entidade sendo testada
	 */
	private Seguro entity = new Seguro();

	/*
	 * Inicialização do teste
	 */
	@Before
	public void setUp() throws Exception {
		entity.setDescricao("Seguro de teste");
		entity.setDataAquisicao(Calendar.getInstance());
	}
	
	/*
	 * Valida o preenchimento do campo
	 */
	@Test(expected=ValidationException.class)
	public void testValidateDescricao() {
		entity.setDescricao("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ          ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ          ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ          ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ          ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ          ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ          ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ     ");
		entity.validate();
	}
	
	/*
	 * Valida o preenchimento do campo
	 */
	@Test(expected=ValidationException.class)
	public void testValidateDataAquisicao() {
		entity.setDataAquisicao(null);
		entity.validate();
	}
	
	/*
	 * Testa o método getLabel()
	 */
	@Test
	public void testLabel() {
		assertEquals("Seguro de teste", entity.getLabel());
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateTipoSeguro() {
		entity.setTipoSeguro(null);
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidatePeriodicidadeRenovacao() {
		entity.setPeriodicidadeRenovacao(null);
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidatePeriodicidadePagamento() {
		entity.setPeriodicidadePagamento(null);
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidatePremioSeguro() {
		entity.setPremioSeguro(null);
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateMoeda() {
		entity.setMoeda(null);
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateLancamentoPeriodico() {
		entity.setLancamentoPeriodico(null);
		entity.validate();
	}

	@Test
	public void testGerarParcela() {
		// Inicializa as entidades
		EntityPersistenceMock epm = new EntityPersistenceMock()
				.criarUsuario()
				.comFavorecido(true)
				.comMoedaPadrao()
				.comContaCorrente()
				.ePossuiSeguro();
		entity = (Seguro)epm.get(EntityPersistenceEnum.SEGURO);

		try {
			entity.validate();
		} catch (ValidationException ve) {
			// Teste OK
		}

		entity.gerarDespesaFixa();

		entity.validate();

		entity.getLancamentoPeriodico().validate();
	}
}