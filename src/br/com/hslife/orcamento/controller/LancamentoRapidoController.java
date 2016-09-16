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
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoCartao;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.facade.IUsuario;

@Component("lancamentoRapidoMB")
@Scope("session")
public class LancamentoRapidoController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -682900866443729177L;
	
	private static final Logger logger = LogManager.getLogger(LancamentoRapidoController.class);
	
	@Autowired
	private IUsuario usuarioService;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private ICategoria categoriaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private ILancamentoConta lancamentoContaService;
	
	private String userTokenID;
	private LancamentoConta lancamento = new LancamentoConta();

	@Override
	protected void initializeEntity() {
		lancamento = new LancamentoConta();
	}

	@Override
	public String startUp() {
		return null;
	}

	public void registrar() {
		try {
			// Se a conta for do tipo Cartão e o cartão for do tipo Crédito seta a moeda selecionada. 
			// Caso contrário seta a moeda da conta
			if (lancamento.getConta().getTipoConta().equals(TipoConta.CARTAO) 
					&& lancamento.getConta().getCartaoCredito().getTipoCartao().equals(TipoCartao.CREDITO)) {
				// Mantém a moeda selecionada na combo
			} else {
				lancamento.setMoeda(lancamento.getConta().getMoeda());
			}
			
			// Seta o tipo de lançamento de acordo com a categoria selecionada
			// Se nenhuma categoria for selecionada o padrão é Despesa
			lancamento.setTipoLancamento(TipoLancamento.DESPESA);
			if (lancamento.getCategoria() != null && lancamento.getCategoria().getTipoCategoria().equals(TipoCategoria.CREDITO)) {
				lancamento.setTipoLancamento(TipoLancamento.RECEITA);
			}
			
			lancamento.setStatusLancamentoConta(StatusLancamentoConta.VALIDAR);
			lancamento.validate();
			lancamentoContaService.cadastrar(lancamento);
			infoMessage("Lançamento registrado com sucesso!");
			initializeEntity();
		} catch (Exception e) {
			errorMessage(e.getMessage());
			logger.catching(e);
		}
	}
	
	public String getUserTokenID() {
		return userTokenID;
	}

	public void setUserTokenID(String userTokenID) {
		this.userTokenID = userTokenID;
	}
	
	@Override
	public Usuario getUsuarioLogado() {
		try {
			return usuarioService.buscarPorTokenID(userTokenID);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return null;
	}
	
	public List<Conta> getListaConta() {
		try {
			return contaService.buscarTodosAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<Categoria> getListaCategoria() {
		try {
			return categoriaService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<Favorecido> getListaFavorecido() {
		try {
			return favorecidoService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<MeioPagamento> getListaMeioPagamento() {
		try {
		return meioPagamentoService.buscarAtivosPorUsuario(getUsuarioLogado());
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
	
	public List<SelectItem> getListaTipoLancamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		listaSelectItem.add(new SelectItem(TipoLancamento.RECEITA, "Receita"));
		listaSelectItem.add(new SelectItem(TipoLancamento.DESPESA, "Despesa"));
		return listaSelectItem;
	}

	public LancamentoConta getLancamento() {
		return lancamento;
	}

	public void setLancamento(LancamentoConta lancamento) {
		this.lancamento = lancamento;
	}
}