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

package br.com.hslife.orcamento.entity;

import java.util.Calendar;
import java.util.Date;
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
		return this.descricao;
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
	
	public void criaResumoInvestimento(Date data) {
		// A partir de um objeto Calendar, pega o mês e ano do Date para
		// passar ao método em questão
		Calendar temp = Calendar.getInstance();
		temp.setTime(data);
		
		// Chama o método propriamente dito
		this.criaResumoInvestimento(temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
	}
	
	public void criaResumoInvestimento(int mes, int ano) {
		// Verifica se existe resumo de investimento para o mês e ano informado
		if (existeResumoInvestimento(mes, ano)) {
			// Lança um BusinessException, pois não se pode criar um resumo com
			// mês/ano informados
			throw new BusinessException("Não é possível criar! Já existe resumo com o mês/ano informados!");
		}
		
		// Cria um novo resumo
		ResumoInvestimento resumo = new ResumoInvestimento(mes, ano);
		
		// Insere o resumo no List
		this.resumosInvestimento.add(resumo);
	}
	
	private boolean existeResumoInvestimento(int mes, int ano) {
		boolean encontrou = false;
		// Varre o List de resumos a procura de um resumo com o mês/ano informados
		for (ResumoInvestimento resumo : this.resumosInvestimento) {
			if (resumo.getMes() == mes & resumo.getAno() == ano)
				return true; // encontrou
		}
		return encontrou;
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
}