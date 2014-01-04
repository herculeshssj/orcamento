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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.ofx4j.domain.data.MessageSetType;
import net.sf.ofx4j.domain.data.ResponseEnvelope;
import net.sf.ofx4j.domain.data.ResponseMessageSet;
import net.sf.ofx4j.domain.data.banking.BankStatementResponseTransaction;
import net.sf.ofx4j.domain.data.banking.BankingResponseMessageSet;
import net.sf.ofx4j.domain.data.common.Transaction;
import net.sf.ofx4j.domain.data.signon.SignonResponse;
import net.sf.ofx4j.io.AggregateUnmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IImportacaoLancamento;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoImportadoRepository;
import br.com.hslife.orcamento.util.Util;

@Service("importacaoLancamentoService")
public class ImportacaoLancamentoService implements IImportacaoLancamento {
	
	@Autowired
	private LancamentoImportadoRepository lancamentoImportadoRepository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;

	public void setLancamentoImportadoRepository(
			LancamentoImportadoRepository lancamentoImportadoRepository) {
		this.lancamentoImportadoRepository = lancamentoImportadoRepository;
	}

	public void setLancamentoContaRepository(
			LancamentoContaRepository lancamentoContaRepository) {
		this.lancamentoContaRepository = lancamentoContaRepository;
	}
	
	@Override
	public List<LancamentoImportado> buscarLancamentoImportadoPorConta(Conta conta) throws BusinessException {
		return lancamentoImportadoRepository.findByConta(conta);
	}
	
	@Override
	public void excluirLancamentoImportado(LancamentoImportado entity) throws BusinessException {
		lancamentoImportadoRepository.delete(entity);
	}
	
