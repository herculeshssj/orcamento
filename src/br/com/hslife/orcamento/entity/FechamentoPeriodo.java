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

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.hslife.orcamento.enumeration.OperacaoConta;
import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="fechamentoperiodo")
@SuppressWarnings("serial")
public class FechamentoPeriodo extends EntityPersistence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
	private String usuario;
	
	@Column(nullable=false, precision=18, scale=2)
	private double saldo;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date data;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date dataAlteracao;
	
	@Column
	private int mes;
	
	@Column
	private int ano;
	
	@Column(length=10, nullable=false)
	@Enumerated(EnumType.STRING)
	private OperacaoConta operacao;
	
	@ManyToOne
	@JoinColumn(name="idContaBancaria", nullable=false)
	private Conta conta;
	
	public FechamentoPeriodo() {
		dataAlteracao = new Date();
	}

	public Long getId() {
		return id;
	}
	
	@Override
	public String getLabel() {
		switch (this.operacao) {
			case ABERTURA : return "Perído aberto em " + Util.formataDataHora(this.data, Util.DATA);
			case FECHAMENTO : return "Período fechado em " + Util.formataDataHora(this.data, Util.DATA);
			case REABERTURA : return "Período reaberto em " + Util.formataDataHora(this.dataAlteracao, Util.DATA);
			default : return this.toString();
		}
	}
	
	@Override
	public void validate() {
				
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public OperacaoConta getOperacao() {
		return operacao;
	}

	public void setOperacao(OperacaoConta operacao) {
		this.operacao = operacao;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}
}