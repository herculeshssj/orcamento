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
package br.com.hslife.orcamento.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;

import br.com.hslife.orcamento.util.Util;

public class InfoOFX {
	
	private String bancoID;
	
	private String nomeBanco;
	
	private String idioma;
	
	private String agencia;
	
	private String conta;
	
	private String tipoConta;
	
	private Date dataArquivo;
	
	private String moedaPadrao;
	
	private int quantidadeTransacao;
	
	private double balancoFinal;
	
	private Date inicioTransacoes;
	
	private Date fimTransacoes;
	
	public InfoOFX() {
		
	}
	
	private InfoOFX(Builder builder) {
		this.bancoID = builder.bancoID;		
		this.nomeBanco = builder.nomeBanco;
		this.idioma = builder.idioma;
		this.agencia = builder.agencia;
		this.conta = builder.conta;
		this.tipoConta = builder.tipoConta;
		this.dataArquivo = builder.dataArquivo;
		this.moedaPadrao = builder.moedaPadrao;
		this.quantidadeTransacao = builder.quantidadeTransacao;
		this.balancoFinal = builder.balancoFinal;
		this.inicioTransacoes = builder.inicioTransacoes;
		this.fimTransacoes = builder.fimTransacoes;
	}
	
	public static class Builder {
		private String bancoID;
		
		public Builder bancoID(String bancoID) {
			this.bancoID = bancoID;
			return this;
		}
		
		private String nomeBanco;
		
		public Builder nomeBanco(String nomeBanco) {
			this.nomeBanco = nomeBanco;
			return this;
		}
		
		private String idioma;
		
		public Builder idioma(String idioma) {
			this.idioma = idioma;
			return this;
		}
		
		private String agencia;
		
		public Builder agencia(String agencia) {
			this.agencia = agencia;
			return this;
		}
		
		private String conta;
		
		public Builder conta(String conta) {
			this.conta = conta;
			return this;
		}
		
		private String tipoConta;
		
		public Builder tipoConta(String tipoConta) {
			this.tipoConta = tipoConta;
			return this;
		}
		
		private Date dataArquivo;
		
		public Builder dataArquivo(Date dataArquivo) {
			this.dataArquivo = dataArquivo;
			return this;
		}
		
		private String moedaPadrao;
		
		public Builder moedaPadrao(String moedaPadrao) {
			this.moedaPadrao = moedaPadrao;
			return this;
		}
		
		private int quantidadeTransacao;
		
		public Builder quantidadeTransacao(int quantidadeTransacao) {
			this.quantidadeTransacao = quantidadeTransacao;
			return this;
		}
		
		private double balancoFinal;
		
		public Builder balancoFinal(double balancoFinal) {
			this.balancoFinal = balancoFinal;
			return this;
		}
		
		private Date inicioTransacoes;
		
		public Builder inicioTransacoes(Date inicioTransacoes) {
			this.inicioTransacoes = inicioTransacoes;
			return this;
		}
		
		private Date fimTransacoes;
		
		public Builder fimTransacoes(Date fimTransacoes) {
			this.fimTransacoes = fimTransacoes;
			return this;
		}
		
		public InfoOFX build() {
			return new InfoOFX(this);
		}
		
	}
	
	public void lerJson(String jsonData) {
		JSONObject jsonRead = new JSONObject(jsonData);
		Map<String, String> dados = new HashMap<>();
		
		// Preenche o Map com os dados do JSON
		for (Object obj : jsonRead.keySet()) {
			dados.put((String)obj, (String)jsonRead.get((String)obj));
		}
		
		// Popula os campos de acordo com os dados encontrados
		this.bancoID = dados.get("bancoid") == null ? null : dados.get("bancoid");
		this.nomeBanco = dados.get("nomebanco") == null ? null : dados.get("nomebanco");
		this.idioma = dados.get("idioma") == null ? null : dados.get("idioma");
		this.agencia = dados.get("agencia") == null ? null : dados.get("agencia");
		this.conta = dados.get("conta") == null ? null : dados.get("conta");
		this.tipoConta = dados.get("tipoconta") == null ? null : dados.get("tipoconta");
		this.moedaPadrao = dados.get("moedapadrao") == null ? null : dados.get("moedapadrao");
	}
	
