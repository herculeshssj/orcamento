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
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.CategoriaDocumento;
import br.com.hslife.orcamento.entity.Documento;
import br.com.hslife.orcamento.entity.HistoricoSaude;
import br.com.hslife.orcamento.entity.Saude;
import br.com.hslife.orcamento.entity.TratamentoSaude;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.ICategoriaDocumento;
import br.com.hslife.orcamento.facade.IDocumento;
import br.com.hslife.orcamento.facade.ISaude;

@Component
@Scope("session")
public class SaudeController extends AbstractCRUDController<Saude> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5076023665451990660L;
	
	@Autowired
	private ISaude service; 
	
	@Autowired
	private ICategoriaDocumento categoriaDocumentoService;
	
	@Autowired
	private IDocumento documentoService;
	
	private HistoricoSaude historicoSaude;
	private TratamentoSaude tratamentoSaude;
	private Documento documento;
	
	public SaudeController() {
		super(new Saude());
		moduleTitle = "Saúde";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Saude();
		listEntity = new ArrayList<Saude>();
		historicoSaude = new HistoricoSaude();
		tratamentoSaude = new TratamentoSaude();
		documento = null;
	}
	
	@Override
	public String startUp() {
		initializeEntity();
		find();
		return super.startUp();
	}
	
	@Override
	public void find() {
		listEntity = getService().buscarTodosAtivosPorUsuario(getUsuarioLogado());
	}
	
	@Override
	public String save() {
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}
	
	public void novoHistoricoETratamento() {
		historicoSaude = new HistoricoSaude();
		tratamentoSaude = new TratamentoSaude();				
	}
	
	public void adicionarHistorico() {
		try {
			historicoSaude.setSaude(entity);
			getService().salvarHistoricoSaude(historicoSaude);
			entity = getService().buscarPorID(entity.getId());
			historicoSaude = new HistoricoSaude();
			infoMessage("Histórico adicionado com sucesso!");
		} catch (BusinessException | ValidationException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void editarHistorico() {
		// Método usado unicamente para dar refresh na tela
	}
	
	public void excluirHistorico() {
		try {
			getService().excluirHistoricoSaude(historicoSaude);
			entity = getService().buscarPorID(entity.getId());
			historicoSaude = new HistoricoSaude();
			infoMessage("Histórico excluído com sucesso!");
		} catch (BusinessException | ValidationException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void adicionarTratamento() {
		try {
			tratamentoSaude.setSaude(entity);
			getService().salvarTratamentoSaude(tratamentoSaude);
			entity = getService().buscarPorID(entity.getId());
			tratamentoSaude = new TratamentoSaude();
			infoMessage("Tratamento adicionado com sucesso!");
		} catch (BusinessException | ValidationException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void editarTratamento() {
		// Método usado unicamente para dar refresh na tela
	}
	
	public void excluirTratamento() {
		try {
			getService().excluirTratamentoSaude(tratamentoSaude);
			entity = getService().buscarPorID(entity.getId());
			tratamentoSaude = new TratamentoSaude();
			infoMessage("Tratamento excluído com sucesso!");
		} catch (BusinessException | ValidationException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void baixarArquivo() {		
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		try {			
			response.setContentType(documento.getArquivo().getContentType());
			response.setHeader("Content-Disposition","attachment; filename=" + documento.getArquivo().getNomeArquivo());
			response.setContentLength(documento.getArquivo().getDados().length);
			ServletOutputStream output = response.getOutputStream();
			output.write(documento.getArquivo().getDados(), 0, documento.getArquivo().getDados().length);
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			errorMessage(e.getMessage());
		}		
	}
	
	public List<Documento> getListaDocumentos() {
		try {
			if (entity.getCategoriaDocumento() != null) {
				return getDocumentoService().buscarPorCategoriaDocumento(entity.getCategoriaDocumento());
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<CategoriaDocumento> getListaCategoriaDocumento() {
		try {
			return getCategoriaDocumentoService().buscarPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<CategoriaDocumento>();
	}
	
	public ISaude getService() {
		return service;
	}

	public IDocumento getDocumentoService() {
		return documentoService;
	}

	public ICategoriaDocumento getCategoriaDocumentoService() {
		return categoriaDocumentoService;
	}

	public HistoricoSaude getHistoricoSaude() {
		return historicoSaude;
	}

	public void setHistoricoSaude(HistoricoSaude historicoSaude) {
		this.historicoSaude = historicoSaude;
	}

	public TratamentoSaude getTratamentoSaude() {
		return tratamentoSaude;
	}

	public void setTratamentoSaude(TratamentoSaude tratamentoSaude) {
		this.tratamentoSaude = tratamentoSaude;
	}

	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}
}