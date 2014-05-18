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
import java.util.Date;
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

@Component("fechamentoPeriodoMB")
@Scope("session")
public class FechamentoPeriodoController extends AbstractCRUDController<FechamentoPeriodo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3650197694657342579L;

	@Autowired
	private IConta contaService;
	
	@Autowired
	private IFechamentoPeriodo service;

	private Conta contaSelecionada;
	private boolean exibirReabertura;
	private Date dataFechamento;
	
	public FechamentoPeriodoController() {	
		super(new FechamentoPeriodo());
		moduleTitle = "Fechamento de Período";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new FechamentoPeriodo();
		listEntity = new ArrayList<FechamentoPeriodo>();
		dataFechamento = null;
	}

	public List<Conta> getListaConta() {
		try {
			// Obtém o valor da opção do sistema
			OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("CONTA_EXIBIR_INATIVAS", getUsuarioLogado());
			
			// Determina qual listagem será retornada
			if (opcao != null && Boolean.valueOf(opcao.getValor()))
				return contaService.buscarPorUsuario(getUsuarioLogado());
			else 
				return contaService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public List<Conta> getListaContaAtiva() {
		try {
			return contaService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public void find() {		
		try {
			if (exibirReabertura) {
				listEntity = getService().buscarPorContaEOperacaoConta(contaSelecionada, OperacaoConta.REABERTURA);
			} else {
				listEntity = getService().buscarPorContaEOperacaoConta(contaSelecionada, OperacaoConta.FECHAMENTO);
			}	
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String fecharPeriodo() {
		try {
			getService().fecharPeriodo(dataFechamento, contaSelecionada);
			infoMessage("Período fechado com sucesso!");
			// Verifica se a listagem de resultados está nula ou não para poder efetuar novamente a busca
			if (listEntity != null && !listEntity.isEmpty()) {
				// Inicializa os objetos
				initializeEntity();
				
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", getUsuarioLogado());
							
				// Determina se a busca será executada novamente
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {					
					find();
				}
			} else {
				initializeEntity();
			}
			actionTitle = "";
			return "/pages/" + entity.getClass().getSimpleName() + "/list" + entity.getClass().getSimpleName(); 
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String reabrirPeriodo() {
		try {
			getService().reabrirPeriodo(entity);
			infoMessage("Período reaberto com sucesso!");
			// Verifica se a listagem de resultados está nula ou não para poder efetuar novamente a busca
			if (listEntity != null && !listEntity.isEmpty()) {
				// Inicializa os objetos
				initializeEntity();
				
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", getUsuarioLogado());
							
				// Determina se a busca será executada novamente
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {					
					find();
				}
			} else {
				initializeEntity();
			}
			actionTitle = "";
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}

	public Conta getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(Conta contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}

	public void setService(IFechamentoPeriodo service) {
		this.service = service;
	}

	public IFechamentoPeriodo getService() {
		return service;
	}

	public void setContaService(IConta contaService) {
		this.contaService = contaService;
	}

	public boolean isExibirReabertura() {
		return exibirReabertura;
	}

	public void setExibirReabertura(boolean exibirReabertura) {
		this.exibirReabertura = exibirReabertura;
	}

	public Date getDataFechamento() {
		return dataFechamento;
	}

	public void setDataFechamento(Date dataFechamento) {
		this.dataFechamento = dataFechamento;
	}
}