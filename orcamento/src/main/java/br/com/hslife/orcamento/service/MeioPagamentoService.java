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
package br.com.hslife.orcamento.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.repository.MeioPagamentoRepository;

@Service("meioPagamentoService")
public class MeioPagamentoService extends AbstractCRUDService<MeioPagamento> implements IMeioPagamento {
	
	@Autowired
	private MeioPagamentoRepository repository;
	
	public MeioPagamentoRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}

	@Override
	public void cadastrar(MeioPagamento entity) {		
		if (entity.isPadrao()) {
			getRepository().updateAllToNotDefault(entity.getUsuario());
		}
		super.cadastrar(entity);
	}
	
	@Override
	public void alterar(MeioPagamento entity) {
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
	public void excluir(MeioPagamento entity) {
		try {
			super.excluir(entity);
		} catch (DataIntegrityViolationException dive) {
			throw new BusinessException("Não é possível excluir! Existem vínculos existentes com o registro!", dive);
		} catch (Exception e) {
			throw new BusinessException("Não é possível excluir! Existem vínculos existentes com o registro!", e);
		}
	}
	
	@Override
	public List<MeioPagamento> buscarPorUsuario(Usuario usuario) {
		return getRepository().findByUsuario(usuario);
	}

	@Override
	public List<MeioPagamento> buscarPorDescricaoEUsuario(String descricao, Usuario usuario) {
		return getRepository().findByDescricaoAndUsuario(descricao, usuario);
	}
	
	public List<MeioPagamento> buscarAtivosPorUsuario(Usuario usuario) {
		return getRepository().findEnabledByUsuario(usuario);
	}

	@Override
	public List<MeioPagamento> buscarPorDescricaoUsuarioEAtivo(String descricao, Usuario usuario, boolean ativo) {
		return getRepository().findByDescricaoUsuarioAndAtivo(descricao, usuario, ativo);
	}
	
	public List<MeioPagamento> buscarDescricaoEAtivoPorUsuario(String descricao, Boolean ativo, Usuario usuario) {
		return getRepository().findDescricaoAndAtivoByUsuario(descricao, ativo, usuario);
	}
	
	@Override
	public List<MeioPagamento> buscarDescricaoEAtivoPorUsuario(String descricao, Boolean ativo, List<Usuario> usuarios) {
		return getRepository().findDescricaoAndAtivoByUsuario(descricao, ativo, usuarios);
	}
	
	public MeioPagamento buscarMeioPagamento(String descricaoMeioPagamento, Usuario usuario) {
		// Verifica se o meio de pagamento informado existe na base de dados
		List<MeioPagamento> meiosPagamento = getRepository().findByDescricaoAndUsuario(descricaoMeioPagamento, usuario);
		MeioPagamento meioPagamentoEncontrado = null;
		for (MeioPagamento m : meiosPagamento) {
			if (descricaoMeioPagamento != null && !descricaoMeioPagamento.trim().isEmpty() && m.getDescricao().contains(descricaoMeioPagamento)) {
				meioPagamentoEncontrado = m;
				break;
			}
		}
		
		if (meioPagamentoEncontrado == null)
			meioPagamentoEncontrado = getRepository().findDefaultByUsuario(usuario);
		
		return meioPagamentoEncontrado;
	}
}
