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

package br.com.hslife.orcamento.entity;
import java.util.Date;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.hslife.orcamento.enumeration.Container;

@Entity
@Table(name="arquivo")
public class Arquivo extends EntityPersistence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3525039093524704725L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String nomeArquivo;
	
	@Column
	private String contentType;
	
	@Column
	private long tamanho;
	
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCriacao;
	
	@Column(nullable=false, length=30)
	@Enumerated(EnumType.STRING)
	private Container container;
	
	@Column(nullable=false, length=50)
	private String attribute;
	
	@OneToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(length=16777216)
	private byte[] dados;
	
	@Transient
	private Map<String, Integer> opcoesSistema;
	
	public Arquivo() {
		dataCriacao = new Date();
	}
	
	private Arquivo(Builder builder) {
		this.nomeArquivo = builder.nomeArquivo;
		this.contentType = builder.contentType;
		this.tamanho = builder.tamanho;
		this.dataCriacao = builder.dataCriacao;
		this.container = builder.container;
		this.attribute = builder.attribute;
		this.usuario = builder.usuario;
		this.dados = builder.dados;
	}
	
	public static class Builder {
		private String nomeArquivo;
		private String contentType;
		private long tamanho;
		private Date dataCriacao = new Date();
		private Container container = Container.DOCUMENTOS;
		private String attribute = "arquivo";
		private Usuario usuario;
		private byte[] dados;
		
		public Builder nomeArquivo(String valor) {
			this.nomeArquivo = valor;
			return this;
		}
		
		public Builder contentType(String valor) {
			this.contentType = valor;
			return this;
		}
		
		public Builder tamanho(long valor) {
			this.tamanho = valor;
			return this;
		}
		
		public Builder usuario(Usuario valor) {
			this.usuario = valor;
			return this;
		}
		
		public Builder dados(byte[] valor) {
			this.dados = valor;
			return this;
		}
		
		public Arquivo build() {
			return new Arquivo(this);
		}
	}

	public Long getId() {
		return id;
	}
	
	@Override
	public String getLabel() {
		return this.nomeArquivo;
	}
	
	@Override
	public void validate() {
				
	}

	public void setOpcoesSistema(Map<String, Integer> opcoesSistema) {
		this.opcoesSistema = opcoesSistema;
	}
	
	public boolean isPrazoExpirado() {
		if (this.opcoesSistema != null && !this.opcoesSistema.isEmpty())
			switch(this.container) {
				case FATURACARTAO : return this.container.isPrazoExpirado(this.dataCriacao, this.opcoesSistema.get("ARQUIVO_TEMPO_GUARDA_FATURACARTAO"));
				case LANCAMENTOCONTA : return this.container.isPrazoExpirado(this.dataCriacao, this.opcoesSistema.get("ARQUIVO_TEMPO_GUARDA_LANCAMENTOCONTA"));
				case LANCAMENTOPERIODICO : return this.container.isPrazoExpirado(this.dataCriacao, this.opcoesSistema.get("ARQUIVO_TEMPO_GUARDA_LANCAMENTOPERIODICO"));
				default : return false;
			}
		return false;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public byte[] getDados() {
		return dados;
	}

	public void setDados(byte[] dados) {
		this.dados = dados;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getTamanho() {
		return tamanho;
	}

	public void setTamanho(long tamanho) {
		this.tamanho = tamanho;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
}
