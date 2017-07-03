/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor
	
	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

    para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 - 
	
	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/

package br.com.hslife.orcamento.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.ContaCompartilhada;
import br.com.hslife.orcamento.entity.ConversaoMoeda;
import br.com.hslife.orcamento.entity.FaturaCartao;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.CadastroSistema;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoCartao;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IContaCompartilhada;
import br.com.hslife.orcamento.facade.IFaturaCartao;
import br.com.hslife.orcamento.facade.IFechamentoPeriodo;
import br.com.hslife.orcamento.facade.IInvestimento;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.ILancamentoPeriodico;
import br.com.hslife.orcamento.facade.IResumoEstatistica;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.model.LancamentoPanoramaCadastro;
import br.com.hslife.orcamento.model.PanoramaCadastro;
import br.com.hslife.orcamento.model.PanoramaLancamentoCartao;
import br.com.hslife.orcamento.model.PanoramaLancamentoConta;
import br.com.hslife.orcamento.model.ResumoMensalContas;
import br.com.hslife.orcamento.model.SaldoAtualConta;
import br.com.hslife.orcamento.util.LancamentoContaUtil;
import br.com.hslife.orcamento.util.Util;

@Service("resumoEstatisticaService")
@Transactional(propagation=Propagation.SUPPORTS)
public class ResumoEstatisticaService implements IResumoEstatistica {
	
	/*** Declaração dos serviços ***/
	
	@Autowired
	private IFechamentoPeriodo fechamentoPeriodoService;
	
	@Autowired
	private ILancamentoConta lancamentoContaService;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private ILancamentoPeriodico lancamentoPeriodicoService;
	
	@Autowired
	private IFaturaCartao faturaCartaoService;
	
	@Autowired
	private IContaCompartilhada contaCompartilhadaService;
	
	@Autowired
	private IInvestimento investimentoService;
	
	/*** Declaração dos Getters dos serviços ***/
	
	public IFechamentoPeriodo getFechamentoPeriodoService() {
		return fechamentoPeriodoService;
	}
	
	public ILancamentoConta getLancamentoContaService() {
		return lancamentoContaService;
	}
	
	public IConta getContaService() {
		return contaService;
	}
	
	public ILancamentoPeriodico getLancamentoPeriodicoService() {
		return lancamentoPeriodicoService;
	}

	public IFaturaCartao getFaturaCartaoService() {
		return faturaCartaoService;
	}

	public IContaCompartilhada getContaCompartilhadaService() {
		return contaCompartilhadaService;
	}

	public IInvestimento getInvestimentoService() {
		return investimentoService;
	}

	/*** Declaração dos componentes ***/
	
	@Autowired
	private UsuarioComponent usuarioComponent;
	
	/*** Declaração dos Getters dos componentes ***/
	
	public UsuarioComponent getUsuarioComponent() {
		return usuarioComponent;
	}
	
	/*** Implementação dos métodos da interface ***/
	
	@Override
	public List<SaldoAtualConta> gerarSaldoAtualContas(boolean agendado, Usuario usuario) {
		// Declaração dos objetos
		List<SaldoAtualConta> saldoAtualContas = new ArrayList<>();
		SaldoAtualConta saldoAtual = new SaldoAtualConta();
		
		// Resgata todas as contas do usuário
		Set<Conta> contas = new HashSet<>(getContaService().buscarDescricaoOuTipoContaOuAtivoPorUsuario(null, new TipoConta[]{}, usuario, null));
		
		// Traz as contas compartilhadas para com o usuário atualmente logado
		List<ContaCompartilhada> contasCompartilhadas = getContaCompartilhadaService().buscarTodosPorUsuario(usuario);
		
		// Acrescenta no Set as contas compartilhadas dos demais usuários
		for (ContaCompartilhada contaCompartilhada : contasCompartilhadas) {
			contas.add(contaCompartilhada.getConta());
		}
		
		// Itera todas as contas do usuário
		for (Conta conta : contas) {
			
			// Define a descrição da conta
			saldoAtual.setDescricaoConta(conta.getDescricao());
			saldoAtual.setMoedaConta(conta.getMoeda());
			saldoAtual.setTipoConta(conta.getTipoConta());
			
			// Define a situação da conta
			saldoAtual.setAtivo(conta.isAtivo());
			
			// Declara o critério de busca
			CriterioBuscaLancamentoConta criterio = new CriterioBuscaLancamentoConta();
			
			// Verifica se a conta é do tipo CARTAO
			if (conta.getTipoConta().equals(TipoConta.CARTAO)) {
				// Seta o saldo do período com o limite do cartão
				saldoAtual.setSaldoPeriodo(conta.getCartaoCredito().getLimiteCartao());
				
				// Traz todos os lançamentos do cartão que ainda não foram quitados
				criterio.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.REGISTRADO, StatusLancamentoConta.AGENDADO});
				
				// Seta a conta
				criterio.setConta(conta);
			} else {
				// Traz o saldo do período anterior
				double saldoPeriodoAnterior = 0.0;
				if (agendado) {
					saldoPeriodoAnterior = getLancamentoContaService()
							.buscarSaldoPeriodoByContaAndPeriodoAndStatusLancamento(conta, null, Util.ultimoDiaMesAnterior(), 
									new StatusLancamentoConta[]{StatusLancamentoConta.AGENDADO, StatusLancamentoConta.REGISTRADO, StatusLancamentoConta.QUITADO});
				} else {
					saldoPeriodoAnterior = getLancamentoContaService()
							.buscarSaldoPeriodoByContaAndPeriodoAndStatusLancamento(conta, null, Util.ultimoDiaMesAnterior(), 
									new StatusLancamentoConta[]{StatusLancamentoConta.QUITADO, StatusLancamentoConta.REGISTRADO});
				}
				
				saldoAtual.setSaldoPeriodo(saldoPeriodoAnterior);
			}
			
