/***
  
  	Copyright (c) 2012 - 2016 Hércules S. S. José

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

import org.junit.Before;
import org.junit.Test;

import br.com.hslife.orcamento.enumeration.TipoDado;
import br.com.hslife.orcamento.exception.ValidationException;

public class RelatorioColunaTest {

	private RelatorioColuna entity = new RelatorioColuna();

	@Before
	public void setUp() throws Exception {
		entity.setNomeColuna("colunateste");
		entity.setTextoExibicao("Coluna de Teste");
		entity.setTipoDado(TipoDado.STRING);
	}
	
	@Test
	public void testLabel() {
		assertEquals("Coluna de Teste (colunateste)", entity.getLabel());
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateNomeColuna() {
		entity.setNomeColuna("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ     ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateTextoExibicao() {
		entity.setTextoExibicao("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ     ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateTipoDado() {
		entity.setTipoDado(null);
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateMascaraFormatacaoPreenchido() {
		entity.setFormatar(true);
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateMascaraFormatacaoTamanho() {
		entity.setFormatar(true);
		entity.setMascaraFormatacao("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ     ");
		entity.validate();
	}
}
