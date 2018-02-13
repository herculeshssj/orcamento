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
package br.com.hslife.orcamento.specification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import br.com.hslife.orcamento.entity.Favorecido;

public class FavorecidoSpecification extends AbstractSpecification<Favorecido> {
	
	private static final Logger logger = LogManager.getLogger(FavorecidoSpecification.class);

	@Override
	public boolean isSatisfiedBy(Favorecido t) {		
		if (t.getNome() == null || t.getNome().trim().isEmpty()) {
			//throw new BusinessException("Informe um nome!");
			return false;
		}
		
		if (t.getUsuario() == null) {
			//throw new BusinessException("Informe o usuário!");
			return false;
		}
		
		//EntityPersistenceUtil.validaCampoNulo("Tipo de pessoa", this.tipoPessoa);
		if (t.getTipoPessoa() == null) {
			return false;
		}
		
		try {
			CPFValidator validatorCpf = new CPFValidator();
			CNPJValidator validatorCnpj = new CNPJValidator();
			
			if (t.getCpfCnpj() != null && !t.getCpfCnpj().trim().isEmpty()) {
				switch (t.getTipoPessoa()) {
					case FISICA : validatorCpf.assertValid(t.getCpfCnpj()); break;
					case JURIDICA : validatorCnpj.assertValid(t.getCpfCnpj()); break;
				}
			}			
			
		} catch (InvalidStateException ise) {
			logger.catching(ise);
			return false;
		}
		
		return true; 
	}

}
