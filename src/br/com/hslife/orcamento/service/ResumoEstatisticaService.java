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
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.PanoramaLancamentoCartao;
import br.com.hslife.orcamento.entity.PrevisaoLancamentoConta;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.LancamentoAgendado;
import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IResumoEstatistica;
import br.com.hslife.orcamento.model.CriterioLancamentoConta;
import br.com.hslife.orcamento.model.ResumoMensalContas;
import br.com.hslife.orcamento.model.SaldoAtualConta;
import br.com.hslife.orcamento.repository.ContaRepository;
import br.com.hslife.orcamento.repository.FechamentoPeriodoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.PanoramaLancamentoCartaoRepository;
import br.com.hslife.orcamento.repository.PrevisaoLancamentoContaRepository;
import br.com.hslife.orcamento.util.Util;

@Service("resumoEstatisticaService")
public class ResumoEstatisticaService implements IResumoEstatistica {

	/*** Declaração dos repositórios ***/
	
	@Autowired
	private PrevisaoLancamentoContaRepository previsaoLancamentoContaRepository;
	
	@Autowired
	private PanoramaLancamentoCartaoRepository panoramaLancamentoCartaoRepository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private FechamentoPeriodoRepository fechamentoPeriodoRepository;

	/*** Declaração dos componentes ***/
	
	@Autowired
	private ContaComponent contaComponent;	
	
	/*** Declaração dos métodos Setters dos repositórios ***/
	
	public void setPrevisaoLancamentoContaRepository(
			PrevisaoLancamentoContaRepository previsaoLancamentoContaRepository) {
		this.previsaoLancamentoContaRepository = previsaoLancamentoContaRepository;
	}
	
	public void setLancamentoContaRepository(
			LancamentoContaRepository lancamentoContaRepository) {
		this.lancamentoContaRepository = lancamentoContaRepository;
	}
	
	public void setContaRepository(ContaRepository contaRepository) {
		this.contaRepository = contaRepository;
	}

	public void setFechamentoPeriodoRepository(
			FechamentoPeriodoRepository fechamentoPeriodoRepository) {
		this.fechamentoPeriodoRepository = fechamentoPeriodoRepository;
	}
	
	public void setPanoramaLancamentoCartaoRepository(
			PanoramaLancamentoCartaoRepository panoramaLancamentoCartaoRepository) {
		this.panoramaLancamentoCartaoRepository = panoramaLancamentoCartaoRepository;
	}

	/*** Declaração dos métodos Setters dos componentes ***/
	
	public void setContaComponent(ContaComponent contaComponent) {
		this.contaComponent = contaComponent;
	}	

	/*** Implementação dos métodos da interface ***/
	
	public List<SaldoAtualConta> gerarSaldoAtualContas(boolean lancamentoAgendado, Usuario usuario) throws BusinessException {
		// Declaração dos objetos
		List<SaldoAtualConta> saldoAtualContas = new ArrayList<>();
		SaldoAtualConta saldoAtual = new SaldoAtualConta();
		
		// Itera todas as contas do usuário
		for (Conta conta : contaRepository.findByUsuario(usuario.getId())) {
			
			// Define a descrição da conta
			saldoAtual.setDescricaoConta(conta.getDescricao());
			
			// Define a situação da conta
			saldoAtual.setAtivo(conta.isAtivo());
			
			// Traz o último período de fechamento da conta
			FechamentoPeriodo ultimoFechamento = fechamentoPeriodoRepository.findUltimoFechamentoByConta(conta); 
			if (ultimoFechamento == null) {
				saldoAtual.setSaldoPeriodo(conta.getSaldoInicial());
			} else {
				saldoAtual.setSaldoPeriodo(ultimoFechamento.getSaldo());
			}
			
			// Traz os lançamentos da data de fechamento em diante			
			CriterioLancamentoConta criterio = new CriterioLancamentoConta();
			if (lancamentoAgendado)
				criterio.setAgendado(LancamentoAgendado.COM);
			else
				criterio.setAgendado(LancamentoAgendado.SEM);
			criterio.setConta(conta);
			if (ultimoFechamento == null) {
				criterio.setDataInicio(conta.getDataAbertura());
			}
			else {
				Calendar temp = Calendar.getInstance();
				temp.setTime(ultimoFechamento.getData());
				temp.add(Calendar.DAY_OF_YEAR, 1);
				criterio.setDataInicio(temp.getTime());
			}				
			
			List<LancamentoConta> lancamentos = lancamentoContaRepository.findByCriterioLancamentoConta(criterio);
			
			// Calcula o saldo dos lançamentos e registra no saldo atual
			saldoAtual.setSaldoRegistrado(contaComponent.calcularSaldoLancamentos(lancamentos));
			
			// Calcula o saldo atual da conta
			saldoAtual.setSaldoAtual(saldoAtual.getSaldoPeriodo() + saldoAtual.getSaldoRegistrado());
			
			// Adiciona na lista de saldos da conta
			saldoAtualContas.add(saldoAtual);
			saldoAtual = new SaldoAtualConta();
		}
		
		// Retorna a lista de saldos atuais
		return saldoAtualContas;
	}
	
