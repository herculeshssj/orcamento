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
import br.com.hslife.orcamento.enumeration.TipoAgrupamentoBusca;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IResumoEstatistica;
import br.com.hslife.orcamento.model.CriterioLancamentoConta;
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
	
	public List<PrevisaoLancamentoConta> visualizarRelatorioPrevisaoLancamentoConta(Conta conta, int ano, TipoAgrupamentoBusca agrupamento) throws BusinessException {
		return previsaoLancamentoContaRepository.findByContaAnoAndAgrupamento(conta, ano, agrupamento);
	}
	
	@Override
	public void gerarRelatorioPrevisaoLancamentoConta(CriterioLancamentoConta criterioBusca, int ano, TipoAgrupamentoBusca agruparPor) throws BusinessException {
		// Exclui o relatório existente
		previsaoLancamentoContaRepository.deletePrevisaoLancamentoConta(criterioBusca.getConta(), ano, agruparPor);
		
		// Declara o Map de previsão de lançamentos da conta
		Map<String, PrevisaoLancamentoConta> mapPrevisaoLancamentos = new HashMap<String, PrevisaoLancamentoConta>();
		
		// Busca os lançamentos a partir do critério de busca fornecido
		// Logo após itera os lançamentos
		for (LancamentoConta lancamento : lancamentoContaRepository.findByCriterioLancamentoConta(criterioBusca)) {
			String oid;
			switch (agruparPor) {
				case FAVORECIDO :   
					if (lancamento.getFavorecido() == null)
						oid = Util.MD5("Sem favorecido");
					else
						oid = Util.MD5(lancamento.getFavorecido().getNome());
					break;
				case MEIOPAGAMENTO :
					if (lancamento.getMeioPagamento() == null)
						oid = Util.MD5("Sem meio de pagamento");
					else
						oid = Util.MD5(lancamento.getMeioPagamento().getDescricao());
					break;
				default :
					if (lancamento.getCategoria() == null) {
						oid = Util.MD5("Sem categoria");
					} else {
						oid = Util.MD5(lancamento.getCategoria().getDescricao());
					}
			}						
			if (mapPrevisaoLancamentos.containsKey(oid)) {
				this.inserirValorMesPrevisaoLancamentoConta(mapPrevisaoLancamentos, lancamento, oid);
			} else {
				PrevisaoLancamentoConta previsao = new PrevisaoLancamentoConta();
				previsao.setConta(lancamento.getConta());
				previsao.setAno(ano);
				previsao.setOid(oid);
				previsao.setAgrupamento(agruparPor);
				switch (agruparPor) {
				case FAVORECIDO : 
					if (lancamento.getFavorecido() == null)
						previsao.setDescricaoPrevisao("Sem favorecido");
					else
						previsao.setDescricaoPrevisao(lancamento.getFavorecido().getNome());
					break;
				case MEIOPAGAMENTO :
					if (lancamento.getMeioPagamento() == null)
						previsao.setDescricaoPrevisao("Sem meio de pagamento");
					else
						previsao.setDescricaoPrevisao(lancamento.getMeioPagamento().getDescricao());
					break;
				default :
					if (lancamento.getCategoria() == null) {
						previsao.setDescricaoPrevisao("Sem categoria");
					} else {
						previsao.setDescricaoPrevisao(lancamento.getCategoria().getDescricao());
					}
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
		saldoTotal.setAgrupamento(agruparPor);
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