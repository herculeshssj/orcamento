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

package br.com.hslife.orcamento.entity;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import br.com.hslife.orcamento.enumeration.TipoInvestimento;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.model.InfoCotacao;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;
import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="investimento")
@SuppressWarnings("serial")
public class Investimento extends EntityPersistence {
	
	@Column(length=50, nullable=false)
	private String descricao;
	
	@Column(length=14, nullable=true)
	private String cnpj;
	
	@Column(nullable=false)
	@Temporal(TemporalType.DATE)
	private Date inicioInvestimento;
	
	@Column(nullable=true)
	@Temporal(TemporalType.DATE)
	private Date terminoInvestimento;
	
	@Column(length=10, nullable=true)
	private String ticker;
	
	@Column(columnDefinition="text", nullable=true)
	private String observacao;
	
	@ManyToOne
	@JoinColumn(name="idConta", nullable=false)
	private Conta conta;
	
	@ManyToOne
	@JoinColumn(name="idCategoriaInvestimento", nullable=false)
	private CategoriaInvestimento categoriaInvestimento;
	
	@OneToMany(fetch=FetchType.EAGER, orphanRemoval=true, cascade=CascadeType.ALL)	
	private Set<MovimentacaoInvestimento> movimentacoesInvestimento;
	
	/*** Atributos para o resumo Carteira de Investimento ***/
	
	@Transient
	private InfoCotacao infoCotacao;
	
	@Transient
	private double percentualInvestimento;
	
	@Transient
	private double valorInvestimentoAtualizado;
	
	/*** Atributos para o resumo Carteira de Investimento ***/
	
	public Investimento() {
		movimentacoesInvestimento = new LinkedHashSet<>();		
	}
	
	@Override
	public String getLabel() {
		return (this.isAtivo() ? "" : "[INATIVO] ") + this.descricao;
	}

	@Override
	public void validate() {
		// Limpa a formatação do CNPJ
		this.cnpj = this.cnpj.replace(".", "").replace("/", "").replace("-", "");
		
		EntityPersistenceUtil.validaCampoNulo("Conta", this.conta);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Código da ação", this.ticker, 10);
		EntityPersistenceUtil.validaTamanhoExatoCampoStringOpcional("CNPJ", this.cnpj, 14);
		EntityPersistenceUtil.validaCampoNulo("Início do investimento", this.inicioInvestimento);
		EntityPersistenceUtil.validaCampoNulo("Categoria de Investimento", this.categoriaInvestimento);
		
		try {
			CNPJValidator validatorCnpj = new CNPJValidator();
			
			if (this.getCnpj() != null && !this.getCnpj().trim().isEmpty() && this.getCnpj().length() != 0) {
				validatorCnpj.assertValid(this.getCnpj());				
			}			
			
		} catch (InvalidStateException ise) {
			throw new ValidationException(ise);
		}
	}
	
	public String getCnpjFormatado() {
		if (this.cnpj != null && this.cnpj.length() != 0)
			return Util.formatarCNPJ(this.cnpj);
		else
			return this.cnpj;
	}
	
	public void movimentarInvestimento(MovimentacaoInvestimento movimentacao) {
		// Valida a movimentação para se certificar que está válida
		movimentacao.validate();
		
		// Insere a movimentação no Set
		this.movimentacoesInvestimento.add(movimentacao);
	}

	public void movimentarInvestimento(TipoLancamento tipo, String historico, Date data, double valor) {
		// Cria uma nova movimentação, popula os campos e faz a validação
		MovimentacaoInvestimento movimentacao = new MovimentacaoInvestimento(tipo, historico, data, valor);
		movimentacao.validate();
		
		// Insere a movimentação no Set
		this.movimentacoesInvestimento.add(movimentacao);
		
	}
	
	public void excluirMovimentacao(MovimentacaoInvestimento movimentacao) {
		for (Iterator<MovimentacaoInvestimento> i = movimentacoesInvestimento.iterator(); i.hasNext(); ) {
			MovimentacaoInvestimento m = i.next();
			if (m.equals(movimentacao)) {
				i.remove();
				break;
			}
		}
	}
	
	public void investimentoInicial(Date inicioInvestimento, double valorInicial) {
		// Verifica se a data não é nula e o valor inicial é maior que zero.
		if (inicioInvestimento == null) 
			throw new ValidationException("Não é possível continuar! Data informada é inválida!");
		
		if (this.categoriaInvestimento.getTipoInvestimento().equals(TipoInvestimento.FIXO) && valorInicial <= 0) 
			throw new ValidationException("Valor inicial deve ser maior que zero!");
		
		// Por se tratar de um investimento inicial, o List e o Set são reiniciados
		movimentacoesInvestimento = new LinkedHashSet<>();
		
		// Cria uma nova movimentação
		this.movimentarInvestimento(TipoLancamento.RECEITA, "APLICAÇÃO", inicioInvestimento, valorInicial);
	}
	
