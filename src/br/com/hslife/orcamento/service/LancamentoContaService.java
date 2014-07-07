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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.component.ContaComponent;
import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FaturaCartao;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.PanoramaLancamentoConta;
import br.com.hslife.orcamento.enumeration.IncrementoClonagemLancamento;
import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.enumeration.StatusFaturaCartao;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.model.AgrupamentoLancamento;
import br.com.hslife.orcamento.model.CriterioLancamentoConta;
import br.com.hslife.orcamento.repository.FaturaCartaoRepository;
import br.com.hslife.orcamento.repository.FechamentoPeriodoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoImportadoRepository;
import br.com.hslife.orcamento.repository.MoedaRepository;
import br.com.hslife.orcamento.repository.PanoramaLancamentoContaRepository;
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

	@Autowired
	private PanoramaLancamentoContaRepository panoramaLancamentoContaRepository;

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
	public void alterar(LancamentoConta entity) throws BusinessException {
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
		super.alterar(entity);
	}

	@Override
	public void excluir(LancamentoConta entity) throws BusinessException {
		// TODO remover esta validação na versão AGO2014 após concluir a tarefa #1045
		if (entity.getLancamentoPeriodico() != null && entity.getLancamentoPeriodico().getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.PARCELADO)) {
			throw new BusinessException("Este lançamento não pode ser excluído pois representa uma parcela!");
		}
		super.excluir(entity);
	}
	
	@Override
	public List<LancamentoConta> buscarPorCriterioLancamentoConta(CriterioLancamentoConta criterio) throws BusinessException {
		return getComponent().buscarPorCriterioLancamentoConta(criterio);
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
			switch (entity.getLancamentoPeriodico().getTipoLancamentoPeriodico()) {
				case FIXO :
					lancamento = getRepository().findLancamentoByPeriodoAndAnoAndLancamentoPeriodico(entity.getPeriodo(), entity.getAno(), entity.getLancamentoPeriodico());					 
					if (lancamento != null && !lancamento.equals(entity)) {
						throw new BusinessException("Já existe lançamento com o período e ano informados!");
					}
					break;
				case PARCELADO : 
					lancamento = getRepository().findLancamentoByParcelaAndLancamentoPeriodico(entity.getParcela(), entity.getLancamentoPeriodico());
					if (lancamento != null && !lancamento.equals(entity)) {
						throw new BusinessException("Já existe lançamento com a parcela informada!");
					}
					break;
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
	public void vincularAFaturaAtual(LancamentoConta lancamento) throws BusinessException {
		FaturaCartao faturaAtual = faturaCartaoRepository.findFaturaCartaoAberta(lancamento.getConta());
		faturaAtual.getDetalheFatura().add(lancamento);
		faturaCartaoRepository.update(faturaAtual);		
	}
	
	@Override
	public void vincularAProximaFatura(LancamentoConta lancamento) throws BusinessException {
		FaturaCartao faturaFutura = faturaCartaoRepository.findNextFaturaCartaoFutura(lancamento.getConta());
		if (faturaFutura == null) {
			// Busca a fatura atual
			FaturaCartao faturaAtual = faturaCartaoRepository.findFaturaCartaoAberta(lancamento.getConta());
			
			// Instancia uma nova fatura futura
			faturaFutura = new FaturaCartao();
			
			// Preenche os atributos da fatura futura
			faturaFutura.setConta(lancamento.getConta());
			faturaFutura.setMoeda(lancamento.getMoeda());
			faturaFutura.setStatusFaturaCartao(StatusFaturaCartao.FUTURA);
			
			// Data de vencimento da próxima fatura
			Calendar vencimento = Calendar.getInstance();
			vencimento.setTime(faturaAtual.getDataVencimento());		
			vencimento.add(Calendar.MONTH, 1);		
			faturaFutura.setDataVencimento(vencimento.getTime());
					
			// Salva a nova fatura
			faturaCartaoRepository.save(faturaFutura);
			
			// Adiciona o lançamento criado a fatura
			faturaFutura.getDetalheFatura().add(lancamento);
			
			// Atualiza a nova fatura
			faturaCartaoRepository.update(faturaFutura);
		} else {
			faturaFutura.getDetalheFatura().add(lancamento);
			faturaCartaoRepository.update(faturaFutura);
		}		
	}
	
	@Override
	public boolean existeVinculoFaturaCartao(LancamentoConta lancamento) throws BusinessException {
		return getRepository().existsLinkageFaturaCartao(lancamento);
	}
	
	@SuppressWarnings("deprecation")
	public void gerarRelatorioPanoramaLancamentoConta(CriterioLancamentoConta criterioBusca, int ano, Integer mesAEstimar) throws BusinessException {
		// Exclui o relatório existente
		panoramaLancamentoContaRepository.deletePanoramaLancamentoConta(criterioBusca.getConta(), ano);
				
		// Declara o Map de previsão de lançamentos da conta
		Map<String, PanoramaLancamentoConta> mapPanoramaLancamentos = new HashMap<String, PanoramaLancamentoConta>();
		
		// Insere no Map o panorama para o cálculo do saldo anterior
		PanoramaLancamentoConta saldoAnterior = new PanoramaLancamentoConta();
		saldoAnterior.setConta(criterioBusca.getConta());
		saldoAnterior.setAno(ano);
		saldoAnterior.setOid(Util.MD5("Saldo Anterior"));
		saldoAnterior.setDescricao("Saldo Anterior");
		saldoAnterior.setIndice(mapPanoramaLancamentos.values().size());
		
		mapPanoramaLancamentos.put(saldoAnterior.getOid(), saldoAnterior);
		
		// Busca os lançamentos e classifica-os em suas respectivas categorias
		List<Categoria> categorias = component.organizarLancamentosPorCategoria(getRepository().findByCriterioLancamentoConta(criterioBusca));
		
		for (Categoria categoria : categorias) {
			String oid = Util.MD5(categoria.getDescricao());
			if (mapPanoramaLancamentos.containsKey(oid)) {
				// Rotina de inserção dos valores dos lançamentos no panorama
				for (LancamentoConta lancamento : categoria.getLancamentos()) {
					mapPanoramaLancamentos.get(oid).setarMes(lancamento.getDataPagamento().getMonth(), lancamento);
				}
			} else {
				PanoramaLancamentoConta panorama = new PanoramaLancamentoConta();
				panorama.setConta(criterioBusca.getConta());
				panorama.setDescricao(categoria.getDescricao());
				panorama.setAno(ano);
				panorama.setOid(oid);								
				panorama.setIndice(mapPanoramaLancamentos.values().size() + 1);
				mapPanoramaLancamentos.put(oid, panorama);
				
				// Rotina de inserção dos valores dos lançamentos no panorama
				for (LancamentoConta lancamento : categoria.getLancamentos()) {
					mapPanoramaLancamentos.get(oid).setarMes(lancamento.getDataPagamento().getMonth(), lancamento);
					
					// Verifica se o mês do pagamento é igual ao mês que será estimado
					if (mesAEstimar != null && lancamento.getDataPagamento().getMonth() == mesAEstimar - 1) {
						lancamento.getDataPagamento().setMonth(new Date().getMonth());
						List<LancamentoConta> lancamentosEstimados = lancamento.clonarLancamentos(12 - (new Date().getMonth() + 1), IncrementoClonagemLancamento.MES);
						
						// Insere os valores no Map para os meses subsequentes
						for (LancamentoConta l : lancamentosEstimados) {
							mapPanoramaLancamentos.get(oid).setarMes(l.getDataPagamento().getMonth(), l);
						}
					}
				}
			}
		}
		
		// Realiza o cálculo do saldo total
		PanoramaLancamentoConta saldoTotal = new PanoramaLancamentoConta();
		saldoTotal.setConta(criterioBusca.getConta());
		saldoTotal.setAno(ano);
		saldoTotal.setOid(Util.MD5("Saldo Total"));
		saldoTotal.setDescricao("Saldo Total");
		saldoTotal.setIndice(mapPanoramaLancamentos.values().size() + 1);
		
		for (PanoramaLancamentoConta panorama : mapPanoramaLancamentos.values()) {
			saldoTotal.setJaneiro(saldoTotal.getJaneiro() + panorama.getJaneiro());
			saldoTotal.setFevereiro(saldoTotal.getFevereiro() + panorama.getFevereiro());
			saldoTotal.setMarco(saldoTotal.getMarco() + panorama.getMarco());
			saldoTotal.setAbril(saldoTotal.getAbril() + panorama.getAbril());
			saldoTotal.setMaio(saldoTotal.getMaio() + panorama.getMaio());
			saldoTotal.setJunho(saldoTotal.getJunho() + panorama.getJunho());
			saldoTotal.setJulho(saldoTotal.getJulho() + panorama.getJulho());
			saldoTotal.setAgosto(saldoTotal.getAgosto() + panorama.getAgosto());
			saldoTotal.setSetembro(saldoTotal.getSetembro() + panorama.getSetembro());
			saldoTotal.setOutubro(saldoTotal.getOutubro() + panorama.getOutubro());
			saldoTotal.setNovembro(saldoTotal.getNovembro() + panorama.getNovembro());
			saldoTotal.setDezembro(saldoTotal.getDezembro() + panorama.getDezembro());
		}
		
		mapPanoramaLancamentos.put(saldoTotal.getOid(), saldoTotal);
		
		// Pegar o valor do último fechamento do ano anterior e atribui no mês de janeiro do panorama do saldo total	
		FechamentoPeriodo fechamento = fechamentoPeriodoRepository.findLastFechamentoPeriodoBeforeDateByContaAndOperacao(criterioBusca.getConta(), Util.ultimoDiaAno(ano - 1), OperacaoConta.FECHAMENTO);
		if (fechamento == null) {
			mapPanoramaLancamentos.get(saldoAnterior.getOid()).setJaneiro(criterioBusca.getConta().getSaldoInicial());
		} else {
			mapPanoramaLancamentos.get(saldoAnterior.getOid()).setJaneiro(fechamento.getSaldo());
		}	
		mapPanoramaLancamentos.get(saldoTotal.getOid()).setJaneiro(mapPanoramaLancamentos.get(saldoTotal.getOid()).getJaneiro() + mapPanoramaLancamentos.get(saldoAnterior.getOid()).getJaneiro());
		
		// Preenche o panorama do saldo anterior 		
		mapPanoramaLancamentos.get(saldoAnterior.getOid()).setFevereiro(mapPanoramaLancamentos.get(saldoTotal.getOid()).getJaneiro());
		mapPanoramaLancamentos.get(saldoTotal.getOid()).setFevereiro(mapPanoramaLancamentos.get(saldoTotal.getOid()).getFevereiro() + mapPanoramaLancamentos.get(saldoAnterior.getOid()).getFevereiro());
		
		mapPanoramaLancamentos.get(saldoAnterior.getOid()).setMarco(mapPanoramaLancamentos.get(saldoTotal.getOid()).getFevereiro());
		mapPanoramaLancamentos.get(saldoTotal.getOid()).setMarco(mapPanoramaLancamentos.get(saldoTotal.getOid()).getMarco() + mapPanoramaLancamentos.get(saldoAnterior.getOid()).getMarco());
		
		mapPanoramaLancamentos.get(saldoAnterior.getOid()).setAbril(mapPanoramaLancamentos.get(saldoTotal.getOid()).getMarco());
		mapPanoramaLancamentos.get(saldoTotal.getOid()).setAbril(mapPanoramaLancamentos.get(saldoTotal.getOid()).getAbril() + mapPanoramaLancamentos.get(saldoAnterior.getOid()).getAbril());
		
		mapPanoramaLancamentos.get(saldoAnterior.getOid()).setMaio(mapPanoramaLancamentos.get(saldoTotal.getOid()).getAbril());
		mapPanoramaLancamentos.get(saldoTotal.getOid()).setMaio(mapPanoramaLancamentos.get(saldoTotal.getOid()).getMaio() + mapPanoramaLancamentos.get(saldoAnterior.getOid()).getMaio());
		
		mapPanoramaLancamentos.get(saldoAnterior.getOid()).setJunho(mapPanoramaLancamentos.get(saldoTotal.getOid()).getMaio());
		mapPanoramaLancamentos.get(saldoTotal.getOid()).setJunho(mapPanoramaLancamentos.get(saldoTotal.getOid()).getJunho() + mapPanoramaLancamentos.get(saldoAnterior.getOid()).getJunho());
		
		mapPanoramaLancamentos.get(saldoAnterior.getOid()).setJulho(mapPanoramaLancamentos.get(saldoTotal.getOid()).getJunho());
		mapPanoramaLancamentos.get(saldoTotal.getOid()).setJulho(mapPanoramaLancamentos.get(saldoTotal.getOid()).getJulho() + mapPanoramaLancamentos.get(saldoAnterior.getOid()).getJulho());
		
		mapPanoramaLancamentos.get(saldoAnterior.getOid()).setAgosto(mapPanoramaLancamentos.get(saldoTotal.getOid()).getJulho());
		mapPanoramaLancamentos.get(saldoTotal.getOid()).setAgosto(mapPanoramaLancamentos.get(saldoTotal.getOid()).getAgosto() + mapPanoramaLancamentos.get(saldoAnterior.getOid()).getAgosto());
		
		mapPanoramaLancamentos.get(saldoAnterior.getOid()).setSetembro(mapPanoramaLancamentos.get(saldoTotal.getOid()).getAgosto());
		mapPanoramaLancamentos.get(saldoTotal.getOid()).setSetembro(mapPanoramaLancamentos.get(saldoTotal.getOid()).getSetembro() + mapPanoramaLancamentos.get(saldoAnterior.getOid()).getSetembro());
		
		mapPanoramaLancamentos.get(saldoAnterior.getOid()).setOutubro(mapPanoramaLancamentos.get(saldoTotal.getOid()).getSetembro());
		mapPanoramaLancamentos.get(saldoTotal.getOid()).setOutubro(mapPanoramaLancamentos.get(saldoTotal.getOid()).getOutubro() + mapPanoramaLancamentos.get(saldoAnterior.getOid()).getOutubro());
		
		mapPanoramaLancamentos.get(saldoAnterior.getOid()).setNovembro(mapPanoramaLancamentos.get(saldoTotal.getOid()).getOutubro());
		mapPanoramaLancamentos.get(saldoTotal.getOid()).setNovembro(mapPanoramaLancamentos.get(saldoTotal.getOid()).getNovembro() + mapPanoramaLancamentos.get(saldoAnterior.getOid()).getNovembro());
		
		mapPanoramaLancamentos.get(saldoAnterior.getOid()).setDezembro(mapPanoramaLancamentos.get(saldoTotal.getOid()).getNovembro());
		mapPanoramaLancamentos.get(saldoTotal.getOid()).setDezembro(mapPanoramaLancamentos.get(saldoTotal.getOid()).getDezembro() + mapPanoramaLancamentos.get(saldoAnterior.getOid()).getDezembro());
		
		// Salva na base
		for (PanoramaLancamentoConta panorama : mapPanoramaLancamentos.values()) {
			panoramaLancamentoContaRepository.save(panorama);
		}
		
		// Limpa o Map para facilitar o GC de recolher da memória
		mapPanoramaLancamentos.clear();
	}
	
	public List<PanoramaLancamentoConta> visualizarRelatorioPanoramaLancamentoConta(Conta conta, int ano) throws BusinessException {
		return panoramaLancamentoContaRepository.findByContaAnoAndAgrupamento(conta, ano);
	}
}