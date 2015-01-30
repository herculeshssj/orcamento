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

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.CategoriaDocumento;
import br.com.hslife.orcamento.entity.Documento;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.Container;
import br.com.hslife.orcamento.enumeration.TipoUsuario;
import br.com.hslife.orcamento.exception.BusinessException;
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
	
	private List<SelectItem> listaCategoriaDocumento;
		
	private CategoriaDocumento findCategoria;
	private Usuario findUsuario;
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
	public String startUp() {
		loadCombos();
		return super.startUp();
	}

	private void loadCombos() {
		listaCategoriaDocumento = new ArrayList<SelectItem>();
		try {
			if (operation.equals("edit")) {
				for (CategoriaDocumento cd : categoriaDocumentoService.buscarPorUsuario(getUsuarioLogado())) {
					listaCategoriaDocumento.add(new SelectItem(cd, cd.getDescricao()));
				}
			} else {
				for (CategoriaDocumento cd : categoriaDocumentoService.buscarPorUsuario(getUsuarioLogado())) {
					listaCategoriaDocumento.add(new SelectItem(cd, cd.getDescricao()));
				}
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public void find() {		
		try {
			if (findCategoria == null) {
				listEntity = getService().buscarPorNomeEUsuario(nomeDocumento, getUsuarioLogado());
			} else {
				listEntity = getService().buscarPorNomeECategoriaDocumentoPorUsuario(nomeDocumento, findCategoria, getUsuarioLogado());
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String save() {
		if (entity.getArquivo() == null || entity.getArquivo().getDados() == null || entity.getArquivo().getDados().length == 0) {
			warnMessage("Anexe um arquivo!");
			return "";
		}
		return super.save();
	}
	
	public void atualizaListaCategorias() {
		listaCategoriaDocumento = new ArrayList<SelectItem>();
		try {
			if (getUsuarioLogado().getTipoUsuario().equals(TipoUsuario.ROLE_ADMIN)) {
				if (operation.equals("edit") || operation.equals("create")) {
					for (CategoriaDocumento cd : categoriaDocumentoService.buscarPorUsuario(getUsuarioLogado())) {
						listaCategoriaDocumento.add(new SelectItem(cd, cd.getDescricao()));
					}
				} else {
					for (CategoriaDocumento cd : categoriaDocumentoService.buscarPorUsuario(findUsuario)) {
						listaCategoriaDocumento.add(new SelectItem(cd, cd.getDescricao()));
					}
				}
			} else {
				for (CategoriaDocumento cd : categoriaDocumentoService.buscarPorUsuario(getUsuarioLogado())) {
					listaCategoriaDocumento.add(new SelectItem(cd, cd.getDescricao()));
				}				
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
			entity.getArquivo().setContainer(Container.DOCUMENTOS);
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
	
	/* Métodos Getters e Setters */
	
	public List<SelectItem> getListaCategoriaDocumento() {
		return listaCategoriaDocumento;
	}

	public void setListaCategoriaDocumento(List<SelectItem> listaCategoriaDocumento) {
		this.listaCategoriaDocumento = listaCategoriaDocumento;
	}

	public CategoriaDocumento getFindCategoria() {
		return findCategoria;
	}

	public void setFindCategoria(CategoriaDocumento findCategoria) {
		this.findCategoria = findCategoria;
	}

	public Usuario getFindUsuario() {
		return findUsuario;
	}

	public void setFindUsuario(Usuario findUsuario) {
		this.findUsuario = findUsuario;
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
}