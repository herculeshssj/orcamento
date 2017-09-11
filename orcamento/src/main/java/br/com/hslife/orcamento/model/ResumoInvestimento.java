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

package br.com.hslife.orcamento.model;

import br.com.hslife.orcamento.entity.Investimento;
import br.com.hslife.orcamento.entity.MovimentacaoInvestimento;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.util.Util;

public class ResumoInvestimento {
	
	private double aplicacao;
	
	private double resgate;
	
	private double rendimentoBruto;
	
	private double impostoRenda;
	
	private double iof;
	
	private double rendimentoLiquido;
	
	private double cotas;
	
	private Investimento investimento;
	
	private double precoMedio;
	
	public ResumoInvestimento() {
		this.inicializar();
	}
	
	public ResumoInvestimento(Investimento investimento) {
		this.inicializar();
		this.investimento = investimento;
	}
	
	private void inicializar() {
		this.aplicacao = 0.0;
		this.resgate = 0.0;
		this.rendimentoBruto = 0.0;
		this.impostoRenda = 0.0;
		this.iof = 0.0;
		this.rendimentoLiquido = 0.0;
		this.cotas = 0;
		this.precoMedio = 0.0;
	}

	public double getAplicacao() {
		return aplicacao;
	}

	public double getResgate() {
		return resgate;
	}

	public double getRendimentoBruto() {
		return rendimentoBruto;
	}

	public double getImpostoRenda() {
		return impostoRenda;
	}

	public double getIof() {
		return iof;
	}

	public double getRendimentoLiquido() {
		return rendimentoLiquido;
	}

	public double getCotas() {
		if (investimento == null)
			return cotas;
		
		for (MovimentacaoInvestimento movimentacao : investimento.getMovimentacoesInvestimento()) {
			if (movimentacao.getTipoLancamento().equals(TipoLancamento.RECEITA)) {
				cotas += movimentacao.getCotas();
			}
		}
		
		return cotas;
	}
	
	public double getPrecoMedio() {
		if (investimento == null)
			return precoMedio;
		
		double totalGasto = 0.0;
		double totalCotas = 0;
		for (MovimentacaoInvestimento movimentacao : investimento.getMovimentacoesInvestimento()) {
			if (movimentacao.getTipoLancamento().equals(TipoLancamento.RECEITA)) {
				totalGasto += movimentacao.getValorTotalRendaVariavel();
				totalCotas += movimentacao.getCotas();
			} else {
				totalGasto -= movimentacao.getValorTotalRendaVariavel();
				totalCotas -= movimentacao.getCotas();
			}
		}
		
		// Verifica se as variáveis são zero para não ocorrer divisão por zero
		if (totalGasto != 0 & totalCotas != 0)
			precoMedio = Util.arredondar(totalGasto / totalCotas);
		
		return precoMedio;
	}
}