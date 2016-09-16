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
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.ModeloDocumento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IModeloDocumento;
import br.com.hslife.orcamento.util.Util;

@Component("modeloDocumentoMB")
@Scope("session")
public class ModeloDocumentoController extends AbstractCRUDController<ModeloDocumento>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -372533275028178301L;

	@Autowired
	private IModeloDocumento service;
	
	private String descricaoModeloPesquisa;
	private boolean somenteAtivos = true;
	private boolean modeloAtivo = true;
	private String descricaoModelo;
	private String conteudoModelo;
	private String statusSalvamento;
	
	public ModeloDocumentoController() {
		super(new ModeloDocumento());
		
		moduleTitle = "Modelos de Documentos";
	}

	@Override
	protected void initializeEntity() {
		entity = new ModeloDocumento();
		listEntity = new ArrayList<ModeloDocumento>();
		conteudoModelo = "";
		descricaoModelo = "";
		statusSalvamento = "";
	}

	@Override
	public void find() {
		try {			
			listEntity = getService().buscarDescricaoOuAtivoPorUsuario(descricaoModeloPesquisa, somenteAtivos, getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String save() {
		/*
		if (entity.getId() == null) {
			entity.setUsuario(getUsuarioLogado());
		} 
		entity.setConteudo(conteudoModelo);
		entity.setDescricao(descricaoModelo);
		return super.save();*/
		this.autoSave();
		infoMessage("Registro salvo com sucesso.");
		return list();
	}
	
	@Override
	public String edit() {
		String retorno = super.edit();
		conteudoModelo = entity.getConteudo();
		descricaoModelo = entity.getDescricao();
		modeloAtivo = entity.isAtivo();
		return retorno;
	}
	
	public void autoSave() {
		try {
			if (entity.getId() == null) {
				
				if (descricaoModelo !=  null && !descricaoModelo.isEmpty()) {
					descricaoModelo = "Modelo salvo automaticamente em " + Util.formataDataHora(new Date(), Util.DATAHORA);
				}
				
				entity.setUsuario(getUsuarioLogado());
				entity.setConteudo(conteudoModelo);
				entity.setDescricao(descricaoModelo);
				entity.setAtivo(modeloAtivo);
				getService().cadastrar(entity);
				
				// Tentando contornar a exceção org.hibernate.StaleObjectStateException
				entity = getService().buscarPorID(entity.getId());
			} else {
				ModeloDocumento modelo = getService().buscarPorID(entity.getId());
				modelo.setConteudo(conteudoModelo);
				modelo.setDescricao(descricaoModelo);
				modelo.setAtivo(modeloAtivo);
				getService().alterar(modelo);
			}
			statusSalvamento = "Modelo de documento salvo automaticamente em " + new Date().toString();
		}
		catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void clonarModelo() {
		try {
			ModeloDocumento modeloClonado = entity.clonar();
			modeloClonado.validate();
			getService().cadastrar(modeloClonado);
			infoMessage("Modelo clonado com sucesso!");
			initializeEntity();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String visualizarModeloDocumento() {
		conteudoModelo = entity.getConteudo();
		return "/pages/ModeloDocumento/visualizarDocumento";
	}

	public IModeloDocumento getService() {
		return service;
	}

	public void setService(IModeloDocumento service) {
		this.service = service;
	}

	public String getDescricaoModeloPesquisa() {
		return descricaoModeloPesquisa;
	}

	public void setDescricaoModeloPesquisa(String descricaoModeloPesquisa) {
		this.descricaoModeloPesquisa = descricaoModeloPesquisa;
	}

	public boolean isSomenteAtivos() {
		return somenteAtivos;
	}

	public void setSomenteAtivos(boolean somenteAtivos) {
		this.somenteAtivos = somenteAtivos;
	}

	public String getDescricaoModelo() {
		return descricaoModelo;
	}

	public void setDescricaoModelo(String descricaoModelo) {
		this.descricaoModelo = descricaoModelo;
	}

	public String getConteudoModelo() {
		return conteudoModelo;
	}

	public void setConteudoModelo(String conteudoModelo) {
		this.conteudoModelo = conteudoModelo;
	}

	public String getStatusSalvamento() {
		return statusSalvamento;
	}

	public void setStatusSalvamento(String statusSalvamento) {
		this.statusSalvamento = statusSalvamento;
	}

	public boolean isModeloAtivo() {
		return modeloAtivo;
	}

	public void setModeloAtivo(boolean modeloAtivo) {
		this.modeloAtivo = modeloAtivo;
	}
}