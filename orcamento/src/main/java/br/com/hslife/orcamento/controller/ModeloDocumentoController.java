/***

	Copyright (c) 2012 - 2021 Hércules S. S. José

	Este arquivo é parte do programa Orçamento Doméstico.


	Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

	modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

	publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

	Licença.


	Este programa é distribuído na esperança que possa ser útil, mas SEM

	NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer

	MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor

	GNU em português para maiores detalhes.


	Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob

	o nome de "LICENSE" junto com este programa, se não, acesse o site do

	projeto no endereco https://github.com/herculeshssj/orcamento ou escreva

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 

	Floor, Boston, MA  02110-1301, USA.


	Para mais informações sobre o programa Orçamento Doméstico e seu autor

	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

	para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.component.AutosalvamentoComponent;
import br.com.hslife.orcamento.entity.Autosalvamento;
import br.com.hslife.orcamento.entity.ModeloDocumento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IModeloDocumento;

@Component("modeloDocumentoMB")
@Scope("session")
public class ModeloDocumentoController extends AbstractCRUDController<ModeloDocumento>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -372533275028178301L;

	@Autowired
	private AutosalvamentoComponent autoSalvamentoComponent;
	
	@Autowired
	private IModeloDocumento service;
	
	private String descricaoModeloPesquisa;
	private boolean somenteAtivos = true;
	private String statusSalvamento;
	
	public ModeloDocumentoController() {
		super(new ModeloDocumento());
		
		moduleTitle = "Modelos de Documentos";
	}

	@Override
	protected void initializeEntity() {
		entity = new ModeloDocumento();
		listEntity = new ArrayList<ModeloDocumento>();
		statusSalvamento = "";
	}

	@Override
	public void find() {
		try {			
			listEntity = getService().buscarDescricaoOuAtivoPorUsuario(descricaoModeloPesquisa, somenteAtivos, getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String save() {
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}
	
	public void autoSave() {
		try {
			getAutoSalvamentoComponent().salvar(idEntity, entity.getClass().getSimpleName(), "conteudo", entity.getConteudo());
			
			Autosalvamento auto = getAutoSalvamentoComponent().buscarUltimoSalvamento(idEntity, entity.getClass().getSimpleName(), "conteudo");
			
			statusSalvamento = "Conteúdo salvo automaticamente em " + auto.getDataCriacao().toString();
		} catch (ValidationException | BusinessException be) {
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
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String visualizarModeloDocumento() {
		return "/pages/ModeloDocumento/visualizarDocumento";
	}

	public IModeloDocumento getService() {
		return service;
	}

	public void setService(IModeloDocumento service) {
		this.service = service;
	}

	public AutosalvamentoComponent getAutoSalvamentoComponent() {
		return autoSalvamentoComponent;
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

	public String getStatusSalvamento() {
		return statusSalvamento;
	}

	public void setStatusSalvamento(String statusSalvamento) {
		this.statusSalvamento = statusSalvamento;
	}
}
