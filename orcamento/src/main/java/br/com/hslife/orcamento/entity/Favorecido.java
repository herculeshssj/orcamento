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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import br.com.hslife.orcamento.enumeration.TipoPessoa;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;
import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="favorecido")
@SuppressWarnings("serial")
public class Favorecido extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=100, nullable=false)
	private String nome;
	
	@Column(columnDefinition="text", nullable=true)
	private String contato;
	
	@Column(length=10, nullable=false)
	@Enumerated(EnumType.STRING)
	private TipoPessoa tipoPessoa;
	
	@Column
	private boolean ativo;
	
	@Column
	private boolean padrao;
	
	@Column(length=14, nullable=true)
	private String cpfCnpj;
	
	@Column
	private boolean financeiro;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	@Transient
	private List<LancamentoConta> lancamentos;
	
	@Transient
	private double saldoPago;
	
	@Transient
	private double saldoCredito;
	
	@Transient
	private double saldoDebito;
	
	public Favorecido() {
		ativo = true;
		financeiro = true;
		saldoPago = 0.0;
		saldoCredito = 0.0;
		saldoDebito = 0.0;
	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Nome", this.nome, 100);
		EntityPersistenceUtil.validaCampoNulo("Tipo de pessoa", this.tipoPessoa);
		
		try {
			CPFValidator validatorCpf = new CPFValidator();
			CNPJValidator validatorCnpj = new CNPJValidator();
			
			if (this.getCpfCnpj() != null && !this.getCpfCnpj().trim().isEmpty()) {
				switch (this.getTipoPessoa()) {
					case FISICA : validatorCpf.assertValid(this.getCpfCnpj()); break;
					case JURIDICA : validatorCnpj.assertValid(this.getCpfCnpj()); break;
				}
			}			
			
		} catch (InvalidStateException ise) {
			throw new ValidationException(ise);
		}		
	}
	
	@Override
	public String getLabel() {
		return nome;
	}
	
	public String getSaldoPagoFormatado() {
		return Util.moedaBrasil(saldoPago);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getContato() {
		return contato;
	}

	public void setContato(String contato) {
		this.contato = contato;
	}

	public TipoPessoa getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(TipoPessoa tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
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

	public List<LancamentoConta> getLancamentos() {
		return lancamentos;
	}

	public void setLancamentos(List<LancamentoConta> lancamentos) {
		this.lancamentos = lancamentos;
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

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public boolean isFinanceiro() {
		return financeiro;
	}

	public void setFinanceiro(boolean financeiro) {
		this.financeiro = financeiro;
	}
}
