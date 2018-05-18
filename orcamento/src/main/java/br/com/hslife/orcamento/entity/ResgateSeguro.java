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

import br.com.hslife.orcamento.util.EntityPersistenceUtil;
import br.com.hslife.orcamento.util.Util;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name="resgateseguro")
@SuppressWarnings("serial")
public class ResgateSeguro extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Calendar dataResgate;

	@Column(nullable=false, precision=18, scale=2)
	private double valorResgatado;

	@Column(columnDefinition="text")
	private String motivoResgate;

	@Column(nullable = true)
	private Long idArquivo;

	@ManyToOne
	@JoinColumn(name="idSeguro", nullable=false)
	private Seguro seguro;

	public ResgateSeguro() {

	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaCampoNulo("Data do resgate", this.getDataResgate());
		EntityPersistenceUtil.validaCampoNulo("Seguro", this.getSeguro());
	}

	@Override
	public String getLabel() {
		return Util.concatenar(
				"Resgate de ",
				Util.moedaBrasil(this.getValorResgatado()),
				" efetuado em ",
				Util.formataDataHora(this.getDataResgate().getTime(), Util.DATA)
		);
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Calendar getDataResgate() {
		return dataResgate;
	}

	public void setDataResgate(Calendar dataResgate) {
		this.dataResgate = dataResgate;
	}

	public double getValorResgatado() {
		return valorResgatado;
	}

	public void setValorResgatado(double valorResgatado) {
		this.valorResgatado = valorResgatado;
	}

	public String getMotivoResgate() {
		return motivoResgate;
	}

	public void setMotivoResgate(String motivoResgate) {
		this.motivoResgate = motivoResgate;
	}

	public Long getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Long idArquivo) {
		this.idArquivo = idArquivo;
	}

	public Seguro getSeguro() {
		return seguro;
	}

	public void setSeguro(Seguro seguro) {
		this.seguro = seguro;
	}
}
