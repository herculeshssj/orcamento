/***
  
  	Copyright (c) 2012, 2013, 2014 Hércules S. S. José

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

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site HSlife
    
    no endereco www.hslife.com.br ou escreva para a Fundação do Software 
    
    Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor acesse o 

    endereço www.hslife.com.br, pelo e-mail contato@hslife.com.br ou escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
 ***/

package br.com.hslife.orcamento.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoConta;

public class CriterioBuscaLancamentoConta {
	
	// Atributo que armazena os critérios selecionados
	private Map<String, Criterion> hibernateCriterions = new HashMap<String, Criterion>();
	
	private Conta conta;
	private String descricao;
	private Date dataInicio;
	private Date dataFim;
	private String agrupamento;
	private Long idAgrupamento;
	private TipoConta[] tipoConta;
	private StatusLancamentoConta[] statusLancamentoConta;
	
	private Integer limiteResultado = 0;
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
		
		if (descricao != null && !descricao.isEmpty())
			hibernateCriterions.put("descricao", Restrictions.ilike("lancamento.descricao", descricao, MatchMode.ANYWHERE));
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
		
		if (dataInicio != null)
			hibernateCriterions.put("dataInicio", Restrictions.ge("lancamento.dataPagamento", dataInicio));
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
		
		if (dataFim != null)
			hibernateCriterions.put("dataFim", Restrictions.le("lancamento.dataPagamento", dataFim));
	}
	
	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
		
		// Caso tipoConta esteja setado, remove para dar lugar a conta
		if (conta != null) {
			if (hibernateCriterions.containsKey("tipoConta"))
				hibernateCriterions.remove("tipoConta");
			
			hibernateCriterions.put("conta", Restrictions.eq("lancamento.conta.id", conta.getId()));
		}
	}

	public String getAgrupamento() {
		return agrupamento;
	}

	public void setAgrupamento(String agrupamento) {
		this.agrupamento = agrupamento;
	
		if (agrupamento != null && !agrupamento.isEmpty()) {
			// Remove os agrupamentos anteriormente setados
			hibernateCriterions.remove("categoria");
			hibernateCriterions.remove("favorecido");
			hibernateCriterions.remove("meiopagamento");
			hibernateCriterions.remove("moeda");
			
			switch(agrupamento) {
				case "CAT" : hibernateCriterions.put("categoria", Restrictions.eq("lancamento.categoria.id", idAgrupamento)); break;
				case "FAV" : hibernateCriterions.put("favorecido", Restrictions.eq("lancamento.favorecido.id", idAgrupamento)); break;
				case "PAG" : hibernateCriterions.put("meiopagamento", Restrictions.eq("lancamento.meioPagamento.id", idAgrupamento)); break;
				case "MOE" : hibernateCriterions.put("moeda", Restrictions.eq("lancamento.moeda.id", idAgrupamento)); break;
				default:
			}
		}
		
	}

	public TipoConta[] getTipoConta() {
		return tipoConta;
	}

	public void setTipoConta(TipoConta[] tipoConta) {
		this.tipoConta = tipoConta;
		
		// Caso conta esteja setado, remove para dar lugar a tipoConta
		if (tipoConta != null && tipoConta.length > 0) {
			if (hibernateCriterions.containsKey("conta"))
				hibernateCriterions.remove("conta");
			
			hibernateCriterions.put("tipoConta", Restrictions.in("con.tipoConta", tipoConta));
		}
	}

	public StatusLancamentoConta[] getStatusLancamentoConta() {
		return statusLancamentoConta;
	}

	public void setStatusLancamentoConta(
			StatusLancamentoConta[] statusLancamentoConta) {
		this.statusLancamentoConta = statusLancamentoConta;
		
		if (statusLancamentoConta != null && statusLancamentoConta.length > 0) {
			hibernateCriterions.put("statusLancamentoConta", Restrictions.in("lancamento.statusLancamentoConta", statusLancamentoConta));
		}
	}

	public List<Criterion> buildCriteria() {
		return new ArrayList<Criterion>(hibernateCriterions.values());
	}

	public Long getIdAgrupamento() {
		return idAgrupamento;
	}

	public void setIdAgrupamento(Long idAgrupamento) {
		this.idAgrupamento = idAgrupamento;
	}

	public Integer getLimiteResultado() {
		return limiteResultado;
	}

	public void setLimiteResultado(Integer limiteResultado) {
		this.limiteResultado = limiteResultado;
	}
}
