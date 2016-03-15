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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ofx4j.domain.data.MessageSetType;
import net.sf.ofx4j.domain.data.ResponseEnvelope;
import net.sf.ofx4j.domain.data.ResponseMessageSet;
import net.sf.ofx4j.domain.data.banking.BankStatementResponseTransaction;
import net.sf.ofx4j.domain.data.banking.BankingResponseMessageSet;
import net.sf.ofx4j.domain.data.common.Transaction;
import net.sf.ofx4j.domain.data.creditcard.CreditCardResponseMessageSet;
import net.sf.ofx4j.domain.data.creditcard.CreditCardStatementResponseTransaction;
import net.sf.ofx4j.domain.data.signon.SignonResponse;
import net.sf.ofx4j.io.AggregateUnmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.component.RegraImportacaoComponent;
import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IImportacaoLancamento;
import br.com.hslife.orcamento.model.InfoOFX;
import br.com.hslife.orcamento.repository.CategoriaRepository;
import br.com.hslife.orcamento.repository.FavorecidoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoImportadoRepository;
import br.com.hslife.orcamento.repository.MeioPagamentoRepository;
import br.com.hslife.orcamento.repository.MoedaRepository;
import br.com.hslife.orcamento.util.Util;

@Service("importacaoLancamentoService")
public class ImportacaoLancamentoService implements IImportacaoLancamento {
	
	@Autowired
	private LancamentoImportadoRepository lancamentoImportadoRepository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private FavorecidoRepository favorecidoRepository;
	
	@Autowired
	private MeioPagamentoRepository meioPagamentoRepository;
	
	@Autowired
	private UsuarioComponent usuarioComponent;
	
	@Autowired
	private MoedaRepository moedaRepository;
	
	@Autowired
	private RegraImportacaoComponent regraImportacaoComponent;

	public void setLancamentoImportadoRepository(
			LancamentoImportadoRepository lancamentoImportadoRepository) {
		this.lancamentoImportadoRepository = lancamentoImportadoRepository;
	}

	public void setLancamentoContaRepository(
			LancamentoContaRepository lancamentoContaRepository) {
		this.lancamentoContaRepository = lancamentoContaRepository;
	}
	
	public void setCategoriaRepository(CategoriaRepository categoriaRepository) {
		this.categoriaRepository = categoriaRepository;
	}

	public void setUsuarioComponent(UsuarioComponent usuarioComponent) {
		this.usuarioComponent = usuarioComponent;
	}

	public void setFavorecidoRepository(FavorecidoRepository favorecidoRepository) {
		this.favorecidoRepository = favorecidoRepository;
	}

	public void setMeioPagamentoRepository(
			MeioPagamentoRepository meioPagamentoRepository) {
		this.meioPagamentoRepository = meioPagamentoRepository;
	}

	@Override
	public List<LancamentoImportado> buscarLancamentoImportadoPorConta(Conta conta) throws BusinessException {
		return lancamentoImportadoRepository.findByConta(conta);
	}
	
	@Override
	public void excluirLancamentoImportado(LancamentoImportado entity) throws BusinessException {
		lancamentoImportadoRepository.delete(entity);
	}
	
	@Override
	public void atualizarLancamentoImportado(LancamentoImportado entity) throws BusinessException {
		lancamentoImportadoRepository.update(entity);		
	}
	
	public LancamentoImportado buscarPorID(Long id) throws BusinessException {
		return lancamentoImportadoRepository.findByID(id);
	}
	
	@Override
	public void  processarArquivoImportado(Arquivo arquivo, Conta conta) throws BusinessException {
		switch (conta.getTipoConta()) {
			case CORRENTE : this.processarArquivoImportadoContaCorrente(arquivo, conta); break;
			case POUPANCA : this.processarArquivoImportadoContaPoupanca(arquivo, conta); break;
			case CARTAO : this.processarArquivoImportadoCartaoCredito(arquivo, conta); break;
			default : throw new BusinessException("Opção inválida para conta!");
		}
	}

