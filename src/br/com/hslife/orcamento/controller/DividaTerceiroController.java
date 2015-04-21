/***
  
  	Copyright (c) 2012 - 2015 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@gmail.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.DividaTerceiro;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.ModeloDocumento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.StatusDivida;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IDividaTerceiro;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IModeloDocumento;
import br.com.hslife.orcamento.facade.IMoeda;

@Component("dividaTerceiroMB")
@Scope("session")
public class DividaTerceiroController extends AbstractCRUDController<DividaTerceiro> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5749287637286980625L;
	
	@Autowired
	private IDividaTerceiro service;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IModeloDocumento modeloDocumentoService;
	
	@Autowired
	private IMoeda moedaService;
	
	private TipoCategoria tipoCategoria;
	private StatusDivida statusDivida;
	private ModeloDocumento modeloSelecionado;
	
	public DividaTerceiroController() {
		super(new DividaTerceiro());
		moduleTitle = "Dívida de Favorecidos";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new DividaTerceiro();
		listEntity = new ArrayList<DividaTerceiro>();		
	}

	@Override
	public void find() {
		try {
			listEntity = getService().buscarFavorecidoOuTipoCategoriaOuStatusDividaPorUsuario(null, tipoCategoria, statusDivida, getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String save() {
		entity.setUsuario(getUsuarioLogado());
		
		return super.save();
	}
	
	public List<Favorecido> getListaFavorecido() {
		try {
			return favorecidoService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<ModeloDocumento> getListaModeloDocumento() {
		try {
			return modeloDocumentoService.buscarDescricaoOuAtivoPorUsuario(null, true, getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<Moeda> getListaMoeda() {
		try {
			return moedaService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}

	public IDividaTerceiro getService() {
		return service;
	}

	public void setService(IDividaTerceiro service) {
		this.service = service;
	}

	public TipoCategoria getTipoCategoria() {
		return tipoCategoria;
	}

	public void setTipoCategoria(TipoCategoria tipoCategoria) {
		this.tipoCategoria = tipoCategoria;
	}

	public StatusDivida getStatusDivida() {
		return statusDivida;
	}

	public void setStatusDivida(StatusDivida statusDivida) {
		this.statusDivida = statusDivida;
	}

	public ModeloDocumento getModeloSelecionado() {
		return modeloSelecionado;
	}

	public void setModeloSelecionado(ModeloDocumento modeloSelecionado) {
		this.modeloSelecionado = modeloSelecionado;
	}
}