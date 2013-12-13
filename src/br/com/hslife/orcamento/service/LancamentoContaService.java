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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.component.ContaComponent;
import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.model.AgrupamentoLancamento;
import br.com.hslife.orcamento.model.CriterioLancamentoConta;
import br.com.hslife.orcamento.repository.FechamentoPeriodoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoImportadoRepository;
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
	
	@Override
	public void alterar(LancamentoConta entity) throws BusinessException {
		if (entity.getLancamentoImportado() != null) {
			entity.setDataPagamento(entity.getLancamentoImportado().getData());
			entity.setHistorico(entity.getLancamentoImportado().getHistorico());
			entity.setNumeroDocumento(entity.getLancamentoImportado().getDocumento());
			entity.setValorPago(Math.abs(entity.getLancamentoImportado().getValor()));
			entity.setHashImportacao(entity.getLancamentoImportado().getHash());
			
			if (entity.getLancamentoImportado().getData().after(new Date())) {
				entity.setAgendado(true);
			} else {
				entity.setAgendado(false);
			}
			
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
	public List<LancamentoConta> buscarPorCriterioLancamentoConta(CriterioLancamentoConta criterio) throws BusinessException {
		return getComponent().buscarPorCriterioLancamentoConta(criterio);
	}

	@Override
	public void validar(LancamentoConta entity) throws BusinessException {
		if (entity.getConta().getTipoConta().equals(TipoConta.CARTAO)) {
			if (entity.getDataLancamento() == null)
				throw new BusinessException("Informe a data de lançamento!");
		} else {
			if (entity.getDataPagamento() == null)
				throw new BusinessException("Informe a data de pagamento!");
		}
		
		if (!entity.getConta().getTipoConta().equals(TipoConta.CARTAO)) {
			if (entity.getDataPagamento().before(entity.getConta().getDataAbertura())) {
				throw new BusinessException("Data de lançamento deve ser posterior a data de abertura da conta!");
			}
			if (entity.getConta().getDataFechamento() != null && entity.getDataPagamento().after(entity.getConta().getDataFechamento())) {
				throw new BusinessException("Data de lançamento deve ser anterior a data de fechamento da conta!");
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
		List<Categoria> categorias = new ArrayList<Categoria>();
		
		/* Usa-se o Set para separar as categorias da listagem de lançamentos */
		Set<Categoria> setCategorias = new HashSet<Categoria>();
		
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
		
		// Cria um favorecido a mais para os lançamentos que não tem favorecido atribuídos
		Categoria c = new Categoria();
		c.setDescricao("Sem categoria");
		c.setLancamentos(lancamentosSemCategoria);
		c.setSaldoPago(saldoLancamentosSemCategoria);
		categorias.add(c);
		
		return categorias;
	}

	@Override
	public List<Favorecido> organizarLancamentosPorFavorecido(List<LancamentoConta> lancamentos) throws BusinessException {
		List<Favorecido> favorecidos = new ArrayList<Favorecido>();
		
		/* Usa-se o Set para separar os favorecidos da listagem de lançamentos */
		Set<Favorecido> setFavorecidos = new HashSet<Favorecido>();
		
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
		
		// Varre a lista de lançamentos para adicionar os lançamentos nos respectivos favorecidos
		for (LancamentoConta l : lancamentos) {
			
			for (int i = 0; i < favorecidos.size(); i++) {
				if (l.getFavorecido() != null && favorecidos.get(i).getId().equals(l.getFavorecido().getId())) {
					favorecidos.get(i).getLancamentos().add(l);
					if (l.getTipoLancamento() == TipoLancamento.RECEITA) {						
						favorecidos.get(i).setSaldoPago(favorecidos.get(i).getSaldoPago() + l.getValorPago());
					} else {						
						favorecidos.get(i).setSaldoPago(favorecidos.get(i).getSaldoPago() - l.getValorPago());
					}						
				}
			}
			if (l.getFavorecido() == null) {
				lancamentosSemFavorecido.add(l);
				if (l.getTipoLancamento() == TipoLancamento.RECEITA) {						
					saldoLancamentosSemFavorecido += l.getValorPago();
				} else {						
					saldoLancamentosSemFavorecido -= l.getValorPago();
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
			}
			indice--;
		}
		
		// Cria um favorecido a mais para os lançamentos que não tem favorecido atribuídos
		Favorecido f = new Favorecido();
		f.setNome("Sem favorecido/sacado");
		f.setLancamentos(lancamentosSemFavorecido);
		f.setSaldoPago(saldoLancamentosSemFavorecido);
		favorecidos.add(f);
			
		return favorecidos;
	}

	@Override
	public List<MeioPagamento> organizarLancamentosPorMeioPagamento(List<LancamentoConta> lancamentos) throws BusinessException {
		List<MeioPagamento> meiosPagamento = new ArrayList<MeioPagamento>();
		
		/* Usa-se o Set para separar os meios de pagamento da listagem de lançamentos */
		Set<MeioPagamento> setMeiosPagamento = new HashSet<MeioPagamento>();
		
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
		
		// Varre a lista de lançamentos para adicionar os lançamentos nos respectivos meios de pagamento
		for (LancamentoConta l : lancamentos) {
			
			for (int i = 0; i < meiosPagamento.size(); i++) {
				if (l.getMeioPagamento() != null && meiosPagamento.get(i).getId().equals(l.getMeioPagamento().getId())) {
					meiosPagamento.get(i).getLancamentos().add(l);
					if (l.getTipoLancamento() == TipoLancamento.RECEITA) {						
						meiosPagamento.get(i).setSaldoPago(meiosPagamento.get(i).getSaldoPago() + l.getValorPago());
					} else {						
						meiosPagamento.get(i).setSaldoPago(meiosPagamento.get(i).getSaldoPago() - l.getValorPago());
					}						
				}
			}
			if (l.getMeioPagamento() == null) {
				lancamentosSemMeioPagamento.add(l);
				if (l.getTipoLancamento() == TipoLancamento.RECEITA) {						
					saldoLancamentosSemMeioPagamento += l.getValorPago();
				} else {						
					saldoLancamentosSemMeioPagamento -= l.getValorPago();
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
			}
			indice--;
		}
		
		// Cria um meio de pagamento a mais para os lançamentos que não tem meios de pagamento atribuídos
		MeioPagamento m = new MeioPagamento();
		m.setDescricao("Sem meio de pagamento");
		m.setLancamentos(lancamentosSemMeioPagamento);
		m.setSaldoPago(saldoLancamentosSemMeioPagamento);
		meiosPagamento.add(m);
			
		return meiosPagamento;
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
	public void moverLancamentos(List<LancamentoConta> lancamentos, Map<String, Object> parametros) throws BusinessException {
		for (LancamentoConta l : lancamentos) {
			l.setConta((Conta)parametros.get("CONTA_DESTINO"));
			if (parametros.get("CATEGORIA_DESTINO") != null) { 
				if ( ((Categoria)parametros.get("CATEGORIA_DESTINO")).getTipoCategoria().equals(TipoCategoria.CREDITO) ) {
					l.setTipoLancamento(TipoLancamento.RECEITA);
				} else {
					l.setTipoLancamento(TipoLancamento.DESPESA);
				}
				l.setCategoria((Categoria)parametros.get("CATEGORIA_DESTINO"));
			}
			if (parametros.get("FAVORECIDO_DESTINO") != null) 
				l.setFavorecido((Favorecido)parametros.get("FAVORECIDO_DESTINO"));
			if (parametros.get("MEIOPAGAMENTO_DESTINO") != null) 
				l.setMeioPagamento((MeioPagamento)parametros.get("MEIOPAGAMENTO_DESTINO"));
			getRepository().update(l);
		}
	}
	
	@Override
	public void excluirLancamentos(List<LancamentoConta> lancamentos, Map<String, Object> parametros) throws BusinessException {
		for (LancamentoConta l : lancamentos) {			
			getRepository().delete(l);
		}		
	}
	
	@Override
	public void copiarLancamentos(List<LancamentoConta> lancamentos, Map<String, Object> parametros) throws BusinessException {
		LancamentoConta lancamentoCopiado;
		for (LancamentoConta l : lancamentos) {
			lancamentoCopiado = new LancamentoConta(l);
			if (parametros.get("CONTA_DESTINO") != null)
				lancamentoCopiado.setConta((Conta)parametros.get("CONTA_DESTINO"));
			if (parametros.get("CATEGORIA_DESTINO") != null) { 
				if ( ((Categoria)parametros.get("CATEGORIA_DESTINO")).getTipoCategoria().equals(TipoCategoria.CREDITO) ) {
					lancamentoCopiado.setTipoLancamento(TipoLancamento.RECEITA);
				} else {
					lancamentoCopiado.setTipoLancamento(TipoLancamento.DESPESA);
				}
				lancamentoCopiado.setCategoria((Categoria)parametros.get("CATEGORIA_DESTINO"));
			}
			if (parametros.get("FAVORECIDO_DESTINO") != null) 
				lancamentoCopiado.setFavorecido((Favorecido)parametros.get("FAVORECIDO_DESTINO"));
			if (parametros.get("MEIOPAGAMENTO_DESTINO") != null) 
				lancamentoCopiado.setMeioPagamento((MeioPagamento)parametros.get("MEIOPAGAMENTO_DESTINO"));
			getRepository().save(lancamentoCopiado);
		}
	}
	
	@Override
	public void duplicarLancamentos(List<LancamentoConta> lancamentos, Map<String, Object> parametros) throws BusinessException {
		LancamentoConta lancamentoDuplicado;
		Integer quantADuplicar = (Integer)parametros.get("QUANT_DUPLICAR");		
		String incrementarData = parametros.get("INCREMENTAR_DATA") == null ? null : (String)parametros.get("INCREMENTAR_DATA");
		for (LancamentoConta l : lancamentos) {
			for (int i = 1; i <= quantADuplicar; i++) {
				lancamentoDuplicado = new LancamentoConta(l);
				if (parametros.get("CONTA_DESTINO") != null)
					lancamentoDuplicado.setConta((Conta)parametros.get("CONTA_DESTINO"));
				if (parametros.get("CATEGORIA_DESTINO") != null) { 
					if ( ((Categoria)parametros.get("CATEGORIA_DESTINO")).getTipoCategoria().equals(TipoCategoria.CREDITO) ) {
						lancamentoDuplicado.setTipoLancamento(TipoLancamento.RECEITA);
					} else {
						lancamentoDuplicado.setTipoLancamento(TipoLancamento.DESPESA);
					}
					lancamentoDuplicado.setCategoria((Categoria)parametros.get("CATEGORIA_DESTINO"));
				}
				if (parametros.get("FAVORECIDO_DESTINO") != null) 
					lancamentoDuplicado.setFavorecido((Favorecido)parametros.get("FAVORECIDO_DESTINO"));
				if (parametros.get("MEIOPAGAMENTO_DESTINO") != null) 
					lancamentoDuplicado.setMeioPagamento((MeioPagamento)parametros.get("MEIOPAGAMENTO_DESTINO"));
				if (incrementarData != null) {
					Calendar temp = Calendar.getInstance();
					temp.setTime(lancamentoDuplicado.getDataPagamento());
					if (incrementarData.equals("DIA")) {						
						temp.add(Calendar.DAY_OF_YEAR, i);						
					}
					if (incrementarData.equals("MES")) {						
						temp.add(Calendar.MONTH, i);						
					}
					if (incrementarData.equals("ANO")) {						
						temp.add(Calendar.YEAR, i);						
					}
					lancamentoDuplicado.setDataPagamento(temp.getTime());
				}
				getRepository().save(lancamentoDuplicado);
			}
		}
	}
	
	@Override
	public void transferirLancamentos(LancamentoConta lancamentoATransferir, Map<String, Object> parametros) throws BusinessException {
		Conta contaOrigem = (Conta)parametros.get("CONTA_ORIGEM");
		Conta contaDestino = (Conta)parametros.get("CONTA_DESTINO");
				
		if (contaOrigem != null && contaDestino != null) {
			if (contaOrigem.equals(contaDestino)) {
				throw new BusinessException("Conta de destino não pode ser igual a conta de origem!");
			}
		}
		
		LancamentoConta lancamentoOrigem = new LancamentoConta(lancamentoATransferir);
		LancamentoConta lancamentoDestino = new LancamentoConta(lancamentoATransferir);
		
		lancamentoOrigem.setConta(contaOrigem);
		lancamentoOrigem.setTipoLancamento(TipoLancamento.DESPESA);
		lancamentoOrigem.setCategoria(parametros.get("CATEGORIA_ORIGEM") == null ? null : (Categoria)parametros.get("CATEGORIA_ORIGEM"));
		lancamentoOrigem.setFavorecido(parametros.get("FAVORECIDO_DESTINO") == null ? null : (Favorecido)parametros.get("FAVORECIDO_DESTINO"));
		lancamentoOrigem.setMeioPagamento(parametros.get("MEIOPAGAMENTO_DESTINO") == null ? null : (MeioPagamento)parametros.get("MEIOPAGAMENTO_DESTINO"));
		
		lancamentoDestino.setConta(contaDestino);
		lancamentoDestino.setTipoLancamento(TipoLancamento.RECEITA);
		lancamentoDestino.setCategoria(parametros.get("CATEGORIA_DESTINO") == null ? null : (Categoria)parametros.get("CATEGORIA_DESTINO"));
		lancamentoDestino.setFavorecido(parametros.get("FAVORECIDO_DESTINO") == null ? null : (Favorecido)parametros.get("FAVORECIDO_DESTINO"));
		lancamentoDestino.setMeioPagamento(parametros.get("MEIOPAGAMENTO_DESTINO") == null ? null : (MeioPagamento)parametros.get("MEIOPAGAMENTO_DESTINO"));
		
		getRepository().save(lancamentoOrigem);
		getRepository().save(lancamentoDestino);
	}
	
	@Override
	public List<LancamentoImportado> buscarLancamentoImportadoPorConta(Conta conta) throws BusinessException {
		return lancamentoImportadoRepository.findByConta(conta);
	}
}