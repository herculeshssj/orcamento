/***

	Copyright (c) 2012 - 2021 Hércules S. S. José

	Este arquivo é parte do programa Orçamento Doméstico.


	Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

	modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

	publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

	Licença.


	Este programa é distribuído na esperança que possa ser útil, mas SEM

	NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer

	MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor

	GNU em português para maiores detalhes.


	Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob

	o nome de "LICENSE" junto com este programa, se não, acesse o site do

	projeto no endereco https://github.com/herculeshssj/orcamento ou escreva

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth

	Floor, Boston, MA  02110-1301, USA.


	Para mais informações sobre o programa Orçamento Doméstico e seu autor

	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

	para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.CategoriaDocumento;
import br.com.hslife.orcamento.entity.Documento;
import br.com.hslife.orcamento.enumeration.Container;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.ICategoriaDocumento;
import br.com.hslife.orcamento.facade.IDocumento;

@Component("documentoMB")
@Scope("session")
public class DocumentoController extends AbstractCRUDController<Documento>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5008455377685602947L;

	@Autowired
	private IDocumento service;
	
	@Autowired
	private ICategoriaDocumento categoriaDocumentoService;
		
	private CategoriaDocumento categoriaSelecionada;
	private String nomeDocumento;
	
	public DocumentoController() {
		super(new Documento());
		
		moduleTitle = "Documentos";
	}

	@Override
	protected void initializeEntity() {
		entity = new Documento();
		listEntity = new ArrayList<Documento>();
	}

	@Override
	public void find() {		
		try {
			listEntity = getService().buscarPorNomeECategoriaDocumentoPorUsuario(nomeDocumento, categoriaSelecionada, getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String create() {
		entity.setCategoriaDocumento(categoriaSelecionada);
		return super.create();
	}
	
	@Override
	public String save() {
		if (entity.getIdArquivo() == null) {
			warnMessage("Anexe um arquivo!");
			return "";
		}
		return super.save();
	}
	
	public void carregarArquivo(FileUploadEvent event) {
		if (event.getFile() != null) {
			Arquivo arquivo = new Arquivo();
			arquivo.setDados(event.getFile().getContents());
			arquivo.setNomeArquivo(event.getFile().getFileName().replace(" ", "."));
			arquivo.setContentType(event.getFile().getContentType());
			arquivo.setTamanho(event.getFile().getSize());
			arquivo.setContainer(Container.DOCUMENTOS);
			arquivo.setUsuario(getUsuarioLogado());
			arquivo.setAttribute("arquivo");
			entity.setIdArquivo(getArquivoComponent().carregarArquivo(arquivo));
		} 
	}
	
	public void baixarArquivo() {
		Arquivo arquivo = getArquivoComponent().buscarArquivo(entity.getIdArquivo()); 
		if (arquivo == null) {
			warnMessage("Nenhum arquivo adicionado!");
		} else {
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			try {			
				response.setContentType(arquivo.getContentType());
				response.setHeader("Content-Disposition","attachment; filename=" + arquivo.getNomeArquivo());
				response.setContentLength(arquivo.getDados().length);
				ServletOutputStream output = response.getOutputStream();
				output.write(arquivo.getDados(), 0, arquivo.getDados().length);
				FacesContext.getCurrentInstance().responseComplete();
			} catch (Exception e) {
				errorMessage(e.getMessage());
			}
		}
	}
	
	public List<CategoriaDocumento> getListaCategoriaDocumento() {
		try {
			return categoriaDocumentoService.buscarPorDescricaoEUsuario("", getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<CategoriaDocumento>();
	}

	public IDocumento getService() {
		return service;
	}

	public void setService(IDocumento service) {
		this.service = service;
	}

	public void setCategoriaDocumentoService(
			ICategoriaDocumento categoriaDocumentoService) {
		this.categoriaDocumentoService = categoriaDocumentoService;
	}

	public String getNomeDocumento() {
		return nomeDocumento;
	}

	public void setNomeDocumento(String nomeDocumento) {
		this.nomeDocumento = nomeDocumento;
	}

	public CategoriaDocumento getCategoriaSelecionada() {
		return categoriaSelecionada;
	}

	public void setCategoriaSelecionada(CategoriaDocumento categoriaSelecionada) {
		this.categoriaSelecionada = categoriaSelecionada;
	}
}
