/***
  
  	Copyright (c) 2012 - 2016 Hércules S. S. José

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

import br.com.hslife.orcamento.entity.RelatorioColuna;
import br.com.hslife.orcamento.entity.RelatorioCustomizado;
import br.com.hslife.orcamento.entity.RelatorioParametro;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IRelatorioCustomizado;

@Component("relatorioCustomizadoMB")
@Scope("session")
public class RelatorioCustomizadoController extends AbstractCRUDController<RelatorioCustomizado> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5508989331062227746L;

	@Autowired
	private IRelatorioCustomizado service; 
	
	private String nomeRelatorio;
	
	private RelatorioColuna colunaRelatorio;
	private RelatorioParametro parametroRelatorio;
	
	public RelatorioCustomizadoController() {
		super(new RelatorioCustomizado());
		moduleTitle = "Relatórios Customizados";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new RelatorioCustomizado();
		listEntity = new ArrayList<RelatorioCustomizado>();		
	}
	
	@Override
	public void find() {
		try {
			listEntity = getService().buscarNomePorUsuario(nomeRelatorio, getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String save() {
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	public IRelatorioCustomizado getService() {
		return service;
	}

	public RelatorioColuna getColunaRelatorio() {
		return colunaRelatorio;
	}

	public void setColunaRelatorio(RelatorioColuna colunaRelatorio) {
		this.colunaRelatorio = colunaRelatorio;
	}

	public RelatorioParametro getParametroRelatorio() {
		return parametroRelatorio;
	}

	public void setParametroRelatorio(RelatorioParametro parametroRelatorio) {
		this.parametroRelatorio = parametroRelatorio;
	}
}