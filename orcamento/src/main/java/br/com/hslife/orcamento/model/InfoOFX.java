/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

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
	public boolean equals(Object obj) {
		if (obj instanceof InfoOFX) {
			
			InfoOFX other = (InfoOFX)obj;
			
			return other.getBancoID().equalsIgnoreCase(this.bancoID)
					&& other.getNomeBanco().equalsIgnoreCase(this.nomeBanco)
					&& other.getIdioma().equalsIgnoreCase(this.idioma)
					&& other.getAgencia().equalsIgnoreCase(this.agencia)
					&& other.getConta().equalsIgnoreCase(this.conta)
					&& other.getTipoConta().equalsIgnoreCase(this.tipoConta)
					&& other.getMoedaPadrao().equalsIgnoreCase(this.moedaPadrao);
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