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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.CategoriaDocumento;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.Meta;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Patrimonio;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.ICategoriaDocumento;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IMeta;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.facade.IPatrimonio;

@Component
@Scope("session")
public class PatrimonioController extends AbstractCRUDController<Patrimonio> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5919353747340308697L;
	
	@Autowired
	private IPatrimonio service;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;
	
	@Autowired
	private ICategoriaDocumento categoriaDocumentoService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private IMeta grupoLancamentoService;
	
	@Autowired
	private BenfeitoriaController benfeitoriaController;
	
	public PatrimonioController() {
		super(new Patrimonio());
		moduleTitle = "Patrimônio";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Patrimonio();
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
			listEntity = getService().buscarTodosPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException e) {
			errorMessage(e.getMessage());
		}
	}
	
	public String benfeitoriaView() {
		try {
			Patrimonio patrimonio = getService().buscarPorID(idEntity);
			getBenfeitoriaController().setPatrimonioSelecionado(patrimonio);			
			return getBenfeitoriaController().startUp();
		} catch (BusinessException e) {
			errorMessage(e.getMessage());
		}
		return "";
	}
	
	public List<Favorecido> getListaFavorecido() {
		try {
			return getFavorecidoService().buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
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
	
	public List<Moeda> getListaMoeda() {
		try {
			return getMoedaService().buscarAtivosPorUsuario(getUsuarioLogado());
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

	public IPatrimonio getService() {
		return service;
	}

	public IMeioPagamento getMeioPagamentoService() {
		return meioPagamentoService;
	}

	public IFavorecido getFavorecidoService() {
		return favorecidoService;
	}

	public ICategoriaDocumento getCategoriaDocumentoService() {
		return categoriaDocumentoService;
	}

	public IMoeda getMoedaService() {
		return moedaService;
	}

	public IMeta getGrupoLancamentoService() {
		return grupoLancamentoService;
	}

	public BenfeitoriaController getBenfeitoriaController() {
		return benfeitoriaController;
	}
}
