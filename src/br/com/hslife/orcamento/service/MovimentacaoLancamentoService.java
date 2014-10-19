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

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.enumeration.IncrementoClonagemLancamento;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IMovimentacaoLancamento;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;

@Service("movimentacaoLancamentoService")
public class MovimentacaoLancamentoService implements IMovimentacaoLancamento {
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Override
	public void moverLancamentos(List<LancamentoConta> lancamentos, Conta conta) throws BusinessException {
		for (LancamentoConta l : lancamentos) {
			l.setConta(conta);
			lancamentoContaRepository.update(l);
		}
	}
	
	@Override
	public void excluirLancamentos(List<LancamentoConta> lancamentos) throws BusinessException {
		for (LancamentoConta l : lancamentos) {			
			lancamentoContaRepository.delete(l);
		}		
	}
	
	public void duplicarLancamentos(List<LancamentoConta> lancamentos, Conta conta, int quantidade, IncrementoClonagemLancamento incremento) throws BusinessException {
		for (LancamentoConta lancamentoOrigem : lancamentos) {
			for (LancamentoConta lancamentoCopiado : lancamentoOrigem.clonarLancamentos(quantidade, incremento)) {
				lancamentoCopiado.setConta(conta);
				lancamentoContaRepository.save(lancamentoCopiado);
			}
		}
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
			
			lancamentoContaRepository.update(lancamento);
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
		
		lancamentoContaRepository.save(lancamentoOrigem);
		lancamentoContaRepository.save(lancamentoDestino);
	}	
}