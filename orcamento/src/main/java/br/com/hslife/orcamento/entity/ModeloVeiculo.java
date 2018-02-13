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

package br.com.hslife.orcamento.entity;import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="modeloveiculo")
public class ModeloVeiculo extends EntityPersistence {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7974352882681899742L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;	
	
	@Column(length=50, nullable=false)	
	private String descricao;
	
	@Column
	private double potencia;

	@Column
	private int anoModelo;
	
	@Column
	private int cilindradas;

	@Column	
	private int numeroCilindros;

	@Column	
	private int numeroPortas;
	
	@Column(length=255)	
	private String detalhesModeloVeiculo;

	@ManyToOne
	@JoinColumn(name="idTipoVeiculo", nullable=false)
	private TipoVeiculo tipoVeiculo;

	@ManyToOne
	@JoinColumn(name="idMontadora", nullable=false)
	private Montadora montadora;
	
	@ManyToOne
	@JoinColumn(name="idCombustivel", nullable=false)
	private Combustivel combustivel;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	public ModeloVeiculo() {

	}	
	
	@Override
	public String getLabel() {
		return this.descricao;
	}
	
	@Override
	public void validate() {
		
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", descricao, 50);
		
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

	public double getPotencia() {
		return potencia;
	}

	public void setPotencia(double potencia) {
		this.potencia = potencia;
	}

	public int getAnoModelo() {
		return anoModelo;
	}

	public void setAnoModelo(int anoModelo) {
		this.anoModelo = anoModelo;
	}

	public int getCilindradas() {
		return cilindradas;
	}

	public void setCilindradas(int cilindradas) {
		this.cilindradas = cilindradas;
	}

	public int getNumeroCilindros() {
		return numeroCilindros;
	}

	public void setNumeroCilindros(int numeroCilindros) {
		this.numeroCilindros = numeroCilindros;
	}

	public int getNumeroPortas() {
		return numeroPortas;
	}

	public void setNumeroPortas(int numeroPortas) {
		this.numeroPortas = numeroPortas;
	}

	public String getDetalhesModeloVeiculo() {
		return detalhesModeloVeiculo;
	}

	public void setDetalhesModeloVeiculo(String detalhesModeloVeiculo) {
		this.detalhesModeloVeiculo = detalhesModeloVeiculo;
	}

	public TipoVeiculo getTipoVeiculo() {
		return tipoVeiculo;
	}

	public void setTipoVeiculo(TipoVeiculo tipoVeiculo) {
		this.tipoVeiculo = tipoVeiculo;
	}

	public Montadora getMontadora() {
		return montadora;
	}

	public void setMontadora(Montadora montadora) {
		this.montadora = montadora;
	}

	public Combustivel getCombustivel() {
		return combustivel;
	}

	public void setCombustivel(Combustivel combustivel) {
		this.combustivel = combustivel;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}	
}
