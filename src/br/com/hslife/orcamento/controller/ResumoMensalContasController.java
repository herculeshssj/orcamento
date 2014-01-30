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

package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFechamentoPeriodo;
import br.com.hslife.orcamento.facade.IResumoEstatistica;
import br.com.hslife.orcamento.model.ResumoMensalContas;
import br.com.hslife.orcamento.model.SaldoAtualConta;

@Component("resumoMensalContasMB")
@Scope("session")
public class ResumoMensalContasController extends AbstractController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1202530011221580347L;

	private boolean lancamentoAgendado;
	private double saldoTotalContas;
	private List<SaldoAtualConta> contasAtivas = new ArrayList<>();
	private List<SaldoAtualConta> contasInativas = new ArrayList<>();
	
	@Autowired
	private IResumoEstatistica service;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private IFechamentoPeriodo fechamentoPeriodoService;
	
	private Conta contaSelecionada;
	private FechamentoPeriodo fechamentoSelecionado;
	private ResumoMensalContas resumoMensal;
	
	public ResumoMensalContasController() {
		moduleTitle = "Resumo Mensal das Contas";
	}

	@Override
	protected void initializeEntity() {
		// TODO Auto-generated method stub
		//contasAtivas = new ArrayList<>();
		///contasInativas = new ArrayList<>();
		///saldoTotalContas = 0;
	}
	
	@Override
	public String startUp() {
		//initializeEntity();
		//this.getListaSaldoAtualConta(new Object());
		return "/pages/ResumoEstatistica/resumoMensalContas";
	}
	
	public String find() {
		try {
			resumoMensal = getService().gerarRelatorioResumoMensalContas(contaSelecionada, fechamentoSelecionado);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	@SuppressWarnings("null")
	public List<Conta> getListaConta() {
		try {
			// Variável que armazenará a lista de contas
			List<Conta> contas;
			// Obtém o valor da opção do sistema
			OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("CONTA_EXIBIR_INATIVAS", getUsuarioLogado());
			
			// Determina qual listagem será retornada
			if (opcao != null && Boolean.valueOf(opcao.getValor())) {
				contas = contaService.buscarPorUsuario(getUsuarioLogado().getId());
				if (contas != null || contas.size() != 0) {
					contaSelecionada = contas.get(0);
				}
				return contas;
			}
			else {
				contas = contaService.buscarAtivosPorUsuario(getUsuarioLogado());
				if (contas != null || contas.size() != 0) {
					contaSelecionada = contas.get(0);
				}
				return contas;
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public List<FechamentoPeriodo> getListaFechamentoPeriodo() {
		try {
				return fechamentoPeriodoService.buscarPorContaEOperacaoConta(contaSelecionada, OperacaoConta.FECHAMENTO);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<FechamentoPeriodo>();
	}
	
	public void atualizaListaFechamentoPeriodo() {
		this.getListaFechamentoPeriodo();
	}
	
	public void getListaSaldoAtualConta(Object document) {
	/*	try {
			List<SaldoAtualConta> saldos = new ArrayList<>();
			saldos = getService().gerarSaldoAtualContas(lancamentoAgendado, getUsuarioLogado());
			for (SaldoAtualConta saldo : saldos) {
				if (saldo.isAtivo()) {
					contasAtivas.add(saldo);
					saldoTotalContas += saldo.getSaldoAtual();
				} else {
					contasInativas.add(saldo);
				}
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}*/		
	}

	public boolean isLancamentoAgendado() {
		return lancamentoAgendado;
	}

	public void setLancamentoAgendado(boolean lancamentoAgendado) {
		this.lancamentoAgendado = lancamentoAgendado;
	}

	public IResumoEstatistica getService() {
		return service;
	}

	public void setService(IResumoEstatistica service) {
		this.service = service;
	}

	public List<SaldoAtualConta> getContasAtivas() {
		return contasAtivas;
	}

	public void setContasAtivas(List<SaldoAtualConta> contasAtivas) {
		this.contasAtivas = contasAtivas;
	}

	public double getSaldoTotalContas() {
		return saldoTotalContas;
	}

	public void setSaldoTotalContas(double saldoTotalContas) {
		this.saldoTotalContas = saldoTotalContas;
	}

	public List<SaldoAtualConta> getContasInativas() {
		return contasInativas;
	}

	public void setContasInativas(List<SaldoAtualConta> contasInativas) {
		this.contasInativas = contasInativas;
	}
	public Conta getContaSelecionada() {
		return contaSelecionada;
	}
	public void setContaSelecionada(Conta contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}
	public void setContaService(IConta contaService) {
		this.contaService = contaService;
	}
	public void setFechamentoPeriodoService(
			IFechamentoPeriodo fechamentoPeriodoService) {
		this.fechamentoPeriodoService = fechamentoPeriodoService;
	}

	public ResumoMensalContas getResumoMensal() {
		return resumoMensal;
	}

	public void setResumoMensal(ResumoMensalContas resumoMensal) {
		this.resumoMensal = resumoMensal;
	}

	public FechamentoPeriodo getFechamentoSelecionado() {
		return fechamentoSelecionado;
	}

	public void setFechamentoSelecionado(FechamentoPeriodo fechamentoSelecionado) {
		this.fechamentoSelecionado = fechamentoSelecionado;
	}
}