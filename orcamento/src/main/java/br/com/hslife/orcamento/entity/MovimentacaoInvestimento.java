/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a qualquer
    
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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="movimentacaoinvestimento")
@SuppressWarnings("serial")
public class MovimentacaoInvestimento extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=10, nullable=false)
	@Enumerated(EnumType.STRING)
	private TipoLancamento tipoLancamento;
	
	@Column(nullable=true)
	@Temporal(TemporalType.DATE)
	private Date data;
	
	@Column(length=50, nullable=false)
	private String historico;
	
	@Column(length=50, nullable=true)
	private String documento;
	
	@Column(nullable=false, precision=18, scale=2)
	private double valor;
	
	@Column(nullable=false, precision=18, scale=2)
	private double impostoRenda;
	
	@Column(nullable=false, precision=18, scale=2)
	private double iof;
	
	@Column(nullable=false, precision=18, scale=2)
	private double compensacaoImpostoRenda;
	
	@Column(nullable=false, precision=18, scale=6)
	private double cotas;
	
	@Column(nullable=false, precision=18, scale=9)
	private double valorCota;
	
	@Column(nullable=false, precision=18, scale=9)
	private double saldoCotas;
	
	public MovimentacaoInvestimento() {
		
	}
	
	public MovimentacaoInvestimento(TipoLancamento tipo, String historico, Date data, double valor) {
		this.tipoLancamento = tipo;
		this.historico = historico;
		this.data = data;
		this.valor = valor;
	}

	@Override
	public String getLabel() {
		return this.historico;
	}

	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Histórico", this.historico, 50);
		EntityPersistenceUtil.validaCampoNulo("Tipo de lançamento", this.tipoLancamento);
		EntityPersistenceUtil.validaCampoNulo("Data", this.data);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getHistorico() {
		return historico;
	}

	public void setHistorico(String historico) {
		this.historico = historico;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public double getImpostoRenda() {
		return impostoRenda;
	}

	public void setImpostoRenda(double impostoRenda) {
		this.impostoRenda = impostoRenda;
	}

	public double getIof() {
		return iof;
	}

	public void setIof(double iof) {
		this.iof = iof;
	}

	public double getValorCota() {
		return valorCota;
	}

	public void setValorCota(double valorCota) {
		this.valorCota = valorCota;
	}

	public double getSaldoCotas() {
		return saldoCotas;
	}

	public void setSaldoCotas(double saldoCotas) {
		this.saldoCotas = saldoCotas;
	}

	public TipoLancamento getTipoLancamento() {
		return tipoLancamento;
	}

	public void setTipoLancamento(TipoLancamento tipoLancamento) {
		this.tipoLancamento = tipoLancamento;
	}

	public double getCompensacaoImpostoRenda() {
		return compensacaoImpostoRenda;
	}

	public void setCompensacaoImpostoRenda(double compensacaoImpostoRenda) {
		this.compensacaoImpostoRenda = compensacaoImpostoRenda;
	}

	public double getCotas() {
		return cotas;
	}

	public void setCotas(double cotas) {
		this.cotas = cotas;
	}
}