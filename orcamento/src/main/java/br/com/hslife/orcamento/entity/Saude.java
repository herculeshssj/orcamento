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

package br.com.hslife.orcamento.entity;import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="saude")
@SuppressWarnings("serial")
public class Saude extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50, nullable=false)
	private String descricao;
	
	@Column(columnDefinition="text", nullable=false)
	private String sintomas;
	
	@Column(length=100, nullable=true)
	private String medico;
	
	@Column(length=50, nullable=true)
	private String especialidade;
	
	@Column(columnDefinition="text", nullable=true)
	private String contatoMedico;
	
	@Column
	private boolean ativo;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name="idCategoriaDocumento", nullable=true)
	private CategoriaDocumento categoriaDocumento;
	
	@OneToMany(mappedBy="saude", fetch=FetchType.EAGER, orphanRemoval=true, cascade=CascadeType.ALL)
	private List<HistoricoSaude> historico;
	
	@OneToMany(mappedBy="saude", fetch=FetchType.EAGER, orphanRemoval=true, cascade=CascadeType.ALL)
	private Set<TratamentoSaude> tratamento;
	
	public Saude() {
		ativo = true;
		historico = new ArrayList<>();
		tratamento = new HashSet<>();
	}

	@Override
	public String getLabel() {
		return this.descricao;
	}

	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 50);
		EntityPersistenceUtil.validaCampoNulo("Sintomas", this.sintomas);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getSintomas() {
		return sintomas;
	}

	public void setSintomas(String sintomas) {
		this.sintomas = sintomas;
	}

	public String getMedico() {
		return medico;
	}

	public void setMedico(String medico) {
		this.medico = medico;
	}

	public String getContatoMedico() {
		return contatoMedico;
	}

	public void setContatoMedico(String contatoMedico) {
		this.contatoMedico = contatoMedico;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public CategoriaDocumento getCategoriaDocumento() {
		return categoriaDocumento;
	}

	public void setCategoriaDocumento(CategoriaDocumento categoriaDocumento) {
		this.categoriaDocumento = categoriaDocumento;
	}

	public List<HistoricoSaude> getHistorico() {
		return historico;
	}

	public void setHistorico(List<HistoricoSaude> historico) {
		this.historico = historico;
	}

	public Set<TratamentoSaude> getTratamento() {
		return tratamento;
	}

	public void setTratamento(Set<TratamentoSaude> tratamento) {
		this.tratamento = tratamento;
	}
}
