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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.CartaoCredito;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.ConversaoMoeda;
import br.com.hslife.orcamento.entity.FaturaCartao;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusFaturaCartao;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IFaturaCartao;
import br.com.hslife.orcamento.repository.ContaRepository;
import br.com.hslife.orcamento.repository.FaturaCartaoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.MoedaRepository;
import br.com.hslife.orcamento.util.Util;

@Service("faturaCartaoService")
public class FaturaCartaoService extends AbstractCRUDService<FaturaCartao> implements IFaturaCartao {
	
	@Autowired
	private FaturaCartaoRepository repository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private MoedaRepository moedaRepository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	public FaturaCartaoRepository getRepository() {
		return repository;
	}

	public void setRepository(FaturaCartaoRepository repository) {
		this.repository = repository;
	}

	public void setLancamentoContaRepository(
			LancamentoContaRepository lancamentoContaRepository) {
		this.lancamentoContaRepository = lancamentoContaRepository;
	}

	public void setMoedaRepository(MoedaRepository moedaRepository) {
		this.moedaRepository = moedaRepository;
	}

	public void setContaRepository(ContaRepository contaRepository) {
		this.contaRepository = contaRepository;
	}

	@Override
	public void validar(FaturaCartao entity) throws BusinessException {
		FaturaCartao ultimaFaturaFechada = getRepository().lastFaturaCartaoFechada(entity.getConta());
		if (Util.formataDataHora(entity.getDataVencimento(), Util.DATA).equals(Util.formataDataHora(ultimaFaturaFechada.getDataVencimento(), Util.DATA)) 
				|| entity.getDataVencimento().before(ultimaFaturaFechada.getDataVencimento())) {
			throw new BusinessException("Data de vencimento não pode ser igual ou anterior ao vencimento da última fatura fechada!");
		}
		
		// Impede que haja mais de uma fatura com a mesma data de vencimento para o cartão selecionado
		List<FaturaCartao> faturas = getRepository().findByVencimentoAndStatusFatura(entity.getConta(), entity.getDataVencimento(), entity.getStatusFaturaCartao());
		if (faturas != null && faturas.size() > 1) {
			throw new BusinessException("Data de vencimento já informada para uma fatura cadastrada!");
		}
	}
	
	public double saldoDevedorUltimaFatura(CartaoCredito cartao) throws BusinessException {
		FaturaCartao fatura = getRepository().lastFaturaCartaoFechada(cartao.getConta());
		if (fatura != null && !fatura.getStatusFaturaCartao().equals(StatusFaturaCartao.QUITADA)) {
			return fatura.getValorFatura() + fatura.getSaldoDevedor();
		} else {
			return 0.0;
		}
	}
	
