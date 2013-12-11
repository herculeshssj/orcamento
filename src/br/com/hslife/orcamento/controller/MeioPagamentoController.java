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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IMeioPagamento;

@Component("meioPagamentoMB")
@Scope("session")
public class MeioPagamentoController extends AbstractCRUDController<MeioPagamento> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1572214060196144237L;
	
	@Autowired
	private IMeioPagamento service; 
	
	private String descricaoMeioPagamento;
	
	public MeioPagamentoController() {
		super(new MeioPagamento());
		moduleTitle = "Meios de Pagamento";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new MeioPagamento();
		listEntity = new ArrayList<MeioPagamento>();		
	}
	
	@Override
	public void find() {
		try {
			listEntity = getService().buscarPorDescricaoEUsuario(descricaoMeioPagamento, getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String save() {
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}

	public IMeioPagamento getService() {
		return service;
	}

	public void setService(IMeioPagamento service) {
		this.service = service;
	}

	public String getDescricaoMeioPagamento() {
		return descricaoMeioPagamento;
	}

	public void setDescricaoMeioPagamento(String descricaoMeioPagamento) {
		this.descricaoMeioPagamento = descricaoMeioPagamento;
	}
}