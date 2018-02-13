/***

	Copyright (c) 2012 - 2021 Hércules S. S. José

	Este arquivo é parte do programa Orçamento Doméstico.


	Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

	modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

	publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

	Licença.


	Este programa é distribuído na esperança que possa ser útil, mas SEM

	NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer

	MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor

	GNU em português para maiores detalhes.


	Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob

	o nome de "LICENSE" junto com este programa, se não, acesse o site do

	projeto no endereco https://github.com/herculeshssj/orcamento ou escreva

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth

	Floor, Boston, MA  02110-1301, USA.


	Para mais informações sobre o programa Orçamento Doméstico e seu autor

	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

	para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.TipoCartao;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.facade.IResumoEstatistica;
import br.com.hslife.orcamento.model.SaldoAtualConta;

@Component("saldoAtualContasMB")
@Scope("request")
public class SaldoAtualContasController extends AbstractController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1202530011221580347L;
	
	private List<SaldoAtualConta> listEntity = new ArrayList<SaldoAtualConta>();
	
	@Autowired
	private IResumoEstatistica service;
	
	@Autowired
	private IMoeda moedaService;
	
	private boolean lancamentoAgendado = false;
	
	public SaldoAtualContasController() {
		moduleTitle = "Saldo Atual das Contas";
	}

	@Override
	protected void initializeEntity() {
		
	}
	
	@Override
	@PostConstruct
	public String startUp() {
		this.gerarSaldoAtualContas();
		return "/pages/ResumoEstatistica/saldoAtualContas";
	}
	
	public void find() {
		this.gerarSaldoAtualContas();
	}
	
	private void gerarSaldoAtualContas() {
		try {
		listEntity = getService().gerarSaldoAtualContas(lancamentoAgendado, getUsuarioLogado());
		if (listEntity == null || listEntity.isEmpty())
			listEntity = new ArrayList<>();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}

	public IResumoEstatistica getService() {
		return service;
	}

	public void setService(IResumoEstatistica service) {
		this.service = service;
	}

	public List<SaldoAtualConta> getLimiteCartaoCredito() {
		List<SaldoAtualConta> saldoAtualConta = new ArrayList<>();
		for (SaldoAtualConta saldo : listEntity) 
			if (saldo.isAtivo() && saldo.getTipoConta().equals(TipoConta.CARTAO) && saldo.getTipoCartao().equals(TipoCartao.CREDITO)) 
				saldoAtualConta.add(saldo);
		return saldoAtualConta;
	}
	
	public List<SaldoAtualConta> getContasAtivas() {
		List<SaldoAtualConta> saldoAtualConta = new ArrayList<>();
		for (SaldoAtualConta saldo : listEntity) 
			if (saldo.isAtivo() && (saldo.getTipoCartao() == null || !saldo.getTipoCartao().equals(TipoCartao.CREDITO))) 
				saldoAtualConta.add(saldo);
		return saldoAtualConta;				
	}

	public double getSaldoTotalContas() {
		double resultado = 0.0;
		for (SaldoAtualConta saldo : listEntity) 
			if (saldo.isAtivo() && !saldo.getTipoConta().equals(TipoConta.CARTAO)) 
				resultado += saldo.getSaldoAtual();
		return resultado;
	}

	public List<SaldoAtualConta> getContasInativas() {		
		List<SaldoAtualConta> saldoAtualConta = new ArrayList<>();
		for (SaldoAtualConta saldo : listEntity) 
			if (!saldo.isAtivo()) 
				saldoAtualConta.add(saldo);
		return saldoAtualConta;			
	}
	
	public Moeda getMoedaPadrao() {
		try {
			return moedaService.buscarPadraoPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new Moeda();
	}

	public boolean isLancamentoAgendado() {
		return lancamentoAgendado;
	}

	public void setLancamentoAgendado(boolean lancamentoAgendado) {
		this.lancamentoAgendado = lancamentoAgendado;
	}
}
