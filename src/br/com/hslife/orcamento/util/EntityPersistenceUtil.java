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

package br.com.hslife.orcamento.util;

import br.com.hslife.orcamento.exception.ValidationException;


public class EntityPersistenceUtil {

	private EntityPersistenceUtil() {
		// Esta classe não pode ser instanciada.
	}
	
	public static void validaTamanhoCampoStringOpcional(String nomeCampo, String campo, int tamanho) throws ValidationException {
		if (campo != null && campo.trim().length() > tamanho) {
			throw new ValidationException("Campo '" + nomeCampo + "' aceita no máximo "+ tamanho + " caracteres!");
		}		
	}
	
	public static void validaTamanhoExatoCampoStringOpcional(String nomeCampo, String campo, int tamanho) throws ValidationException {
		if (campo != null && campo.trim().length() != tamanho) {
			throw new ValidationException("Campo '" + nomeCampo + "' aceita exatamente "+ tamanho + " caracteres!");
		}		
	}
	
	public static void validaTamanhoCampoStringObrigatorio(String nomeCampo, String campo, int tamanho) throws ValidationException {
		if (campo == null) {
			throw new ValidationException("Campo '" + nomeCampo + "' não pode ser nulo.");
		}
		
		if (campo.trim().isEmpty()) {
			throw new ValidationException("Campo '" + nomeCampo + "' não pode ser vazio.");
		}
		
		if (campo.trim().length() > tamanho) {
			throw new ValidationException("Campo '" + nomeCampo + "' aceita no máximo "+ tamanho + " caracteres!");
		}		
	}
	
	public static void validaTamanhoExatoCampoStringObrigatorio(String nomeCampo, String campo, int tamanho) throws ValidationException {
		if (campo == null) {
			throw new ValidationException("Campo '" + nomeCampo + "' não pode ser nulo.");
		}
		
		if (campo.trim().isEmpty()) {
			throw new ValidationException("Campo '" + nomeCampo + "' não pode ser vazio.");
		}
		
		if (campo.trim().length() != tamanho) {
			throw new ValidationException("Campo '" + nomeCampo + "' aceita exatamente "+ tamanho + " caracteres!");
		}		
	}
	
	public static void validaCampoNulo(String nomeCampo, Object campo) throws ValidationException {
		if (campo == null) {
			throw new ValidationException("Campo '" + nomeCampo + "' não pode ser nulo.");
		}
	}
	
	public static void validaCampoInteiroZerado(String nomeCampo, Integer campo) throws ValidationException {
		if (campo == null) {
			throw new ValidationException("Campo '" + nomeCampo + "' não pode ser nulo.");
		} else if (campo.intValue() == 0) {
			throw new ValidationException("Campo '" + nomeCampo + "' não pode ser zero.");
		}
	}
}

