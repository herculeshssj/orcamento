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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.hslife.orcamento.enumeration.TipoOpcaoSistema;
import br.com.hslife.orcamento.rest.json.OpcaoSistemaJson;

@Entity
@Table(name="opcaosistema")
public class OpcaoSistema extends EntityPersistence {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1170322496528587954L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=100, nullable=false)
	private String chave;
	
	@Column(columnDefinition="text", nullable=true)
	private String valor;
	
	@Column(length=15)
	@Enumerated(EnumType.STRING)
	private TipoOpcaoSistema tipoOpcaoSistema;
	
	@Column
	private boolean enabled;
	
	@Column
	private boolean visible;
	
	@Column
	private boolean required;
	
	@Column(length=20, nullable=false)
	private String tipoValor;
	
	@Column(length=100, nullable=false)
	private String casoDeUso;
	
	@ManyToOne
	@JoinColumn(name="idUsuario")
	private Usuario usuario;
	
	public OpcaoSistema() {
		enabled = true;
		visible = true;
	}
	
	@Override
	public String getLabel() {
		return chave;
	}
	
	@Override
	public void validate() {
				
	}
	
	@Override
	public OpcaoSistemaJson toJson() {
		return new OpcaoSistemaJson();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public TipoOpcaoSistema getTipoOpcaoSistema() {
		return tipoOpcaoSistema;
	}

	public void setTipoOpcaoSistema(TipoOpcaoSistema tipoOpcaoSistema) {
		this.tipoOpcaoSistema = tipoOpcaoSistema;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getTipoValor() {
		return tipoValor;
	}

	public void setTipoValor(String tipoValor) {
		this.tipoValor = tipoValor;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getCasoDeUso() {
		return casoDeUso;
	}

	public void setCasoDeUso(String casoDeUso) {
		this.casoDeUso = casoDeUso;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
}