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
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.RegraImportacao;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IRegraImportacao;

@Component("regraImportacaoMB")
@Scope("session")
public class RegraImportacaoController extends AbstractCRUDController<RegraImportacao> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3490586942206992826L;
	
	@Autowired
	private IRegraImportacao service;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private ICategoria categoriaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;
	
	private Conta contaSelecionada;
	
	public RegraImportacaoController() {
		super(new RegraImportacao());
	
		moduleTitle = "Regras de Importação";
	}

	@Override
	public void find() {
		try {
			listEntity = getService().buscarTodosPorConta(contaSelecionada);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}

	@Override
	protected void initializeEntity() {
		entity = new RegraImportacao();
		listEntity = new ArrayList<RegraImportacao>();
	}
	
	public List<Conta> getListaConta() {
		try {
			if (getOpcoesSistema().getExibirContasInativas()) {
				return contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", new TipoConta[]{}, getUsuarioLogado(), null); // resolvendo a ambiguidade do método
			} else {
				return contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", new TipoConta[]{}, getUsuarioLogado(), true); // resolvendo a ambiguidade do método
			}			
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public List<SelectItem> getListaCategoria() {
		List<SelectItem> lista = new ArrayList<SelectItem>();
		try {
			for (Categoria c : categoriaService.buscarAtivosPorUsuario(getUsuarioLogado())) {
				lista.add(new SelectItem(c.getId(), c.getTipoCategoria() + " - " + c.getDescricao()));
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return lista;
	}
	
	public List<SelectItem> getListaFavorecido() {
		List<SelectItem> lista = new ArrayList<SelectItem>();
		try {
			for (Favorecido f : favorecidoService.buscarAtivosPorUsuario(getUsuarioLogado())) {
				lista.add(new SelectItem(f.getId(), f.getNome()));
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return lista;
	}
	
	public List<SelectItem> getListaMeioPagamento() {
		List<SelectItem> lista = new ArrayList<SelectItem>();
		try {
			for (MeioPagamento m : meioPagamentoService.buscarAtivosPorUsuario(getUsuarioLogado())) {
				lista.add(new SelectItem(m.getId(), m.getDescricao()));
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return lista;
	}

	public IRegraImportacao getService() {
		return service;
	}

	public void setService(IRegraImportacao service) {
		this.service = service;
	}

	public Conta getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(Conta contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}
}