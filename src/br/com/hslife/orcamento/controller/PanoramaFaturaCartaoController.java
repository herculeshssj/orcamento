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

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IResumoEstatistica;
import br.com.hslife.orcamento.model.PanoramaFaturaCartao;
import br.com.hslife.orcamento.util.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.util.Util;

@Component("panoramaFaturaCartaoMB")
@Scope("session")
public class PanoramaFaturaCartaoController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1909509361923924981L;

	@Autowired
	private IResumoEstatistica service;
	
	@Autowired
	private IConta contaService;
	
	private CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
	private int ano;
	
	private PanoramaFaturaCartao entity;
	private List<PanoramaFaturaCartao> listEntity;
	
	private CartesianChartModel saldoTotalModel;
	private boolean exibirGrafico = false;
	
	public PanoramaFaturaCartaoController() {
		moduleTitle = "Panorama das Faturas do Cartão";
	}
	
	@Override
	public String startUp() {
		return "/pages/ResumoEstatistica/panoramaFaturaCartao";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new PanoramaFaturaCartao();
		listEntity = new LinkedList<PanoramaFaturaCartao>();
	}
	
	public String find() {
		if (ano < 1970) {
			warnMessage("Ano deve ser maior que 1970!");
		} else {
			criterioBusca.setDataInicio(Util.primeiroDiaAno(ano));
			criterioBusca.setDataFim(Util.ultimoDiaAno(ano));
			criterioBusca.setDescricao("");
			try {
				listEntity = getService().gerarRelatorioPanoramaFaturaCartao(criterioBusca.getConta(), ano);
				
				if (listEntity == null || listEntity.size() == 0) {
					warnMessage("Nenhum resultado encontrado. Relatório não disponível para visualização.");
				} else {
					infoMessage("Relatório " + ano + " gerado com sucesso!");
					this.gerarGrafico();
				}
			}
			catch (BusinessException be) {
				errorMessage(be.getMessage());
			}
		}
		return "";
	}
	
	private void gerarGrafico(){
		saldoTotalModel = new CartesianChartModel();
		LineChartSeries serie = new LineChartSeries();
		serie.setLabel("Total da fatura");
		
		for (PanoramaFaturaCartao panorama : listEntity) {
			if (panorama.getDescricao().equals("Total da fatura")) {
				serie.set("JAN", panorama.getJaneiro());
				serie.set("FEV", panorama.getFevereiro());
				serie.set("MAR", panorama.getMarco());
				serie.set("ABR", panorama.getAbril());
				serie.set("MAI", panorama.getMaio());
				serie.set("JUN", panorama.getJunho());
				serie.set("JUL", panorama.getJulho());
				serie.set("AGO", panorama.getAgosto());
				serie.set("SET", panorama.getSetembro());
				serie.set("OUT", panorama.getOutubro());
				serie.set("NOV", panorama.getNovembro());
				serie.set("DEZ", panorama.getDezembro());
			}
		}
		
		saldoTotalModel.addSeries(serie);
		exibirGrafico = true;
	}
	
	public String verRelatorioCompleto() {
		if (listEntity ==  null || listEntity.size() == 0) {
			warnMessage("Nenhum resultado disponível. Relatório não disponível para visualização.");
			return "";
		} else { 
			return "/pages/ResumoEstatistica/planilhaPanoramaFaturaCartao";
		}
	}
	
	public List<Conta> getListaContaAtiva() {
		try {
			return contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", TipoConta.CARTAO, getUsuarioLogado(), true);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
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

	public IResumoEstatistica getService() {
		return service;
	}

	public void setService(IResumoEstatistica service) {
		this.service = service;
	}

	public CartesianChartModel getSaldoTotalModel() {
		return saldoTotalModel;
	}

	public void setSaldoTotalModel(CartesianChartModel saldoTotalModel) {
		this.saldoTotalModel = saldoTotalModel;
	}

	public boolean isExibirGrafico() {
		return exibirGrafico;
	}

	public void setExibirGrafico(boolean exibirGrafico) {
		this.exibirGrafico = exibirGrafico;
	}

	public CriterioBuscaLancamentoConta getCriterioBusca() {
		return criterioBusca;
	}

	public void setCriterioBusca(CriterioBuscaLancamentoConta criterioBusca) {
		this.criterioBusca = criterioBusca;
	}

	public PanoramaFaturaCartao getEntity() {
		return entity;
	}

	public void setEntity(PanoramaFaturaCartao entity) {
		this.entity = entity;
	}

	public List<PanoramaFaturaCartao> getListEntity() {
		return listEntity;
	}

	public void setListEntity(List<PanoramaFaturaCartao> listEntity) {
		this.listEntity = listEntity;
	}
}