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

package br.com.hslife.orcamento.model;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamento;

public class PanoramaLancamentoCartao {
	
	private String oid;
	
	private final double mes[] = new double[12];
	
	private int ano;
	
	private String descricao;
	
	private Conta conta;
	
	private double janeiro;
	
	private double fevereiro;
	
	private double marco;
	
	private double abril;
	
	private double maio;
	
	private double junho;
	
	private double julho;
	
	private double agosto;
	
	private double setembro;
	
	private double outubro;
	
	private double novembro;
	
	private double dezembro;
	
	private int indice;
	
	public PanoramaLancamentoCartao() {
		for (int i = 0; i < 12; i++) {
			mes[i] = 0;
		}
	}

	public void setarMes(int indice, LancamentoConta lancamento) {
		if (lancamento.getTipoLancamento().equals(TipoLancamento.RECEITA)) {
			this.mes[indice] += lancamento.getValorPago();
		} else {
			this.mes[indice] -= lancamento.getValorPago();
		}
		this.setJaneiro(indice, mes[indice]);
		this.setFevereiro(indice, mes[indice]);
		this.setMarco(indice, mes[indice]);
		this.setAbril(indice, mes[indice]);
		this.setMaio(indice, mes[indice]);
		this.setJunho(indice, mes[indice]);
		this.setJulho(indice, mes[indice]);
		this.setAgosto(indice, mes[indice]);
		this.setSetembro(indice, mes[indice]);
		this.setOutubro(indice, mes[indice]);
		this.setNovembro(indice, mes[indice]);
		this.setDezembro(indice, mes[indice]);
	}
	
	public double obterMes(int indice) {
		return mes[indice];
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public double getJaneiro() {
		return janeiro;
	}

	public void setJaneiro(double janeiro) {
		this.janeiro = janeiro;
	}
	
	public void setJaneiro(int indice, double valor) {
		if (indice == 0) 
			this.janeiro = valor;
	}

	public double getFevereiro() {
		return fevereiro;
	}

	public void setFevereiro(double fevereiro) {
		this.fevereiro = fevereiro;
	}

	public void setFevereiro(int indice, double valor) {
		if (indice == 1) 
			this.fevereiro = valor;
	}
	
	public double getMarco() {
		return marco;
	}

	public void setMarco(double marco) {
		this.marco = marco;
	}
	
	public void setMarco(int indice, double valor) {
		if (indice == 2) 
			this.marco = valor;
	}

	public double getAbril() {
		return abril;
	}

	public void setAbril(double abril) {
		this.abril = abril;
	}
	
	public void setAbril(int indice, double valor) {
		if (indice == 3) 
			this.abril = valor;
	}

	public double getMaio() {
		return maio;
	}

	public void setMaio(double maio) {
		this.maio = maio;
	}
	
	public void setMaio(int indice, double valor) {
		if (indice == 4) 
			this.maio = valor;
	}

	public double getJunho() {
		return junho;
	}

	public void setJunho(double junho) {
		this.junho = junho;
	}
	
	public void setJunho(int indice, double valor) {
		if (indice == 5) 
			this.junho = valor;
	}

	public double getJulho() {
		return julho;
	}

	public void setJulho(double julho) {
		this.julho = julho;
	}
	
	public void setJulho(int indice, double valor) {
		if (indice == 6) 
			this.julho = valor;
	}

	public double getAgosto() {
		return agosto;
	}

	public void setAgosto(double agosto) {
		this.agosto = agosto;
	}

	public void setAgosto(int indice, double valor) {
		if (indice == 7) 
			this.agosto = valor;
	}
	
	public double getSetembro() {
		return setembro;
	}

	public void setSetembro(double setembro) {
		this.setembro = setembro;
	}
	
	public void setSetembro(int indice, double valor) {
		if (indice == 8) 
			this.setembro = valor;
	}

	public double getOutubro() {
		return outubro;
	}

	public void setOutubro(double outubro) {
		this.outubro = outubro;
	}
	
	public void setOutubro(int indice, double valor) {
		if (indice == 9) 
			this.outubro = valor;
	}

	public double getNovembro() {
		return novembro;
	}

	public void setNovembro(double novembro) {
		this.novembro = novembro;
	}
	
	public void setNovembro(int indice, double valor) {
		if (indice == 10) 
			this.novembro = valor;
	}

	public double getDezembro() {
		return dezembro;
	}

	public void setDezembro(double dezembro) {
		this.dezembro = dezembro;
	}
	
	public void setDezembro(int indice, double valor) {
		if (indice == 11) 
			this.dezembro = valor;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public double getTotal() {
		return this.janeiro + this.fevereiro + this.marco + this.abril
				+ this.maio + this.junho + this.julho + this.agosto
				+ this.setembro + this.outubro + this.novembro + this.dezembro;
	}
}