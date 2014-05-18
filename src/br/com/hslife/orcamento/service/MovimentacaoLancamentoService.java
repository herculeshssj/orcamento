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
	public void excluirLancamentos(List<LancamentoConta> lancamentos, Map<String, Object> parametros) throws BusinessException {
		for (LancamentoConta l : lancamentos) {			
			lancamentoContaRepository.delete(l);
		}		
	}
	
	@Override
	public void copiarLancamentos(List<LancamentoConta> lancamentos, Map<String, Object> parametros) throws BusinessException {
		for (LancamentoConta lancamentoOrigem : lancamentos) {
			for (LancamentoConta lancamentoCopiado : lancamentoOrigem.clonarLancamentos(1, IncrementoClonagemLancamento.NENHUM)) {
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
				lancamentoContaRepository.save(lancamentoCopiado);
			}
		}
		
		/*
		// Instanciação das variáveis
		LancamentoConta lancamentoCopiado;
		List<LancamentoConta> lancamentosCopiados = new ArrayList<LancamentoConta>(); 
		FaturaCartao faturaAtual = new FaturaCartao();
		FaturaCartao faturaFutura = new FaturaCartao();
		
		// Pega o tipo de vinculação da fatura
		String vincularFatura = (String)parametros.get("VINCULAR_FATURA") == null ? "ATUAL" : (String)parametros.get("VINCULAR_FATURA");
		
		// Pega a conta que será setada na fatura
		Conta conta = parametros.get("CONTA_DESTINO") ==  null ? lancamentos.get(0).getConta() : (Conta)parametros.get("CONTA_DESTINO");
		
		// Verifica qual tipo de vinculação será usado e carrega a fatura correspondente
		if (vincularFatura.equalsIgnoreCase("ATUAL")) {
			faturaAtual = faturaCartaoRepository.findFaturaCartaoAberta(conta);
		} else if (vincularFatura.equalsIgnoreCase("FUTURA")) {			
			faturaFutura = faturaCartaoRepository.findNextFaturaCartaoFutura(conta);
			
			// Se não existir fatura futura uma nova é criada
			if (faturaFutura == null) {
				// Traz a fatura atual para determinar a data de vencimento da fatura futura
				faturaAtual = faturaCartaoRepository.findFaturaCartaoAberta(conta);
				
				// Instancia uma nova fatura futura
				faturaFutura = new FaturaCartao();
				
				// Preenche os atributos da fatura futura
				faturaFutura.setConta(conta);
				faturaFutura.setMoeda(moedaRepository.findDefaultByUsuario(conta.getUsuario()));
				faturaFutura.setStatusFaturaCartao(StatusFaturaCartao.FUTURA);
				
				// Data de vencimento da próxima fatura
				Calendar vencimento = Calendar.getInstance();
				vencimento.setTime(faturaAtual.getDataVencimento());		
				vencimento.add(Calendar.MONTH, 1);		
				faturaFutura.setDataVencimento(vencimento.getTime());
						
				// Salva a nova fatura
				faturaCartaoRepository.save(faturaFutura);
			}
		}
		
		// Realiza a cópia dos lançamentos		
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
			lancamentosCopiados.add(lancamentoCopiado);
		}
		
		// Determina o tipo de vinculação com a fatura, adiciona os lançamentos copiados e salva
		if (parametros.get("CONTA_DESTINO") != null && ((Conta)parametros.get("CONTA_DESTINO")).getTipoConta().equals(TipoConta.CARTAO)) {
			if (vincularFatura.equalsIgnoreCase("ATUAL")) {
				faturaAtual.getDetalheFatura().addAll(lancamentosCopiados);
				faturaCartaoRepository.update(faturaAtual);
			} else if (vincularFatura.equalsIgnoreCase("FUTURA")) {
				faturaFutura.getDetalheFatura().addAll(lancamentosCopiados);
				faturaCartaoRepository.update(faturaFutura);
			}
		}
		*/
	}
	
	@Override
	public void duplicarLancamentos(List<LancamentoConta> lancamentos, Map<String, Object> parametros) throws BusinessException {
		/*
		LancamentoConta lancamentoDuplicado;
		Integer quantADuplicar = (Integer)parametros.get("QUANT_DUPLICAR");		
		String incrementarData = parametros.get("INCREMENTAR_DATA") == null ? null : (String)parametros.get("INCREMENTAR_DATA");
		
		// Map que armazenará as faturas futuras. A chave é uma representação String da data de vencimento da fatura
		Map<String, FaturaCartao> faturasFuturas = new HashMap<String, FaturaCartao>();
		
		FaturaCartao faturaAtual = new FaturaCartao();
		FaturaCartao faturaFutura = new FaturaCartao();
		Date dataVencimentoFaturaAtual = new Date();
		
		// Pega o tipo de vinculação da fatura
		String vincularFatura = (String)parametros.get("VINCULAR_FATURA") == null ? "ATUAL" : (String)parametros.get("VINCULAR_FATURA");
		
		// Pega a conta que será setada na fatura
		Conta conta = parametros.get("CONTA_DESTINO") ==  null ? lancamentos.get(0).getConta() : (Conta)parametros.get("CONTA_DESTINO");
		
		// Verifica qual tipo de vinculação será usado e carrega a fatura correspondente
		if (vincularFatura.equalsIgnoreCase("ATUAL")) {
			faturaAtual = faturaCartaoRepository.findFaturaCartaoAberta(conta);
		} else if (vincularFatura.equalsIgnoreCase("FUTURA")) {			
			// Popula o Map com as fatura futuras encontradas
			for (FaturaCartao fc : faturaCartaoRepository.findAllByStatusFatura(conta, StatusFaturaCartao.FUTURA)) {
				faturasFuturas.put(Util.formataDataHora(fc.getDataVencimento(), Util.DATA), fc);
			}
			
			// Armazena a data de vencimento da fatura atual para uso futuro no método
			faturaAtual = faturaCartaoRepository.findFaturaCartaoAberta(conta);
			dataVencimentoFaturaAtual = faturaAtual.getDataVencimento();			
		}
	
		// Realiza a duplicação dos lançamentos
		for (LancamentoConta l : lancamentos) {
			
			// Prepara a data de vencimento da fatura
			Calendar tempFatura = Calendar.getInstance();
			if (conta.getTipoConta().equals(TipoConta.CARTAO)) {			
				tempFatura.setTime(dataVencimentoFaturaAtual);				
			}
			
			// Duplica os lançamentos incrementando por dia, mês ou ano
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

				// Prepara os contadores
				Calendar temp = Calendar.getInstance();				
				temp.setTime(lancamentoDuplicado.getDataPagamento());
				
				if (incrementarData != null && !incrementarData.isEmpty()) {
					
					if (incrementarData.equals("DIA")) {						
						temp.add(Calendar.DAY_OF_YEAR, i);
					}
					if (incrementarData.equals("MES")) {						
						temp.add(Calendar.MONTH, i);
						tempFatura.add(Calendar.MONTH, 1);
					}
					if (incrementarData.equals("ANO")) {						
						temp.add(Calendar.YEAR, i);
						tempFatura.add(Calendar.YEAR, 1);
					}
					lancamentoDuplicado.setDataPagamento(temp.getTime());
				} else {
					tempFatura.add(Calendar.MONTH, 1);
				}
				getRepository().save(lancamentoDuplicado);
				
				if (conta.getTipoConta().equals(TipoConta.CARTAO)) {
					// Adiciona o lançamento duplicado do Map, criando novas faturas caso não haja
					if (vincularFatura.equalsIgnoreCase("FUTURA")) {
						if (faturasFuturas.containsKey(Util.formataDataHora(tempFatura.getTime(), Util.DATA))) {
							faturasFuturas.get(Util.formataDataHora(tempFatura.getTime(), Util.DATA)).getDetalheFatura().add(lancamentoDuplicado);
						} else {
							// Instancia uma nova fatura futura
							faturaFutura = new FaturaCartao();
							
							// Preenche os atributos da fatura futura
							faturaFutura.setConta(conta);
							faturaFutura.setMoeda(moedaRepository.findDefaultByUsuario(conta.getUsuario()));
							faturaFutura.setStatusFaturaCartao(StatusFaturaCartao.FUTURA);
							faturaFutura.setDataVencimento(tempFatura.getTime());
							faturaFutura.getDetalheFatura().add(lancamentoDuplicado);
							
							faturasFuturas.put(Util.formataDataHora(tempFatura.getTime(), Util.DATA), faturaFutura);
						}
					} else if (vincularFatura.equalsIgnoreCase("ATUAL")) {
						faturaAtual.getDetalheFatura().add(lancamentoDuplicado);
					}
				}
			}
			
		}
		
		// Salva as faturas
		if (vincularFatura.equalsIgnoreCase("ATUAL")) {
			faturaCartaoRepository.update(faturaAtual);
		} else if (vincularFatura.equalsIgnoreCase("FUTURA")) {
			for (FaturaCartao fc : faturasFuturas.values()) {
				if (fc.getId() == null) {
					faturaCartaoRepository.save(fc);
				} else {
					faturaCartaoRepository.update(fc);
				}
			}
			
		}
		*/
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
		
		lancamentoContaRepository.save(lancamentoOrigem);
		lancamentoContaRepository.save(lancamentoDestino);
	}
	
}