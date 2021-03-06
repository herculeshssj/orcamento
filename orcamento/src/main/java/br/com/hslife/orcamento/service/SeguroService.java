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

import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.facade.ILancamentoPeriodico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.Seguro;
import br.com.hslife.orcamento.facade.ISeguro;
import br.com.hslife.orcamento.repository.SeguroRepository;

import java.util.List;

@Service
public class SeguroService extends AbstractCRUDService<Seguro> implements ISeguro {

	@Autowired
	private SeguroRepository repository;

	@Autowired
	private ILancamentoPeriodico lancamentoPeriodicoService;

	public SeguroRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}


	public ILancamentoPeriodico getLancamentoPeriodicoService() {
		return lancamentoPeriodicoService;
	}

	@Override
	public void cadastrar(Seguro entity) {
		// Gera o lançamento periódico que representa o seguro
		entity.gerarDespesaFixa();

		// Cadastra o lançamento periódico vinculado ao seguro
		getLancamentoPeriodicoService().cadastrar(entity.getLancamentoPeriodico());

		// Salva o seguro
		super.cadastrar(entity);
	}

	@Override
	public void excluir(Seguro entity) {
		// Obtém o ID do lançamento periódico
		Long idLancamentoPeriodico = entity.getLancamentoPeriodico().getId();

		// Exclui o seguro
		super.excluir(entity);

		// Traz o lançamento periódico e depois exclui
		getLancamentoPeriodicoService().excluir(getLancamentoPeriodicoService().buscarPorID(idLancamentoPeriodico));
	}

	@Override
	public void encerrarSeguro(Seguro seguro) {
		// Seta o seguro como inativo e salva
		seguro.setAtivo(false);

		super.alterar(seguro);

		// Seta o lançamento periódico como encerrado e salva
		getLancamentoPeriodicoService().alterarStatusLancamento(seguro.getLancamentoPeriodico(), StatusLancamento.ENCERRADO);
	}

	@Override
	public void reativarSeguro(Seguro seguro) {
		// Seta o seguro como ativo e salva
		seguro.setAtivo(true);

		super.alterar(seguro);

		// Seta o lançamento periódico como ativo e salva
		getLancamentoPeriodicoService().alterarStatusLancamento(seguro.getLancamentoPeriodico(), StatusLancamento.ATIVO);
	}

	@Override
	public List<Seguro> buscarTodosPorUsuarioEAtivo(Usuario usuario, boolean ativo) {
		return getRepository().findAllByUsuarioAndAtivo(usuario, ativo);
	}
}
