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
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.hslife.orcamento.util.EntityPersistenceUtil;
import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="pagamentodividaterceiro")
@SuppressWarnings("serial")
public class PagamentoDividaTerceiro extends EntityPersistence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false, precision=18, scale=2)
	private double valorPago;
	
	@Column(nullable=false, precision=18, scale=4)
	private double taxaConversao;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date dataPagamento;
	
	@Column(columnDefinition="text")
	private String comprovantePagamento;
	
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="idArquivoComprovante", nullable=true)
	private Arquivo arquivoComprovante;
	
	@ManyToOne
	@JoinColumn(name="idDivida", nullable=false)
	private DividaTerceiro dividaTerceiro;
	
	@Transient
	private ModeloDocumento modeloDocumento;
	
	public PagamentoDividaTerceiro() {		
		taxaConversao = 1.0000;
	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaCampoNulo("Data do pagamento", this.dataPagamento);
	}

	@Override
	public String getLabel() {
		return Util.concatenar(
				"Pagamento no valor de ",
				this.dividaTerceiro.getMoeda().getSimboloMonetario(),
				" ",
				Double.toString(Util.arredondar(this.valorPago * this.taxaConversao)),
				", efetuado em ",
				Util.formataDataHora(this.dataPagamento, Util.DATA)
		);
	}
	
	public double getValorPagoConvertido() {
		return Util.arredondar(this.valorPago * this.taxaConversao);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getValorPago() {
		return valorPago;
	}

	public void setValorPago(double valorPago) {
		this.valorPago = valorPago;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public String getComprovantePagamento() {
		return comprovantePagamento;
	}

	public void setComprovantePagamento(String comprovantePagamento) {
		this.comprovantePagamento = comprovantePagamento;
	}

	public DividaTerceiro getDividaTerceiro() {
		return dividaTerceiro;
	}

	public void setDividaTerceiro(DividaTerceiro dividaTerceiro) {
		this.dividaTerceiro = dividaTerceiro;
	}

	public double getTaxaConversao() {
		return taxaConversao;
	}

	public void setTaxaConversao(double taxaConversao) {
		this.taxaConversao = taxaConversao;
	}

	public ModeloDocumento getModeloDocumento() {
		return modeloDocumento;
	}

	public void setModeloDocumento(ModeloDocumento modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
	}

	public Arquivo getArquivoComprovante() {
		return arquivoComprovante;
	}

	public void setArquivoComprovante(Arquivo arquivoComprovante) {
		this.arquivoComprovante = arquivoComprovante;
	}
}
