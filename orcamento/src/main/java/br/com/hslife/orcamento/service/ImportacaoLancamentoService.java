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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.component.OpcaoSistemaComponent;
import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.TaxaConversao;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IImportacaoLancamento;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.facade.IRegraImportacao;
import br.com.hslife.orcamento.model.InfoOFX;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoImportadoRepository;
import br.com.hslife.orcamento.util.LancamentoContaComparator;
import br.com.hslife.orcamento.util.Util;
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

@Service("importacaoLancamentoService")
@Transactional(propagation=Propagation.REQUIRED, rollbackFor={ApplicationException.class})
public class ImportacaoLancamentoService implements IImportacaoLancamento {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private ICategoria categoriaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private IRegraImportacao regraImportacaoService;
	
	@Autowired
	private LancamentoImportadoRepository repository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private UsuarioComponent usuarioComponent;
	
	@Autowired
	private OpcaoSistemaComponent opcaoSistemaComponent;

	public LancamentoImportadoRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}

	public ICategoria getCategoriaService() {
		return categoriaService;
	}

	public IFavorecido getFavorecidoService() {
		return favorecidoService;
	}

	public IMeioPagamento getMeioPagamentoService() {
		return meioPagamentoService;
	}

	public IMoeda getMoedaService() {
		return moedaService;
	}

	public IRegraImportacao getRegraImportacaoService() {
		return regraImportacaoService;
	}

	public LancamentoContaRepository getLancamentoContaRepository() {
		this.lancamentoContaRepository.setSessionFactory(this.sessionFactory);
		return lancamentoContaRepository;
	}

	public UsuarioComponent getUsuarioComponent() {
		return usuarioComponent;
	}

	public OpcaoSistemaComponent getOpcaoSistemaComponent() {
		return opcaoSistemaComponent;
	}

	@Override
	public List<LancamentoImportado> buscarLancamentoImportadoPorConta(Conta conta) {
		return getRepository().findByConta(conta);
	}
	
	@Override
	public void excluirLancamentoImportado(LancamentoImportado entity) {
		getRepository().delete(entity);
	}
	
	@Override
	public void atualizarLancamentoImportado(LancamentoImportado entity) {
		getRepository().update(entity);		
	}
	
	public LancamentoImportado buscarPorID(Long id) {
		return getRepository().findByID(id);
	}
	
	@Override
	public void  processarArquivoImportado(Arquivo arquivo, Conta conta) throws ApplicationException {
		switch (conta.getTipoConta()) {
			case CORRENTE : this.processarArquivoImportadoContaCorrente(arquivo, conta); break;
			case POUPANCA : this.processarArquivoImportadoContaPoupanca(arquivo, conta); break;
			case CARTAO : this.processarArquivoImportadoCartaoCredito(arquivo, conta); break;
			default : throw new BusinessException("Opção inválida para conta!");
		}
	}

	@Override
	public List<LancamentoConta> buscarLancamentoContaACriarAtualizar(Conta conta, List<LancamentoImportado> lancamentosImportados) {
		return this.buscarLancamentoContaACriarAtualizar(conta, lancamentosImportados, false);
	}
	
	@Override
	public List<LancamentoConta> buscarLancamentoContaACriarAtualizar(Conta conta, List<LancamentoImportado> lancamentosImportados, boolean quitarAutomaticamente) {
		// Armazena o usuário logado para diminuir o acesso a base
		Usuario usuarioLogado = getUsuarioComponent().getUsuarioLogado();
		
		List<LancamentoConta> lancamentos = new ArrayList<LancamentoConta>();
		
		// Itera a lista de todas as moedas existentes e guarda em um Map todas elas
		// e na variável moedaPadrao a moeda padrão do usuário. 
		Moeda moedaPadrao = new Moeda();
		Map<String, Moeda> moedas = new HashMap<String, Moeda>();
		for (Moeda moeda : getMoedaService().buscarPorUsuario(usuarioLogado)) {
			if (moeda.isPadrao()) {
				moedaPadrao = moeda;
			}
			moedas.put(moeda.getCodigoMonetario(), moeda);
		}
		
		LancamentoConta lc = null;
		
		// Itera a lista de lançamentos importados para gerar os lançamentos da conta correspondentes
		for (LancamentoImportado li : lancamentosImportados) {
			
			lc = getLancamentoContaRepository().findByHash(li.getHash());
			
			if (lc == null) {
			
				lc = new LancamentoConta();
				
				if (li.getValor() > 0 || (li.getTipo() != null && li.getTipo().equalsIgnoreCase("CREDITO"))) {
					lc.setTipoLancamento(TipoLancamento.RECEITA);
					lc.setCategoria(getCategoriaService().buscarCategoria(li.getCategoria(), TipoCategoria.CREDITO, usuarioLogado));
				} else {
					lc.setTipoLancamento(TipoLancamento.DESPESA);
					lc.setCategoria(getCategoriaService().buscarCategoria(li.getCategoria(), TipoCategoria.DEBITO, usuarioLogado));
				}
				
				lc.setFavorecido(getFavorecidoService().buscarFavorecido(li.getFavorecido(), usuarioLogado));
				lc.setMeioPagamento(getMeioPagamentoService().buscarMeioPagamento(li.getMeiopagamento(), usuarioLogado));
				
				// Seta a moeda a partir do código monetário. Caso não encontre, seta a moeda padrão.
				lc.setMoeda(moedas.get(li.getMoeda()) == null ? moedaPadrao : moedas.get(li.getMoeda()));				
				
				lc.setConta(li.getConta());				
				lc.setDataPagamento(li.getData());
				lc.setDescricao(li.getHistorico());
				lc.setHistorico(li.getHistorico());
				lc.setNumeroDocumento(li.getDocumento());
				lc.setValorPago(Math.abs(li.getValor()));
				lc.setHashImportacao(li.getHash());
				lc.setObservacao(li.getObservacao());
				lc.setSelecionado(true);				
				
				// Seta o status do lançamento a ser inserido
				if (li.getData().after(new Date())) {
					lc.setStatusLancamentoConta(StatusLancamentoConta.AGENDADO);
				} else if (lc.getStatusLancamentoConta().equals(StatusLancamentoConta.AGENDADO)) {
					lc.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
				}
				
				// Define a taxa de conversão
				if (lc.getConta().getTipoConta().equals(TipoConta.CARTAO)) {
					lc.setTaxaConversao(new TaxaConversao(lc.getMoeda(), lc.getValorPago(), lc.getConta().getMoeda(), lc.getMoeda().getValorConversao()));
				}
				
				// Seta o lançamento como quitado caso a opção do sistema correspondente seja true
				if (quitarAutomaticamente) {
					lc.setStatusLancamentoConta(StatusLancamentoConta.QUITADO);
				}
				
				lancamentos.add(lc);
			} else {
				lc.setSelecionado(false);
				
				// Seta o status do lançamento a ser atualizado
				if (li.getData().after(new Date())) {
					lc.setStatusLancamentoConta(StatusLancamentoConta.AGENDADO);
				} else if (lc.getStatusLancamentoConta().equals(StatusLancamentoConta.AGENDADO)) {
					lc.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
				}
				
				// Seta o lançamento como quitado caso a opção do sistema correspondente seja true
				if (quitarAutomaticamente) {
					lc.setStatusLancamentoConta(StatusLancamentoConta.QUITADO);
				}
				
				lancamentos.add(lc);
			}
		}
		if (lancamentos.isEmpty())
			return lancamentos;
		else {
			// Ordena os lançamentos
			Collections.sort(lancamentos, new LancamentoContaComparator());
			return getRegraImportacaoService().processarRegras(conta, lancamentos);
		}
	}

	@Override
	public void processarLancamentos(Conta conta, List<LancamentoConta> lancamentos) {
		for (LancamentoConta l : lancamentos) {
			if (l.isSelecionado()) {
				if (l.getId() == null) {
					// Salva o lançamento da conta
					getLancamentoContaRepository().save(l);
					// Exclui o lançamento importado
					getRepository().delete(getRepository().findByHash(l.getHashImportacao()));
				} else {
					// Atualiza o lançamento da conta
					getLancamentoContaRepository().update(l);
					// Exclui o lançamento importado
					getRepository().delete(getRepository().findByHash(l.getHashImportacao()));
				}
			}
		}
	}
	
	@Override
	public void importarLancamento(LancamentoImportado entity) {
		this.importarLancamento(entity, false);
	}
	
	@Override
	public void importarLancamento(LancamentoImportado entity, boolean quitarAutomaticamente) {
		// Armazena o usuário logado para diminuir o acesso a base
		Usuario usuarioLogado = getUsuarioComponent().getUsuarioLogado();
		
		LancamentoConta l = getLancamentoContaRepository().findByHash(entity.getHash());
		if (l == null) {
			// Cria um novo lançamento
			l = new LancamentoConta();
			
			Moeda moedaPadrao = getMoedaService().buscarPadraoPorUsuario(usuarioLogado);
			
			if (entity.getValor() > 0) {
				l.setTipoLancamento(TipoLancamento.RECEITA);
				l.setCategoria(getCategoriaService().buscarCategoria(entity.getCategoria(), TipoCategoria.CREDITO, usuarioLogado));
			} else {
				l.setTipoLancamento(TipoLancamento.DESPESA);
				l.setCategoria(getCategoriaService().buscarCategoria(entity.getCategoria(), TipoCategoria.DEBITO, usuarioLogado));
			}
			
			// Seta o status do lançamento a ser inserido
			if (entity.getData().after(new Date())) {
				l.setStatusLancamentoConta(StatusLancamentoConta.AGENDADO);
			} else if (l.getStatusLancamentoConta().equals(StatusLancamentoConta.AGENDADO)) {
				l.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
			}
			
			// Seta o lançamento como quitado caso a opção do sistema correspondente seja true
			if (quitarAutomaticamente) {
				l.setStatusLancamentoConta(StatusLancamentoConta.QUITADO);
			}
			
			l.setFavorecido(getFavorecidoService().buscarFavorecido(entity.getFavorecido(), usuarioLogado));
			l.setMeioPagamento(getMeioPagamentoService().buscarMeioPagamento(entity.getMeiopagamento(), usuarioLogado));
			
			l.setConta(entity.getConta());
			l.setDataPagamento(entity.getData());
			l.setDescricao(entity.getHistorico());
			l.setHistorico(entity.getHistorico());
			l.setNumeroDocumento(entity.getDocumento());
			l.setValorPago(Math.abs(entity.getValor()));
			l.setHashImportacao(entity.getHash());
			l.setObservacao(entity.getObservacao());
			l.setMoeda(getMoedaService().buscarCodigoMonetarioPorUsuario(entity.getMoeda(), usuarioLogado) == null 
					? moedaPadrao 
					: getMoedaService().buscarCodigoMonetarioPorUsuario(entity.getMoeda(), usuarioLogado));
			
			// Define a taxa de conversão
			if (l.getConta().getTipoConta().equals(TipoConta.CARTAO)) {
				l.setTaxaConversao(new TaxaConversao(l.getMoeda(), l.getValorPago(), l.getConta().getMoeda(), l.getMoeda().getValorConversao()));
			}
			
			// Salva o lançamento
			getLancamentoContaRepository().save(getRegraImportacaoService().processarRegras(entity.getConta(), l));
			
			// Exclui o lançamento importado
			getRepository().delete(entity);
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
			
			// Define a taxa de conversão
			if (l.getConta().getTipoConta().equals(TipoConta.CARTAO)) {
				l.setTaxaConversao(new TaxaConversao(l.getMoeda(), l.getValorPago(), l.getConta().getMoeda(), l.getMoeda().getValorConversao()));
			}
			
			// Seta o lançamento como quitado caso a opção do sistema correspondente seja true
			if (quitarAutomaticamente) {
				l.setStatusLancamentoConta(StatusLancamentoConta.QUITADO);
			}
			
			getLancamentoContaRepository().update(l);
			
			// Exclui o lançamento importado
			getRepository().delete(entity);
		}
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public InfoOFX obterInformacaoArquivoImportado(Arquivo arquivo, Conta conta) throws ApplicationException {
		InfoOFX info = new InfoOFX();
		try {
			if (conta.getTipoConta().equals(TipoConta.CARTAO)) {
				AggregateUnmarshaller a = new AggregateUnmarshaller(ResponseEnvelope.class);
				ResponseEnvelope re = (ResponseEnvelope) a.unmarshal(new InputStreamReader(new ByteArrayInputStream(arquivo.getDados()), "ISO-8859-1"));
				CreditCardResponseMessageSet message = (CreditCardResponseMessageSet) re.getMessageSet(MessageSetType.creditcard);
				SignonResponse sr = re.getSignonResponse();
				if (message != null) {
					List<CreditCardStatementResponseTransaction> creditcard = message.getStatementResponses();
				    for (CreditCardStatementResponseTransaction c : creditcard) {
				    	info.setIdioma(sr.getLanguage());
				    	info.setNomeBanco(sr.getFinancialInstitution().getOrganization());
				    	info.setBancoID(sr.getFinancialInstitution().getId());
				    	info.setConta(c.getMessage().getAccount().getAccountNumber());
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
				    	info.setIdioma(sr.getLanguage());
				    	info.setNomeBanco(sr.getFinancialInstitution().getOrganization());
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
			throw new ApplicationException("Erro ao ler o arquivo OFX:" + e.getMessage(), e);
		}
		return info;
	}
	
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	private void processarArquivoImportadoContaCorrente(Arquivo arquivo, Conta conta) throws ApplicationException {
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
			        	throw new ApplicationException("Número do banco " + conta.getBanco().getNumero() + " não confere com do arquivo (" + b.getMessage().getAccount().getBankId() + ")!");
			        }
			        if (!conta.getAgencia().equals(b.getMessage().getAccount().getBranchId())) {		        	
			        	throw new ApplicationException("Número da agência " + conta.getAgencia() + " não confere com do arquivo (" + b.getMessage().getAccount().getBranchId() + ")!");
			        }		        
			        if (!conta.getContaCorrente().equals(b.getMessage().getAccount().getAccountNumber())) {
			        	throw new ApplicationException("Número da conta " + conta.getContaCorrente() + " não confere com do arquivo (" + b.getMessage().getAccount().getAccountNumber() + ")!");
			        }
			        if (!conta.getTipoConta().equals(TipoConta.CORRENTE)) {
			        	throw new ApplicationException("Somente contas correntes são aceitas!");
			        }
			        
			        /* Término do código do meu código de validação de conta */
			        
			        List<Transaction> list = b.getMessage().getTransactionList().getTransactions();			        
			        for (Transaction transaction : list) {
			        				            
			            /* Aqui começa meu código */
			            LancamentoImportado li = getRepository().findByHash(Util.MD5(transaction.getId()));
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
				            
			            		getRepository().save(li);
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
			            
		            		getRepository().update(li);
			            }
			            
			            /* Fim do meu código */
			       }
			   }
			} 
		}catch (Exception e) {
			throw new ApplicationException("Erro ao processar o arquivo OFX:" + e.getMessage(), e);
		}
	}
	
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	private void processarArquivoImportadoContaPoupanca(Arquivo arquivo, Conta conta) throws ApplicationException {
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
			        	throw new ApplicationException("Número do banco " + conta.getBanco().getNumero() + " não confere com do arquivo (" + b.getMessage().getAccount().getBankId() + ")!");
			        }
			        if (!conta.getAgencia().equals(b.getMessage().getAccount().getBranchId())) {		        	
			        	throw new ApplicationException("Número da agência " + conta.getAgencia() + " não confere com do arquivo (" + b.getMessage().getAccount().getBranchId() + ")!");
			        }		        
			        if (!conta.getContaCorrente().equals(b.getMessage().getAccount().getAccountNumber())) {
			        	throw new ApplicationException("Número da conta " + conta.getContaCorrente() + " não confere com do arquivo (" + b.getMessage().getAccount().getAccountNumber() + ")!");
			        }
			        if (!conta.getTipoConta().equals(TipoConta.POUPANCA)) {
			        	throw new ApplicationException("Somente contas correntes são aceitas!");
			        }
			        
			        /* Término do código do meu código de validação de conta */
			        
			        List<Transaction> list = b.getMessage().getTransactionList().getTransactions();			        
			        for (Transaction transaction : list) {
			        				            
			            /* Aqui começa meu código */
			            LancamentoImportado li = getRepository().findByHash(Util.MD5(transaction.getId()));
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
				            
			            		getRepository().save(li);
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
			            
		            		getRepository().update(li);
			            }
			            
			            /* Fim do meu código */
			       }
			   }
			} 
		}catch (Exception e) {
			throw new ApplicationException("Erro ao processar o arquivo OFX:" + e.getMessage(), e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void processarArquivoImportadoCartaoCredito(Arquivo arquivo, Conta conta) throws ApplicationException {
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
			        	LancamentoImportado li = getRepository().findByHash(Util.MD5(transaction.getId()));
			            if (li == null) {			            
			            	li = new LancamentoImportado();
			            	li.setConta(conta);
			            	li.setData(transaction.getDatePosted());
			            	li.setMoeda(transaction.getCurrency() == null ? c.getMessage().getCurrencyCode() : transaction.getCurrency().getCode());			            	
			            	li.setHash(Util.MD5(transaction.getId()));
			            	li.setHistorico(transaction.getMemo());
			            	li.setValor(transaction.getAmount());
			            	
			            	getRepository().save(li);			            	
			            } else {
			            	// Caso o lançamento já exista o mesmo será atualizado
			            	li.setConta(conta);
			            	li.setData(transaction.getDatePosted());
			            	li.setMoeda(transaction.getCurrency() == null ? c.getMessage().getCurrencyCode() : transaction.getCurrency().getCode());			            	
			            	li.setHash(Util.MD5(transaction.getId()));
			            	li.setHistorico(transaction.getMemo());
			            	li.setValor(transaction.getAmount());
			            	
			            	getRepository().update(li);
			            }
			       }
				}				
			} 
		} catch (Exception e) {
			throw new ApplicationException("Erro ao processar o arquivo OFX: " + e.getMessage(), e);
		}
	}
	
	@Override
	public void processarArquivoCSVImportado(Arquivo arquivo, Conta conta) throws ApplicationException {
		try {
			// Declaração e leitura dos dados do CSV
			final Reader reader = new InputStreamReader(new ByteArrayInputStream(arquivo.getDados()), "UTF-8");
			final CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
			
			// Declaração das variáveis
			LancamentoImportado lancamentoImportado = new LancamentoImportado();
		
			for (CSVRecord record : parser) {
				
				int quantidade = Integer.parseInt(record.get("QUANTIDADE"));
				
				lancamentoImportado.setConta(conta);
				lancamentoImportado.setData(new SimpleDateFormat("yyyy-MM-dd").parse(record.get("DATA")));
				lancamentoImportado.setHistorico(record.get("HISTORICO"));
				lancamentoImportado.setValor(Double.parseDouble(record.get("VALOR")));
				lancamentoImportado.setMoeda(record.get("MOEDA"));
				lancamentoImportado.setDocumento(record.get("DOCUMENTO"));
				lancamentoImportado.setObservacao(record.get("OBSERVACAO"));
				lancamentoImportado.setCategoria(record.get("CATEGORIA"));
				lancamentoImportado.setFavorecido(record.get("FAVORECIDO"));
				lancamentoImportado.setMeiopagamento(record.get("MEIOPAGAMENTO"));
				lancamentoImportado.setHash(lancamentoImportado.getFullLabel());
								
				// Insere o lançamento importado X vezes de acordo com o campo QUANTIDADE
				for (int i = 1; i <= quantidade; i++) {
					getRepository().save(lancamentoImportado.clonarLancamento(i));
				}
			}
			
			// Fecha os streams
			parser.close();
			reader.close();
		
		} catch (Exception e) {
			throw new ApplicationException(e);
		}		
	}
	
	@Override
	public void apagarLancamentosImportados(Conta conta) {
		List<LancamentoImportado> lancamentos = getRepository().findByConta(conta);
		for (LancamentoImportado li : lancamentos) {
			getRepository().delete(li);
		}
	}
}