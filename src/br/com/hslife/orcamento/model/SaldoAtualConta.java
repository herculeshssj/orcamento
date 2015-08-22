/***
  
  	Copyright (c) 2012 - 2016 Hércules S. S. José

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

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento ou escreva 
    
    para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor, 
    
    Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor entre  

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.model;

import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.enumeration.TipoConta;

public class SaldoAtualConta {
	
	private TipoConta tipoConta;
	
	private String descricaoConta;
	
	private Moeda moedaConta;
	
	private double saldoPeriodo;
	
	private double saldoRegistrado;
	
	private double saldoAtual;
	
	private boolean ativo;

	public double getSaldoPeriodo() {
		return saldoPeriodo;
	}

	public void setSaldoPeriodo(double saldoPeriodo) {
		this.saldoPeriodo = saldoPeriodo;
	}

	public double getSaldoRegistrado() {
		return saldoRegistrado;
	}

	public void setSaldoRegistrado(double saldoRegistrado) {
		this.saldoRegistrado = saldoRegistrado;
	}

	public double getSaldoAtual() {
		return saldoAtual;
	}

	public void setSaldoAtual(double saldoAtual) {
		this.saldoAtual = saldoAtual;
	}

	public String getDescricaoConta() {
		return descricaoConta;
	}

	public void setDescricaoConta(String descricaoConta) {
		this.descricaoConta = descricaoConta;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public TipoConta getTipoConta() {
		return tipoConta;
	}

	public void setTipoConta(TipoConta tipoConta) {
		this.tipoConta = tipoConta;
	}

	public Moeda getMoedaConta() {
		return moedaConta;
	}

	public void setMoedaConta(Moeda moedaConta) {
		this.moedaConta = moedaConta;
	}
}