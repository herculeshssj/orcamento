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

package br.com.hslife.orcamento.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.repository.ContaRepository;
import br.com.hslife.orcamento.repository.FechamentoPeriodoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoPeriodicoRepository;
import br.com.hslife.orcamento.repository.MoedaRepository;
import br.com.hslife.orcamento.util.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.util.EntityLabelComparator;
import br.com.hslife.orcamento.util.Util;

@Component
public class ContaComponent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4282979732617114700L;

	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private FechamentoPeriodoRepository fechamentoPeriodoRepository;
	
	@Autowired
	private LancamentoPeriodicoRepository lancamentoPeriodicoRepository;
	
	@Autowired
	private MoedaRepository moedaRepository;
	
	@Autowired
	private UsuarioComponent usuarioComponent;

	public void setLancamentoContaRepository(
			LancamentoContaRepository lancamentoContaRepository) {
		this.lancamentoContaRepository = lancamentoContaRepository;
	}

	public void setFechamentoPeriodoRepository(
			FechamentoPeriodoRepository fechamentoPeriodoRepository) {
		this.fechamentoPeriodoRepository = fechamentoPeriodoRepository;
	}

	public void setContaRepository(ContaRepository contaRepository) {
		this.contaRepository = contaRepository;
	}
	
	public void setUsuarioComponent(UsuarioComponent usuarioComponent) {
		this.usuarioComponent = usuarioComponent;
	}
	
	public FechamentoPeriodo buscarUltimoFechamentoPeriodoPorConta(Conta conta) {
		return fechamentoPeriodoRepository.findUltimoFechamentoByConta(conta);
	}
	
	public Moeda getMoedaPadrao() {
		return moedaRepository.findDefaultByUsuario(usuarioComponent.getUsuarioLogado());
	}
	
	public double calcularSaldoLancamentos(List<LancamentoConta> lancamentos) {		
		double total = 0.0;
		if (lancamentos != null) {
			for (LancamentoConta l : lancamentos) {
				if (l.getTipoLancamento().equals(TipoLancamento.RECEITA)) {
					total += l.getValorPago();
				} else {
					total -= l.getValorPago();
				}
			}
		}
		return Util.arredondar(total);
	}
	
	public double calcularSaldoLancamentosComConversao(List<LancamentoConta> lancamentos) {		
		double total = 0.0;
		for (LancamentoConta l : lancamentos) {
			if (l.getTipoLancamento().equals(TipoLancamento.RECEITA)) {
				if (l.getMoeda().isPadrao())
					total += l.getValorPago();
				else
					total += l.getValorPago() * l.getMoeda().getValorConversao();
			} else {
				if (l.getMoeda().isPadrao())
					total -= l.getValorPago();
				else
					total -= l.getValorPago() * l.getMoeda().getValorConversao();
			}
		}
		return Util.arredondar(total);
	}
	
	public List<Categoria> organizarLancamentosPorCategoria(List<LancamentoConta> lancamentos) throws BusinessException {
		List<Categoria> categorias = new ArrayList<Categoria>();
		
		/* Usa-se o Set para separar as categorias da listagem de lançamentos */
		SortedSet<Categoria> setCategorias = new TreeSet<Categoria>(new EntityLabelComparator());
		
		// Adiciona as categorias no Set
		for (LancamentoConta l : lancamentos) {
			if (l.getCategoria() != null)
				setCategorias.add(l.getCategoria());
		}
		
		// Adiciona as categorias no List
		categorias.addAll(setCategorias);
					
		// Zera a lista de categorias
		for (Categoria c : categorias) {
			c.setLancamentos(new ArrayList<LancamentoConta>());
			c.setSaldoPago(0.0);
		}
		
		List<LancamentoConta> lancamentosSemCategoria = new ArrayList<LancamentoConta>();
		double saldoLancamentosSemCategoria = 0.0;
				
		// Varre a lista de lançamentos para adicionar os lançamentos nas respectivas categorias
		for (LancamentoConta l : lancamentos) {
			
			for (int i = 0; i < categorias.size(); i++) {
				if (l.getCategoria() != null && categorias.get(i).getId().equals(l.getCategoria().getId())) {
					categorias.get(i).getLancamentos().add(l);
					if (l.getTipoLancamento() == TipoLancamento.RECEITA) {
						categorias.get(i).setSaldoPago(categorias.get(i).getSaldoPago() + l.getValorPago());
					} else {
						categorias.get(i).setSaldoPago(categorias.get(i).getSaldoPago() - l.getValorPago());
					}						
				} 
			}
			if (l.getCategoria() == null) {
				lancamentosSemCategoria.add(l);
				if (l.getTipoLancamento() == TipoLancamento.RECEITA) {						
					saldoLancamentosSemCategoria += l.getValorPago();
				} else {						
					saldoLancamentosSemCategoria -= l.getValorPago();
				}
			}
		}
		
		// Remove as categorias que não tem lançamentos e ajusta as casas decimais dos saldos
		int indice = categorias.size() - 1;
		while (indice >= 0) {
			if (categorias.get(indice).getLancamentos().size() == 0) {
				categorias.remove(indice);				
			} else {
				categorias.get(indice).setSaldoPago(Util.arredondar(categorias.get(indice).getSaldoPago()));
			}
			indice--;
		}
		
		// Cria uma categoria a mais para os lançamentos que não tem categorias atribuídas
		if (saldoLancamentosSemCategoria != 0) {
			Categoria c = new Categoria();
			c.setDescricao("Sem categoria");
			c.setLancamentos(lancamentosSemCategoria);
			c.setSaldoPago(saldoLancamentosSemCategoria);
			categorias.add(c);
		}
		
		return categorias;
	}

	public List<Favorecido> organizarLancamentosPorFavorecido(List<LancamentoConta> lancamentos) throws BusinessException {
		List<Favorecido> favorecidos = new ArrayList<Favorecido>();
		
		/* Usa-se o Set para separar os favorecidos da listagem de lançamentos */
		SortedSet<Favorecido> setFavorecidos = new TreeSet<Favorecido>(new EntityLabelComparator());
		
		// Adiciona os favorecidos no Set
		for (LancamentoConta l : lancamentos) {
			if (l.getFavorecido() != null)
				setFavorecidos.add(l.getFavorecido());
		}
		
		// Adiciona os favorecidos no List
		favorecidos.addAll(setFavorecidos);
		
		// Zera a lista de favorecidos
		for (Favorecido f : favorecidos) {
			f.setLancamentos(new ArrayList<LancamentoConta>());			
			f.setSaldoPago(0.0);
		}
				
		List<LancamentoConta> lancamentosSemFavorecido = new ArrayList<LancamentoConta>();
		double saldoLancamentosSemFavorecido = 0.0;
		double saldoCreditoLancamentosSemFavorecido = 0.0;
		double saldoDebitoLancamentosSemFavorecido = 0.0;
		
		// Varre a lista de lançamentos para adicionar os lançamentos nos respectivos favorecidos
		for (LancamentoConta l : lancamentos) {
			
			for (int i = 0; i < favorecidos.size(); i++) {
				if (l.getFavorecido() != null && favorecidos.get(i).getId().equals(l.getFavorecido().getId())) {
					favorecidos.get(i).getLancamentos().add(l);
					if (l.getTipoLancamento() == TipoLancamento.RECEITA) {						
						favorecidos.get(i).setSaldoPago(favorecidos.get(i).getSaldoPago() + l.getValorPago());
						favorecidos.get(i).setSaldoCredito(favorecidos.get(i).getSaldoCredito() + l.getValorPago());
					} else {						
						favorecidos.get(i).setSaldoPago(favorecidos.get(i).getSaldoPago() - l.getValorPago());
						favorecidos.get(i).setSaldoDebito(favorecidos.get(i).getSaldoDebito() + l.getValorPago());
					}						
				}
			}
			if (l.getFavorecido() == null) {
				lancamentosSemFavorecido.add(l);
				if (l.getTipoLancamento() == TipoLancamento.RECEITA) {						
					saldoLancamentosSemFavorecido += l.getValorPago();
					saldoCreditoLancamentosSemFavorecido += l.getValorPago();
				} else {						
					saldoLancamentosSemFavorecido -= l.getValorPago();
					saldoDebitoLancamentosSemFavorecido += l.getValorPago();
				}
			}
		}
		
		// Remove os favorecidos que não tem lançamentos e ajusta as casas decimais dos saldos
		int indice = favorecidos.size() - 1;
		while (indice >= 0) {
			if (favorecidos.get(indice).getLancamentos().size() == 0) {
				favorecidos.remove(indice);				
			} else {
				favorecidos.get(indice).setSaldoPago(Util.arredondar(favorecidos.get(indice).getSaldoPago()));
				favorecidos.get(indice).setSaldoCredito(Util.arredondar(favorecidos.get(indice).getSaldoCredito()));
				favorecidos.get(indice).setSaldoDebito(Util.arredondar(favorecidos.get(indice).getSaldoDebito()));
			}
			indice--;
		}
		
		// Cria um favorecido a mais para os lançamentos que não tem favorecido atribuídos
		if (saldoLancamentosSemFavorecido != 0) {
			Favorecido f = new Favorecido();
			f.setNome("Sem favorecido/sacado");
			f.setLancamentos(lancamentosSemFavorecido);
			f.setSaldoPago(saldoLancamentosSemFavorecido);
			f.setSaldoCredito(saldoCreditoLancamentosSemFavorecido);
			f.setSaldoDebito(saldoDebitoLancamentosSemFavorecido);
			favorecidos.add(f);
		}
			
		return favorecidos;
	}

	public List<MeioPagamento> organizarLancamentosPorMeioPagamento(List<LancamentoConta> lancamentos) throws BusinessException {
		List<MeioPagamento> meiosPagamento = new ArrayList<MeioPagamento>();
		
		/* Usa-se o Set para separar os meios de pagamento da listagem de lançamentos */
		SortedSet<MeioPagamento> setMeiosPagamento = new TreeSet<MeioPagamento>(new EntityLabelComparator());
		
		// Adiciona os meios de pagamento no Set
		for (LancamentoConta l : lancamentos) {
			if (l.getMeioPagamento() != null)
				setMeiosPagamento.add(l.getMeioPagamento());
		}
		
		// Adiciona os meios de pagamento no List
		meiosPagamento.addAll(setMeiosPagamento);
		
		// Zera a lista de meios de pagamento
		for (MeioPagamento m : meiosPagamento) {
			m.setLancamentos(new ArrayList<LancamentoConta>());			
			m.setSaldoPago(0.0);
		}
				
		List<LancamentoConta> lancamentosSemMeioPagamento = new ArrayList<LancamentoConta>();
		double saldoLancamentosSemMeioPagamento = 0.0;
		double saldoCreditoLancamentosSemMeioPagamento = 0.0;
		double saldoDebitoLancamentosSemMeioPagamento = 0.0;
		
		// Varre a lista de lançamentos para adicionar os lançamentos nos respectivos meios de pagamento
		for (LancamentoConta l : lancamentos) {
			
			for (int i = 0; i < meiosPagamento.size(); i++) {
				if (l.getMeioPagamento() != null && meiosPagamento.get(i).getId().equals(l.getMeioPagamento().getId())) {
					meiosPagamento.get(i).getLancamentos().add(l);
					if (l.getTipoLancamento() == TipoLancamento.RECEITA) {						
						meiosPagamento.get(i).setSaldoPago(meiosPagamento.get(i).getSaldoPago() + l.getValorPago());
						meiosPagamento.get(i).setSaldoCredito(meiosPagamento.get(i).getSaldoCredito() + l.getValorPago());
					} else {						
						meiosPagamento.get(i).setSaldoPago(meiosPagamento.get(i).getSaldoPago() - l.getValorPago());
						meiosPagamento.get(i).setSaldoDebito(meiosPagamento.get(i).getSaldoDebito() + l.getValorPago());
					}						
				}
			}
			if (l.getMeioPagamento() == null) {
				lancamentosSemMeioPagamento.add(l);
				if (l.getTipoLancamento() == TipoLancamento.RECEITA) {						
					saldoLancamentosSemMeioPagamento += l.getValorPago();
					saldoCreditoLancamentosSemMeioPagamento += l.getValorPago();
				} else {						
					saldoLancamentosSemMeioPagamento -= l.getValorPago();
					saldoDebitoLancamentosSemMeioPagamento += l.getValorPago();
				}
			}
		}
		
		// Remove os meios de pagamento que não tem lançamentos e ajusta as casas decimais dos saldos
		int indice = meiosPagamento.size() - 1;
		while (indice >= 0) {
			if (meiosPagamento.get(indice).getLancamentos().size() == 0) {
				meiosPagamento.remove(indice);				
			} else {
				meiosPagamento.get(indice).setSaldoPago(Util.arredondar(meiosPagamento.get(indice).getSaldoPago()));
				meiosPagamento.get(indice).setSaldoCredito(Util.arredondar(meiosPagamento.get(indice).getSaldoCredito()));
				meiosPagamento.get(indice).setSaldoDebito(Util.arredondar(meiosPagamento.get(indice).getSaldoDebito()));
			}
			indice--;
		}
		
		// Cria um meio de pagamento a mais para os lançamentos que não tem meios de pagamento atribuídos
		if (saldoLancamentosSemMeioPagamento != 0) {
			MeioPagamento m = new MeioPagamento();
			m.setDescricao("Sem meio de pagamento");
			m.setLancamentos(lancamentosSemMeioPagamento);
			m.setSaldoPago(saldoLancamentosSemMeioPagamento);
			m.setSaldoCredito(saldoCreditoLancamentosSemMeioPagamento);
			m.setSaldoDebito(saldoDebitoLancamentosSemMeioPagamento);
			meiosPagamento.add(m);
		}
			
		return meiosPagamento;
	}

	public List<FechamentoPeriodo> buscarPorContaEOperacaoConta(Conta conta, OperacaoConta operacaoConta) throws BusinessException {
		return fechamentoPeriodoRepository.findByContaAndOperacaoConta(conta, operacaoConta);
	}
	
	public void fecharPeriodo(Date dataFechamento, Conta conta) throws BusinessException {
		this.fecharPeriodo(dataFechamento, conta, null, null);
	}
	
	public void fecharPeriodo(Date dataFechamento, Conta conta, List<LancamentoPeriodico> lancamentosPeriodicos) throws BusinessException {
		this.fecharPeriodo(dataFechamento, conta, null, lancamentosPeriodicos);
	}
	
	public void fecharPeriodo(FechamentoPeriodo fechamentoPeriodo, List<LancamentoPeriodico> lancamentosPeriodicos) throws BusinessException {
		this.fecharPeriodo(fechamentoPeriodo.getData(), fechamentoPeriodo.getConta(), fechamentoPeriodo, lancamentosPeriodicos);
	}
	
	@SuppressWarnings("deprecation")
	public void fecharPeriodo(Date dataFechamento, Conta conta, FechamentoPeriodo fechamentoReaberto, List<LancamentoPeriodico> lancamentosPeriodicos) throws BusinessException {
		// Obtém-se o último fechamento realizado
		FechamentoPeriodo fechamentoAnterior;
		if (fechamentoReaberto == null)
			fechamentoAnterior = fechamentoPeriodoRepository.findUltimoFechamentoByConta(conta);
		else 
			fechamentoAnterior = fechamentoPeriodoRepository.findFechamentoPeriodoAnterior(fechamentoReaberto);
		
		double saldoFechamentoAnterior = 0;
		
		if (fechamentoAnterior == null) {
			saldoFechamentoAnterior = conta.getSaldoInicial();
		} else {
			saldoFechamentoAnterior = fechamentoAnterior.getSaldo();
		}
		
		// Incrementa a data do fechamento anterior
		Calendar temp = Calendar.getInstance();
		if (fechamentoAnterior != null) {
			temp.setTime(fechamentoAnterior.getData());
			temp.add(Calendar.DAY_OF_YEAR, 1);
		} else
			temp.setTime(conta.getDataAbertura());		
		
		// Calcula o saldo do período
		CriterioBuscaLancamentoConta criterio = new CriterioBuscaLancamentoConta();
		criterio.setConta(conta);
		criterio.setDescricao("");
		criterio.setDataInicio(temp.getTime());
		criterio.setDataFim(dataFechamento);
		criterio.setStatusLancamentoConta(new StatusLancamentoConta[]{StatusLancamentoConta.REGISTRADO, StatusLancamentoConta.QUITADO});
		double saldoFechamento = this.calcularSaldoLancamentos(lancamentoContaRepository.findByCriterioBusca(criterio));
		
		// Cria o novo fechamento para a conta
		FechamentoPeriodo novoFechamento = new FechamentoPeriodo();
		if (fechamentoReaberto == null) {
			// Antes de prosseguir, verifica se não existem períodos reabertos
			List<FechamentoPeriodo> fechamentosReabertos = fechamentoPeriodoRepository.findByContaAndOperacaoConta(conta, OperacaoConta.REABERTURA);
			if (fechamentosReabertos != null && !fechamentosReabertos.isEmpty()) {
				throw new BusinessException("Não é possível fechar! Existem períodos anteriores reabertos!");
			}
			
			novoFechamento.setConta(conta);
			novoFechamento.setData(dataFechamento);
			novoFechamento.setOperacao(OperacaoConta.FECHAMENTO);
			novoFechamento.setDataAlteracao(new Date());
			novoFechamento.setSaldo(saldoFechamentoAnterior + saldoFechamento);
			
			// Obtém o mês e ano da data de fechamento
			temp.setTime(dataFechamento);
			novoFechamento.setMes(temp.getTime().getMonth() + 1);
			novoFechamento.setAno(temp.get(Calendar.YEAR));
			
			// Salva o fechamento
			fechamentoPeriodoRepository.save(novoFechamento);
		} else {
			// Antes de prosseguir, verifica se o período selecionado não contém
			// períodos reabertos anteriores
			List<FechamentoPeriodo> fechamentosAnterioresReabertos = fechamentoPeriodoRepository.findFechamentosAnterioresReabertos(fechamentoReaberto);
			if (fechamentosAnterioresReabertos != null && !fechamentosAnterioresReabertos.isEmpty()) {
				throw new BusinessException("Não é possível fechar! Existem períodos anteriores reabertos!");
			}
			
			// Altera os dados do fechamento já existente
			fechamentoReaberto.setOperacao(OperacaoConta.FECHAMENTO);
			fechamentoReaberto.setDataAlteracao(new Date());
			fechamentoReaberto.setSaldo(saldoFechamentoAnterior + saldoFechamento);
			
			// Salva o fechamento
			fechamentoPeriodoRepository.update(fechamentoReaberto);
		}
		
		// Quita os lançamentos do período
		for (LancamentoConta l : lancamentoContaRepository.findByCriterioBusca(criterio)) {
			l.setStatusLancamentoConta(StatusLancamentoConta.QUITADO);
			if (fechamentoReaberto == null)
				l.setFechamentoPeriodo(novoFechamento);
			else
				l.setFechamentoPeriodo(fechamentoReaberto);

			if (lancamentosPeriodicos.contains(l.getLancamentoPeriodico())) {
				this.registrarPagamento(l);
			} else {
				lancamentoContaRepository.update(l);
			}
		}
		
		
	}
	
	public void reabrirPeriodo(FechamentoPeriodo entity) throws BusinessException {
		// Busca os fechamentos posteriores ao fechamento selecionado
		List<FechamentoPeriodo> fechamentosPosteriores = fechamentoPeriodoRepository.findFechamentosPosteriores(entity);
		
		// Itera a lista de fechamentos realizando a reabertura dos mesmos e dos lançamentos vinculados
		for (FechamentoPeriodo fechamentoPeriodo : fechamentosPosteriores) {
			FechamentoPeriodo fechamento = fechamentoPeriodoRepository.findById(fechamentoPeriodo.getId());
			fechamento.setOperacao(OperacaoConta.REABERTURA);
			fechamento.setDataAlteracao(new Date());
			fechamentoPeriodoRepository.update(fechamento);
			
			for (LancamentoConta l : fechamento.getLancamentos()) {
				l.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
				lancamentoContaRepository.update(l);
			}
		}
	}

	public List<Moeda> organizarLancamentosPorMoeda(List<LancamentoConta> lancamentos) {
		List<Moeda> moedas = new ArrayList<Moeda>();
		
		/* Usa-se o Set para separar os moedas da listagem de lançamentos */
		SortedSet<Moeda> setMoedas = new TreeSet<Moeda>(new EntityLabelComparator());
		
		// Adiciona os moedas no Set
		for (LancamentoConta l : lancamentos) {
			if (l.getMoeda() != null)
				setMoedas.add(l.getMoeda());
		}
		
		// Adiciona os moedas no List
		moedas.addAll(setMoedas);
		
		// Zera a lista de moedas
		for (Moeda m : moedas) {
			m.setLancamentos(new ArrayList<LancamentoConta>());			
			m.setSaldoPago(0.0);
		}
				
		List<LancamentoConta> lancamentosSemMoeda = new ArrayList<LancamentoConta>();
		double saldoLancamentosSemMoeda = 0.0;
		double saldoCreditoLancamentosSemMoeda = 0.0;
		double saldoDebitoLancamentosSemMoeda = 0.0;
		
		// Varre a lista de lançamentos para adicionar os lançamentos nos respectivos moedas
		for (LancamentoConta l : lancamentos) {
			
			for (int i = 0; i < moedas.size(); i++) {
				if (l.getMoeda() != null && moedas.get(i).getId().equals(l.getMoeda().getId())) {
					moedas.get(i).getLancamentos().add(l);
					if (l.getTipoLancamento() == TipoLancamento.RECEITA) {						
						moedas.get(i).setSaldoPago(moedas.get(i).getSaldoPago() + l.getValorPago());
						moedas.get(i).setSaldoCredito(moedas.get(i).getSaldoCredito() + l.getValorPago());
					} else {						
						moedas.get(i).setSaldoPago(moedas.get(i).getSaldoPago() - l.getValorPago());
						moedas.get(i).setSaldoDebito(moedas.get(i).getSaldoDebito() + l.getValorPago());
					}						
				}
			}
			if (l.getMoeda() == null) {
				lancamentosSemMoeda.add(l);
				if (l.getTipoLancamento() == TipoLancamento.RECEITA) {						
					saldoLancamentosSemMoeda += l.getValorPago();
					saldoCreditoLancamentosSemMoeda += l.getValorPago();
				} else {						
					saldoLancamentosSemMoeda -= l.getValorPago();
					saldoDebitoLancamentosSemMoeda += l.getValorPago();
				}
			}
		}
		
		// Remove os moedas que não tem lançamentos e ajusta as casas decimais dos saldos
		int indice = moedas.size() - 1;
		while (indice >= 0) {
			if (moedas.get(indice).getLancamentos().size() == 0) {
				moedas.remove(indice);				
			} else {
				moedas.get(indice).setSaldoPago(Util.arredondar(moedas.get(indice).getSaldoPago()));
				moedas.get(indice).setSaldoCredito(Util.arredondar(moedas.get(indice).getSaldoCredito()));
				moedas.get(indice).setSaldoDebito(Util.arredondar(moedas.get(indice).getSaldoDebito()));
			}
			indice--;
		}
		
		// Cria um favorecido a mais para os lançamentos que não tem favorecido atribuídos
		if (saldoLancamentosSemMoeda != 0) {
			Moeda m = new Moeda();
			m.setNome("Sem moeda definida");
			m.setLancamentos(lancamentosSemMoeda);
			m.setSaldoPago(saldoLancamentosSemMoeda);
			m.setSaldoCredito(saldoCreditoLancamentosSemMoeda);
			m.setSaldoDebito(saldoDebitoLancamentosSemMoeda);
			moedas.add(m);
		}
			
		return moedas;
	}
	
	// TODO diminuir a complexidade ciclomática do método. Averiguar se a próxima mensalidade pode ser gerada por clonagem
	public void registrarPagamento(LancamentoConta pagamentoPeriodo) throws BusinessException {		
		pagamentoPeriodo.setStatusLancamentoConta(StatusLancamentoConta.QUITADO);
		
		// Atualiza o pagamento
		lancamentoContaRepository.update(pagamentoPeriodo);
		
		// Gera o próximo pagamento para os lançamentos fixos
		if (pagamentoPeriodo.getLancamentoPeriodico().getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.FIXO)
				&& pagamentoPeriodo.getLancamentoPeriodico().getStatusLancamento().equals(StatusLancamento.ATIVO)) {
			
			// Busca o pagamento mais recente
			LancamentoConta ultimaMensalidade = lancamentoContaRepository.findLastGeneratedPagamentoPeriodo(pagamentoPeriodo.getLancamentoPeriodico());
			
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
			
			LancamentoConta proximaMensalidade = new LancamentoConta();
			proximaMensalidade.setLancamentoPeriodico(pagamentoPeriodo.getLancamentoPeriodico());
			dataVencimento.set(Calendar.DAY_OF_MONTH, pagamentoPeriodo.getLancamentoPeriodico().getDiaVencimento());
			
			proximaMensalidade.setAno(dataVencimento.get(Calendar.YEAR));
			proximaMensalidade.setPeriodo(dataVencimento.get(Calendar.MONTH) + 1);
			proximaMensalidade.setDataVencimento(dataVencimento.getTime());
			
			// Setando os demais atributos
			proximaMensalidade.setConta(pagamentoPeriodo.getLancamentoPeriodico().getConta());
			proximaMensalidade.setTipoLancamento(pagamentoPeriodo.getTipoLancamento());
			proximaMensalidade.setValorPago(proximaMensalidade.getLancamentoPeriodico().getValorParcela());
			proximaMensalidade.setDataPagamento(proximaMensalidade.getDataVencimento());
			proximaMensalidade.setCategoria(pagamentoPeriodo.getCategoria());
			proximaMensalidade.setFavorecido(pagamentoPeriodo.getFavorecido());
			proximaMensalidade.setMeioPagamento(pagamentoPeriodo.getMeioPagamento());
			proximaMensalidade.setMoeda(pagamentoPeriodo.getMoeda());
			proximaMensalidade.setStatusLancamentoConta(proximaMensalidade.getDataPagamento().after(new Date()) ? StatusLancamentoConta.AGENDADO : StatusLancamentoConta.REGISTRADO);
			
			// Define a descrição definitiva do lançamento a ser criado
			proximaMensalidade.setDescricao(pagamentoPeriodo.getLancamentoPeriodico().getDescricao() + " - Período " + proximaMensalidade.getPeriodo() + " / " + proximaMensalidade.getAno() + ", vencimento para " + Util.formataDataHora(proximaMensalidade.getDataVencimento(), Util.DATA));
			
			lancamentoContaRepository.save(proximaMensalidade);
			
		} else {
			// Verifica se o lançamento periódico vinculado já pode ser encerrado.
			if (pagamentoPeriodo.getLancamentoPeriodico().podeEncerrar()) {
				pagamentoPeriodo.getLancamentoPeriodico().setStatusLancamento(StatusLancamento.ENCERRADO);
				lancamentoPeriodicoRepository.update(pagamentoPeriodo.getLancamentoPeriodico());
			}
		}
	}

	public void gerarParcelas(LancamentoPeriodico lancamentoPeriodico) {
		
		LancamentoConta parcela;
		Calendar dataVencimento = Calendar.getInstance();
		dataVencimento.setTime(lancamentoPeriodico.getDataPrimeiraParcela());
				
		for (int i = 1; i <= lancamentoPeriodico.getTotalParcela(); i++) {
			parcela = new LancamentoConta();			
			parcela.setAno(dataVencimento.get(Calendar.YEAR));
			parcela.setLancamentoPeriodico(lancamentoPeriodico);
			parcela.setPeriodo(dataVencimento.get(Calendar.MONTH) + 1);
			parcela.setDataVencimento(dataVencimento.getTime());
			parcela.setParcela(i);
			
			// Setando os demais atributos
			parcela.setConta(lancamentoPeriodico.getConta());
			parcela.setDescricao(lancamentoPeriodico.getDescricao());
			parcela.setValorPago(lancamentoPeriodico.getValorParcela());
			parcela.setDataPagamento(parcela.getDataVencimento());
			parcela.setCategoria(lancamentoPeriodico.getCategoria());
			parcela.setFavorecido(lancamentoPeriodico.getFavorecido());
			parcela.setMeioPagamento(lancamentoPeriodico.getMeioPagamento());
			parcela.setMoeda(lancamentoPeriodico.getMoeda());
			if (parcela.getDataVencimento().before(new Date()))
				parcela.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
			else
				parcela.setStatusLancamentoConta(StatusLancamentoConta.AGENDADO);
			
			// Define a descrição definitiva do lançamento a ser criado
			parcela.setDescricao(lancamentoPeriodico.getDescricao() + " - Parcela " + parcela.getParcela() + " / " + lancamentoPeriodico.getTotalParcela() + ", vencimento para " + Util.formataDataHora(parcela.getDataVencimento(), Util.DATA)); 
			
			lancamentoContaRepository.save(parcela);
			dataVencimento.add(Calendar.MONTH, 1);
			dataVencimento.set(Calendar.DAY_OF_MONTH, lancamentoPeriodico.getDiaVencimento());
		}
	}
	
}