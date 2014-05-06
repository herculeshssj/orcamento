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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.PagamentoPeriodo;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ILancamentoPeriodico;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoPeriodicoRepository;
import br.com.hslife.orcamento.repository.PagamentoPeriodoRepository;

@Service("lancamentoPeriodicoService")
public class LancamentoPeriodicoService extends AbstractCRUDService<LancamentoPeriodico> implements ILancamentoPeriodico {

	@Autowired
	private LancamentoPeriodicoRepository repository;
	
	@Autowired
	private PagamentoPeriodoRepository pagamentoPeriodoRepository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;

	public LancamentoPeriodicoRepository getRepository() {
		return repository;
	}

	public void setRepository(LancamentoPeriodicoRepository repository) {
		this.repository = repository;
	}

	public void setPagamentoPeriodoRepository(
			PagamentoPeriodoRepository pagamentoPeriodoRepository) {
		this.pagamentoPeriodoRepository = pagamentoPeriodoRepository;
	}
	
	@Override
	public void cadastrar(LancamentoPeriodico entity) throws BusinessException {
		super.cadastrar(entity);
		if (entity.getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.FIXO)) {
			gerarMensalidade(entity);
		} else {
			gerarParcelas(entity);
		}
	}
	
	@Override
	public void excluir(LancamentoPeriodico entity) throws BusinessException {
		List<PagamentoPeriodo> pagamentos = pagamentoPeriodoRepository.findPagosByLancamentoPeriodico(entity);
		if (pagamentos != null) {
			if (pagamentos.size() != 0) {
				throw new BusinessException("Não é possível excluir! Existem pagamentos registrados!");
			}
		}
		// Exclui os pagamentos e depois exclui o lançamento
		for (PagamentoPeriodo pagamento : pagamentoPeriodoRepository.findByLancamentoPeriodico(entity)) {
			pagamentoPeriodoRepository.delete(pagamento);
		}
		super.excluir(entity);
	}

	@Override
	public List<LancamentoPeriodico> buscarPorTipoLancamentoContaEStatusLancamento(TipoLancamentoPeriodico tipo, Conta conta, StatusLancamento statusLancamento) throws BusinessException {
		return getRepository().findByTipoLancamentoContaAndStatusLancamento(tipo, conta, statusLancamento);
	}
	
	@Override
	public List<LancamentoPeriodico> buscarPorTipoLancamentoEStatusLancamentoPorUsuario(TipoLancamentoPeriodico tipo, StatusLancamento status, Usuario usuario) throws BusinessException {
		return getRepository().findByTipoLancamentoAndStatusLancamentoByUsuario(tipo, status, usuario);
	}
	
	@Override
	public List<PagamentoPeriodo> buscarPagamentosNaoPagosPorLancamentoPeriodico(LancamentoPeriodico entity) throws BusinessException {
		return pagamentoPeriodoRepository.findNotPagosByLancamentoPeriodico(entity);
	}
	
	@Override
	public List<PagamentoPeriodo> buscarPagamentosPagosPorLancamentoPeriodico(LancamentoPeriodico entity) throws BusinessException {
		return pagamentoPeriodoRepository.findPagosByLancamentoPeriodico(entity);
	}
	
	@Override
	public List<PagamentoPeriodo> buscarPagamentosPorLancamentoPeriodicoEPago(LancamentoPeriodico lancamento, Boolean pago) throws BusinessException {
		return pagamentoPeriodoRepository.findPagamentosByLancamentoPeriodicoAndPago(lancamento, pago);
	}
	
	@Override
	public List<PagamentoPeriodo> buscarTodosPagamentosPagosLancamentosAtivosPorTipoLancamentoEUsuario(TipoLancamentoPeriodico tipo, Usuario usuario) throws BusinessException {
		return pagamentoPeriodoRepository.findAllPagamentosPagosActivedLancamentosByTipoLancamentoAndUsuario(tipo, usuario);
	}
	
	@Override
	public List<PagamentoPeriodo> buscarPagamentosPorTipoLancamentoEUsuarioEPago(TipoLancamentoPeriodico tipo, Usuario usuario, Boolean pago) throws BusinessException {
		return pagamentoPeriodoRepository.findPagamentosByTipoLancamentoAndUsuarioAndPago(tipo, usuario, pago);
	}
	
	@Override
	public List<PagamentoPeriodo> buscarPagamentosPorTipoLancamentoEContaEPago(TipoLancamentoPeriodico tipo, Conta conta,Boolean pago) throws BusinessException {
		return pagamentoPeriodoRepository.findPagamentosByTipoLancamentoAndContaAndPago(tipo, conta, pago);
	}
	
	@Override
	public List<PagamentoPeriodo> buscarPagamentosPorTipoLancamentoETipoContaEPago(TipoLancamentoPeriodico tipo, TipoConta tipoConta, Boolean pago) throws BusinessException {
		return pagamentoPeriodoRepository.findPagamentosByTipoLancamentoAndTipoContaAndPago(tipo, tipoConta, pago);
	}
	
	@Override
	public List<LancamentoPeriodico> buscarPorTipoLancamentoETipoContaEStatusLancamento(TipoLancamentoPeriodico tipo, TipoConta tipoConta, StatusLancamento statusLancamento) throws BusinessException {
		return getRepository().findByTipoLancamentoAndTipoContaAndStatusLancamento(tipo, tipoConta, statusLancamento);
	}
	
	@Override
	public void alterarStatusLancamento(LancamentoPeriodico entity, StatusLancamento novoStatus) throws BusinessException {
		entity.setStatusLancamento(novoStatus);
		getRepository().update(entity);
	}
	
	@Override
	public void registrarPagamento(PagamentoPeriodo pagamentoPeriodo) throws BusinessException {		
		pagamentoPeriodo.setPago(true);
		
		// Verifica se é para gerar o lançamento correspondente ao pagamento. Se for, gera o lançamento
		if (pagamentoPeriodo.isGerarLancamento()) {
			LancamentoConta lancamento = new LancamentoConta();
			lancamento.setAgendado(false);
			lancamento.setCategoria(pagamentoPeriodo.getLancamentoPeriodico().getCategoria());
			lancamento.setConta(pagamentoPeriodo.getLancamentoPeriodico().getConta());
			lancamento.setDataLancamento(pagamentoPeriodo.getDataPagamento());
			lancamento.setDataPagamento(pagamentoPeriodo.getDataPagamento());
			lancamento.setDescricao(pagamentoPeriodo.getLancamentoPeriodico().getDescricao() + " - " +  pagamentoPeriodo.getLabel());
			lancamento.setFavorecido(pagamentoPeriodo.getLancamentoPeriodico().getFavorecido());
			lancamento.setMeioPagamento(pagamentoPeriodo.getLancamentoPeriodico().getMeioPagamento());
			lancamento.setMoeda(pagamentoPeriodo.getLancamentoPeriodico().getMoeda());
			lancamento.setTipoLancamento(pagamentoPeriodo.getLancamentoPeriodico().getTipoLancamento());
			lancamento.setValorPago(pagamentoPeriodo.getValorPago());
			
			lancamentoContaRepository.save(lancamento);
			pagamentoPeriodo.setLancamentoConta(lancamento);
		}
		
		// Atualiza o pagamento
		pagamentoPeriodoRepository.update(pagamentoPeriodo);
		
		// Gera o próximo pagamento para os lançamentos fixos
		if (pagamentoPeriodo.getLancamentoPeriodico().getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.FIXO)) {
			
			// Busca o pagamento mais recente
			PagamentoPeriodo ultimaMensalidade = pagamentoPeriodoRepository.findLastGeneratedPagamentoPeriodo(pagamentoPeriodo.getLancamentoPeriodico());
			
			Calendar dataVencimento = Calendar.getInstance();
			dataVencimento.setTime(ultimaMensalidade.getDataVencimento());
			
			if (dataVencimento.get(Calendar.DAY_OF_MONTH) >= pagamentoPeriodo.getLancamentoPeriodico().getDiaVencimento()) {
				switch (pagamentoPeriodo.getLancamentoPeriodico().getPeriodoLancamento()) {
					case MENSAL : dataVencimento.add(Calendar.MONTH, 1); break;
					case BIMESTRAL : dataVencimento.add(Calendar.MONTH, 2); break;
					case TRIMESTRAL : dataVencimento.add(Calendar.MONTH, 3); break;
					case QUADRIMESTRAL : dataVencimento.add(Calendar.MONTH, 4); break;
					case SEMESTRAL : dataVencimento.add(Calendar.MONTH, 6); break;
					case ANUAL : dataVencimento.add(Calendar.YEAR, 1); break;
					default : throw new BusinessException("Período informado é inválido!");
				}
			}
			
			PagamentoPeriodo proximaMensalidade = new PagamentoPeriodo();
			proximaMensalidade.setLancamentoPeriodico(pagamentoPeriodo.getLancamentoPeriodico());
			dataVencimento.set(Calendar.DAY_OF_MONTH, pagamentoPeriodo.getLancamentoPeriodico().getDiaVencimento());
			
			proximaMensalidade.setAno(dataVencimento.get(Calendar.YEAR));
			proximaMensalidade.setPeriodo(dataVencimento.get(Calendar.MONTH) + 1);
			proximaMensalidade.setDataVencimento(dataVencimento.getTime());
			
			pagamentoPeriodoRepository.save(proximaMensalidade);
			
		}
	}
	
	@Override
	public List<PagamentoPeriodo> gerarPrevisaoProximosPagamentos(LancamentoPeriodico lancamentoPeriodico, int quantidadePeriodos) throws BusinessException {
		List<PagamentoPeriodo> pagamentosGerados = new ArrayList<>();
		// Gera o próximo pagamento para os lançamentos fixos
		if (lancamentoPeriodico.getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.FIXO)) {
			
			// Busca o pagamento mais recente
			PagamentoPeriodo ultimaMensalidade = pagamentoPeriodoRepository.findLastGeneratedPagamentoPeriodo(lancamentoPeriodico);
			
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
				
				PagamentoPeriodo proximaMensalidade = new PagamentoPeriodo();
				proximaMensalidade.setLancamentoPeriodico(lancamentoPeriodico);
				dataVencimento.set(Calendar.DAY_OF_MONTH, lancamentoPeriodico.getDiaVencimento());
				
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
	
	private void gerarMensalidade(LancamentoPeriodico entity) throws BusinessException {
		PagamentoPeriodo proximaMensalidade = new PagamentoPeriodo();
		proximaMensalidade.setLancamentoPeriodico(entity);
		
		Calendar dataVencimento = Calendar.getInstance();
		
		if (dataVencimento.get(Calendar.DAY_OF_MONTH) >= entity.getDiaVencimento()) {
			switch (entity.getPeriodoLancamento()) {
				case MENSAL : dataVencimento.add(Calendar.MONTH, 1); break;
				case BIMESTRAL : dataVencimento.add(Calendar.MONTH, 2); break;
				case TRIMESTRAL : dataVencimento.add(Calendar.MONTH, 3); break;
				case QUADRIMESTRAL : dataVencimento.add(Calendar.MONTH, 4); break;
				case SEMESTRAL : dataVencimento.add(Calendar.MONTH, 6); break;
				case ANUAL : dataVencimento.add(Calendar.YEAR, 1); break;
				default : throw new BusinessException("Período informado é inválido!");
			}
		}
		
		dataVencimento.set(Calendar.DAY_OF_MONTH, entity.getDiaVencimento());
		
		proximaMensalidade.setAno(dataVencimento.get(Calendar.YEAR));
		proximaMensalidade.setPeriodo(dataVencimento.get(Calendar.MONTH));
		proximaMensalidade.setDataVencimento(dataVencimento.getTime());
		
		pagamentoPeriodoRepository.save(proximaMensalidade);		
		
	}
	
	private void gerarParcelas(LancamentoPeriodico entity) throws BusinessException {
		PagamentoPeriodo parcela;
		Calendar dataVencimento = Calendar.getInstance();
		dataVencimento.setTime(entity.getDataPrimeiraParcela());
				
		for (int i = 1; i <= entity.getTotalParcela(); i++) {
			parcela = new PagamentoPeriodo();			
			parcela.setAno(dataVencimento.get(Calendar.YEAR));
			parcela.setLancamentoPeriodico(entity);
			parcela.setPeriodo(dataVencimento.get(Calendar.MONTH) + 1);
			parcela.setDataVencimento(dataVencimento.getTime());
			parcela.setParcela(i);
			
			pagamentoPeriodoRepository.save(parcela);
			dataVencimento.add(Calendar.MONTH, 1);
		}
	}
}