/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor
	
	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

    para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 - 
	
	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/

package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Benfeitoria;
import br.com.hslife.orcamento.entity.CategoriaDocumento;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.GrupoLancamento;
import br.com.hslife.orcamento.entity.Patrimonio;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IBenfeitoria;
import br.com.hslife.orcamento.facade.ICategoriaDocumento;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IGrupoLancamento;
import br.com.hslife.orcamento.facade.IPatrimonio;
import br.com.hslife.orcamento.model.OrcamentoBenfeitoria;

@Component
@Scope("session")
public class BenfeitoriaController extends AbstractCRUDController<Benfeitoria> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5919353747340308697L;
	
	@Autowired
	private IBenfeitoria service;
	
	@Autowired
	private IPatrimonio patrimonioService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private ICategoriaDocumento categoriaDocumentoService;
	
	@Autowired
	private IGrupoLancamento grupoLancamentoService;
	
	private Patrimonio patrimonioSelecionado;
	
	private OrcamentoBenfeitoria orcamento = new OrcamentoBenfeitoria();
	private List<OrcamentoBenfeitoria> listaOrcamento;
	
	public BenfeitoriaController() {
		super(new Benfeitoria());
		moduleTitle = "Patrimônio - Benfeitorias";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Benfeitoria();
		listEntity = new ArrayList<>();
	}
	
	@Override
	public String startUp() {
		find();
		return super.startUp();
	}
	
	@Override
	public void find() {
		try {
			listEntity = getService().buscarTodosPorPatrimonioEBenfeitoria(patrimonioSelecionado, getUsuarioLogado());
		} catch (ValidationException | BusinessException e) {
			errorMessage(e.getMessage());
		}
	}
	
	@Override
	public String save() {
		entity.setPatrimonio(patrimonioSelecionado);
		String retorno = super.save();
		find(); // somente para forçar uma busca inicial
		return retorno;
	}
	
	public String orcamentoBenfeitoriaView() {		
		if (entity.getOrcamentoBenfeitoria() != null && !entity.getOrcamentoBenfeitoria().isEmpty()) {
			JSONArray jsonArray = new JSONArray(entity.getOrcamentoBenfeitoria());
			
			listaOrcamento = new ArrayList<>();
			
			for (int i = 0; i < jsonArray.length(); i++) {
				listaOrcamento.add(new OrcamentoBenfeitoria(jsonArray.getJSONObject(i)));
			}
		}
		
		return "/pages/Benfeitoria/orcamentoBenfeitoria";
	}
	
	public void adicionarOrcamento() {
		orcamento.setId(System.currentTimeMillis());
		listaOrcamento.add(orcamento);
		orcamento = new OrcamentoBenfeitoria();
		
		Collections.sort(listaOrcamento);
	}
	
	public void editarOrcamento() {
		// Usado somente para carregar as informações do orçamento
	}
	
	public void removerOrcamento() {
		for (Iterator<OrcamentoBenfeitoria> i = listaOrcamento.iterator(); i.hasNext(); ) {
			OrcamentoBenfeitoria ob = i.next();
			if (ob.equals(orcamento)) {
				i.remove();
				break;
			}
		}
	}
	
	public String salvarOrcamentoBenfeitoria() {
		try {
			JSONArray jsonArray = new JSONArray(listaOrcamento);
			entity.setOrcamentoBenfeitoria(jsonArray.toString());
			return this.save();
		} catch (BusinessException | ValidationException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public List<CategoriaDocumento> getListaCategoriaDocumento() {
		try {
			return getCategoriaDocumentoService().buscarPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<GrupoLancamento> getListaGrupoLancamento() {
		try {
			return getGrupoLancamentoService().buscarTodosDescricaoEAtivoPorUsuario(null, true, getUsuarioLogado());
		} catch (ValidationException | BusinessException e) {
			errorMessage(e.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<Favorecido> getListaFavorecido() {
		try {
			return getFavorecidoService().buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}

	public IBenfeitoria getService() {
		return service;
	}

	public IPatrimonio getPatrimonioService() {
		return patrimonioService;
	}

	public ICategoriaDocumento getCategoriaDocumentoService() {
		return categoriaDocumentoService;
	}

	public IGrupoLancamento getGrupoLancamentoService() {
		return grupoLancamentoService;
	}

	public Patrimonio getPatrimonioSelecionado() {
		return patrimonioSelecionado;
	}

	public void setPatrimonioSelecionado(Patrimonio patrimonioSelecionado) {
		this.patrimonioSelecionado = patrimonioSelecionado;
	}

	public IFavorecido getFavorecidoService() {
		return favorecidoService;
	}

	public OrcamentoBenfeitoria getOrcamento() {
		return orcamento;
	}

	public void setOrcamento(OrcamentoBenfeitoria orcamento) {
		this.orcamento = orcamento;
	}

	public List<OrcamentoBenfeitoria> getListaOrcamento() {
		return listaOrcamento;
	}

	public void setListaOrcamento(List<OrcamentoBenfeitoria> listaOrcamento) {
		this.listaOrcamento = listaOrcamento;
	}
}