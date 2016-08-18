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
import br.com.hslife.orcamento.entity.ConversaoMoeda;
import br.com.hslife.orcamento.entity.FaturaCartao;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.PeriodoLancamento;
import br.com.hslife.orcamento.enumeration.StatusFaturaCartao;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IFaturaCartao;
import br.com.hslife.orcamento.facade.ILancamentoPeriodico;
import br.com.hslife.orcamento.repository.FaturaCartaoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.util.Util;

@Service("faturaCartaoService")
public class FaturaCartaoService extends AbstractCRUDService<FaturaCartao> implements IFaturaCartao {
	
	@Autowired
	private FaturaCartaoRepository repository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private FechamentoPeriodoService fechamentoPeriodoService;
	
	@Autowired
	private ILancamentoPeriodico lancamentoPeriodicoService;
	
	public FaturaCartaoRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}

	public ILancamentoPeriodico getLancamentoPeriodicoService() {
		return lancamentoPeriodicoService;
	}

	public FechamentoPeriodoService getFechamentoPeriodoService() {
		return fechamentoPeriodoService;
	}

	public LancamentoContaRepository getLancamentoContaRepository() {
		this.lancamentoContaRepository.setSessionFactory(this.sessionFactory);
		return lancamentoContaRepository;
	}

	@Override
	public void validar(FaturaCartao entity) throws BusinessException {
		// Impede que haja mais de uma fatura com a mesma data de vencimento para o cartão selecionado
		List<FaturaCartao> faturas = getRepository().findByContaAndDataVencimento(entity.getConta(), entity.getDataVencimento());
		if (faturas != null && !faturas.isEmpty()) {
			faturas.remove(entity);
			if (faturas.size() != 0)
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
	
	@SuppressWarnings("deprecation")
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
		faturaCartao.setDataVencimento(entity.getDataVencimento());
		faturaCartao.setDataFechamento(entity.getDataFechamento());
		faturaCartao.setStatusFaturaCartao(StatusFaturaCartao.FECHADA);
		
		// Calcula o valor total da fatura
		double totalFatura = 0.0;
		for (Moeda moeda : conversoes) {
			if (moeda.isPadrao()) { 
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
		
		// Verifica se a fatura possui saldo total e saldo devedor zerado
		// Caso tenha, a fatura é automaticamento quitada
		if (faturaCartao.getSaldoDevedor() == 0 && faturaCartao.getValorFatura() == 0) {
			faturaCartao.setStatusFaturaCartao(StatusFaturaCartao.QUITADA);
		}
		
		// Salva a fatura do cartão
		getRepository().update(faturaCartao);
		
		// Quita todos os lançamentos vinculados à fatura
		for (LancamentoConta lancamento : faturaCartao.getDetalheFatura()) {
			LancamentoConta l = getLancamentoContaRepository().findById(lancamento.getId());
			if (l.getLancamentoPeriodico() != null) {
				// Delega a quitação do lançamento para a rotina de registro de pagamento de lançamentos periódicos
				getFechamentoPeriodoService().registrarPagamento(l);
			} else {
				l.setStatusLancamentoConta(StatusLancamentoConta.QUITADO);
				getLancamentoContaRepository().update(l);
			}
		}
		
		// Verifica se existe fatura futura para alterar o status
		if (getRepository().findNextFaturaCartaoFutura(entity.getConta()) == null) {
			// Cria a próxima fatura ABERTA
			FaturaCartao novaFatura = new FaturaCartao();
			novaFatura.setConta(entity.getConta());
			novaFatura.setStatusFaturaCartao(StatusFaturaCartao.ABERTA);
			// Data de vencimento da próxima fatura
			Calendar vencimento = Calendar.getInstance();
			vencimento.setTime(entity.getDataVencimento());		
			vencimento.add(Calendar.MONTH, 1);		
			novaFatura.setDataVencimento(vencimento.getTime());
			
			// Seta o mês e ano da fatura
			novaFatura.setMes(novaFatura.getDataVencimento().getMonth() + 1);
			novaFatura.setAno(novaFatura.getDataVencimento().getYear() + 1900);
					
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
		//fatura.setDataFechamento(null);
		fatura.setStatusFaturaCartao(StatusFaturaCartao.ABERTA);

		// Salva a fatura do cartão
		getRepository().update(fatura);

				
		// Reabre todos os lançamentos vinculados à fatura
		for (LancamentoConta lancamento : fatura.getDetalheFatura()) {
			LancamentoConta l = getLancamentoContaRepository().findById(lancamento.getId());
			l.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
			getLancamentoContaRepository().update(l);
		}
	}
	
	@Override
	public void quitarFaturaDebitoConta(FaturaCartao faturaCartao, Conta contaCorrente,	double valorAQuitar, Date dataPagamento) throws BusinessException {		
		if (Math.abs(Util.arredondar(valorAQuitar)) < Math.abs(Util.arredondar(faturaCartao.getValorMinimo()))) {
			throw new BusinessException("Valor de pagamento não pode ser menor que o valor mínimo da fatura!");
		}
		
		if (Math.abs(Util.arredondar(valorAQuitar)) > Math.abs(Util.arredondar(faturaCartao.getValorFatura() + faturaCartao.getSaldoDevedor()))) {
			throw new BusinessException("Não é possível quitar um valor maior que o valor total da fatura!");
		}
		
		if (dataPagamento.before(contaCorrente.getDataAbertura())) {
			throw new BusinessException("Data de pagamento deve ser posterior a data de abertura da conta selecionada!");
		}
			
		// Traz a fatura e seta seus atributos
		FaturaCartao fatura = getRepository().findById(faturaCartao.getId());
		fatura.setStatusFaturaCartao(StatusFaturaCartao.QUITADA);
		fatura.setDataPagamento(dataPagamento);
		fatura.setValorPago(valorAQuitar);
		fatura.setValorMinimo(faturaCartao.getValorMinimo());
		
		// Cria o lançamento que irá quitar a fatura
		LancamentoConta lancamentoPagamento = new LancamentoConta();
		lancamentoPagamento.setConta(contaCorrente);
		lancamentoPagamento.setMoeda(contaCorrente.getMoeda());
		lancamentoPagamento.setDataPagamento(dataPagamento);
		lancamentoPagamento.setDescricao("Pagamento Fatura " + fatura.getLabel() + ", " + faturaCartao.getConta().getCartaoCredito().getDescricao());
		lancamentoPagamento.setStatusLancamentoConta(StatusLancamentoConta.QUITADO);
		lancamentoPagamento.setValorPago(valorAQuitar);
		lancamentoPagamento.setTipoLancamento(TipoLancamento.DESPESA);
		lancamentoPagamento.setCategoria(faturaCartao.getCategoriaSelecionada());
		lancamentoPagamento.setFavorecido(faturaCartao.getFavorecidoSelecionado());
		lancamentoPagamento.setMeioPagamento(faturaCartao.getMeioPagamentoSelecionado());
		
		// Determina o valor do saldo devedor
		double saldoDevedor = (faturaCartao.getValorFatura() + faturaCartao.getSaldoDevedor()) - valorAQuitar;
		
		// Salva o lançamento
		getLancamentoContaRepository().save(lancamentoPagamento);
		
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
	public void quitarFaturaParcelamento(FaturaCartao faturaCartao, int quantParcelas, Date dataParcelamento) throws BusinessException {
		
		/* Efetua a quitação da fatura */
		FaturaCartao fatura = getRepository().findById(faturaCartao.getId());
		fatura.setStatusFaturaCartao(StatusFaturaCartao.QUITADA);
		fatura.setDataPagamento(dataParcelamento);
		fatura.setValorPago(faturaCartao.getValorFatura() + faturaCartao.getSaldoDevedor());
		fatura.setValorMinimo(faturaCartao.getValorMinimo());
				
		// Salva a fatura paga
		getRepository().update(fatura);
		
		/* Efetua o parcelamento da fatura */
		
		// Traz as informações da fatura atualmente aberta
		FaturaCartao faturaAberta = getRepository().findFaturaCartaoAberta(faturaCartao.getConta());
		
		// Determina a data de fechamento da fatura atual
		Calendar temp = Calendar.getInstance();
		temp.setTime(faturaAberta.getDataVencimento());
		if (faturaAberta.getConta().getCartaoCredito().getDiaFechamentoFatura() < faturaAberta.getConta().getCartaoCredito().getDiaVencimentoFatura()) {
			temp.set(Calendar.DAY_OF_MONTH, faturaAberta.getConta().getCartaoCredito().getDiaFechamentoFatura());
		} else {
			temp.add(Calendar.MONTH, 1);
			temp.set(Calendar.DAY_OF_MONTH, faturaAberta.getConta().getCartaoCredito().getDiaFechamentoFatura());
		}
		
		LancamentoPeriodico parcelamentoFatura = new LancamentoPeriodico();
		parcelamentoFatura.setConta(faturaCartao.getConta());
		parcelamentoFatura.setDataAquisicao(dataParcelamento);		
		parcelamentoFatura.setDataPrimeiraParcela(temp.getTime());
		parcelamentoFatura.setDescricao("Parcelamento Fatura " + faturaCartao.getMes() + "/" + faturaCartao.getAno());
		parcelamentoFatura.setDiaVencimento(faturaCartao.getConta().getCartaoCredito().getDiaFechamentoFatura());
		parcelamentoFatura.setMoeda(faturaCartao.getConta().getMoeda());
		parcelamentoFatura.setPeriodoLancamento(PeriodoLancamento.MENSAL);
		parcelamentoFatura.setStatusLancamento(StatusLancamento.ATIVO);
		parcelamentoFatura.setTipoLancamento(TipoLancamento.DESPESA);
		parcelamentoFatura.setTipoLancamentoPeriodico(TipoLancamentoPeriodico.PARCELADO);
		parcelamentoFatura.setTotalParcela(quantParcelas);
		parcelamentoFatura.setUsuario(faturaCartao.getConta().getUsuario());
		parcelamentoFatura.setValorCompra(fatura.getValorPago());
		parcelamentoFatura.setValorParcela(Util.arredondar(fatura.getValorPago() / quantParcelas));
		parcelamentoFatura.setCategoria(faturaCartao.getCategoriaSelecionada());
		parcelamentoFatura.setFavorecido(faturaCartao.getFavorecidoSelecionado());
		parcelamentoFatura.setMeioPagamento(faturaCartao.getMeioPagamentoSelecionado());
		getLancamentoPeriodicoService().cadastrar(parcelamentoFatura);		
	}
	
	@Override
	public void quitarFaturaLancamentoSelecionado(FaturaCartao faturaCartao, LancamentoConta lancamentoConta) throws BusinessException {
		// Verifica se o lançamento selecionado já foi vinculado com outra fatura
		if (getLancamentoContaRepository().existsLinkagePagamentoFaturaCartao(lancamentoConta)) {
			throw new BusinessException("Lançamento selecionado já foi usado para quitar outra fatura!");
		}
		
		// Verifica se o valor do lançamento selecionado é menor que o valor mínimo da fatura
		if (Math.abs(Util.arredondar(lancamentoConta.getValorPago())) < Math.abs(Util.arredondar(faturaCartao.getValorMinimo()))) {
			throw new BusinessException("Valor de pagamento não pode ser menor que o valor mínimo da fatura!");
		}
		
		// Verifica se o valor do lançamento selecionado é maior que o valor total da fatura
		if (Math.abs(Util.arredondar(lancamentoConta.getValorPago())) > Math.abs(Util.arredondar(faturaCartao.getValorFatura() + faturaCartao.getSaldoDevedor()))) {
			throw new BusinessException("Não é possível quitar um valor maior que o valor total da fatura!");
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
	
	@Override
	public List<FaturaCartao> buscarPorCartaoCreditoEStatusFatura(CartaoCredito cartao, StatusFaturaCartao statusFatura) throws BusinessException {
		return getRepository().findByContaAndStatusFatura(cartao.getConta(), statusFatura);
	}
	
	@Override
	public List<FaturaCartao> buscarTodosPorContaOrdenadoPorMesEAno(Conta conta) throws BusinessException {
		return getRepository().findAllByContaOrderedByMesAndAno(conta);
	}
}