	public Set<MovimentacaoInvestimento> buscarMovimentacoesInvestimento(Integer mes, int ano) {
		Set<MovimentacaoInvestimento> movimentacoes = new LinkedHashSet<>();
		
		// Itera o set de movimentações, adicionando as movimentações cuja data pertence ao mês/ano informados
		Calendar temp = Calendar.getInstance();
		for (MovimentacaoInvestimento movimentacao : this.movimentacoesInvestimento) {
			temp.setTime(movimentacao.getData());
			
			if (mes == null || mes == 0) {
				// Busca somente pelo ano
				if (temp.get(Calendar.YEAR) == ano)
					movimentacoes.add(movimentacao);
			} else {
				if ((temp.get(Calendar.MONTH) + 1) == mes && temp.get(Calendar.YEAR) == ano) 
					movimentacoes.add(movimentacao);
			}
		}
		
		return movimentacoes;
	}
	
	/**
	 * Atributo usado pela interface para dizer se o investimento está
	 * ativo ou não a partir da data de término do investimento. 
	 */
	public boolean isAtivo() {
		if (this.terminoInvestimento != null)
			return false;
		
		return true;
	}
	
	/*
	 * Obtém o total de cotas do investimento variável
	 */
	public double getTotalCotas() {
		int cotas = 0;
		if (this.getCategoriaInvestimento().getTipoInvestimento().equals(TipoInvestimento.VARIAVEL)) {
			for (MovimentacaoInvestimento movimentacao : this.getMovimentacoesInvestimento()) {
				if (movimentacao.getTipoLancamento().equals(TipoLancamento.RECEITA)) {
					cotas += movimentacao.getCotas();
				} else {
					cotas -= movimentacao.getCotas();
				}
			}
		}
		
		return cotas;
	}
	
	public double getPrecoMedio() {
		double precoMedio = 0.0;
		if (this.getCategoriaInvestimento().getTipoInvestimento().equals(TipoInvestimento.VARIAVEL)) {
			double totalGasto = 0.0;
			double totalCotas = 0;
			
			for (MovimentacaoInvestimento movimentacao : this.getMovimentacoesInvestimento()) {
				if (movimentacao.getTipoLancamento().equals(TipoLancamento.RECEITA)) {
					totalGasto += movimentacao.getValorTotalRendaVariavel();
					totalCotas += movimentacao.getCotas();
				} else {
					totalGasto -= movimentacao.getValorTotalRendaVariavel();
					totalCotas -= movimentacao.getCotas();
				}
			}
			
			// Verifica se as variáveis são zero para não ocorrer divisão por zero
			if (totalGasto != 0 & totalCotas != 0)
				precoMedio = Util.arredondar(totalGasto / totalCotas);
		}
		
		return precoMedio;
	}
	
	public double getSaldoCapitalizado() {
		double saldo = 0.0;
		
		if (this.getCategoriaInvestimento().getTipoInvestimento().equals(TipoInvestimento.FIXO)) {
			for (MovimentacaoInvestimento movimentacao : this.getMovimentacoesInvestimento()) {
				if (movimentacao.getTipoLancamento().equals(TipoLancamento.RECEITA)) {
					saldo += movimentacao.getValorTotalRendaFixa();
				} else {
					saldo -= movimentacao.getValorTotalRendaFixa();
				}
			}
		}
		
		return saldo;
	}

	public double getValorInvestimento() {
		if (this.categoriaInvestimento.getTipoInvestimento().equals(TipoInvestimento.FIXO))
			return this.getSaldoCapitalizado();
		else
			return this.getTotalCotas() * this.getPrecoMedio();
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public Date getInicioInvestimento() {
		return inicioInvestimento;
	}

	public void setInicioInvestimento(Date inicioInvestimento) {
		this.inicioInvestimento = inicioInvestimento;
	}

	public Date getTerminoInvestimento() {
		return terminoInvestimento;
	}

	public void setTerminoInvestimento(Date terminoInvestimento) {
		this.terminoInvestimento = terminoInvestimento;
	}

	public Set<MovimentacaoInvestimento> getMovimentacoesInvestimento() {
		return movimentacoesInvestimento;
	}

	public void setMovimentacoesInvestimento(Set<MovimentacaoInvestimento> movimentacoesInvestimento) {
		this.movimentacoesInvestimento = movimentacoesInvestimento;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public CategoriaInvestimento getCategoriaInvestimento() {
		return categoriaInvestimento;
	}

	public void setCategoriaInvestimento(CategoriaInvestimento categoriaInvestimento) {
		this.categoriaInvestimento = categoriaInvestimento;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public InfoCotacao getInfoCotacao() {
		return infoCotacao;
	}

	public void setInfoCotacao(InfoCotacao infoCotacao) {
		this.infoCotacao = infoCotacao;
	}

	public double getPercentualInvestimento() {
		return percentualInvestimento;
	}

	public void setPercentualInvestimento(double percentualInvestimento) {
		this.percentualInvestimento = percentualInvestimento;
	}

	public double getValorInvestimentoAtualizado() {
		return valorInvestimentoAtualizado;
	}

	public void setValorInvestimentoAtualizado(double valorInvestimentoAtualizado) {
		this.valorInvestimentoAtualizado = valorInvestimentoAtualizado;
	}
}
