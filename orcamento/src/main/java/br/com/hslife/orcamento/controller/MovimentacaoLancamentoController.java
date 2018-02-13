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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.DetalheLancamento;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.IncrementoClonagemLancamento;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.facade.IMovimentacaoLancamento;

@Component("movimentacaoLancamentoMB")
@Scope("session")
public class MovimentacaoLancamentoController extends AbstractController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2116626869708662527L;
	
	@Autowired
	private IMovimentacaoLancamento service;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private ICategoria categoriaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;
	
	@Autowired
	private IMoeda moedaService;
	
	private int quantADuplicar = 1;
	private LancamentoConta lancamentoATransferir;
	private IncrementoClonagemLancamento incremento;
	private String descricao;
	private String observacao;
	private Date dataPagamento;
	private TipoCategoria tipoCategoriaSelecionada;	
	private List<LancamentoConta> lancamentosSelecionados;	
	private LancamentoConta lancamentoSelecionado;	
	private DetalheLancamento detalheLancamento;
	private Conta contaSelecionada;
	private Conta contaDestino;	
	private Categoria categoriaSelecionada;
	private Categoria categoriaDestino;	
	private Favorecido favorecidoSelecionado;	
	private MeioPagamento meioPagamentoSelecionado;
	private Moeda moedaSelecionada;
	
	private String goToListContaPage = "/pages/LancamentoConta/listLancamentoConta";
	
	public MovimentacaoLancamentoController() {
		lancamentosSelecionados = new ArrayList<LancamentoConta>();
		
		moduleTitle = "Movimentação de Lançamentos";
	}
	
	@Override
	public String startUp() {
		return null;
	}
	
	@Override
	public String getModuleTitle() {		
		return super.getModuleTitle() + actionTitle;
	}
	
	@Override
	protected void initializeEntity() {
		lancamentosSelecionados = new ArrayList<LancamentoConta>();
		this.descricao = "";
		this.dataPagamento = null;
		this.tipoCategoriaSelecionada = null;
		this.favorecidoSelecionado = null;
		this.meioPagamentoSelecionado = null;
		this.observacao = null;
	}
		
	public String cancel() {
		initializeEntity();
		return goToListContaPage;
	}
	
	public String moverView() {
		try {
			if (lancamentosSelecionados != null && !lancamentosSelecionados.isEmpty()) {
				for (Iterator<LancamentoConta> i = lancamentosSelecionados.iterator(); i.hasNext(); ) {
					LancamentoConta l = i.next();
					if (l.getFaturaCartao() != null || l.getLancamentoPeriodico() != null || l.getHashImportacao() != null) {
						i.remove();
					}
				}
				if (lancamentosSelecionados.isEmpty()) {
					warnMessage("Nenhum lançamento a mover!");
				} else {
					actionTitle = " - Mover";
					return "/pages/MovimentacaoLancamento/moverLancamentos";
				}
			} else {
				warnMessage("Nenhum lançamento selecionado!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage(e.getMessage());
		}
		return "";
	}
	
	public String moverLancamentos() {
		try {
			getService().moverLancamentos(lancamentosSelecionados, contaSelecionada);
			infoMessage("Lançamentos movidos com sucesso!");
			return cancel();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String duplicarView() {
		try {
			if (lancamentosSelecionados != null && !lancamentosSelecionados.isEmpty()) {				
				actionTitle = " - Duplicar";
				return "/pages/MovimentacaoLancamento/duplicarLancamentos";				
			} else {
				warnMessage("Nenhum lançamento selecionado!");
			}
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String duplicarLancamentos() {
		try {
			getService().duplicarLancamentos(lancamentosSelecionados, contaSelecionada, quantADuplicar, incremento);			
			infoMessage("Lançamentos duplicados com sucesso!");
			return cancel();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String excluirView() {
		try {
			if (lancamentosSelecionados != null && !lancamentosSelecionados.isEmpty()) {
				for (Iterator<LancamentoConta> i = lancamentosSelecionados.iterator(); i.hasNext(); ) {
					LancamentoConta l = i.next();
					if (l.getFaturaCartao() != null || l.getLancamentoPeriodico() != null) {
						i.remove();
					}
				}
				if (lancamentosSelecionados.isEmpty()) {
					warnMessage("Nenhum lançamento a excluir!");
				} else {
					actionTitle = " - Excluir";
					return "/pages/MovimentacaoLancamento/excluirLancamentos";
				}
			} else {
				warnMessage("Nenhum lançamento selecionado!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage(e.getMessage());
		}
		return "";
	}
	
	public String excluirLancamentos() {
		try {
			getService().excluirLancamentos(lancamentosSelecionados);
			infoMessage("Lançamentos excluídos com sucesso!");
			return cancel();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return ""; 
	}
	
	public String removerVinculosView() {
		try {
			if (lancamentosSelecionados != null && !lancamentosSelecionados.isEmpty()) {				
				actionTitle = " - Vínculos";
				return "/pages/MovimentacaoLancamento/vinculosLancamentos";				
			} else {
				warnMessage("Nenhum lançamento selecionado!");
			}
		} catch (Exception e) {
			errorMessage(e.getMessage());
		}
		return "";
	}
	
	public String removerVinculos() {
		try {
			getService().removerVinculos(lancamentosSelecionados);
			infoMessage("Vínculos removidos com sucesso!");
			return cancel();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String transferirView() {
		try {
			actionTitle = " - Transferir";
			lancamentoATransferir = new LancamentoConta();
			return "/pages/MovimentacaoLancamento/transferirLancamento";
		} catch (Exception e) {
			errorMessage(e.getMessage());
		}
		return "";
	}
	
	public String transferirLancamentos() {
		try {
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("CONTA_ORIGEM", contaSelecionada);
			parametros.put("CONTA_DESTINO", contaDestino);
			parametros.put("CATEGORIA_ORIGEM", categoriaSelecionada);
			parametros.put("CATEGORIA_DESTINO", categoriaDestino);
			parametros.put("FAVORECIDO_DESTINO", favorecidoSelecionado);
			parametros.put("MEIOPAGAMENTO_DESTINO", meioPagamentoSelecionado);
			getService().transferirLancamentos(lancamentoATransferir, parametros);
			infoMessage("Valor transferido com sucesso!");
			return cancel();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return ""; 
	}
	
	public String alterarPropriedadesView() {
		try {
			actionTitle = " - Alterar propriedades";
			return "/pages/MovimentacaoLancamento/propriedadesLancamento";
		} catch (Exception e) {
			errorMessage(e.getMessage());
		}
		return "";
	}
	
	public String alterarPropriedades() {
		try {
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("DESCRICAO_DESTINO", descricao);
			parametros.put("OBSERVACAO_DESTINO", observacao);
			parametros.put("CATEGORIA_DESTINO", categoriaSelecionada);
			parametros.put("FAVORECIDO_DESTINO", favorecidoSelecionado);
			parametros.put("MEIOPAGAMENTO_DESTINO", meioPagamentoSelecionado);
			getService().alterarPropriedades(lancamentosSelecionados, parametros);
			infoMessage("Propriedades alteradas com sucesso!");
			return cancel();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return ""; 
	}
	
	public void excluirLancamentoListaSelecionados() {
		if (lancamentosSelecionados.size() > 1)
			lancamentosSelecionados.remove(lancamentoSelecionado);
		else {
			warnMessage("É necessário ter um lançamento selecionado!");
		}
	}
	
	public String mesclarView() {
		try {
			if (lancamentosSelecionados != null && !lancamentosSelecionados.isEmpty()) {
				for (Iterator<LancamentoConta> i = lancamentosSelecionados.iterator(); i.hasNext(); ) {
					LancamentoConta l = i.next();
					if (l.getHashImportacao() != null || l.getLancamentoPeriodico() != null) {
						i.remove();
					}
				}
				if (lancamentosSelecionados.isEmpty()) {
					warnMessage("Nenhum lançamento a mesclar!");
				} else {
					actionTitle = " - Mesclar";
					return "/pages/MovimentacaoLancamento/mesclarLancamentos";
				}
			} else {
				warnMessage("Nenhum lançamento selecionado!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage(e.getMessage());
		}
		return "";
	}
	
	public String mesclar() {
		if (descricao == null || descricao.isEmpty()) { warnMessage("Informe a descrição!"); return ""; }
		if (dataPagamento == null) { warnMessage("Informe a data de pagamento!"); return ""; }
		if (tipoCategoriaSelecionada == null) { warnMessage("Informe um tipo de categoria!"); return ""; }
		try {
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("CONTA_DESTINO", contaSelecionada);
			parametros.put("DESCRICAO_DESTINO", descricao);
			parametros.put("OBSERVACAO_DESTINO", observacao);
			parametros.put("DATAPAGAMENTO", dataPagamento);
			parametros.put("CATEGORIA_DESTINO", categoriaSelecionada);
			parametros.put("FAVORECIDO_DESTINO", favorecidoSelecionado);
			parametros.put("MEIOPAGAMENTO_DESTINO", meioPagamentoSelecionado);
			parametros.put("MOEDA_DESTINO", moedaSelecionada);
			if (tipoCategoriaSelecionada.equals(TipoCategoria.CREDITO))
				parametros.put("TIPO_LANCAMENTO", TipoLancamento.RECEITA);
			else
				parametros.put("TIPO_LANCAMENTO", TipoLancamento.DESPESA);						
			getService().mesclarLancamento(lancamentosSelecionados, parametros);
			infoMessage("Lançamentos mesclados com sucesso!");
			initializeEntity();
			return cancel();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String dividirView() {
		if (lancamentosSelecionados != null && !lancamentosSelecionados.isEmpty()) {
			for (Iterator<LancamentoConta> i = lancamentosSelecionados.iterator(); i.hasNext(); ) {
				LancamentoConta l = i.next();
				if (l.getHashImportacao() != null || l.getLancamentoPeriodico() != null) {
					i.remove();
				}
			}
			if (lancamentosSelecionados.isEmpty()) {
				warnMessage("Nenhum lançamento a dividir!");
			} else {
				actionTitle = " - Dividir";
				lancamentoSelecionado = lancamentosSelecionados.get(0);
				return "/pages/MovimentacaoLancamento/dividirLancamento";
			}
		} else {
			warnMessage("Nenhum lançamento selecionado!");
		}
		return "";
	}
	
	public String dividirLancamento() {
		try {
			getService().dividirLancamento(lancamentoSelecionado, quantADuplicar);
			infoMessage("Lançamento dividido com sucesso!");
			return cancel();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public String detalharView() {
		if (lancamentosSelecionados == null || lancamentosSelecionados.isEmpty()) {
			warnMessage("Nenhum lançamento a detalhar!");
		} else {
			actionTitle = " - Detalhar";
			lancamentoSelecionado = lancamentosSelecionados.get(0);
			detalheLancamento = new DetalheLancamento();
			return "/pages/MovimentacaoLancamento/detalharLancamento";
		}
		return "";
	}
	
	public String salvarDetalhamento() {
		try {
			// Antes de salvar valida cada entrada e atribui o lançamento aos detalhes
			for (DetalheLancamento detalhe : lancamentoSelecionado.getDetalhes()) {
				detalhe.validate();
			}
			getService().salvarDetalhamentoLancamento(lancamentoSelecionado);
			infoMessage("Detalhamento salvo com sucesso.");
			return cancel();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public void adicionarDetalheLancamento(){
		if (detalheLancamento.getDescricao() == null || detalheLancamento.getDescricao().isEmpty() || detalheLancamento.getData() == null) {
			warnMessage("Informe a descrição e a data do lançamento!");
			return;
		}
		
		if (detalheLancamento.getValor() > lancamentoSelecionado.getTotalADetalhar()) {
			warnMessage("Valor informado supera o total a detalhar!");
			return;
		}
		
		lancamentoSelecionado.getDetalhes().add(detalheLancamento);
		detalheLancamento = new DetalheLancamento();
	}
	
	public void removerDetalheLancamento() {
		lancamentoSelecionado.getDetalhes().remove(detalheLancamento);
		detalheLancamento = new DetalheLancamento();
	}
	
	public void atualizaComboCategorias() {
		this.getListaCategoria();
	}
	
	public List<Categoria> getListaCategoria() {
		try {
			if (tipoCategoriaSelecionada != null)
				return categoriaService.buscarAtivosPorTipoCategoriaEUsuario(tipoCategoriaSelecionada, getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		} 
		return new ArrayList<Categoria>();
	}
	
	public List<Categoria> getListaCategoriaCredito() {
		try {
			return categoriaService.buscarAtivosPorTipoCategoriaEUsuario(TipoCategoria.CREDITO, getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<Categoria> getListaCategoriaDebito() {
		try {
			return categoriaService.buscarAtivosPorTipoCategoriaEUsuario(TipoCategoria.DEBITO, getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<Favorecido> getListaFavorecido() {
		try {
			return favorecidoService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<MeioPagamento> getListaMeioPagamento() {
		try {
			return meioPagamentoService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<Conta> getListaContaAtivo() {
		try {
			return contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", null, getUsuarioLogado(), true);
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Conta>();
	}
	
	public List<Moeda> getListaMoeda() {
		try {
			return moedaService.buscarAtivosPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public IMovimentacaoLancamento getService() {
		return service;
	}

	public void setService(IMovimentacaoLancamento service) {
		this.service = service;
	}

	public ICategoria getCategoriaService() {
		return categoriaService;
	}

	public void setCategoriaService(ICategoria categoriaService) {
		this.categoriaService = categoriaService;
	}

	public int getQuantADuplicar() {
		return quantADuplicar;
	}

	public void setQuantADuplicar(int quantADuplicar) {
		this.quantADuplicar = quantADuplicar;
	}

	public List<LancamentoConta> getLancamentosSelecionados() {
		return lancamentosSelecionados;
	}

	public void setLancamentosSelecionados(
			List<LancamentoConta> lancamentosSelecionados) {
		this.lancamentosSelecionados = lancamentosSelecionados;
	}

	public LancamentoConta getLancamentoSelecionado() {
		return lancamentoSelecionado;
	}

	public void setLancamentoSelecionado(LancamentoConta lancamentoSelecionado) {
		this.lancamentoSelecionado = lancamentoSelecionado;
	}

	public Conta getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(Conta contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}

	public Categoria getCategoriaSelecionada() {
		return categoriaSelecionada;
	}

	public void setCategoriaSelecionada(Categoria categoriaSelecionada) {
		this.categoriaSelecionada = categoriaSelecionada;
	}

	public Favorecido getFavorecidoSelecionado() {
		return favorecidoSelecionado;
	}

	public void setFavorecidoSelecionado(Favorecido favorecidoSelecionado) {
		this.favorecidoSelecionado = favorecidoSelecionado;
	}

	public MeioPagamento getMeioPagamentoSelecionado() {
		return meioPagamentoSelecionado;
	}

	public void setMeioPagamentoSelecionado(MeioPagamento meioPagamentoSelecionado) {
		this.meioPagamentoSelecionado = meioPagamentoSelecionado;
	}

	public Conta getContaDestino() {
		return contaDestino;
	}

	public void setContaDestino(Conta contaDestino) {
		this.contaDestino = contaDestino;
	}

	public Categoria getCategoriaDestino() {
		return categoriaDestino;
	}

	public void setCategoriaDestino(Categoria categoriaDestino) {
		this.categoriaDestino = categoriaDestino;
	}

	public LancamentoConta getLancamentoATransferir() {
		return lancamentoATransferir;
	}

	public void setLancamentoATransferir(LancamentoConta lancamentoATransferir) {
		this.lancamentoATransferir = lancamentoATransferir;
	}

	public IncrementoClonagemLancamento getIncremento() {
		return incremento;
	}

	public void setIncremento(IncrementoClonagemLancamento incremento) {
		this.incremento = incremento;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public TipoCategoria getTipoCategoriaSelecionada() {
		return tipoCategoriaSelecionada;
	}

	public void setTipoCategoriaSelecionada(TipoCategoria tipoCategoriaSelecionada) {
		this.tipoCategoriaSelecionada = tipoCategoriaSelecionada;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public DetalheLancamento getDetalheLancamento() {
		return detalheLancamento;
	}

	public void setDetalheLancamento(DetalheLancamento detalheLancamento) {
		this.detalheLancamento = detalheLancamento;
	}

	public Moeda getMoedaSelecionada() {
		return moedaSelecionada;
	}

	public void setMoedaSelecionada(Moeda moedaSelecionada) {
		this.moedaSelecionada = moedaSelecionada;
	}
}
