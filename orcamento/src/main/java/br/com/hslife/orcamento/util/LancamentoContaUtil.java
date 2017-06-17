/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a qualquer
    
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

package br.com.hslife.orcamento.util;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.model.AgrupamentoLancamento;

public final class LancamentoContaUtil {

	private LancamentoContaUtil() {
		// Classe estática e final. Não é possível herdar e instanciar
	}
	
	public static double calcularSaldoLancamentos(final List<LancamentoConta> lancamentos) {		
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
	
	public static double calcularSaldoLancamentosComConversao(final List<LancamentoConta> lancamentos) {		
		double total = 0.0;
		for (LancamentoConta l : lancamentos) {
			if (l.getTaxaConversao() != null) {
				// Usa a taxa de conversão para calcular no lugar do valor pago do lançamento
				if (l.getTipoLancamento().equals(TipoLancamento.RECEITA)) {
					total += l.getTaxaConversao().getValorMoedaDestino();
				} else {
					total -= l.getTaxaConversao().getValorMoedaDestino();
				}
			} else {
				// Usa o valor pago do lançamento
				if (l.getTipoLancamento().equals(TipoLancamento.RECEITA)) {
					total += l.getValorPago();
				} else {
					total -= l.getValorPago();
				}
			}
		}
		return Util.arredondar(total);
	}
	
	public static List<Categoria> organizarLancamentosPorCategoria(final List<LancamentoConta> lancamentos) {
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

	public static List<Favorecido> organizarLancamentosPorFavorecido(final List<LancamentoConta> lancamentos) {
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

	public static List<MeioPagamento> organizarLancamentosPorMeioPagamento(final List<LancamentoConta> lancamentos) {
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
	
	public static List<Moeda> organizarLancamentosPorMoeda(final List<LancamentoConta> lancamentos) {
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
	
	public static List<AgrupamentoLancamento> organizarLancamentosPorDebitoCredito(final List<LancamentoConta> lancamentos) {
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
}