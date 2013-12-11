/***
  
  	Copyright (c) 2012, 2013, 2014 Hércules S. S. José

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

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site HSlife
    
    no endereco www.hslife.com.br ou escreva para a Fundação do Software 
    
    Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor acesse o 

    endereço www.hslife.com.br, pelo e-mail contato@hslife.com.br ou escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
 ***/

package br.com.hslife.orcamento.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.Identidade;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoIdentidade;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IIdentidade;
import br.com.hslife.orcamento.repository.IdentidadeRepository;
import br.com.hslife.orcamento.util.Util;

@Service("identidadeService")
public class IdentidadeService extends AbstractCRUDService<Identidade> implements IIdentidade {
	
	@Autowired
	private IdentidadeRepository repository;

	public IdentidadeRepository getRepository() {
		return repository;
	}

	public void setRepository(IdentidadeRepository repository) {
		this.repository = repository;
	}

	@Override
	public void validar(Identidade entity) throws BusinessException {
		// Verifica se a identidade passada é um CPF válido
		if (entity.getTipoIdentidade().equals(TipoIdentidade.CPF) && entity.getNumero() != null && !entity.getNumero().trim().isEmpty()) {
			if (!Util.validaCPF(entity.getNumero()))
				throw new BusinessException("O CPF informado não é válido!");
		}
		
		// Verifica se todos os campos foram preenchidos no RG
		if (entity.getTipoIdentidade().equals(TipoIdentidade.RG)) {
			if ( (Util.eVazio(entity.getNumero()) && Util.eVazio(entity.getOrgaoExpedidor()) && Util.eVazio(entity.getUf()) && entity.getDataExpedicao() == null)
					|| (!Util.eVazio(entity.getNumero()) && !Util.eVazio(entity.getOrgaoExpedidor()) && !Util.eVazio(entity.getUf()) && entity.getDataExpedicao() != null)) {
				// Nada a fazer. Pode gravar sem problemas
			} else {
				throw new BusinessException("Todos os campos do RG são obrigatórios!");
			}
		}
		
		// Verifica se todos os campos foram preenchidos no Título de Eleitor
		if (entity.getTipoIdentidade().equals(TipoIdentidade.TITULO_ELEITOR)) {
			if ( (Util.eVazio(entity.getNumero()) && Util.eVazio(entity.getMunicipio()) && Util.eVazio(entity.getUf()) && Util.eVazio(entity.getZona()) && Util.eVazio(entity.getSecao()) && entity.getDataExpedicao() == null) 
					|| (!Util.eVazio(entity.getNumero()) && !Util.eVazio(entity.getMunicipio()) && !Util.eVazio(entity.getUf()) && !Util.eVazio(entity.getZona()) && !Util.eVazio(entity.getSecao()) && entity.getDataExpedicao() != null) ) {
				// Nada a fazer. Pode gravar sem problemas.
			} else {
				throw new BusinessException("Todos os campos do título de eleitor são obrigatórios!");
			}
		}
		
		// Verifica se todos os campos foram preenchidos na carteira de trabalho
		if (entity.getTipoIdentidade().equals(TipoIdentidade.CARTEIRA_TRABALHO)) {
			if ( (Util.eVazio(entity.getNumero()) && Util.eVazio(entity.getSerie()) && Util.eVazio(entity.getOrgaoExpedidor()) && Util.eVazio(entity.getUf()) && entity.getDataExpedicao() == null)
					|| (!Util.eVazio(entity.getNumero()) && !Util.eVazio(entity.getSerie()) && !Util.eVazio(entity.getOrgaoExpedidor()) && !Util.eVazio(entity.getUf()) && entity.getDataExpedicao() != null)) {
				// Nada a fazer. Pode gravar sem problemas
			} else {
				throw new BusinessException("Todos os campos da carteira de trabalho são obrigatórios!");
			}
		}
		
		// Verifica se todos os campos foram preenchidos na certidão de nascimento
		if (entity.getTipoIdentidade().equals(TipoIdentidade.CERTIDAO_NASCIMENTO)) {
			if ( (Util.eVazio(entity.getNumero()) && Util.eVazio(entity.getOrgaoExpedidor()) && Util.eVazio(entity.getLivro()) && Util.eVazio(entity.getFolha()) && entity.getDataExpedicao() == null)
					|| (!Util.eVazio(entity.getNumero()) && !Util.eVazio(entity.getOrgaoExpedidor()) && !Util.eVazio(entity.getLivro()) && !Util.eVazio(entity.getFolha()) && entity.getDataExpedicao() != null) ) {
				// Nada a fazer. Pode gravar sem problemas
			} else {
				throw new BusinessException("Todos os campos da certidão de nascimento são obrigatórios!");
			}
		}
		
		// Verifica se todos os campos foram preenchidos na carteira de motorista
		if (entity.getTipoIdentidade().equals(TipoIdentidade.CNH)) {
			if ( (Util.eVazio(entity.getNumero()) && Util.eVazio(entity.getCategoria()) && Util.eVazio(entity.getMunicipio()) && Util.eVazio(entity.getUf()) && entity.getDataExpedicao() == null && entity.getDataPrimeiraHabilitacao() == null && entity.getDataValidade() == null)
					|| (!Util.eVazio(entity.getNumero()) && !Util.eVazio(entity.getCategoria()) && !Util.eVazio(entity.getMunicipio()) && !Util.eVazio(entity.getUf()) && entity.getDataExpedicao() != null && entity.getDataPrimeiraHabilitacao() != null && entity.getDataValidade() != null)) {
				// nada a fazer. Pode gravar sem problemas
			} else {
				throw new BusinessException("Todos os campos da carteira de motorista são obrigatórios!");
			}
		}
		
		// Verifica se todos os campos foram preenchidos no certificado de reservista
		if (entity.getTipoIdentidade().equals(TipoIdentidade.DOC_MILITAR)) {
			if ( (Util.eVazio(entity.getNumero()) && Util.eVazio(entity.getSerie()) && Util.eVazio(entity.getOrgaoExpedidor()) && Util.eVazio(entity.getMunicipio()) && Util.eVazio(entity.getUf()))
					|| (!Util.eVazio(entity.getNumero()) && !Util.eVazio(entity.getSerie()) && !Util.eVazio(entity.getOrgaoExpedidor()) && !Util.eVazio(entity.getMunicipio()) && !Util.eVazio(entity.getUf())) ) {
				// Nada a fazer. Pode gravar sem problemas
			} else {
				throw new BusinessException("Todos os campos do certificado de reservista são obrigatórios!");
			}
		}
		
		// Verifica se todos os campos foram preenchidos no passaporte
		if (entity.getTipoIdentidade().equals(TipoIdentidade.PASSAPORTE)) {
			if ( (Util.eVazio(entity.getNumero()) && Util.eVazio(entity.getPais()) && Util.eVazio(entity.getOrgaoExpedidor()) && entity.getDataExpedicao() == null && entity.getDataValidade() == null)
					|| (!Util.eVazio(entity.getNumero()) && !Util.eVazio(entity.getPais()) && !Util.eVazio(entity.getOrgaoExpedidor()) && entity.getDataExpedicao() != null && entity.getDataValidade() != null)) {
				// Nada a fazer. Pode gravar sem problemas
			} else {
				throw new BusinessException("Todos os campos do passaporte são obrigatórios!");
			}
		}
	}

	@Override
	public void salvarDocumentos(List<Identidade> documentos) throws BusinessException {
		for (Identidade identidade : documentos) {
			if (identidade.getId() == null || getRepository().findById(identidade.getId()) == null) {
				getRepository().save(identidade);
			} else {
				getRepository().update(identidade);
			}
		}		
	}

	@Override
	public Identidade buscarPorUsuarioETipoIdentidade(Usuario usuario, TipoIdentidade tipoIdentidade) throws BusinessException {
		return getRepository().findByUsuarioAndTipoIdentidade(usuario, tipoIdentidade);
	}	
}