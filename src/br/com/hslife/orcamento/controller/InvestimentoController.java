/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Banco;
import br.com.hslife.orcamento.entity.Investimento;
import br.com.hslife.orcamento.entity.MovimentacaoInvestimento;
import br.com.hslife.orcamento.entity.ResumoInvestimento;
import br.com.hslife.orcamento.enumeration.TipoInvestimento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IBanco;
import br.com.hslife.orcamento.facade.IInvestimento;
import br.com.hslife.orcamento.util.Util;

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
	private IBanco bancoService;
	
	private TipoInvestimento tipoSelecionado;
	private ResumoInvestimento resumo;
	private MovimentacaoInvestimento movimentacao;
	private double investimentoInicial;
	private Set<MovimentacaoInvestimento> movimentacoesInvestimento;
	
	private int mesResumo;
	private int anoResumo;
	private int mesMovimentacao;
	private int anoMovimentacao;
	
	public InvestimentoController() {
		super(new Investimento());
		moduleTitle = "Investimentos";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Investimento();
		listEntity = new ArrayList<Investimento>();		
	}
	
	@Override
	public void find() {
		// Método em stand-by
//		try {
//			listEntity = getService().buscarPorTipoInvestimentoEUsuario(tipoSelecionado, getUsuarioLogado());
//		} catch (ValidationException | BusinessException be) {
//			errorMessage(be.getMessage());
//		}
	}
	
	@Override
	public String create() {
		initializeEntity();
		return super.create();
	}
	
	@Override
	public String save() {
		try {
			
			entity.setUsuario(getUsuarioLogado());
			entity.investimentoInicial(entity.getInicioInvestimento(), investimentoInicial);
			
			return super.save();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		return "";
	}
	
	public void selecionarResumoInvestimento() {
		resumo = entity.buscarResumoInvestimento(mesResumo, anoResumo);
	}
	
	public void selecionarMovimentacoesInvestimento() {
		movimentacoesInvestimento = entity.buscarMovimentacoesInvestimento(mesMovimentacao, anoMovimentacao);
	}

	public void atualizaListaInvestimento() {
		listEntity = getService().buscarPorTipoInvestimentoEUsuario(tipoSelecionado, getUsuarioLogado());
	}
	
	public List<Banco> getListaBanco() {
		try {
			List<Banco> resultado = getBancoService().buscarAtivosPorUsuario(getUsuarioLogado());
			// Lógica para incluir o banco inativo da entidade na combo
			if (resultado != null && entity.getBanco() != null) {
				if (!resultado.contains(entity.getBanco())) {
					entity.getBanco().setAtivo(true);
					resultado.add(entity.getBanco());
				}
			}
			return resultado;
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public List<SelectItem> getListaMeses() {
		List<SelectItem> meses = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			meses.add(new SelectItem(Integer.valueOf(i + 1), Util.meses[i]));
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

	public TipoInvestimento getTipoSelecionado() {
		return tipoSelecionado;
	}

	public void setTipoSelecionado(TipoInvestimento tipoSelecionado) {
		this.tipoSelecionado = tipoSelecionado;
	}

	public ResumoInvestimento getResumo() {
		return resumo;
	}

	public void setResumo(ResumoInvestimento resumo) {
		this.resumo = resumo;
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

	public IBanco getBancoService() {
		return bancoService;
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

	public int getMesMovimentacao() {
		return mesMovimentacao;
	}

	public void setMesMovimentacao(int mesMovimentacao) {
		this.mesMovimentacao = mesMovimentacao;
	}

	public int getAnoMovimentacao() {
		return anoMovimentacao;
	}

	public void setAnoMovimentacao(int anoMovimentacao) {
		this.anoMovimentacao = anoMovimentacao;
	}
}