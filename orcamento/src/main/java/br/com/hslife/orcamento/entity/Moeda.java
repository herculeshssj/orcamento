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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="moeda")
@SuppressWarnings("serial")
public class Moeda extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false, length=50)
	private String nome;
	
	@Column(nullable=false, length=50)
	private String pais;
	
	@Column(nullable=false, length=2)
	private String siglaPais;
	
	@Column(nullable=false, length=5)
	private String simboloMonetario;
	
	@Column(nullable=true, length=5)
	private String codigoMonetario;
	
	@Column(nullable=false, precision=18, scale=4)
	private double valorConversao;
	
	@Column
	private boolean padrao;
	
	@Column
	private boolean ativo;
	
	@ManyToOne
	@JoinColumn(name="idUsuario")
	private Usuario usuario;
	
	@Transient
	private List<LancamentoConta> lancamentos;
	
	@Transient
	private double compraSaque;
	
	@Transient
	private double parcelado;
	
	@Transient
	private double total;
	
	@Transient
	private double taxaConversao;
	
	@Transient
	private double totalConvertido;
	
	@Transient
	private double saldoPago;
	
	@Transient
	private double saldoCredito;
	
	@Transient
	private double saldoDebito;
	
	public Moeda() {
		ativo = true;
		saldoPago = 0.0;
		saldoCredito = 0.0;
		saldoDebito = 0.0;
		valorConversao = 1.0000;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return this.simboloMonetario + " - " + this.nome;
	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Nome da moeda", this.nome, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("País", this.pais, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Sigla do País", this.siglaPais, 2);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Símbolo monetário", this.siglaPais, 5);
		
		if (!this.padrao && this.valorConversao == 0) {
			throw new ValidationException("Informe um valor de conversão diferente de 0!");
		}
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getSiglaPais() {
		return siglaPais;
	}

	public void setSiglaPais(String siglaPais) {
		this.siglaPais = siglaPais;
	}

	public String getSimboloMonetario() {
		return simboloMonetario;
	}

	public void setSimboloMonetario(String simboloMonetario) {
		this.simboloMonetario = simboloMonetario;
	}

	public boolean isPadrao() {
		return padrao;
	}

	public void setPadrao(boolean padrao) {
		this.padrao = padrao;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<LancamentoConta> getLancamentos() {
		return lancamentos;
	}

	public void setLancamentos(List<LancamentoConta> lancamentos) {
		this.lancamentos = lancamentos;
	}

	public double getCompraSaque() {
		return compraSaque;
	}

	public void setCompraSaque(double compraSaque) {
		this.compraSaque = compraSaque;
	}

	public double getParcelado() {
		return parcelado;
	}

	public void setParcelado(double parcelado) {
		this.parcelado = parcelado;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getTaxaConversao() {
		return taxaConversao;
	}

	public void setTaxaConversao(double taxaConversao) {
		this.taxaConversao = taxaConversao;
	}

	public double getTotalConvertido() {
		return totalConvertido;
	}

	public void setTotalConvertido(double totalConvertido) {
		this.totalConvertido = totalConvertido;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public double getValorConversao() {
		return valorConversao;
	}

	public void setValorConversao(double valorConversao) {
		this.valorConversao = valorConversao;
	}

	public double getSaldoPago() {
		return saldoPago;
	}

	public void setSaldoPago(double saldoPago) {
		this.saldoPago = saldoPago;
	}

	public double getSaldoCredito() {
		return saldoCredito;
	}

	public void setSaldoCredito(double saldoCredito) {
		this.saldoCredito = saldoCredito;
	}

	public double getSaldoDebito() {
		return saldoDebito;
	}

	public void setSaldoDebito(double saldoDebito) {
		this.saldoDebito = saldoDebito;
	}

	public String getCodigoMonetario() {
		return codigoMonetario;
	}

	public void setCodigoMonetario(String codigoMonetario) {
		this.codigoMonetario = codigoMonetario;
	}
}
