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

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IMeioPagamento;

public abstract class AbstractLancamentoContaController extends AbstractCRUDController<LancamentoConta> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8180594539371851973L;
	
	@Autowired
	private ICategoria categoriaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;
	
	private TipoCategoria tipoCategoriaSelecionada;

	public AbstractLancamentoContaController() {
		super(new LancamentoConta());
	}
	
	public void atualizaComboCategorias() {
		if (entity.getTipoLancamento() != null) {
			if (entity.getTipoLancamento().equals(TipoLancamento.RECEITA)) {
				tipoCategoriaSelecionada = TipoCategoria.CREDITO;
			} else {
				tipoCategoriaSelecionada = TipoCategoria.DEBITO;
			}
		}
	}
	
	public List<Categoria> getListaCategoria() {
		try {
			 
			if (tipoCategoriaSelecionada != null) {
				List<Categoria> resultado = categoriaService.buscarAtivosPorTipoCategoriaEUsuario(tipoCategoriaSelecionada, getUsuarioLogado());				
				// Lógica para incluir a categoria inativa da entidade na combo
				if (resultado != null && entity.getCategoria() != null) {
					if (!resultado.contains(entity.getCategoria())) {
						entity.getCategoria().setAtivo(true);
						resultado.add(entity.getCategoria());
					}
				}
				return resultado;
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		} 
		return new ArrayList<Categoria>();
	}
	
	public List<Categoria> getListaCategoriaSemTipoCategoria() {
		try {
			 List<Categoria> resultado = categoriaService.buscarAtivosPorUsuario(getUsuarioLogado());				
			// Lógica para incluir a categoria inativa da entidade na combo
			if (resultado != null && entity.getCategoria() != null) {
				if (!resultado.contains(entity.getCategoria())) {
					entity.getCategoria().setAtivo(true);
					resultado.add(entity.getCategoria());
				}
			}
			return resultado;			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		} 
		return new ArrayList<Categoria>();
	}
	
	public List<Favorecido> getListaFavorecido() {
		try {
			List<Favorecido> resultado = favorecidoService.buscarAtivosPorUsuario(getUsuarioLogado());
			// Lógica para incluir o favorecido inativo da entidade na combo
			if (resultado != null && entity.getFavorecido() != null) {
				if (!resultado.contains(entity.getFavorecido())) {
					entity.getFavorecido().setAtivo(true);
					resultado.add(entity.getFavorecido());
				}
			}
			return resultado;
		} catch (BusinessException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<MeioPagamento> getListaMeioPagamento() {
		try {
			List<MeioPagamento> resultado = meioPagamentoService.buscarAtivosPorUsuario(getUsuarioLogado());
			// Lógica para incluir o meio de pagamento inativo da entidade na combo
			if (resultado != null && entity.getMeioPagamento() != null) {
				if (!resultado.contains(entity.getMeioPagamento())) {
					entity.getMeioPagamento().setAtivo(true);
					resultado.add(entity.getMeioPagamento());
				}
			}
			return resultado;
		} catch (BusinessException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
}