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

import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.AberturaFechamentoConta;
import br.com.hslife.orcamento.entity.Banco;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IBanco;
import br.com.hslife.orcamento.facade.IConta;

@Component("contaMB")
@Scope("session")
public class ContaController extends AbstractCRUDController<Conta> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5542222141716552390L;

	@Autowired
	private IBanco bancoService;
	
	@Autowired
	private IConta service;
	
	private Banco findBanco;
	private AberturaFechamentoConta aberturaFechamentoConta;
	private String opcaoLancamentos;
	private Conta contaSelecionada;
	
	private List<SelectItem> listaBanco;
	private List<SelectItem> listaConta;
	private List<AberturaFechamentoConta> historicoAberturaFechamentoConta;
	
	public ContaController() {
		super(new Conta());
		
		moduleTitle = "Contas";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Conta();
		listEntity = new ArrayList<Conta>();
		aberturaFechamentoConta = null;
		historicoAberturaFechamentoConta = new ArrayList<AberturaFechamentoConta>();		
	}
	
	@Override
	public String startUp() {
		loadCombos();
		return super.startUp();
	}
	
	@Override
	public void find() {
		try {
			listEntity = getService().buscarPorBancoEUsuario(findBanco, getUsuarioLogado());			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String save() {
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}
	
	private void loadCombos() {
		listaBanco = new ArrayList<SelectItem>();
		try {		
			if (operation.equals("edit")) {
				// Carrega os bancos
				for (Banco b : bancoService.buscarPorUsuario(entity.getUsuario())) {
					listaBanco.add(new SelectItem(b, b.getNome(), "", !b.isAtivo()));
				}
			} else {
				// Carrega os bancos
				for (Banco b : bancoService.buscarPorUsuario(getUsuarioLogado())) {
					listaBanco.add(new SelectItem(b, b.getNome(), "", !b.isAtivo()));
				}
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}

	public String ativarConta() {
		try {
			entity = getService().buscarPorID(idEntity);
			validate(operation);
			getService().ativarConta(entity);
			infoMessage("Conta ativada com sucesso!");
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
			loadCombos();
			return super.list();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String desativarContaView() {
		try {
			actionTitle = " - Desativar";
			//operation = "";
			entity = getService().buscarPorID(idEntity);
			entity.setDataFechamento(new Date());
			return goToViewPage;
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String desativarConta() {
		try {
			validate(operation);
			getService().desativarConta(entity, opcaoLancamentos);
			infoMessage("Conta desativada com sucesso!");
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
			loadCombos();
			return super.list();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public List<SelectItem> getListaTipoConta() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		listaSelectItem.add(new SelectItem(TipoConta.CORRENTE, "Conta Corrente"));
		listaSelectItem.add(new SelectItem(TipoConta.POUPANCA, "Conta Poupança"));
		return listaSelectItem;
	}
	
	public void atualizaListaBancos() {
		listaBanco = new ArrayList<SelectItem>();
		try {
			for (Banco b : bancoService.buscarPorUsuario(getUsuarioLogado())) {
				listaBanco.add(new SelectItem(b, b.getNome(), "", !b.isAtivo()));
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void atualizaListaContas() {
		listaConta = new ArrayList<SelectItem>();
		try {
			for (Conta c : getService().buscarPorUsuario(getUsuarioLogado().getId())) {
				listaConta.add(new SelectItem(c, c.getLabel()));
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void atualizaListaBancosCartoes() {
		listaBanco = new ArrayList<SelectItem>();
		try {
			// Carrega os bancos
			for (Banco b : bancoService.buscarPorUsuario(entity.getUsuario())) {
				listaBanco.add(new SelectItem(b, b.getNome(), "", !b.isAtivo()));
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String historicoAberturaFechamentoConta() {
		//operation = "";
		actionTitle = " - Histórico de aberturas e fechamentos";
		//findUsuario = getUsuarioLogado();
		this.atualizaListaContas();
		return "/pages/Conta/historicoAberturaFechamentoConta";
	}
	
	public void exibirHistoricoConta() {
		try {
			if (contaSelecionada == null)
				warnMessage("Selecione uma conta!");				
			else
				historicoAberturaFechamentoConta = getService().buscarHistoricoAberturaFechamentoPorConta(contaSelecionada);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String excluirHistorico() throws BusinessException {
		try {
			getService().excluirHistorico(aberturaFechamentoConta);
			infoMessage("Histórico excluído com sucesso!");
			initializeEntity();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public IConta getService() {
		return service;
	}

	public void setService(IConta service) {
		this.service = service;
	}

	public AberturaFechamentoConta getAberturaFechamentoConta() {
		return aberturaFechamentoConta;
	}

	public void setAberturaFechamentoConta(
			AberturaFechamentoConta aberturaFechamentoConta) {
		this.aberturaFechamentoConta = aberturaFechamentoConta;
	}

	public List<SelectItem> getListaBanco() {
		return listaBanco;
	}

	public void setListaBanco(List<SelectItem> listaBanco) {
		this.listaBanco = listaBanco;
	}

	public List<AberturaFechamentoConta> getHistoricoAberturaFechamentoConta() {
		return historicoAberturaFechamentoConta;
	}

	public void setHistoricoAberturaFechamentoConta(
			List<AberturaFechamentoConta> historicoAberturaFechamentoConta) {
		this.historicoAberturaFechamentoConta = historicoAberturaFechamentoConta;
	}

	public void setBancoService(IBanco bancoService) {
		this.bancoService = bancoService;
	}

	public Banco getFindBanco() {
		return findBanco;
	}

	public void setFindBanco(Banco findBanco) {
		this.findBanco = findBanco;
	}

	public Conta getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(Conta contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}

	public List<SelectItem> getListaConta() {
		return listaConta;
	}

	public void setListaConta(List<SelectItem> listaConta) {
		this.listaConta = listaConta;
	}

	public String getOpcaoLancamentos() {
		return opcaoLancamentos;
	}

	public void setOpcaoLancamentos(String opcaoLancamentos) {
		this.opcaoLancamentos = opcaoLancamentos;
	}
}