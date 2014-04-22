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
import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.entity.PagamentoPeriodo;
import br.com.hslife.orcamento.enumeration.PeriodoLancamento;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.ILancamentoPeriodico;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IMoeda;

@Component("lancamentoPeriodicoMB")
@Scope("session")
public class LancamentoPeriodicoController extends AbstractCRUDController<LancamentoPeriodico>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4809986079850307573L;
	
	@Autowired
	private ILancamentoPeriodico service;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private ICategoria categoriaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;
	
	private Conta contaSelecionada;
	private PagamentoPeriodo pagamentoPeriodo;
	private Moeda moedaPadrao;
	private StatusLancamento statusLancamento;
	private TipoCategoria tipoCategoriaSelecionada;
	private TipoLancamentoPeriodico tipoLancamentoPeriodico;
	private boolean parcelamento;
	private boolean selecionarTodosLancamentos;
	private boolean gerarLancamento;
	
	public LancamentoPeriodicoController() {
		super(new LancamentoPeriodico());

		moduleTitle = "Despesas fixas e parceladas";
	}

	@Override
	protected void initializeEntity() {
		entity = new LancamentoPeriodico();
		listEntity = new ArrayList<LancamentoPeriodico>();
	}
	
	@Override
	public void find() {
		try {
			listEntity = getService().buscarPorTipoLancamentoContaEStatusLancamento(tipoLancamentoPeriodico, contaSelecionada, statusLancamento);
			this.carregarMoedaPadrao();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String save() {
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}
	
	@Override
	public String edit() {
		String goToPage = super.edit();
		if (goToPage.equals(goToFormPage)) {
			atualizaComboCategorias();
			atualizaPainelCadastro();
		}
		return goToPage;
	}
	
	private String alterarStatus(StatusLancamento novoStatus) {
		try {
			// Altera o status da entidade
			getService().alterarStatusLancamento(entity, novoStatus);
			
			infoMessage("Status alterado com sucesso!");
			
			// Verifica se a listagem de resultados está nula ou não para poder efetuar novamente a busca
			if (listEntity != null && !listEntity.isEmpty()) {
				// Inicializa os objetos
				initializeEntity();
				
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", getUsuarioLogado());
							
				// Determina se a busca será executada novamente
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {					
					find();
				}
			} else {
				initializeEntity();
			}
			
			return list();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String suspenderLancamento() {
		return this.alterarStatus(StatusLancamento.SUSPENSO);
	}
	
	public String ativarLancamento() {
		return this.alterarStatus(StatusLancamento.ATIVO);
	}
	
	public String encerrarLancamento() {
		return this.alterarStatus(StatusLancamento.ENCERRADO);
	}
	
	public String registrarPagamentoView() {
		try {
			entity = getService().buscarPorID(idEntity);
			actionTitle = " - Registrar pagamento";
			gerarLancamento = false;
			return "/pages/LancamentoPeriodico/registrarPagamento";
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String registrarPagamento() {
		try {
			pagamentoPeriodo.setGerarLancamento(gerarLancamento);
			getService().registrarPagamento(pagamentoPeriodo);
			infoMessage("Pagamento registrado com sucesso!");
			pagamentoPeriodo = new PagamentoPeriodo();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	private void carregarMoedaPadrao() {
		try {
			if (moedaPadrao == null) {
				moedaPadrao = moedaService.buscarPadraoPorUsuario(getUsuarioLogado());
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void carregarArquivo(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (entity.getArquivo() == null) entity.setArquivo(new Arquivo());
			entity.getArquivo().setDados(event.getFile().getContents());
			entity.getArquivo().setNomeArquivo(event.getFile().getFileName().replace(" ", "."));
			entity.getArquivo().setContentType(event.getFile().getContentType());
			entity.getArquivo().setTamanho(event.getFile().getSize());			
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
			entity.setArquivo(null);
			infoMessage("Arquivo excluído! Salve para confirmar as alterações.");
		}
	}
	
	public void atualizaPainelCadastro() {
		if (entity.getTipoLancamentoPeriodico() != null && entity.getTipoLancamentoPeriodico().equals(TipoLancamentoPeriodico.FIXO)) {
			parcelamento = false;
		} else {
			parcelamento = true;
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
	
	public void carregarPagamentoPeriodo() {
		// método criado para carregar os dados do pagamento do período
	}
	
	public List<PagamentoPeriodo> getListaPagamentoPeriodo() {
		try {
			return getService().buscarPagamentosNaoPagosPorLancamentoPeriodico(entity);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public double getTotalAPagar() {
		double valor = 0.0;
		if (entity.getPagamentos() != null && entity.getPagamentos().size() > 0) {
			for (PagamentoPeriodo pagamento : entity.getPagamentos()) {
				if (!pagamento.isPago()) {
					valor += entity.getValorParcela();
				}
			}
		}
		return valor;
	}
	
	public double getTotalPago() {
		double valor = 0.0;
		if (entity.getPagamentos() != null && entity.getPagamentos().size() > 0) {
			for (PagamentoPeriodo pagamento : entity.getPagamentos()) {
				if (pagamento.isPago()) {
					valor += pagamento.getValorPago();
				}
			}
		}
		return valor;
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
	
	public List<Moeda> getListaMoeda() {
		try {
			return moedaService.buscarPorUsuario(getUsuarioLogado());
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<SelectItem> getListaStatusLancamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		listaSelectItem.add(new SelectItem(StatusLancamento.ATIVO, "Ativo"));
		listaSelectItem.add(new SelectItem(StatusLancamento.SUSPENSO, "Suspenso"));
		listaSelectItem.add(new SelectItem(StatusLancamento.ENCERRADO, "Encerrado"));
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaTipoLancamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		listaSelectItem.add(new SelectItem(TipoLancamento.RECEITA, "Receita"));
		listaSelectItem.add(new SelectItem(TipoLancamento.DESPESA, "Despesa"));
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaTipoLancamentoPeriodico() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		listaSelectItem.add(new SelectItem(TipoLancamentoPeriodico.FIXO, "Fixo"));
		listaSelectItem.add(new SelectItem(TipoLancamentoPeriodico.PARCELADO, "Parcelado"));
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaPeriodoLancamento() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (PeriodoLancamento periodo : PeriodoLancamento.values()) {
			listaSelectItem.add(new SelectItem(periodo, periodo.toString()));
		}
		return listaSelectItem;
	}

	public ILancamentoPeriodico getService() {
		return service;
	}

	public void setService(ILancamentoPeriodico service) {
		this.service = service;
	}

	public Conta getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(Conta contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}

	public StatusLancamento getStatusLancamento() {
		return statusLancamento;
	}

	public void setStatusLancamento(StatusLancamento statusLancamento) {
		this.statusLancamento = statusLancamento;
	}

	public void setContaService(IConta contaService) {
		this.contaService = contaService;
	}

	public void setMoedaService(IMoeda moedaService) {
		this.moedaService = moedaService;
	}

	public boolean isParcelamento() {
		return parcelamento;
	}

	public void setParcelamento(boolean parcelamento) {
		this.parcelamento = parcelamento;
	}

	public TipoCategoria getTipoCategoriaSelecionada() {
		return tipoCategoriaSelecionada;
	}

	public void setTipoCategoriaSelecionada(TipoCategoria tipoCategoriaSelecionada) {
		this.tipoCategoriaSelecionada = tipoCategoriaSelecionada;
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

	public boolean isSelecionarTodosLancamentos() {
		return selecionarTodosLancamentos;
	}

	public void setSelecionarTodosLancamentos(boolean selecionarTodosLancamentos) {
		this.selecionarTodosLancamentos = selecionarTodosLancamentos;
	}

	public PagamentoPeriodo getPagamentoPeriodo() {
		return pagamentoPeriodo;
	}

	public void setPagamentoPeriodo(PagamentoPeriodo pagamentoPeriodo) {
		this.pagamentoPeriodo = pagamentoPeriodo;
	}

	public TipoLancamentoPeriodico getTipoLancamentoPeriodico() {
		return tipoLancamentoPeriodico;
	}

	public void setTipoLancamentoPeriodico(
			TipoLancamentoPeriodico tipoLancamentoPeriodico) {
		this.tipoLancamentoPeriodico = tipoLancamentoPeriodico;
	}

	public Moeda getMoedaPadrao() {
		return moedaPadrao;
	}

	public void setMoedaPadrao(Moeda moedaPadrao) {
		this.moedaPadrao = moedaPadrao;
	}

	public boolean isGerarLancamento() {
		return gerarLancamento;
	}

	public void setGerarLancamento(boolean gerarLancamento) {
		this.gerarLancamento = gerarLancamento;
	}
}