	public List<PrevisaoLancamentoConta> visualizarRelatorioPrevisaoLancamentoConta(Conta conta, int ano) throws BusinessException {
		return previsaoLancamentoContaRepository.findByContaAnoAndAgrupamento(conta, ano);
	}
	
	@Override
	public void gerarRelatorioPrevisaoLancamentoConta(CriterioLancamentoConta criterioBusca, int ano) throws BusinessException {
		// Exclui o relatório existente
		previsaoLancamentoContaRepository.deletePrevisaoLancamentoConta(criterioBusca.getConta(), ano);
		
		// Declara o Map de previsão de lançamentos da conta
		Map<String, PrevisaoLancamentoConta> mapPrevisaoLancamentos = new HashMap<String, PrevisaoLancamentoConta>();
		
		PrevisaoLancamentoConta saldoAnterior = new PrevisaoLancamentoConta();
		saldoAnterior.setConta(criterioBusca.getConta());
		saldoAnterior.setAno(ano);
		saldoAnterior.setOid(Util.MD5("Saldo Anterior"));
		saldoAnterior.setDescricaoPrevisao("Saldo Anterior");
		saldoAnterior.setIndice(mapPrevisaoLancamentos.values().size());
		
		mapPrevisaoLancamentos.put(saldoAnterior.getOid(), saldoAnterior);
		
		// Busca os lançamentos a partir do critério de busca fornecido
		// Logo após itera os lançamentos
		for (LancamentoConta lancamento : lancamentoContaRepository.findByCriterioLancamentoConta(criterioBusca)) {
			String oid;
			if (lancamento.getCategoria() == null) {
				oid = Util.MD5("Sem categoria");
			} else {
				oid = Util.MD5(lancamento.getCategoria().getDescricao());
			}									
			if (mapPrevisaoLancamentos.containsKey(oid)) {
				this.inserirValorMesPrevisaoLancamentoConta(mapPrevisaoLancamentos, lancamento, oid);
			} else {
				PrevisaoLancamentoConta previsao = new PrevisaoLancamentoConta();
				previsao.setConta(lancamento.getConta());
				previsao.setAno(ano);
				previsao.setOid(oid);
				if (lancamento.getCategoria() == null) {
					previsao.setDescricaoPrevisao("Sem categoria");
				} else {
					previsao.setDescricaoPrevisao(lancamento.getCategoria().getDescricao());
				}				
				previsao.setIndice(mapPrevisaoLancamentos.values().size() + 1);
				mapPrevisaoLancamentos.put(oid, previsao);
				this.inserirValorMesPrevisaoLancamentoConta(mapPrevisaoLancamentos, lancamento, oid);
			}
		}
		
		// Calcular o saldo total
		PrevisaoLancamentoConta saldoTotal = new PrevisaoLancamentoConta();
		saldoTotal.setConta(criterioBusca.getConta());
		saldoTotal.setAno(ano);
		saldoTotal.setOid(Util.MD5("Saldo Total"));
		saldoTotal.setDescricaoPrevisao("Saldo Total");
		saldoTotal.setIndice(mapPrevisaoLancamentos.values().size() + 1);
		
		for (PrevisaoLancamentoConta previsao : mapPrevisaoLancamentos.values()) {
			saldoTotal.setJaneiro(saldoTotal.getJaneiro() + previsao.getJaneiro());
			saldoTotal.setFevereiro(saldoTotal.getFevereiro() + previsao.getFevereiro());
			saldoTotal.setMarco(saldoTotal.getMarco() + previsao.getMarco());
			saldoTotal.setAbril(saldoTotal.getAbril() + previsao.getAbril());
			saldoTotal.setMaio(saldoTotal.getMaio() + previsao.getMaio());
			saldoTotal.setJunho(saldoTotal.getJunho() + previsao.getJunho());
			saldoTotal.setJulho(saldoTotal.getJulho() + previsao.getJulho());
			saldoTotal.setAgosto(saldoTotal.getAgosto() + previsao.getAgosto());
			saldoTotal.setSetembro(saldoTotal.getSetembro() + previsao.getSetembro());
			saldoTotal.setOutubro(saldoTotal.getOutubro() + previsao.getOutubro());
			saldoTotal.setNovembro(saldoTotal.getNovembro() + previsao.getNovembro());
			saldoTotal.setDezembro(saldoTotal.getDezembro() + previsao.getDezembro());
		}
		
		mapPrevisaoLancamentos.put(saldoTotal.getOid(), saldoTotal);
		
		// Pegar o valor do último fechamento do ano anterior		
		FechamentoPeriodo fechamento = fechamentoPeriodoRepository.findLastFechamentoPeriodoBeforeDateByContaAndOperacao(criterioBusca.getConta(), Util.ultimoDiaAno(ano - 1), OperacaoConta.FECHAMENTO);
		if (fechamento == null) {
			mapPrevisaoLancamentos.get(saldoAnterior.getOid()).setJaneiro(criterioBusca.getConta().getSaldoInicial());
		} else {
			mapPrevisaoLancamentos.get(saldoAnterior.getOid()).setJaneiro(fechamento.getSaldo());
		}	
		mapPrevisaoLancamentos.get(saldoTotal.getOid()).setJaneiro(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getJaneiro() + mapPrevisaoLancamentos.get(saldoAnterior.getOid()).getJaneiro());
		
		// Preenche o saldo anterior		
		mapPrevisaoLancamentos.get(saldoAnterior.getOid()).setFevereiro(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getJaneiro());
		mapPrevisaoLancamentos.get(saldoTotal.getOid()).setFevereiro(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getFevereiro() + mapPrevisaoLancamentos.get(saldoAnterior.getOid()).getFevereiro());
		
		mapPrevisaoLancamentos.get(saldoAnterior.getOid()).setMarco(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getFevereiro());
		mapPrevisaoLancamentos.get(saldoTotal.getOid()).setMarco(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getMarco() + mapPrevisaoLancamentos.get(saldoAnterior.getOid()).getMarco());
		
		mapPrevisaoLancamentos.get(saldoAnterior.getOid()).setAbril(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getMarco());
		mapPrevisaoLancamentos.get(saldoTotal.getOid()).setAbril(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getAbril() + mapPrevisaoLancamentos.get(saldoAnterior.getOid()).getAbril());
		
		mapPrevisaoLancamentos.get(saldoAnterior.getOid()).setMaio(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getAbril());
		mapPrevisaoLancamentos.get(saldoTotal.getOid()).setMaio(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getMaio() + mapPrevisaoLancamentos.get(saldoAnterior.getOid()).getMaio());
		
		mapPrevisaoLancamentos.get(saldoAnterior.getOid()).setJunho(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getMaio());
		mapPrevisaoLancamentos.get(saldoTotal.getOid()).setJunho(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getJunho() + mapPrevisaoLancamentos.get(saldoAnterior.getOid()).getJunho());
		
		mapPrevisaoLancamentos.get(saldoAnterior.getOid()).setJulho(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getJunho());
		mapPrevisaoLancamentos.get(saldoTotal.getOid()).setJulho(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getJulho() + mapPrevisaoLancamentos.get(saldoAnterior.getOid()).getJulho());
		
		mapPrevisaoLancamentos.get(saldoAnterior.getOid()).setAgosto(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getJulho());
		mapPrevisaoLancamentos.get(saldoTotal.getOid()).setAgosto(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getAgosto() + mapPrevisaoLancamentos.get(saldoAnterior.getOid()).getAgosto());
		
		mapPrevisaoLancamentos.get(saldoAnterior.getOid()).setSetembro(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getAgosto());
		mapPrevisaoLancamentos.get(saldoTotal.getOid()).setSetembro(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getSetembro() + mapPrevisaoLancamentos.get(saldoAnterior.getOid()).getSetembro());
		
		mapPrevisaoLancamentos.get(saldoAnterior.getOid()).setOutubro(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getSetembro());
		mapPrevisaoLancamentos.get(saldoTotal.getOid()).setOutubro(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getOutubro() + mapPrevisaoLancamentos.get(saldoAnterior.getOid()).getOutubro());
		
		mapPrevisaoLancamentos.get(saldoAnterior.getOid()).setNovembro(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getOutubro());
		mapPrevisaoLancamentos.get(saldoTotal.getOid()).setNovembro(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getNovembro() + mapPrevisaoLancamentos.get(saldoAnterior.getOid()).getNovembro());
		
		mapPrevisaoLancamentos.get(saldoAnterior.getOid()).setDezembro(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getNovembro());
		mapPrevisaoLancamentos.get(saldoTotal.getOid()).setDezembro(mapPrevisaoLancamentos.get(saldoTotal.getOid()).getDezembro() + mapPrevisaoLancamentos.get(saldoAnterior.getOid()).getDezembro());
		
		// Salva na base
		for (PrevisaoLancamentoConta previsao : mapPrevisaoLancamentos.values()) {
			previsaoLancamentoContaRepository.save(previsao);
		}
		
		mapPrevisaoLancamentos.clear();
	}
	
