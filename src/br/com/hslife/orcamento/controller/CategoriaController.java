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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICategoria;

@Component("categoriaMB")
@Scope("session")
public class CategoriaController extends AbstractSimpleCRUDController<Categoria> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1172336301433508801L;

	@Autowired
	private ICategoria service; 
	
	private String descricaoCategoria;
	private boolean somenteAtivos = true;
	
	public CategoriaController() {
		super(new Categoria());
		moduleTitle = "Categoria";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Categoria();
		listEntity = new ArrayList<Categoria>();		
	}
	
	@Override
	public void find() {
		try {
			listEntity = getService().buscarPorDescricaoUsuarioEAtivo(descricaoCategoria, getUsuarioLogado(), somenteAtivos);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public void save() {
		entity.setUsuario(getUsuarioLogado());
		super.save();
	}

	public ICategoria getService() {
		return service;
	}

	public void setService(ICategoria service) {
		this.service = service;
	}

	public String getDescricaoCategoria() {
		return descricaoCategoria;
	}

	public void setDescricaoCategoria(String descricaoCategoria) {
		this.descricaoCategoria = descricaoCategoria;
	}

	public boolean isSomenteAtivos() {
		return somenteAtivos;
	}

	public void setSomenteAtivos(boolean somenteAtivos) {
		this.somenteAtivos = somenteAtivos;
	}
}