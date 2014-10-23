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

import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.DetalheOrcamento;
import br.com.hslife.orcamento.entity.Orcamento;
import br.com.hslife.orcamento.enumeration.AbrangenciaOrcamento;
import br.com.hslife.orcamento.enumeration.PeriodoLancamento;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoOrcamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IOrcamento;

@Component("orcamentoMB")
@Scope("session")
public class OrcamentoController extends AbstractCRUDController<Orcamento> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5508989331062227746L;

	@Autowired
	private IOrcamento service; 
	
	@Autowired
	private IConta contaService;
	
	private DetalheOrcamento detalheOrcamento;
	
	public OrcamentoController() {
		super(new Orcamento());
		moduleTitle = "Orçamento do Período";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Orcamento();
		listEntity = new ArrayList<Orcamento>();
		detalheOrcamento = new DetalheOrcamento();
	}
	
	@Override
	public void find() {
		
	}
	
	public void atualizaListaItens() {
		this.getListaItensDetalheOrcamento();
	}
	
	public List<DetalheOrcamento> getListaItensDetalheOrcamento() {
		List<DetalheOrcamento> resultado = new ArrayList<DetalheOrcamento>();
		if (entity.getAbrangenciaOrcamento() != null) {
			switch (entity.getAbrangenciaOrcamento()) {
				case CREDITODEBITO:
					resultado.add(new DetalheOrcamento(null, "Crédito"));
					resultado.add(new DetalheOrcamento(null, "Débito"));
					break;
				case CATEGORIA : System.out.println("Categorias carregadas"); break;
				case FAVORECIDO : System.out.println("Favorecidos carregados"); break;
				case MEIOPAGAMENTO : System.out.println("Meios de pagamento carregados"); break;
			}
		}
		return resultado;
	}

	public List<Conta> getListaConta() {
		try {
			return contaService.buscarPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<SelectItem> getListaTipoOrcamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (TipoOrcamento orcamento : TipoOrcamento.values()) {
			listaSelectItem.add(new SelectItem(orcamento, orcamento.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaTipoConta() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (TipoConta tipo : TipoConta.values()) {
			listaSelectItem.add(new SelectItem(tipo, tipo.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaPeriodoLancamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (PeriodoLancamento periodo : PeriodoLancamento.values()) {
			listaSelectItem.add(new SelectItem(periodo, periodo.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaAbrangenciaOrcamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (AbrangenciaOrcamento abrangencia : AbrangenciaOrcamento.values()) {
			listaSelectItem.add(new SelectItem(abrangencia, abrangencia.toString()));
		}
		return listaSelectItem;
	}

	public IOrcamento getService() {
		return service;
	}

	public void setService(IOrcamento service) {
		this.service = service;
	}

	public DetalheOrcamento getDetalheOrcamento() {
		return detalheOrcamento;
	}

	public void setDetalheOrcamento(DetalheOrcamento detalheOrcamento) {
		this.detalheOrcamento = detalheOrcamento;
	}

	
	
}