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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="resultadoscript")
@SuppressWarnings("serial")
public class ResultadoScript extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="idScript", nullable=false)
	private Script script;
	
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date inicioExecucao;
	
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date terminoExecucao;
	
	@Column(columnDefinition="longtext", nullable=true)
	private String resultado;
	
	public ResultadoScript() {
		resultado = null;
	}
	
	@Override
	public String getLabel() {
		return "Executado em " + Util.formataDataHora(inicioExecucao, Util.DATAHORA);
	}

	@Override
	public void validate() {
		
	}
	
	public String getTempoExecucao() {
		return Util.tempoTranscorrido(inicioExecucao, terminoExecucao);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Script getScript() {
		return script;
	}

	public void setScript(Script script) {
		this.script = script;
	}

	public Date getInicioExecucao() {
		return inicioExecucao;
	}

	public void setInicioExecucao(Date inicioExecucao) {
		this.inicioExecucao = inicioExecucao;
	}

	public Date getTerminoExecucao() {
		return terminoExecucao;
	}

	public void setTerminoExecucao(Date terminoExecucao) {
		this.terminoExecucao = terminoExecucao;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
}
