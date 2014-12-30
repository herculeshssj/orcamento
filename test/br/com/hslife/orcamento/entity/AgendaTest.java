/***
  
  	Copyright (c) 2012 - 2015 Hércules S. S. José

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

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento ou escreva 
    
    para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor, 
    
    Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor entre  

    em contato pelo e-mail herculeshssj@gmail.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.hslife.orcamento.enumeration.TipoAgendamento;
import br.com.hslife.orcamento.exception.BusinessException;
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

	@Test
	public void testValidateDescricao() {
		try {
			entity.setDescricao("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateLocalAgendamento() {
		try {
			entity.setLocalAgendamento("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ   ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo aceita no máximo 200 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateTipoAgendamento() {
		try {
			entity.setTipoAgendamento(null);
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Informe o tipo de agendamento!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateFimBeforeInicio() {
		try {
			entity.setFim(null);
			entity.validate();
			Calendar temp = Calendar.getInstance();
			temp.add(Calendar.DAY_OF_MONTH, -5);
			entity.setFim(temp.getTime());
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Data de término não pode ser anterior a data de início!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
	@Test
	public void testValidateUsuario() {
		try {
			entity.setUsuario(null);
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Informe o usuário!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
		fail("Falha no teste!");
	}
	
//	@Test
//	public void textExtrairData() {		
//		Calendar dataTest = Calendar.getInstance();
//		Date dataAtual = dataTest.getTime();
//		dataTest.set(Calendar.HOUR, 0);
//		dataTest.set(Calendar.MINUTE, 0);
//		dataTest.set(Calendar.SECOND, 0);
//		dataTest.set(Calendar.MILLISECOND, 0);
//		
//		assertEquals(dataTest.getTime(), entity.extrairData(dataAtual));
//	}
	
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
	
//	@Test
//	public void testExtrairSegundo() {
//		Calendar data = Calendar.getInstance();
//		
//		assertEquals(data.get(Calendar.SECOND), entity.extrairSegundo(data.getTime()));
//	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testComporDataHoraMinuto() {		
		Calendar dataTest = Calendar.getInstance();
		dataTest.set(new Date().getYear() + 1900, new Date().getMonth(), new Date().getDate(), 15, 30, 0);
		
		assertEquals(dataTest.getTime(), entity.comporData(dataTest.getTime(), 15, 30));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testComporDataHoraMinutoSegundo() {
		Calendar dataTest = Calendar.getInstance();
		dataTest.set(new Date().getYear() + 1900, new Date().getMonth(), new Date().getDate(), 15, 30, 45);
		
		assertEquals(dataTest.getTime(), entity.comporData(dataTest.getTime(), 15, 30, 45));
	}
	
	@Test
	public void testComporTextoAgendamentoPrevisao() throws BusinessException {
		Calendar dataTest = Calendar.getInstance();
		dataTest.set(Calendar.DAY_OF_MONTH, 0);
		dataTest.set(Calendar.MONTH, 2);
		dataTest.set(Calendar.YEAR, 2005);
		dataTest.set(Calendar.HOUR, 15);
		dataTest.set(Calendar.MINUTE, 30);
		dataTest.set(Calendar.SECOND, 45);
		dataTest.set(Calendar.MILLISECOND, 0);
		
		// Testando o tipo PREVISAO
		entity.setInicio(dataTest.getTime());
		entity.setTipoAgendamento(TipoAgendamento.PREVISAO);
		assertEquals("01/03/2005", entity.comporTextoAgendamento());
	}
	
	@Test
	public void testComporTextoAgendamentoCompromisso() throws BusinessException {
		Calendar dataTest = Calendar.getInstance();
		dataTest.set(Calendar.DAY_OF_MONTH, 0);
		dataTest.set(Calendar.MONTH, 2);
		dataTest.set(Calendar.YEAR, 2005);
		dataTest.set(Calendar.HOUR, 15);
		dataTest.set(Calendar.MINUTE, 30);
		dataTest.set(Calendar.SECOND, 45);
		dataTest.set(Calendar.MILLISECOND, 0);
		
		// Testando o tipo COMPROMISSO
		entity.setFim(null);
		entity.setInicio(null);
		entity.setTipoAgendamento(TipoAgendamento.COMPROMISSO);
		try {
			entity.comporTextoAgendamento();
		} catch (BusinessException be) {
			if (!be.getMessage().equals("Informe a data de início!")) {
				throw be;
			}			
		}
		
		entity.setInicio(dataTest.getTime());
		entity.setDiaInteiro(true);
		assertEquals("01/03/2005", entity.comporTextoAgendamento());
		
		dataTest.set(Calendar.DAY_OF_MONTH, 2);
		entity.setFim(dataTest.getTime());
		assertEquals("01/03/2005\nà\n02/03/2005", entity.comporTextoAgendamento());
				
		entity.setDiaInteiro(false);
		dataTest.set(Calendar.HOUR, 15);
		dataTest.set(Calendar.MINUTE, 30);
		entity.setFim(dataTest.getTime());
		assertEquals("01/03/2005 03:30\nà\n02/03/2005 15:30", entity.comporTextoAgendamento());
	}
	
	@Test
	public void testComporTextoAgendamentoTarefa() throws BusinessException {
		Calendar dataTest = Calendar.getInstance();
		dataTest.set(Calendar.DAY_OF_MONTH, 0);
		dataTest.set(Calendar.MONTH, 2);
		dataTest.set(Calendar.YEAR, 2005);
		dataTest.set(Calendar.HOUR, 15);
		dataTest.set(Calendar.MINUTE, 30);
		dataTest.set(Calendar.SECOND, 45);
		dataTest.set(Calendar.MILLISECOND, 0);
		
		// Testando o tipo COMPROMISSO
		entity.setFim(null);
		entity.setInicio(null);
		entity.setTipoAgendamento(TipoAgendamento.TAREFA);
		try {
			entity.comporTextoAgendamento();
		} catch (BusinessException be) {
			if (!be.getMessage().equals("Informe a data de início!")) {
				throw be;
			}			
		}
		
		entity.setInicio(dataTest.getTime());
		assertEquals("01/03/2005 03:30", entity.comporTextoAgendamento());
		
		dataTest.set(Calendar.DAY_OF_MONTH, 2);
		entity.setFim(dataTest.getTime());
		assertEquals("01/03/2005\nà\n02/03/2005", entity.comporTextoAgendamento());
				
		entity.setDiaInteiro(false);
		dataTest.set(Calendar.HOUR, 15);
		dataTest.set(Calendar.MINUTE, 30);
		entity.setFim(dataTest.getTime());
		assertEquals("01/03/2005\nà\n02/03/2005", entity.comporTextoAgendamento());
	}
}
