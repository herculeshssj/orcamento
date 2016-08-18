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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;
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

	public LancamentoContaRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}
	
	public LancamentoImportadoRepository getLancamentoImportadoRepository() {
		this.lancamentoImportadoRepository.setSessionFactory(this.sessionFactory);
		return lancamentoImportadoRepository;
	}
	
	public IMoeda getMoedaService() {
		return moedaService;
	}

	@Override
	public void cadastrar(LancamentoConta entity) throws BusinessException {
		// Salva a informação da moeda no lançamento da conta
		if (!entity.getConta().getTipoConta().equals(TipoConta.CARTAO))
			entity.setMoeda(entity.getConta().getMoeda());
		
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
	public void alterar(LancamentoConta entity) throws BusinessException {
		// Salva a informação da moeda no lançamento da conta
		if (!entity.getConta().getTipoConta().equals(TipoConta.CARTAO))
			entity.setMoeda(entity.getConta().getMoeda());
		
		if (entity.getLancamentoImportado() != null) {
			// Seta a moeda a partir do código monetário. Caso não encontre, seta a moeda padrão.
			entity.setMoeda(getMoedaService().buscarCodigoMonetarioPorUsuario(entity.getLancamentoImportado().getMoeda(), entity.getConta().getUsuario()) == null 
					? getMoedaService().buscarPadraoPorUsuario(entity.getConta().getUsuario()) 
					: getMoedaService().buscarCodigoMonetarioPorUsuario(entity.getLancamentoImportado().getMoeda(), entity.getConta().getUsuario()));
			
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

	@Override
	public void excluir(LancamentoConta entity) throws BusinessException {
		if (entity.getLancamentoPeriodico() != null && entity.getLancamentoPeriodico().getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.PARCELADO)) {
			throw new BusinessException("Este lançamento não pode ser excluído pois representa uma parcela!");
		}
		super.excluir(entity);
	}
	
	@Override
	public List<LancamentoConta> buscarPorCriterioBusca(CriterioBuscaLancamentoConta criterioBusca)	throws BusinessException {
		return getRepository().findByCriterioBusca(criterioBusca);
	}

	@Override
	public void validar(LancamentoConta entity) throws BusinessException {
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
	public boolean existeVinculoFaturaCartao(LancamentoConta lancamento) throws BusinessException {
		return getRepository().existsLinkageFaturaCartao(lancamento);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosNaoPagosPorLancamentoPeriodico(LancamentoPeriodico entity) throws BusinessException {
		return getRepository().findNotPagosByLancamentoPeriodico(entity);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosPagosPorLancamentoPeriodico(LancamentoPeriodico entity) throws BusinessException {
		return getRepository().findPagosByLancamentoPeriodico(entity);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosPorLancamentoPeriodicoEPago(LancamentoPeriodico lancamento, StatusLancamentoConta pago) throws BusinessException {
		return getRepository().findPagamentosByLancamentoPeriodicoAndPago(lancamento, pago);
	}
	
	@Override
	public List<LancamentoConta> buscarTodosPagamentosPagosLancamentosAtivosPorTipoLancamentoEUsuario(TipoLancamentoPeriodico tipo, Usuario usuario) throws BusinessException {
		return getRepository().findAllPagamentosPagosActivedLancamentosByTipoLancamentoAndUsuario(tipo, usuario);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosPorTipoLancamentoEUsuarioEPago(TipoLancamentoPeriodico tipo, Usuario usuario, StatusLancamentoConta pago) throws BusinessException {
		return getRepository().findPagamentosByTipoLancamentoAndUsuarioAndPago(tipo, usuario, pago);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosPorTipoLancamentoEContaEPago(TipoLancamentoPeriodico tipo, Conta conta, StatusLancamentoConta pago) throws BusinessException {
		return getRepository().findPagamentosByTipoLancamentoAndContaAndPago(tipo, conta, pago);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosPorTipoLancamentoETipoContaEPago(TipoLancamentoPeriodico tipo, TipoConta tipoConta, StatusLancamentoConta pago) throws BusinessException {
		return getRepository().findPagamentosByTipoLancamentoAndTipoContaAndPago(tipo, tipoConta, pago);
	}
	
	@Override
	public List<LancamentoConta> gerarPrevisaoProximosPagamentos(LancamentoPeriodico lancamentoPeriodico, int quantidadePeriodos) throws BusinessException {
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
				
				LancamentoConta proximaMensalidade = new LancamentoConta();
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
	
	public LancamentoConta buscarUltimoPagamentoPeriodoGerado(LancamentoPeriodico lancamentoPeriodico) throws BusinessException {
		return getRepository().findLastGeneratedPagamentoPeriodo(lancamentoPeriodico);
	}
	
	public List<LancamentoConta> buscarPorLancamentoPeriodico(LancamentoPeriodico lancamentoPeriodico) throws BusinessException {
		return getRepository().findByLancamentoPeriodico(lancamentoPeriodico);
	}
}