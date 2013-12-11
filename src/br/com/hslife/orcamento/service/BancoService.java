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

import br.com.hslife.orcamento.entity.Banco;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IBanco;
import br.com.hslife.orcamento.repository.BancoRepository;

@Service("bancoService")
public class BancoService extends AbstractCRUDService<Banco> implements IBanco {
	
	@Autowired
	private BancoRepository repository;
	
	public BancoRepository getRepository() {
		return this.repository;
	}
	
	public void setRepository(final BancoRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public void cadastrar(Banco entity) throws BusinessException {
		if (entity.isPadrao()) {
			getRepository().updateAllToNotDefault(entity.getUsuario());
		}
		super.cadastrar(entity);
	}
	
	@Override
	public void alterar(Banco entity) throws BusinessException {
		if (entity.isPadrao()) {
			getRepository().updateAllToNotDefault(entity.getUsuario());
		}
		super.alterar(entity);
	}
	
	@Override
	public void excluir(Banco entity) throws BusinessException {
		if (getRepository().existsLinkages(entity)) {
			entity.setAtivo(false);
			super.alterar(entity);
		} else {
			super.excluir(entity);
		}
	}

	@Override
	public List<Banco> buscarPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findByUsuario(usuario);
	}

	@Override
	public List<Banco> buscarPorNomeEUsuario(String nome, Usuario usuario) throws BusinessException {
		return getRepository().findByNomeAndUsuario(nome, usuario);
	}

	@Override
	public void validar(Banco entity) throws BusinessException {
		
	}
	
	/*
	public void setContaComponent(ContaComponent contaComponent) {
		this.contaComponent = contaComponent;
	}

	@Override
	public void cadastrar(Banco entity) throws BusinessException {
		if (entity.isPadrao()) {
			Banco b = buscarPadraoPorUsuario(entity.getUsuario().getId());
			if (b != null) {
				b.setPadrao(false);
				alterar(b);
			}
		}
		getRepository().save(entity);
	}
	
	@Override
	public void alterar(Banco entity) throws BusinessException {
		if (entity.isPadrao()) {
			Banco b = buscarPadraoPorUsuario(entity.getUsuario().getId());
			if (b != null) {
				b.setPadrao(false);
				alterar(b);
			}
		}
		getRepository().update(entity);
	}

	@Override
	public void excluir(Banco entity) throws BusinessException {		
		if (contaComponent.buscarContasPorBanco(entity).size() == 0) {
			getRepository().delete(entity);
		} else {
			entity.setAtivo(false);
			getRepository().update(entity);
		}
	}
	
	@Override
	public List<Banco> buscarPorNome(String nome) throws BusinessException {
		return getRepository().findByNome(nome);
	}
	
	@Override
	public Banco buscarPadrao() throws BusinessException {
		return getRepository().findDefault();
	}
	
	@Override
	public Banco buscarPadraoPorUsuario(Long idUsuario) throws BusinessException {
		return getRepository().findDefaultByUsuario(idUsuario);
	}

	@Override
	public List<Banco> buscarPorUsuario(Long idUsuario)	throws BusinessException {
		return getRepository().findByUsuario(idUsuario);
	}

	@Override
	public List<Banco> buscarPorNomeEUsuario(String nome, Usuario usuario) throws BusinessException {
		return getRepository().findByNomeAndUsuario(nome, usuario);
	}

	@Override
	public Banco buscar(Long id) throws BusinessException {
		return getRepository().findById(id);
	}

	@Override
	public List<Banco> buscarTodos() throws BusinessException {
		return getRepository().findAll();
	}*/
}