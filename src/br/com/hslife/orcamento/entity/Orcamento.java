/***
  
  	Copyright (c) 2012 - 2015 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@gmail.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

import br.com.hslife.orcamento.enumeration.AbrangenciaOrcamento;
import br.com.hslife.orcamento.enumeration.PeriodoLancamento;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoOrcamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;
import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="orcamento")
@SuppressWarnings("serial")
public class Orcamento extends EntityPersistence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;	
	
	@Column(length=50, nullable=false)	
	private String descricao;
	
	@Column(length=10, nullable=false)
	@Enumerated(EnumType.STRING)
	private TipoOrcamento tipoOrcamento;
	
	@Column(length=10, nullable = true)
	@Enumerated(EnumType.STRING)
	private TipoConta tipoConta;
	
	@Column(nullable=false)
	@Temporal(TemporalType.DATE)
	private Date inicio;
	
	@Column(nullable=false)
	@Temporal(TemporalType.DATE)
	private Date fim;
	
	@Column(length=15, nullable=false)
	@Enumerated(EnumType.STRING)
	private PeriodoLancamento periodoLancamento;
	
	@Column(length=15, nullable=false)
	@Enumerated(EnumType.STRING)
	private AbrangenciaOrcamento abrangenciaOrcamento;
	
	@Column
	private boolean automatico;
	
	@Column
	private boolean arquivar;
	
	@Column
	private boolean ativo;
	
	@ManyToOne
	@JoinColumn(name="idConta", nullable=true)
	private Conta conta;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
	private List<DetalheOrcamento> detalhes;
	
	public Orcamento() {
		detalhes = new ArrayList<DetalheOrcamento>();
		ativo = true;
		tipoOrcamento = TipoOrcamento.SEMDISTINCAO;
		periodoLancamento = PeriodoLancamento.MENSAL;
		abrangenciaOrcamento = AbrangenciaOrcamento.CATEGORIA;
	}	
	
	@Override
	public String getLabel() {
		if (this.ativo)
			return this.descricao;
		else
			return this.descricao + " [INATIVO]";
	}
	
	public String getAlternativeLabel() {
		return "Orçamento " + this.periodoLancamento.toString() + " - " + Util.formataDataHora(inicio, Util.DATA) + " à " + Util.formataDataHora(fim, Util.DATA);
	}
	
	@Override
	public void validate() throws BusinessException {
		
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", descricao, 50);
		
		EntityPersistenceUtil.validaCampoNulo("Período de lançamento", periodoLancamento);
		
		EntityPersistenceUtil.validaCampoNulo("Início", inicio);
		
		EntityPersistenceUtil.validaCampoNulo("Fim", fim);
		
		EntityPersistenceUtil.validaCampoNulo("Usuário", usuario);
		
		if (detalhes == null || detalhes.isEmpty()) {
			throw new BusinessException("Entre com pelo menos um item nos detalhes!");
		}
		
		for (DetalheOrcamento detalhe : detalhes) {
			detalhe.validate();
		}
		
		// Valida o preenchimento do campo conta de acordo com o tipo de orçamento selecionado
		if (this.tipoOrcamento.equals(TipoOrcamento.CONTA)) {
			EntityPersistenceUtil.validaCampoNulo("Conta", this.conta);
		}
	}

	public Long getId() {
		return id;
	}
	
	/**
	 * Altera o status do orçamento de ativo para inativo, e vice-versa
	 */
	public void alterarStatus() {
		if (this.ativo) {
			this.ativo = false;
		} else {
			this.ativo = true;
		}
	}
	
	/**
	 * Gera um novo orçamento a partir das informações contidas no atual.
	 * As datas de início e fim são incrementadas de acordo com o valor
	 * do período de lançamento.
	 * Só é possível gerar um novo orçamento se o período de lançamento
	 * for diferente de FIXO. Um BusinessException será gerado ao se tentar
	 * gerar um.
	 * 
	 * @return o novo orçamento gerado
	 * @throws BusinessException
	 */
	public Orcamento gerarOrcamento() throws BusinessException {
		Orcamento novoOrcamento = new Orcamento();
		
		// Valida a entidade para saber se não tem inconsistências
		this.validate();
		
		/* Ajusta as datas de início e fim */
		
		// Ajusta a data de início
		Calendar tempInicio = Calendar.getInstance();
		tempInicio.setTime(this.fim);
		tempInicio.add(Calendar.DAY_OF_YEAR, 1);
		novoOrcamento.setInicio(tempInicio.getTime());
		
		// Ajusta a data de fim
		Calendar tempFim = Calendar.getInstance();
		tempFim.setTime(novoOrcamento.getInicio());
		
		switch (this.periodoLancamento) {
			case MENSAL : tempFim.add(Calendar.MONTH, 1); tempFim.add(Calendar.DAY_OF_YEAR, -1); break;
			case BIMESTRAL : tempFim.add(Calendar.MONTH, 2); tempFim.add(Calendar.DAY_OF_YEAR, -1); break;
			case TRIMESTRAL : tempFim.add(Calendar.MONTH, 3); tempFim.add(Calendar.DAY_OF_YEAR, -1); break;
			case QUADRIMESTRAL : tempFim.add(Calendar.MONTH, 4); tempFim.add(Calendar.DAY_OF_YEAR, -1); break;
			case SEMESTRAL : tempFim.add(Calendar.MONTH, 6); tempFim.add(Calendar.DAY_OF_YEAR, -1); break;
			case ANUAL : tempFim.add(Calendar.YEAR, 1); tempFim.add(Calendar.DAY_OF_YEAR, -1); break;
			default : throw new BusinessException("Não é possível gerar novos orçamentos a partir de um com período fixo!");
		}
		
		novoOrcamento.setFim(tempFim.getTime());
		
		/* Realiza a clonagem das informações */
		novoOrcamento.setAbrangenciaOrcamento(this.abrangenciaOrcamento);
		novoOrcamento.setArquivar(this.arquivar);
		novoOrcamento.setAtivo(this.ativo);
		novoOrcamento.setAutomatico(this.automatico);
		novoOrcamento.setConta(this.conta);
		novoOrcamento.setDescricao(this.descricao + " - " + Util.formataDataHora(novoOrcamento.getInicio(), Util.DATA));
		novoOrcamento.setPeriodoLancamento(this.periodoLancamento);
		novoOrcamento.setTipoConta(this.tipoConta);
		novoOrcamento.setTipoOrcamento(this.tipoOrcamento);
		novoOrcamento.setUsuario(this.usuario);
		
		DetalheOrcamento novoDetalhe;
		for (DetalheOrcamento detalhe : this.detalhes) {
			novoDetalhe = new DetalheOrcamento();
			novoDetalhe.setDescricao(detalhe.getDescricao());
			novoDetalhe.setIdEntity(detalhe.getIdEntity());
			novoDetalhe.setPrevisao(detalhe.getPrevisao());
			novoDetalhe.setPrevisaoCredito(detalhe.getPrevisaoCredito());
			novoDetalhe.setPrevisaoDebito(detalhe.getPrevisaoDebito());
			novoDetalhe.setRealizado(detalhe.getRealizado());
			novoDetalhe.setRealizadoCredito(detalhe.getRealizadoCredito());
			novoDetalhe.setRealizadoDebito(detalhe.getRealizadoDebito());
			novoDetalhe.setTipoCategoria(detalhe.getTipoCategoria());
			
			novoOrcamento.getDetalhes().add(novoDetalhe);
		}
		
		return novoOrcamento;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public TipoConta getTipoConta() {
		return tipoConta;
	}

	public void setTipoConta(TipoConta tipoConta) {
		this.tipoConta = tipoConta;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public PeriodoLancamento getPeriodoLancamento() {
		return periodoLancamento;
	}

	public void setPeriodoLancamento(PeriodoLancamento periodoLancamento) {
		this.periodoLancamento = periodoLancamento;
	}

	public AbrangenciaOrcamento getAbrangenciaOrcamento() {
		return abrangenciaOrcamento;
	}

	public void setAbrangenciaOrcamento(AbrangenciaOrcamento abrangenciaOrcamento) {
		this.abrangenciaOrcamento = abrangenciaOrcamento;
	}

	public boolean isAutomatico() {
		return automatico;
	}

	public void setAutomatico(boolean automatico) {
		this.automatico = automatico;
	}

	public boolean isArquivar() {
		return arquivar;
	}

	public void setArquivar(boolean arquivar) {
		this.arquivar = arquivar;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public List<DetalheOrcamento> getDetalhes() {
		return detalhes;
	}

	public void setDetalhes(List<DetalheOrcamento> detalhes) {
		this.detalhes = detalhes;
	}

	public TipoOrcamento getTipoOrcamento() {
		return tipoOrcamento;
	}

	public void setTipoOrcamento(TipoOrcamento tipoOrcamento) {
		this.tipoOrcamento = tipoOrcamento;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}	
}