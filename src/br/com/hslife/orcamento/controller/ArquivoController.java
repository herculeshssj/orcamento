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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.enumeration.Container;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IArquivo;
import br.com.hslife.orcamento.model.CriterioArquivo;

@Component("arquivoMB")
@Scope("session")
public class ArquivoController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8085348187243579495L;

	@Autowired
	private IArquivo service;
	
	private CriterioArquivo criterio = new CriterioArquivo();
	
	private Arquivo entity;
	private List<Arquivo> listEntity;
	private Long idEntity;
	
	private BigDecimal espacoOcupado = new BigDecimal(0);

	public ArquivoController() {		
		moduleTitle = "Arquivos Anexados";
	}
	
	@Override
	protected void initializeEntity() {
		/* Este método sempre irá garantir que haja objetos elegíveis para o Garbage Collector */
		
		// Seta o valor null na entidade antes de instanciar um novo objeto
		entity = null;
		entity = new Arquivo();
		
		// Chama o método clear para remover todas as referências, seta null na lista e instancia uma nova
		if (listEntity != null && !listEntity.isEmpty()) 
			listEntity.clear();
		
		listEntity = null;
		listEntity = new ArrayList<Arquivo>();
	}
	
	@Override
	public String startUp() {
		initializeEntity();
		
		// Invoca o Garbage Collector
		System.gc();
		
		return "/pages/Arquivo/listArquivo";
	}
	
	public String find() {
		initializeEntity();
		
		// Invoca o Garbage Collector
		System.gc();
		
		try {
			criterio.setUsuario(getUsuarioLogado());
			listEntity = getService().buscarPorCriterioArquivo(criterio);
			
			// Variável que contabiliza quantos arquivos podem ser descartados
			int quantDescartar = 0;
			BigDecimal bytesALiberar = new BigDecimal(0);
			
			// Calcula o espaço ocupado e injeta as opções do sistema nas entidades
			espacoOcupado = new BigDecimal(0);
			Map<String, Integer> opcoesSistema = getOpcoesSistema().getOpcoesArquivosAnexados(getUsuarioLogado());
			for (Arquivo a : listEntity) {
				espacoOcupado = espacoOcupado.add(new BigDecimal(a.getTamanho()));
				a.setOpcoesSistema(opcoesSistema);
				if (a.isPrazoExpirado()) {
					quantDescartar++;
					bytesALiberar = bytesALiberar.add(new BigDecimal(a.getTamanho()));
				}
			}
			
			// Retorna uma mensagem informando quandos arquivos podem ser excluídos
			if (quantDescartar != 0) {
				warnMessage(quantDescartar + " arquivo(s) pode(m) ser descartado(s), liberando " + new DecimalFormat("#,##0.##").format(bytesALiberar) + " bytes no disco.");
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		return "";
	}
	
	public String view() {
		return "/pages/Arquivo/viewArquivo";
	}
	
	public String delete() {
		try {
			getService().excluir(entity);			
			infoMessage("Registro excluído com sucesso!");
		
			// Verifica se a listagem de resultados está nula ou não para poder efetuar novamente a busca
			if (listEntity != null && !listEntity.isEmpty()) {				
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", getUsuarioLogado());
							
				// Determina se a busca será executada novamente
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {					
					find();
				} else {
					initializeEntity();
				}
			} else {
				initializeEntity();
			}
			return "/pages/Arquivo/listArquivo"; 
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String cancel() {
		return "/pages/Arquivo/listArquivo";
	}
	
	public void baixarArquivo() {		
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		try {			
			response.setContentType(entity.getContentType());
			response.setHeader("Content-Disposition","attachment; filename=" + entity.getNomeArquivo());
			response.setContentLength(entity.getDados().length);
			ServletOutputStream output = response.getOutputStream();
			output.write(entity.getDados(), 0, entity.getDados().length);
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			errorMessage(e.getMessage());
		}		
	}
	
	public List<SelectItem> getListaContainer() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (Container status : Container.values()) {
			listaSelectItem.add(new SelectItem(status, status.toString()));
		}
		return listaSelectItem;
	} 
	
	public int getQuantRegistros() {
		if (listEntity == null || listEntity.isEmpty()) {
			return 0;
		} else {
			return listEntity.size();
		}
	}
	
	public String getEspacoOcupado() {
		return new DecimalFormat("#,##0.##").format(espacoOcupado);
	}

	public IArquivo getService() {
		return service;
	}

	public void setService(IArquivo service) {
		this.service = service;
	}

	public Arquivo getEntity() {
		return entity;
	}

	public void setEntity(Arquivo entity) {
		this.entity = entity;
	}

	public List<Arquivo> getListEntity() {
		return listEntity;
	}

	public void setListEntity(List<Arquivo> listEntity) {
		this.listEntity = listEntity;
	}

	public Long getIdEntity() {
		return idEntity;
	}

	public void setIdEntity(Long idEntity) {
		this.idEntity = idEntity;
	}

	public CriterioArquivo getCriterio() {
		return criterio;
	}

	public void setCriterio(CriterioArquivo criterio) {
		this.criterio = criterio;
	}
}