/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

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

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento ou escreva 
    
    para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor, 
    
    Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor entre  

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.ContaCompartilhada;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IContaCompartilhada;
import br.com.hslife.orcamento.facade.IResumoEstatistica;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.model.PanoramaLancamentoConta;
import br.com.hslife.orcamento.util.Util;

@Component("panoramaLancamentoContaMB")
@Scope("session")
public class PanoramaLancamentoContaController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1909509361923924981L;

	@Autowired
	private IResumoEstatistica service;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private IContaCompartilhada contaCompartilhadaService;
	
	private CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
	private int ano = Calendar.getInstance().get(Calendar.YEAR);
	
	private PanoramaLancamentoConta entity;
	private List<PanoramaLancamentoConta> listEntity;
	
	public PanoramaLancamentoContaController() {
		moduleTitle = "Panorama dos Lançamentos da Conta";
	}
	
	@Override
	public String startUp() {
		return "/pages/ResumoEstatistica/panoramaLancamentoConta";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new PanoramaLancamentoConta();
		listEntity = new LinkedList<PanoramaLancamentoConta>();
	}
	
	public String find() {
		if (criterioBusca.getConta() == null) {
			warnMessage("Informe a conta!");
			return "";
		}
		
		if (ano < 1970) {
			warnMessage("Ano deve ser maior que 1970!");
		} else {
			criterioBusca.setDataInicio(Util.primeiroDiaAno(ano));
			criterioBusca.setDataFim(Util.ultimoDiaAno(ano));
			criterioBusca.setDescricao("");
			try {
				listEntity = getService().gerarRelatorioPanoramaLancamentoConta(criterioBusca, ano);
				
				if (listEntity == null || listEntity.size() == 0) {
					warnMessage("Nenhum resultado encontrado. Relatório não disponível para visualização.");
				} else {
					infoMessage("Relatório " + ano + " gerado com sucesso!");
				}
			} catch (ValidationException | BusinessException be) {
				errorMessage(be.getMessage());
			}
		}
		return "";
	}
	
	public String verRelatorioCompleto() {
		if (listEntity ==  null || listEntity.size() == 0) {
			warnMessage("Nenhum resultado disponível. Relatório não disponível para visualização.");
			return "";
		} else { 
			return "/pages/ResumoEstatistica/planilhaPanoramaLancamentoConta";
		}
	}
	
	public List<Conta> getListaContaAtiva() {
		Set<Conta> contas = new HashSet<>();
		try {
			contas.addAll(contaService.buscarAtivosPorUsuario(getUsuarioLogado()));
			
			// Traz as contas compartilhadas para com o usuário atualmente logado
			List<ContaCompartilhada> contasCompartilhadas = contaCompartilhadaService.buscarTodosPorUsuario(getUsuarioLogado());
									
			// Acrescenta no Set as contas compartilhadas dos demais usuários
			for (ContaCompartilhada contaCompartilhada : contasCompartilhadas) {
				if (!contaCompartilhada.getConta().isAtivo() 
						&& getOpcoesSistema().getExibirContasInativas() 
						&& !contaCompartilhada.getConta().getTipoConta().equals(TipoConta.CARTAO)) {
					contas.add(contaCompartilhada.getConta());
				} else if (contaCompartilhada.getConta().isAtivo() 
						&& !contaCompartilhada.getConta().getTipoConta().equals(TipoConta.CARTAO)) {
					contas.add(contaCompartilhada.getConta());
				} else {
					continue;
				}
			}
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>(contas);
	}
	
	public List<Integer> getListaAno() {
		List<Integer> anos = new ArrayList<>();
		for (int i = Calendar.getInstance().get(Calendar.YEAR) + 5; i > Calendar.getInstance().get(Calendar.YEAR) - 6; i--) {
			anos.add(i);
		}
		return anos;
	}

	public void setContaService(IConta contaService) {
		this.contaService = contaService;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public PanoramaLancamentoConta getEntity() {
		return entity;
	}

	public void setEntity(PanoramaLancamentoConta entity) {
		this.entity = entity;
	}

	public List<PanoramaLancamentoConta> getListEntity() {
		return listEntity;
	}

	public void setListEntity(List<PanoramaLancamentoConta> listEntity) {
		this.listEntity = listEntity;
	}

	public IResumoEstatistica getService() {
		return service;
	}

	public void setService(IResumoEstatistica service) {
		this.service = service;
	}

	public CriterioBuscaLancamentoConta getCriterioBusca() {
		return criterioBusca;
	}

	public void setCriterioBusca(CriterioBuscaLancamentoConta criterioBusca) {
		this.criterioBusca = criterioBusca;
	}
}