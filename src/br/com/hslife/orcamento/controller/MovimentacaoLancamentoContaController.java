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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.IMeioPagamento;

@Component("movimentacaoLancamentoContaMB")
@Scope("session")
public class MovimentacaoLancamentoContaController extends AbstractController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2116626869708662527L;
	
	private ILancamentoConta service;
	private IConta contaService;
	private ICategoria categoriaService;
	private IFavorecido favorecidoService;
	private IMeioPagamento meioPagamentoService;
	
	private String opcaoMovimentar;
	private int quantADuplicar = 1;
	private String incrementarData;
	private LancamentoConta lancamentoATransferir;
	
	private List<LancamentoConta> lancamentosSelecionados;
	
	private LancamentoConta lancamentoSelecionado;	
	private Conta contaSelecionada;
	private Conta contaDestino;	
	private Categoria categoriaSelecionada;
	private Categoria categoriaDestino;	
	private Favorecido favorecidoSelecionado;		
	private MeioPagamento meioPagamentoSelecionado;
	
	private String goToListPage = "/pages/LancamentoConta/listLancamentoConta";
	

	@Override
	public String startUp() {
		return null;
	}
	
	@Override
	public String getModuleTitle() {		
		return super.getModuleTitle() + actionTitle;
	}
	
	public MovimentacaoLancamentoContaController() {
		lancamentosSelecionados = new ArrayList<LancamentoConta>();
		operation = "list";
		
		moduleTitle = "Lançamentos da Conta";
	}
	
	@Override
	protected void initializeEntity() {
		lancamentosSelecionados = new ArrayList<LancamentoConta>();		
	}
		
	public String cancel() {
		initializeEntity();
		return goToListPage;
	}
	
	public String movimentarLancamentos() {		
		if (opcaoMovimentar.equals("MOVE")) {
			return moverLancamentos();
		}
		if (opcaoMovimentar.equals("COPY")) {
			return copiarLancamentos();
		}
		if (opcaoMovimentar.equals("DOUBLE")) {
			return duplicarLancamentos();
		}
		if (opcaoMovimentar.equals("DELETE")) {
			return excluirLancamentos();
		}
		if (opcaoMovimentar.equals("TRANSFER")) {
			return transferirLancamentos();
		}
		return "";
	}
	
	public String moverView() {
		try {
			actionTitle = " - Mover";
			opcaoMovimentar = "MOVE";
			return "/pages/LancamentoConta/moverLancamentoConta";
		} catch (Exception e) {
			errorMessage(e.getMessage());
		}
		return "";
	}
	
	public String moverLancamentos() {
		try {
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("CONTA_DESTINO", contaSelecionada);
			if (categoriaSelecionada != null) 
				parametros.put("CATEGORIA_DESTINO", categoriaSelecionada);
			if (favorecidoSelecionado != null) 
				parametros.put("FAVORECIDO_DESTINO", favorecidoSelecionado);
			if (meioPagamentoSelecionado != null) 
				parametros.put("MEIOPAGAMENTO_DESTINO", meioPagamentoSelecionado);
			getService().moverLancamentos(lancamentosSelecionados, parametros);
			infoMessage("Lançamentos movidos com sucesso!");
			return goToListPage;
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String copiarView() {
		try {
			actionTitle = " - Copiar";
			opcaoMovimentar = "COPY";
			return "/pages/LancamentoConta/copiarLancamentoConta";
		} catch (Exception e) {
			errorMessage(e.getMessage());
		}
		return "";
	}
	
	public String copiarLancamentos() {
		try {
			Map<String, Object> parametros = new HashMap<String, Object>();
			if (contaSelecionada != null)
				parametros.put("CONTA_DESTINO", contaSelecionada);
			if (categoriaSelecionada != null) 
				parametros.put("CATEGORIA_DESTINO", categoriaSelecionada);
			if (favorecidoSelecionado != null) 
				parametros.put("FAVORECIDO_DESTINO", favorecidoSelecionado);
			if (meioPagamentoSelecionado != null) 
				parametros.put("MEIOPAGAMENTO_DESTINO", meioPagamentoSelecionado);
			getService().copiarLancamentos(lancamentosSelecionados, parametros);
			infoMessage("Lançamentos copiados com sucesso!");
			return goToListPage;
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return ""; 
	}
	
	public String duplicarView() {
		try {
			actionTitle = " - Duplicar";
			opcaoMovimentar = "DOUBLE";	
			return "/pages/LancamentoConta/duplicarLancamentoConta";
		} catch (Exception e) {
			errorMessage(e.getMessage());
		}
		return "";
	}
	
	public String duplicarLancamentos() {
		try {
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("QUANT_DUPLICAR", quantADuplicar);
			parametros.put("INCREMENTAR_DATA", incrementarData);
			if (contaSelecionada != null)
				parametros.put("CONTA_DESTINO", contaSelecionada);						
			if (categoriaSelecionada != null) 
				parametros.put("CATEGORIA_DESTINO", categoriaSelecionada);
			if (favorecidoSelecionado != null) 
				parametros.put("FAVORECIDO_DESTINO", favorecidoSelecionado);
			if (meioPagamentoSelecionado != null) 
				parametros.put("MEIOPAGAMENTO_DESTINO", meioPagamentoSelecionado);
			getService().duplicarLancamentos(lancamentosSelecionados, parametros);
			infoMessage("Lançamentos duplicados com sucesso!");
			return goToListPage;
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String excluirView() {
		actionTitle = " - Excluir";
		opcaoMovimentar = "DELETE";
		return "/pages/LancamentoConta/excluirLancamentoConta";
	}
	
	public String excluirLancamentos() {
		try {
			Map<String, Object> parametros = new HashMap<String, Object>();
			getService().excluirLancamentos(lancamentosSelecionados, parametros);
			infoMessage("Lançamentos excluídos com sucesso!");
			return goToListPage;
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return ""; 
	}
	
	public String transferirView() {
		try {
			actionTitle = " - Transferir";
			opcaoMovimentar = "TRANSFER";
			lancamentoATransferir = new LancamentoConta();
			return "/pages/LancamentoConta/transferirLancamentoConta";
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
			return goToListPage;
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

	public List<Conta> getListaContaAtiva() {
		try {
			return contaService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public List<SelectItem> getListaCategoriaAtivo() {
		List<SelectItem> listaCategoria = new ArrayList<SelectItem>();
		try {
			for (Categoria c : categoriaService.buscarAtivosPorUsuario(getUsuarioLogado())) {
				listaCategoria.add(new SelectItem(c, c.getTipoCategoria() + " - " + c.getDescricao()));
			}
			return listaCategoria;
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		} 
		return new ArrayList<SelectItem>();
	}
	
	public List<Categoria> getListaCategoriaDebitoAtivo() {		
		try {
			return categoriaService.buscarPorTipoCategoriaEUsuario(TipoCategoria.DEBITO, getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Categoria>();
	}
	
	public List<Categoria> getListaCategoriaCreditoAtivo() {
		try {
			return categoriaService.buscarPorTipoCategoriaEUsuario(TipoCategoria.CREDITO, getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Categoria>();
	}
	
	public List<Favorecido> getListaFavorecidoAtivo() {
		try {
			return favorecidoService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Favorecido>();
	}
	
	public List<MeioPagamento> getListaMeioPagamentoAtivo() {
		try {
			return meioPagamentoService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<MeioPagamento>();
	}
	
	public ILancamentoConta getService() {
		return service;
	}

	public void setService(ILancamentoConta service) {
		this.service = service;
	}

	public IConta getContaService() {
		return contaService;
	}

	public void setContaService(IConta contaService) {
		this.contaService = contaService;
	}

	public ICategoria getCategoriaService() {
		return categoriaService;
	}

	public void setCategoriaService(ICategoria categoriaService) {
		this.categoriaService = categoriaService;
	}

	public IFavorecido getFavorecidoService() {
		return favorecidoService;
	}

	public void setFavorecidoService(IFavorecido favorecidoService) {
		this.favorecidoService = favorecidoService;
	}

	public IMeioPagamento getMeioPagamentoService() {
		return meioPagamentoService;
	}

	public void setMeioPagamentoService(IMeioPagamento meioPagamentoService) {
		this.meioPagamentoService = meioPagamentoService;
	}

	public String getOpcaoMovimentar() {
		return opcaoMovimentar;
	}

	public void setOpcaoMovimentar(String opcaoMovimentar) {
		this.opcaoMovimentar = opcaoMovimentar;
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

	public String getGoToListPage() {
		return goToListPage;
	}

	public void setGoToListPage(String goToListPage) {
		this.goToListPage = goToListPage;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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

	public String getIncrementarData() {
		return incrementarData;
	}

	public void setIncrementarData(String incrementarData) {
		this.incrementarData = incrementarData;
	}

	public LancamentoConta getLancamentoATransferir() {
		return lancamentoATransferir;
	}

	public void setLancamentoATransferir(LancamentoConta lancamentoATransferir) {
		this.lancamentoATransferir = lancamentoATransferir;
	}
}