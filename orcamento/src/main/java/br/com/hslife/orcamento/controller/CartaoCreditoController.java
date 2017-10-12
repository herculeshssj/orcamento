/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor
	
	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

    para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 - 
	
	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

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
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.TipoCartao;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IBanco;
import br.com.hslife.orcamento.facade.ICartaoCredito;
import br.com.hslife.orcamento.facade.IMoeda;

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
	
	@Autowired
	private IMoeda moedaService;
	
	private boolean somenteAtivos = true;
	private CartaoCredito novoCartao;
	private String numeroCartao = "";
	private boolean exibir;
	private TipoCartao tipoCartao;
	
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
			listEntity = getService().buscarDescricaoOuTipoCartaoOuAtivoPorUsuario("", tipoCartao, getUsuarioLogado(), somenteAtivos);
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String create() {
		entity.setTipoCartao(TipoCartao.DEBITO);
		return super.create();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String save() {
		Date validade = new Date(entity.getAnoValidade() - 1900, entity.getMesValidade() - 1, entity.getDiaVencimentoFatura());
		entity.setUsuario(getUsuarioLogado());
		entity.setValidade(validade);
		numeroCartao = "";
		return super.save();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String edit() {
		String retorno = super.edit();
		entity.setMesValidade(entity.getValidade().getMonth() + 1);
		entity.setAnoValidade(entity.getValidade().getYear() + 1900);
		//entity.setMoeda(entity.getConta().getMoeda());
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
		} catch (ValidationException | BusinessException be) {
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
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String desativarCartaoView() {
		try {
			entity = getService().buscarPorID(idEntity);
			actionTitle = " - Desativar";
		} catch (ValidationException | BusinessException be) {
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
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String substituirCartaoView() {
		try {
			novoCartao = new CartaoCredito();
			entity = getService().buscarPorID(idEntity);
			actionTitle = " - Substituir";
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}		
		return "/pages/CartaoCredito/substituirCartao";
	}
	
	@SuppressWarnings("deprecation")
	public String substituirCartao() {
		try {
			if (!entity.getTipoCartao().equals(novoCartao.getTipoCartao())) {
				if (entity.getTipoCartao().equals(TipoCartao.CREDITO) && novoCartao.getTipoCartao().equals(TipoCartao.DEBITO)) {
					warnMessage("Informe detalhes sobre fatura e validade do cartão");
					novoCartao.setTipoCartao(TipoCartao.CREDITO);
					return "";
				}
			}
			novoCartao.validate();
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
			return list();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String reparar() {
		try {
			if (!entity.getTipoCartao().equals(TipoCartao.CREDITO)) {
				warnMessage("Este recurso só funciona com cartões do crédito!");
				return "";
			}
			getService().repararInconsistênciaFatura(entity);
			infoMessage("Inconsistências reparadas com sucesso!");
			return list();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public List<Banco> getListaBanco() {
		try {
			List<Banco> resultado = getBancoService().buscarPorNomeEAtivo("", true);
			// Lógica para incluir o banco inativo da entidade na combo
			if (resultado != null && entity.getBanco() != null) {
				if (!resultado.contains(entity.getBanco())) {
					entity.getBanco().setAtivo(true);
					resultado.add(entity.getBanco());
				}
			}
			return resultado;
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<Moeda> getListaMoeda() {
		try {
			List<Moeda> resultado = moedaService.buscarAtivosPorUsuario(getUsuarioLogado());
			// Lógica para incluir o banco inativo da entidade na combo
			if (resultado != null && entity.getMoeda() != null) {
				if (!resultado.contains(entity.getMoeda())) {
					entity.getMoeda().setAtivo(true);
					resultado.add(entity.getMoeda());
				}
			}
			return resultado;
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public ICartaoCredito getService() {
		return service;
	}

	public IBanco getBancoService() {
		return bancoService;
	}

	public IMoeda getMoedaService() {
		return moedaService;
	}

	public void setService(ICartaoCredito service) {
		this.service = service;
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

	public boolean isExibir() {
		return exibir;
	}

	public void setExibir(boolean exibir) {
		this.exibir = exibir;
	}

	public TipoCartao getTipoCartao() {
		return tipoCartao;
	}

	public void setTipoCartao(TipoCartao tipoCartao) {
		this.tipoCartao = tipoCartao;
	}
}