	@Override
	public List<LancamentoConta> buscarLancamentoContaACriarAtualizar(List<LancamentoImportado> lancamentosImportados)
			throws BusinessException {
		// TODO Auto-generated method stub
		return null;
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
		// Armazena o usuário logado para diminuir o acesso a base
		Usuario usuarioLogado = usuarioComponent.getUsuarioLogado();
		
		List<LancamentoConta> lancamentos = new ArrayList<LancamentoConta>();
		
		LancamentoConta lc;
		Categoria categoriaPadraoCredito = categoriaRepository.findDefaultByTipoCategoriaAndUsuario(usuarioLogado, TipoCategoria.CREDITO);
		Categoria categoriaPadraoDebito = categoriaRepository.findDefaultByTipoCategoriaAndUsuario(usuarioLogado, TipoCategoria.DEBITO);
		
		Favorecido favorecidoPadrao = favorecidoRepository.findDefaultByUsuario(usuarioLogado);
		
		MeioPagamento meioPagamentoPadrao = meioPagamentoRepository.findDefaultByUsuario(usuarioLogado);
		
		// Itera a lista de todas as moedas existentes e guarda em um Map todas elas
		// e na variável moedaPadrao a moeda padrão do usuário. 
		Moeda moedaPadrao = new Moeda();
		Map<String, Moeda> moedas = new HashMap<String, Moeda>();
		for (Moeda moeda : moedaRepository.findByUsuario(usuarioLogado)) {
			if (moeda.isPadrao()) {
				moedaPadrao = moeda;
			}
			moedas.put(moeda.getCodigoMonetario(), moeda);
		}
		
		Conta conta = null;
		
		// Itera a lista de lançamentos importados para gerar os lançamentos da conta correspondentes
		for (LancamentoImportado li : lancamentosImportados) {
			if (lancamentoContaRepository.findByHash(li.getHash()) == null) {
			
				lc = new LancamentoConta();
				
				if (li.getValor() > 0) {
					lc.setTipoLancamento(TipoLancamento.RECEITA);
					lc.setCategoria(categoriaPadraoCredito);
				} else {
					lc.setTipoLancamento(TipoLancamento.DESPESA);
					lc.setCategoria(categoriaPadraoDebito);
				}
				lc.setFavorecido(favorecidoPadrao);
				lc.setMeioPagamento(meioPagamentoPadrao);
				
				// Seta a moeda a partir do código monetário. Caso não encontre, seta a moeda padrão.
				lc.setMoeda(moedas.get(li.getMoeda()) == null ? moedaPadrao : moedas.get(li.getMoeda()));				
				
				lc.setConta(li.getConta());				
				lc.setDataPagamento(li.getData());
				lc.setDescricao(li.getHistorico());
				lc.setHistorico(li.getHistorico());
				lc.setNumeroDocumento(li.getDocumento());
				lc.setValorPago(Math.abs(li.getValor()));
				lc.setHashImportacao(li.getHash());
				
				// Seta o status do lançamento a ser inserido
				if (li.getData().after(new Date())) {
					lc.setStatusLancamentoConta(StatusLancamentoConta.AGENDADO);
				} else if (lc.getStatusLancamentoConta().equals(StatusLancamentoConta.AGENDADO)) {
					lc.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
				}
				
				lancamentos.add(lc);
				conta = lc.getConta();
			}
		}
		if (lancamentos.isEmpty())
			return lancamentos;
		else
			return regraImportacaoComponent.processarRegras(conta, lancamentos);
	}

