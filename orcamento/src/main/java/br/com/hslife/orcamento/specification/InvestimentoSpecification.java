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
package br.com.hslife.orcamento.specification;

import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Investimento;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.repository.InvestimentoRepository;

@Component
public class InvestimentoSpecification extends AbstractCRUDSpecification<Investimento> {
	
	private InvestimentoRepository investimentoRepository;
	
	private boolean toValidate(Investimento entity) {
		try {
			entity.validate();
			return true;
		} catch (ValidationException ve) {
			
		}
		return false;
	}

	@Override
	public boolean isSatisfiedToSave(Investimento entity) {
		return this.toValidate(entity)
				&& !this.existeCNPJCadastrado(entity.getCnpj());
	}

	@Override
	public boolean isSatisfiedToEdit(Investimento entity) {
		return this.toValidate(entity)
				&& !this.existeCNPJCadastradoOutroInvestimento(entity);
	}

	@Override
	public boolean isSafisfiedToDelete(Investimento entity) {
		return this.toValidate(entity);
	}

	@Override
	public boolean isSatisfiedBy(Investimento entity) {
		return this.toValidate(entity);
	}
	
	private boolean existeCNPJCadastrado(String cnpj) {
		Investimento investimento = getInvestimentoRepository().findByCNPJ(cnpj);
		if (investimento == null) {
			return false;
		} else {
			setErrorMessage("CNPJ informado já foi cadastrado!");
		}
		return true;
	}
	
	private boolean existeCNPJCadastradoOutroInvestimento(Investimento entity) {
		Investimento investimento = getInvestimentoRepository().findByCNPJ(entity.getCnpj());
		if (investimento == null) {
			return false;
		} else {
			if (investimento.equals(entity))
				return false;
			else 
				setErrorMessage("CNPJ informado já foi cadastrado para outro investimento!");
		}
		return true;
	}

	public InvestimentoRepository getInvestimentoRepository() {
		return investimentoRepository;
	}

	public void setInvestimentoRepository(InvestimentoRepository investimentoRepository) {
		this.investimentoRepository = investimentoRepository;
	}
}
