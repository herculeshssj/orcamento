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

package br.com.hslife.orcamento.entity;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="lancamentoveiculo")
public class LancamentoVeiculo extends EntityPersistence {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9179853728878780335L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;	
	
	@Column(length=50, nullable=false)	
	private String descricao;
	
	@Column(precision=18, scale=2)
	private double kilometragem;
	
	@Column(nullable=false, precision=18, scale=2)
	private double valor;
	
	@Column(nullable=true, precision=18, scale=2)	
	private double quantidadeCombustivel;
	
	@Column
	private boolean tanqueCheio;
	
	@Column(nullable=true)
	@Temporal(TemporalType.DATE)	
	private Date dataVencimento;
	
	@Column(nullable=false)
	@Temporal(TemporalType.DATE)	
	private Date dataPagamento;
	
	@ManyToOne
	@JoinColumn(name="idCombustivel", nullable=true)	
	private Combustivel combustivel;
	
	@ManyToOne
	@JoinColumn(name="idTipoLancamentoVeiculo", nullable=false)	
	private TipoLancamentoVeiculo tipoLancamentoVeiculo;
	
	public LancamentoVeiculo() {

	}	
	
	@Override
	public String getLabel() {
		return this.descricao;
	}
	
	@Override
	public void validate() {
		
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", descricao, 50);
		
		EntityPersistenceUtil.validaCampoNulo("Data de pagamento", this.dataPagamento);
				
		EntityPersistenceUtil.validaCampoNulo("Tipo de lançamento", this.tipoLancamentoVeiculo);
	}

	public Long getId() {
		return id;
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

	public double getKilometragem() {
		return kilometragem;
	}

	public void setKilometragem(double kilometragem) {
		this.kilometragem = kilometragem;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public double getQuantidadeCombustivel() {
		return quantidadeCombustivel;
	}

	public void setQuantidadeCombustivel(double quantidadeCombustivel) {
		this.quantidadeCombustivel = quantidadeCombustivel;
	}

	public boolean isTanqueCheio() {
		return tanqueCheio;
	}

	public void setTanqueCheio(boolean tanqueCheio) {
		this.tanqueCheio = tanqueCheio;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public Combustivel getCombustivel() {
		return combustivel;
	}

	public void setCombustivel(Combustivel combustivel) {
		this.combustivel = combustivel;
	}

	public TipoLancamentoVeiculo getTipoLancamentoVeiculo() {
		return tipoLancamentoVeiculo;
	}

	public void setTipoLancamentoVeiculo(TipoLancamentoVeiculo tipoLancamentoVeiculo) {
		this.tipoLancamentoVeiculo = tipoLancamentoVeiculo;
	}
}
