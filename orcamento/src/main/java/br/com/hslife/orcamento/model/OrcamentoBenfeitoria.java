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
package br.com.hslife.orcamento.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@XmlRootElement
@JsonAutoDetect
public class OrcamentoBenfeitoria implements Comparable<OrcamentoBenfeitoria>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8459175751443019450L;

	private Long id;
	
	private String favorecido;
	
	private String contato;
	
	private Date data;
	
	private Long dataLong;
	
	private Double valor;
	
	private String detalhes;
	
	private Boolean aprovado;
	
	private String versao = "1.0";
	
	public OrcamentoBenfeitoria() {
		data = new Date();
	}
	
	public OrcamentoBenfeitoria(JSONObject json) {		
		this.id = json.getLong("id");
		this.favorecido = json.getString("favorecido");
		this.contato = json.getString("contato");
		this.valor = json.getDouble("valor");
		this.detalhes = json.getString("detalhes");
		this.dataLong = json.getLong("dataLong");
		this.data = new Date(this.dataLong);
		this.aprovado = json.getBoolean("aprovado");
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Long.signum(getId() == null ? 0 : getId() ^ (getId() == null ? 1 : getId() >>> 32));
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getId() == null) return false;
		if (getClass() != obj.getClass()) return false;

		OrcamentoBenfeitoria other = (OrcamentoBenfeitoria) obj;
		if (!getId().equals(other.getId())) return false;
		return true;
	}
	
	
	@Override
	public int compareTo(OrcamentoBenfeitoria o) {
		if (this.getId() != null && o != null && o.getId() != null)
			return this.getId().compareTo(o.getId());
		return 0;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFavorecido() {
		return favorecido;
	}

	public void setFavorecido(String favorecido) {
		this.favorecido = favorecido;
	}

	public String getContato() {
		return contato;
	}

	public void setContato(String contato) {
		this.contato = contato;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public String getDetalhes() {
		return detalhes;
	}

	public void setDetalhes(String detalhes) {
		this.detalhes = detalhes;
	}

	public String getVersao() {
		return versao;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Long getDataLong() {
		return dataLong;
	}

	public void setDataLong(Long dataLong) {
		this.dataLong = dataLong;
	}

	public Boolean getAprovado() {
		return aprovado;
	}

	public void setAprovado(Boolean aprovado) {
		this.aprovado = aprovado;
	}
}
