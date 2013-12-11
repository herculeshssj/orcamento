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

package br.com.hslife.orcamento.component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.model.CriterioLancamentoConta;
import br.com.hslife.orcamento.repository.ContaRepository;
import br.com.hslife.orcamento.repository.FechamentoPeriodoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.util.Util;

@Component
public class ContaComponent {

	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private FechamentoPeriodoRepository fechamentoPeriodoRepository;
	
	@Autowired
	private UsuarioComponent usuarioComponent;

	public void setLancamentoContaRepository(
			LancamentoContaRepository lancamentoContaRepository) {
		this.lancamentoContaRepository = lancamentoContaRepository;
	}

	public void setFechamentoPeriodoRepository(
			FechamentoPeriodoRepository fechamentoPeriodoRepository) {
		this.fechamentoPeriodoRepository = fechamentoPeriodoRepository;
	}

	public void setContaRepository(ContaRepository contaRepository) {
		this.contaRepository = contaRepository;
	}
	
	public void setUsuarioComponent(UsuarioComponent usuarioComponent) {
		this.usuarioComponent = usuarioComponent;
	}
	
	public List<LancamentoConta> buscarPorCriterioLancamentoConta(CriterioLancamentoConta criterio) throws BusinessException {
		return lancamentoContaRepository.findByCriterioLancamentoConta(criterio);
	}
	
	public FechamentoPeriodo buscarUltimoFechamentoPeriodoPorConta(Conta conta) {
		return fechamentoPeriodoRepository.findUltimoFechamentoByConta(conta);
	}
	
	public double calcularSaldoLancamentos(List<LancamentoConta> lancamentos) {		
		double total = 0.0;
		for (LancamentoConta l : lancamentos) {
			if (l.getTipoLancamento().equals(TipoLancamento.RECEITA)) {
				total += l.getValorPago();
			} else {
				total -= l.getValorPago();
			}
		}
		return Util.arredondar(total);
	}

	@SuppressWarnings("deprecation")
	public void fecharPeriodo(Date dataFechamento, Conta conta) throws BusinessException {
		// Obtém-se o último fechamento realizado
		FechamentoPeriodo fechamentoAnterior = fechamentoPeriodoRepository.findUltimoFechamentoByConta(conta);
		
		double saldoFechamentoAnterior = 0;
		
		if (fechamentoAnterior == null) {
			saldoFechamentoAnterior = conta.getSaldoInicial();
		} else {
			saldoFechamentoAnterior = fechamentoAnterior.getSaldo();
		}
		
		// Incrementa a data do fechamento anterior
		Calendar temp = Calendar.getInstance();
		if (fechamentoAnterior != null) {
			temp.setTime(fechamentoAnterior.getData());
			temp.add(Calendar.DAY_OF_YEAR, 1);
		}
		else
			temp.setTime(conta.getDataAbertura());
		
		
		// Calcula o saldo do período
		CriterioLancamentoConta criterio = new CriterioLancamentoConta();
		criterio.setConta(conta);
		criterio.setDescricao("");
		criterio.setDataInicio(temp.getTime());
		criterio.setDataFim(dataFechamento);
		criterio.setAgendado(false);
		double saldoFechamento = this.calcularSaldoLancamentos(this.buscarPorCriterioLancamentoConta(criterio));
		
		// Cria o novo fechamento para a conta
		FechamentoPeriodo novoFechamento = new FechamentoPeriodo();
		novoFechamento.setConta(conta);
		novoFechamento.setData(dataFechamento);
		novoFechamento.setOperacao(OperacaoConta.FECHAMENTO);
		novoFechamento.setDataAlteracao(new Date());
		novoFechamento.setUsuario(usuarioComponent.getUsuarioLogado().getLogin());
		novoFechamento.setSaldo(saldoFechamentoAnterior + saldoFechamento);
		
		// Obtém o mês e ano da data de fechamento
		temp.setTime(dataFechamento);
		novoFechamento.setMes(temp.getTime().getMonth() + 1);
		novoFechamento.setAno(temp.get(Calendar.YEAR));
		
		// Quita os lançamentos do período
		for (LancamentoConta l : this.buscarPorCriterioLancamentoConta(criterio)) {
			LancamentoConta lancamento = this.lancamentoContaRepository.findById(l.getId());
			lancamento.setQuitado(true);
			lancamentoContaRepository.update(lancamento);
		}
		
		// Salva o fechamento
		fechamentoPeriodoRepository.save(novoFechamento);
	}		
}