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

import br.com.hslife.orcamento.entity.Banco;
import br.com.hslife.orcamento.entity.CartaoCredito;
import br.com.hslife.orcamento.enumeration.Abrangencia;
import br.com.hslife.orcamento.enumeration.Bandeira;
import br.com.hslife.orcamento.enumeration.TipoCartao;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IBanco;
import br.com.hslife.orcamento.facade.ICartaoCredito;

@Component("cartaoCreditoMB")
@Scope("session")
public class CartaoCreditoController extends AbstractCRUDController<CartaoCredito> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7153832982187599206L;

	@Autowired
	private ICartaoCredito service;
	
	@Autowired
	private IBanco bancoService;
	
	private String descricaoCartao;
	private CartaoCredito novoCartao;
	
	public CartaoCreditoController() {
		super(new CartaoCredito());
		
		moduleTitle = "Cartão de Crédito";
	}

	@Override
	protected void initializeEntity() {
		entity = new CartaoCredito();
		listEntity = new ArrayList<>();		
	}
	
	public void find() {
		try {
			listEntity = getService().buscarPorDescricaoEUsuario(descricaoCartao, getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String save() {
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}
	
	public List<Banco> getListaBanco() {
		try {
			return bancoService.buscarPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public String substituirCartaoView() {
		try {
			novoCartao = new CartaoCredito();
			entity = getService().buscarPorID(idEntity);
			actionTitle = " - Substituir";
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}		
		return "/pages/CartaoCredito/substituirCartao";
	}
	
	public String substituirCartao() {
		try {
			novoCartao.setUsuario(getUsuarioLogado());
			entity.setCartaoSubstituto(novoCartao);			
			getService().substituirCartao(entity);
			infoMessage("Substituição efetuada com sucesso!");
			initializeEntity();
			return list();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public List<SelectItem> getListaTipoCartao() {
		List<SelectItem> lista = new ArrayList<>();
		lista.add(new SelectItem(TipoCartao.CREDITO));
		lista.add(new SelectItem(TipoCartao.DEBITO));
		return lista;
	}
	
	public List<SelectItem> getListaAbrangencia() {
		List<SelectItem> lista = new ArrayList<>();
		lista.add(new SelectItem(Abrangencia.NACIONAL));
		lista.add(new SelectItem(Abrangencia.INTERNACIONAL));
		return lista;
	}
	
	public List<SelectItem> getListaBandeira() {
		List<SelectItem> lista = new ArrayList<>();
		lista.add(new SelectItem(Bandeira.NENHUMA));
		lista.add(new SelectItem(Bandeira.AMERICAN_EXPRESS));
		lista.add(new SelectItem(Bandeira.AURA));
		lista.add(new SelectItem(Bandeira.BNDES));
		lista.add(new SelectItem(Bandeira.DINERS_CLUB));
		lista.add(new SelectItem(Bandeira.ELO));
		lista.add(new SelectItem(Bandeira.HIPERCARD));
		lista.add(new SelectItem(Bandeira.MASTERCARD));
		lista.add(new SelectItem(Bandeira.SOROCRED));
		lista.add(new SelectItem(Bandeira.VISA));
		return lista;
	}
	
	public ICartaoCredito getService() {
		return service;
	}

	public void setService(ICartaoCredito service) {
		this.service = service;
	}

	public String getDescricaoCartao() {
		return descricaoCartao;
	}

	public void setDescricaoCartao(String descricaoCartao) {
		this.descricaoCartao = descricaoCartao;
	}

	public void setBancoService(IBanco bancoService) {
		this.bancoService = bancoService;
	}

	public CartaoCredito getNovoCartao() {
		return novoCartao;
	}

	public void setNovoCartao(CartaoCredito novoCartao) {
		this.novoCartao = novoCartao;
	}
}