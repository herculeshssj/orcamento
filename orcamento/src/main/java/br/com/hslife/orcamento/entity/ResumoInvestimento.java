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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.hslife.orcamento.rest.json.AbstractJson;

@Entity
@Table(name="resumoinvestimento")
@SuppressWarnings("serial")
public class ResumoInvestimento extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
	private int mes;
	
	@Column(nullable=false)
	private int ano;
	
	@Column(nullable=false, precision=18, scale=2)
	private double aplicacao;
	
	@Column(nullable=false, precision=18, scale=2)
	private double resgate;
	
	@Column(nullable=false, precision=18, scale=2)
	private double rendimentoBruto;
	
	@Column(nullable=false, precision=18, scale=2)
	private double impostoRenda;
	
	@Column(nullable=false, precision=18, scale=2)
	private double iof;
	
	@Column(nullable=false, precision=18, scale=2)
	private double rendimentoLiquido;
	
	public ResumoInvestimento() {
		this.aplicacao = 0.0;
		this.resgate = 0.0;
		this.rendimentoBruto = 0.0;
		this.impostoRenda = 0.0;
		this.iof = 0.0;
		this.rendimentoLiquido = 0.0;
	}
	
	public ResumoInvestimento(int mes, int ano) {
		this.mes = mes;
		this.ano = ano;
		this.aplicacao = 0.0;
		this.resgate = 0.0;
		this.rendimentoBruto = 0.0;
		this.impostoRenda = 0.0;
		this.iof = 0.0;
		this.rendimentoLiquido = 0.0;
	}
	
	@Override
	public String getLabel() {
		return this.mes + " / " + this.ano;
	}

	@Override
	public void validate() {
				
	}
	
	@Override
	public AbstractJson toJson() {
		// TODO Auto-generated method stub
		return null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public double getAplicacao() {
		return aplicacao;
	}

	public void setAplicacao(double aplicacao) {
		this.aplicacao = aplicacao;
	}

	public double getResgate() {
		return resgate;
	}

	public void setResgate(double resgate) {
		this.resgate = resgate;
	}

	public double getRendimentoBruto() {
		return rendimentoBruto;
	}

	public void setRendimentoBruto(double rendimentoBruto) {
		this.rendimentoBruto = rendimentoBruto;
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

	public double getRendimentoLiquido() {
		return rendimentoLiquido;
	}

	public void setRendimentoLiquido(double rendimentoLiquido) {
		this.rendimentoLiquido = rendimentoLiquido;
	}
}