	@Override
	public void processarLancamentosImportados(Conta conta, boolean gerarNovosLancamentos) throws BusinessException {
		List<LancamentoConta> lancamentosAInserir = new ArrayList<LancamentoConta>();
		List<LancamentoConta> lancamentosAAtualizar = new ArrayList<LancamentoConta>(); 
		
		lancamentosAAtualizar = this.buscarLancamentoContaAAtualizar(this.buscarLancamentoImportadoPorConta(conta));
		lancamentosAInserir = this.gerarLancamentoContaAInserir(buscarLancamentoImportadoPorConta(conta));
		
		Moeda moedaPadrao = moedaRepository.findDefaultByUsuario(usuarioComponent.getUsuarioLogado());

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

			// Seta o status do lançamento a ser inserido
			if (li.getData().after(new Date())) {
				l.setStatusLancamentoConta(StatusLancamentoConta.AGENDADO);
			} else if (l.getStatusLancamentoConta().equals(StatusLancamentoConta.AGENDADO)) {
				l.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
			}
			
			l.setHistorico(li.getHistorico());
			l.setNumeroDocumento(li.getDocumento());
			l.setValorPago(Math.abs(li.getValor()));
			l.setMoeda(moedaRepository.findCodigoMoedaByUsuario(li.getMoeda(), usuarioComponent.getUsuarioLogado()) == null 
					? moedaPadrao 
					: moedaRepository.findCodigoMoedaByUsuario(li.getMoeda(), usuarioComponent.getUsuarioLogado()));
			
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
			Categoria categoriaPadraoCredito = categoriaRepository.findDefaultByTipoCategoriaAndUsuario(usuarioComponent.getUsuarioLogado(), TipoCategoria.CREDITO);
			Categoria categoriaPadraoDebito = categoriaRepository.findDefaultByTipoCategoriaAndUsuario(usuarioComponent.getUsuarioLogado(), TipoCategoria.DEBITO);
			
			Favorecido favorecidoPadrao = favorecidoRepository.findDefaultByUsuario(usuarioComponent.getUsuarioLogado());
			
			MeioPagamento meioPagamentoPadrao = meioPagamentoRepository.findDefaultByUsuario(usuarioComponent.getUsuarioLogado());
			
			Moeda moedaPadrao = moedaRepository.findDefaultByUsuario(usuarioComponent.getUsuarioLogado());
			
			if (entity.getValor() > 0) {
				l.setTipoLancamento(TipoLancamento.RECEITA);
				l.setCategoria(categoriaPadraoCredito);
			} else {
				l.setTipoLancamento(TipoLancamento.DESPESA);
				l.setCategoria(categoriaPadraoDebito);
			}
			
			// Seta o status do lançamento a ser inserido
			if (entity.getData().after(new Date())) {
				l.setStatusLancamentoConta(StatusLancamentoConta.AGENDADO);
			} else if (l.getStatusLancamentoConta().equals(StatusLancamentoConta.AGENDADO)) {
				l.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
			}
			
			l.setFavorecido(favorecidoPadrao);
			l.setMeioPagamento(meioPagamentoPadrao);
			
			l.setConta(entity.getConta());
			l.setDataPagamento(entity.getData());
			l.setDescricao(entity.getHistorico());
			l.setHistorico(entity.getHistorico());
			l.setNumeroDocumento(entity.getDocumento());
			l.setValorPago(Math.abs(entity.getValor()));
			l.setHashImportacao(entity.getHash());
			l.setMoeda(moedaRepository.findCodigoMoedaByUsuario(entity.getMoeda(), usuarioComponent.getUsuarioLogado()) == null 
					? moedaPadrao 
					: moedaRepository.findCodigoMoedaByUsuario(entity.getMoeda(), usuarioComponent.getUsuarioLogado()));
			
			// Salva o lançamento
			lancamentoContaRepository.save(regraImportacaoComponent.processarRegras(entity.getConta(), l));
			
			// Exclui o lançamento importado
			lancamentoImportadoRepository.delete(entity);
		} else {
			// Atualiza o lançamento existente
						
			l.setDataPagamento(entity.getData());						
			l.setHistorico(entity.getHistorico());
			l.setNumeroDocumento(entity.getDocumento());
			l.setValorPago(Math.abs(entity.getValor()));
			
			if (entity.getValor() > 0) {
				l.setTipoLancamento(TipoLancamento.RECEITA);
			} else {
				l.setTipoLancamento(TipoLancamento.DESPESA);
			}
			
			// Seta o status do lançamento a ser atualizado
			if (entity.getData().after(new Date())) {
				l.setStatusLancamentoConta(StatusLancamentoConta.AGENDADO);
			} else if (l.getStatusLancamentoConta().equals(StatusLancamentoConta.AGENDADO)) {
				l.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
			}
			
			lancamentoContaRepository.update(l);
			
			// Exclui o lançamento importado
			lancamentoImportadoRepository.delete(entity);
		}
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	public InfoOFX obterInformacaoArquivoImportado(Arquivo arquivo, Conta conta) throws BusinessException {
		InfoOFX info = new InfoOFX();
		try {
			if (conta.getTipoConta().equals(TipoConta.CARTAO)) {
				AggregateUnmarshaller a = new AggregateUnmarshaller(ResponseEnvelope.class);
				ResponseEnvelope re = (ResponseEnvelope) a.unmarshal(new InputStreamReader(new ByteArrayInputStream(arquivo.getDados()), "ISO-8859-1"));
				CreditCardResponseMessageSet message = (CreditCardResponseMessageSet) re.getMessageSet(MessageSetType.creditcard);
				if (message != null) {
					List<CreditCardStatementResponseTransaction> creditcard = message.getStatementResponses();
				    for (CreditCardStatementResponseTransaction c : creditcard) {
				    	info.setQuantidadeTransacao(c.getMessage().getTransactionList().getTransactions().size());
				    	info.setMoedaPadrao(c.getMessage().getCurrencyCode());
				    	info.setInicioTransacoes(c.getMessage().getTransactionList().getStart());
				    	info.setFimTransacoes(c.getMessage().getTransactionList().getEnd());
				   }
				}
			} else {
				AggregateUnmarshaller a = new AggregateUnmarshaller(ResponseEnvelope.class);
				ResponseEnvelope re = (ResponseEnvelope) a.unmarshal(new InputStreamReader(new ByteArrayInputStream(arquivo.getDados()), "ISO-8859-1"));				
				SignonResponse sr = re.getSignonResponse();			
				MessageSetType type = MessageSetType.banking;
				ResponseMessageSet message = re.getMessageSet(type);			
				if (message != null) {
					List<BankStatementResponseTransaction> bank = ((BankingResponseMessageSet) message).getStatementResponses();
				    for (BankStatementResponseTransaction b : bank) {
				    	info.setBancoID(b.getMessage().getAccount().getBankId());
				    	info.setConta(b.getMessage().getAccount().getAccountNumber());
				    	info.setAgencia(b.getMessage().getAccount().getBranchId());
				    	info.setTipoConta(b.getMessage().getAccount().getAccountType().name());
				        info.setBalancoFinal(b.getMessage().getLedgerBalance().getAmount());
				        info.setDataArquivo(b.getMessage().getLedgerBalance().getAsOfDate());
				        info.setMoedaPadrao(b.getMessage().getCurrencyCode());
				        info.setQuantidadeTransacao(b.getMessage().getTransactionList().getTransactions().size());
				        info.setInicioTransacoes(b.getMessage().getTransactionList().getStart());
				        info.setFimTransacoes(b.getMessage().getTransactionList().getEnd());			        
				   }
				} 
			}
		}catch (Exception e) {
			throw new BusinessException("Erro ao ler o arquivo OFX:" + e.getMessage(), e);
		}
		return info;
	}
	
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	private void processarArquivoImportadoContaCorrente(Arquivo arquivo, Conta conta) throws BusinessException {
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
			        for (Transaction transaction : list) {
			        				            
			            /* Aqui começa meu código */
			            LancamentoImportado li = lancamentoImportadoRepository.findByHash(Util.MD5(transaction.getId()));
			            if (li == null) {			            
			            	// Cria os lançamentos se a data do movimento for posterior à data de abertura da conta
			            	if (transaction.getDatePosted().after(conta.getDataAbertura())) {
			            		li = new LancamentoImportado();
			            		li.setConta(conta);
			            		li.setData(transaction.getDatePosted());
			            		li.setMoeda(transaction.getCurrency() == null ? b.getMessage().getCurrencyCode() : transaction.getCurrency().getCode());
			            		li.setDocumento(transaction.getReferenceNumber());
			            		li.setHash(Util.MD5(transaction.getId()));
			            		li.setHistorico(transaction.getMemo());
			            		li.setValor(transaction.getAmount());
				            
			            		lancamentoImportadoRepository.save(li);
			            	}
			            } else {
			            	// Caso o lançamento importado já exista, o mesmo será atualizado
			            	li.setConta(conta);
		            		li.setData(transaction.getDatePosted());
		            		li.setMoeda(transaction.getCurrency() == null ? b.getMessage().getCurrencyCode() : transaction.getCurrency().getCode());
		            		li.setDocumento(transaction.getReferenceNumber());
		            		li.setHash(Util.MD5(transaction.getId()));
		            		li.setHistorico(transaction.getMemo());
		            		li.setValor(transaction.getAmount());
			            
		            		lancamentoImportadoRepository.update(li);
			            }
			            
			            /* Fim do meu código */
			       }
			   }
			} 
		}catch (Exception e) {
			throw new BusinessException("Erro ao processar o arquivo OFX:" + e.getMessage(), e);
		}
	}
	
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	private void processarArquivoImportadoContaPoupanca(Arquivo arquivo, Conta conta) throws BusinessException {
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
			        if (!conta.getTipoConta().equals(TipoConta.POUPANCA)) {
			        	throw new BusinessException("Somente contas correntes são aceitas!");
			        }
			        
			        /* Término do código do meu código de validação de conta */
			        
			        List<Transaction> list = b.getMessage().getTransactionList().getTransactions();			        
			        for (Transaction transaction : list) {
			        				            
			            /* Aqui começa meu código */
			            LancamentoImportado li = lancamentoImportadoRepository.findByHash(Util.MD5(transaction.getId()));
			            if (li == null) {			            
			            	// Cria os lançamentos se a data do movimento for posterior à data de abertura da conta
			            	if (transaction.getDatePosted().after(conta.getDataAbertura())) {
			            		li = new LancamentoImportado();
			            		li.setConta(conta);
			            		li.setData(transaction.getDatePosted());
			            		li.setMoeda(transaction.getCurrency() == null ? b.getMessage().getCurrencyCode() : transaction.getCurrency().getCode());
			            		li.setDocumento(transaction.getReferenceNumber());
			            		li.setHash(Util.MD5(transaction.getId()));
			            		li.setHistorico(transaction.getMemo());
			            		li.setValor(transaction.getAmount());
				            
			            		lancamentoImportadoRepository.save(li);
			            	}
			            } else {
			            	// Caso o lançamento já exista ele será atualizado
			            	li.setConta(conta);
		            		li.setData(transaction.getDatePosted());
		            		li.setMoeda(transaction.getCurrency() == null ? b.getMessage().getCurrencyCode() : transaction.getCurrency().getCode());
		            		li.setDocumento(transaction.getReferenceNumber());
		            		li.setHash(Util.MD5(transaction.getId()));
		            		li.setHistorico(transaction.getMemo());
		            		li.setValor(transaction.getAmount());
			            
		            		lancamentoImportadoRepository.update(li);
			            }
			            
			            /* Fim do meu código */
			       }
			   }
			} 
		}catch (Exception e) {
			throw new BusinessException("Erro ao processar o arquivo OFX:" + e.getMessage(), e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void processarArquivoImportadoCartaoCredito(Arquivo arquivo, Conta conta) throws BusinessException {
		try {
			AggregateUnmarshaller a = new AggregateUnmarshaller(ResponseEnvelope.class);
			ResponseEnvelope re = (ResponseEnvelope) a.unmarshal(new InputStreamReader(new ByteArrayInputStream(arquivo.getDados()), "ISO-8859-1"));
			CreditCardResponseMessageSet message = (CreditCardResponseMessageSet) re.getMessageSet(MessageSetType.creditcard);
			if (message != null) {
				List<CreditCardStatementResponseTransaction> creditcard = message.getStatementResponses();
				for (CreditCardStatementResponseTransaction c : creditcard) {
					
					// Valida o número do cartão de crédito
					if (conta.getCartaoCredito().getNumeroCartao() == null || !conta.getCartaoCredito().getNumeroCartao().equals(Util.SHA1(c.getMessage().getAccount().getAccountNumber()))) {
			        	throw new BusinessException("Número do cartão informado não confere com do arquivo!");
			        }
					
					List<Transaction> list = c.getMessage().getTransactionList().getTransactions();
			        for (Transaction transaction : list) {			        	
			        	LancamentoImportado li = lancamentoImportadoRepository.findByHash(Util.MD5(transaction.getId()));
			            if (li == null) {			            
			            	li = new LancamentoImportado();
			            	li.setConta(conta);
			            	li.setData(transaction.getDatePosted());
			            	li.setMoeda(transaction.getCurrency() == null ? c.getMessage().getCurrencyCode() : transaction.getCurrency().getCode());			            	
			            	li.setHash(Util.MD5(transaction.getId()));
			            	li.setHistorico(transaction.getMemo());
			            	li.setValor(transaction.getAmount());
			            	
			            	lancamentoImportadoRepository.save(li);			            	
			            } else {
			            	// Caso o lançamento já exista o mesmo será atualizado
			            	li.setConta(conta);
			            	li.setData(transaction.getDatePosted());
			            	li.setMoeda(transaction.getCurrency() == null ? c.getMessage().getCurrencyCode() : transaction.getCurrency().getCode());			            	
			            	li.setHash(Util.MD5(transaction.getId()));
			            	li.setHistorico(transaction.getMemo());
			            	li.setValor(transaction.getAmount());
			            	
			            	lancamentoImportadoRepository.update(li);
			            }
			       }
				}				
			} 
		} catch (Exception e) {
			throw new BusinessException("Erro ao processar o arquivo OFX:" + e.getMessage(), e);
		}
	}
}