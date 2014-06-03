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
import br.com.hslife.orcamento.entity.PanoramaLancamentoConta;
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
import br.com.hslife.orcamento.repository.PanoramaLancamentoContaRepository;
import br.com.hslife.orcamento.util.Util;

@Service("resumoEstatisticaService")
public class ResumoEstatisticaService implements IResumoEstatistica {

	/*** Declaração dos repositórios ***/
	
	@Autowired
	private PanoramaLancamentoContaRepository panoramaLancamentoContaRepository;
	
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

	public void setPanoramaLancamentoContaRepository(
			PanoramaLancamentoContaRepository panoramaLancamentoContaRepository) {
		this.panoramaLancamentoContaRepository = panoramaLancamentoContaRepository;
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
		for (Conta conta : contaRepository.findByUsuario(usuario)) {
			
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
	
	public List<PanoramaLancamentoConta> visualizarRelatorioPanoramaLancamentoConta(Conta conta, int ano) throws BusinessException {
		return panoramaLancamentoContaRepository.findByContaAnoAndAgrupamento(conta, ano);
	}
	
	@Override
	public void gerarRelatorioPanoramaLancamentoConta(CriterioLancamentoConta criterioBusca, int ano) throws BusinessException {
		// Exclui o relatório existente
		panoramaLancamentoContaRepository.deletePanoramaLancamentoConta(criterioBusca.getConta(), ano);
		
		// Declara o Map de previsão de lançamentos da conta
		Map<String, PanoramaLancamentoConta> mapPanoramaLancamentos = new HashMap<String, PanoramaLancamentoConta>();
		
		PanoramaLancamentoConta saldoAnterior = new PanoramaLancamentoConta();
		saldoAnterior.setConta(criterioBusca.getConta());
		saldoAnterior.setAno(ano);
		saldoAnterior.setOid(Util.MD5("Saldo Anterior"));
		saldoAnterior.setDescricao("Saldo Anterior");
		saldoAnterior.setIndice(mapPanoramaLancamentos.values().size());
		
		mapPanoramaLancamentos.put(saldoAnterior.getOid(), saldoAnterior);
		
		// Busca os lançamentos a partir do critério de busca fornecido
		// Logo após itera os lançamentos
		for (LancamentoConta lancamento : lancamentoContaRepository.findByCriterioLancamentoConta(criterioBusca)) {
			String oid;
			if (lancamento.getCategoria() == null) {
				oid = Util.MD5("Sem categoria");
			} else {
				oid = Util.MD5(lancamento.getCategoria().getDescricao());
			}									
			if (mapPanoramaLancamentos.containsKey(oid)) {
				this.inserirValorMesPanoramaLancamentoConta(mapPanoramaLancamentos, lancamento, oid);
			} else {
				PanoramaLancamentoConta panorama = new PanoramaLancamentoConta();
				panorama.setConta(lancamento.getConta());
				panorama.setAno(ano);
				panorama.setOid(oid);
				if (lancamento.getCategoria() == null) {
					panorama.setDescricao("Sem categoria");
				} else {
					panorama.setDescricao(lancamento.getCategoria().getDescricao());
				}				
				panorama.setIndice(mapPanoramaLancamentos.values().size() + 1);
				mapPanoramaLancamentos.put(oid, panorama);
				this.inserirValorMesPanoramaLancamentoConta(mapPanoramaLancamentos, lancamento, oid);
			}
		}
		
		// Calcular o saldo total
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
		
		// Pegar o valor do último fechamento do ano anterior		
		FechamentoPeriodo fechamento = fechamentoPeriodoRepository.findLastFechamentoPeriodoBeforeDateByContaAndOperacao(criterioBusca.getConta(), Util.ultimoDiaAno(ano - 1), OperacaoConta.FECHAMENTO);
		if (fechamento == null) {
			mapPanoramaLancamentos.get(saldoAnterior.getOid()).setJaneiro(criterioBusca.getConta().getSaldoInicial());
		} else {
			mapPanoramaLancamentos.get(saldoAnterior.getOid()).setJaneiro(fechamento.getSaldo());
		}	
		mapPanoramaLancamentos.get(saldoTotal.getOid()).setJaneiro(mapPanoramaLancamentos.get(saldoTotal.getOid()).getJaneiro() + mapPanoramaLancamentos.get(saldoAnterior.getOid()).getJaneiro());
		
		// Preenche o saldo anterior		
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
	private void inserirValorMesPanoramaLancamentoConta(Map<String, PanoramaLancamentoConta> mapPrevisaoLancamentos, LancamentoConta lancamento, String oid) {
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
}