	@Override
	public void fecharFatura(FaturaCartao entity, List<Moeda> conversoes) throws BusinessException {
		// Busca a fatura que será fechada
		FaturaCartao faturaCartao = this.buscarPorID(entity.getId());		
		
		// Obtém o saldo devedor da fatura atualmente fechada
		if (this.saldoDevedorUltimaFatura(entity.getConta().getCartaoCredito()) == 0.0)
			faturaCartao.setSaldoDevedor(faturaCartao.getSaldoDevedor() + this.saldoDevedorUltimaFatura(entity.getConta().getCartaoCredito()));
		else
			faturaCartao.setSaldoDevedor(this.saldoDevedorUltimaFatura(entity.getConta().getCartaoCredito()));
		
		// Atribuições dos valores
		faturaCartao.setDataFechamento(entity.getDataFechamento());
		faturaCartao.setStatusFaturaCartao(StatusFaturaCartao.FECHADA);
		
		// Calcula o valor total da fatura
		double totalFatura = 0.0;
		for (Moeda moeda : conversoes) {
			if (moeda.isPadrao()) { 
				faturaCartao.setMoeda(moeda);
				moeda.setTotalConvertido(Util.arredondar(moeda.getTotal()));
			} else {
				moeda.setTotalConvertido(Util.arredondar(moeda.getTotal() * moeda.getTaxaConversao()));
			}
			totalFatura += moeda.getTotalConvertido();
		}
		// Adiciona ao total da fatura o valor da fatura vencida		
		faturaCartao.setValorFatura(totalFatura);
		faturaCartao.setValorMinimo(Util.arredondar((Math.abs(totalFatura + faturaCartao.getSaldoDevedor()) * 15) / 100)); // 15% do valor da fatura
		
		// Popula a lista de conversões de moeda
		for (Moeda moeda : conversoes) {
			ConversaoMoeda conversao = new ConversaoMoeda();
			conversao.setFaturaCartao(faturaCartao);
			conversao.setMoeda(moeda);
			conversao.setTaxaConversao(moeda.getTaxaConversao());
			conversao.setValor(moeda.getTotalConvertido());
			faturaCartao.getConversoesMoeda().add(conversao);
		}
		
		// Altera o status da fatura atualmente fechada para VENCIDA		
		FaturaCartao faturaVencida = getRepository().lastFaturaCartaoFechada(entity.getConta());
		if (faturaVencida != null && faturaVencida.getStatusFaturaCartao().equals(StatusFaturaCartao.FECHADA)) {
			faturaVencida.setStatusFaturaCartao(StatusFaturaCartao.VENCIDA);			
			// Seta as informações de pagamento da fatura
			faturaVencida.setDataPagamento(entity.getDataFechamento());
			faturaVencida.setValorPago(Math.abs(faturaVencida.getValorFatura()));
			faturaVencida.setValorMinimo(Util.arredondar((Math.abs(faturaVencida.getValorFatura()) * 15) / 100));			
			// Salva a fatura
			getRepository().update(faturaVencida);			
		}
		
		// Salva a fatura do cartão
		getRepository().update(faturaCartao);
		
		// Quita todos os lançamentos vinculados à fatura
		for (LancamentoConta lancamento : faturaCartao.getDetalheFatura()) {
			LancamentoConta l = lancamentoContaRepository.findById(lancamento.getId());
			l.setQuitado(true);
			lancamentoContaRepository.update(l);
		}
		
		// Verifica se existe fatura futura para alterar o status
		if (getRepository().findNextFaturaCartaoFutura(entity.getConta()) == null) {
			// Cria a próxima fatura ABERTA
			FaturaCartao novaFatura = new FaturaCartao();
			novaFatura.setConta(entity.getConta());
			novaFatura.setMoeda(moedaRepository.findDefaultByUsuario(entity.getConta().getUsuario()));
			novaFatura.setStatusFaturaCartao(StatusFaturaCartao.ABERTA);
			// Data de vencimento da próxima fatura
			Calendar vencimento = Calendar.getInstance();
			vencimento.setTime(entity.getDataVencimento());		
			vencimento.add(Calendar.MONTH, 1);		
			novaFatura.setDataVencimento(vencimento.getTime());
					
			// Salva a nova fatura
			getRepository().save(novaFatura);
		} else {
			FaturaCartao novaFatura = getRepository().findNextFaturaCartaoFutura(entity.getConta());
			novaFatura.setStatusFaturaCartao(StatusFaturaCartao.ABERTA);
			getRepository().update(novaFatura);
		}
	}
	
	@Override
	public void reabrirFatura(FaturaCartao entity) throws BusinessException {
		// Busca a fatura atualmente aberta e muda seu status para FUTURA
		FaturaCartao faturaAtual = getRepository().findFaturaCartaoAberta(entity.getConta());
		if (faturaAtual != null) {
			faturaAtual.setStatusFaturaCartao(StatusFaturaCartao.FUTURA);
			getRepository().update(faturaAtual);
		}
		
		// Busca a fatura que será reaberta
		FaturaCartao fatura = getRepository().findById(entity.getId());

		// Atribuições dos valores
		fatura.setDataFechamento(null);
		fatura.setStatusFaturaCartao(StatusFaturaCartao.ABERTA);

		// Salva a fatura do cartão
		getRepository().update(fatura);

				
		// Reabre todos os lançamentos vinculados à fatura
		for (LancamentoConta lancamento : fatura.getDetalheFatura()) {
			LancamentoConta l = lancamentoContaRepository.findById(lancamento.getId());
			l.setQuitado(false);
			lancamentoContaRepository.update(l);
		}
	}
	
