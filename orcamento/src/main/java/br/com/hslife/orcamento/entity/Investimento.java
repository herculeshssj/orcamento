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

package br.com.hslife.orcamento.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import br.com.hslife.orcamento.enumeration.TipoInvestimento;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="investimento")
@SuppressWarnings("serial")
public class Investimento extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="idBanco", nullable=false)
	private Banco banco;
	
	@Column(length=10, nullable=false)
	@Enumerated(EnumType.STRING)
	private TipoInvestimento tipoInvestimento;
	
	@Column(length=50, nullable=false)
	private String descricao;
	
	@Column(length=14, nullable=false)
	private String cnpj;
	
	@Column(nullable=false)
	@Temporal(TemporalType.DATE)
	private Date inicioInvestimento;
	
	@Column(nullable=true)
	@Temporal(TemporalType.DATE)
	private Date terminoInvestimento;
	
	@Column(columnDefinition="text", nullable=true)
	private String observacao;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	@OneToMany(fetch=FetchType.EAGER, orphanRemoval=true, cascade=CascadeType.ALL)	
	private List<ResumoInvestimento> resumosInvestimento;
	
	@OneToMany(fetch=FetchType.EAGER, orphanRemoval=true, cascade=CascadeType.ALL)	
	private Set<MovimentacaoInvestimento> movimentacoesInvestimento;
	
	public Investimento() {
		resumosInvestimento = new LinkedList<>();
		movimentacoesInvestimento = new LinkedHashSet<>();		
	}
	
	@Override
	public String getLabel() {
		return this.descricao + (this.isAtivo() ? "" : " - INATIVO");
	}

	@Override
	public void validate() {
		EntityPersistenceUtil.validaCampoNulo("Banco", this.banco);
		EntityPersistenceUtil.validaCampoNulo("Tipo de investimento", this.tipoInvestimento);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 50);
		EntityPersistenceUtil.validaTamanhoExatoCampoStringObrigatorio("CNPJ", this.cnpj, 14);
		EntityPersistenceUtil.validaCampoNulo("Início do investimento", this.inicioInvestimento);
		
		try {
			CNPJValidator validatorCnpj = new CNPJValidator();
			
			if (this.getCnpj() != null && !this.getCnpj().trim().isEmpty()) {
				validatorCnpj.assertValid(this.getCnpj());				
			}			
			
		} catch (InvalidStateException ise) {
			throw new ValidationException(ise);
		}
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
	
	public void criaResumoInvestimento(Date data) {
		// A partir de um objeto Calendar, pega o mês e ano do Date para
		// passar ao método em questão
		Calendar temp = Calendar.getInstance();
		temp.setTime(data);
		
		// Chama o método propriamente dito
		this.criaResumoInvestimento(temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
	}
	
	public void criaResumoInvestimento(int mes, int ano) {
		// Cria um novo resumo
		ResumoInvestimento resumo = new ResumoInvestimento(mes, ano);
		this.criarResumoInvestimento(resumo);
	}
	
	public void criarResumoInvestimento(ResumoInvestimento resumo) {
		// Verifica se existe resumo de investimento para o mês e ano informado
		if (existeResumoInvestimento(resumo.getMes(), resumo.getAno())) {
			// Lança um BusinessException, pois não se pode criar um resumo com
			// mês/ano informados
			throw new BusinessException("Não é possível criar! Já existe resumo com o mês/ano informados!");
		}
		
		// Insere o resumo no List
		this.resumosInvestimento.add(resumo);
	}
	
	public void investimentoInicial(Date inicioInvestimento, double valorInicial) {
		// Verifica se a data não é nula e o valor inicial é maior que zero.
		if (inicioInvestimento == null) 
			throw new ValidationException("Não é possível continuar! Data informada é inválida!");
		
		if (valorInicial <= 0) 
			throw new ValidationException("Valor inicial deve ser maior que zero!");
		
		// Por se tratar de um investimento inicial, o List e o Set são reiniciados
		resumosInvestimento = new LinkedList<>();
		movimentacoesInvestimento = new LinkedHashSet<>();
		
		// Cria um novo resumo
		this.criaResumoInvestimento(inicioInvestimento);
		
		// Cria uma nova movimentação
		this.movimentarInvestimento(TipoLancamento.RECEITA, "APLICAÇÃO", inicioInvestimento, valorInicial);
	}
	
	private boolean existeResumoInvestimento(int mes, int ano) {		
		if (this.buscarResumoInvestimento(mes, ano) != null)
			return true;
		
		return false;
	}
	
	public ResumoInvestimento buscarResumoInvestimento(int mes, int ano) {
		// Itera todo o list de resumo, retornando aquele que tem o mês e ano selecionados
		// Caso não encontre, retorna nulo
		for (ResumoInvestimento resumo : this.resumosInvestimento) {
			if (resumo.getMes() == mes & resumo.getAno() == ano)
				return resumo;
		}
		return null;
	}
	
	public Set<MovimentacaoInvestimento> buscarMovimentacoesInvestimento(int mes, int ano) {
		Set<MovimentacaoInvestimento> movimentacoes = new LinkedHashSet<>();
		
		// Itera o set de movimentações, adicionando as movimentações cuja data pertence ao mês/ano informados
		Calendar temp = Calendar.getInstance();
		for (MovimentacaoInvestimento movimentacao : this.movimentacoesInvestimento) {
			temp.setTime(movimentacao.getData());
			
			if ((temp.get(Calendar.MONTH) + 1) == mes && temp.get(Calendar.YEAR) == ano) 
				movimentacoes.add(movimentacao);
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
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	public TipoInvestimento getTipoInvestimento() {
		return tipoInvestimento;
	}

	public void setTipoInvestimento(TipoInvestimento tipoInvestimento) {
		this.tipoInvestimento = tipoInvestimento;
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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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

	public List<ResumoInvestimento> getResumosInvestimento() {
		return resumosInvestimento;
	}

	public void setResumosInvestimento(List<ResumoInvestimento> resumosInvestimento) {
		this.resumosInvestimento = resumosInvestimento;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
}