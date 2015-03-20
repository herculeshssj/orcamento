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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.enumeration.IncrementoClonagemLancamento;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IMovimentacaoLancamento;

@Component("movimentacaoLancamentoMB")
@Scope("session")
public class MovimentacaoLancamentoController extends AbstractController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2116626869708662527L;
	
	@Autowired
	private IMovimentacaoLancamento service;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private ICategoria categoriaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;
	
	private int quantADuplicar = 1;
	private LancamentoConta lancamentoATransferir;
	private IncrementoClonagemLancamento incremento;
	private String descricao;
	private String observacao;
	private TipoCategoria tipoCategoriaSelecionada;
	
	private List<LancamentoConta> lancamentosSelecionados;
	
	private LancamentoConta lancamentoSelecionado;
	
	private Conta contaSelecionada;
	private Conta contaDestino;
	
	private Categoria categoriaSelecionada;
	private Categoria categoriaDestino;
	
	private Favorecido favorecidoSelecionado;
	
	private MeioPagamento meioPagamentoSelecionado;
	
	private String goToListContaPage = "/pages/LancamentoConta/listLancamentoConta";

	public MovimentacaoLancamentoController() {
		lancamentosSelecionados = new ArrayList<LancamentoConta>();
		
		moduleTitle = "Movimentação de Lançamentos";
	}
	
	@Override
	public String startUp() {
		return null;
	}
	
	@Override
	public String getModuleTitle() {		
		return super.getModuleTitle() + actionTitle;
	}
	
	@Override
	protected void initializeEntity() {
		lancamentosSelecionados = new ArrayList<LancamentoConta>();		
	}
		
	public String cancel() {
		initializeEntity();
		return goToListContaPage;
	}
	
	public String moverView() {
		try {
			if (lancamentosSelecionados != null && !lancamentosSelecionados.isEmpty()) {
				for (Iterator<LancamentoConta> i = lancamentosSelecionados.iterator(); i.hasNext(); ) {
					LancamentoConta l = i.next();
					if (l.getFaturaCartao() != null || l.getLancamentoPeriodico() != null || l.getHashImportacao() != null) {
						i.remove();
					}
				}
				if (lancamentosSelecionados.isEmpty()) {
					warnMessage("Nenhum lançamento a mover!");
				} else {
					actionTitle = " - Mover";
					return "/pages/MovimentacaoLancamento/moverLancamentos";
				}
			} else {
				warnMessage("Nenhum lançamento selecionado!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage(e.getMessage());
		}
		return "";
	}
	
	public String moverLancamentos() {
		try {
			getService().moverLancamentos(lancamentosSelecionados, contaSelecionada);
			infoMessage("Lançamentos movidos com sucesso!");
			return cancel();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String duplicarView() {
		try {
			if (lancamentosSelecionados != null && !lancamentosSelecionados.isEmpty()) {				
				actionTitle = " - Duplicar";
				return "/pages/MovimentacaoLancamento/duplicarLancamentos";				
			} else {
				warnMessage("Nenhum lançamento selecionado!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage(e.getMessage());
		}
		return "";
	}
	
	public String duplicarLancamentos() {
		try {
			getService().duplicarLancamentos(lancamentosSelecionados, contaSelecionada, quantADuplicar, incremento);			
			infoMessage("Lançamentos duplicados com sucesso!");
			return cancel();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String excluirView() {
		try {
			if (lancamentosSelecionados != null && !lancamentosSelecionados.isEmpty()) {
				for (Iterator<LancamentoConta> i = lancamentosSelecionados.iterator(); i.hasNext(); ) {
					LancamentoConta l = i.next();
					if (l.getFaturaCartao() != null || l.getLancamentoPeriodico() != null) {
						i.remove();
					}
				}
				if (lancamentosSelecionados.isEmpty()) {
					warnMessage("Nenhum lançamento a excluir!");
				} else {
					actionTitle = " - Excluir";
					return "/pages/MovimentacaoLancamento/excluirLancamentos";
				}
			} else {
				warnMessage("Nenhum lançamento selecionado!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage(e.getMessage());
		}
		return "";
	}
	
	public String excluirLancamentos() {
		try {
			getService().excluirLancamentos(lancamentosSelecionados);
			infoMessage("Lançamentos excluídos com sucesso!");
			return cancel();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return ""; 
	}
	
	public String transferirView() {
		try {
			actionTitle = " - Transferir";
			lancamentoATransferir = new LancamentoConta();
			return "/pages/MovimentacaoLancamento/transferirLancamento";
		} catch (Exception e) {
			errorMessage(e.getMessage());
		}
		return "";
	}
	
	public String transferirLancamentos() {
		try {
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("CONTA_ORIGEM", contaSelecionada);
			parametros.put("CONTA_DESTINO", contaDestino);
			parametros.put("CATEGORIA_ORIGEM", categoriaSelecionada);
			parametros.put("CATEGORIA_DESTINO", categoriaDestino);
			parametros.put("FAVORECIDO_DESTINO", favorecidoSelecionado);
			parametros.put("MEIOPAGAMENTO_DESTINO", meioPagamentoSelecionado);
			getService().transferirLancamentos(lancamentoATransferir, parametros);
			infoMessage("Valor transferido com sucesso!");
			return cancel();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return ""; 
	}
	
	public String alterarPropriedadesView() {
		try {
			actionTitle = " - Alterar propriedades";
			return "/pages/MovimentacaoLancamento/propriedadesLancamento";
		} catch (Exception e) {
			errorMessage(e.getMessage());
		}
		return "";
	}
	
	public String alterarPropriedades() {
		try {
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("DESCRICAO_DESTINO", descricao);
			parametros.put("OBSERVACAO_DESTINO", observacao);
			parametros.put("CATEGORIA_DESTINO", categoriaSelecionada);
			parametros.put("FAVORECIDO_DESTINO", favorecidoSelecionado);
			parametros.put("MEIOPAGAMENTO_DESTINO", meioPagamentoSelecionado);
			getService().alterarPropriedades(lancamentosSelecionados, parametros);
			infoMessage("Propriedades alteradas com sucesso!");
			return cancel();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return ""; 
	}
	
	public void excluirLancamentoListaSelecionados() {
		if (lancamentosSelecionados.size() > 1)
			lancamentosSelecionados.remove(lancamentoSelecionado);
		else {
			warnMessage("É necessário ter um lançamento selecionado!");
		}
	}
	
	public void atualizaComboCategorias() {
		this.getListaCategoria();
	}
	
	public List<Categoria> getListaCategoria() {
		try {
			if (tipoCategoriaSelecionada != null)
				return categoriaService.buscarAtivosPorTipoCategoriaEUsuario(tipoCategoriaSelecionada, getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		} 
		return new ArrayList<Categoria>();
	}
	
	public List<Categoria> getListaCategoriaCredito() {
		try {
			return categoriaService.buscarAtivosPorTipoCategoriaEUsuario(TipoCategoria.CREDITO, getUsuarioLogado());
		} catch (BusinessException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<Categoria> getListaCategoriaDebito() {
		try {
			return categoriaService.buscarAtivosPorTipoCategoriaEUsuario(TipoCategoria.DEBITO, getUsuarioLogado());
		} catch (BusinessException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<Favorecido> getListaFavorecido() {
		try {
			return favorecidoService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<MeioPagamento> getListaMeioPagamento() {
		try {
			return meioPagamentoService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<Conta> getListaContaAtivo() {
		try {
			return contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", null, getUsuarioLogado(), true);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public IMovimentacaoLancamento getService() {
		return service;
	}

	public void setService(IMovimentacaoLancamento service) {
		this.service = service;
	}

	public ICategoria getCategoriaService() {
		return categoriaService;
	}

	public void setCategoriaService(ICategoria categoriaService) {
		this.categoriaService = categoriaService;
	}

	public int getQuantADuplicar() {
		return quantADuplicar;
	}

	public void setQuantADuplicar(int quantADuplicar) {
		this.quantADuplicar = quantADuplicar;
	}

	public List<LancamentoConta> getLancamentosSelecionados() {
		return lancamentosSelecionados;
	}

	public void setLancamentosSelecionados(
			List<LancamentoConta> lancamentosSelecionados) {
		this.lancamentosSelecionados = lancamentosSelecionados;
	}

	public LancamentoConta getLancamentoSelecionado() {
		return lancamentoSelecionado;
	}

	public void setLancamentoSelecionado(LancamentoConta lancamentoSelecionado) {
		this.lancamentoSelecionado = lancamentoSelecionado;
	}

	public Conta getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(Conta contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}

	public Categoria getCategoriaSelecionada() {
		return categoriaSelecionada;
	}

	public void setCategoriaSelecionada(Categoria categoriaSelecionada) {
		this.categoriaSelecionada = categoriaSelecionada;
	}

	public Favorecido getFavorecidoSelecionado() {
		return favorecidoSelecionado;
	}

	public void setFavorecidoSelecionado(Favorecido favorecidoSelecionado) {
		this.favorecidoSelecionado = favorecidoSelecionado;
	}

	public MeioPagamento getMeioPagamentoSelecionado() {
		return meioPagamentoSelecionado;
	}

	public void setMeioPagamentoSelecionado(MeioPagamento meioPagamentoSelecionado) {
		this.meioPagamentoSelecionado = meioPagamentoSelecionado;
	}

	public Conta getContaDestino() {
		return contaDestino;
	}

	public void setContaDestino(Conta contaDestino) {
		this.contaDestino = contaDestino;
	}

	public Categoria getCategoriaDestino() {
		return categoriaDestino;
	}

	public void setCategoriaDestino(Categoria categoriaDestino) {
		this.categoriaDestino = categoriaDestino;
	}

	public LancamentoConta getLancamentoATransferir() {
		return lancamentoATransferir;
	}

	public void setLancamentoATransferir(LancamentoConta lancamentoATransferir) {
		this.lancamentoATransferir = lancamentoATransferir;
	}

	public IncrementoClonagemLancamento getIncremento() {
		return incremento;
	}

	public void setIncremento(IncrementoClonagemLancamento incremento) {
		this.incremento = incremento;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public TipoCategoria getTipoCategoriaSelecionada() {
		return tipoCategoriaSelecionada;
	}

	public void setTipoCategoriaSelecionada(TipoCategoria tipoCategoriaSelecionada) {
		this.tipoCategoriaSelecionada = tipoCategoriaSelecionada;
	}
}