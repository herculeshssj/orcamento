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

import br.com.hslife.orcamento.entity.BuscaSalva;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IBuscaSalva;
import br.com.hslife.orcamento.repository.BuscaSalvaRepository;

@Service("buscaSalvaService")
public class BuscaSalvaService extends AbstractCRUDService<BuscaSalva> implements IBuscaSalva {
	
	@Autowired
	private BuscaSalvaRepository repository;

	public BuscaSalvaRepository getRepository() {
		return repository;
	}

	public void setRepository(BuscaSalvaRepository repository) {
		this.repository = repository;
	}

	public List<BuscaSalva> buscarPorConta(Conta conta) throws BusinessException {
		return getRepository().findByConta(conta);
	}
	
	public List<BuscaSalva> buscarTodosPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findAllByUsuario(usuario);
	}
	
	public List<BuscaSalva> buscarTodosContaAtivaPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findAllEnabledContaByUsuario(usuario);
	}
	
	@Override
	public List<BuscaSalva> buscarTodosPorContaEUsuario(Conta conta, Usuario usuario) throws BusinessException {	
		return getRepository().findAllByContaAndUsuario(conta, usuario);
	}
	
	public List<BuscaSalva> buscarTodosContaAtivaPorContaEUsuario(Conta conta, Usuario usuario) throws BusinessException {
		return getRepository().findAllEnabledContaByContaAndUsuario(conta, usuario);
	}
	
	@Override
	public List<BuscaSalva> buscarTodosContaCartaoPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findAllContaCartaoByUsuario(usuario);
	}
	
	@Override
	public List<BuscaSalva> buscarTodosContaCartaoAtivaPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findAllEnabledContaCartaoByUsuario(usuario);
	}

	@Override
	public void validar(BuscaSalva entity) throws BusinessException {
		// TODO Auto-generated method stub
		
	}
}
