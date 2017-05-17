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
	
	private String conteudoDocumento;
	private String novaJustificativa;
	private Moeda moedaSelecionada;
	
	private String statusSalvamento;
	private ModeloDocumento modeloDocumento;
	private TipoTermoDividaTerceiro tipoTermoDivida;
	private Arquivo arquivoAnexado;
	private String conteudoTexto;
	
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
		moedaSelecionada = null;
		pagamentoDivida = null;
		statusSalvamento = null;
		conteudoDocumento = null;
		conteudoTexto = null;
		tipoTermoDivida = null;
		arquivoAnexado = null;
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
		String atributo = "";		
		switch (operation) {
			case "vigorar" : atributo = "termoDivida"; break; 
			case "pagamento" : atributo = "comprovantePagamento"; break;
			case "quitar" : atributo = "termoQuitacao"; break;
		}
		
		try {
			getAutoSalvamentoComponent().salvar(entity.getId(), entity.getClass().getSimpleName(), atributo, this.conteudoTexto);
			
			Autosalvamento auto = getAutoSalvamentoComponent().buscarUltimoSalvamento(entity.getId(), entity.getClass().getSimpleName(), atributo);
			
			statusSalvamento = "Conteúdo salvo automaticamente em " + auto.getDataCriacao().toString();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String vigorarDividaTerceiroView() {
		try {
			Autosalvamento auto = getAutoSalvamentoComponent().buscarUltimoSalvamento(entity.getId(), entity.getClass().getSimpleName(), "termoDivida");
			if (auto != null)
				setConteudoTexto(auto.getConteudoTexto());
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
			if (getModeloDocumento() == null) {
				warnMessage("Selecione um modelo de documento!");
				return "";
			} else {
				entity.setArquivoTermoDivida(null);
				entity.setTermoDivida(getModeloDocumento().getConteudo());
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
			if (this.conteudoTexto.isEmpty()) {
				warnMessage("Entre com o texto do termo!");
				return "";
			} else {
				entity.setArquivoTermoDivida(null);
				entity.setTermoDivida(this.conteudoTexto);
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
		try {
			Autosalvamento auto = getAutoSalvamentoComponent().buscarUltimoSalvamento(entity.getId(), entity.getClass().getSimpleName(), "termoDivida");
			if (auto != null)
				setConteudoTexto(auto.getConteudoTexto());
			actionTitle = " - Renegociar dívida";
			operation = "vigorar";
			entity.setDataNegociacao(new Date());
			return "/pages/DividaTerceiro/renegociarDivida";
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String renegociarDivida() {
		if (tipoTermoDivida.equals(TipoTermoDividaTerceiro.MODELO_DOCUMENTO)) {
			if (getModeloDocumento() == null) {
				warnMessage("Selecione um modelo de documento!");
				return "";
			} else {
				entity.setArquivoTermoDivida(null);
				entity.setTermoDivida(getModeloDocumento().getConteudo());
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
			if (this.conteudoTexto.isEmpty()) {
				warnMessage("Entre com o texto do termo!");
				return "";
			} else {
				entity.setArquivoTermoDivida(null);
				entity.setTermoDivida(this.conteudoTexto);
			}
		}
		
		try {
			// renegocia a dívida
			getService().renegociarDividaTerceiro(entity, novaJustificativa);
			
			// Exclui os autosalvamentos existentes
			getAutoSalvamentoComponent().excluirTodosSalvamentos(entity.getId(), entity.getClass().getSimpleName(), "termoDivida");
			
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
		try {
			Autosalvamento auto = getAutoSalvamentoComponent().buscarUltimoSalvamento(entity.getId(), entity.getClass().getSimpleName(), "comprovantePagamento");
			if (auto != null)
				setConteudoTexto(auto.getConteudoTexto());
			actionTitle = " - Registrar pagamento";
			operation = "pagamento";
			moedaSelecionada = entity.getMoeda();
			pagamentoDivida = new PagamentoDividaTerceiro();
			return "/pages/DividaTerceiro/registrarPagamento";
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String registrarPagamento() {
		if (tipoTermoDivida.equals(TipoTermoDividaTerceiro.MODELO_DOCUMENTO)) {
			if (getModeloDocumento() == null) {
				warnMessage("Selecione um modelo de documento!");
				return "";
			} else {
				pagamentoDivida.setArquivoComprovante(null);
				pagamentoDivida.setComprovantePagamento(getModeloDocumento().getConteudo());
			}
		} 
		
		if (tipoTermoDivida.equals(TipoTermoDividaTerceiro.ARQUIVO)) {
			if (pagamentoDivida.getArquivoComprovante() == null) {
				warnMessage("Selecione um arquivo");
				return "";
			} else {
				pagamentoDivida.setComprovantePagamento("");
			}
		} 
		
		if (tipoTermoDivida.equals(TipoTermoDividaTerceiro.EDITOR_TEXTO)) {
			if (this.conteudoTexto.isEmpty()) {
				warnMessage("Entre com o texto do termo!");
				return "";
			} else {
				pagamentoDivida.setArquivoComprovante(null);
				pagamentoDivida.setComprovantePagamento(this.conteudoTexto);
			}
		}
		
		try {
			getService().registrarPagamentoDivida(entity, pagamentoDivida);
			infoMessage("Pagamento registrado com sucesso.");
			
			// Exclui os autosalvamentos existentes
			getAutoSalvamentoComponent().excluirTodosSalvamentos(entity.getId(), entity.getClass().getSimpleName(), "comprovantePagamento");
			
			// Verifica se a dívida pode ser quitada
			if (entity.getTotalPago() + pagamentoDivida.getValorPagoConvertido() >= entity.getValorDivida()) {
				idEntity = entity.getId();
				tipoTermoDivida = null;
				conteudoTexto = null;
				statusSalvamento = null;
				warnMessage("Dívida prestes a ser quitada.");
				return this.quitarDividaTerceiroView();
			}
			return super.list();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";	
	}
	
	private String quitarDividaTerceiroView() {
		try {
			entity = getService().buscarPorID(idEntity);
			
			Autosalvamento auto = getAutoSalvamentoComponent().buscarUltimoSalvamento(entity.getId(), entity.getClass().getSimpleName(), "termoQuitacao");
			if (auto != null)
				setConteudoTexto(auto.getConteudoTexto());
			actionTitle = " - Quitar dívida";
			operation = "quitar";
			return "/pages/DividaTerceiro/quitarDividaTerceiro";
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String quitarDividaTerceiro() {
		if (tipoTermoDivida.equals(TipoTermoDividaTerceiro.MODELO_DOCUMENTO)) {
			if (getModeloDocumento() == null) {
				warnMessage("Selecione um modelo de documento!");
				return "";
			} else {
				entity.setArquivoTermoQuitacao(null);
				entity.setTermoQuitacao(getModeloDocumento().getConteudo());
			}
		} 
		
		if (tipoTermoDivida.equals(TipoTermoDividaTerceiro.ARQUIVO)) {
			if (entity.getArquivoTermoQuitacao() == null) {
				warnMessage("Selecione um arquivo");
				return "";
			} else {
				entity.setTermoQuitacao("");
			}
		} 
		
		if (tipoTermoDivida.equals(TipoTermoDividaTerceiro.EDITOR_TEXTO)) {
			if (this.conteudoTexto.isEmpty()) {
				warnMessage("Entre com o texto do termo!");
				return "";
			} else {
				entity.setArquivoTermoQuitacao(null);
				entity.setTermoQuitacao(this.conteudoTexto);
			}
		}
		
		try {
			// Quita a dívida
			getService().quitarDividaTerceiro(entity);
			
			// Exclui os autosalvamentos existentes
			getAutoSalvamentoComponent().excluirTodosSalvamentos(entity.getId(), entity.getClass().getSimpleName(), "termoQuitacao");
			
			infoMessage("Registro salvo com sucesso. Dívida quitada.");
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
	
	public String visualizarConteudoTexto() {
		conteudoDocumento = getConteudoTexto();
		return "/pages/DividaTerceiro/visualizarDocumento";
	}
	
	public String visualizarModeloDocumento() {
		conteudoDocumento = getModeloDocumento().getConteudo();
		return "/pages/DividaTerceiro/visualizarDocumento";
	}
	
	public String visualizarTermoDivida() {
		conteudoDocumento = entity.getTermoDivida();
		return "/pages/DividaTerceiro/visualizarDocumento";
	}
	
	public String visualizarComprovante() {
		conteudoDocumento = pagamentoDivida.getComprovantePagamento();
		return "/pages/DividaTerceiro/visualizarDocumento";
	}
	
	public String visualizarTermoQuitacao() {
		conteudoDocumento = entity.getTermoQuitacao();
		return "/pages/DividaTerceiro/visualizarDocumento";
	}
	
	public void carregarArquivo(FileUploadEvent event) {
		if (event.getFile() != null) {
			arquivoAnexado = new Arquivo();
			arquivoAnexado.setDados(event.getFile().getContents());
			arquivoAnexado.setNomeArquivo(event.getFile().getFileName().replace(" ", "."));
			arquivoAnexado.setContentType(event.getFile().getContentType());
			arquivoAnexado.setTamanho(event.getFile().getSize());
			arquivoAnexado.setUsuario(getUsuarioLogado());
			
			switch (operation) {
				case "vigorar" : 
					arquivoAnexado.setContainer(Container.DIVIDATERCEIROS);
					arquivoAnexado.setAttribute("arquivoTermoDivida");
					entity.setArquivoTermoDivida(arquivoAnexado);
					break;
				case "pagamento" :
					arquivoAnexado.setContainer(Container.PAGAMENTODIVIDATERCEIRO);
					arquivoAnexado.setAttribute("arquivoComprovantePagamento");
					pagamentoDivida.setArquivoComprovante(arquivoAnexado);
					break;
				case "quitar" : 
					arquivoAnexado.setContainer(Container.DIVIDATERCEIROS);
					arquivoAnexado.setAttribute("arquivoTermoQuitacao");
					entity.setArquivoTermoQuitacao(arquivoAnexado);
					break;
			}
		}
		
	}
	
	public void visualizarTermoDividaAnexado() {
		this.baixarArquivo(VisualizacaoDocumento.TERMO_DIVIDA);
	}
	
	public void visualizarComprovanteAnexado() {
		this.baixarArquivo(VisualizacaoDocumento.COMPROVANTE_PAGAMENTO);
	}
	
	public void visualizarTermoQuitacaoAnexado() {
		this.baixarArquivo(VisualizacaoDocumento.TERMO_QUITACAO);
	}
	
	private void baixarArquivo(VisualizacaoDocumento visualizacao) {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		Arquivo arquivoAnexado = null;
		switch (visualizacao) {
			case TERMO_DIVIDA : arquivoAnexado = entity.getArquivoTermoDivida(); break;
			case COMPROVANTE_PAGAMENTO : arquivoAnexado = pagamentoDivida.getArquivoComprovante(); break;
			case TERMO_QUITACAO : arquivoAnexado = entity.getArquivoTermoQuitacao(); break;
			default : arquivoAnexado = new Arquivo();
		}
		try {			
			response.setContentType(arquivoAnexado.getContentType());
			response.setHeader("Content-Disposition","attachment; filename=" + arquivoAnexado.getNomeArquivo());
			response.setContentLength(arquivoAnexado.getDados().length);
			ServletOutputStream output = response.getOutputStream();
			output.write(arquivoAnexado.getDados(), 0, arquivoAnexado.getDados().length);
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

	public String getStatusSalvamento() {
		return statusSalvamento;
	}

	public void setStatusSalvamento(String statusSalvamento) {
		this.statusSalvamento = statusSalvamento;
	}

	public ModeloDocumento getModeloDocumento() {
		return modeloDocumento;
	}

	public void setModeloDocumento(ModeloDocumento modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
	}

	public String getConteudoTexto() {
		return conteudoTexto;
	}

	public void setConteudoTexto(String conteudoTexto) {
		this.conteudoTexto = conteudoTexto;
	}
}