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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
@Table(name="auditoria")
public class Auditoria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 170587402135053960L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
	private String classe;
	
	@Column(nullable=false)
	private String usuario;
	
	@Column(nullable=false)
	private String ip;
	
	@Column(nullable=false)
	@Temporal(TemporalType.DATE)
	private Date data;
	
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataHora;
	
	@Column(nullable=false)
	private String transacao; // INSERT, UPDATE, DELETE
	
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	@Version
	private Date versionEntity;
	
	@Column(columnDefinition="mediumtext", nullable=true)
	private String dadosAuditados;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<AuditoriaDados> dadosAuditoria;
	
	public Auditoria() {
		data = Calendar.getInstance().getTime();
		dataHora = Calendar.getInstance().getTime();
		dadosAuditoria = new ArrayList<AuditoriaDados>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getTransacao() {
		return transacao;
	}

	public void setTransacao(String transacao) {
		this.transacao = transacao;
	}

	public List<AuditoriaDados> getDadosAuditoria() {
		return dadosAuditoria;
	}

	public void setDadosAuditoria(List<AuditoriaDados> dadosAuditoria) {
		this.dadosAuditoria = dadosAuditoria;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public Date getVersionEntity() {
		return versionEntity;
	}

	public void setVersionEntity(Date versionEntity) {
		this.versionEntity = versionEntity;
	}

	public String getDadosAuditados() {
		return dadosAuditados;
	}

	public void setDadosAuditados(String dadosAuditados) {
		this.dadosAuditados = dadosAuditados;
	}
}