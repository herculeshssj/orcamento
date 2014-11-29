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

import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IResumoEstatistica;
import br.com.hslife.orcamento.model.SaldoAtualConta;

@Component("saldoAtualContasMB")
@Scope("request")
public class SaldoAtualContasController extends AbstractController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1202530011221580347L;

	private double saldoTotalContas;
	
	@Autowired
	private IResumoEstatistica service;
	
	public SaldoAtualContasController() {
		moduleTitle = "Saldo Atual das Contas";
	}

	@Override
	protected void initializeEntity() {
		
	}
	
	@Override
	public String startUp() {
		this.getContasAtivas();
		return "/pages/ResumoEstatistica/saldoAtualContas";
	}

	public IResumoEstatistica getService() {
		return service;
	}

	public void setService(IResumoEstatistica service) {
		this.service = service;
	}

	public List<SaldoAtualConta> getContasAtivas() {
		try {
			List<SaldoAtualConta> saldos = new ArrayList<>();
			for (SaldoAtualConta saldo : getService().gerarSaldoAtualContas(getUsuarioLogado())) {
				if (saldo.isAtivo()) {
					saldos.add(saldo);
					saldoTotalContas += saldo.getSaldoAtual();
				}
			}
			return saldos;			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<SaldoAtualConta>();
	}

	public double getSaldoTotalContas() {
		return saldoTotalContas;
	}

	public void setSaldoTotalContas(double saldoTotalContas) {
		this.saldoTotalContas = saldoTotalContas;
	}

	public List<SaldoAtualConta> getContasInativas() {
		try {
			List<SaldoAtualConta> saldos = new ArrayList<>();
			for (SaldoAtualConta saldo : getService().gerarSaldoAtualContas(getUsuarioLogado())) {
				if (!saldo.isAtivo()) {
					saldos.add(saldo);
				}
			}
			return saldos;			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<SaldoAtualConta>();
	}
}