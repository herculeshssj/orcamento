/***
  
  	Copyright (c) 2012 - 2015 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@gmail.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.component.ContaComponent;
import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.model.AgrupamentoLancamento;
import br.com.hslife.orcamento.repository.FaturaCartaoRepository;
import br.com.hslife.orcamento.repository.FechamentoPeriodoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoImportadoRepository;
import br.com.hslife.orcamento.repository.MoedaRepository;
import br.com.hslife.orcamento.util.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.util.Util;

@Service("lancamentoContaService")
public class LancamentoContaService extends AbstractCRUDService<LancamentoConta> implements ILancamentoConta {

	@Autowired
	private LancamentoContaRepository repository;
	
	@Autowired
	private ContaComponent component;
	
	@Autowired
	private FechamentoPeriodoRepository fechamentoPeriodoRepository;
	
	@Autowired
	private LancamentoImportadoRepository lancamentoImportadoRepository;
	
	@Autowired
	private FaturaCartaoRepository faturaCartaoRepository;
	
	@Autowired
	private MoedaRepository moedaRepository;

	public LancamentoContaRepository getRepository() {
		return repository;
	}

	public void setRepository(LancamentoContaRepository repository) {
		this.repository = repository;
	}
	
	public void setFechamentoPeriodoRepository(
			FechamentoPeriodoRepository fechamentoPeriodoRepository) {
		this.fechamentoPeriodoRepository = fechamentoPeriodoRepository;
	}

	public ContaComponent getComponent() {
		return component;
	}

	public void setComponent(ContaComponent component) {
		this.component = component;
	}

	public void setLancamentoImportadoRepository(
			LancamentoImportadoRepository lancamentoImportadoRepository) {
		this.lancamentoImportadoRepository = lancamentoImportadoRepository;
	}
	
	public void setFaturaCartaoRepository(
			FaturaCartaoRepository faturaCartaoRepository) {
		this.faturaCartaoRepository = faturaCartaoRepository;
	}

	public void setMoedaRepository(MoedaRepository moedaRepository) {
		this.moedaRepository = moedaRepository;
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
			entity.setMoeda(moedaRepository.findCodigoMoedaByUsuario(entity.getLancamentoImportado().getMoeda(), entity.getConta().getUsuario()) == null 
					? moedaRepository.findDefaultByUsuario(entity.getConta().getUsuario()) 
					: moedaRepository.findCodigoMoedaByUsuario(entity.getLancamentoImportado().getMoeda(), entity.getConta().getUsuario()));
			
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
						
			lancamentoImportadoRepository.delete(entity.getLancamentoImportado());
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
		return getComponent().calcularSaldoLancamentos(lancamentos);
	}
	
	public double saldoUltimoFechamento(Conta conta) throws BusinessException {
		FechamentoPeriodo ultimoFechamento = fechamentoPeriodoRepository.findUltimoFechamentoByConta(conta);
		if (ultimoFechamento == null) {
			return conta.getSaldoInicial();
		} else {
			return ultimoFechamento.getSaldo();
		}
	}

	@Override
	public List<Categoria> organizarLancamentosPorCategoria(List<LancamentoConta> lancamentos) throws BusinessException {
		return getComponent().organizarLancamentosPorCategoria(lancamentos);
	}

	@Override
	public List<Favorecido> organizarLancamentosPorFavorecido(List<LancamentoConta> lancamentos) throws BusinessException {
		return getComponent().organizarLancamentosPorFavorecido(lancamentos);
	}

	@Override
	public List<MeioPagamento> organizarLancamentosPorMeioPagamento(List<LancamentoConta> lancamentos) throws BusinessException {
		return getComponent().organizarLancamentosPorMeioPagamento(lancamentos);
	}
	
	@Override
	public List<Moeda> organizarLancamentosPorMoeda(List<LancamentoConta> lancamentos) throws BusinessException {
		return getComponent().organizarLancamentosPorMoeda(lancamentos);
	}

	@Override
	public List<AgrupamentoLancamento> organizarLancamentosPorDebitoCredito(List<LancamentoConta> lancamentos) throws BusinessException {
		List<AgrupamentoLancamento> debitosCreditos = new ArrayList<AgrupamentoLancamento>();
		
		AgrupamentoLancamento agrupamentoDebito = new AgrupamentoLancamento("Débitos");
		AgrupamentoLancamento agrupamentoCredito = new AgrupamentoLancamento("Créditos");
		
		// Varre a lista de lançamentos para adicionar os lançamentos nas respectivas instâncias de AgrupamentoLancamento 
		for (LancamentoConta l : lancamentos) {
			if (l.getTipoLancamento().equals(TipoLancamento.DESPESA)) {
				agrupamentoDebito.getLancamentos().add(l);
				agrupamentoDebito.setSaldoPago(agrupamentoDebito.getSaldoPago() + l.getValorPago());
			} else {
				agrupamentoCredito.getLancamentos().add(l);
				agrupamentoCredito.setSaldoPago(agrupamentoCredito.getSaldoPago() + l.getValorPago());
			}
		}
		
		// Corrige as casas decimais e adiciona na lista de AgrupamentoLancamento
		agrupamentoCredito.setSaldoPago(Util.arredondar(agrupamentoCredito.getSaldoPago()));
		agrupamentoDebito.setSaldoPago(Util.arredondar(agrupamentoDebito.getSaldoPago()));
		
		debitosCreditos.add(agrupamentoCredito);
		debitosCreditos.add(agrupamentoDebito);

		return debitosCreditos;
	}
	
	@Override
	public List<LancamentoImportado> buscarLancamentoImportadoPorConta(Conta conta) throws BusinessException {
		return lancamentoImportadoRepository.findByConta(conta);
	}
	
	@Override
	public boolean existeVinculoFaturaCartao(LancamentoConta lancamento) throws BusinessException {
		return getRepository().existsLinkageFaturaCartao(lancamento);
	}
	
	@Override
	public List<FechamentoPeriodo> buscarPorContaEOperacaoConta(Conta conta, OperacaoConta operacaoConta) throws BusinessException {
		return getComponent().buscarPorContaEOperacaoConta(conta, operacaoConta);
	}
	
	public void fecharPeriodo(Date dataFechamento, Conta conta) throws BusinessException {
		getComponent().fecharPeriodo(dataFechamento, conta);
	}
	
	public void fecharPeriodo(Date dataFechamento, Conta conta, List<LancamentoPeriodico> lancamentosPeriodicos) throws BusinessException {
		getComponent().fecharPeriodo(dataFechamento, conta, lancamentosPeriodicos);
	}
	
	@Override
	public void fecharPeriodo(FechamentoPeriodo fechamentoPeriodo, List<LancamentoPeriodico> lancamentosPeriodicos) throws BusinessException {
		getComponent().fecharPeriodo(fechamentoPeriodo, lancamentosPeriodicos);		
	}
	
	@Override
	public FechamentoPeriodo buscarFechamentoPeriodoAnterior(FechamentoPeriodo fechamentoPeriodo) {
		return fechamentoPeriodoRepository.findFechamentoPeriodoAnterior(fechamentoPeriodo);
	}
	
	@Override
	public FechamentoPeriodo buscarUltimoFechamentoConta(Conta conta) {
		return fechamentoPeriodoRepository.findUltimoFechamentoByConta(conta);
	}

	@Override
	public void reabrirPeriodo(FechamentoPeriodo entity)throws BusinessException {
		getComponent().reabrirPeriodo(entity);		
	}
	
	@Override
	public List<FechamentoPeriodo> buscarTodosFechamentoPorConta(Conta conta) throws BusinessException {
		return fechamentoPeriodoRepository.findAllByConta(conta);
	}
	
	@Override
	public FechamentoPeriodo buscarFechamentoPorID(Long id) throws BusinessException {
		return fechamentoPeriodoRepository.findById(id);
	}
}