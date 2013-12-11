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

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.BuscaSalva;
import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.enumeration.TipoAgrupamentoBusca;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IBuscaSalva;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.ILancamentoConta;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.model.AgrupamentoLancamento;
import br.com.hslife.orcamento.model.CriterioLancamentoConta;
import br.com.hslife.orcamento.util.Util;

@Component("lancamentoCartaoMB")
@Scope("session")
public class LancamentoCartaoController extends AbstractCRUDController<LancamentoConta>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4809986079850307573L;
	
	@Autowired
	private ILancamentoConta service;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private ICategoria categoriaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;
	
	@Autowired
	private MovimentacaoLancamentoCartaoController movimentacaoLancamentoCartaoMB;
	
	@Autowired
	private IBuscaSalva buscaSalvaService;
	
	@Autowired
	private IMoeda moedaService;
	
	private CriterioLancamentoConta criterioBusca = new CriterioLancamentoConta();
	
	private String agrupamentoSelecionado;
	private boolean exibirSaldoUltimoFechamento;
	private TipoCategoria tipoCategoriaSelecionada;
	
	private List<Categoria> agrupamentoLancamentoPorCategoria = new ArrayList<Categoria>();
	private List<Favorecido> agrupamentoLancamentoPorFavorecido = new ArrayList<Favorecido>();
	private List<MeioPagamento> agrupamentoLancamentoPorMeioPagamento = new ArrayList<MeioPagamento>();
	private List<AgrupamentoLancamento> agrupamentoLancamentoPorDebitoCredito = new ArrayList<AgrupamentoLancamento>();
	
	private BuscaSalva buscaSalva = new BuscaSalva();
	private List<BuscaSalva> buscasSalvas = new ArrayList<BuscaSalva>();
	
	public LancamentoCartaoController() {
		super(new LancamentoConta());
		
		moduleTitle = "Lançamentos do Cartão";
		
		goToListPage = "/pages/LancamentoCartao/listLancamentoCartao";
		goToFormPage = "/pages/LancamentoCartao/formLancamentoCartao";
		goToViewPage = "/pages/LancamentoCartao/viewLancamentoCartao";
	}

	@Override
	protected void initializeEntity() {
		entity = new LancamentoConta();
		listEntity = new ArrayList<LancamentoConta>();
		
		agrupamentoLancamentoPorCategoria = new ArrayList<Categoria>();
		agrupamentoLancamentoPorFavorecido = new ArrayList<Favorecido>();
		agrupamentoLancamentoPorMeioPagamento = new ArrayList<MeioPagamento>();
		agrupamentoLancamentoPorDebitoCredito = new ArrayList<AgrupamentoLancamento>();
		
		buscasSalvas.clear();
	}
	
	private void initializeMovimentaLancamentoMB() {
		movimentacaoLancamentoCartaoMB.setService(getService());
		movimentacaoLancamentoCartaoMB.setCategoriaService(categoriaService);
		movimentacaoLancamentoCartaoMB.setFavorecidoService(favorecidoService);
		movimentacaoLancamentoCartaoMB.setMeioPagamentoService(meioPagamentoService);
		movimentacaoLancamentoCartaoMB.setContaService(contaService);
	}
	
	@Override
	public void find() {
		try {
			if (criterioBusca.getConta() == null) {
				warnMessage("Informe a conta!");
			} else {
				listEntity = getService().buscarPorCriterioLancamentoConta(criterioBusca);
				
				if (agrupamentoSelecionado.equals("CAT"))
					agrupamentoLancamentoPorCategoria = getService().organizarLancamentosPorCategoria(listEntity);
				if (agrupamentoSelecionado.equals("FAV"))
					agrupamentoLancamentoPorFavorecido = getService().organizarLancamentosPorFavorecido(listEntity);
				if (agrupamentoSelecionado.equals("PAG"))
					agrupamentoLancamentoPorMeioPagamento = getService().organizarLancamentosPorMeioPagamento(listEntity);
				if (agrupamentoSelecionado.equals("CD"))
					agrupamentoLancamentoPorDebitoCredito = getService().organizarLancamentosPorDebitoCredito(listEntity);
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String save() {
		entity.setDataPagamento(entity.getDataLancamento());
		return super.save();
	}
	
	@Override
	public String edit() {
		String goToPage = super.edit();
		if (goToPage.equals(goToFormPage)) {
			atualizaComboCategorias();
		}
		return goToPage;
	}
	
	public void carregarArquivo(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (event.getFile().getSize() > 16777216) {
				errorMessage("Arquivo excedeu o tamanho máximo de 16 MB!");
			} else {
				if (entity.getArquivo() == null) entity.setArquivo(new Arquivo());
				entity.getArquivo().setDados(event.getFile().getContents());
				entity.getArquivo().setNomeArquivo(event.getFile().getFileName().replace(" ", "."));
				entity.getArquivo().setContentType(event.getFile().getContentType());
				entity.getArquivo().setTamanho(event.getFile().getSize());
				//entity.setArquivo(arquivo);
				infoMessage("Arquivo anexado com sucesso!");
			}
		} else {
			infoMessage("Nenhum arquivo anexado!");
		}
	}
	
	public void baixarArquivo() {
		if (entity.getArquivo() == null || entity.getArquivo().getDados() == null || entity.getArquivo().getDados().length == 0) {
			warnMessage("Nenhum arquivo adicionado!");
		} else {
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			try {			
				response.setContentType(entity.getArquivo().getContentType());
				response.setHeader("Content-Disposition","attachment; filename=" + entity.getArquivo().getNomeArquivo());
				response.setContentLength(entity.getArquivo().getDados().length);
				ServletOutputStream output = response.getOutputStream();
				output.write(entity.getArquivo().getDados(), 0, entity.getArquivo().getDados().length);
				FacesContext.getCurrentInstance().responseComplete();
			} catch (Exception e) {
				errorMessage(e.getMessage());
			}
		}
	}
	
	public void excluirArquivo() {
		if (entity.getArquivo() == null || entity.getArquivo().getDados() == null || entity.getArquivo().getDados().length == 0) {
			warnMessage("Nenhum arquivo adicionado!");
		} else {
			entity.setArquivo(new Arquivo());
			infoMessage("Arquivo excluído! Salve para confirmar as alterações.");
		}
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
	
	public void atualizaComboLancamentosImportado() {
		this.getListaLancamentoImportado();
	}
	
	public void atualizaComboBuscasSalvas() {
		try {
			buscasSalvas = buscaSalvaService.buscarPorConta(criterioBusca.getConta());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String mover() {
		initializeMovimentaLancamentoMB();
		movimentacaoLancamentoCartaoMB.setLancamentosSelecionados(new ArrayList<LancamentoConta>());
		if (listEntity != null && !listEntity.isEmpty()) {
			for (LancamentoConta l : listEntity) {
				if (l.isSelecionado() && !l.isQuitado()) {
					movimentacaoLancamentoCartaoMB.getLancamentosSelecionados().add(l);
				}
			}
			if (!movimentacaoLancamentoCartaoMB.getLancamentosSelecionados().isEmpty()) {
				initializeEntity();
				return movimentacaoLancamentoCartaoMB.moverView();
			} else {
				warnMessage("Nenhum lançamento a mover!");
			}
		} else {
			warnMessage("Nenhum lançamento selecionado!");
		}
		return "";
	}
	
	public String copiar() {
		initializeMovimentaLancamentoMB();
		movimentacaoLancamentoCartaoMB.setLancamentosSelecionados(new ArrayList<LancamentoConta>());
		if (listEntity != null && !listEntity.isEmpty()) {
			for (LancamentoConta l : listEntity) {
				if (l.isSelecionado()) {
					movimentacaoLancamentoCartaoMB.getLancamentosSelecionados().add(l);
				}
			}
			if (!movimentacaoLancamentoCartaoMB.getLancamentosSelecionados().isEmpty()) {
				initializeEntity();
				return movimentacaoLancamentoCartaoMB.copiarView();
			} else {
				warnMessage("Nenhum lançamento a copiar!");
			}
		} else {
			warnMessage("Nenhum lançamento selecionado!");
		}
		return "";
	}
	
	public String duplicar() {
		initializeMovimentaLancamentoMB();
		movimentacaoLancamentoCartaoMB.setLancamentosSelecionados(new ArrayList<LancamentoConta>());
		if (listEntity != null && !listEntity.isEmpty()) {
			for (LancamentoConta l : listEntity) {
				if (l.isSelecionado()) {
					movimentacaoLancamentoCartaoMB.getLancamentosSelecionados().add(l);
				}
			}
			if (!movimentacaoLancamentoCartaoMB.getLancamentosSelecionados().isEmpty()) {
				initializeEntity();
				return movimentacaoLancamentoCartaoMB.duplicarView();
			} else {
				warnMessage("Nenhum lançamento a duplicar!");
			}
		} else {
			warnMessage("Nenhum lançamento selecionado!");
		}
		return "";
	}
	
	public String excluir() {
		initializeMovimentaLancamentoMB();
		movimentacaoLancamentoCartaoMB.setLancamentosSelecionados(new ArrayList<LancamentoConta>());
		if (listEntity != null && !listEntity.isEmpty()) {
			for (LancamentoConta l : listEntity) {
				if (l.isSelecionado() && !l.isQuitado()) {
					movimentacaoLancamentoCartaoMB.getLancamentosSelecionados().add(l);
				}
			}
			if (!movimentacaoLancamentoCartaoMB.getLancamentosSelecionados().isEmpty()) {
				initializeEntity();
				return movimentacaoLancamentoCartaoMB.excluirView();
			} else {
				warnMessage("Nenhum lançamento a excluir!");
			}
		} else {
			warnMessage("Nenhum lançamento selecionado!");
		}
		return "";
	}
	
	public String transferir() {
		initializeMovimentaLancamentoMB();
		return movimentacaoLancamentoCartaoMB.transferirView();
	}
	
	public void quebrarVinculo() {		
		entity.setHashImportacao(null);
		warnMessage("Vínculo quebrado. Salve para efetivar as alterações.");
	}
	
	public void processarBuscaSalva() {
		if (buscaSalva != null) {
			criterioBusca.setConta(buscaSalva.getConta());
			criterioBusca.setDescricao(buscaSalva.getTextoBusca());
			criterioBusca.setLancadoEm(buscaSalva.getLancadoEm());
			criterioBusca.setParcela(buscaSalva.getTextoParcela());			
			switch (buscaSalva.getTipoAgrupamentoBusca()) {
				case DEBITO_CREDITO : agrupamentoSelecionado = "CD"; break;
				case CATEGORIA : agrupamentoSelecionado = "CAT"; break;
				case FAVORECIDO : agrupamentoSelecionado = "FAV"; break;
				default : agrupamentoSelecionado = "";
			}
			this.find();
		} else {
			warnMessage("Nenhuma busca selecionada!");
		}
	}
	
	public String salvarBuscaView() {
		buscaSalva = new BuscaSalva();
		
		buscaSalva.setConta(criterioBusca.getConta());
		buscaSalva.setTextoBusca(criterioBusca.getDescricao());
		buscaSalva.setTextoParcela(criterioBusca.getParcela());
		buscaSalva.setLancadoEm(criterioBusca.getLancadoEm());
		
		if (agrupamentoSelecionado.equals("CD")) buscaSalva.setTipoAgrupamentoBusca(TipoAgrupamentoBusca.DEBITO_CREDITO);
		if (agrupamentoSelecionado.equals("CAT")) buscaSalva.setTipoAgrupamentoBusca(TipoAgrupamentoBusca.CATEGORIA);
		if (agrupamentoSelecionado.equals("FAV")) buscaSalva.setTipoAgrupamentoBusca(TipoAgrupamentoBusca.FAVORECIDO);
		
		actionTitle = " - Salvar busca";
		return "/pages/LancamentoCartao/salvarBusca";
	}
	
	public String salvarBusca() {
		try {
			buscaSalvaService.cadastrar(buscaSalva);
			infoMessage("Busca cadastrada com sucesso!");
			buscasSalvas.clear();
			return list();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}		
		return "";
	}
	
	public String excluirBuscaView() {
		if (buscaSalva == null) {
			warnMessage("Nenhuma busca selecionada!");
			return "";
		}
		actionTitle = " - Excluir busca";
		return "/pages/LancamentoCartao/excluirBusca";
	}
	
	public String excluirBusca() {
		try {
			buscaSalvaService.excluir(buscaSalva);
			infoMessage("Busca excluída com sucesso!");
			buscasSalvas.clear();
			return list();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}		
		return "";
	}
	
	public List<Conta> getListaConta() {
		try {
			// Obtém o valor da opção do sistema
			OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("CONTA_EXIBIR_INATIVAS", getUsuarioLogado());
			
			// Determina qual listagem será retornada
			if (opcao != null && Boolean.valueOf(opcao.getValor()))
				return contaService.buscarSomenteTipoCartaoPorUsuario(getUsuarioLogado());
			else 
				return contaService.buscarSomenteTipoCartaoAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public List<Conta> getListaContaAtivo() {
		try {
			return contaService.buscarSomenteTipoCartaoAtivosPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public List<Categoria> getListaCategoria() {
		try {
			if (tipoCategoriaSelecionada != null)
				return categoriaService.buscarPorTipoCategoriaEUsuario(tipoCategoriaSelecionada, getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		} 
		return new ArrayList<Categoria>();
	}
	
	public List<Favorecido> getListaFavorecido() {
		try {
			return favorecidoService.buscarPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Favorecido>();
	}
	
	public List<MeioPagamento> getListaMeioPagamento() {
		try {
			return meioPagamentoService.buscarPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<MeioPagamento>();
	}
	
	public List<LancamentoImportado> getListaLancamentoImportado() {
		try {
			return getService().buscarLancamentoImportadoPorConta(entity.getConta());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<LancamentoImportado>();
	}
	
	public List<Moeda> getListaMoeda() {
		try {
			return moedaService.buscarPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<BuscaSalva> getBuscasSalvas() {
		try {
			if (buscasSalvas.isEmpty()) {
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("CONTA_EXIBIR_INATIVAS", getUsuarioLogado());
				
				// Determina qual listagem será retornada
				if (opcao != null && Boolean.valueOf(opcao.getValor()))
					if (criterioBusca.getConta() == null)
						buscasSalvas = buscaSalvaService.buscarTodosContaCartaoPorUsuario(getUsuarioLogado());
					else
						buscasSalvas = buscaSalvaService.buscarTodosPorContaEUsuario(criterioBusca.getConta(), getUsuarioLogado());
				else 
					if (criterioBusca.getConta() == null)
						buscasSalvas = buscaSalvaService.buscarTodosContaCartaoAtivaPorUsuario(getUsuarioLogado());
					else
						buscasSalvas = buscaSalvaService.buscarTodosContaAtivaPorContaEUsuario(criterioBusca.getConta(), getUsuarioLogado());
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return buscasSalvas;
	}
	
	public String getSaldoTotalFormatado() {
		if (listEntity == null || listEntity.isEmpty()) {
			return Util.moedaBrasil(0.0);
		} else {
			if(exibirSaldoUltimoFechamento)
				try {
					return Util.moedaBrasil(getService().saldoUltimoFechamento(criterioBusca.getConta()) + getService().calcularSaldoLancamentos(listEntity));
				}
				catch (BusinessException be) {
					errorMessage(be.getMessage());
				}
			else
				return Util.moedaBrasil(getService().calcularSaldoLancamentos(listEntity));
		}
		return Util.moedaBrasil(0.0);
	}
	
	public List<SelectItem> getListaTipoLancamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		listaSelectItem.add(new SelectItem(TipoLancamento.RECEITA, "Receita"));
		listaSelectItem.add(new SelectItem(TipoLancamento.DESPESA, "Despesa"));
		return listaSelectItem;
	}

	public ILancamentoConta getService() {
		return service;
	}

	public void setService(ILancamentoConta service) {
		this.service = service;
	}

	public void setContaService(IConta contaService) {
		this.contaService = contaService;
	}

	public void setCategoriaService(ICategoria categoriaService) {
		this.categoriaService = categoriaService;
	}

	public void setFavorecidoService(IFavorecido favorecidoService) {
		this.favorecidoService = favorecidoService;
	}

	public void setMeioPagamentoService(IMeioPagamento meioPagamentoService) {
		this.meioPagamentoService = meioPagamentoService;
	}

	public CriterioLancamentoConta getCriterioBusca() {
		return criterioBusca;
	}

	public void setCriterioBusca(CriterioLancamentoConta criterioBusca) {
		this.criterioBusca = criterioBusca;
	}

	public String getAgrupamentoSelecionado() {
		return agrupamentoSelecionado;
	}

	public void setAgrupamentoSelecionado(String agrupamentoSelecionado) {
		this.agrupamentoSelecionado = agrupamentoSelecionado;
	}

	public boolean isExibirSaldoUltimoFechamento() {
		return exibirSaldoUltimoFechamento;
	}

	public void setExibirSaldoUltimoFechamento(boolean exibirSaldoUltimoFechamento) {
		this.exibirSaldoUltimoFechamento = exibirSaldoUltimoFechamento;
	}

	public TipoCategoria getTipoCategoriaSelecionada() {
		return tipoCategoriaSelecionada;
	}

	public void setTipoCategoriaSelecionada(TipoCategoria tipoCategoriaSelecionada) {
		this.tipoCategoriaSelecionada = tipoCategoriaSelecionada;
	}

	public List<Categoria> getAgrupamentoLancamentoPorCategoria() {
		return agrupamentoLancamentoPorCategoria;
	}

	public void setAgrupamentoLancamentoPorCategoria(
			List<Categoria> agrupamentoLancamentoPorCategoria) {
		this.agrupamentoLancamentoPorCategoria = agrupamentoLancamentoPorCategoria;
	}

	public List<Favorecido> getAgrupamentoLancamentoPorFavorecido() {
		return agrupamentoLancamentoPorFavorecido;
	}

	public void setAgrupamentoLancamentoPorFavorecido(
			List<Favorecido> agrupamentoLancamentoPorFavorecido) {
		this.agrupamentoLancamentoPorFavorecido = agrupamentoLancamentoPorFavorecido;
	}

	public List<MeioPagamento> getAgrupamentoLancamentoPorMeioPagamento() {
		return agrupamentoLancamentoPorMeioPagamento;
	}

	public void setAgrupamentoLancamentoPorMeioPagamento(
			List<MeioPagamento> agrupamentoLancamentoPorMeioPagamento) {
		this.agrupamentoLancamentoPorMeioPagamento = agrupamentoLancamentoPorMeioPagamento;
	}

	public List<AgrupamentoLancamento> getAgrupamentoLancamentoPorDebitoCredito() {
		return agrupamentoLancamentoPorDebitoCredito;
	}

	public void setAgrupamentoLancamentoPorDebitoCredito(
			List<AgrupamentoLancamento> agrupamentoLancamentoPorDebitoCredito) {
		this.agrupamentoLancamentoPorDebitoCredito = agrupamentoLancamentoPorDebitoCredito;
	}
	
	public BuscaSalva getBuscaSalva() {
		return buscaSalva;
	}

	public void setBuscaSalva(BuscaSalva buscaSalva) {
		this.buscaSalva = buscaSalva;
	}

	public void setBuscaSalvaService(IBuscaSalva buscaSalvaService) {
		this.buscaSalvaService = buscaSalvaService;
	}

	public void setMovimentacaoLancamentoCartaoMB(
			MovimentacaoLancamentoCartaoController movimentacaoLancamentoCartaoMB) {
		this.movimentacaoLancamentoCartaoMB = movimentacaoLancamentoCartaoMB;
	}

	public void setMoedaService(IMoeda moedaService) {
		this.moedaService = moedaService;
	}
}