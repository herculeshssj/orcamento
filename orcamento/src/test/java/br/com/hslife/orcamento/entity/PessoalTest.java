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

import org.junit.Test;

import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class PessoalTest {
	
	private Pessoal entity = EntityInitializerFactory.createPessoal(EntityInitializerFactory.createUsuario());

	@Test(expected=ValidationException.class)
	public void testValidateGenero() {
		entity.setGenero('G');
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateDataNascimento() {
		entity.setDataNascimento(null);
		entity.validate();
	}

	@Test(expected=ValidationException.class)
	public void testValidateEtnia() {
		entity.setEtnia("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateTipoSanguineo() {
		entity.setTipoSanguineo("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateNacionalidade() {
		entity.setNacionalidade("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateNaturalidade() {
		entity.setNaturalidade("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateEscolaridade() {
		entity.setEscolaridade("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateFiliacaoPai() {
		entity.setFiliacaoPai("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateFiliacaoMae() {
		entity.setFiliacaoMae("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
		entity.validate();
	}
	
	@Test(expected=ValidationException.class)
	public void testValidateEstadoCivil() {
		entity.setEstadoCivil("     ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ      ");
		entity.validate();
	}
}