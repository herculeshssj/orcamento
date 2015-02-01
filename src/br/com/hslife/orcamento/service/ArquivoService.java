/***
  
  	Copyright (c) 2012 - 2015 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@gmail.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IArquivo;
import br.com.hslife.orcamento.model.CriterioArquivo;
import br.com.hslife.orcamento.repository.ArquivoRepository;

@Service("arquivoService")
@Transactional
public class ArquivoService implements IArquivo {
	
	@Autowired
	private ArquivoRepository repository;

	public ArquivoRepository getRepository() {
		return repository;
	}

	public void setRepository(ArquivoRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<Arquivo> buscarPorCriterioArquivo(CriterioArquivo criterio) throws BusinessException {
		return getRepository().findByCriterioArquivo(criterio);
	}
	
	@Override
	public void excluir(Arquivo arquivo) throws BusinessException {
		switch (arquivo.getContainer()) {
			case ARQUIVO : break;
			case DOCUMENTOS :
				if (getRepository().deleteFromDocumento(arquivo)) {
					// Nada a fazer
				} else {
					throw new BusinessException("Não foi possível excluir o arquivo.");
				}
				break;
			case FATURACARTAO :
				if (getRepository().deleteFromFaturaCartao(arquivo)) {
					// Nada a fazer
				} else {
					throw new BusinessException("Não foi possível excluir o arquivo.");
				}
				break;
			case LANCAMENTOCONTA : 
				if (getRepository().deleteFromLancamentoConta(arquivo)) {
					// Nada a fazer
				} else {
					throw new BusinessException("Não foi possível excluir o arquivo.");
				}
				break;
			case LANCAMENTOPERIODICO :
				if (getRepository().deleteFromLancamentoPeriodico(arquivo)) {
					// Nada a fazer
				} else {
					throw new BusinessException("Não foi possível excluir o arquivo.");
				}
				break;
		}
	}
}