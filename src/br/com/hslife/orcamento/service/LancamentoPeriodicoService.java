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

import br.com.hslife.orcamento.component.ContaComponent;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ILancamentoPeriodico;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoPeriodicoRepository;
import br.com.hslife.orcamento.util.Util;

@Service("lancamentoPeriodicoService")
public class LancamentoPeriodicoService extends AbstractCRUDService<LancamentoPeriodico> implements ILancamentoPeriodico {

	@Autowired
	private LancamentoPeriodicoRepository repository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private ContaComponent contaComponent;

	public LancamentoPeriodicoRepository getRepository() {
		return repository;
	}

	public void setRepository(LancamentoPeriodicoRepository repository) {
		this.repository = repository;
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
		List<LancamentoConta> pagamentos = lancamentoContaRepository.findPagosByLancamentoPeriodico(entity);
		if (pagamentos != null) {
			if (pagamentos.size() != 0) {
				throw new BusinessException("Não é possível excluir! Existem pagamentos registrados!");
			}
		}
		// Exclui os pagamentos e depois exclui o lançamento
		for (LancamentoConta pagamento : lancamentoContaRepository.findByLancamentoPeriodico(entity)) {
			lancamentoContaRepository.delete(pagamento);
		}
		super.excluir(entity);
	}
	
	@Override
	public void alterarStatusLancamento(LancamentoPeriodico entity, StatusLancamento novoStatus) throws BusinessException {
		entity.setStatusLancamento(novoStatus);
		getRepository().update(entity);
	}
	
	@Override
	public void registrarPagamento(LancamentoConta pagamentoPeriodo) throws BusinessException {		
		contaComponent.registrarPagamento(pagamentoPeriodo);
	}
	
