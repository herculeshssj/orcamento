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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.hslife.orcamento.exception.BusinessException;

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
	
	@Column
	private boolean perecivel;
	
	@ManyToOne
	@JoinColumn(name="idUnidadeMedida")
	private UnidadeMedida unidadeMedida;
	
	@ManyToOne
	@JoinColumn(name="idDespensa")
	private Despensa despensa;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval=true)	
	private List<MovimentoItemDespensa> movimentacao;
	
	/*** Aplicando o padrão Builder para eliminar o construtor com n parâmetros ***/
	
	public static class Builder {
		private String descricao = "";
		private String caracteristicas = "";
		private Despensa despensa = new Despensa();
		private UnidadeMedida unidadeMedida = new UnidadeMedida();
		private int quantidade = 0;
		private double valor = 0.0;
		private boolean perecivel = false;

		public Builder() {}
		
		public Builder descricao(String valor) {this.descricao = valor; return this;}
		public Builder caracteristicas(String valor) {this.caracteristicas = valor; return this;}
		public Builder despensa(Despensa valor) {this.despensa = valor; return this;}
		public Builder unidadeMedida(UnidadeMedida valor) {this.unidadeMedida = valor; return this;}
		public Builder quantidade(int valor) {this.valor = valor; return this;}
		public Builder valor(double valor) {this.valor = valor; return this;}
		public Builder perecivel(boolean valor) {this.perecivel = valor; return this;}
		
		public ItemDespensa build() {
			return new ItemDespensa(this);
		}
	}
	
	/*** Construtor privado para receber o Builder ***/
	private ItemDespensa(Builder builder) {
		this.descricao = builder.descricao;
		this.caracteristicas = builder.caracteristicas;
		this.despensa = builder.despensa;
		this.unidadeMedida = builder.unidadeMedida;
		this.quantidadeAtual = builder.quantidade;
		this.valor = builder.valor;
		this.perecivel = builder.perecivel;
	}
	
	public ItemDespensa() {
		movimentacao = new ArrayList<MovimentoItemDespensa>();
	}
	
	public ItemDespensa(String descricao, String caracteristicas, Despensa despensa, UnidadeMedida unidade, int quantidade, double valor) {
		this.descricao = descricao;
		this.caracteristicas = caracteristicas;
		this.despensa = despensa;
		this.unidadeMedida = unidade;
		this.quantidadeAtual = quantidade;
		this.valor = valor;
	}
	
	
	@Override
	public String getLabel() {
		return this.descricao;
	}
	
	@Override
	public void validate() throws BusinessException {
		if (this.descricao == null || this.descricao.trim().isEmpty()) {
			throw new BusinessException("Informe uma descrição!");
		}
		
		if (this.despensa == null) {
			throw new BusinessException("Informe a despensa!");
		}
		
		if (this.unidadeMedida == null) {
			throw new BusinessException("Informe a unidade de medida!");
		}
		
		if (this.quantidadeVerde < 0 || this.quantidadeAmarelo < 0 || this.quantidadeVermelho < 0) {
			throw new BusinessException("Quantidade 'Verde', 'Amarelo' ou 'Vermelho' não pode ser menor que 0!");
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
}