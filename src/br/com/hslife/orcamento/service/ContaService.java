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

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.component.ContaComponent;
import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.AberturaFechamentoConta;
import br.com.hslife.orcamento.entity.Banco;
import br.com.hslife.orcamento.entity.CartaoCredito;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.repository.AberturaFechamentoContaRepository;
import br.com.hslife.orcamento.repository.ContaRepository;
import br.com.hslife.orcamento.repository.FechamentoPeriodoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoImportadoRepository;
import br.com.hslife.orcamento.util.Util;

@Service("contaService")
public class ContaService extends AbstractCRUDService<Conta> implements IConta {

	@Autowired
	private ContaRepository repository;
	
	@Autowired
	private ContaComponent component;
	
	@Autowired
	private AberturaFechamentoContaRepository aberturaFechamentoContaRepository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private UsuarioComponent usuarioComponent;
	
	@Autowired
	private FechamentoPeriodoRepository fechamentoPeriodoRepository;
	
	@Autowired
	private LancamentoImportadoRepository lancamentoImportadoRepository;
	
	public ContaComponent getComponent() {
		return component;
	}

	public void setComponent(ContaComponent component) {
		this.component = component;
	}

	public ContaRepository getRepository() {
		return repository;
	}

	public void setRepository(ContaRepository repository) {
		this.repository = repository;
	}

	public void setAberturaFechamentoContaRepository(
			AberturaFechamentoContaRepository aberturaFechamentoContaRepository) {
		this.aberturaFechamentoContaRepository = aberturaFechamentoContaRepository;
	}
	
	public void setLancamentoContaRepository(
			LancamentoContaRepository lancamentoContaRepository) {
		this.lancamentoContaRepository = lancamentoContaRepository;
	}
	
	public void setUsuarioComponent(UsuarioComponent usuarioComponent) {
		this.usuarioComponent = usuarioComponent;
	}

	public void setLancamentoImportadoRepository(
			LancamentoImportadoRepository lancamentoImportadoRepository) {
		this.lancamentoImportadoRepository = lancamentoImportadoRepository;
	}

	public void setFechamentoPeriodoRepository(
			FechamentoPeriodoRepository fechamentoPeriodoRepository) {
		this.fechamentoPeriodoRepository = fechamentoPeriodoRepository;
	}

	@Override
	public void validar(Conta entity) throws BusinessException {
		if (entity.getDataFechamento() != null) {
			if (entity.getDataAbertura().after(entity.getDataFechamento()) && entity.getDataFechamento().before(entity.getDataAbertura())) {
				throw new BusinessException("Data de abertura (" + Util.formataDataHora(entity.getDataAbertura(), Util.DATA) + ") e data de fechamento (" +  Util.formataDataHora(entity.getDataFechamento(), Util.DATA)+") incorretos!");
			}
		}
	}
	
	@Override
	public void cadastrar(Conta entity) throws BusinessException {
		// Cadastra a conta		
		getRepository().save(entity);
		
		// Registra a abertura da conta
		this.ativarConta(entity);
	}
	
	@Override
	public void ativarConta(Conta conta) throws BusinessException {	
		// Seta a conta como ativa
		conta.setAtivo(true);
		
		// Exclui a data e saldo de fechamento
		conta.setDataFechamento(null);
		conta.setSaldoFinal(0);
		
		// Salva o histórico de abertura da conta
		AberturaFechamentoConta afc = new AberturaFechamentoConta();
		afc.setConta(conta);
		afc.setData(conta.getDataAbertura());
		afc.setOperacao(OperacaoConta.ABERTURA);
		afc.setSaldo(conta.getSaldoInicial());
		afc.setUsuario(usuarioComponent.getUsuarioLogado().getLogin());
		aberturaFechamentoContaRepository.save(afc);
		
		// Salva a conta
		getRepository().update(conta);
	}
	