	@Override
	public List<PanoramaLancamentoCartao> visualizarRelatorioPanoramaLancamentoCartao(Conta conta, int ano, Moeda moeda) throws BusinessException {
		return panoramaLancamentoCartaoRepository.findByContaAnoAndMoeda(conta, ano, moeda);
	}
	
	@Override
	public void gerarRelatorioPanoramaLancamentoCartao(CriterioLancamentoConta criterioBusca, int ano) throws BusinessException {
		// Exclui o relatório existente
		panoramaLancamentoCartaoRepository.deletePanoramaLancamentoCartao(criterioBusca.getConta(), ano, criterioBusca.getMoeda());
		
		// Declara o Map de previsão de lançamentos da conta
		Map<String, PanoramaLancamentoCartao> mapPanoramaLancamentos = new HashMap<String, PanoramaLancamentoCartao>();
		
		// Busca os lançamentos a partir do critério de busca fornecido
		// Logo após itera os lançamentos
		for (LancamentoConta lancamento : lancamentoContaRepository.findByCriterioLancamentoCartao(criterioBusca)) {
			String oid;
			if (lancamento.getCategoria() == null) {
				oid = Util.MD5("Sem categoria");
			} else {
				oid = Util.MD5(lancamento.getCategoria().getDescricao());
			}
								
			if (mapPanoramaLancamentos.containsKey(oid)) {
				this.inserirValorMesPanoramaLancamentoCartao(mapPanoramaLancamentos, lancamento, oid);
			} else {
				PanoramaLancamentoCartao panorama = new PanoramaLancamentoCartao();
				panorama.setConta(lancamento.getConta());
				panorama.setMoeda(lancamento.getMoeda());
				panorama.setAno(ano);
				panorama.setOid(oid);				
				if (lancamento.getCategoria() == null) {
					panorama.setDescricao("Sem categoria");
				} else {
					panorama.setDescricao(lancamento.getCategoria().getDescricao());
				}
				
				panorama.setIndice(mapPanoramaLancamentos.values().size() + 1);
				mapPanoramaLancamentos.put(oid, panorama);
				this.inserirValorMesPanoramaLancamentoCartao(mapPanoramaLancamentos, lancamento, oid);
			}
		}
		
		// Calcular o saldo total
		PanoramaLancamentoCartao saldoTotal = new PanoramaLancamentoCartao();
		saldoTotal.setConta(criterioBusca.getConta());
		saldoTotal.setAno(ano);
		saldoTotal.setMoeda(criterioBusca.getMoeda());
		saldoTotal.setOid(Util.MD5("Saldo Total"));
		saldoTotal.setDescricao("Saldo Total");
		saldoTotal.setIndice(mapPanoramaLancamentos.values().size() + 1);
		
		for (PanoramaLancamentoCartao panorama : mapPanoramaLancamentos.values()) {
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
		
		// Salva na base
		for (PanoramaLancamentoCartao panorama : mapPanoramaLancamentos.values()) {
			panoramaLancamentoCartaoRepository.save(panorama);
		}
		
		mapPanoramaLancamentos.clear();		
	}
	
	@Override
	public ResumoMensalContas gerarRelatorioResumoMensalContas(Conta conta, FechamentoPeriodo fechamentoPeriodo) throws BusinessException {
		ResumoMensalContas resumoMensal = new ResumoMensalContas();
		CriterioLancamentoConta criterioBusca = new CriterioLancamentoConta();
		FechamentoPeriodo fechamentoAnterior = null;
				
		// Busca o fechamento do período anterior
		if (fechamentoPeriodo != null) 
			fechamentoAnterior = fechamentoPeriodoRepository.findFechamentoPeriodoAnterior(fechamentoPeriodo);
		else
			fechamentoAnterior = fechamentoPeriodoRepository.findUltimoFechamentoByConta(conta);
		
		// Preenche os parâmetros de busca
		criterioBusca.setAgendado(false);
		criterioBusca.setConta(conta);
		
		// Determina a data de início do período
		if (fechamentoAnterior == null) {
			criterioBusca.setDataInicio(conta.getDataAbertura());
			criterioBusca.setDataFim(new Date());
		} else {
			Calendar temp = Calendar.getInstance();
			temp.setTime(fechamentoAnterior.getData());
			temp.add(Calendar.DAY_OF_YEAR, 1);
			criterioBusca.setDataInicio(temp.getTime());
		}
		
		// Determina a data de fim do período
		if (fechamentoPeriodo == null) {
			criterioBusca.setDataFim(new Date());
		} else {
			criterioBusca.setDataFim(fechamentoPeriodo.getData());
		}
		
		// Realiza a busca
		List<LancamentoConta> lancamentos = lancamentoContaRepository.findByCriterioLancamentoConta(criterioBusca);
		
		// Processa as categorias
		if (fechamentoAnterior == null) {
			resumoMensal.setCategorias(contaComponent.organizarLancamentosPorCategoria(lancamentos), conta.getSaldoInicial(), conta.getSaldoInicial() + contaComponent.calcularSaldoLancamentos(lancamentos));
			resumoMensal.setFavorecidos(contaComponent.organizarLancamentosPorFavorecido(lancamentos), conta.getSaldoInicial(), conta.getSaldoInicial() + contaComponent.calcularSaldoLancamentos(lancamentos));
			resumoMensal.setMeiosPagamento(contaComponent.organizarLancamentosPorMeioPagamento(lancamentos), conta.getSaldoInicial(), conta.getSaldoInicial() + contaComponent.calcularSaldoLancamentos(lancamentos));
		} else {
			resumoMensal.setCategorias(contaComponent.organizarLancamentosPorCategoria(lancamentos), fechamentoAnterior.getSaldo(), fechamentoAnterior.getSaldo() + contaComponent.calcularSaldoLancamentos(lancamentos));
			resumoMensal.setFavorecidos(contaComponent.organizarLancamentosPorFavorecido(lancamentos), fechamentoAnterior.getSaldo(), fechamentoAnterior.getSaldo() + contaComponent.calcularSaldoLancamentos(lancamentos));
			resumoMensal.setMeiosPagamento(contaComponent.organizarLancamentosPorMeioPagamento(lancamentos), fechamentoAnterior.getSaldo(), fechamentoAnterior.getSaldo() + contaComponent.calcularSaldoLancamentos(lancamentos));
		}
		
		return resumoMensal;
	}
	
	/*** Implementação dos métodos privados ***/
	
	@SuppressWarnings("deprecation")
	private void inserirValorMesPrevisaoLancamentoConta(Map<String, PrevisaoLancamentoConta> mapPrevisaoLancamentos, LancamentoConta lancamento, String oid) {
		int mes = lancamento.getDataPagamento().getMonth();
		switch(mes) {
			case Calendar.JANUARY :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPrevisaoLancamentos.get(oid).setJaneiro(mapPrevisaoLancamentos.get(oid).getJaneiro() + lancamento.getValorPago());
				else
					mapPrevisaoLancamentos.get(oid).setJaneiro(mapPrevisaoLancamentos.get(oid).getJaneiro() - lancamento.getValorPago());
				break;
			case Calendar.FEBRUARY :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPrevisaoLancamentos.get(oid).setFevereiro(mapPrevisaoLancamentos.get(oid).getFevereiro() + lancamento.getValorPago());
				else
					mapPrevisaoLancamentos.get(oid).setFevereiro(mapPrevisaoLancamentos.get(oid).getFevereiro() - lancamento.getValorPago());
				break;
			case Calendar.MARCH :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPrevisaoLancamentos.get(oid).setMarco(mapPrevisaoLancamentos.get(oid).getMarco() + lancamento.getValorPago());
				else
					mapPrevisaoLancamentos.get(oid).setMarco(mapPrevisaoLancamentos.get(oid).getMarco() - lancamento.getValorPago());
				break;
			case Calendar.APRIL :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPrevisaoLancamentos.get(oid).setAbril(mapPrevisaoLancamentos.get(oid).getAbril() + lancamento.getValorPago());
				else
					mapPrevisaoLancamentos.get(oid).setAbril(mapPrevisaoLancamentos.get(oid).getAbril() - lancamento.getValorPago());
				break;
			case Calendar.MAY :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPrevisaoLancamentos.get(oid).setMaio(mapPrevisaoLancamentos.get(oid).getMaio() + lancamento.getValorPago());
				else
					mapPrevisaoLancamentos.get(oid).setMaio(mapPrevisaoLancamentos.get(oid).getMaio() - lancamento.getValorPago());
				break;
			case Calendar.JUNE :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPrevisaoLancamentos.get(oid).setJunho(mapPrevisaoLancamentos.get(oid).getJunho() + lancamento.getValorPago());
				else
					mapPrevisaoLancamentos.get(oid).setJunho(mapPrevisaoLancamentos.get(oid).getJunho() - lancamento.getValorPago());
				break;
			case Calendar.JULY :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPrevisaoLancamentos.get(oid).setJulho(mapPrevisaoLancamentos.get(oid).getJulho() + lancamento.getValorPago());
				else
					mapPrevisaoLancamentos.get(oid).setJulho(mapPrevisaoLancamentos.get(oid).getJulho() - lancamento.getValorPago());
				break;
			case Calendar.AUGUST :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPrevisaoLancamentos.get(oid).setAgosto(mapPrevisaoLancamentos.get(oid).getAgosto() + lancamento.getValorPago());
				else
					mapPrevisaoLancamentos.get(oid).setAgosto(mapPrevisaoLancamentos.get(oid).getAgosto() - lancamento.getValorPago());
				break;
			case Calendar.SEPTEMBER :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPrevisaoLancamentos.get(oid).setSetembro(mapPrevisaoLancamentos.get(oid).getSetembro() + lancamento.getValorPago());
				else
					mapPrevisaoLancamentos.get(oid).setSetembro(mapPrevisaoLancamentos.get(oid).getSetembro() - lancamento.getValorPago());
				break;
			case Calendar.OCTOBER :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPrevisaoLancamentos.get(oid).setOutubro(mapPrevisaoLancamentos.get(oid).getOutubro() + lancamento.getValorPago());
				else
					mapPrevisaoLancamentos.get(oid).setOutubro(mapPrevisaoLancamentos.get(oid).getOutubro() - lancamento.getValorPago());
				break;
			case Calendar.NOVEMBER :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPrevisaoLancamentos.get(oid).setNovembro(mapPrevisaoLancamentos.get(oid).getNovembro() + lancamento.getValorPago());
				else
					mapPrevisaoLancamentos.get(oid).setNovembro(mapPrevisaoLancamentos.get(oid).getNovembro() - lancamento.getValorPago());
				break;
			case Calendar.DECEMBER :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPrevisaoLancamentos.get(oid).setDezembro(mapPrevisaoLancamentos.get(oid).getDezembro() + lancamento.getValorPago());
				else
					mapPrevisaoLancamentos.get(oid).setDezembro(mapPrevisaoLancamentos.get(oid).getDezembro() - lancamento.getValorPago());
				break;
		}
	}
	
	@SuppressWarnings("deprecation")
	private void inserirValorMesPanoramaLancamentoCartao(Map<String, PanoramaLancamentoCartao> mapPanoramaLancamentos, LancamentoConta lancamento, String oid) {
		int mes = lancamento.getFaturaCartao().getDataVencimento().getMonth();
		switch(mes) {
			case Calendar.JANUARY :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPanoramaLancamentos.get(oid).setJaneiro(mapPanoramaLancamentos.get(oid).getJaneiro() + lancamento.getValorPago());
				else
					mapPanoramaLancamentos.get(oid).setJaneiro(mapPanoramaLancamentos.get(oid).getJaneiro() - lancamento.getValorPago());
				break;
			case Calendar.FEBRUARY :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPanoramaLancamentos.get(oid).setFevereiro(mapPanoramaLancamentos.get(oid).getFevereiro() + lancamento.getValorPago());
				else
					mapPanoramaLancamentos.get(oid).setFevereiro(mapPanoramaLancamentos.get(oid).getFevereiro() - lancamento.getValorPago());
				break;
			case Calendar.MARCH :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPanoramaLancamentos.get(oid).setMarco(mapPanoramaLancamentos.get(oid).getMarco() + lancamento.getValorPago());
				else
					mapPanoramaLancamentos.get(oid).setMarco(mapPanoramaLancamentos.get(oid).getMarco() - lancamento.getValorPago());
				break;
			case Calendar.APRIL :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPanoramaLancamentos.get(oid).setAbril(mapPanoramaLancamentos.get(oid).getAbril() + lancamento.getValorPago());
				else
					mapPanoramaLancamentos.get(oid).setAbril(mapPanoramaLancamentos.get(oid).getAbril() - lancamento.getValorPago());
				break;
			case Calendar.MAY :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPanoramaLancamentos.get(oid).setMaio(mapPanoramaLancamentos.get(oid).getMaio() + lancamento.getValorPago());
				else
					mapPanoramaLancamentos.get(oid).setMaio(mapPanoramaLancamentos.get(oid).getMaio() - lancamento.getValorPago());
				break;
			case Calendar.JUNE :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPanoramaLancamentos.get(oid).setJunho(mapPanoramaLancamentos.get(oid).getJunho() + lancamento.getValorPago());
				else
					mapPanoramaLancamentos.get(oid).setJunho(mapPanoramaLancamentos.get(oid).getJunho() - lancamento.getValorPago());
				break;
			case Calendar.JULY :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPanoramaLancamentos.get(oid).setJulho(mapPanoramaLancamentos.get(oid).getJulho() + lancamento.getValorPago());
				else
					mapPanoramaLancamentos.get(oid).setJulho(mapPanoramaLancamentos.get(oid).getJulho() - lancamento.getValorPago());
				break;
			case Calendar.AUGUST :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPanoramaLancamentos.get(oid).setAgosto(mapPanoramaLancamentos.get(oid).getAgosto() + lancamento.getValorPago());
				else
					mapPanoramaLancamentos.get(oid).setAgosto(mapPanoramaLancamentos.get(oid).getAgosto() - lancamento.getValorPago());
				break;
			case Calendar.SEPTEMBER :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPanoramaLancamentos.get(oid).setSetembro(mapPanoramaLancamentos.get(oid).getSetembro() + lancamento.getValorPago());
				else
					mapPanoramaLancamentos.get(oid).setSetembro(mapPanoramaLancamentos.get(oid).getSetembro() - lancamento.getValorPago());
				break;
			case Calendar.OCTOBER :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPanoramaLancamentos.get(oid).setOutubro(mapPanoramaLancamentos.get(oid).getOutubro() + lancamento.getValorPago());
				else
					mapPanoramaLancamentos.get(oid).setOutubro(mapPanoramaLancamentos.get(oid).getOutubro() - lancamento.getValorPago());
				break;
			case Calendar.NOVEMBER :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPanoramaLancamentos.get(oid).setNovembro(mapPanoramaLancamentos.get(oid).getNovembro() + lancamento.getValorPago());
				else
					mapPanoramaLancamentos.get(oid).setNovembro(mapPanoramaLancamentos.get(oid).getNovembro() - lancamento.getValorPago());
				break;
			case Calendar.DECEMBER :
				if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA))
					mapPanoramaLancamentos.get(oid).setDezembro(mapPanoramaLancamentos.get(oid).getDezembro() + lancamento.getValorPago());
				else
					mapPanoramaLancamentos.get(oid).setDezembro(mapPanoramaLancamentos.get(oid).getDezembro() - lancamento.getValorPago());
				break;
		}
	}
}