	public String gerarJson() {
		// Popula um Map para poder gerar o JSON
		Map<String, String> dados = new HashMap<>();
		dados.put("bancoid", this.bancoID != null && !this.bancoID.trim().isEmpty() ? this.bancoID : null);
		dados.put("nomebanco", this.nomeBanco != null && !this.nomeBanco.trim().isEmpty() ? this.nomeBanco : null);
		dados.put("idioma", this.idioma != null && !this.idioma.trim().isEmpty() ? this.idioma : null);
		dados.put("agencia", this.agencia != null && !this.agencia.trim().isEmpty() ? this.agencia : null);
		dados.put("conta", this.conta != null && !this.conta.trim().isEmpty() ? this.conta : null);
		dados.put("tipoconta", this.tipoConta != null && !this.tipoConta.trim().isEmpty() ? this.tipoConta : null);
		dados.put("moedapadrao", this.moedaPadrao != null && !this.moedaPadrao.trim().isEmpty() ? this.moedaPadrao : null);
		
		// Gera o JSON em si
		JSONObject json = new JSONObject();
		for (String s : dados.keySet()) {
			json.put(s, dados.get(s));
		}
		
		return json.toString();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.bancoID, 
				this.nomeBanco, 
				this.idioma, 
				this.agencia, 
				this.conta, 
				this.tipoConta, 
				this.moedaPadrao);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InfoOFX) {
			
			InfoOFX other = (InfoOFX)obj;
			
			return (!Util.eVazio(other.getBancoID()) && other.getBancoID().equalsIgnoreCase(this.bancoID))
					&& (!Util.eVazio(other.getNomeBanco()) && other.getNomeBanco().equalsIgnoreCase(this.nomeBanco))
					&& (!Util.eVazio(other.getIdioma()) && other.getIdioma().equalsIgnoreCase(this.idioma))
					&& (!Util.eVazio(other.getAgencia()) && other.getAgencia().equalsIgnoreCase(this.agencia))
					&& (!Util.eVazio(other.getConta()) && other.getConta().equalsIgnoreCase(this.conta))
					&& (!Util.eVazio(other.getTipoConta()) && other.getTipoConta().equalsIgnoreCase(this.tipoConta))
					&& (!Util.eVazio(other.getMoedaPadrao()) && other.getMoedaPadrao().equalsIgnoreCase(this.moedaPadrao));
		}
		
		return false;
	}
	
	public String getBancoID() {
		return bancoID;
	}

	public void setBancoID(String bancoID) {
		this.bancoID = bancoID;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta;
	}

	public String getTipoConta() {
		return tipoConta;
	}

	public void setTipoConta(String tipoConta) {
		this.tipoConta = tipoConta;
	}

	public Date getDataArquivo() {
		return dataArquivo;
	}

	public void setDataArquivo(Date dataArquivo) {
		this.dataArquivo = dataArquivo;
	}

	public String getMoedaPadrao() {
		return moedaPadrao;
	}

	public void setMoedaPadrao(String moedaPadrao) {
		this.moedaPadrao = moedaPadrao;
	}

	public int getQuantidadeTransacao() {
		return quantidadeTransacao;
	}

	public void setQuantidadeTransacao(int quantidadeTransacao) {
		this.quantidadeTransacao = quantidadeTransacao;
	}

	public double getBalancoFinal() {
		return balancoFinal;
	}

	public void setBalancoFinal(double balancoFinal) {
		this.balancoFinal = balancoFinal;
	}

	public Date getInicioTransacoes() {
		return inicioTransacoes;
	}

	public void setInicioTransacoes(Date inicioTransacoes) {
		this.inicioTransacoes = inicioTransacoes;
	}

	public Date getFimTransacoes() {
		return fimTransacoes;
	}

	public void setFimTransacoes(Date fimTransacoes) {
		this.fimTransacoes = fimTransacoes;
	}

	public String getNomeBanco() {
		return nomeBanco;
	}

	public void setNomeBanco(String nomeBanco) {
		this.nomeBanco = nomeBanco;
	}

	public String getIdioma() {
		return idioma;
	}

	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
}
