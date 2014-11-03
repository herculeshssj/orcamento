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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.component.ContaComponent;
import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.IncrementoClonagemLancamento;
import br.com.hslife.orcamento.enumeration.LancamentoAgendado;
import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.enumeration.PeriodoLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IResumoEstatistica;
import br.com.hslife.orcamento.model.CriterioLancamentoConta;
import br.com.hslife.orcamento.model.PanoramaLancamentoConta;
import br.com.hslife.orcamento.model.ResumoMensalContas;
import br.com.hslife.orcamento.model.SaldoAtualConta;
import br.com.hslife.orcamento.repository.ContaRepository;
import br.com.hslife.orcamento.repository.FechamentoPeriodoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoPeriodicoRepository;
import br.com.hslife.orcamento.util.Util;

@Service("resumoEstatisticaService")
public class ResumoEstatisticaService implements IResumoEstatistica {

	/*** Declaração dos repositórios ***/
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private FechamentoPeriodoRepository fechamentoPeriodoRepository;
	
	@Autowired
	private LancamentoPeriodicoRepository lancamentoPeriodicoRepository;

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
	
	@SuppressWarnings("deprecation")
	@Override
	public List<PanoramaLancamentoConta> gerarRelatorioPanoramaLancamentoConta(CriterioLancamentoConta criterioBusca, int ano, int mesAEstimar, int anoAEstimar) throws BusinessException {
		
		// Pega a data atual
		Calendar hoje = Calendar.getInstance();
		
		// Declara o Map de previsão de lançamentos da conta
		Map<String, PanoramaLancamentoConta> mapPanoramaLancamentos = new LinkedHashMap<String, PanoramaLancamentoConta>();
		
		// Insere no Map o panorama para o cálculo do saldo anterior
		PanoramaLancamentoConta saldoAnterior = new PanoramaLancamentoConta();
		saldoAnterior.setConta(criterioBusca.getConta());
		saldoAnterior.setAno(ano);
		saldoAnterior.setOid(Util.MD5("Saldo Anterior"));
		saldoAnterior.setDescricao("Saldo Anterior");
		saldoAnterior.setIndice(mapPanoramaLancamentos.values().size());
		
		mapPanoramaLancamentos.put(saldoAnterior.getOid(), saldoAnterior);
		
		// Criação das listas que serão usadas
		List<LancamentoConta> avulsos = new ArrayList<LancamentoConta>();
		List<LancamentoConta> parcelas = new ArrayList<LancamentoConta>();
		List<LancamentoConta> lancamentosProcessados = new ArrayList<LancamentoConta>();
		
		if (ano <= hoje.get(Calendar.YEAR)) {
			// Ano atual e anteriores é trazido o que está atualmente registrado na conta
			lancamentosProcessados = lancamentoContaRepository.findByCriterioLancamentoConta(criterioBusca);
		} else {
			// Anos posteriores é realizado a estimativa baseado no mês e ano informado e os lançamentos periódicos ativos da conta
			
			/*** Lançamentos parcelados ***/
			// Traz todos os lançamentos parcelados ativos da conta selecionada
			List<LancamentoPeriodico> parcelamentos = lancamentoPeriodicoRepository.
					findByTipoLancamentoContaAndStatusLancamento(TipoLancamentoPeriodico.PARCELADO, criterioBusca.getConta(), StatusLancamento.ATIVO);
			
			// Itera os lançamentos parcelados a adiciona suas parcelas caso esteja no mesmo ano que o relatório
			for (LancamentoPeriodico parcelamento : parcelamentos) {
				for (LancamentoConta parcela : parcelamento.getPagamentos()) {
					int anoParcela = parcela.getDataPagamento().getYear() + 1900;
					if (anoParcela == ano) {
						parcelas.add(parcela);
					}
				}
			}
			
			// Adiciona as parcelas nos lançamentos processados
			lancamentosProcessados.addAll(parcelas);
			
			/*** Lançamentos fixos ***/
			// Traz todos os lançamentos fixos ativos da conta selecionada
			List<LancamentoPeriodico> despesasFixas = lancamentoPeriodicoRepository.
					findByTipoLancamentoContaAndStatusLancamento(TipoLancamentoPeriodico.FIXO, criterioBusca.getConta(), StatusLancamento.ATIVO);
			
			// Itera os lançamentos fixos
			for (LancamentoPeriodico despesaFixa : despesasFixas) {
				
				// Busca a última mensalidade paga
				LancamentoConta ultimaMensalidade = lancamentoContaRepository.findLastGeneratedPagamentoPeriodo(despesaFixa);
				
				// Verifica se a despesa fixa é mensal
				if (despesaFixa.getPeriodoLancamento().equals(PeriodoLancamento.MENSAL)) {
					// Seta o mês da data de pagamento da mensalidade para Janeiro e duplica os lançamentos
					Calendar temp = Calendar.getInstance();
					temp.setTime(ultimaMensalidade.getDataPagamento());
					temp.set(Calendar.DAY_OF_MONTH, despesaFixa.getDiaVencimento());
					temp.set(Calendar.MONTH, Calendar.JANUARY);
					temp.set(Calendar.YEAR, ano);
					ultimaMensalidade.setDataPagamento(temp.getTime());
					
					// Seta o valor definido na despesa fixa
					ultimaMensalidade.setValorPago(despesaFixa.getValorParcela());
					
					lancamentosProcessados.add(ultimaMensalidade);
					lancamentosProcessados.addAll(ultimaMensalidade.clonarLancamentos(11, IncrementoClonagemLancamento.MES));
				} else {
					// TODO Os valores inseridos estão vindo com o dobro do registrado na despesa fixa. Ver depois o que está acontecendo.
					
					// Gera mais 12 mensalidades e inclui na lista de acordo com o período da despesa fixa
					switch (despesaFixa.getPeriodoLancamento()) {
						case BIMESTRAL : 
							for (LancamentoConta l : ultimaMensalidade.clonarLancamentos(12, IncrementoClonagemLancamento.BIMESTRE)) {
								if ((l.getDataPagamento().getYear() + 1900) == ano)  
									lancamentosProcessados.add(l);
							}							
							break;
						case TRIMESTRAL : 
							for (LancamentoConta l : ultimaMensalidade.clonarLancamentos(12, IncrementoClonagemLancamento.TRIMESTRE)) {
								if ((l.getDataPagamento().getYear() + 1900) == ano)  
									lancamentosProcessados.add(l);
							}							
							break;
						case QUADRIMESTRAL : 
							for (LancamentoConta l : ultimaMensalidade.clonarLancamentos(12, IncrementoClonagemLancamento.QUADRIMESTRE)) {
								if ((l.getDataPagamento().getYear() + 1900) == ano)  
									lancamentosProcessados.add(l);
							}							
							break;
						case SEMESTRAL : 
							for (LancamentoConta l : ultimaMensalidade.clonarLancamentos(12, IncrementoClonagemLancamento.SEMESTRE)) {
								if ((l.getDataPagamento().getYear() + 1900) == ano)  
									lancamentosProcessados.add(l);
							}							
							break;
						case ANUAL : 
							for (LancamentoConta l : ultimaMensalidade.clonarLancamentos(12, IncrementoClonagemLancamento.ANO)) {
								if ((l.getDataPagamento().getYear() + 1900) == ano)  
									lancamentosProcessados.add(l);
							}							
							break;
						default : // não faz nada
					}
					
				}
				
			}
			
			
			/*** Lançamentos avulsos ***/
			// Traz os lançamentos avulsos existente no ano do relatório
			avulsos = lancamentoContaRepository.findByCriterioLancamentoConta(criterioBusca);
			
			// Itera os lançamentos avulsos para remover as mensalidades e parcelas
			for (Iterator<LancamentoConta> iterator = avulsos.iterator(); iterator.hasNext(); ) {
				if (iterator.next().getLancamentoPeriodico() != null) {
					iterator.remove();
				}
			}
			
			// Inclui os lançamentos avulsos
			lancamentosProcessados.addAll(avulsos);
			
			if (mesAEstimar != 99 && anoAEstimar != ano) { // Diz que é pra gerar a previsão usando os dados passados do mês e ano
			
				// Traz os lançamentos avulsos do mês e ano informados
				criterioBusca.setDataInicio(Util.primeiroDiaMes(mesAEstimar, anoAEstimar));
				criterioBusca.setDataFim(Util.ultimoDiaMes(mesAEstimar, anoAEstimar));
				
				avulsos = lancamentoContaRepository.findByCriterioLancamentoConta(criterioBusca);
				
				// Itera os lançamentos avulsos para remover as mensalidades e parcelas
				for (Iterator<LancamentoConta> iterator = avulsos.iterator(); iterator.hasNext(); ) {
					if (iterator.next().getLancamentoPeriodico() != null) {
						iterator.remove();
					}
				}
				
				// Seta o mês de pagamento para Janeiro e cria 12 novas instâncias do lançamento
				for (LancamentoConta lancamento : avulsos) {
					Calendar temp = Calendar.getInstance();
					temp.setTime(lancamento.getDataPagamento());
					temp.set(Calendar.MONTH, Calendar.JANUARY);
					lancamento.setDataPagamento(temp.getTime());
					
					lancamentosProcessados.add(lancamento);
					lancamentosProcessados.addAll(lancamento.clonarLancamentos(11, IncrementoClonagemLancamento.MES));
				}
			}
			
		}
		
		// Busca os lançamentos e classifica-os em suas respectivas categorias
		List<Categoria> categorias = contaComponent.organizarLancamentosPorCategoria(lancamentosProcessados);
		
		for (Categoria categoria : categorias) {
			String oid = Util.MD5(categoria.getDescricao());
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
		
		// Salva o resultado em um List e depois retorna os valores adicionados
		List<PanoramaLancamentoConta> resultado = new LinkedList<>(mapPanoramaLancamentos.values());

		return resultado;
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
	
	
}