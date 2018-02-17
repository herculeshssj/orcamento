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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.CartaoCredito;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FaturaCartao;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusFaturaCartao;
import br.com.hslife.orcamento.enumeration.TipoCartao;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICartaoCredito;
import br.com.hslife.orcamento.repository.CartaoCreditoRepository;
import br.com.hslife.orcamento.repository.ContaRepository;
import br.com.hslife.orcamento.repository.FaturaCartaoRepository;

@Service("cartaoCreditoService")
public class CartaoCreditoService extends AbstractCRUDService<CartaoCredito> implements ICartaoCredito {
	
	@Autowired
	private CartaoCreditoRepository repository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private FaturaCartaoRepository faturaCartaoRepository;

	public CartaoCreditoRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}
	
	public ContaRepository getContaRepository() {
		this.contaRepository.setSessionFactory(this.sessionFactory);
		return contaRepository;
	}

	public FaturaCartaoRepository getFaturaCartaoRepository() {
		this.faturaCartaoRepository.setSessionFactory(this.sessionFactory);
		return faturaCartaoRepository;
	}
	
	@Override
	public void validar(CartaoCredito entity) {
		super.validar(entity);
		
		// Verifica se existe conta corrente/poupança para registrar as faturas
		// dos cartões de crédito
		if (entity.getTipoCartao().equals(TipoCartao.CREDITO))
			if (!getContaRepository().existsContaCorrentePoupanca()) 
				throw new BusinessException("Não existem contas corrente/poupança para registrar os pagamentos das faturas!");
	}

	@Override
	public void cadastrar(CartaoCredito entity) {
		// Salva o cartão, e logo em seguida a conta
		super.cadastrar(entity);
		getContaRepository().save(entity.createConta());	
		
		if (entity.getTipoCartao().equals(TipoCartao.CREDITO)) {
			this.criarFaturaCartao(entity.getConta());
		}
	}
	
	@Override
	public void alterar(CartaoCredito entity) {
		Conta conta = getContaRepository().findByCartaoCredito(entity);
		
		// Verifica se o tipo do cartão foi alterado para poder realizar a criação da fatura
		if (!conta.getCartaoCredito().getTipoCartao().equals(entity.getTipoCartao())) {
			// Verifica se a conta já possui faturas registradas
			if (!getFaturaCartaoRepository().existsFaturaCartao(conta)) {
				this.criarFaturaCartao(conta);
			}
		}
		
		super.alterar(entity);
		
		// Atualiza a descrição da conta se for diferente do cartão de crédito
		if (!conta.getDescricao().equalsIgnoreCase(entity.getDescricao())) {
			conta.setDescricao(entity.getDescricao());
			getContaRepository().update(conta);
		}
	}
	
	private void criarFaturaCartao(Conta conta) {
		// Cria uma nova fatura para o cartão
		FaturaCartao novaFatura = new FaturaCartao();
		novaFatura.setConta(conta);
		novaFatura.setStatusFaturaCartao(StatusFaturaCartao.ABERTA);
		// Data de vencimento da fatura
		Calendar vencimento = Calendar.getInstance();
		// Fechamento < Vencimento = mesmo mês; Fechamento >= Vencimento = mês seguinte
		if (novaFatura.getConta().getCartaoCredito().getDiaFechamentoFatura() < novaFatura.getConta().getCartaoCredito().getDiaVencimentoFatura()) {
			vencimento.set(Calendar.DAY_OF_MONTH, novaFatura.getConta().getCartaoCredito().getDiaVencimentoFatura());
		} else {
			vencimento.set(Calendar.DAY_OF_MONTH, novaFatura.getConta().getCartaoCredito().getDiaVencimentoFatura());
			vencimento.add(Calendar.MONTH, 1);
		}
		
		// Verifica se data de vencimento da fatura não conflita com outra existente
		while (getFaturaCartaoRepository().existsFaturaCartaoByContaAndDataVencimento(conta, vencimento.getTime())) {
			vencimento.add(Calendar.MONTH, 1);
		}
		
		novaFatura.setDataVencimento(vencimento.getTime());
		novaFatura.setMes(vencimento.get(Calendar.MONTH) + 1);
		novaFatura.setAno(vencimento.get(Calendar.YEAR));
		
		getFaturaCartaoRepository().save(novaFatura);
	}
	
	@Override
	public void excluir(CartaoCredito entity) {
		if (getRepository().isSubstituto(entity)) {
			throw new BusinessException("Não é possível excluir! O cartão selecionado substituiu outro já inativo!");
		}
		//Conta conta = getContaRepository().findByCartaoCredito(entity);
		
		// Feito um pequeno ajuste para viabilizar a exclusão de um cartão de crédito
		// cadastrado e sem conta vinculada. O erro foi reportado na tarefa #1108
		if (entity.getConta() != null) {
			// Procura uma fatura aberta para a conta do cartão selecionado
			FaturaCartao faturaAberta = getFaturaCartaoRepository().findFaturaCartaoAberta(entity.getConta());
			
			if (getRepository().existsLinkages(entity)) {
				throw new BusinessException("Não é possível excluir! Existe vínculo com lançamentos e/ou faturas cadastradas, ou encontra-se compartilhado!");
			} else {
				// Exclui a fatura aberta caso exista
				if (faturaAberta != null) getFaturaCartaoRepository().delete(faturaAberta);
				getContaRepository().delete(entity.getConta());
				super.excluir(entity);
			}
		} else {
			super.excluir(entity);
		}
	}
	
	@Override
	public List<CartaoCredito> buscarPorDescricaoEUsuario(String descricao, Usuario usuario) {
		return getRepository().findByDescricaoAndUsuario(descricao, usuario);
	}
	
	@Override
	public List<CartaoCredito> buscarPorUsuario(Usuario usuario) {
		return getRepository().findByUsuario(usuario);
	}
	
	@Override
	public List<CartaoCredito> buscarSomenteCreditoPorUsuario(Usuario usuario) {
		return getRepository().findOnlyCartaoTipoCreditoByUsuario(usuario);
	}
	
	@Override
	public List<CartaoCredito> buscarAtivosSomenteCreditoPorUsuario(Usuario usuario) {
		return getRepository().findEnabledOnlyCartaoTipoCreditoByUsuario(usuario);
	}
	
	@Override
	public List<CartaoCredito> buscarDescricaoOuTipoCartaoOuAtivoPorUsuario(String descricao, TipoCartao tipoCartao, Usuario usuario, Boolean ativo) {
		return getRepository().findDescricaoOrTipoCartaoOrAtivoByUsuario(descricao, tipoCartao, usuario, ativo);
	}
	
	@Override
	public void desativarCartoes() {
		// Retorna os cartões que a data de expiração é igual ou menor que a data atual
		List<CartaoCredito> cartoes = getRepository().findByDataValidade(new Date());
		
		// Itera a lista de cartões, desativa e salva
		for (CartaoCredito cartao : cartoes) {
			if (cartao.isAtivo()) {
				// Busca a conta vinculada ao cartão
				Conta conta = getContaRepository().findByCartaoCredito(cartao);
				conta.setAtivo(false);
				cartao.setAtivo(false);
				getContaRepository().update(conta);
				getRepository().update(cartao);
			}
		}
	}
	
	@Override
	public void substituirCartao(CartaoCredito entity) {
		// Desativa o cartão antigo
		entity.setAtivo(false);
		getRepository().save(entity.getCartaoSubstituto());
		getRepository().update(entity);
		
		// Vincula o novo cartão à conta do cartão antigo
		Conta conta = getContaRepository().findByCartaoCredito(entity);
		conta.setCartaoCredito(entity.getCartaoSubstituto());
		conta.setDescricao(entity.getDescricao());
		getContaRepository().update(conta);
	}
	
	@Override
	public void ativarCartao(CartaoCredito entity) {
		entity.setAtivo(true);
		getRepository().update(entity);
		
		Conta conta = getContaRepository().findByCartaoCredito(entity);
		conta.setAtivo(true);
		getContaRepository().update(conta);
	}
	
	@Override
	public void desativarCartao(CartaoCredito entity) {
		entity.setAtivo(false);
		getRepository().update(entity);
		
		Conta conta = getContaRepository().findByCartaoCredito(entity);
		conta.setAtivo(false);
		getContaRepository().update(conta);
	}
	
	@Override
	public void repararInconsistênciaFatura(CartaoCredito cartaoCredito) {
		// Traz a conta do cartão selecionado
		Conta conta = getContaRepository().findByCartaoCredito(cartaoCredito);
		
		// 1ª verificação - confere se existe faturas para o cartão informado
		boolean existeFaturas = getFaturaCartaoRepository().existsFaturaCartao(conta);
		
		// 2ª verificação - confere se existe fatura aberta para o cartão informado
		boolean existeFaturaAberta = getFaturaCartaoRepository().findFaturaCartaoAberta(conta) != null ? true : false;
		
		if (!existeFaturas) {
			// Cria uma nova fatura para o cartão informado
			this.criarFaturaCartao(conta);
			return;
		}
		
		if (!existeFaturaAberta) {
			// Cria uma nova fatura aberta para o cartão informado
			this.criarFaturaCartao(conta);
			return;
		}
		
		// Faz uma nova verificação para saber se a inconsistência foi resolvida ou não
		
		// 1ª verificação - confere se existe faturas para o cartão informado
		existeFaturas = getFaturaCartaoRepository().existsFaturaCartao(conta);
		
		// 2ª verificação - confere se existe fatura aberta para o cartão informado
		existeFaturaAberta = getFaturaCartaoRepository().findFaturaCartaoAberta(conta) != null ? true : false;
		
		if (!existeFaturas) {
			throw new BusinessException("Não foi possível criar uma fatura para o cartão selecionado!");
		}
		
		if (!existeFaturaAberta) {
			throw new BusinessException("Não foi possível criar uma fatura aberta para o cartão selecionado!");
		}
	}
}
