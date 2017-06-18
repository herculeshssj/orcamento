/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor
	
	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

    para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 - 
	
	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/

package br.com.hslife.orcamento.exception;

import br.com.hslife.orcamento.util.Util;

/**
 * Exceção não verificada que indica erros de validação a nível de 
 * consistência de dados de entidade. Deve ser usada sempre para 
 * indicar que um atributo não está de acordo com os requisitos
 * da entidade.
 * 
 * @author herculeshssj
 *
 */
public class ValidationException extends RuntimeException {
	
	private static final long serialVersionUID = -6615643150827475837L;

	public ValidationException() {
		super();
	}

	public ValidationException(String message) {
		super(message);
	}
	
	public ValidationException(Exception exception) {
		super(exception);
	}
	
	public ValidationException(String message, Exception exception) {
		super(message, exception);
	}
	
	public ValidationException(String[] messages) {
		super(Util.montarString(messages));
	}
	
	public ValidationException(String[] messages, Exception exception) {
		super(Util.montarString(messages), exception);
	}
}