/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor
	
	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

    para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 - 
	
	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/

package br.com.hslife.orcamento.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoPessoa;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.repository.FavorecidoRepository;

@Service("favorecidoService")
public class FavorecidoService extends AbstractCRUDService<Favorecido> implements IFavorecido {
	
	@Autowired
	private FavorecidoRepository repository;

	public FavorecidoRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}
	
	@Override
	public void cadastrar(Favorecido entity) {		
		if (entity.isPadrao()) {
			getRepository().updateAllToNotDefault(entity.getUsuario());
		}
		super.cadastrar(entity);
	}
	
	@Override
	public void alterar(Favorecido entity) {
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
	public void excluir(Favorecido entity) {
		try {
			super.excluir(entity);
		} catch (DataIntegrityViolationException dive) {
			throw new BusinessException("Não é possível excluir! Existem vínculos existentes com o registro!", dive);
		} catch (Exception e) {
			throw new BusinessException("Não é possível excluir! Existem vínculos existentes com o registro!", e);
		}
	}
	
	@Override
	public List<Favorecido> buscarPorUsuario(Usuario usuario) {
		return getRepository().findByUsuario(usuario);
	}

	@Override
	public List<Favorecido> buscarPorNomeEUsuario(String nome, Usuario usuario) {
		return getRepository().findByNomeAndUsuario(nome, usuario);
	}
	
	public List<Favorecido> buscarAtivosPorUsuario(Usuario usuario) {
		return getRepository().findEnabledByUsuario(usuario);
	}

	@Override
	public List<Favorecido> buscarPorNomeUsuarioEAtivo(String nome, Usuario usuario, boolean ativo) {
		return getRepository().findByNomeUsuarioAndAtivo(nome, usuario, ativo);
	}
	
	public List<Favorecido> buscarTipoPessoaENomeEAtivoPorUsuario(TipoPessoa tipoPessoa, String nome, Boolean ativo, Usuario usuario) {
		return getRepository().findTipoPessoaAndNomeAndAtivoByUsuario(tipoPessoa, nome, ativo, usuario);
	}
	
	@Override
	public List<Favorecido> buscarTipoPessoaENomeEAtivoPorUsuario(TipoPessoa tipoPessoa, String nome, Boolean ativo, List<Usuario> usuarios) {
		return getRepository().findTipoPessoaAndNomeAndAtivoByUsuario(tipoPessoa, nome, ativo, usuarios);
	}
	
	public Favorecido buscarFavorecido(String nomeFavorecido, Usuario usuario) {		
		// Verifica se o favorecido informado existe na base de dados
		List<Favorecido> favorecidos = getRepository().findByNomeAndUsuario(nomeFavorecido, usuario);
		Favorecido favorecidoEncontrado = null;
		for (Favorecido f : favorecidos) {
			if (nomeFavorecido != null && !nomeFavorecido.trim().isEmpty() && f.getNome().contains(nomeFavorecido)) {
				favorecidoEncontrado = f;
				break;
			}
		}
		
		if (favorecidoEncontrado == null) 
			favorecidoEncontrado = getRepository().findDefaultByUsuario(usuario);
		
		return favorecidoEncontrado;
	}
}