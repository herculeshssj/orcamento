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
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.PanoramaLancamentoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IResumoEstatistica;
import br.com.hslife.orcamento.model.CriterioLancamentoConta;
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
	
	private CriterioLancamentoConta criterioBusca = new CriterioLancamentoConta();
	private int ano;
	
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
		if (ano <= 0) {
			warnMessage("Ano deve ser maior que zero (0)!");
		} else {
			criterioBusca.setDataInicio(Util.primeiroDiaAno(ano));
			criterioBusca.setDataFim(Util.ultimoDiaAno(ano));
			criterioBusca.setDescricao("");
			try {
				listEntity = getService().visualizarRelatorioPanoramaLancamentoConta(criterioBusca.getConta(), ano);
				if (listEntity == null || listEntity.size() == 0) {
					warnMessage("Nenhum resultado encontrado. Relatório não disponível para visualização.");
				} else {
					infoMessage("Relatório " + ano + " disponível para visualização");
				}
			}
			catch (BusinessException be) {
				errorMessage(be.getMessage());
			}
		}
		return "";
	}
	
	public void gerarRelatorio() {
		if (ano <= 0) {
			warnMessage("Ano deve ser maior que zero (0)!");
		} else {
			criterioBusca.setDataInicio(Util.primeiroDiaAno(ano));
			criterioBusca.setDataFim(Util.ultimoDiaAno(ano));
			criterioBusca.setDescricao("");
			try {
				getService().gerarRelatorioPanoramaLancamentoConta(criterioBusca, ano);
				infoMessage("Relatório gerado com sucesso!");
			}
			catch (BusinessException be) {
				errorMessage(be.getMessage());
			}
		}		
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
		try {
			return contaService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}

	public IResumoEstatistica getService() {
		return service;
	}

	public void setService(IResumoEstatistica service) {
		this.service = service;
	}

	public void setContaService(IConta contaService) {
		this.contaService = contaService;
	}

	public CriterioLancamentoConta getCriterioBusca() {
		return criterioBusca;
	}

	public void setCriterioBusca(CriterioLancamentoConta criterioBusca) {
		this.criterioBusca = criterioBusca;
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
}