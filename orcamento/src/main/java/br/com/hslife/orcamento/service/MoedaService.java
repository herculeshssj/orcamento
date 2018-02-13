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

***/package br.com.hslife.orcamento.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.repository.MoedaRepository;

@Service("moedaService")
public class MoedaService extends AbstractCRUDService<Moeda> implements IMoeda {
	
	@Autowired
	private MoedaRepository repository;
	
	public MoedaRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}
	
	@Override
	public void cadastrar(Moeda entity) {
		if (entity.isPadrao()) {
			Moeda moeda = getRepository().findDefaultByUsuario(entity.getUsuario());
			if (moeda != null && !moeda.equals(entity)) {
				moeda.setPadrao(false);
				getRepository().update(moeda);
			}
		}
		super.cadastrar(entity);
	}
	
	@Override
	public void alterar(Moeda entity) {
		if (entity.isPadrao()) {
			Moeda moeda = getRepository().findDefaultByUsuario(entity.getUsuario());
			if (moeda != null && !moeda.equals(entity)) {
				moeda.setPadrao(false);
				getRepository().update(moeda);
			}
		}
		super.alterar(entity);
	}
	
	@Override
	public void excluir(Moeda entity) {
		try {
			super.excluir(entity);
		} catch (DataIntegrityViolationException dive) {
			throw new BusinessException("Não é possível excluir! Existem vínculos existentes com o registro!", dive);
		} catch (Exception e) {
			throw new BusinessException("Não é possível excluir! Existem vínculos existentes com o registro!", e);
		}
	}

	@Override
	public List<Moeda> buscarPorNomeEUsuario(String nome, Usuario usuario) {
		return getRepository().findByNomeAndUsuario(nome, usuario);
	}

	@Override
	public List<Moeda> buscarPorUsuario(Usuario usuario) {
		return getRepository().findByUsuario(usuario);
	}	
	
	@Override
	public Moeda buscarPadraoPorUsuario(Usuario usuario) {
		return getRepository().findDefaultByUsuario(usuario);
	}
	
	@Override
	public List<Moeda> buscarPorNomeUsuarioEAtivo(String nome, Usuario usuario,	boolean ativo) {
		return getRepository().findByNomeUsuarioAndAtivo(nome, usuario, ativo);
	}
	
	@Override
	public List<Moeda> buscarAtivosPorUsuario(Usuario usuario) {
		return getRepository().findActiveByUsuario(usuario);
	}
	
	@Override
	public List<String> buscarTodosCodigoMonetarioPorUsuario(Usuario usuario) {
		return getRepository().findAllCodigoMonetarioByUsuario(usuario);
	}
	
	public Moeda buscarCodigoMonetarioPorUsuario(String codigoMonetario, Usuario usuario) {
		return getRepository().findCodigoMoedaByUsuario(codigoMonetario, usuario);
	}
}
