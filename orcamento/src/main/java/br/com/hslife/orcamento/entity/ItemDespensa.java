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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="itemdespensa")
@SuppressWarnings("serial")
public class ItemDespensa extends EntityPersistence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;	
	
	@Column(length=50, nullable=false)	
	private String descricao;
	
	@Column
	private String caracteristicas;
	
	@Column(nullable=false)
	private int quantidadeAtual;
	
	@Column
	private boolean arquivado;
	
	@Column(nullable=false)
	private int quantidadeVerde;
	
	@Column(nullable=false)
	private int quantidadeAmarelo;
	
	@Column(nullable=false)
	private int quantidadeVermelho;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=true)
	private Date validade;
	
	@Column(nullable=false, precision=18, scale=2)
	private double valor;
	
	@Column(length=50, nullable=true)	
	private String marca;
	
	@Column
	private boolean perecivel;
	
	@ManyToOne
	@JoinColumn(name="idUnidadeMedida")
	private UnidadeMedida unidadeMedida;
	
	@ManyToOne
	@JoinColumn(name="idDespensa")
	private Despensa despensa;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)
	@JoinTable(name="itemdespensa_movimentoitemdespensa", schema="orcamento")
	private List<MovimentoItemDespensa> movimentacao;
	
	public ItemDespensa() {
		movimentacao = new ArrayList<MovimentoItemDespensa>();
	}	
	
	@Override
	public String getLabel() {
		return this.descricao;
	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Marca favorita", marca, 50);
		EntityPersistenceUtil.validaCampoNulo("Despensa", this.despensa);
		EntityPersistenceUtil.validaCampoNulo("Unidade de medida", this.unidadeMedida);
		
		if (this.quantidadeVerde < 0 || this.quantidadeAmarelo < 0 || this.quantidadeVermelho < 0) {
			throw new ValidationException("Quantidade 'Verde', 'Amarelo' ou 'Vermelho' não pode ser menor que 0!");
		}
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

	public String getCaracteristicas() {
		return caracteristicas;
	}

	public void setCaracteristicas(String caracteristicas) {
		this.caracteristicas = caracteristicas;
	}

	public int getQuantidadeAtual() {
		return quantidadeAtual;
	}

	public void setQuantidadeAtual(int quantidadeAtual) {
		this.quantidadeAtual = quantidadeAtual;
	}

	public boolean isArquivado() {
		return arquivado;
	}

	public void setArquivado(boolean arquivado) {
		this.arquivado = arquivado;
	}

	public int getQuantidadeVerde() {
		return quantidadeVerde;
	}

	public void setQuantidadeVerde(int quantidadeVerde) {
		this.quantidadeVerde = quantidadeVerde;
	}

	public int getQuantidadeAmarelo() {
		return quantidadeAmarelo;
	}

	public void setQuantidadeAmarelo(int quantidadeAmarelo) {
		this.quantidadeAmarelo = quantidadeAmarelo;
	}

	public int getQuantidadeVermelho() {
		return quantidadeVermelho;
	}

	public void setQuantidadeVermelho(int quantidadeVermelho) {
		this.quantidadeVermelho = quantidadeVermelho;
	}

	public UnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public Despensa getDespensa() {
		return despensa;
	}

	public void setDespensa(Despensa despensa) {
		this.despensa = despensa;
	}

	public List<MovimentoItemDespensa> getMovimentacao() {
		return movimentacao;
	}

	public void setMovimentacao(List<MovimentoItemDespensa> movimentacao) {
		this.movimentacao = movimentacao;
	}

	public Date getValidade() {
		return validade;
	}

	public void setValidade(Date validade) {
		this.validade = validade;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public boolean isPerecivel() {
		return perecivel;
	}

	public void setPerecivel(boolean perecivel) {
		this.perecivel = perecivel;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}
}
