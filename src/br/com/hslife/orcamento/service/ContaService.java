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
import br.com.hslife.orcamento.entity.BuscaSalva;
import br.com.hslife.orcamento.entity.CartaoCredito;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.entity.PanoramaLancamentoConta;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.repository.BuscaSalvaRepository;
import br.com.hslife.orcamento.repository.ContaRepository;
import br.com.hslife.orcamento.repository.FechamentoPeriodoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoImportadoRepository;
import br.com.hslife.orcamento.repository.PanoramaLancamentoContaRepository;
import br.com.hslife.orcamento.util.Util;

@Service("contaService")
public class ContaService extends AbstractCRUDService<Conta> implements IConta {

	@Autowired
	private ContaRepository repository;
	
	@Autowired
	private ContaComponent component;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private FechamentoPeriodoRepository fechamentoPeriodoRepository;
	
	@Autowired
	private LancamentoImportadoRepository lancamentoImportadoRepository;
	
	@Autowired
	private PanoramaLancamentoContaRepository previsaoLancamentoContaRepository;
	
	@Autowired
	private BuscaSalvaRepository buscaSalvaRepository;
	
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
	
	public void setLancamentoContaRepository(
			LancamentoContaRepository lancamentoContaRepository) {
		this.lancamentoContaRepository = lancamentoContaRepository;
	}

	public void setLancamentoImportadoRepository(
			LancamentoImportadoRepository lancamentoImportadoRepository) {
		this.lancamentoImportadoRepository = lancamentoImportadoRepository;
	}

	public void setFechamentoPeriodoRepository(
			FechamentoPeriodoRepository fechamentoPeriodoRepository) {
		this.fechamentoPeriodoRepository = fechamentoPeriodoRepository;
	}

	public void setPrevisaoLancamentoContaRepository(
			PanoramaLancamentoContaRepository previsaoLancamentoContaRepository) {
		this.previsaoLancamentoContaRepository = previsaoLancamentoContaRepository;
	}

	public void setBuscaSalvaRepository(BuscaSalvaRepository buscaSalvaRepository) {
		this.buscaSalvaRepository = buscaSalvaRepository;
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
		// Cadastra a conta e já realiza a abertura da mesma
		getRepository().save(entity);
	}
	
	@Override
	public void ativarConta(Conta conta) throws BusinessException {	
		// Seta a conta como ativa
		conta.setAtivo(true);
		
		// Exclui a data e saldo de fechamento
		conta.setDataFechamento(null);
		conta.setSaldoFinal(0);
		
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
		
		// Salva a conta
		getRepository().update(conta);
	}
	
	@Override
	public void excluir(Conta entity) throws BusinessException {
		if (getRepository().existsLinkages(entity)) {
			throw new BusinessException("Não é possível excluir! Existem registros relacionamentos com a conta!");
		} else {
			// Exclui as previsões dos lançamentos da conta
			for (PanoramaLancamentoConta previsao : previsaoLancamentoContaRepository.findByConta(entity)) {
				previsaoLancamentoContaRepository.delete(previsao);
			}
			
			// Exclui as buscas salvas
			for (BuscaSalva busca : buscaSalvaRepository.findContaAndTipoContaAndContaAtivaByUsuario(entity, null, null, entity.getUsuario())) {
				buscaSalvaRepository.delete(busca);
			}
			
			// Exclui os lançamentos importados
			for (LancamentoImportado importado : lancamentoImportadoRepository.findByConta(entity)) {
				lancamentoImportadoRepository.delete(importado);
			}
			
			// Exclui os fechamentos de períodos com status REABERTURA
			for (FechamentoPeriodo fechamento : fechamentoPeriodoRepository.findByContaAndOperacaoConta(entity, OperacaoConta.REABERTURA)) {
				fechamentoPeriodoRepository.delete(fechamento);
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
	public List<Conta> buscarPorTipoContaEUsuario(TipoConta tipoConta, Usuario usuario) throws BusinessException {
		return getRepository().findByTipoContaAndUsuario(tipoConta, usuario);
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
	
	@Override
	public List<Conta> buscarDescricaoOuTipoContaOuAtivoPorUsuario(String descricao, TipoConta tipoConta, Usuario usuario, Boolean ativo) throws BusinessException {
		return getRepository().findDescricaoOrTipoContaOrAtivoByUsuario(descricao, tipoConta, usuario, ativo);
	}
}