	@Override
	public List<LancamentoConta> gerarPrevisaoProximosPagamentos(LancamentoPeriodico lancamentoPeriodico, int quantidadePeriodos) throws BusinessException {
		List<LancamentoConta> pagamentosGerados = new ArrayList<>();
		// Gera o próximo pagamento para os lançamentos fixos
		if (lancamentoPeriodico.getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.FIXO)) {
			
			// Busca o pagamento mais recente
			LancamentoConta ultimaMensalidade = lancamentoContaRepository.findLastGeneratedPagamentoPeriodo(lancamentoPeriodico);
			
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
	
	private void gerarMensalidade(LancamentoPeriodico entity) throws BusinessException {
		LancamentoConta proximaMensalidade = new LancamentoConta();
		LancamentoPeriodico lancamentoPeriodico = getRepository().findById(entity.getId());
		proximaMensalidade.setLancamentoPeriodico(lancamentoPeriodico);
		
		Calendar dataVencimento = Calendar.getInstance();
		
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
		
		dataVencimento.set(Calendar.DAY_OF_MONTH, lancamentoPeriodico.getDiaVencimento());
		
		proximaMensalidade.setAno(dataVencimento.get(Calendar.YEAR));
		proximaMensalidade.setPeriodo(dataVencimento.get(Calendar.MONTH) + 1);
		proximaMensalidade.setDataVencimento(dataVencimento.getTime());
		
		// Setando os demais atributos
		proximaMensalidade.setConta(lancamentoPeriodico.getConta());
		proximaMensalidade.setTipoLancamento(lancamentoPeriodico.getTipoLancamento());
		proximaMensalidade.setValorPago(lancamentoPeriodico.getValorParcela());
		proximaMensalidade.setDataPagamento(proximaMensalidade.getDataVencimento());
		proximaMensalidade.setCategoria(lancamentoPeriodico.getCategoria());
		proximaMensalidade.setFavorecido(lancamentoPeriodico.getFavorecido());
		proximaMensalidade.setMeioPagamento(lancamentoPeriodico.getMeioPagamento());
		proximaMensalidade.setMoeda(lancamentoPeriodico.getMoeda());
		proximaMensalidade.setAgendado(true);
		
		// Define a descrição definitiva do lançamento a ser criado
		proximaMensalidade.setDescricao(lancamentoPeriodico.getDescricao() + " - Período " + proximaMensalidade.getPeriodo() + " / " + proximaMensalidade.getAno() + ", vencimento para " + Util.formataDataHora(proximaMensalidade.getDataVencimento(), Util.DATA));
		
		lancamentoContaRepository.save(proximaMensalidade);		
		
	}
	
	private void gerarParcelas(LancamentoPeriodico entity) throws BusinessException {
		LancamentoConta parcela;
		Calendar dataVencimento = Calendar.getInstance();
		dataVencimento.setTime(entity.getDataPrimeiraParcela());
				
		for (int i = 1; i <= entity.getTotalParcela(); i++) {
			parcela = new LancamentoConta();			
			parcela.setAno(dataVencimento.get(Calendar.YEAR));
			parcela.setLancamentoPeriodico(entity);
			parcela.setPeriodo(dataVencimento.get(Calendar.MONTH) + 1);
			parcela.setDataVencimento(dataVencimento.getTime());
			parcela.setParcela(i);
			
			// Setando os demais atributos
			parcela.setConta(entity.getConta());
			parcela.setDescricao(entity.getDescricao());
			parcela.setValorPago(entity.getValorParcela());
			parcela.setDataPagamento(parcela.getDataVencimento());
			parcela.setCategoria(entity.getCategoria());
			parcela.setFavorecido(entity.getFavorecido());
			parcela.setMeioPagamento(entity.getMeioPagamento());
			parcela.setMoeda(entity.getMoeda());
			parcela.setAgendado(true);
			
			// Define a descrição definitiva do lançamento a ser criado
			parcela.setDescricao(entity.getDescricao() + " - Parcela " + parcela.getParcela() + " / " + entity.getTotalParcela() + ", vencimento para " + Util.formataDataHora(parcela.getDataVencimento(), Util.DATA)); 
			
			lancamentoContaRepository.save(parcela);
			dataVencimento.add(Calendar.MONTH, 1);
		}
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
	public List<LancamentoConta> buscarPagamentosNaoPagosPorLancamentoPeriodico(LancamentoPeriodico entity) throws BusinessException {
		return lancamentoContaRepository.findNotPagosByLancamentoPeriodico(entity);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosPagosPorLancamentoPeriodico(LancamentoPeriodico entity) throws BusinessException {
		return lancamentoContaRepository.findPagosByLancamentoPeriodico(entity);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosPorLancamentoPeriodicoEPago(LancamentoPeriodico lancamento, Boolean pago) throws BusinessException {
		return lancamentoContaRepository.findPagamentosByLancamentoPeriodicoAndPago(lancamento, pago);
	}
	
	@Override
	public List<LancamentoConta> buscarTodosPagamentosPagosLancamentosAtivosPorTipoLancamentoEUsuario(TipoLancamentoPeriodico tipo, Usuario usuario) throws BusinessException {
		return lancamentoContaRepository.findAllPagamentosPagosActivedLancamentosByTipoLancamentoAndUsuario(tipo, usuario);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosPorTipoLancamentoEUsuarioEPago(TipoLancamentoPeriodico tipo, Usuario usuario, Boolean pago) throws BusinessException {
		return lancamentoContaRepository.findPagamentosByTipoLancamentoAndUsuarioAndPago(tipo, usuario, pago);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosPorTipoLancamentoEContaEPago(TipoLancamentoPeriodico tipo, Conta conta,Boolean pago) throws BusinessException {
		return lancamentoContaRepository.findPagamentosByTipoLancamentoAndContaAndPago(tipo, conta, pago);
	}
	
	@Override
	public List<LancamentoConta> buscarPagamentosPorTipoLancamentoETipoContaEPago(TipoLancamentoPeriodico tipo, TipoConta tipoConta, Boolean pago) throws BusinessException {
		return lancamentoContaRepository.findPagamentosByTipoLancamentoAndTipoContaAndPago(tipo, tipoConta, pago);
	}
	
	@Override
	public List<LancamentoPeriodico> buscarPorTipoLancamentoETipoContaEStatusLancamento(TipoLancamentoPeriodico tipo, TipoConta tipoConta, StatusLancamento statusLancamento) throws BusinessException {
		return getRepository().findByTipoLancamentoAndTipoContaAndStatusLancamento(tipo, tipoConta, statusLancamento);
	}
}