			// Se a opção de agendado estiver marcada, traz os lançamentos agendados
			if (agendado)
				criterio.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.AGENDADO, StatusLancamentoConta.REGISTRADO, StatusLancamentoConta.QUITADO});
			else 
				criterio.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.REGISTRADO, StatusLancamentoConta.QUITADO});
			
			criterio.setConta(conta);
			
			// Traz os lançamentos da data de fechamento em diante
			Calendar temp = Calendar.getInstance();
			temp.setTime(Util.ultimoDiaMesAnterior());
			temp.add(Calendar.DAY_OF_YEAR, 1);
			criterio.setDataInicio(temp.getTime());
			
			List<LancamentoConta> lancamentos = getLancamentoContaService().buscarPorCriterioBusca(criterio);
			
			// Calcula o saldo dos lançamentos e registra no saldo atual
			saldoAtual.setSaldoRegistrado(LancamentoContaUtil.calcularSaldoLancamentosComConversao(lancamentos));
			
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
	public List<PanoramaLancamentoCartao> gerarRelatorioPanoramaLancamentoCartaoDebito(CriterioBuscaLancamentoConta criterioBusca, int ano) {
		// TODO refatorar para trabalhar com categoria, e não favorecido
		// Declara o Map de previsão de lançamentos da conta
		Map<String, PanoramaLancamentoCartao> mapPanoramaLancamentos = new LinkedHashMap<String, PanoramaLancamentoCartao>();
		
		// Traz os lançamentos registrados no cartão de débito, independente de ser avulso, mensalidade ou parcela
		List<LancamentoConta> lancamentosProcessados = new ArrayList<LancamentoConta>();
		criterioBusca.setDataInicio(Util.primeiroDiaAno(ano));
		criterioBusca.setDataFim(Util.ultimoDiaAno(ano));
		lancamentosProcessados = getLancamentoContaService().buscarPorCriterioBusca(criterioBusca);
		
		// Busca os lançamentos e classifica-os em suas respectivas categorias
		List<Favorecido> favorecidos = LancamentoContaUtil.organizarLancamentosPorFavorecido(lancamentosProcessados);
		
		for (Favorecido favorecido : favorecidos) {
			String oid = Util.MD5(favorecido.getNome());
			PanoramaLancamentoCartao panorama = new PanoramaLancamentoCartao();
			panorama.setConta(criterioBusca.getConta());
			panorama.setDescricao(favorecido.getNome());
			panorama.setAno(ano);
			panorama.setOid(oid);								
			panorama.setIndice(mapPanoramaLancamentos.values().size() + 1);
			mapPanoramaLancamentos.put(oid, panorama);
				
			// Rotina de inserção dos valores dos lançamentos no panorama
			for (LancamentoConta lancamento : favorecido.getLancamentos()) {
				mapPanoramaLancamentos.get(oid).setarMes(lancamento.getDataPagamento().getMonth(), lancamento);
			}
		}		
		
		// Realiza o cálculo do saldo total
		PanoramaLancamentoCartao saldoTotal = new PanoramaLancamentoCartao();
		saldoTotal.setConta(criterioBusca.getConta());
		saldoTotal.setAno(ano);
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
		
		// Salva o resultado em um List e depois retorna os valores adicionados
		List<PanoramaLancamentoCartao> resultado = new LinkedList<>(mapPanoramaLancamentos.values());

		return resultado;
	}
	
	@Override
	public List<PanoramaLancamentoCartao> gerarRelatorioPanoramaLancamentoCartao(CriterioBuscaLancamentoConta criterioBusca, int ano) {
		
		// Caso o cartão seja de débito, desvia para o método correspondente
		if (criterioBusca.getConta().getCartaoCredito().getTipoCartao().equals(TipoCartao.DEBITO)) {
			return this.gerarRelatorioPanoramaLancamentoCartaoDebito(criterioBusca, ano);
		}
		
		// Declara o Map de previsão de lançamentos do cartao
		Map<String, PanoramaLancamentoCartao> mapPanoramaLancamentos = new LinkedHashMap<String, PanoramaLancamentoCartao>();
		
		// Pega a data atual
		Calendar hoje = Calendar.getInstance();
		
		// Declaração do List de faturas
		List<FaturaCartao> faturasCartao = new ArrayList<>();
		
		if (ano <= hoje.get(Calendar.YEAR)) {
			// Traz todas as faturas do ano selecionado
			faturasCartao = getFaturaCartaoService().buscarTodosPorContaEAnoOrdenadosPorMesAno(criterioBusca.getConta(), ano);
		} else {
			/** Eliminado a previsão de faturas de anos posteriores **/
			/**
			// Verifica se realmente não existem faturas para o ano seleciona. Se existirem, segue com o fluxo normal do método
			faturasCartao = getFaturaCartaoService().buscarTodosPorContaEAnoOrdenadosPorMesAno(criterioBusca.getConta(), ano);
			
			if (faturasCartao == null || faturasCartao.isEmpty()) {
				// Anos posteriores é realizado a estimativa baseado no ano informado e os lançamentos periódicos ativos da conta
				// Todos os lançamentos gerados são incluídos em faturas correspondentes aos meses do ano informado 
				
				/*** Parcelamentos ***
				// Busca todos os parcelamentos ativos da conta selecionada
				List<LancamentoPeriodico> parcelamentos = getLancamentoPeriodicoService().
						buscarPorTipoLancamentoContaEStatusLancamento(TipoLancamentoPeriodico.PARCELADO, criterioBusca.getConta(), StatusLancamento.ATIVO);
				
				// Itera todos os parcelamentos para trazer todas as parcelas existentes
				List<LancamentoConta> todasParcelas = new ArrayList<>();
				for (LancamentoPeriodico parcelamento : parcelamentos) {
					List<LancamentoConta> parcelasLancamento = getLancamentoContaService().buscarPorLancamentoPeriodico(parcelamento);
					
					for (LancamentoConta parcela : parcelasLancamento) {
						int anoParcela = parcela.getDataPagamento().getYear() + 1900;
						if (anoParcela == ano) {
							todasParcelas.add(parcela);
						}
					}				
				}
				
				/*** Despesas fixas ***
				// Traz todos os lançamentos fixos ativos da conta selecionada
				List<LancamentoPeriodico> despesasFixas = getLancamentoPeriodicoService().
						buscarPorTipoLancamentoContaEStatusLancamento(TipoLancamentoPeriodico.FIXO, criterioBusca.getConta(), StatusLancamento.ATIVO);
				
				// Itera os lançamentos fixos
				List<LancamentoConta> todasMensalidades = new ArrayList<>();
				for (LancamentoPeriodico despesaFixa : despesasFixas) {
					
					// Busca a última mensalidade paga
					LancamentoConta ultimaMensalidade = getLancamentoContaService().buscarUltimoPagamentoPeriodoGerado(despesaFixa);
					
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
						
						todasMensalidades.add(ultimaMensalidade);
						todasMensalidades.addAll(ultimaMensalidade.clonarLancamentos(11, IncrementoClonagemLancamento.MES));
					} else {
						
						// Gera mais 12 mensalidades e inclui na lista de acordo com o período da despesa fixa
						switch (despesaFixa.getPeriodoLancamento()) {
							case BIMESTRAL : 
								for (LancamentoConta l : ultimaMensalidade.clonarLancamentos(12, IncrementoClonagemLancamento.BIMESTRE)) {
									if ((l.getDataPagamento().getYear() + 1900) == ano)  
										todasMensalidades.add(l);
								}							
								break;
							case TRIMESTRAL : 
								for (LancamentoConta l : ultimaMensalidade.clonarLancamentos(12, IncrementoClonagemLancamento.TRIMESTRE)) {
									if ((l.getDataPagamento().getYear() + 1900) == ano)  
										todasMensalidades.add(l);
								}							
								break;
							case QUADRIMESTRAL : 
								for (LancamentoConta l : ultimaMensalidade.clonarLancamentos(12, IncrementoClonagemLancamento.QUADRIMESTRE)) {
									if ((l.getDataPagamento().getYear() + 1900) == ano)  
										todasMensalidades.add(l);
								}							
								break;
							case SEMESTRAL : 
								for (LancamentoConta l : ultimaMensalidade.clonarLancamentos(12, IncrementoClonagemLancamento.SEMESTRE)) {
									if ((l.getDataPagamento().getYear() + 1900) == ano)  
										todasMensalidades.add(l);
								}							
								break;
							case ANUAL : 
								for (LancamentoConta l : ultimaMensalidade.clonarLancamentos(12, IncrementoClonagemLancamento.ANO)) {
									if ((l.getDataPagamento().getYear() + 1900) == ano)  
										todasMensalidades.add(l);
								}							
								break;
							default : // não faz nada
						}
						
					}
					
				}
				
				/*** Lançamentos avulsos ***
				// Traz os lançamentos avulsos existente no ano do relatório
				List<LancamentoConta> todosAvulsos = getLancamentoContaService().buscarPorCriterioBusca(criterioBusca);
				
				// Itera os lançamentos avulsos para remover as mensalidades e parcelas
				for (Iterator<LancamentoConta> iterator = todosAvulsos.iterator(); iterator.hasNext(); ) {
					if (iterator.next().getLancamentoPeriodico() != null) {
						iterator.remove();
					}
				}	
				
				// Cria as doze faturas para o ano selecionado
				for (int i = 1; i <= 12; i++) {
					FaturaCartao fatura = new FaturaCartao(criterioBusca.getConta(), i, ano);
					// Adiciona as mensalidades e parcelas na fatura
					fatura.adicionarLancamentos(todasMensalidades);
					fatura.adicionarLancamentos(todasParcelas);
					
					// Adiciona os lançamentos avulsos
					fatura.adicionarLancamentos(todosAvulsos);
					
					// Adiciona a fatura na lista de faturas
					faturasCartao.add(fatura);
				}
				
			}
			**/
		}
				
		// Traz os lançamentos de cada fatura e classifica-os em suas respectivas categorias
		for (FaturaCartao fatura : faturasCartao) {
			fatura.converterValorTodosLancamentos();
			List<Categoria> categorias = LancamentoContaUtil.organizarLancamentosPorCategoria(new ArrayList<>(fatura.getDetalheFatura()));
			
			for (Categoria categoria : categorias) {
				String oid = Util.MD5(categoria.getDescricao());
				
				if (mapPanoramaLancamentos.get(oid) == null) {				
					PanoramaLancamentoCartao panorama = new PanoramaLancamentoCartao();
					panorama.setConta(criterioBusca.getConta());
					panorama.setDescricao(categoria.getDescricao());
					panorama.setAno(ano);
					panorama.setOid(oid);								
					panorama.setIndice(mapPanoramaLancamentos.values().size() + 1);
					mapPanoramaLancamentos.put(oid, panorama);				
				}
					
				// Rotina de inserção dos valores dos lançamentos no panorama
				for (LancamentoConta lancamento : categoria.getLancamentos()) {
					mapPanoramaLancamentos.get(oid).setarMes(fatura.getMes() - 1, lancamento);
				}
			}
		}
		
		// Linha 'Saldo Total'
		PanoramaLancamentoCartao valorTotal = new PanoramaLancamentoCartao();
		valorTotal.setConta(criterioBusca.getConta());
		valorTotal.setAno(ano);
		valorTotal.setOid(Util.MD5("Saldo Total"));
		valorTotal.setDescricao("Saldo Total");
		valorTotal.setIndice(mapPanoramaLancamentos.values().size() + 1);
		
		// Itera as faturas do cartão para preencher o campo 'Valor da fatura'
		for (FaturaCartao fatura : faturasCartao) {
			switch (fatura.getMes()) {
				case 1 : valorTotal.setJaneiro(Math.abs(LancamentoContaUtil.calcularSaldoLancamentos(new ArrayList<>(fatura.getDetalheFatura())))); continue;
				case 2 : valorTotal.setFevereiro(Math.abs(LancamentoContaUtil.calcularSaldoLancamentos(new ArrayList<>(fatura.getDetalheFatura())))); continue;
				case 3 : valorTotal.setMarco(Math.abs(LancamentoContaUtil.calcularSaldoLancamentos(new ArrayList<>(fatura.getDetalheFatura())))); continue;
				case 4 : valorTotal.setAbril(Math.abs(LancamentoContaUtil.calcularSaldoLancamentos(new ArrayList<>(fatura.getDetalheFatura())))); continue;
				case 5 : valorTotal.setMaio(Math.abs(LancamentoContaUtil.calcularSaldoLancamentos(new ArrayList<>(fatura.getDetalheFatura())))); continue;
				case 6 : valorTotal.setJunho(Math.abs(LancamentoContaUtil.calcularSaldoLancamentos(new ArrayList<>(fatura.getDetalheFatura())))); continue;
				case 7 : valorTotal.setJulho(Math.abs(LancamentoContaUtil.calcularSaldoLancamentos(new ArrayList<>(fatura.getDetalheFatura())))); continue;
				case 8 : valorTotal.setAgosto(Math.abs(LancamentoContaUtil.calcularSaldoLancamentos(new ArrayList<>(fatura.getDetalheFatura())))); continue;
				case 9 : valorTotal.setSetembro(Math.abs(LancamentoContaUtil.calcularSaldoLancamentos(new ArrayList<>(fatura.getDetalheFatura())))); continue;
				case 10 : valorTotal.setOutubro(Math.abs(LancamentoContaUtil.calcularSaldoLancamentos(new ArrayList<>(fatura.getDetalheFatura())))); continue;
				case 11 : valorTotal.setNovembro(Math.abs(LancamentoContaUtil.calcularSaldoLancamentos(new ArrayList<>(fatura.getDetalheFatura())))); continue;
				case 12 : valorTotal.setDezembro(Math.abs(LancamentoContaUtil.calcularSaldoLancamentos(new ArrayList<>(fatura.getDetalheFatura())))); continue;
				default : throw new BusinessException("Opção de mês inválido!");
			}
		}
		
		// Adiciona no Map do panorama de lançamentos
		mapPanoramaLancamentos.put(valorTotal.getOid(), valorTotal);
		
		// Salva o resultado em um List e depois retorna os valores adicionados
		List<PanoramaLancamentoCartao> resultado = new LinkedList<>(mapPanoramaLancamentos.values());

		return resultado; 
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public List<PanoramaLancamentoConta> gerarRelatorioPanoramaLancamentoConta(CriterioBuscaLancamentoConta criterioBusca, int ano) {
		
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
		//List<LancamentoConta> avulsos = new ArrayList<LancamentoConta>();
		//List<LancamentoConta> parcelas = new ArrayList<LancamentoConta>();
		List<LancamentoConta> lancamentosProcessados = new ArrayList<LancamentoConta>();
		
		if (ano <= hoje.get(Calendar.YEAR)) {
			// Ano atual e anteriores é trazido o que está atualmente registrado na conta
			lancamentosProcessados = getLancamentoContaService().buscarPorCriterioBusca(criterioBusca);
		} else {
			/** Eliminado a previsão de lançamentos de anos posteriores **/
			/**
			// Anos posteriores é realizado a estimativa baseado no mês e ano informado e os lançamentos periódicos ativos da conta
			
			/*** Lançamentos parcelados ***
			// Traz todos os lançamentos parcelados ativos da conta selecionada
			List<LancamentoPeriodico> parcelamentos = getLancamentoPeriodicoService().
					buscarPorTipoLancamentoContaEStatusLancamento(TipoLancamentoPeriodico.PARCELADO, criterioBusca.getConta(), StatusLancamento.ATIVO);					
			
			// Itera os lançamentos parcelados adicionando suas parcelas caso estejam no mesmo ano que o relatório
			for (LancamentoPeriodico parcelamento : parcelamentos) {
				List<LancamentoConta> parcelasLancamento = getLancamentoContaService().buscarPorLancamentoPeriodico(parcelamento);
				
				for (LancamentoConta parcela : parcelasLancamento) {
					int anoParcela = parcela.getDataPagamento().getYear() + 1900;
					if (anoParcela == ano) {
						parcelas.add(parcela);
					}
				}
			}
			
			// Adiciona as parcelas nos lançamentos processados
			lancamentosProcessados.addAll(parcelas);
			
			/*** Lançamentos fixos ***
			// Traz todos os lançamentos fixos ativos da conta selecionada
			List<LancamentoPeriodico> despesasFixas = getLancamentoPeriodicoService().
					buscarPorTipoLancamentoContaEStatusLancamento(TipoLancamentoPeriodico.FIXO, criterioBusca.getConta(), StatusLancamento.ATIVO);
			
			// Itera os lançamentos fixos
			for (LancamentoPeriodico despesaFixa : despesasFixas) {
				
				// Busca a última mensalidade paga
				LancamentoConta ultimaMensalidade = getLancamentoContaService().buscarUltimoPagamentoPeriodoGerado(despesaFixa);
				
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
			
			
			/*** Lançamentos avulsos ***
			// Traz os lançamentos avulsos existente no ano do relatório
			avulsos = getLancamentoContaService().buscarPorCriterioBusca(criterioBusca);
			
			// Itera os lançamentos avulsos para remover as mensalidades e parcelas
			for (Iterator<LancamentoConta> iterator = avulsos.iterator(); iterator.hasNext(); ) {
				if (iterator.next().getLancamentoPeriodico() != null) {
					iterator.remove();
				}
			}
			
			// Inclui os lançamentos avulsos
			lancamentosProcessados.addAll(avulsos);
			**/
		}
		
		// Busca os lançamentos e classifica-os em suas respectivas categorias
		List<Categoria> categorias = LancamentoContaUtil.organizarLancamentosPorCategoria(lancamentosProcessados);
		
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
		mapPanoramaLancamentos.get(saldoAnterior.getOid()).setJaneiro(
				getLancamentoContaService().buscarSaldoPeriodoByContaAndPeriodoAndStatusLancamento(criterioBusca.getConta(), null, Util.ultimoDiaAno(ano - 1), null));	
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
	public ResumoMensalContas gerarRelatorioResumoMensalContas(Conta conta, FaturaCartao faturaCartao) {
		ResumoMensalContas resumoMensal = new ResumoMensalContas();
		List<LancamentoConta> lancamentos = new ArrayList<>();
		
		// Seta a conta no resumo
		resumoMensal.setConta(conta);
		
		// Busca a fatura selecionada
		if (faturaCartao == null) {
			// Busca todos os lançamentos registrados e agendados do cartão
			CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
			criterioBusca.setConta(conta);
			criterioBusca.setStatusLancamentoConta(new StatusLancamentoConta[] {StatusLancamentoConta.REGISTRADO, StatusLancamentoConta.AGENDADO});
			
			// Adiciona os lançamentos já convertendo o valor para a moeda padrão
			for (LancamentoConta lancamento : getLancamentoContaService().buscarPorCriterioBusca(criterioBusca)) {
				if (!lancamento.getMoeda().equals(conta.getMoeda())) {
					lancamento.setValorPago(lancamento.getValorPago() * lancamento.getMoeda().getValorConversao());
				}
				lancamentos.add(lancamento);
			}
		} else {
			// Busca a fatura e adiciona os lançamentos contidos nela
			FaturaCartao fatura = getFaturaCartaoService().buscarPorID(faturaCartao.getId());
			
			// Adiciona os lançamentos já convertendo o valor de acordo com a tabela de conversões de moeda
			for (LancamentoConta lancamento : fatura.getDetalheFatura()) {
				if (!lancamento.getMoeda().equals(conta.getMoeda())) {
					for (Iterator<ConversaoMoeda> i = fatura.getConversoesMoeda().iterator(); i.hasNext(); ) {
						ConversaoMoeda conversao = i.next();
						if (conversao.getMoeda().equals(lancamento.getMoeda())) {
							lancamento.setValorPago(lancamento.getValorPago() * conversao.getTaxaConversao());
						}
					}
				}
				lancamentos.add(lancamento);
			}
		}
		
		// Seta as datas de início e fim do resumo
		if (lancamentos != null && lancamentos.size() > 0) {
			resumoMensal.setInicio(lancamentos.get(0).getDataPagamento());
			resumoMensal.setFim(lancamentos.get(lancamentos.size()-1).getDataPagamento());
		}
		
		resumoMensal.setCategoriasCartao(LancamentoContaUtil.organizarLancamentosPorCategoria(lancamentos));
		resumoMensal.setFavorecidos(LancamentoContaUtil.organizarLancamentosPorFavorecido(lancamentos));
		resumoMensal.setMeiosPagamento(LancamentoContaUtil.organizarLancamentosPorMeioPagamento(lancamentos));
		
		return resumoMensal;
	}
	
	public ResumoMensalContas gerarRelatorioResumoMensalContas(Conta conta, Date dataInicio, Date dataFim) {
		ResumoMensalContas resumoMensal = new ResumoMensalContas();
		CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
		
		// Preenche os parâmetros de busca
		criterioBusca.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.REGISTRADO, StatusLancamentoConta.QUITADO});
		criterioBusca.setConta(conta);
		resumoMensal.setConta(conta);
		
		// Determina a data de início do período
		if (dataInicio == null) {
			criterioBusca.setDataInicio(conta.getDataAbertura());
		}
		
		// Determina a data de fim do período
		if (dataFim == null) {
			criterioBusca.setDataFim(new Date());
		}
		
		criterioBusca.setDataInicio(dataInicio);
		criterioBusca.setDataFim(dataFim);
		
		// Realiza a busca
		List<LancamentoConta> lancamentos = getLancamentoContaService().buscarPorCriterioBusca(criterioBusca);
		
		// Determina a data de término do período anterior
		Calendar dataPeriodoAnterior = Calendar.getInstance();
		dataPeriodoAnterior.setTime(dataInicio);
		dataPeriodoAnterior.add(Calendar.DAY_OF_YEAR, -1);
		
		// Calcula o saldo do periodo anterior
		double saldoAnterior = getLancamentoContaService()
				.buscarSaldoPeriodoByContaAndPeriodoAndStatusLancamento(conta, null, dataPeriodoAnterior.getTime(), 
						new StatusLancamentoConta[]{StatusLancamentoConta.REGISTRADO, StatusLancamentoConta.QUITADO});
		
		// Calcula o saldo atual
		double saldoAtual = saldoAnterior + LancamentoContaUtil.calcularSaldoLancamentos(lancamentos);
		
		// Processa as categorias, favorecidos e meios de pagamento
		resumoMensal.setCategorias(LancamentoContaUtil.organizarLancamentosPorCategoria(lancamentos), saldoAnterior, saldoAtual);
		resumoMensal.setFavorecidos(LancamentoContaUtil.organizarLancamentosPorFavorecido(lancamentos));
		resumoMensal.setMeiosPagamento(LancamentoContaUtil.organizarLancamentosPorMeioPagamento(lancamentos));
		
		// Seta no resumo o início e fim do período buscado
		resumoMensal.setInicio(criterioBusca.getDataInicio());
		resumoMensal.setFim(criterioBusca.getDataFim());
		
		return resumoMensal;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public List<Conta> gerarRelatorioPanoramaCadastro(CadastroSistema cadastro, Long idRegistro) {		
		// Busca todas as contas existentes
		List<Conta> contasExistentes = getContaService().buscarDescricaoOuTipoContaOuAtivoPorUsuario(null, null, getUsuarioComponent().getUsuarioLogado(), null);
		
		// Traz as contas compartilhadas com o usuário
		// Traz as contas compartilhadas para com o usuário atualmente logado
		List<ContaCompartilhada> contasCompartilhadas = getContaCompartilhadaService().buscarTodosPorUsuario(getUsuarioComponent().getUsuarioLogado());
		
		// Acrescenta no Set as contas compartilhadas dos demais usuários
		for (ContaCompartilhada contaCompartilhada : contasCompartilhadas) {
			contasExistentes.add(contaCompartilhada.getConta());
		}
		
		// Declara a lista de contas que será retornada
		List<Conta> contasProcessadas = new ArrayList<Conta>();
		
		// Para cada conta existente busca TODOS os lançamentos do cadastro e do registro selecionado
		for (Conta conta : contasExistentes) {
			
			// Seta os parâmetros de busca
			CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
			criterioBusca.setConta(conta);
			criterioBusca.setIdAgrupamento(idRegistro);
			criterioBusca.setCadastro(cadastro);
			
			// Traz todos os lançamentos encontrados
			//List<LancamentoConta> lancamentos = getLancamentoContaService().buscarPorCriterioBusca(criterioBusca);
			List<LancamentoPanoramaCadastro> lancamentos = getLancamentoContaService().buscarLancamentoParaPanoramaCadastro(conta, cadastro, idRegistro);
			
			// Se a lista de lançamentos vier zerada, passa para a próxima conta
			if (lancamentos == null || lancamentos.isEmpty()) continue;
			
			// Instancia o Map que irá armazenar as informações de ano, quantidade e valor
			Map<Integer, PanoramaCadastro> panoramas = new TreeMap<Integer, PanoramaCadastro>();
			
			// Itera a lista de lançamentos, faz os cálculos de valor e quantidade
			for (LancamentoPanoramaCadastro lancamento : lancamentos) {				
				// Determina o ano
				Integer ano = Integer.valueOf(lancamento.getDataPagamento().getYear() + 1900);
				
				// Verifica se o ano já existe no Map, caso contrário cria uma nova instância
				if (!panoramas.containsKey(ano)) {
					panoramas.put(ano, new PanoramaCadastro(ano));
				}
				
				// Verifica se o cadastro é CATEGORIA para poder setar os atributos de quantidade e valor corretamente
				if (cadastro.equals(CadastroSistema.CATEGORIA)) {
					panoramas.get(ano).setQuantidade(panoramas.get(ano).getQuantidade() + 1);
					panoramas.get(ano).setValor(panoramas.get(ano).getValor() + this.getValorPagoConvertido(lancamento));
					conta.setQuantPanoramaCadastro(conta.getQuantPanoramaCadastro() + 1);
					conta.setTotalPanoramaCadastro(conta.getTotalPanoramaCadastro() + this.getValorPagoConvertido(lancamento));
				} else {
					switch (lancamento.getTipoLancamento()) {
						case RECEITA :
							panoramas.get(ano).setQuantidadeCredito(panoramas.get(ano).getQuantidadeCredito() + 1);
							panoramas.get(ano).setValorCredito(panoramas.get(ano).getValorCredito() + this.getValorPagoConvertido(lancamento));
							conta.setQuantCreditoPanoramaCadastro(conta.getQuantCreditoPanoramaCadastro() + 1);
							conta.setTotalCreditoPanoramaCadastro(conta.getTotalCreditoPanoramaCadastro() + this.getValorPagoConvertido(lancamento));
							break;
						case DESPESA : 
							panoramas.get(ano).setQuantidadeDebito(panoramas.get(ano).getQuantidadeDebito() + 1);
							panoramas.get(ano).setValorDebito(panoramas.get(ano).getValorDebito() + this.getValorPagoConvertido(lancamento));
							conta.setQuantDebitoPanoramaCadastro(conta.getQuantDebitoPanoramaCadastro() + 1);
							conta.setTotalDebitoPanoramaCadastro(conta.getTotalDebitoPanoramaCadastro() + this.getValorPagoConvertido(lancamento));
							break;
					}
				}
			}
			
			// Adiciona os panoramas na conta
			conta.setPanoramasCadastro(new ArrayList<PanoramaCadastro>());
			conta.getPanoramasCadastro().addAll(panoramas.values());
			
			// Inverte a ordem dos anos
			Collections.reverse(conta.getPanoramasCadastro());
			
			
			// Adiciona a conta na lista de contas processadas
			contasProcessadas.add(conta);
		}
		
		return contasProcessadas;
	}
	
	/*** Implementação dos métodos privados ***/
	
	private double getValorPagoConvertido(LancamentoPanoramaCadastro lancamento) {
		if (lancamento.getValorMoedaDestino() != null) {
			return lancamento.getValorMoedaDestino();
		} else {
			return lancamento.getValorPago();
		}
	}
	
	/*
	 * Incorpora os investimentos para compor o saldo atual das contas, de modo que o valor total
	 * represente o patrimônio ativo do usuário.
	 */
	/* Desativado por enquanto */
	@Deprecated
	private List<SaldoAtualConta> incorporarInvestimentos(List<SaldoAtualConta> saldosAtualContas) {
//		// Traz todos os investimento do usuário
//		List<Investimento> investimentos = getInvestimentoService().buscarPorUsuario(getUsuarioComponent().getUsuarioLogado());
//		
//		// Pega o mês/ano atual
//		Calendar temp = Calendar.getInstance();
//		int mes = temp.get(Calendar.MONTH) + 1;
//		int ano = temp.get(Calendar.YEAR);
//		
//		// Itera os investimentos
//		for (Investimento investimento : investimentos) {
//			
//			ResumoInvestimento resumo = investimento.buscarResumoInvestimento(mes, ano);
//			if (resumo == null)
//				resumo = new ResumoInvestimento(mes, ano);
//			
//			SaldoAtualConta saldo = new SaldoAtualConta();
//			saldo.setAtivo(investimento.isAtivo());
//			saldo.setDescricaoConta(investimento.getLabel());
//			saldo.setMoedaConta(getMoedaService().buscarPadraoPorUsuario(investimento.getUsuario()));
//			saldo.setTipoConta(TipoConta.OUTROS);			
//			saldo.setSaldoPeriodo(resumo.getAplicacao() - resumo.getResgate());
//			saldo.setSaldoRegistrado(resumo.getRendimentoBruto() - (resumo.getImpostoRenda() + resumo.getIof()));
//			saldo.setSaldoAtual(saldo.getSaldoPeriodo() + saldo.getSaldoRegistrado());
//			
//			// Inclui o saldo no List
//			saldosAtualContas.add(saldo);
//		}
		
		return saldosAtualContas;
	}
}