	@Override
	public void quitarFaturaDebitoConta(FaturaCartao faturaCartao, Conta contaCorrente,	double valorAQuitar, Date dataPagamento) throws BusinessException {
		// TODO reativar a validação quando o problema de casas decimais e sinal dos valores monetários for completamente resolvido.
		
		//if (Math.abs(Util.arredondar(valorAQuitar)) < Math.abs(Util.arredondar(faturaCartao.getValorMinimo()))) {
		//	throw new BusinessException("Valor de pagamento não pode ser menor que o valor mínimo da fatura!");
		//}
		
		//if (Math.abs(Util.arredondar(valorAQuitar)) > Math.abs(Util.arredondar(faturaCartao.getValorFatura() + faturaCartao.getSaldoDevedor()))) {
		//	throw new BusinessException("Não é possível quitar um valor maior que o valor total da fatura!");
		//}
			
		// Traz a fatura e seta seus atributos
		FaturaCartao fatura = getRepository().findById(faturaCartao.getId());
		fatura.setStatusFaturaCartao(StatusFaturaCartao.QUITADA);
		fatura.setDataPagamento(dataPagamento);
		fatura.setValorPago(valorAQuitar);
		fatura.setValorMinimo(faturaCartao.getValorMinimo());
		
		// Cria o lançamento que irá quitar a fatura
		LancamentoConta lancamentoPagamento = new LancamentoConta();
		lancamentoPagamento.setConta(contaCorrente);
		lancamentoPagamento.setDataPagamento(dataPagamento);
		lancamentoPagamento.setDescricao("Pagamento Fatura " + fatura.getLabel() + ", " + faturaCartao.getConta().getCartaoCredito().getDescricao());
		lancamentoPagamento.setQuitado(true);
		lancamentoPagamento.setValorPago(valorAQuitar);
		lancamentoPagamento.setTipoLancamento(TipoLancamento.DESPESA);
		
		// Determina o valor do saldo devedor
		double saldoDevedor = (faturaCartao.getValorFatura() + faturaCartao.getSaldoDevedor()) - valorAQuitar;
		
		// Salva o lançamento
		lancamentoContaRepository.save(lancamentoPagamento);
		
		// Atribui o lançamento à fatura
		fatura.setLancamentoPagamento(lancamentoPagamento);
		
		// Salva a fatura paga
		getRepository().update(fatura);
		
		// Busca a fatura aberta e atribui o saldo devedor a ela. Depois salva
		FaturaCartao faturaAtual = getRepository().findFaturaCartaoAberta(faturaCartao.getConta());
		faturaAtual.setSaldoDevedor(saldoDevedor);
		getRepository().update(faturaAtual);
	}
	
	@Override
	public void quitarFaturaParcelamento() throws BusinessException {
		throw new BusinessException("Não implementado!");
	}
	
	@Override
	public void quitarFaturaLancamentoSelecionado(FaturaCartao faturaCartao, LancamentoConta lancamentoConta) throws BusinessException {
		// Verifica se o lançamento selecionado já foi vinculado com outra fatura
		if (lancamentoContaRepository.existsLinkagePagamentoFaturaCartao(lancamentoConta)) {
			throw new BusinessException("Lançamento selecionado já foi usado para quitar outra fatura!");
		}
		
		FaturaCartao fatura = getRepository().findById(faturaCartao.getId());
		fatura.setStatusFaturaCartao(StatusFaturaCartao.QUITADA);
		fatura.setDataPagamento(lancamentoConta.getDataPagamento());
		fatura.setValorPago(lancamentoConta.getValorPago());
		fatura.setValorMinimo(faturaCartao.getValorMinimo());
		
		double saldoDevedor = (faturaCartao.getValorFatura() + faturaCartao.getSaldoDevedor()) - lancamentoConta.getValorPago();
		
		// Atribui o lançamento à fatura
		fatura.setLancamentoPagamento(lancamentoConta);
				
		// Salva a fatura paga
		getRepository().update(fatura);
				
		// Busca a fatura aberta e atribui o saldo devedor a ela. Depois salva
		FaturaCartao faturaAtual = getRepository().findFaturaCartaoAberta(faturaCartao.getConta());
		faturaAtual.setSaldoDevedor(saldoDevedor);
		getRepository().update(faturaAtual);
	}

	@Override
	public List<FaturaCartao> buscarTodosPorCartaoCredito(Conta conta) throws BusinessException {
		return getRepository().findAllByCartaoCredito(conta);
	}
	
	@Override
	public List<FaturaCartao> buscarTodos() throws BusinessException {
		return getRepository().findAll();
	}
	
	@Override
	public List<FaturaCartao> buscarTodosPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findAllByUsuario(usuario);
	}
}