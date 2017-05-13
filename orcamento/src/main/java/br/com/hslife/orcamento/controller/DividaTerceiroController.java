/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou 

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como 

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da 

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
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.component.AutosalvamentoComponent;
import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.Autosalvamento;
import br.com.hslife.orcamento.entity.DividaTerceiro;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.ModeloDocumento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.PagamentoDividaTerceiro;
import br.com.hslife.orcamento.enumeration.Container;
import br.com.hslife.orcamento.enumeration.StatusDivida;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoTermoDividaTerceiro;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IDividaTerceiro;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IModeloDocumento;
import br.com.hslife.orcamento.facade.IMoeda;

@Component("dividaTerceiroMB")
@Scope("session")
public class DividaTerceiroController extends AbstractCRUDController<DividaTerceiro> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5749287637286980625L;
	
	@Autowired
	private IDividaTerceiro service;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IModeloDocumento modeloDocumentoService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private AutosalvamentoComponent autoSalvamentoComponent;
	
	private TipoCategoria tipoCategoria;
	private StatusDivida statusDivida;
	private PagamentoDividaTerceiro pagamentoDivida;
	private VisualizacaoDocumento visualizacao;
	private String conteudoDocumento;
	private String novaJustificativa;
	private Moeda moedaSelecionada;
	private boolean dividaQuitada = false;
	private String statusSalvamento;
	
	private TipoTermoDividaTerceiro tipoTermoDivida;
	private Arquivo arquivoAnexado;
	private String conteudoEditorTexto;
	
	private enum VisualizacaoDocumento {
		MODELO_DOCUMENTO, TERMO_DIVIDA, TERMO_QUITACAO, COMPROVANTE_PAGAMENTO, MODELO_COMPROVANTE;
	}
	
	public DividaTerceiroController() {
		super(new DividaTerceiro());
		moduleTitle = "Dívida de Favorecidos";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new DividaTerceiro();
		listEntity = new ArrayList<DividaTerceiro>();	
		novaJustificativa = null;
		dividaQuitada = false;
		moedaSelecionada = null;
		pagamentoDivida = null;
		statusSalvamento = null;
	}

	@Override
	public void find() {
		try {
			listEntity = getService().buscarFavorecidoOuTipoCategoriaOuStatusDividaPorUsuario(null, tipoCategoria, statusDivida, getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String save() {
		entity.setUsuario(getUsuarioLogado());		
		return super.save();
	}
	
	public void autoSave() {
		String conteudoTexto = "";
		
		switch (operation) {
			case "vigorar" : conteudoTexto = entity.getTermoDivida(); break; 
		}
		
		try {
			getAutoSalvamentoComponent().salvar(entity.getId(), entity.getClass().getSimpleName(), "termoDivida", conteudoTexto);
			
			Autosalvamento auto = getAutoSalvamentoComponent().buscarUltimoSalvamento(entity.getId(), entity.getClass().getSimpleName(), "termoDivida");
			
			statusSalvamento = "Conteúdo salvo automaticamente em " + auto.getDataCriacao().toString();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String vigorarDividaTerceiroView() {
		try {
			Autosalvamento auto = getAutoSalvamentoComponent().buscarUltimoSalvamento(entity.getId(), entity.getClass().getSimpleName(), "termoDivida");
			if (auto != null)
				entity.setTermoDivida(auto.getConteudoTexto());
			actionTitle = " - Vigorar dívida";
			operation = "vigorar";
			return "/pages/DividaTerceiro/vigorarDividaTerceiro";
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String vigorarDividaTerceiro() {
		if (tipoTermoDivida.equals(TipoTermoDividaTerceiro.MODELO_DOCUMENTO)) {
			if (entity.getModeloDocumento() == null) {
				warnMessage("Selecione um modelo de documento!");
				return "";
			} else {
				entity.setTermoDivida(entity.getModeloDocumento().getConteudo());
			}
		} 
		
		if (tipoTermoDivida.equals(TipoTermoDividaTerceiro.ARQUIVO)) {
			if (entity.getArquivoTermoDivida() == null) {
				warnMessage("Selecione um arquivo");
				return "";
			} else {
				entity.setTermoDivida("");
			}
		} 
		
		if (tipoTermoDivida.equals(TipoTermoDividaTerceiro.EDITOR_TEXTO)) {
			if (entity.getTermoDivida() == null || entity.getTermoDivida().isEmpty()) {
				warnMessage("Entre com o texto do termo!");
				return "";
			} else {
				entity.setArquivoTermoDivida(null);
			}
		}
		
		try {
			// Vigora o termo
			getService().vigorarDividaTerceiro(entity);
			
			// Exclui os autosalvamentos existentes
			getAutoSalvamentoComponent().excluirTodosSalvamentos(entity.getId(), entity.getClass().getSimpleName(), "termoDivida");
			
			infoMessage("Registro salvo com sucesso. Termo/contrato da dívida entrou em vigor.");
			return super.list();			
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String renegociarDividaView() {
		actionTitle = " - Renegociar dívida";
		entity.setDataNegociacao(new Date());
		return "/pages/DividaTerceiro/renegociarDivida";
	}
	
	public String renegociarDivida() {
		try {
			getService().renegociarDividaTerceiro(entity, novaJustificativa);
			infoMessage("Registro salvo com sucesso. Dívida renegociada.");
			return super.list();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";	
	}
	
	public String encerrarDividaView() {
		actionTitle = " - Encerrar dívida";
		return "/pages/DividaTerceiro/encerrarDivida";
	}
	
	public String encerrarDivida() {
		try {
			getService().encerrarDividaTerceiro(entity, novaJustificativa);
			infoMessage("Registro salvo com sucesso. Dívida encerrada.");
			return super.list();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";	
	}
	
	public String registrarPagamentoView() {
		actionTitle = " - Registrar pagamento";
		moedaSelecionada = entity.getMoeda();
		pagamentoDivida = new PagamentoDividaTerceiro();
		dividaQuitada = false;
		return "/pages/DividaTerceiro/registrarPagamento";
	}
	
	public String registrarPagamento() {
		if (entity.getTotalPago() + pagamentoDivida.getValorPagoConvertido() >= entity.getValorDivida() && entity.getModeloDocumento() == null) {
			warnMessage("Dívida prestes a ser quitada. Selecione um termo de quitação.");
			dividaQuitada = true;
			return "";
		}
		
		try {
			getService().registrarPagamentoDivida(entity, pagamentoDivida);
			if (dividaQuitada) {
				infoMessage("Pagamento registrado com sucesso. Dívida quitada.");
			} else {
				infoMessage("Pagamento registrado com sucesso.");
			}
			return super.list();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";	
	}
	
	public String detalheDividaTerceiro() {
		actionTitle = " - Detalhes";
		return "/pages/DividaTerceiro/detalheDividaTerceiro";
	}
	
	public String visualizarModeloDocumento() {
		if (entity.getModeloDocumento() == null) {
			warnMessage("Selecione um modelo de documento");
			return "";
		}
		visualizacao = VisualizacaoDocumento.MODELO_DOCUMENTO;
		return this.visualizarDocumento();
	}
	
	public String visualizarModeloComprovante() {
		if (pagamentoDivida.getModeloDocumento() == null) {
			warnMessage("Selecione um modelo de documento");
			return "";
		}
		visualizacao = VisualizacaoDocumento.MODELO_COMPROVANTE;
		return this.visualizarDocumento();
	}
	
	public String visualizarTermoDivida() {
		visualizacao = VisualizacaoDocumento.TERMO_DIVIDA;
		return this.visualizarDocumento();
	}
	
	public String visualizarTermoQuitacao() {
		visualizacao = VisualizacaoDocumento.TERMO_QUITACAO;
		return this.visualizarDocumento();
	}
	
	public String visualizarComprovante() {
		visualizacao = VisualizacaoDocumento.COMPROVANTE_PAGAMENTO;
		return this.visualizarDocumento();
	}
	
	private String visualizarDocumento() {
		switch (visualizacao) {
			case COMPROVANTE_PAGAMENTO :
				conteudoDocumento = pagamentoDivida.getComprovantePagamento();
				break;
			case MODELO_COMPROVANTE:
				conteudoDocumento = pagamentoDivida.getModeloDocumento().getConteudo();
				break;
			case MODELO_DOCUMENTO :
				conteudoDocumento = entity.getModeloDocumento().getConteudo();
				break;
			case TERMO_DIVIDA :
				conteudoDocumento = entity.getTermoDivida();
				break;
			case TERMO_QUITACAO :
				conteudoDocumento = entity.getTermoQuitacao();
				break;
		}
		return "/pages/DividaTerceiro/visualizarDocumento";
	}
	
	public List<DividaTerceiro> getDividaTerceiroAtrasado() {
		try {
			// Busca todas as dívidas que não receberam pagamento a mais de 30 dias
			return getService().buscarDividaTerceiroAtrasado(30);
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public double getCreditoAReceber() {
		try {
			List<DividaTerceiro> dividas = getService().buscarFavorecidoOuTipoCategoriaOuStatusDividaPorUsuario(null, TipoCategoria.CREDITO, StatusDivida.VIGENTE, getUsuarioLogado());
			double valor = 0.0;
			for (DividaTerceiro divida : dividas) {
				if (divida.getMoeda().isPadrao())
					valor += divida.getTotalAPagar();
				else
					valor += divida.getTotalAPagar() * divida.getMoeda().getValorConversao();
			}
			return valor;
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return 0.0;
	}
	
	public double getDebitoAPagar() {
		try {
			List<DividaTerceiro> dividas = getService().buscarFavorecidoOuTipoCategoriaOuStatusDividaPorUsuario(null, TipoCategoria.DEBITO, StatusDivida.VIGENTE, getUsuarioLogado());
			double valor = 0.0;
			for (DividaTerceiro divida : dividas) {
				if (divida.getMoeda().isPadrao())
					valor += divida.getTotalAPagar();
				else
					valor += divida.getTotalAPagar() * divida.getMoeda().getValorConversao();
			}
			return valor;
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return 0.0;
	}
	
	public void carregarArquivo(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (operation.equals("vigorar")) {
				if (entity.getArquivoTermoDivida() == null) entity.setArquivoTermoDivida(new Arquivo());
				entity.getArquivoTermoDivida().setDados(event.getFile().getContents());
				entity.getArquivoTermoDivida().setNomeArquivo(event.getFile().getFileName().replace(" ", "."));
				entity.getArquivoTermoDivida().setContentType(event.getFile().getContentType());
				entity.getArquivoTermoDivida().setTamanho(event.getFile().getSize());
				entity.getArquivoTermoDivida().setContainer(Container.DIVIDATERCEIROS);
				entity.getArquivoTermoDivida().setUsuario(getUsuarioLogado());
				entity.getArquivoTermoDivida().setAttribute("arquivoTermoDivida");
			}	
		} 
	}
	
	public void visualizarTermoDividaAnexado() {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		try {			
			response.setContentType(entity.getArquivoTermoDivida().getContentType());
			response.setHeader("Content-Disposition","attachment; filename=" + entity.getArquivoTermoDivida().getNomeArquivo());
			response.setContentLength(entity.getArquivoTermoDivida().getDados().length);
			ServletOutputStream output = response.getOutputStream();
			output.write(entity.getArquivoTermoDivida().getDados(), 0, entity.getArquivoTermoDivida().getDados().length);
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			errorMessage(e.getMessage());
		}
	}
	
	public List<Favorecido> getListaFavorecido() {
		try {
			return favorecidoService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, be.getMessage(), null));
		}
		return new ArrayList<>();
	}
	
	public List<ModeloDocumento> getListaModeloDocumento() {
		try {
			return modeloDocumentoService.buscarDescricaoOuAtivoPorUsuario(null, true, getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<Moeda> getListaMoeda() {
		try {
			return moedaService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public Moeda getMoedaPadrao() {
		try {
			return moedaService.buscarPadraoPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return null;
	}

	public IDividaTerceiro getService() {
		return service;
	}

	public void setService(IDividaTerceiro service) {
		this.service = service;
	}

	public AutosalvamentoComponent getAutoSalvamentoComponent() {
		return autoSalvamentoComponent;
	}

	public TipoCategoria getTipoCategoria() {
		return tipoCategoria;
	}

	public void setTipoCategoria(TipoCategoria tipoCategoria) {
		this.tipoCategoria = tipoCategoria;
	}

	public StatusDivida getStatusDivida() {
		return statusDivida;
	}

	public void setStatusDivida(StatusDivida statusDivida) {
		this.statusDivida = statusDivida;
	}

	public String getConteudoDocumento() {
		return conteudoDocumento;
	}

	public void setConteudoDocumento(String conteudoDocumento) {
		this.conteudoDocumento = conteudoDocumento;
	}

	public String getNovaJustificativa() {
		return novaJustificativa;
	}

	public void setNovaJustificativa(String novaJustificativa) {
		this.novaJustificativa = novaJustificativa;
	}

	public PagamentoDividaTerceiro getPagamentoDivida() {
		return pagamentoDivida;
	}

	public void setPagamentoDivida(PagamentoDividaTerceiro pagamentoDivida) {
		this.pagamentoDivida = pagamentoDivida;
	}

	public Moeda getMoedaSelecionada() {
		return moedaSelecionada;
	}

	public void setMoedaSelecionada(Moeda moedaSelecionada) {
		this.moedaSelecionada = moedaSelecionada;
	}

	public boolean isDividaQuitada() {
		return dividaQuitada;
	}

	public void setDividaQuitada(boolean dividaQuitada) {
		this.dividaQuitada = dividaQuitada;
	}

	public TipoTermoDividaTerceiro getTipoTermoDivida() {
		return tipoTermoDivida;
	}

	public void setTipoTermoDivida(TipoTermoDividaTerceiro tipoTermoDivida) {
		this.tipoTermoDivida = tipoTermoDivida;
	}

	public Arquivo getArquivoAnexado() {
		return arquivoAnexado;
	}

	public void setArquivoAnexado(Arquivo arquivoAnexado) {
		this.arquivoAnexado = arquivoAnexado;
	}

	public String getConteudoEditorTexto() {
		return conteudoEditorTexto;
	}

	public void setConteudoEditorTexto(String conteudoEditorTexto) {
		this.conteudoEditorTexto = conteudoEditorTexto;
	}

	public String getStatusSalvamento() {
		return statusSalvamento;
	}

	public void setStatusSalvamento(String statusSalvamento) {
		this.statusSalvamento = statusSalvamento;
	}
}