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
package br.com.hslife.orcamento.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.enumeration.CadastroSistema;
import br.com.hslife.orcamento.enumeration.StatusLancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoConta;

public class CriterioBuscaLancamentoConta {
	
	// Atributo que armazena os critérios selecionados
	private Map<String, Criterion> hibernateCriterions = new TreeMap<String, Criterion>();
	
	private Conta conta;
	private String descricao;
	private Date dataInicio;
	private Date dataFim;
	private Long idAgrupamento;
	private CadastroSistema cadastro;
	private TipoConta[] tipoConta;
	private StatusLancamentoConta[] statusLancamentoConta;	
	
	private Integer limiteResultado = 0;
	
	public void limparCriterios() {
		this.setDescricao(null);
		this.setCadastro(null);
		this.setTipoConta(null);
		this.setStatusLancamentoConta(null);
		this.setCadastro(null);
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
		
		// Remove a descrição anteriormente setada
		hibernateCriterions.remove("descricao");
		
		if (descricao != null && !descricao.isEmpty())
			hibernateCriterions.put("descricao", Restrictions.ilike("lancamento.descricao", descricao, MatchMode.ANYWHERE));
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
		
		// Remove a data de início anteriormente setada
		hibernateCriterions.remove("dataInicio");
		
		if (dataInicio != null)
			hibernateCriterions.put("dataInicio", Restrictions.ge("lancamento.dataPagamento", dataInicio));			
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
		
		// Remove a data final anteriormente setada
		hibernateCriterions.remove("dataFim");
		
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

	public CadastroSistema getCadastro() {
		return cadastro;
	}
	
	public void setCadastro(CadastroSistema cadastro) {
		this.cadastro = cadastro;
		this.atribuirParametrosAgrupamento();
	}
	
	private void atribuirParametrosAgrupamento() {
		// Remove os agrupamentos anteriormente setados
		hibernateCriterions.remove("categoria");
		hibernateCriterions.remove("favorecido");
		hibernateCriterions.remove("meiopagamento");
		hibernateCriterions.remove("moeda");
		
		if (cadastro != null) {			
			switch(cadastro) {
				case CATEGORIA : hibernateCriterions.put("categoria", Restrictions.eq("lancamento.categoria.id", idAgrupamento)); break;
				case FAVORECIDO : hibernateCriterions.put("favorecido", Restrictions.eq("lancamento.favorecido.id", idAgrupamento)); break;
				case MEIOPAGAMENTO : hibernateCriterions.put("meiopagamento", Restrictions.eq("lancamento.meioPagamento.id", idAgrupamento)); break;
				case MOEDA : hibernateCriterions.put("moeda", Restrictions.eq("lancamento.moeda.id", idAgrupamento)); break;
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
		if (tipoConta != null && tipoConta.length > 0 && this.containsValidObjects(tipoConta)) {
			if (hibernateCriterions.containsKey("conta"))
				hibernateCriterions.remove("conta");
			
			hibernateCriterions.put("tipoConta", Restrictions.in("con.tipoConta", Arrays.asList(tipoConta)));
		}
	}
	
	
	private boolean containsValidObjects(Object[] array) {
		int countValidObjects = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null) countValidObjects++;
		}
		return countValidObjects == 0 ? false : true; 
	}

	public StatusLancamentoConta[] getStatusLancamentoConta() {
		return statusLancamentoConta;
	}

	public void setStatusLancamentoConta(
			StatusLancamentoConta[] statusLancamentoConta) {
		this.statusLancamentoConta = statusLancamentoConta;
		
		hibernateCriterions.remove("statusLancamentoConta");
		
		if (statusLancamentoConta != null && statusLancamentoConta.length > 0 && this.containsValidObjects(statusLancamentoConta)) {
			hibernateCriterions.put("statusLancamentoConta", Restrictions.in("lancamento.statusLancamentoConta", Arrays.asList(statusLancamentoConta)));
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
		this.atribuirParametrosAgrupamento();
	}

	public Integer getLimiteResultado() {
		return limiteResultado;
	}

	public void setLimiteResultado(Integer limiteResultado) {
		this.limiteResultado = limiteResultado;
	}

	public int quantCriteriosDefinidos() {
		return hibernateCriterions.size();
	}
}
