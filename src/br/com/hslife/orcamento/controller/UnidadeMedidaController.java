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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.UnidadeMedida;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IUnidadeMedida;

@Component("unidadeMedidaMB")
@Scope("session")
public class UnidadeMedidaController extends AbstractCRUDController<UnidadeMedida> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2473644194677434039L;
	
	@Autowired
	private IUnidadeMedida service;
	
	private String descricaoUnidade;
	
	public UnidadeMedidaController() {
		super(new UnidadeMedida());
		moduleTitle = "Unidades de Medida";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new UnidadeMedida();
		listEntity = new ArrayList<UnidadeMedida>();
	}
	
	@Override
	public void find() {
		try {
			listEntity = getService().buscarPorDescricaoEUsuario(descricaoUnidade, getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String save() {
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}

	public IUnidadeMedida getService() {
		return service;
	}

	public void setService(IUnidadeMedida service) {
		this.service = service;
	}

	public String getDescricaoUnidade() {
		return descricaoUnidade;
	}

	public void setDescricaoUnidade(String descricaoUnidade) {
		this.descricaoUnidade = descricaoUnidade;
	}
}