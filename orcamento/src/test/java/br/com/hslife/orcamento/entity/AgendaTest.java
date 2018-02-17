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
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.hslife.orcamento.enumeration.TipoAgendamento;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.util.Util;

public class AgendaTest {

	private Agenda entity;

	@Before
	public void setUp() throws Exception {
		
		Usuario usuario = new Usuario();
		usuario.setNome("Usuário de teste");
		
		entity = new Agenda();
		entity.setUsuario(usuario);
		entity.setDescricao("Compromisso de teste");
		entity.setLocalAgendamento("Local do compromisso");
		entity.setInicio(new Date());
		entity.setFim(new Date());
		entity.setTipoAgendamento(TipoAgendamento.COMPROMISSO);		
	}

	@Test
	public void testGetLabel() {
		assertEquals("Compromisso de teste", entity.getLabel());
	}
	
	@Test
	public void testGetDateLabel() {
		entity.setFim(null);
		entity.setTipoAgendamento(TipoAgendamento.COMPROMISSO);
		assertEquals(Util.formataDataHora(new Date(), Util.DATAHORA), entity.getDateLabel());
	}

	@Test(expected=ValidationException.class)
	public void testValidateDescricao() {
		entity.setDescricao("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ        ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ        ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ        ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ        ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ        ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ        ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ        ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateLocalAgendamento() {
		entity.setLocalAgendamento("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateTipoAgendamento() {
		entity.setTipoAgendamento(null);
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateFimBeforeInicio() {
		entity.setFim(null);
		entity.validate();
		Calendar temp = Calendar.getInstance();
		temp.add(Calendar.DAY_OF_MONTH, -5);
		entity.setFim(temp.getTime());
		entity.validate();
	}
	
	@Test
	public void testExtrairHora() {
		Calendar data = Calendar.getInstance();
		
		assertEquals(data.get(Calendar.HOUR_OF_DAY), entity.extrairHora(data.getTime()));
	}
	
	@Test
	public void testExtrairMinuto() {
		Calendar data = Calendar.getInstance();
		
		assertEquals(data.get(Calendar.MINUTE), entity.extrairMinuto(data.getTime()));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testComporDataHoraMinuto() {		
		Date dataTest = new Date();
		dataTest.setHours(15);
		dataTest.setMinutes(30);
		dataTest.setSeconds(0);
		
		assertEquals(dataTest, entity.comporData(dataTest, 15, 30));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testComporDataHoraMinutoSegundo() {
		Calendar dataTest = Calendar.getInstance();
		dataTest.set(new Date().getYear() + 1900, new Date().getMonth(), new Date().getDate(), 15, 30, 45);
		
		assertEquals(dataTest.getTime(), entity.comporData(dataTest.getTime(), 15, 30, 45));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testComporTextoAgendamentoPrevisao() throws ApplicationException {
		Date dataTest = new Date(2005-1900,2,1,15,30,45);
		
		// Testando o tipo PREVISAO
		entity.setInicio(dataTest);
		entity.setTipoAgendamento(TipoAgendamento.PREVISAO);
		assertEquals("01/03/2005", entity.comporTextoAgendamento());
	}
	
	@SuppressWarnings("deprecation")
	@Test(expected=ValidationException.class)
	public void testComporTextoAgendamentoCompromisso() throws ApplicationException {
		Date dataTest = new Date(2005-1900,2,1,15,30,45);
		
		// Testando o tipo COMPROMISSO
		entity.setFim(null);
		entity.setInicio(null);
		entity.setTipoAgendamento(TipoAgendamento.COMPROMISSO);
		entity.comporTextoAgendamento();
		
		entity.setInicio(dataTest);
		entity.setDiaInteiro(true);
		assertEquals("01/03/2005", entity.comporTextoAgendamento());
		
		Date dataTestFim = (Date)dataTest.clone();
		dataTestFim.setDate(2);
		entity.setFim(dataTestFim);
		assertEquals("01/03/2005\nà\n02/03/2005", entity.comporTextoAgendamento());
				
		entity.setDiaInteiro(false);
		dataTestFim.setHours(15);
		dataTestFim.setMinutes(30);
		entity.setFim(dataTestFim);
		assertEquals("01/03/2005 15:30\nà\n02/03/2005 15:30", entity.comporTextoAgendamento());
	}
	
	@SuppressWarnings("deprecation")
	@Test(expected=ValidationException.class)
	public void testComporTextoAgendamentoTarefa() throws ApplicationException {
		Date dataTest = new Date(2005-1900,2,1,15,30,45);
		
		// Testando o tipo COMPROMISSO
		entity.setFim(null);
		entity.setInicio(null);
		entity.setTipoAgendamento(TipoAgendamento.TAREFA);
		entity.comporTextoAgendamento();
				
		entity.setInicio(dataTest);
		assertEquals("01/03/2005 15:30", entity.comporTextoAgendamento());
		
		Date dataTestFim = (Date)dataTest.clone();
		dataTestFim.setDate(2);
		entity.setFim(dataTestFim);
		assertEquals("01/03/2005 15:30\nà\n02/03/2005 15:30", entity.comporTextoAgendamento());
				
		entity.setDiaInteiro(false);
		dataTestFim.setHours(15);
		dataTestFim.setMinutes(30);
		entity.setFim(dataTestFim);
		assertEquals("01/03/2005 15:30\nà\n02/03/2005 15:30", entity.comporTextoAgendamento());
	}
}
