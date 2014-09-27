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
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Banco;
import br.com.hslife.orcamento.entity.CartaoCredito;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IBanco;
import br.com.hslife.orcamento.facade.ICartaoCredito;
import br.com.hslife.orcamento.util.Util;

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
	private boolean somenteAtivos = true;
	private CartaoCredito novoCartao;
	private String numeroCartao = "";
	
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
			listEntity = getService().buscarDescricaoOuAtivoPorUsuario(descricaoCartao, getUsuarioLogado(), somenteAtivos);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String save() {
		Date validade = new Date(entity.getAnoValidade() - 1900, entity.getMesValidade() - 1, entity.getDiaVencimentoFatura());
		entity.setUsuario(getUsuarioLogado());
		entity.setValidade(validade);
		if (!Util.eVazio(numeroCartao) & numeroCartao.length() == 16 & Util.onlyNumber(numeroCartao)) {
			entity.setNumeroCartao(Util.SHA1(numeroCartao));
		}
		numeroCartao = "";
		return super.save();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String edit() {
		String retorno = super.edit();
		entity.setMesValidade(entity.getValidade().getMonth() + 1);
		entity.setAnoValidade(entity.getValidade().getYear() + 1900);
		return retorno;
	}
	
	public String ativarCartaoView() {
		try {
			entity = getService().buscarPorID(idEntity);
			if (entity.getValidade().before(new Date())) {
				warnMessage("Não é possível ativar! Cartão fora da validade!");
				return "";
			}
			actionTitle = " - Ativar";
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}		
		return "/pages/CartaoCredito/ativarDesativarCartao";
	}

	public String ativarCartao() {
		try {		
			getService().ativarCartao(entity);
			infoMessage("Cartão ativado com sucesso!");
			initializeEntity();
			return list();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String desativarCartaoView() {
		try {
			entity = getService().buscarPorID(idEntity);
			actionTitle = " - Desativar";
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}		
		return "/pages/CartaoCredito/ativarDesativarCartao";
	}
	
	public String desativarCartao() {
		try {		
			getService().desativarCartao(entity);
			infoMessage("Cartão desativado com sucesso!");
			initializeEntity();
			return list();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
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
	
	@SuppressWarnings("deprecation")
	public String substituirCartao() {
		try {
			novoCartao.setBanco(entity.getBanco());
			novoCartao.setLimiteSaque(entity.getLimiteSaque());
			novoCartao.setLimiteCartao(entity.getLimiteCartao());
			novoCartao.setJuros(entity.getJuros());
			novoCartao.setMulta(entity.getMulta());
			Date validade = new Date(novoCartao.getAnoValidade() - 1900, novoCartao.getMesValidade() - 1, novoCartao.getDiaVencimentoFatura());
			novoCartao.setValidade(validade);
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
	
	public List<Banco> getListaBanco() {
		try {
			List<Banco> resultado = bancoService.buscarAtivosPorUsuario(getUsuarioLogado());
			// Lógica para incluir o banco inativo da entidade na combo
			if (resultado != null && entity.getBanco() != null) {
				if (!resultado.contains(entity.getBanco())) {
					entity.getBanco().setAtivo(true);
					resultado.add(entity.getBanco());
				}
			}
			return resultado;
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
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

	public boolean isSomenteAtivos() {
		return somenteAtivos;
	}

	public void setSomenteAtivos(boolean somenteAtivos) {
		this.somenteAtivos = somenteAtivos;
	}

	public String getNumeroCartao() {
		return numeroCartao;
	}

	public void setNumeroCartao(String numeroCartao) {
		this.numeroCartao = numeroCartao;
	}
}