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
package br.com.hslife.orcamento.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.Despensa;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IDespensa;
import br.com.hslife.orcamento.repository.DespensaRepository;
import br.com.hslife.orcamento.repository.ItemDespensaRepository;

@Service("despensaService")
public class DespensaService extends AbstractCRUDService<Despensa> implements IDespensa{

	@Autowired
	private DespensaRepository repository;
	
	@Autowired
	private ItemDespensaRepository itemDespensaRepository;

	public DespensaRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}
	
	public ItemDespensaRepository getItemDespensaRepository() {
		this.itemDespensaRepository.setSessionFactory(this.sessionFactory);
		return itemDespensaRepository;
	}

	@Override
	public void excluir(Despensa entity) {
		if (getItemDespensaRepository().findByDespensa(entity) != null
				&& !getItemDespensaRepository().findByDespensa(entity).isEmpty()) {
			throw new BusinessException("Não é possível excluir! Existem itens vinculados a esta despensa!");
		}
		super.excluir(entity);
	}
	
	@Override
	public List<Despensa> buscarPorDescricaoEUsuario(String descricao, Usuario usuario) {
		return getRepository().findByDescricaoAndUsuario(descricao, usuario);
	}

	@Override
	public List<Despensa> buscarTodosPorUsuario(Usuario usuario) {
		return getRepository().findAllByUsuario(usuario);
	}
}
