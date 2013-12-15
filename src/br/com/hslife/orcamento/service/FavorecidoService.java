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

import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.repository.FavorecidoRepository;

@Service("favorecidoService")
public class FavorecidoService extends AbstractCRUDService<Favorecido> implements IFavorecido {
	
	@Autowired
	private FavorecidoRepository repository;

	public FavorecidoRepository getRepository() {
		return repository;
	}

	public void setRepository(FavorecidoRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public void cadastrar(Favorecido entity) throws BusinessException {		
		if (entity.isPadrao()) {
			getRepository().updateAllToNotDefault(entity.getUsuario());
		}
		super.cadastrar(entity);
	}
	
	@Override
	public void alterar(Favorecido entity) throws BusinessException {
		if (!entity.isPadrao()) {
			if (entity.equals(getRepository().findDefaultByUsuario(entity.getUsuario()))) {
				entity.setPadrao(true);
			}
		}
		if (entity.isPadrao()) {
			getRepository().updateAllToNotDefault(entity.getUsuario());
		}
		super.alterar(entity);
	}
	
	@Override
	public void excluir(Favorecido entity) throws BusinessException {
		if (getRepository().existsLinkages(entity) ) {
			entity.setAtivo(false);
			super.alterar(entity);
		} else {
			super.excluir(entity);
		}
	}
	
	@Override
	public List<Favorecido> buscarPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findByUsuario(usuario);
	}

	@Override
	public List<Favorecido> buscarPorNomeEUsuario(String nome, Usuario usuario) throws BusinessException {
		return getRepository().findByNomeAndUsuario(nome, usuario);
	}
	
	
	@Override
	public void validar(Favorecido entity) throws BusinessException {
		
	}
	
	public List<Favorecido> buscarAtivosPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findEnabledByUsuario(usuario);
	}
}