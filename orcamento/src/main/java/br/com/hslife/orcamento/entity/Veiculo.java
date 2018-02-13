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
@Table(name="veiculo")
public class Veiculo extends EntityPersistence {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8711872782821875658L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;	
	
	@Column(length=50, nullable=false)	
	private String descricao;
	
	@Column(length=10, nullable=true)	
	private String placa;
	
	@Column(length=10, nullable=true)	
	private String cor;
	
	@Column(nullable=false)	
	private int anoFabricacao;
	
	@Column(nullable=true, precision=18, scale=2)
	private double odometro;
	
	@Column(length=255, nullable=true)	
	private String observacao;

	@Column(length=30, nullable=true)	
	private String renavam;

	@Column(length=35, nullable=true)	
	private String chassis;

	@Column(length=20, nullable=true)	
	private String numeroMotor;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=false)	
	private Date dataCompra;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=true)	
	private Date dataVenda;
	
	@Column(nullable=false, precision=18, scale=2)
	private double valorPago;
	
	@Column(nullable=true, precision=18, scale=2)	
	private double valorVenda;
	
	@Column(nullable=true, precision=18, scale=2)	
	private double odometroCompra;
	
	@Column(nullable=true, precision=18, scale=2)	
	private double odometroVenda;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=false)	
	private Date garantia;

	@Column(length=255, nullable=true)	
	private String observacaoCompra;
	
	@Column(length=255, nullable=true)	
	private String observacaoVenda;

	@ManyToOne
	@JoinColumn(name="idComprador", nullable=true)	
	private Favorecido comprador;

	@ManyToOne
	@JoinColumn(name="idVendedor", nullable=false)	
	private Favorecido vendedor;
	
	@ManyToOne
	@JoinColumn(name="idMeioPagamentoCompra", nullable=false)	
	private MeioPagamento meioPagamentoCompra;
	
	@ManyToOne
	@JoinColumn(name="idMeioPagamentoVenda", nullable=true)	
	private MeioPagamento meioPagamentoVenda;
	
	@ManyToOne
	@JoinColumn(name="idSeguro", nullable=true)
	private Seguro seguro;
	
	@ManyToOne
	@JoinColumn(name="idModeloVeiculo", nullable=false)
	private ModeloVeiculo modeloVeiculo;

	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	public Veiculo() {

	}	
	
	@Override
	public String getLabel() {
		return this.descricao;
	}
		
	@Override
	public void validate() {
		
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", descricao, 50);
		
		EntityPersistenceUtil.validaCampoNulo("Data de compra", this.dataCompra);
		
		EntityPersistenceUtil.validaCampoNulo("Vendedor", this.vendedor);
		
		EntityPersistenceUtil.validaCampoNulo("Meio de pagamento - compra", this.meioPagamentoCompra);
		
		EntityPersistenceUtil.validaCampoNulo("Modelo de veículo", this.modeloVeiculo);		
		
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

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public int getAnoFabricacao() {
		return anoFabricacao;
	}

	public void setAnoFabricacao(int anoFabricacao) {
		this.anoFabricacao = anoFabricacao;
	}

	public double getOdometro() {
		return odometro;
	}

	public void setOdometro(double odometro) {
		this.odometro = odometro;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getRenavam() {
		return renavam;
	}

	public void setRenavam(String renavam) {
		this.renavam = renavam;
	}

	public String getChassis() {
		return chassis;
	}

	public void setChassis(String chassis) {
		this.chassis = chassis;
	}

	public String getNumeroMotor() {
		return numeroMotor;
	}

	public void setNumeroMotor(String numeroMotor) {
		this.numeroMotor = numeroMotor;
	}

	public Date getDataCompra() {
		return dataCompra;
	}

	public void setDataCompra(Date dataCompra) {
		this.dataCompra = dataCompra;
	}

	public Date getDataVenda() {
		return dataVenda;
	}

	public void setDataVenda(Date dataVenda) {
		this.dataVenda = dataVenda;
	}

	public double getValorPago() {
		return valorPago;
	}

	public void setValorPago(double valorPago) {
		this.valorPago = valorPago;
	}

	public double getValorVenda() {
		return valorVenda;
	}

	public void setValorVenda(double valorVenda) {
		this.valorVenda = valorVenda;
	}

	public double getOdometroCompra() {
		return odometroCompra;
	}

	public void setOdometroCompra(double odometroCompra) {
		this.odometroCompra = odometroCompra;
	}

	public double getOdometroVenda() {
		return odometroVenda;
	}

	public void setOdometroVenda(double odometroVenda) {
		this.odometroVenda = odometroVenda;
	}

	public Date getGarantia() {
		return garantia;
	}

	public void setGarantia(Date garantia) {
		this.garantia = garantia;
	}

	public String getObservacaoCompra() {
		return observacaoCompra;
	}

	public void setObservacaoCompra(String observacaoCompra) {
		this.observacaoCompra = observacaoCompra;
	}

	public String getObservacaoVenda() {
		return observacaoVenda;
	}

	public void setObservacaoVenda(String observacaoVenda) {
		this.observacaoVenda = observacaoVenda;
	}

	public Favorecido getComprador() {
		return comprador;
	}

	public void setComprador(Favorecido comprador) {
		this.comprador = comprador;
	}

	public Favorecido getVendedor() {
		return vendedor;
	}

	public void setVendedor(Favorecido vendedor) {
		this.vendedor = vendedor;
	}

	public MeioPagamento getMeioPagamentoCompra() {
		return meioPagamentoCompra;
	}

	public void setMeioPagamentoCompra(MeioPagamento meioPagamentoCompra) {
		this.meioPagamentoCompra = meioPagamentoCompra;
	}

	public MeioPagamento getMeioPagamentoVenda() {
		return meioPagamentoVenda;
	}

	public void setMeioPagamentoVenda(MeioPagamento meioPagamentoVenda) {
		this.meioPagamentoVenda = meioPagamentoVenda;
	}

	public Seguro getSeguro() {
		return seguro;
	}

	public void setSeguro(Seguro seguro) {
		this.seguro = seguro;
	}

	public ModeloVeiculo getModeloVeiculo() {
		return modeloVeiculo;
	}

	public void setModeloVeiculo(ModeloVeiculo modeloVeiculo) {
		this.modeloVeiculo = modeloVeiculo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}
