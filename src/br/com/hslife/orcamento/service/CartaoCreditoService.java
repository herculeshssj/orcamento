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
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.MoedaRepository;

@Service("cartaoCreditoService")
public class CartaoCreditoService extends AbstractCRUDService<CartaoCredito> implements ICartaoCredito {
	
	@Autowired
	private CartaoCreditoRepository repository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private MoedaRepository moedaRepository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private FaturaCartaoRepository faturaCartaoRepository;

	public CartaoCreditoRepository getRepository() {
		return repository;
	}

	public void setRepository(CartaoCreditoRepository repository) {
		this.repository = repository;
	}

	public void setContaRepository(ContaRepository contaRepository) {
		this.contaRepository = contaRepository;
	}

	public void setMoedaRepository(MoedaRepository moedaRepository) {
		this.moedaRepository = moedaRepository;
	}

	public void setLancamentoContaRepository(
			LancamentoContaRepository lancamentoContaRepository) {
		this.lancamentoContaRepository = lancamentoContaRepository;
	}
	
	@Override
	public void cadastrar(CartaoCredito entity) throws BusinessException {
		// Salva o cartão, e logo em seguida a conta
		super.cadastrar(entity);
		contaRepository.save(entity.createConta());	
		
		if (entity.getTipoCartao().equals(TipoCartao.CREDITO)) {
			this.criarFaturaCartao(entity.getConta());
		}
	}
	
	@Override
	public void alterar(CartaoCredito entity) throws BusinessException {
		Conta conta = contaRepository.findByCartaoCredito(entity);
		
		// Verifica se o tipo do cartão foi alterado para poder realizar a criação da fatura
		if (!conta.getCartaoCredito().getTipoCartao().equals(entity.getTipoCartao())) {
			// Verifica se a conta já possui faturas registradas
			if (!faturaCartaoRepository.existsFaturaCartao(conta)) {
				this.criarFaturaCartao(conta);
			}
		}
		
		super.alterar(entity);
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
		
		novaFatura.setDataVencimento(vencimento.getTime());
		novaFatura.setMes(vencimento.get(Calendar.MONTH) + 1);
		novaFatura.setAno(vencimento.get(Calendar.YEAR));
		
		faturaCartaoRepository.save(novaFatura);
	}
	
	@Override
	public void excluir(CartaoCredito entity) throws BusinessException {
		if (getRepository().isSubstituto(entity)) {
			throw new BusinessException("Não é possível excluir! O cartão selecionado substituiu outro já inativo!");
		}
		Conta conta = contaRepository.findByCartaoCredito(entity);
		
		// Feito um pequeno ajuste para viabilizar a exclusão de um cartão de crédito
		// cadastrado e sem conta vinculada. O erro foi reportado na tarefa #1108
		if (conta != null) {
			// Procura uma fatura aberta para a conta do cartão selecionado
			FaturaCartao faturaAberta = faturaCartaoRepository.findFaturaCartaoAberta(conta);
			
			if (getRepository().existsLinkages(entity)) {
				throw new BusinessException("Não é possível excluir! Existe vínculo com lançamentos e/ou faturas cadastradas!");
			} else {
				// Exclui a fatura aberta caso exista
				if (faturaAberta != null) faturaCartaoRepository.delete(faturaAberta);
				contaRepository.delete(conta);
				super.excluir(entity);
			}
		} else {
			super.excluir(entity);
		}
	}
	
	@Override
	public List<CartaoCredito> buscarPorDescricaoEUsuario(String descricao, Usuario usuario) throws BusinessException {
		return getRepository().findByDescricaoAndUsuario(descricao, usuario);
	}
	
	@Override
	public List<CartaoCredito> buscarPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findByUsuario(usuario);
	}
	
	@Override
	public List<CartaoCredito> buscarSomenteCreditoPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findOnlyCartaoTipoCreditoByUsuario(usuario);
	}
	
	@Override
	public List<CartaoCredito> buscarAtivosSomenteCreditoPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findEnabledOnlyCartaoTipoCreditoByUsuario(usuario);
	}
	
	@Override
	public List<CartaoCredito> buscarDescricaoOuTipoCartaoOuAtivoPorUsuario(String descricao, TipoCartao tipoCartao, Usuario usuario, Boolean ativo) throws BusinessException {
		return getRepository().findDescricaoOrTipoCartaoOrAtivoByUsuario(descricao, tipoCartao, usuario, ativo);
	}
	
	@Override
	public void desativarCartoes() throws BusinessException {
		// Retorna os cartões que a data de expiração é igual ou menor que a data atual
		List<CartaoCredito> cartoes = getRepository().findByDataValidade(new Date());
		
		// Itera a lista de cartões, desativa e salva
		for (CartaoCredito cartao : cartoes) {
			if (cartao.isAtivo()) {
				// Busca a conta vinculada ao cartão
				Conta conta = contaRepository.findByCartaoCredito(cartao);
				conta.setAtivo(false);
				cartao.setAtivo(false);
				contaRepository.update(conta);
				getRepository().update(cartao);
			}
		}
	}
	
	@Override
	public void substituirCartao(CartaoCredito entity) throws BusinessException {
		// Desativa o cartão antigo
		entity.setAtivo(false);
		getRepository().save(entity.getCartaoSubstituto());
		getRepository().update(entity);
		
		// Vincula o novo cartão à conta do cartão antigo
		Conta conta = contaRepository.findByCartaoCredito(entity);
		conta.setCartaoCredito(entity.getCartaoSubstituto());
		conta.setDescricao(entity.getDescricao());
		contaRepository.update(conta);
	}
	
	@Override
	public void ativarCartao(CartaoCredito entity) throws BusinessException {
		entity.setAtivo(true);
		getRepository().update(entity);
		
		Conta conta = contaRepository.findByCartaoCredito(entity);
		conta.setAtivo(true);
		contaRepository.update(conta);
	}
	
	@Override
	public void desativarCartao(CartaoCredito entity) throws BusinessException {
		entity.setAtivo(false);
		getRepository().update(entity);
		
		Conta conta = contaRepository.findByCartaoCredito(entity);
		conta.setAtivo(false);
		contaRepository.update(conta);
	}
}