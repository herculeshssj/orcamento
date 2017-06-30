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

package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FechamentoPeriodo;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFechamentoPeriodo;

@Component
@Scope("session")
public class FechamentoPeriodoController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8849711178212743444L;

	@Autowired
	private IFechamentoPeriodo service; 
	
	@Autowired
	private IConta contaService;
	
	private Conta contaSelecionada;
	private List<FechamentoPeriodo> listEntity;
	private FechamentoPeriodo entity;
	private String operation;
	
	public FechamentoPeriodoController() {
		moduleTitle = "Fechamento de Período";
	}

	@Override
	public String startUp() {
		actionTitle = "";
		return "/pages/FechamentoPeriodo/listFechamentoPeriodo";
	}
	
	protected void initializeEntity() {
		entity = new FechamentoPeriodo();
		listEntity = new ArrayList<FechamentoPeriodo>();		
	}
	
	public void find() {
		if (contaSelecionada == null) {
			warnMessage("Selecione uma conta!");
			return;
		}
		
		try {
			listEntity = getService().buscarTodosFechamentoPorConta(contaSelecionada); 
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String save() {
		try {
			getService().alterar(entity);
			infoMessage("Registro alterado com sucesso!");
			initializeEntity();
			return startUp();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String delete() {
		try {
			getService().excluir(entity);
			infoMessage("Registro excluído com sucesso!");
			initializeEntity();
			return startUp();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String edit() {
		actionTitle = " - Editar";
		operation = "edit";
		return "/pages/FechamentoPeriodo/formFechamentoPeriodo";
	}
	
	public String view() {
		actionTitle = " - Excluir";
		operation = "delete";
		return "/pages/FechamentoPeriodo/viewFechamentoPeriodo";
	}
	
	public String cancel() {
		return startUp();
	}
	
	public List<Conta> getListaConta() {
		List<Conta> contas = new ArrayList<>();
		try {
			if (getOpcoesSistema().getExibirContasInativas()) {
				contas = contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", new TipoConta[]{TipoConta.CORRENTE, TipoConta.POUPANCA, TipoConta.OUTROS}, getUsuarioLogado(), null);
			} else {
				contas = contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", new TipoConta[]{TipoConta.CORRENTE, TipoConta.POUPANCA, TipoConta.OUTROS}, getUsuarioLogado(), true);
			}
			if (contas != null && !contas.isEmpty() && contaSelecionada == null) {
				contaSelecionada = contas.get(0);
			}
			return contas;
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public int getQuantRegistros() {
		return listEntity == null || listEntity.isEmpty() ? 0 : listEntity.size();
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Conta getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(Conta contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}

	public List<FechamentoPeriodo> getListEntity() {
		return listEntity;
	}

	public void setListEntity(List<FechamentoPeriodo> listEntity) {
		this.listEntity = listEntity;
	}

	public FechamentoPeriodo getEntity() {
		return entity;
	}

	public void setEntity(FechamentoPeriodo entity) {
		this.entity = entity;
	}

	public IFechamentoPeriodo getService() {
		return service;
	}

	public IConta getContaService() {
		return contaService;
	}
}