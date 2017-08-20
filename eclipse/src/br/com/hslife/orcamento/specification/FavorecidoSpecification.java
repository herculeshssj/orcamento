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
