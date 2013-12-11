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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.component.ContaComponent;
import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IFechamentoPeriodo;
import br.com.hslife.orcamento.model.CriterioLancamentoConta;
import br.com.hslife.orcamento.repository.FechamentoPeriodoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;

@Service("fechamentoPeriodoService")
public class FechamentoPeriodoService extends AbstractCRUDService<FechamentoPeriodo> implements IFechamentoPeriodo {
	
	@Autowired
	private FechamentoPeriodoRepository repository;
	
	@Autowired
	private ContaComponent component;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private UsuarioComponent usuarioComponent;

	public FechamentoPeriodoRepository getRepository() {
		return repository;
	}

	public void setRepository(FechamentoPeriodoRepository repository) {
		this.repository = repository;
	}

	public ContaComponent getComponent() {
		return component;
	}

	public void setComponent(ContaComponent component) {
		this.component = component;
	}

	public void setLancamentoRepository(
			LancamentoContaRepository lancamentoRepository) {
		this.lancamentoContaRepository = lancamentoRepository;
	}

	public void setUsuarioComponent(UsuarioComponent usuarioComponent) {
		this.usuarioComponent = usuarioComponent;
	}

	@Override
	public List<FechamentoPeriodo> buscarPorContaEOperacaoConta(Conta conta, OperacaoConta operacaoConta) throws BusinessException {
		return getRepository().findByContaAndOperacaoConta(conta, operacaoConta);
	}
	
	public void fecharPeriodo(Date dataFechamento, Conta conta) throws BusinessException {
		getComponent().fecharPeriodo(dataFechamento, conta);
	}
	
	public void reabrirPeriodo(FechamentoPeriodo entity) throws BusinessException {
		// Obtém-se o último fechamento realizado
		FechamentoPeriodo fechamentoAnterior = getRepository().findUltimoFechamentoByConta(entity.getConta());
				
		// Guarda a data do último fechamento como data final do intervalo
		Date dataFim;
		
		if (fechamentoAnterior == null) {			
			dataFim = entity.getConta().getDataAbertura();
		} else {			
			dataFim = fechamentoAnterior.getData(); 
		}
		
		// Busca os fechamentos para setar como reabertos
		List<FechamentoPeriodo> fechamentos = getRepository().findFechamentosPosteriores(entity);
		
		// Seta os fechamentos encontrados para reabertos e salva
		for (FechamentoPeriodo fp : fechamentos) {
			fp.setOperacao(OperacaoConta.REABERTURA);
			fp.setDataAlteracao(new Date());
			fp.setUsuario(usuarioComponent.getUsuarioLogado().getLogin());
			getRepository().update(fp);
		}
		
		// Obtém-se o atual último fechamento
		fechamentoAnterior = getRepository().findUltimoFechamentoByConta(entity.getConta());
		
		// Guarda a data do fechamento como data inicial do intervalo
		Date dataInicio;
		
		if (fechamentoAnterior == null) {			
			dataInicio = entity.getConta().getDataAbertura();
		} else {			
			dataInicio = fechamentoAnterior.getData(); 
		}
		
		// Incrementa a data de início do intervalo
		Calendar temp = Calendar.getInstance();
		temp.setTime(dataInicio);
		temp.add(Calendar.DAY_OF_YEAR, 1);
		
		// Busca os lançamentos do intervalo e seta como não quitados
		CriterioLancamentoConta criterio = new CriterioLancamentoConta();
		criterio.setConta(entity.getConta());
		criterio.setDescricao("");
		criterio.setDataInicio(temp.getTime());
		criterio.setDataFim(dataFim);
		criterio.setAgendado(false);
		for (LancamentoConta l : getComponent().buscarPorCriterioLancamentoConta(criterio)) {
			LancamentoConta lancamento = lancamentoContaRepository.findById(l.getId());
			lancamento.setQuitado(false);
			lancamentoContaRepository.update(lancamento);
		}
	}

	@Override
	public void validar(FechamentoPeriodo entity) throws BusinessException {
		// TODO Auto-generated method stub
		
	}
}