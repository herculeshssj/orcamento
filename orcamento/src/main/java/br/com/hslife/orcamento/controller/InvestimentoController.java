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
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;

import br.com.hslife.orcamento.entity.*;
import br.com.hslife.orcamento.facade.IAdministradorInvestimento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.component.InfoCotacaoComponent;
import br.com.hslife.orcamento.enumeration.MesesDoAno;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoInvestimento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.ICategoriaInvestimento;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IInvestimento;
import br.com.hslife.orcamento.model.InfoCotacao;
import br.com.hslife.orcamento.util.EntityLabelComparator;

@Component("investimentoMB")
@Scope("session")
public class InvestimentoController extends AbstractCRUDController<Investimento> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5221608208205915464L;

	@Autowired
	private IInvestimento service;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private ICategoriaInvestimento categoriaInvestimentoService;

	@Autowired
	private IAdministradorInvestimento administradorInvestimentoService;
	
	@Autowired
	private InfoCotacaoComponent infoCotacaoComponent;
	
	private Conta contaSelecionada;
	private TipoInvestimento tipoSelecionado;
	private MovimentacaoInvestimento movimentacao;
	private double investimentoInicial;
	private Set<MovimentacaoInvestimento> movimentacoesInvestimento = new HashSet<>();
	private Investimento investimentoSelecionado;
	private InfoCotacao infoCotacao;
	private Dividendo dividendo;
	private Set<Dividendo> dividendosInvestimento;
	
	private int mesResumo;
	private int anoResumo;
	private Integer mesMovimentacao;
	private int anoMovimentacao;
	private Integer mesDividendo;
	private int anoDividendo;
	
	private OperacaoInvestimento operacaoInvestimento;

	public enum OperacaoInvestimento {
		SALVAR_MOVIMENTACAO, EDITAR_MOVIMENTACAO, EXCLUIR_MOVIMENTACAO;
	}
	
	public InvestimentoController() {
		super(new Investimento());
		moduleTitle = "Investimentos";
		
		// Pega o mês/ano atual
		Calendar temp = Calendar.getInstance();
		int mes = temp.get(Calendar.MONTH) + 1;
		int ano = temp.get(Calendar.YEAR);
		
		// Já deixa marcado o mês/ano atual no resumo e na movimentação
		mesMovimentacao = mes;
		anoMovimentacao = ano;
		
		mesDividendo = mes;
		anoDividendo = ano;
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Investimento();		
		tipoSelecionado = null;
		movimentacao = null;
		movimentacoesInvestimento = new LinkedHashSet<>();
		investimentoInicial = 0;
		listEntity = new ArrayList<>();
		dividendosInvestimento = new LinkedHashSet<>();
		investimentoSelecionado = null;
		contaSelecionada = null;
	}
	
	@Override
	public void find() {
		this.atualizaListaInvestimento();
		if (investimentoSelecionado != null && investimentoSelecionado.getId() != null) {
			entity = getService().buscarPorID(investimentoSelecionado.getId());
		}
		
		// Limpa as variáveis usadas para os dados do resumo e 
		// movimentação
		movimentacao = null;
		movimentacoesInvestimento = new LinkedHashSet<>();
		dividendosInvestimento = new LinkedHashSet<>();
		
		// Obtém a cotação do investimento
		if (entity != null 
				&& entity.getCategoriaInvestimento() != null
				&& entity.getCategoriaInvestimento().getTipoInvestimento().equals(TipoInvestimento.VARIAVEL)) {
			this.obterCotacao();
		}
	}
	
	@Override
	public String create() {
		initializeEntity();
		return super.create();
	}
	
	@Override
	public String edit() {
		// Verifica se existe investimento selecionado
		if (idEntity == null || idEntity < 0) {
			warnMessage("Nenhum investimento foi selecionado!");
			return "";
		}
		
		// Verifica se o investimento foi encerrado
		String retorno = super.edit();
		if (!entity.isAtivo()) {
			warnMessage("Não é possível editar! Investimento encerrado.");
			return "";
		} else {
			return retorno;
		}
	}
	
	@Override
	public String view() {
		// Verifica se existe investimento selecionado
		if (idEntity == null || idEntity < 0) {
			warnMessage("Nenhum investimento foi selecionado!");
			return "";
		}
		
		// Verifica se o investimento foi encerrado
		String retorno = super.view();
		if (!entity.isAtivo()) {
			warnMessage("Não é possível excluir/encerrar! Investimento encerrado.");
			return "";
		} else {
			return retorno;
		}
	}
	
	@Override
	public String save() {
		String retorno = null;
		try {
			
			//entity.setUsuario(getUsuarioLogado());
			// Verifica se a operação atual é de criação
			if (this.operation.equals("create"))
				entity.investimentoInicial(entity.getInicioInvestimento(), investimentoInicial);
			
			retorno = super.save();
			
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		return retorno;
	}
	
	public String encerrarInvestimento() {
		// Verifica se o atributo terminoINvestimento contém algum valor
		if (entity.getTerminoInvestimento() == null) {
			warnMessage("Informe a data de encerramento do investimento!");
			return null;
		}
		
		try {
			// Ao atribuir a data de término, o investimento ganha o status de encerrado
			String retorno = super.save();
			
			infoMessage("O investimento foi encerrado.");
			
			return retorno;
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return null;
	}
	
	public void reativarInvestimento() {
		// Verifica se existe investimento selecionado
		if (idEntity == null || idEntity < 0) {
			warnMessage("Nenhum investimento foi selecionado!");
			return;
		}
		
		try {
			entity.setTerminoInvestimento(null);
			super.save();
			infoMessage("O investimento foi reativado.");
			initializeEntity();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}

	public void selecionarMovimentacoesInvestimento() {
		movimentacoesInvestimento = entity.buscarMovimentacoesInvestimento(mesMovimentacao, anoMovimentacao);
	}
	
	public void selecionarDividendos() {
		entity.setDividendos(getService().buscarTodosDividendosPorInvestimento(entity));
		dividendosInvestimento = entity.buscarDividendos(mesDividendo, anoDividendo);
	}
	
	public String voltarInvestimento() {
		actionTitle = "";
		find();
		return "/pages/Investimento/listInvestimento";
	}
	
	public String novaMovimentacao() {
		movimentacao = new MovimentacaoInvestimento();
		actionTitle = " - Nova movimentação";
		operacaoInvestimento = OperacaoInvestimento.SALVAR_MOVIMENTACAO;
		return "/pages/Investimento/formMovimentacaoInvestimento";
	}
	
	public String editarMovimentacao() {
		actionTitle = " - Editar movimentação";
		operacaoInvestimento = OperacaoInvestimento.EDITAR_MOVIMENTACAO;
		return "/pages/Investimento/formMovimentacaoInvestimento";
	}
	
	public String excluirMovimentacao() {
		operacaoInvestimento = OperacaoInvestimento.EXCLUIR_MOVIMENTACAO;
		return this.salvarDadosInvestimento();
	}
	
	public String novoDividendo() {
		dividendo = new Dividendo();
		actionTitle = " - Novo dividendo";
		return "/pages/Investimento/formDividendo";
	}
	
	public String salvarDividendo() {
		try {
			dividendo.setInvestimento(entity);
			dividendo.validate();
			getService().salvarDividendo(dividendo);
			infoMessage("Dados do investimento salvos com sucesso!");
			initializeEntity();
			return this.voltarInvestimento();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		return "";
	}
	
	public String editarDividendo() {
		actionTitle = " - Editar dividendo";
		return "/pages/Investimento/formDividendo";
	}
	
	public String excluirDividendo() {
		try {
			getService().excluirDividendo(dividendo);
			infoMessage("Dados do investimento salvos com sucesso!");
			initializeEntity();
			return this.voltarInvestimento();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		return "";
	}

	public String salvarDadosInvestimento() {
		try {
			switch (operacaoInvestimento) {
				case SALVAR_MOVIMENTACAO : 
					entity.movimentarInvestimento(movimentacao);
					break;
				case EXCLUIR_MOVIMENTACAO : 
					entity.excluirMovimentacao(movimentacao);
				case EDITAR_MOVIMENTACAO : 
				default : 
			}
			getService().alterar(entity);
			infoMessage("Dados do investimento salvos com sucesso!");
			initializeEntity();
			return this.voltarInvestimento();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		return "";
	}
	
	private void obterCotacao() {
		this.infoCotacao = getInfoCotacaoComponent().invokeAPI(entity.getTicker());
	}
	
	public void atualizaListaInvestimento() {
		if (contaSelecionada != null) {
			listEntity = getService().buscarPorConta(contaSelecionada);
			
			// Ordena os investimentos de acordo com o método getLabel()
			listEntity.sort(new EntityLabelComparator());
		} else {
			listEntity = new ArrayList<>();
		}
	}
	
	public void atualizaInvestimentoInicial() {
		// Exibir e ocultar o campo 'Investimento Inicial' no cadastro
	}
	
	public List<SelectItem> getListaMeses() {
		List<SelectItem> meses = new ArrayList<>();
		for (MesesDoAno mes : MesesDoAno.values()) {
			meses.add(new SelectItem(mes.getNumeroMes(), mes.toString()));
		}
		return meses;
	}
	
	public List<Integer> getListaAnos() {
		List<Integer> anos = new ArrayList<>();
		for (int i = Calendar.getInstance().get(Calendar.YEAR); i > Calendar.getInstance().get(Calendar.YEAR) - 50; i--) {
			anos.add(i);
		}
		return anos;
	}
	
	public List<Conta> getListaConta() {
		List<Conta> contas = new ArrayList<>();
		try {
			if (getOpcoesSistema().getExibirContasInativas()) {
				contas = contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", new TipoConta[] {TipoConta.INVESTIMENTO}, getUsuarioLogado(), null);
			} else {
				contas = contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", new TipoConta[] {TipoConta.INVESTIMENTO}, getUsuarioLogado(), true);
			}
			if (contas != null && !contas.isEmpty() && contaSelecionada == null) {
				contaSelecionada = contas.get(0);
			}
			return contas;
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public List<CategoriaInvestimento> getListaCategoriaInvestimento() {
		List<CategoriaInvestimento> categorias = new ArrayList<>();
		try {
			return getCategoriaInvestimentoService().buscarTodos();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return categorias;
	}

	public List<AdministradorInvestimento> getListaAdministradorInvestimento() {
		return getAdministradorInvestimentoService().findAllByUsuario(getUsuarioLogado());
	}

	public IConta getContaService() {
		return contaService;
	}

	public ICategoriaInvestimento getCategoriaInvestimentoService() {
		return categoriaInvestimentoService;
	}

	public TipoInvestimento getTipoSelecionado() {
		return tipoSelecionado;
	}

	public void setTipoSelecionado(TipoInvestimento tipoSelecionado) {
		this.tipoSelecionado = tipoSelecionado;
	}

	public MovimentacaoInvestimento getMovimentacao() {
		return movimentacao;
	}

	public void setMovimentacao(MovimentacaoInvestimento movimentacao) {
		this.movimentacao = movimentacao;
	}

	public double getInvestimentoInicial() {
		return investimentoInicial;
	}

	public void setInvestimentoInicial(double investimentoInicial) {
		this.investimentoInicial = investimentoInicial;
	}

	public IInvestimento getService() {
		return service;
	}

	public Set<MovimentacaoInvestimento> getMovimentacoesInvestimento() {
		return movimentacoesInvestimento;
	}

	public void setMovimentacoesInvestimento(Set<MovimentacaoInvestimento> movimentacoesInvestimento) {
		this.movimentacoesInvestimento = movimentacoesInvestimento;
	}

	public int getMesResumo() {
		return mesResumo;
	}

	public void setMesResumo(int mesResumo) {
		this.mesResumo = mesResumo;
	}

	public int getAnoResumo() {
		return anoResumo;
	}

	public void setAnoResumo(int anoResumo) {
		this.anoResumo = anoResumo;
	}

	public int getAnoMovimentacao() {
		return anoMovimentacao;
	}

	public void setAnoMovimentacao(int anoMovimentacao) {
		this.anoMovimentacao = anoMovimentacao;
	}

	public Conta getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(Conta contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}

	public Investimento getInvestimentoSelecionado() {
		return investimentoSelecionado;
	}

	public void setInvestimentoSelecionado(Investimento investimentoSelecionado) {
		this.investimentoSelecionado = investimentoSelecionado;
	}

	public Integer getMesMovimentacao() {
		return mesMovimentacao;
	}

	public void setMesMovimentacao(Integer mesMovimentacao) {
		this.mesMovimentacao = mesMovimentacao;
	}

	public InfoCotacao getInfoCotacao() {
		return infoCotacao;
	}

	public void setInfoCotacao(InfoCotacao infoCotacao) {
		this.infoCotacao = infoCotacao;
	}

	public InfoCotacaoComponent getInfoCotacaoComponent() {
		return infoCotacaoComponent;
	}

	public Dividendo getDividendo() {
		return dividendo;
	}

	public void setDividendo(Dividendo dividendo) {
		this.dividendo = dividendo;
	}

	public Set<Dividendo> getDividendosInvestimento() {
		return dividendosInvestimento;
	}

	public void setDividendosInvestimento(Set<Dividendo> dividendosInvestimento) {
		this.dividendosInvestimento = dividendosInvestimento;
	}

	public Integer getMesDividendo() {
		return mesDividendo;
	}

	public void setMesDividendo(Integer mesDividendo) {
		this.mesDividendo = mesDividendo;
	}

	public int getAnoDividendo() {
		return anoDividendo;
	}

	public void setAnoDividendo(int anoDividendo) {
		this.anoDividendo = anoDividendo;
	}

	public IAdministradorInvestimento getAdministradorInvestimentoService() {
		return administradorInvestimentoService;
	}
}