	public LancamentoImportado buscarPorID(Long id) throws BusinessException {
		return lancamentoImportadoRepository.findByID(id);
	}
	
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	public void  processarArquivoImportado(Arquivo arquivo, Conta conta) throws BusinessException {
		try {
			// Incluindo o código do projeto OFXImport na forma que está. Futuramente este código sofrerá refatoração (assim espero... :/ )
			
			AggregateUnmarshaller a = new AggregateUnmarshaller(ResponseEnvelope.class);
			ResponseEnvelope re = (ResponseEnvelope) a.unmarshal(new InputStreamReader(new ByteArrayInputStream(arquivo.getDados()), "ISO-8859-1"));
				
			//objeto contendo informações como instituição financeira, idioma, data da conta.
			SignonResponse sr = re.getSignonResponse();
	
			//como não existe esse get "BankStatementResponse bsr = re.getBankStatementResponse();"
			//fiz esse codigo para capturar a lista de transações
			MessageSetType type = MessageSetType.banking;
			ResponseMessageSet message = re.getMessageSet(type);
	
			if (message != null) {
				List<BankStatementResponseTransaction> bank = ((BankingResponseMessageSet) message).getStatementResponses();
			    for (BankStatementResponseTransaction b : bank) {
			    	System.out.println("bank ID: " + b.getMessage().getAccount().getBankId());
			    	System.out.println("cc: " + b.getMessage().getAccount().getAccountNumber());
			        System.out.println("ag: " + b.getMessage().getAccount().getBranchId());
			        System.out.println("tipo da conta: " + b.getMessage().getAccount().getAccountType());
			        System.out.println("balanço final: " + b.getMessage().getLedgerBalance().getAmount());
			        System.out.println("dataDoArquivo: " + b.getMessage().getLedgerBalance().getAsOfDate());
			        
			        /* Aqui começa meu código de validação de conta */
			        
			        if (!conta.getBanco().getNumero().equals(b.getMessage().getAccount().getBankId())) {		        	
			        	throw new BusinessException("Número do banco " + conta.getBanco().getNumero() + " não confere com do arquivo (" + b.getMessage().getAccount().getBankId() + ")!");
			        }
			        if (!conta.getAgencia().equals(b.getMessage().getAccount().getBranchId())) {		        	
			        	throw new BusinessException("Número da agência " + conta.getAgencia() + " não confere com do arquivo (" + b.getMessage().getAccount().getBranchId() + ")!");
			        }		        
			        if (!conta.getContaCorrente().equals(b.getMessage().getAccount().getAccountNumber())) {
			        	throw new BusinessException("Número da conta " + conta.getContaCorrente() + " não confere com do arquivo (" + b.getMessage().getAccount().getAccountNumber() + ")!");
			        }
			        if (!conta.getTipoConta().equals(TipoConta.CORRENTE)) {
			        	throw new BusinessException("Somente contas correntes são aceitas!");
			        }
			        
			        /* Término do código do meu código de validação de conta */
			        
			        List<Transaction> list = b.getMessage().getTransactionList().getTransactions();
			        System.out.println("TRANSAÇÕES\n");
			        for (Transaction transaction : list) {
			        	System.out.println("tipo: " + transaction.getTransactionType().name());
			            System.out.println("id: " + transaction.getId());
			            System.out.println("data: " + transaction.getDatePosted());
			            System.out.println("num. doc.: " + transaction.getReferenceNumber());
			            System.out.println("check number: " + transaction.getCheckNumber());
			            System.out.println("valor: " + transaction.getAmount());
			            System.out.println("descricao: " + transaction.getMemo());
			            System.out.println("");
			            
			            /* Aqui começa meu código */
			            
			            if (this.lancamentoImportadoRepository.findByHash(Util.MD5(transaction.getId())) == null) {
			            
			            	// Cria os lançamentos se a data do movimento for posterior à data de abertura da conta
			            	if (transaction.getDatePosted().after(conta.getDataAbertura())) {
			            		LancamentoImportado li = new LancamentoImportado();
			            		li.setConta(conta);
			            		li.setData(transaction.getDatePosted());
			            		li.setDocumento(transaction.getReferenceNumber());
			            		li.setHash(Util.MD5(transaction.getId()));
			            		li.setHistorico(transaction.getMemo());
			            		li.setValor(transaction.getAmount());
				            
			            		lancamentoImportadoRepository.save(li);
			            	}
			            }
			            
			            /* Fim do meu código */
			       }
			   }
			} 
		}catch (Exception e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public List<LancamentoConta> buscarLancamentoContaAAtualizar(List<LancamentoImportado> lancamentosImportados) throws BusinessException {
		List<LancamentoConta> lancamentos = new ArrayList<LancamentoConta>();
		for (LancamentoImportado li : lancamentosImportados) {
			if (lancamentoContaRepository.findByHash(li.getHash()) != null) {
				lancamentos.add(lancamentoContaRepository.findByHash(li.getHash()));
			}
		}
		return lancamentos;
	}

	@Override
	public List<LancamentoConta> gerarLancamentoContaAInserir(List<LancamentoImportado> lancamentosImportados) throws BusinessException {
		List<LancamentoConta> lancamentos = new ArrayList<LancamentoConta>();
		
		LancamentoConta lc;
		
		for (LancamentoImportado li : lancamentosImportados) {
			if (lancamentoContaRepository.findByHash(li.getHash()) == null) {
			
				lc = new LancamentoConta();
				
				if (li.getData().after(new Date())) {
					lc.setAgendado(true);
				} else {
					lc.setAgendado(false);
				}
				
				if (li.getValor() > 0) {
					lc.setTipoLancamento(TipoLancamento.RECEITA);
				} else {
					lc.setTipoLancamento(TipoLancamento.DESPESA);
				}
				
				lc.setConta(li.getConta());
				lc.setDataPagamento(li.getData());
				lc.setDescricao(li.getHistorico());
				lc.setHistorico(li.getHistorico());
				lc.setNumeroDocumento(li.getDocumento());
				lc.setValorPago(Math.abs(li.getValor()));
				lc.setHashImportacao(li.getHash());
				
				lancamentos.add(lc);
			
			}
		}
		return lancamentos;
	}

	@Override
	public void processarLancamentosImportados(Conta conta, boolean gerarNovosLancamentos) throws BusinessException {
		List<LancamentoConta> lancamentosAInserir = new ArrayList<LancamentoConta>();
		List<LancamentoConta> lancamentosAAtualizar = new ArrayList<LancamentoConta>(); 
		
		lancamentosAAtualizar = this.buscarLancamentoContaAAtualizar(this.buscarLancamentoImportadoPorConta(conta));
		lancamentosAInserir = this.gerarLancamentoContaAInserir(buscarLancamentoImportadoPorConta(conta));

		// Cria os novos lançamentos
		if (!gerarNovosLancamentos) {
			for (LancamentoConta l : lancamentosAInserir) {
				lancamentoContaRepository.save(l);
				
				// Exclui o lançamento importado
				lancamentoImportadoRepository.delete(lancamentoImportadoRepository.findByHash(l.getHashImportacao()));
			}
		}
		
		// Atualiza os lançamentos existentes
		LancamentoImportado li;
		for (LancamentoConta l : lancamentosAAtualizar) {
			li = lancamentoImportadoRepository.findByHash(l.getHashImportacao());
			
			l.setDataPagamento(li.getData());
			l.setHistorico(li.getHistorico());
			l.setNumeroDocumento(li.getDocumento());
			l.setValorPago(Math.abs(li.getValor()));
			
			if (li.getData().after(new Date())) {
				l.setAgendado(true);
			} else {
				l.setAgendado(false);
			}
			
			if (li.getValor() > 0) {
				l.setTipoLancamento(TipoLancamento.RECEITA);
			} else {
				l.setTipoLancamento(TipoLancamento.DESPESA);
			}
			
			lancamentoContaRepository.update(l);
			
			// Exclui o lançamento importado
			lancamentoImportadoRepository.delete(li);
		}
	}
	
	@Override
	public void importarLancamento(LancamentoImportado entity) throws BusinessException {
		LancamentoConta l = lancamentoContaRepository.findByHash(entity.getHash());
		if (l == null) {
			// Cria um novo lançamento
			l = new LancamentoConta();
			
			if (entity.getData().after(new Date())) {
				l.setAgendado(true);
			} else {
				l.setAgendado(false);
			}
			
			if (entity.getValor() > 0) {
				l.setTipoLancamento(TipoLancamento.RECEITA);
			} else {
				l.setTipoLancamento(TipoLancamento.DESPESA);
			}
			
			l.setConta(entity.getConta());
			l.setDataPagamento(entity.getData());
			l.setDescricao(entity.getHistorico());
			l.setHistorico(entity.getHistorico());
			l.setNumeroDocumento(entity.getDocumento());
			l.setValorPago(Math.abs(entity.getValor()));
			l.setHashImportacao(entity.getHash());
			
			// Salva o lançamento
			lancamentoContaRepository.save(l);
			
			// Exclui o lançamento importado
			lancamentoImportadoRepository.delete(entity);
		} else {
			// Atualiza o lançamento existente
			
			l.setDataPagamento(entity.getData());
			l.setHistorico(entity.getHistorico());
			l.setNumeroDocumento(entity.getDocumento());
			l.setValorPago(Math.abs(entity.getValor()));
			
			if (entity.getData().after(new Date())) {
				l.setAgendado(true);
			} else {
				l.setAgendado(false);
			}
			
			if (entity.getValor() > 0) {
				l.setTipoLancamento(TipoLancamento.RECEITA);
			} else {
				l.setTipoLancamento(TipoLancamento.DESPESA);
			}
			
			lancamentoContaRepository.update(l);
			
			// Exclui o lançamento importado
			lancamentoImportadoRepository.delete(entity);
		}
	}
}