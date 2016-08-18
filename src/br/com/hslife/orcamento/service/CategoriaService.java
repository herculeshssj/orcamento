/***
  
  	Copyright (c) 2012 - 2016 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.repository.CategoriaRepository;

@Service("categoriaService")
public class CategoriaService extends AbstractCRUDService<Categoria> implements ICategoria {
	
	@Autowired
	private CategoriaRepository repository;
	
	public CategoriaRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}

	@Override
	public void cadastrar(Categoria entity) throws BusinessException {		
		if (entity.isPadrao()) {
			getRepository().updateAllToNotDefault(entity.getTipoCategoria(), entity.getUsuario());
		}
		super.cadastrar(entity);
	}
	
	@Override
	public void alterar(Categoria entity) throws BusinessException {
		if (!entity.isPadrao()) {
			if (entity.equals(getRepository().findDefaultByTipoCategoriaAndUsuario(entity.getUsuario(), entity.getTipoCategoria()))) {
				entity.setPadrao(true);
			}
		}
		if (entity.isPadrao()) {
			getRepository().updateAllToNotDefault(entity.getTipoCategoria(), entity.getUsuario());
		}
		super.alterar(entity);
	}
	
	@Override
	public void excluir(Categoria entity) throws BusinessException {
		try {
			super.excluir(entity);
		} catch (DataIntegrityViolationException dive) {
			throw new BusinessException("Não é possível excluir! Existem vínculos existentes com o registro!", dive);
		} catch (Exception e) {
			throw new BusinessException("Não é possível excluir! Existem vínculos existentes com o registro!", e);
		}
	}

	@Override
	public List<Categoria> buscarPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findByUsuario(usuario);
	}

	@Override
	public List<Categoria> buscarAtivosPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findEnabledByUsuario(usuario);
	}
	
	@Override
	public List<Categoria> buscarPorDescricaoEUsuario(String descricao, Usuario usuario) throws BusinessException {
		return getRepository().findByDescricaoAndUsuario(descricao, usuario);
	}

	@Override
	public List<Categoria> buscarPorTipoCategoriaEUsuario(TipoCategoria tipoCategoria, Usuario usuario) throws BusinessException {
		return getRepository().findByTipoCategoriaAndUsuario(tipoCategoria, usuario);
	}
	
	@Override
	public void validar(Categoria entity) throws BusinessException {
		
	}

	@Override
	public List<Categoria> buscarPorDescricaoUsuarioEAtivo(String descricao, Usuario usuario, boolean ativo) throws BusinessException {
		return getRepository().findByDescricaoUsuarioAndAtivo(descricao, usuario, ativo);
	}
	
	@Override
	public List<Categoria> buscarAtivosPorTipoCategoriaEUsuario(TipoCategoria tipoCategoria, Usuario usuario) throws BusinessException {
		return getRepository().findActiveByTipoCategoriaAndUsuario(tipoCategoria, usuario);
	}
	
	@Override
	public List<Categoria> buscarTipoCategoriaEDescricaoEAtivoPorUsuario(TipoCategoria tipoCategoria, String descricao, Boolean ativo, Usuario usuario) {
		return getRepository().findTipoCategoriaAndDescricaoAndAtivoByUsuario(tipoCategoria, descricao, ativo, usuario);
	}
	
	@Override
	public Categoria buscarCategoria(String descricaoCategoria, TipoCategoria tipoCategoria, Usuario usuario) throws BusinessException {
		// Verifica se a categoria informada existe na base de dados
		List<Categoria> categorias = getRepository().findTipoCategoriaAndDescricaoAndAtivoByUsuario(tipoCategoria, descricaoCategoria, null, usuario);
		Categoria categoriaEncontrada = null;
		for (Categoria c : categorias) {
			if (c.getDescricao().contains(descricaoCategoria)) {
				categoriaEncontrada = c;
				break;
			}
		}
		
		// Se não existir, retorna a categoria padrão para o tipo de categoria informada
		if (categoriaEncontrada == null) {
			categoriaEncontrada = getRepository().findDefaultByTipoCategoriaAndUsuario(usuario, tipoCategoria);
		}
		
		return categoriaEncontrada;
	}
}