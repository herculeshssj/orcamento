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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Banco;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.ContaConjunta;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IBanco;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IMoeda;

@Component("contaMB")
@Scope("session")
public class ContaController extends AbstractCRUDController<Conta> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5542222141716552390L;
	
	@Autowired
	private IConta service;
	
	@Autowired
	private IBanco bancoService;
	
	@Autowired
	private IMoeda moedaService;
	
	private TipoConta tipoSelecionado;
	private boolean somenteAtivos = true;
	
	private String opcaoLancamentos;
	
	private ContaConjunta contaConjunta = new ContaConjunta();

	public ContaController() {
		super(new Conta());
		
		moduleTitle = "Contas";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Conta();
		listEntity = new ArrayList<Conta>();	
	}
	
	@Override
	public void find() {
		try {
			if (this.tipoSelecionado == null)
				listEntity = getService().buscarDescricaoOuTipoContaOuAtivoPorUsuario(null, null, getUsuarioLogado(), somenteAtivos);
			else
				listEntity = getService().buscarDescricaoOuTipoContaOuAtivoPorUsuario(null, new TipoConta[]{tipoSelecionado}, getUsuarioLogado(), somenteAtivos);
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String save() {
		entity.setUsuario(getUsuarioLogado());
		// Caso a operação seja 'edit' e a conta não seja conjunta,
		// apaga todos os registros existentes
		if (operation.equals("edit") && !entity.isContaConjunta()) {
			for (Iterator<ContaConjunta> i = entity.getContasConjunta().iterator(); i.hasNext(); ) {
				if (i.next() != null)
					i.remove();
			}
		}
		return super.save();
	}

	public String ativarContaView() {
		try {
			actionTitle = " - Ativar";
			entity = getService().buscarPorID(idEntity);
			return "/pages/Conta/ativarDesativarConta";
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
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
			return super.list();
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String desativarContaView() {
		try {
			actionTitle = " - Desativar";
			entity = getService().buscarPorID(idEntity);
			entity.setDataFechamento(new Date());
			return "/pages/Conta/ativarDesativarConta";
		} catch (ValidationException | BusinessException be) {
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
			return super.list();
		} catch (ApplicationException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public void adicionarContaConjunta() {
		try {
			contaConjunta.setConta(entity);
			contaConjunta.validate();
			contaConjunta.setOrdem(entity.getContasConjunta().size() + 1);
			if (contaConjunta.isTitular())
				for (ContaConjunta cj : entity.getContasConjunta()) 
					cj.setTitular(false);
			entity.getContasConjunta().add(contaConjunta);
			contaConjunta = new ContaConjunta();
		} catch (ValidationException be) {
			warnMessage(be.getMessage());
		}		
	}
	
	public void removerContaConjunta() {
		entity.getContasConjunta().remove(contaConjunta);
		contaConjunta = new ContaConjunta();
		
		// Renumera a ordem das contas conjuntas
		int i = 1;
		for (ContaConjunta cj : entity.getContasConjunta()) {
			cj.setOrdem(i);
			i++;
		}
	}
	
	public List<Banco> getListaBanco() {
		try {
			List<Banco> resultado = bancoService.buscarAtivosPorUsuario(getUsuarioLogado());
			// Lógica para incluir o banco inativo da entidade na combo
			if (resultado != null && entity.getBanco() != null) {
				if (!resultado.contains(entity.getBanco())) {
					entity.getBanco().setAtivo(true);
					resultado.add(entity.getBanco());
				}
			}
			return resultado;
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<Moeda> getListaMoeda() {
		try {
			List<Moeda> resultado = moedaService.buscarAtivosPorUsuario(getUsuarioLogado());
			// Lógica para incluir o banco inativo da entidade na combo
			if (resultado != null && entity.getMoeda() != null) {
				if (!resultado.contains(entity.getMoeda())) {
					entity.getMoeda().setAtivo(true);
					resultado.add(entity.getMoeda());
				}
			}
			return resultado;
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public IConta getService() {
		return service;
	}

	public void setService(IConta service) {
		this.service = service;
	}

	public String getOpcaoLancamentos() {
		return opcaoLancamentos;
	}

	public void setOpcaoLancamentos(String opcaoLancamentos) {
		this.opcaoLancamentos = opcaoLancamentos;
	}

	public boolean isSomenteAtivos() {
		return somenteAtivos;
	}

	public void setSomenteAtivos(boolean somenteAtivos) {
		this.somenteAtivos = somenteAtivos;
	}

	public TipoConta getTipoSelecionado() {
		return tipoSelecionado;
	}

	public void setTipoSelecionado(TipoConta tipoSelecionado) {
		this.tipoSelecionado = tipoSelecionado;
	}

	public ContaConjunta getContaConjunta() {
		return contaConjunta;
	}

	public void setContaConjunta(ContaConjunta contaConjunta) {
		this.contaConjunta = contaConjunta;
	}
}