	@Override
	public void desativarConta(Conta conta, String situacaoLancamentos) throws BusinessException {
		
		if (situacaoLancamentos.equals("QUITAR")) {
			// Busca o último lançamento cadastrado, caso não exista cria um novo lançamento e define a data de 
			// pagamento com a data atual
			LancamentoConta ultimoLancamento;
			if (lancamentoContaRepository.findLastLancamentoContaByConta(conta) != null)
				ultimoLancamento = lancamentoContaRepository.findLastLancamentoContaByConta(conta);
			else {
				ultimoLancamento = new LancamentoConta();
				ultimoLancamento.setDataPagamento(new Date());
			}				
			
			// Realiza o fechamento do período
			getComponent().fecharPeriodo(ultimoLancamento.getDataPagamento(), conta);
			
			// Seta a data de fechamento da conta com a data de pagamento do último lançamento
			conta.setDataFechamento(ultimoLancamento.getDataPagamento());
			
			// Exclui todos os lançamentos existentes após a data de fechamento da conta
			lancamentoContaRepository.deleteAllLancamentoContaAfterDateByConta(conta.getDataFechamento(), conta);
			
			// Define o saldo final da conta com base no último fechamento realizado
			conta.setSaldoFinal(getComponent().buscarUltimoFechamentoPeriodoPorConta(conta).getSaldo());
			
		} else if (situacaoLancamentos.equals("EXCLUIR")) {						
			// Realiza o fechamento do período
			getComponent().fecharPeriodo(conta.getDataFechamento(), conta);
						
			// Exclui todos os lançamentos existentes após a data de fechamento da conta
			lancamentoContaRepository.deleteAllLancamentoContaAfterDateByConta(conta.getDataFechamento(), conta);
						
			// Define o saldo final da conta com base no último fechamento realizado
			conta.setSaldoFinal(getComponent().buscarUltimoFechamentoPeriodoPorConta(conta).getSaldo());
						
		}
		
		// Seta a conta como inativa
		conta.setAtivo(false);
		
		// Salva o histórico de fechamento da conta
		AberturaFechamentoConta afc = new AberturaFechamentoConta();
		afc.setConta(conta);
		afc.setData(conta.getDataFechamento());
		afc.setOperacao(OperacaoConta.FECHAMENTO);
		afc.setSaldo(conta.getSaldoFinal());
		afc.setUsuario(usuarioComponent.getUsuarioLogado().getLogin());
		aberturaFechamentoContaRepository.save(afc);
		
		// Salva a conta
		getRepository().update(conta);
		
		// Desvincula as categorias, favorecidos e meios de pagamentos dos lançamentos da conta inativada
		lancamentoContaRepository.setAllDescricaoCategoriaOnLancamentoContaByConta(conta);
		lancamentoContaRepository.setAllDescricaoFavorecidoOnLancamentoContaByConta(conta);
		lancamentoContaRepository.setAllDescricaoMeioPagamentoOnLancamentoContaByConta(conta);
		
		// Caso a conta esteja marcada para arquivamento desvincula as categorias, favorecidos e meios de pagamento
		// dos lançamentos da conta inativada
		if (conta.isArquivado()) {
			lancamentoContaRepository.setNullCategoriaOnLancamentoContaByConta(conta);
			lancamentoContaRepository.setNullFavorecidoOnLancamentoContaByConta(conta);
			lancamentoContaRepository.setNullMeioPagamentoOnLancamentoContaByConta(conta);
		}
	}
	
	@Override
	public void excluir(Conta entity) throws BusinessException {
		if (getRepository().existsLinkages(entity)) {
			throw new BusinessException("Não é possível excluir! Existem lançamentos e fechamentos relacionamentos com a conta!");
		} else {
			// Exclui os lançamentos importados
			for (LancamentoImportado importado : lancamentoImportadoRepository.findByConta(entity)) {
				lancamentoImportadoRepository.delete(importado);
			}
			
			// Exclui os fechamentos de períodos com status REABERTURA
			for (FechamentoPeriodo fechamento : fechamentoPeriodoRepository.findByContaAndOperacaoConta(entity, OperacaoConta.REABERTURA)) {
				fechamentoPeriodoRepository.delete(fechamento);
			}
			
			// Exclui o histórico de abertura e fechamento existente
			for (AberturaFechamentoConta afc : aberturaFechamentoContaRepository.findByConta(entity)) {
				aberturaFechamentoContaRepository.delete(afc);
			}
			
			// Exclui a conta
			super.excluir(entity);
		}		
	}
	
	@Override
	public List<Conta> buscarTodos() throws BusinessException {
		return getRepository().findAll();
	}
	
	@Override
	public List<Conta> buscarPorBanco(Long idBanco) throws BusinessException {
		return getRepository().findByBanco(idBanco);
	}
	
	@Override
	public List<Conta> buscarPorDescricao(String descricao) throws BusinessException {
		return getRepository().findByDescricao(descricao);
	}
	
	@Override
	public List<Conta> buscarPorUsuario(Long idUsuario)	throws BusinessException {
		return getRepository().findByUsuario(idUsuario);
	}
	
	@Override
	public List<Conta> buscarTodosAtivos() throws BusinessException {
		return getRepository().findAllAtivos();
	}
	
	@Override
	public List<Conta> buscarTodosAtivosPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findAllAtivosByUsuario(usuario);
	}
	
	@Override
	public List<Conta> buscarPorDescricaoEUsuario(String descricao, Usuario usuario) throws BusinessException {
		return getRepository().findByDescricaoAndUsuario(descricao, usuario);
	}
	
	@Override
	public List<Conta> buscarPorBancoEUsuario(Banco banco, Usuario usuario) throws BusinessException {
		if (banco == null)
			return getRepository().findByUsuario(usuario.getId());
		else
			return getRepository().findByBancoAndUsuario(banco, usuario);
	}
	
	@Override
	public List<Conta> buscarPorTipoContaEUsuario(TipoConta tipoConta, Usuario usuario) throws BusinessException {
		return getRepository().findByTipoContaAndUsuario(tipoConta, usuario);
	}
	
	@Override
	public List<AberturaFechamentoConta> buscarHistoricoAberturaFechamentoPorConta(Conta conta) throws BusinessException {
		return aberturaFechamentoContaRepository.findByConta(conta);
	}
	
	@Override
	public void excluirHistorico(AberturaFechamentoConta entity) throws BusinessException {
		aberturaFechamentoContaRepository.delete(entity);		
	}
	
	@Override
	public List<Conta> buscarAtivosPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findEnabledByUsuario(usuario);
	}
	
	@Override
	public List<Conta> buscarSomenteTipoCartaoAtivosPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findOnlyTipoCartaoEnabledByUsuario(usuario);
	}
	
	@Override
	public List<Conta> buscarSomenteTipoCartaoPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findOnlyTipoCartaoByUsuario(usuario);
	}
	
	@Override
	public Conta buscarPorCartaoCredito(CartaoCredito cartao) throws BusinessException {
		return getRepository().findByCartaoCredito(cartao);
	}
}