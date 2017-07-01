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

import br.com.hslife.orcamento.rest.json.LancamentoImportadoJson;
import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="lancamentoimportado")
public class LancamentoImportado extends EntityPersistence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2880755953845455507L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date data;
	
	@Column(length=200, nullable=true)
	private String historico;
	
	@Column(length=50, nullable=true)
	private String documento;
	
	@Column(nullable=false, precision=18, scale=2)
	private Double valor;
	
	@Column(length=32, nullable=false)
	private String hash;
	
	@Column(length=5, nullable=true)
	private String moeda;
	
	@Column(length=200, nullable=true)
	private String observacao;
	
	@Column(length=200, nullable=true)
	private String categoria;
	
	@Column(length=200, nullable=true)
	private String favorecido;
	
	@Column(length=200, nullable=true)
	private String meiopagamento;
	
	@Column(length=10, nullable=true)
	private String tipo;
	
	@ManyToOne
	@JoinColumn(name="idConta", nullable=false)
	private Conta conta;
	
	public LancamentoImportado() {
		
	}
	
	@Override
	public String getLabel() {
		return Util.suprimirTextoFim(Util.formataDataHora(this.data, Util.DATA) + " - R$ " + this.valor + " - " + this.historico, 55);
	}
	
	public String getFullLabel() {
		return Util.formataDataHora(this.data, Util.DATA) + " - " + this.moeda + " " + this.valor + " - " + this.historico;
	}
	
	public String hashForCSV(long i) {
		return new StringBuilder()
				.append(this.data.toString())
				.append(this.historico)
				.append(this.documento)
				.append(this.valor.toString())
				.append(this.moeda)
				.append(this.observacao)
				.append(this.categoria)
				.append(this.favorecido)
				.append(this.meiopagamento)
				.append(this.tipo)
				.append("[" + i + "]")
				.toString();
	}
	
	public String hashForCSV() {
		return this.hashForCSV(new Date().getTime());
	}
	
	public LancamentoImportado clonarLancamento() {
		LancamentoImportado li = new LancamentoImportado();
		li.setCategoria(this.categoria);
		li.setConta(this.conta);
		li.setData(this.data);
		li.setDocumento(this.documento);
		li.setFavorecido(this.favorecido);
		li.setHash(Util.MD5(this.hashForCSV()));
		li.setHistorico(this.historico);
		li.setMeiopagamento(this.meiopagamento);
		li.setMoeda(this.moeda);
		li.setObservacao(this.observacao);
		li.setTipo(this.tipo);
		li.setValor(this.valor);
		return li;
	}
	
	public LancamentoImportado clonarLancamento(long i) {
		LancamentoImportado li = this.clonarLancamento();
		li.setHash(Util.MD5(li.hashForCSV(i)));
		return li;
	}

	@Override
	public void validate() {
				
	}
	
	@Override
	public LancamentoImportadoJson toJson() {
		return new LancamentoImportadoJson();
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

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public String getMoeda() {
		return moeda;
	}

	public void setMoeda(String moeda) {
		this.moeda = moeda;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getFavorecido() {
		return favorecido;
	}

	public void setFavorecido(String favorecido) {
		this.favorecido = favorecido;
	}

	public String getMeiopagamento() {
		return meiopagamento;
	}

	public void setMeiopagamento(String meiopagamento) {
		this.meiopagamento = meiopagamento;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
}