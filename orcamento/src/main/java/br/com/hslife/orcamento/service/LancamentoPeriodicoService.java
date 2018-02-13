/***

Copyright (c) 2012 - 2021 Hércules S. S. José

	Este arquivo é parte do programa Orçamento Doméstico.


	Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

	modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

	publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

	Licença.


	Este programa é distribuído na esperança que possa ser útil, mas SEM

	NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer

	MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor

	GNU em português para maiores detalhes.


	Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob

	o nome de "LICENSE" junto com este programa, se não, acesse o site do

	projeto no endereco https://github.com/herculeshssj/orcamento ou escreva

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth

	Floor, Boston, MA  02110-1301, USA.


	Para mais informações sobre o programa Orçamento Doméstico e seu autor

	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

	para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.EntityPersistence;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.TaxaConversao;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ILancamentoPeriodico;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoPeriodicoRepository;
import br.com.hslife.orcamento.util.Util;

@Service("lancamentoPeriodicoService")
public class LancamentoPeriodicoService extends AbstractCRUDService<LancamentoPeriodico> implements ILancamentoPeriodico {

	@Autowired
	private LancamentoPeriodicoRepository repository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private FechamentoPeriodoService fechamentoPeriodoService;

	public LancamentoPeriodicoRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}
	
	public LancamentoContaRepository getLancamentoContaRepository() {
		this.lancamentoContaRepository.setSessionFactory(this.sessionFactory);
		return lancamentoContaRepository;
	}

	public FechamentoPeriodoService getFechamentoPeriodoService() {
		return fechamentoPeriodoService;
	}

	@Override
	public void validar(LancamentoPeriodico entity) {
		if (!entity.getConta().getTipoConta().equals(TipoConta.CARTAO)) {
			if (!entity.getMoeda().equals(entity.getConta().getMoeda())) {
				throw new BusinessException("A moeda do lançamento deve ser igual a moeda da conta!");
			}
		}
	}
	
	@Override
	public void cadastrar(LancamentoPeriodico entity) {
		super.cadastrar(entity);
		if (entity.getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.FIXO)) {
			gerarMensalidade(entity);
		} else {
			this.gerarParcelas(entity);
		}
	}
	
	@Override
	public void excluir(LancamentoPeriodico entity) {
		List<LancamentoConta> pagamentos = lancamentoContaRepository.findPagosByLancamentoPeriodico(entity);
		if (pagamentos != null) {
			if (pagamentos.size() != 0) {
				throw new BusinessException("Não é possível excluir! Existem pagamentos registrados!");
			}
		}
		// Exclui o lançamento e todos os seus pagamentos
		super.excluir(entity);
	}
	
	@Override
	public void alterarStatusLancamento(LancamentoPeriodico entity, StatusLancamento novoStatus) {
		entity.setStatusLancamento(novoStatus);
		getRepository().update(entity);
	}
	
	@Override
	public void registrarPagamento(LancamentoConta pagamentoPeriodo) {	
		getFechamentoPeriodoService().registrarPagamento(pagamentoPeriodo);
	}
	
	public void gerarMensalidade(LancamentoPeriodico entity) {
		LancamentoConta proximaMensalidade = new LancamentoConta();
		LancamentoPeriodico lancamentoPeriodico = getRepository().findById(entity.getId());
		proximaMensalidade.setLancamentoPeriodico(lancamentoPeriodico);
		
		Calendar dataVencimento = Calendar.getInstance();
		
		if (dataVencimento.get(Calendar.DAY_OF_MONTH) >= lancamentoPeriodico.getDiaVencimento()) {
			switch (lancamentoPeriodico.getPeriodoLancamento()) {
				case MENSAL : dataVencimento.add(Calendar.MONTH, 1); break;
				case BIMESTRAL : dataVencimento.add(Calendar.MONTH, 2); break;
				case TRIMESTRAL : dataVencimento.add(Calendar.MONTH, 3); break;
				case QUADRIMESTRAL : dataVencimento.add(Calendar.MONTH, 4); break;
				case SEMESTRAL : dataVencimento.add(Calendar.MONTH, 6); break;
				case ANUAL : dataVencimento.add(Calendar.YEAR, 1); break;
				default : throw new BusinessException("Período informado é inválido!");
			}
		}
		
		dataVencimento.set(Calendar.DAY_OF_MONTH, lancamentoPeriodico.getDiaVencimento());
		
		proximaMensalidade.setAno(dataVencimento.get(Calendar.YEAR));
		proximaMensalidade.setPeriodo(dataVencimento.get(Calendar.MONTH) + 1);
		proximaMensalidade.setDataVencimento(dataVencimento.getTime());
		
		// Setando os demais atributos
		proximaMensalidade.setConta(lancamentoPeriodico.getConta());
		proximaMensalidade.setTipoLancamento(lancamentoPeriodico.getTipoLancamento());
		proximaMensalidade.setValorPago(lancamentoPeriodico.getValorParcela());
		proximaMensalidade.setDataPagamento(proximaMensalidade.getDataVencimento());
		proximaMensalidade.setCategoria(lancamentoPeriodico.getCategoria());
		proximaMensalidade.setFavorecido(lancamentoPeriodico.getFavorecido());
		proximaMensalidade.setMeioPagamento(lancamentoPeriodico.getMeioPagamento());
		proximaMensalidade.setMoeda(lancamentoPeriodico.getMoeda());
		if (proximaMensalidade.getDataVencimento().before(new Date()))
			proximaMensalidade.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
		else
			proximaMensalidade.setStatusLancamentoConta(StatusLancamentoConta.AGENDADO);
		
		// Define a descrição definitiva do lançamento a ser criado
		proximaMensalidade.setDescricao(lancamentoPeriodico.getDescricao() + " - Período " + proximaMensalidade.getPeriodo() + " / " + proximaMensalidade.getAno() + ", vencimento para " + Util.formataDataHora(proximaMensalidade.getDataVencimento(), Util.DATA));
		
		// Define a taxa de conversão
		if (proximaMensalidade.getConta().getTipoConta().equals(TipoConta.CARTAO)) {
			proximaMensalidade.setTaxaConversao(new TaxaConversao(proximaMensalidade.getMoeda(), 
					proximaMensalidade.getValorPago(), 
					proximaMensalidade.getConta().getMoeda(), 
					proximaMensalidade.getMoeda().getValorConversao()));
		}
		
		lancamentoContaRepository.save(proximaMensalidade);		
		
	}
	
	@Override
	public void mesclarLancamentos(LancamentoConta pagamentoPeriodo, LancamentoConta lancamentoAMesclar) {
		// Atribui a lançamento da conta as informações da parcela/mensalidade. Logo após apaga a mensalidade/parcela a mais.
		lancamentoAMesclar.setLancamentoPeriodico(pagamentoPeriodo.getLancamentoPeriodico());
		lancamentoAMesclar.setAno(pagamentoPeriodo.getAno());
		lancamentoAMesclar.setDataVencimento(pagamentoPeriodo.getDataVencimento());
		lancamentoAMesclar.setParcela(pagamentoPeriodo.getParcela());
		lancamentoAMesclar.setPeriodo(pagamentoPeriodo.getPeriodo());
		
		lancamentoContaRepository.update(lancamentoAMesclar);
		
		pagamentoPeriodo.setLancamentoPeriodico(null);
		lancamentoContaRepository.update(pagamentoPeriodo);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void vincularLancamentos(LancamentoPeriodico lancamentoPeriodico, List<LancamentoConta> lancamentosAVincular) {
		// Atribui aos lançamentos selecionados o lançamento periódico informado. O ano, período e vencimento é extraído da data de pagamento.
		for (LancamentoConta l : lancamentosAVincular) {
			l.setLancamentoPeriodico(lancamentoPeriodico);
			l.setPeriodo(l.getDataPagamento().getMonth() + 1);
			l.setAno(l.getDataPagamento().getYear() + 1900);
			l.setDataVencimento(l.getDataPagamento());
			lancamentoContaRepository.update(l);
		}
	}
	
	@Override
	public void removerLancamentos(List<LancamentoConta> lancamentosARemover) {
		// Remove os lançamentos selecionados do lançamento periódico informado.
		for (LancamentoConta l : lancamentosARemover) {
			l.setLancamentoPeriodico(null);
			lancamentoContaRepository.update(l);
		}		
	}
	
	@Override
	public List<LancamentoPeriodico> buscarPorTipoLancamentoContaEStatusLancamento(TipoLancamentoPeriodico tipo, Conta conta, StatusLancamento statusLancamento) {
		return getRepository().findByTipoLancamentoContaAndStatusLancamento(tipo, conta, statusLancamento);
	}
	
	@Override
	public List<LancamentoPeriodico> buscarPorTipoLancamentoEStatusLancamentoPorUsuario(TipoLancamentoPeriodico tipo, StatusLancamento status, Usuario usuario) {
		return getRepository().findByTipoLancamentoAndStatusLancamentoByUsuario(tipo, status, usuario);
	}
	
	@Override
	public List<LancamentoPeriodico> buscarPorTipoLancamentoETipoContaEStatusLancamento(TipoLancamentoPeriodico tipo, TipoConta tipoConta, StatusLancamento statusLancamento) {
		return getRepository().findByTipoLancamentoAndTipoContaAndStatusLancamento(tipo, tipoConta, statusLancamento);
	}
	
	@Override
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
			
			// Define a taxa de conversão
			if (parcela.getConta().getTipoConta().equals(TipoConta.CARTAO)) {
				parcela.setTaxaConversao(new TaxaConversao(parcela.getMoeda(), 
						parcela.getValorPago(), 
						parcela.getConta().getMoeda(), 
						parcela.getMoeda().getValorConversao()));
			}
			
			getLancamentoContaRepository().save(parcela);
			dataVencimento.add(Calendar.MONTH, 1);
			dataVencimento.set(Calendar.DAY_OF_MONTH, lancamentoPeriodico.getDiaVencimento());
		}
	}
	
	@Override
	public List<LancamentoPeriodico> buscarDescricaoEDataAquisicaoPorUsuario(String descricao, Date dataInicio,	Date dataFim, Usuario usuario) {
		return getRepository().findDescricaoAndDataAquisicaoByUsuario(descricao, dataInicio, dataFim, usuario);
	}
	
	 @Override
	public void reclassificarLancamento(LancamentoPeriodico entity, Map<String, ? super EntityPersistence> parametros) {
		// Busca o lançamento periódico e os lançamentos da conta vinculados a ele
		 LancamentoPeriodico lancamentoPeriodico = getRepository().findById(entity.getId());
		 
		 Categoria categoria = parametros.get("CATEGORIA") == null ? null : (Categoria)parametros.get("CATEGORIA");
		 Favorecido favorecido = parametros.get("FAVORECIDO") == null ? null : (Favorecido)parametros.get("FAVORECIDO");
		 MeioPagamento meioPagamento = parametros.get("MEIOPAGAMENTO") ==  null ? null : (MeioPagamento)parametros.get("MEIOPAGAMENTO");
		 
		 // Altera as propriedades do lançamento periódico
		 if (categoria != null)
			 lancamentoPeriodico.setCategoria(categoria);
		 if (favorecido != null) 
			 lancamentoPeriodico.setFavorecido(favorecido);
		 if (meioPagamento != null)
			 lancamentoPeriodico.setMeioPagamento(meioPagamento);
		 
		 // Salva o lançamento periódico
		 getRepository().update(lancamentoPeriodico);
		 
		 // Itera a lista de lançamentos da conta vinculados para alterar as propriedades
		 for (LancamentoConta lancamentoConta : lancamentoPeriodico.getPagamentos()) {
			 // Busca cada lançamento e altera suas propriedades
			 LancamentoConta l = getLancamentoContaRepository().findById(lancamentoConta.getId());
			 
			 // Altera as propriedades do lançamento da conta
			 if (categoria != null)
				 l.setCategoria(categoria);
			 if (favorecido != null) 
				 l.setFavorecido(favorecido);
			 if (meioPagamento != null)
				 l.setMeioPagamento(meioPagamento);
			 
			 // Atualiza o lançamento
			 getLancamentoContaRepository().update(l);
		 }
	}
}
