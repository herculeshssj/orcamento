/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor
	
	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

    para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 - 
	
	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/

package br.com.hslife.orcamento.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.CartaoCredito;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFechamentoPeriodo;
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
	private IFechamentoPeriodo fechamentoPeriodoService;
	
	@Autowired
	private FechamentoPeriodoRepository fechamentoPeriodoRepository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private LancamentoImportadoRepository lancamentoImportadoRepository;

	public IFechamentoPeriodo getFechamentoPeriodoService() {
		return fechamentoPeriodoService;
	}
	
	public FechamentoPeriodoRepository getFechamentoPeriodoRepository() {
		this.fechamentoPeriodoRepository.setSessionFactory(this.sessionFactory);
		return fechamentoPeriodoRepository;
	}

	public ContaRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}

	public LancamentoContaRepository getLancamentoContaRepository() {
		this.lancamentoContaRepository.setSessionFactory(this.sessionFactory);
		return lancamentoContaRepository;
	}

	public LancamentoImportadoRepository getLancamentoImportadoRepository() {
		this.lancamentoImportadoRepository.setSessionFactory(this.sessionFactory);
		return lancamentoImportadoRepository;
	}

	@Override
	public void validar(Conta entity) {
		if (entity.getDataFechamento() != null) {
			if (entity.getDataAbertura().after(entity.getDataFechamento()) && entity.getDataFechamento().before(entity.getDataAbertura())) {
				throw new BusinessException("Data de abertura (" + Util.formataDataHora(entity.getDataAbertura(), Util.DATA) + ") e data de fechamento (" +  Util.formataDataHora(entity.getDataFechamento(), Util.DATA)+") incorretos!");
			}
		}
	}
	
	@Override
	public void cadastrar(Conta entity) {
		// Cadastra a conta e já realiza a abertura da mesma
		getRepository().save(entity);
	}
	
	@Override
	public void ativarConta(Conta conta) {	
		// Seta a conta como ativa
		conta.setAtivo(true);
		
		// Exclui a data e saldo de fechamento
		conta.setDataFechamento(null);
		conta.setSaldoFinal(0);
		
		// Salva a conta
		getRepository().update(conta);
	}
	
	@Override
	public void desativarConta(Conta conta, String situacaoLancamentos, boolean fecharPeriodo) {
		
		if (situacaoLancamentos.equals("QUITAR")) {			
			// Busca o último lançamento cadastrado, caso não exista cria um novo lançamento e define a data de 
			// pagamento com a data atual
			LancamentoConta ultimoLancamento;
			if (getLancamentoContaRepository().findLastLancamentoContaByConta(conta) != null)
				ultimoLancamento = getLancamentoContaRepository().findLastLancamentoContaByConta(conta);
			else {
				ultimoLancamento = new LancamentoConta();
				ultimoLancamento.setDescricao("Último lançamento - Encerramento da conta");
				ultimoLancamento.setMoeda(conta.getMoeda());
				ultimoLancamento.setStatusLancamentoConta(StatusLancamentoConta.REGISTRADO);
				ultimoLancamento.setDataPagamento(new Date());
				ultimoLancamento.setValorPago(0);
				ultimoLancamento.setTipoLancamento(TipoLancamento.DESPESA);
			}				
			
			// Realiza o fechamento do período, caso o usuário tenha optado em fazê-lo
			if (fecharPeriodo)
				getFechamentoPeriodoService().fecharPeriodo(ultimoLancamento.getDataPagamento(), conta);
			
			// Seta a data de fechamento da conta com a data de pagamento do último lançamento
			conta.setDataFechamento(ultimoLancamento.getDataPagamento());
			
			// Exclui todos os lançamentos existentes após a data de fechamento da conta
			getLancamentoContaRepository().deleteAllLancamentoContaAfterDateByConta(conta.getDataFechamento(), conta);
			
			// Define o saldo final da conta com base no último fechamento realizado
			conta.setSaldoFinal(getFechamentoPeriodoService().buscarUltimoFechamentoPeriodoPorConta(conta).getSaldo());
			
		} else if (situacaoLancamentos.equals("EXCLUIR")) {						
			// Realiza o fechamento do período, caso o usuário tenha optado em fazê-lo
			if (fecharPeriodo)
				getFechamentoPeriodoService().fecharPeriodo(conta.getDataFechamento(), conta);
						
			// Exclui todos os lançamentos existentes após a data de fechamento da conta
			getLancamentoContaRepository().deleteAllLancamentoContaAfterDateByConta(conta.getDataFechamento(), conta);
						
			// Define o saldo final da conta com base no último fechamento realizado
			conta.setSaldoFinal(getFechamentoPeriodoService().buscarUltimoFechamentoPeriodoPorConta(conta).getSaldo());			
		}
		
		// Seta a conta como inativa
		conta.setAtivo(false);
		
		// Salva a conta
		getRepository().update(conta);
	}
	
	@Override
	public void excluir(Conta entity) {
		if (getRepository().existsLinkages(entity)) {
			throw new BusinessException("Não é possível excluir! Existem registros relacionamentos com a conta!");
		} else {
			
			// Exclui os lançamentos importados
			for (LancamentoImportado importado : getLancamentoImportadoRepository().findByConta(entity)) {
				getLancamentoImportadoRepository().delete(importado);
			}
			
			// Exclui todos os fechamentos de períodos
			// Neste caso é necessário ser o próprio repositório para poder ficar dentro do mesmo
			// contexto transacional
			for (FechamentoPeriodo fechamento : getFechamentoPeriodoRepository().findAllByConta(entity)) {
				getFechamentoPeriodoRepository().delete(fechamento);
			}
			
			// Exclui a conta
			super.excluir(entity);
		}		
	}
	
	@Override
	public List<Conta> buscarTodos() {
		return getRepository().findAll();
	}
	
	@Override
	public List<Conta> buscarPorDescricao(String descricao) {
		return getRepository().findByDescricao(descricao);
	}
	
	@Override
	public List<Conta> buscarPorUsuario(Usuario usuario) {
		return getRepository().findByUsuario(usuario);
	}
	
	@Override
	public List<Conta> buscarTodosAtivos() {
		return getRepository().findAllAtivos();
	}
	
	@Override
	public List<Conta> buscarTodosAtivosPorUsuario(Usuario usuario) {
		return getRepository().findAllAtivosByUsuario(usuario);
	}
	
	@Override
	public List<Conta> buscarPorDescricaoEUsuario(String descricao, Usuario usuario) {
		return getRepository().findByDescricaoAndUsuario(descricao, usuario);
	}
	
	@Override
	public List<Conta> buscarPorTipoContaEUsuario(TipoConta tipoConta, Usuario usuario) {
		return getRepository().findByTipoContaAndUsuario(tipoConta, usuario);
	}
	
	@Override
	public List<Conta> buscarAtivosPorUsuario(Usuario usuario) {
		return getRepository().findEnabledByUsuario(usuario);
	}
	
	@Override
	public List<Conta> buscarSomenteTipoCartaoAtivosPorUsuario(Usuario usuario) {
		return getRepository().findOnlyTipoCartaoEnabledByUsuario(usuario);
	}
	
	@Override
	public List<Conta> buscarSomenteTipoCartaoPorUsuario(Usuario usuario) {
		return getRepository().findOnlyTipoCartaoByUsuario(usuario);
	}
	
	@Override
	public Conta buscarPorCartaoCredito(CartaoCredito cartao) {
		return getRepository().findByCartaoCredito(cartao);
	}
	
	@Override
	public List<Conta> buscarDescricaoOuTipoContaOuAtivoPorUsuario(String descricao, TipoConta[] tipoConta, Usuario usuario, Boolean ativo) {
		return getRepository().findDescricaoOrTipoContaOrAtivoByUsuario(descricao, tipoConta, usuario, ativo);
	}
	
	@Override
	public List<Conta> buscarTodosUsuarioPorFechamentoAutomatico(boolean fechaAutomaticamente) {
		return getRepository().findAllUsuariosByFechamentoAutomatico(fechaAutomaticamente);
	}
}