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

para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor,

Boston, MA  02110-1301, USA.


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
import br.com.hslife.orcamento.entity.Meta;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Patrimonio;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IBenfeitoria;
import br.com.hslife.orcamento.facade.ICategoriaDocumento;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IMeta;
import br.com.hslife.orcamento.facade.IMeioPagamento;
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
	private IMeioPagamento meioPagamentoService;
	
	@Autowired
	private ICategoriaDocumento categoriaDocumentoService;
	
	@Autowired
	private IMeta grupoLancamentoService;
	
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
		orcamento = new OrcamentoBenfeitoria();
		listaOrcamento = new ArrayList<>();
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
		listaOrcamento = new ArrayList<>();
		
		entity = getService().buscarPorID(idEntity);
		
		if (entity.getOrcamentoBenfeitoria() != null && !entity.getOrcamentoBenfeitoria().isEmpty()) {
			JSONArray jsonArray = new JSONArray(entity.getOrcamentoBenfeitoria());
			
			for (int i = 0; i < jsonArray.length(); i++) {
				listaOrcamento.add(new OrcamentoBenfeitoria(jsonArray.getJSONObject(i)));
			}
		}
		
		return "/pages/Benfeitoria/orcamentoBenfeitoria";
	}
	
	public void adicionarOrcamento() {
		// Verifica se o orçamento já possui ID
		if (orcamento.getId() != null && orcamento.getId() > 0) {
			// Exclui o orçamento da listagem
			this.removerOrcamento();
		} else {
			// Seta o ID do Orçamento
			orcamento.setId(System.currentTimeMillis());
		}
		// Seta a data
		orcamento.setDataLong(orcamento.getData().getTime());
		
		// Itera os itens para que não tenha mais nenhum outro orçamento
		// aprovado
		if (orcamento.getAprovado()) {
			for (OrcamentoBenfeitoria ob : listaOrcamento) {	
				ob.setAprovado(false);
			}
		}		
		
		// Adiciona o orçamento na listagem		
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
	
	public List<Meta> getListaGrupoLancamento() {
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
	
	public List<MeioPagamento> getListaMeioPagamento() {
		try {
			List<MeioPagamento> resultado = getMeioPagamentoService().buscarAtivosPorUsuario(getUsuarioLogado());
			// Lógica para incluir o meio de pagamento inativo da entidade na combo
			if (resultado != null && entity.getMeioPagamento() != null) {
				if (!resultado.contains(entity.getMeioPagamento())) {
					entity.getMeioPagamento().setAtivo(true);
					resultado.add(entity.getMeioPagamento());
				}
			}
			return resultado;
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}

	public IBenfeitoria getService() {
		return service;
	}
	
	public IMeioPagamento getMeioPagamentoService() {
		return meioPagamentoService;
	}

	public IPatrimonio getPatrimonioService() {
		return patrimonioService;
	}

	public ICategoriaDocumento getCategoriaDocumentoService() {
		return categoriaDocumentoService;
	}

	public IMeta getGrupoLancamentoService() {
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
