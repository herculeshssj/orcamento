/***
  
  	Copyright (c) 2012 - 2016 Hércules S. S. José

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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.enumeration.IncrementoClonagemLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IMovimentacaoLancamento;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.util.LancamentoContaUtil;
import br.com.hslife.orcamento.util.Util;

@Service("movimentacaoLancamentoService")
@Transactional(propagation=Propagation.REQUIRED, rollbackFor={BusinessException.class})
public class MovimentacaoLancamentoService implements IMovimentacaoLancamento {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private LancamentoContaRepository repository;
	
	public LancamentoContaRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}

	@Override
	public void moverLancamentos(List<LancamentoConta> lancamentos, Conta conta) throws BusinessException {
		for (LancamentoConta l : lancamentos) {
			l.setConta(conta);
			getRepository().update(l);
		}
	}
	
	@Override
	public void excluirLancamentos(List<LancamentoConta> lancamentos) throws BusinessException {
		for (LancamentoConta l : lancamentos) {			
			getRepository().delete(l);
		}		
	}
	
	@Override
	public void removerVinculos(List<LancamentoConta> lancamentos) throws BusinessException {
		for (LancamentoConta l : lancamentos) {
			l.setHashImportacao(null);
			getRepository().update(l);
		}
		
	}
	
	public void duplicarLancamentos(List<LancamentoConta> lancamentos, Conta conta, int quantidade, IncrementoClonagemLancamento incremento) throws BusinessException {
		for (LancamentoConta lancamentoOrigem : lancamentos) {
			for (LancamentoConta lancamentoCopiado : lancamentoOrigem.clonarLancamentos(quantidade, incremento)) {
				lancamentoCopiado.setConta(conta);
				getRepository().save(lancamentoCopiado);
			}
		}
	}
	
	public void dividirLancamento(LancamentoConta lancamento, int quantidade) throws BusinessException {
		double valorDividido = Util.arredondar(lancamento.getValorPago() / quantidade);
		List<LancamentoConta> lancamentosDivididos = lancamento.clonarLancamentos(quantidade, IncrementoClonagemLancamento.NENHUM);
		for (LancamentoConta l : lancamentosDivididos) {
			l.setValorPago(valorDividido);
			getRepository().save(l);
		}
		getRepository().delete(lancamento);
	}
	
	@Override
	public void alterarPropriedades(List<LancamentoConta> lancamentos, Map<String, Object> parametros) throws BusinessException {
		for (LancamentoConta lancamento : lancamentos) {
			if (parametros.get("DESCRICAO_DESTINO") != null && !((String)parametros.get("DESCRICAO_DESTINO")).trim().isEmpty()) {
				lancamento.setDescricao((String)parametros.get("DESCRICAO_DESTINO"));
			}
			if (parametros.get("OBSERVACAO_DESTINO") != null && !((String)parametros.get("OBSERVACAO_DESTINO")).trim().isEmpty()) {
				lancamento.setObservacao((String)parametros.get("OBSERVACAO_DESTINO"));
			}
			if (parametros.get("CATEGORIA_DESTINO") != null) { 
				if ( ((Categoria)parametros.get("CATEGORIA_DESTINO")).getTipoCategoria().equals(TipoCategoria.CREDITO) ) {
					lancamento.setTipoLancamento(TipoLancamento.RECEITA);
				} else {
					lancamento.setTipoLancamento(TipoLancamento.DESPESA);
				}
				lancamento.setCategoria((Categoria)parametros.get("CATEGORIA_DESTINO"));
			}
			if (parametros.get("FAVORECIDO_DESTINO") != null) 
				lancamento.setFavorecido((Favorecido)parametros.get("FAVORECIDO_DESTINO"));
			if (parametros.get("MEIOPAGAMENTO_DESTINO") != null) 
				lancamento.setMeioPagamento((MeioPagamento)parametros.get("MEIOPAGAMENTO_DESTINO"));
			
			getRepository().update(lancamento);
		}
		
	}
	
	@Override
	public void mesclarLancamento(List<LancamentoConta> lancamentos, Map<String, Object> parametros) throws BusinessException {
		LancamentoConta lancamentoMesclado = new LancamentoConta();
		lancamentoMesclado.setConta((Conta)parametros.get("CONTA_DESTINO"));
		lancamentoMesclado.setTipoLancamento((TipoLancamento)parametros.get("TIPO_LANCAMENTO"));
		lancamentoMesclado.setMoeda(lancamentoMesclado.getConta().getMoeda());
		lancamentoMesclado.setDescricao((String)parametros.get("DESCRICAO_DESTINO"));
		lancamentoMesclado.setDataPagamento((Date)parametros.get("DATAPAGAMENTO"));
		
		lancamentoMesclado.setCategoria(parametros.get("CATEGORIA_DESTINO") == null ? null : (Categoria)parametros.get("CATEGORIA_DESTINO"));
		lancamentoMesclado.setFavorecido(parametros.get("FAVORECIDO_DESTINO") == null ? null : (Favorecido)parametros.get("FAVORECIDO_DESTINO"));
		lancamentoMesclado.setMeioPagamento(parametros.get("MEIOPAGAMENTO_DESTINO") == null ? null : (MeioPagamento)parametros.get("MEIOPAGAMENTO_DESTINO"));
		lancamentoMesclado.setObservacao(parametros.get("OBSERVACAO_DESTINO") == null ? null : (String)parametros.get("OBSERVACAO_DESTINO"));
		
		if (lancamentoMesclado.getDataPagamento().after(new Date())) {
			lancamentoMesclado.setStatusLancamentoConta(StatusLancamentoConta.AGENDADO);
		} else {
			lancamentoMesclado.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
		}
		
		double valorPago = LancamentoContaUtil.calcularSaldoLancamentos(lancamentos);
		
		lancamentoMesclado.setValorPago(valorPago);
		
		getRepository().save(lancamentoMesclado);
		
		for (LancamentoConta l : lancamentos) {
			getRepository().delete(l);
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
		} else {
			throw new BusinessException("Conta de origem e/ou destino não informada(s)!");
		}
		
		LancamentoConta lancamentoOrigem = new LancamentoConta(lancamentoATransferir);
		LancamentoConta lancamentoDestino = new LancamentoConta(lancamentoATransferir);
		
		lancamentoOrigem.setConta(contaOrigem);
		lancamentoOrigem.setMoeda(contaOrigem.getMoeda());
		lancamentoOrigem.setTipoLancamento(TipoLancamento.DESPESA);
		lancamentoOrigem.setCategoria(parametros.get("CATEGORIA_ORIGEM") == null ? null : (Categoria)parametros.get("CATEGORIA_ORIGEM"));
		lancamentoOrigem.setFavorecido(parametros.get("FAVORECIDO_DESTINO") == null ? null : (Favorecido)parametros.get("FAVORECIDO_DESTINO"));
		lancamentoOrigem.setMeioPagamento(parametros.get("MEIOPAGAMENTO_DESTINO") == null ? null : (MeioPagamento)parametros.get("MEIOPAGAMENTO_DESTINO"));
		
		lancamentoDestino.setConta(contaDestino);
		lancamentoDestino.setMoeda(contaDestino.getMoeda());
		lancamentoDestino.setTipoLancamento(TipoLancamento.RECEITA);
		lancamentoDestino.setCategoria(parametros.get("CATEGORIA_DESTINO") == null ? null : (Categoria)parametros.get("CATEGORIA_DESTINO"));
		lancamentoDestino.setFavorecido(parametros.get("FAVORECIDO_DESTINO") == null ? null : (Favorecido)parametros.get("FAVORECIDO_DESTINO"));
		lancamentoDestino.setMeioPagamento(parametros.get("MEIOPAGAMENTO_DESTINO") == null ? null : (MeioPagamento)parametros.get("MEIOPAGAMENTO_DESTINO"));
		
		getRepository().save(lancamentoOrigem);
		getRepository().save(lancamentoDestino);
	}
	
	@Override
	public void salvarDetalhamentoLancamento(LancamentoConta lancamento) throws BusinessException {
		getRepository().update(lancamento);		
	}
}