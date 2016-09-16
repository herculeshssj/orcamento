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
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import br.com.hslife.orcamento.enumeration.TipoDado;
import br.com.hslife.orcamento.exception.BusinessException;

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
	
	@Test
	public void testValidateNomeColuna() {
		try {
			entity.setNomeColuna("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ     ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo 'Nome da coluna' aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
	}
	
	@Test
	public void testValidateTextoExibicao() {
		try {
			entity.setTextoExibicao("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ     ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo 'Texto de exibição' aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
	}
	
	@Test
	public void testValidateTipoDado() {
		try {
			entity.setTipoDado(null);
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo 'Tipo de dado' não pode ser nulo.", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
	}
	
	@Test
	public void testValidateMascaraFormatacaoPreenchido() {
		try {
			entity.setFormatar(true);
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo 'Máscara de formatação' não pode ser nulo.", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
	}
	
	@Test
	public void testValidateMascaraFormatacaoTamanho() {
		try {
			entity.setFormatar(true);
			entity.setMascaraFormatacao("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ     ");
			entity.validate();
		} catch (BusinessException be) {
			assertEquals("Campo 'Máscara de formatação' aceita no máximo 50 caracteres!", be.getMessage());
			return;
		} catch (Throwable t) {
			fail(t.getMessage());
		}
	}
}
