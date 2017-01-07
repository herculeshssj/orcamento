/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.entity;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class InvestimentoTest {

	private Investimento entity = new Investimento();

	@Test(expected=ValidationException.class)
	public void testValidateBanco() {
		entity.setBanco(null);
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateTipoInvestimento() {
		entity.setTipoInvestimento(null);
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateDescricao() {
		entity.setDescricao(entity.getDescricao() + "     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateDescricaoNull() {
		entity.setDescricao(null);
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateCnpj() {
		entity.setCnpj("000000000000000000");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateCnpjNull() {
		entity.setCnpj(null);
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testMovimentarInvestimentoParametroTipoNulo() {
		entity.movimentarInvestimento(null, "teste", new Date(), 0);
	}
	
	@Test(expected=ValidationException.class)
	public void testMovimentarInvestimentoParametroHistoricoNulo() {
		entity.movimentarInvestimento(TipoLancamento.DESPESA, null, new Date(), 0);
	}
	
	@Test(expected=ValidationException.class)
	public void testMovimentarInvestimentoParametroDataNulo() {
		entity.movimentarInvestimento(TipoLancamento.DESPESA, "teste", null, 0);
	}
	
	@Test(expected=ValidationException.class)
	public void testMovimentarInvestimentoParametroHistorico() {
		entity.movimentarInvestimento(TipoLancamento.DESPESA, "     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ", new Date(), 0);
	}
	
	@Test
	public void testContemMovimentacao() {
		entity = EntityInitializerFactory.createInvestimento(
				EntityInitializerFactory.createBanco(
						EntityInitializerFactory.createUsuario()), 
				EntityInitializerFactory.createUsuario());
		assertEquals(3, entity.getMovimentacoesInvestimento().size());
	}
	
	@Test
	public void testContemResumo() {
		entity = EntityInitializerFactory.createInvestimento(
				EntityInitializerFactory.createBanco(
						EntityInitializerFactory.createUsuario()), 
				EntityInitializerFactory.createUsuario());
		assertEquals(1, entity.getResumosInvestimento().size());
	}
}