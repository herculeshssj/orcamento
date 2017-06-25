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

import br.com.hslife.orcamento.enumeration.TipoTratamento;
import br.com.hslife.orcamento.rest.json.AbstractJson;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="tratamentosaude")
@SuppressWarnings("serial")
public class TratamentoSaude extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=100, nullable=false)
	private String descricao;

	@Column(length=15, nullable=false)
	@Enumerated(EnumType.STRING)
	private TipoTratamento tipoTratamento;
	
	@Column(length=50, nullable=true)
	private String periodicidade;
	
	@Column(columnDefinition="text", nullable=true)
	private String observacao;
	
	@ManyToOne
	@JoinColumn(name="idSaude", nullable=false)
	private Saude saude;
	
	public TratamentoSaude() {
		
	}

	@Override
	public String getLabel() {
		return this.descricao;
	}

	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 100);
		EntityPersistenceUtil.validaCampoNulo("Tipo de tratamento", this.tipoTratamento);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Periodicidade", this.periodicidade, 50);
	}

	@Override
	public AbstractJson toJson() {
		// TODO Auto-generated method stub
		return null;
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

	public TipoTratamento getTipoTratamento() {
		return tipoTratamento;
	}

	public void setTipoTratamento(TipoTratamento tipoTratamento) {
		this.tipoTratamento = tipoTratamento;
	}

	public String getPeriodicidade() {
		return periodicidade;
	}

	public void setPeriodicidade(String periodicidade) {
		this.periodicidade = periodicidade;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Saude getSaude() {
		return saude;
	}

	public void setSaude(Saude saude) {
		this.saude = saude;
	}
}