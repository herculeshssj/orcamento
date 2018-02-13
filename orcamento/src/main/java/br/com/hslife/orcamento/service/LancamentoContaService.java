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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.component.OpcaoSistemaComponent;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FaturaCartao;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.TaxaConversao;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.CadastroSistema;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.model.LancamentoPanoramaCadastro;
import br.com.hslife.orcamento.repository.FaturaCartaoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoImportadoRepository;
import br.com.hslife.orcamento.util.LancamentoContaUtil;

@Service("lancamentoContaService")
public class LancamentoContaService extends AbstractCRUDService<LancamentoConta> implements ILancamentoConta {

	@Autowired
	private LancamentoContaRepository repository;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private LancamentoImportadoRepository lancamentoImportadoRepository;
	
	@Autowired
	private FaturaCartaoRepository faturaCartaoRepository;
	
	@Autowired
	private OpcaoSistemaComponent opcaoSistemaComponent;

	public OpcaoSistemaComponent getOpcaoSistemaComponent() {
		return opcaoSistemaComponent;
	}

	public LancamentoContaRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}
	
	public LancamentoImportadoRepository getLancamentoImportadoRepository() {
		this.lancamentoImportadoRepository.setSessionFactory(this.sessionFactory);
		return lancamentoImportadoRepository;
	}
	
	public FaturaCartaoRepository getFaturaCartaoRepository() {
		this.faturaCartaoRepository.setSessionFactory(this.sessionFactory);
		return faturaCartaoRepository;
	}

	public IMoeda getMoedaService() {
		return moedaService;
	}

	@Override
	public void cadastrar(LancamentoConta entity) {
		// Salva a informação da moeda da conta no lançamento
		if (!entity.getConta().getTipoConta().equals(TipoConta.CARTAO)) {
			entity.setMoeda(entity.getConta().getMoeda());
		} else {
			entity.setTaxaConversao(new TaxaConversao(entity.getMoeda(), entity.getValorPago(), entity.getConta().getMoeda(), entity.getMoeda().getValorConversao()));
		}
		
		// Define o status do lançamento da conta
		if (!entity.getStatusLancamentoConta().equals(StatusLancamentoConta.QUITADO)) {
			if (entity.getDataPagamento().before(new Date())) {
				entity.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
			} else {
				entity.setStatusLancamentoConta(StatusLancamentoConta.AGENDADO);
			}
		}
		super.cadastrar(entity);
	}

	@Override
	public void alterar(LancamentoConta entity) {
		// Trata o lançamento importado vinculado ao lançamento da conta
		if (entity.getLancamentoImportado() != null) {
			// Seta a moeda a partir do código monetário. Caso não encontre, seta a moeda padrão.
			Moeda moedaLancamento = getMoedaService().buscarCodigoMonetarioPorUsuario(entity.getLancamentoImportado().getMoeda(), entity.getConta().getUsuario());
			
			entity.setMoeda(moedaLancamento == null ? getOpcaoSistemaComponent().getMoedaPadrao() : moedaLancamento);
			
			// Caso o lançamento seja uma parcela, a data de pagamento não será atualizada
			if (entity.getLancamentoPeriodico() != null && entity.getLancamentoPeriodico().getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.PARCELADO)) {
				// Não atualiza a data de pagamento
			} else {
				entity.setDataPagamento(entity.getLancamentoImportado().getData());
			}
			
			entity.setHistorico(entity.getLancamentoImportado().getHistorico());
			entity.setNumeroDocumento(entity.getLancamentoImportado().getDocumento());
			entity.setValorPago(Math.abs(entity.getLancamentoImportado().getValor()));
			entity.setHashImportacao(entity.getLancamentoImportado().getHash());
			
			if (entity.getLancamentoImportado().getValor() > 0) {
				entity.setTipoLancamento(TipoLancamento.RECEITA);
			} else {
				entity.setTipoLancamento(TipoLancamento.DESPESA);
			}
						
			getLancamentoImportadoRepository().delete(entity.getLancamentoImportado());
		}
		
		// Salva a informação da moeda no lançamento da conta
		if (!entity.getConta().getTipoConta().equals(TipoConta.CARTAO)) {
			entity.setMoeda(entity.getConta().getMoeda());
		} else {
			if (entity.getTaxaConversao() == null) {
				entity.setTaxaConversao(new TaxaConversao(entity.getMoeda(), entity.getValorPago(), entity.getConta().getMoeda(), entity.getMoeda().getValorConversao()));
			} else {
				entity.getTaxaConversao().atualizaTaxaConversao(entity.getMoeda(), entity.getValorPago(), entity.getConta().getMoeda(), entity.getMoeda().getValorConversao());
			}
		}
		
		this.alteraDataPagamentoFaturaCartao(entity);
		
		// Define o status do lançamento da conta
		if (!entity.getStatusLancamentoConta().equals(StatusLancamentoConta.QUITADO)) {
			if (entity.getDataPagamento().before(new Date())) {
				entity.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
			} else {
				entity.setStatusLancamentoConta(StatusLancamentoConta.AGENDADO);
			}
		}
		
		super.alterar(entity);
	}

	/*
	 * Confere se o lançamento representa o pagamento de uma fatura, e se sofreu mudança na sua data de pagamento
	 * Caso tenha sofrido, atualiza a data de pagamento da fatura do cartão.
	 */
	private void alteraDataPagamentoFaturaCartao(LancamentoConta entity) {
		// Traz uma instância do lançamento da base
		LancamentoConta lancamento = getRepository().findById(entity.getId());
		
		// Compara para ver se a data de pagamento não foi alterada
		if (!lancamento.getDataPagamento().equals(entity.getDataPagamento())) {
			// Busca a fatura que o lançamento está vinculado
			FaturaCartao fatura = getFaturaCartaoRepository().findFaturaPagaByLancamentoConta(lancamento);
			
			if (fatura != null) {
				// Altera a data de pagamento da fatura
				fatura.setDataPagamento(entity.getDataPagamento());
				
				// Salva a fatura
				getFaturaCartaoRepository().update(fatura);
			}
		}
	}

	@Override
	public void excluir(LancamentoConta entity) {
		if (entity.getLancamentoPeriodico() != null && entity.getLancamentoPeriodico().getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.PARCELADO)) {
			throw new BusinessException("Este lançamento não pode ser excluído pois representa uma parcela!");
		}
		super.excluir(entity);
	}
	
	@Override
	public List<LancamentoConta> buscarPorCriterioBusca(CriterioBuscaLancamentoConta criterioBusca) {
		return getRepository().findByCriterioBusca(criterioBusca);
	}

	@Override
	public void validar(LancamentoConta entity) {
		if (entity.getDataPagamento() == null)
			throw new BusinessException("Informe a data de pagamento!");
		
		if (!entity.getConta().getTipoConta().equals(TipoConta.CARTAO)) {
			if (entity.getDataPagamento().before(entity.getConta().getDataAbertura())) {
				throw new BusinessException("Data de lançamento deve ser posterior a data de abertura da conta!");
			}
			if (entity.getConta().getDataFechamento() != null && entity.getDataPagamento().after(entity.getConta().getDataFechamento())) {
				throw new BusinessException("Data de lançamento deve ser anterior a data de fechamento da conta!");
			}
		}
		
		if (entity.getLancamentoPeriodico() != null) {
			LancamentoConta lancamento = null;
			if (entity.getLancamentoPeriodico().getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.PARCELADO)) {
				lancamento = getRepository().findLancamentoByParcelaAndLancamentoPeriodico(entity.getParcela(), entity.getLancamentoPeriodico());
				if (lancamento != null && !lancamento.equals(entity)) {
					throw new BusinessException("Já existe lançamento com a parcela informada!");
				}
			}
		}
	}
	
	@Override
	public double calcularSaldoLancamentos(List<LancamentoConta> lancamentos) {
		return LancamentoContaUtil.calcularSaldoLancamentos(lancamentos);
	}
	
	@Override
	public boolean existeVinculoFaturaCartao(LancamentoConta lancamento) {
		return getRepository().existsLinkageFaturaCartao(lancamento);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosNaoPagosPorLancamentoPeriodico(LancamentoPeriodico entity) {
		return getRepository().findNotPagosByLancamentoPeriodico(entity);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosPagosPorLancamentoPeriodico(LancamentoPeriodico entity) {
		return getRepository().findPagosByLancamentoPeriodico(entity);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosPorLancamentoPeriodicoEPago(LancamentoPeriodico lancamento, StatusLancamentoConta pago) {
		return getRepository().findPagamentosByLancamentoPeriodicoAndPago(lancamento, pago);
	}
	
	@Override
	public List<LancamentoConta> buscarTodosPagamentosPagosLancamentosAtivosPorTipoLancamentoEUsuario(TipoLancamentoPeriodico tipo, Usuario usuario) {
		return getRepository().findAllPagamentosPagosActivedLancamentosByTipoLancamentoAndUsuario(tipo, usuario);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosPorTipoLancamentoEUsuarioEPago(TipoLancamentoPeriodico tipo, Usuario usuario, StatusLancamentoConta pago) {
		return getRepository().findPagamentosByTipoLancamentoAndUsuarioAndPago(tipo, usuario, pago);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosPorTipoLancamentoEContaEPago(TipoLancamentoPeriodico tipo, Conta conta, StatusLancamentoConta pago) {
		return getRepository().findPagamentosByTipoLancamentoAndContaAndPago(tipo, conta, pago);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosPorTipoLancamentoETipoContaEPago(TipoLancamentoPeriodico tipo, TipoConta tipoConta, StatusLancamentoConta pago) {
		return getRepository().findPagamentosByTipoLancamentoAndTipoContaAndPago(tipo, tipoConta, pago);
	}
	
	@Override
	public List<LancamentoConta> gerarPrevisaoProximosPagamentos(LancamentoPeriodico lancamentoPeriodico, int quantidadePeriodos) {
		List<LancamentoConta> pagamentosGerados = new ArrayList<>();
		// Gera o próximo pagamento para os lançamentos fixos
		if (lancamentoPeriodico.getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.FIXO)) {
			
			// Busca o pagamento mais recente
			LancamentoConta ultimaMensalidade = getRepository().findLastGeneratedPagamentoPeriodo(lancamentoPeriodico);
			
			Calendar dataVencimento = Calendar.getInstance();
			dataVencimento.setTime(ultimaMensalidade.getDataVencimento());
			
			for (int i = 1; i <= quantidadePeriodos; i++) {
			
				// Determina a data do próximo pagamento
				if (dataVencimento.get(Calendar.DAY_OF_MONTH) >= lancamentoPeriodico.getDiaVencimento()) {
					switch (lancamentoPeriodico.getPeriodoLancamento()) {
						case MENSAL : dataVencimento.add(Calendar.MONTH, 1); break;
						case BIMESTRAL : dataVencimento.add(Calendar.MONTH, 2); break;
						case TRIMESTRAL : dataVencimento.add(Calendar.MONTH, 3); break;
						case QUADRIMESTRAL : dataVencimento.add(Calendar.MONTH, 4); break;
						case SEMESTRAL : dataVencimento.add(Calendar.MONTH, 6); break;
						case ANUAL : dataVencimento.add(Calendar.YEAR, 1); break;
						default : throw new BusinessException("Período informado é inválido!");
					}
				}
				
				LancamentoConta proximaMensalidade = new LancamentoConta(ultimaMensalidade);
				proximaMensalidade.setLancamentoPeriodico(lancamentoPeriodico);
				dataVencimento.set(Calendar.DAY_OF_MONTH, lancamentoPeriodico.getDiaVencimento());
				proximaMensalidade.setConta(lancamentoPeriodico.getConta());
				proximaMensalidade.setAno(dataVencimento.get(Calendar.YEAR));
				proximaMensalidade.setPeriodo(dataVencimento.get(Calendar.MONTH) + 1);
				proximaMensalidade.setDataVencimento(dataVencimento.getTime());
				
				pagamentosGerados.add(proximaMensalidade);
			}
			
		} else {
			throw new BusinessException("Só é possível gerar previsão de pagamento de lançamentos fixos!");
		}
		return pagamentosGerados;
	}
	
	public LancamentoConta buscarUltimoPagamentoPeriodoGerado(LancamentoPeriodico lancamentoPeriodico) {
		return getRepository().findLastGeneratedPagamentoPeriodo(lancamentoPeriodico);
	}
	
	public List<LancamentoConta> buscarPorLancamentoPeriodico(LancamentoPeriodico lancamentoPeriodico) {
		return getRepository().findByLancamentoPeriodico(lancamentoPeriodico);
	}
	
	@Override
	public List<LancamentoPanoramaCadastro> buscarLancamentoParaPanoramaCadastro(Conta conta, CadastroSistema cadastro,	Long idAgrupamento) {
		return getRepository().findLancamentoForPanoramaCadastro(conta, cadastro, idAgrupamento);
	}
	
	@Override
	public BigDecimal buscarSaldoPeriodoByContaAndPeriodoAndStatusLancamento(Conta conta, Date dataInicio, Date dataFim, StatusLancamentoConta[] statusLancamento) {
		return getRepository().getSaldoPeriodoByContaAndPeriodoAndStatusLancamento(conta, dataInicio, dataFim, statusLancamento); 
	}
}
