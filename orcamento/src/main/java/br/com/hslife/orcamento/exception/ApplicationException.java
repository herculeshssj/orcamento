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

***/
package br.com.hslife.orcamento.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.hslife.orcamento.util.Util;

/**
 * Exceção verificada que indica erros de execução interna dentro
 * do sistema. Deve ser usada sempre para encapsular outra exceção
 * e lançar para a camada acima, ou indicar que uma determinada
 * operação corre o risco de falhar.
 * 
 * @author herculeshssj
 *
 */
public class ApplicationException extends Exception {
	
	private static final long serialVersionUID = -6615643150827475837L;
	
	private static final Logger logger = LogManager.getLogger(ApplicationException.class);

	public ApplicationException() {
		super();
	}

	public ApplicationException(String message) {
		super(message);
		logger.catching(this);
	}
	
	public ApplicationException(Exception exception) {
		super(exception);
		logger.catching(exception);
	}
	
	public ApplicationException(String message, Exception exception) {
		super(message, exception);
		logger.catching(exception);
	}
	
	public ApplicationException(String[] messages) {
		super(Util.montarString(messages));
		logger.catching(this);
	}
	
	public ApplicationException(String[] messages, Exception exception) {
		super(Util.montarString(messages), exception);
		logger.catching(exception);
	}
}
