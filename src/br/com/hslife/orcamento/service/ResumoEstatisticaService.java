/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

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
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.ConversaoMoeda;
import br.com.hslife.orcamento.entity.FaturaCartao;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.CadastroSistema;
import br.com.hslife.orcamento.enumeration.IncrementoClonagemLancamento;
import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.enumeration.PeriodoLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoCartao;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFaturaCartao;
import br.com.hslife.orcamento.facade.IFechamentoPeriodo;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.ILancamentoPeriodico;
import br.com.hslife.orcamento.facade.IResumoEstatistica;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.model.PanoramaCadastro;
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

	/*** Declaração dos componentes ***/
	
	@Autowired
	private UsuarioComponent usuarioComponent;
	
	/*** Declaração dos Getters dos componentes ***/
	
	public UsuarioComponent getUsuarioComponent() {
		return usuarioComponent;
	}
	
	/*** Implementação dos métodos da interface ***/
	
	public List<SaldoAtualConta> gerarSaldoAtualContas(boolean agendado, Usuario usuario) throws BusinessException {
		// Declaração dos objetos
		List<SaldoAtualConta> saldoAtualContas = new ArrayList<>();
		SaldoAtualConta saldoAtual = new SaldoAtualConta();
		
		// Itera todas as contas do usuário
		for (Conta conta : getContaService().buscarDescricaoOuTipoContaOuAtivoPorUsuario(null, new TipoConta[]{}, usuario, null)) { // resolvendo a ambiguidade do método
			
			// FIXME decidir como irei tratar o saldo atual dos cartões de débito
			// Caso seja cartão de débito, passa adiante
			if (conta.getTipoConta().equals(TipoConta.CARTAO) && conta.getCartaoCredito().getTipoCartao().equals(TipoCartao.DEBITO))
				continue;
			
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
				saldoAtual.setSaldoPeriodo(conta.getMoeda().isPadrao() 
						? conta.getCartaoCredito().getLimiteCartao() 
								: Util.arredondar(conta.getCartaoCredito().getLimiteCartao() * conta.getMoeda().getValorConversao()));
				
				// Traz todos os lançamentos do cartão que ainda não foram quitados
				criterio.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.REGISTRADO, StatusLancamentoConta.AGENDADO});
				
				// Seta a conta
				criterio.setConta(conta);
			} else {
				// Traz o último período de fechamento da conta
				FechamentoPeriodo ultimoFechamento = getFechamentoPeriodoService().buscarUltimoFechamentoConta(conta); 
				if (ultimoFechamento == null) {
					saldoAtual.setSaldoPeriodo(conta.getMoeda().isPadrao()
							? conta.getSaldoInicial()
									: Util.arredondar(conta.getSaldoInicial() * conta.getMoeda().getValorConversao()));
				} else {
					saldoAtual.setSaldoPeriodo(conta.getMoeda().isPadrao()
							? ultimoFechamento.getSaldo()
									: Util.arredondar(ultimoFechamento.getSaldo() * conta.getMoeda().getValorConversao()));
				}
				
				// Traz os lançamentos da data de fechamento em diante
				// Se a opção de agendado estiver marcada, traz os lançamentos agendados
				if (agendado)
					criterio.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.AGENDADO, StatusLancamentoConta.REGISTRADO, StatusLancamentoConta.QUITADO});
				else 
					criterio.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.REGISTRADO, StatusLancamentoConta.QUITADO});
				
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
			}
			
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
	@Override
	public List<PanoramaLancamentoConta> gerarRelatorioPanoramaLancamentoConta(CriterioBuscaLancamentoConta criterioBusca, int ano) throws BusinessException {
		
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
			lancamentosProcessados = getLancamentoContaService().buscarPorCriterioBusca(criterioBusca);
		} else {
			// Anos posteriores é realizado a estimativa baseado no mês e ano informado e os lançamentos periódicos ativos da conta
			
			/*** Lançamentos parcelados ***/
			// Traz todos os lançamentos parcelados ativos da conta selecionada
			List<LancamentoPeriodico> parcelamentos = getLancamentoPeriodicoService().
					buscarPorTipoLancamentoContaEStatusLancamento(TipoLancamentoPeriodico.PARCELADO, criterioBusca.getConta(), StatusLancamento.ATIVO);					
			
			// Itera os lançamentos parcelados a adiciona suas parcelas caso esteja no mesmo ano que o relatório
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
			
			/*** Lançamentos fixos ***/
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
			
			
			/*** Lançamentos avulsos ***/
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
		FechamentoPeriodo fechamento = getFechamentoPeriodoService().buscarUltimoFechamentoPeriodoAntesDataPorContaEOperacao(criterioBusca.getConta(), Util.ultimoDiaAno(ano - 1), OperacaoConta.FECHAMENTO);
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
	public ResumoMensalContas gerarRelatorioResumoMensalContas(Conta conta, FaturaCartao faturaCartao) throws BusinessException {
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
	
	public ResumoMensalContas gerarRelatorioResumoMensalContas(Conta conta, Date dataInicio, Date dataFim) throws BusinessException {
		ResumoMensalContas resumoMensal = new ResumoMensalContas();
		CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
		
		// Preenche os parâmetros de busca
		//criterioBusca.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.REGISTRADO, StatusLancamentoConta.QUITADO});
		criterioBusca.setConta(conta);
		resumoMensal.setConta(conta);
		
		criterioBusca.setDataInicio(dataInicio);
		criterioBusca.setDataFim(dataFim);
		
		// Realiza a busca
		List<LancamentoConta> lancamentos = getLancamentoContaService().buscarPorCriterioBusca(criterioBusca);
		
		// Processa as categorias, favorecidos e meios de pagamento
		resumoMensal.setCategoriasCartao(LancamentoContaUtil.organizarLancamentosPorCategoria(lancamentos));
		resumoMensal.setFavorecidos(LancamentoContaUtil.organizarLancamentosPorFavorecido(lancamentos));
		resumoMensal.setMeiosPagamento(LancamentoContaUtil.organizarLancamentosPorMeioPagamento(lancamentos));
		
		// Seta no resumo o início e fim do período buscado
		resumoMensal.setInicio(criterioBusca.getDataInicio());
		resumoMensal.setFim(criterioBusca.getDataFim());
		
		return resumoMensal;
	}
	
	@Override
	public ResumoMensalContas gerarRelatorioResumoMensalContas(Conta conta, FechamentoPeriodo fechamentoPeriodo) throws BusinessException {
		ResumoMensalContas resumoMensal = new ResumoMensalContas();
		CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
		FechamentoPeriodo fechamentoAnterior = null;
				
		// Busca o fechamento do período anterior
		if (fechamentoPeriodo != null) 
			fechamentoAnterior = getFechamentoPeriodoService().buscarFechamentoPeriodoAnterior(fechamentoPeriodo);
		else
			fechamentoAnterior = getFechamentoPeriodoService().buscarUltimoFechamentoConta(conta);
		
		// Preenche os parâmetros de busca
		//criterioBusca.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.REGISTRADO, StatusLancamentoConta.QUITADO});
		criterioBusca.setConta(conta);
		resumoMensal.setConta(conta);
		
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
		List<LancamentoConta> lancamentos = getLancamentoContaService().buscarPorCriterioBusca(criterioBusca);
		
		// Processa as categorias
		if (fechamentoAnterior == null) {
			resumoMensal.setCategorias(LancamentoContaUtil.organizarLancamentosPorCategoria(lancamentos), conta.getSaldoInicial(), conta.getSaldoInicial() + LancamentoContaUtil.calcularSaldoLancamentos(lancamentos));
		} else {
			resumoMensal.setCategorias(LancamentoContaUtil.organizarLancamentosPorCategoria(lancamentos), fechamentoAnterior.getSaldo(), fechamentoAnterior.getSaldo() + LancamentoContaUtil.calcularSaldoLancamentos(lancamentos));
		}
		resumoMensal.setFavorecidos(LancamentoContaUtil.organizarLancamentosPorFavorecido(lancamentos));
		resumoMensal.setMeiosPagamento(LancamentoContaUtil.organizarLancamentosPorMeioPagamento(lancamentos));
		
		// Seta no resumo o início e fim do período buscado
		resumoMensal.setInicio(criterioBusca.getDataInicio());
		resumoMensal.setFim(criterioBusca.getDataFim());
		
		return resumoMensal;	
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public List<Conta> gerarRelatorioPanoramaCadastro(CadastroSistema cadastro, Long idRegistro) throws BusinessException {		
		// Busca todas as contas existentes
		List<Conta> contasExistentes = getContaService().buscarDescricaoOuTipoContaOuAtivoPorUsuario(null, new TipoConta[]{}, usuarioComponent.getUsuarioLogado(), null); // resolvendo a ambiguidade do método
		
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
			List<LancamentoConta> lancamentos = getLancamentoContaService().buscarPorCriterioBusca(criterioBusca);
			
			// Se a lista de lançamentos vier zerada, passa para a próxima conta
			if (lancamentos == null || lancamentos.isEmpty()) continue;
			
			// Instancia o Map que irá armazenar as informações de ano, quantidade e valor
			Map<Integer, PanoramaCadastro> panoramas = new TreeMap<Integer, PanoramaCadastro>();
			
			// Itera a lista de lançamentos, faz os cálculos de valor e quantidade
			for (LancamentoConta lancamento : lancamentos) {				
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
				} else {
					switch (lancamento.getTipoLancamento()) {
						case RECEITA :
							panoramas.get(ano).setQuantidadeCredito(panoramas.get(ano).getQuantidadeCredito() + 1);
							panoramas.get(ano).setValorCredito(panoramas.get(ano).getValorCredito() + this.getValorPagoConvertido(lancamento));
							break;
						case DESPESA : 
							panoramas.get(ano).setQuantidadeDebito(panoramas.get(ano).getQuantidadeDebito() + 1);
							panoramas.get(ano).setValorDebito(panoramas.get(ano).getValorDebito() + this.getValorPagoConvertido(lancamento));
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
	
	private double getValorPagoConvertido(LancamentoConta lancamento) {
		double taxaConversao = lancamento.getMoeda().getValorConversao();
		if (lancamento.getMoeda().equals(lancamento.getConta().getMoeda())) {
			return lancamento.getValorPago();
		} else {
			if (lancamento.getFaturaCartao() == null) {
				return Util.arredondar(lancamento.getValorPago() * taxaConversao);
			} else {
				
				for (ConversaoMoeda conversao : lancamento.getFaturaCartao().getConversoesMoeda()) {
					if (conversao.getMoeda().equals(lancamento.getMoeda())) {
						return Util.arredondar(lancamento.getValorPago() * conversao.getTaxaConversao());
					}
				}
				return lancamento.getValorPago() * taxaConversao;
			}
		}
	}	
}