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

import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="benfeitoria")
@SuppressWarnings("serial")
public class Benfeitoria extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50, nullable=false)
	private String descricao;
	
	@Column(columnDefinition="text", nullable=false)
	private String detalheBenfeitoria;
	
	@Column(nullable=false, precision=18, scale=2)
	private double valorBenfeitoria;
	
	@Column(nullable=false)
	@Temporal(TemporalType.DATE)
	private Date dataInicio;
	
	@Column(nullable=true)
	@Temporal(TemporalType.DATE)
	private Date dataFim;
	
	@ManyToOne
	@JoinColumn(name="idCategoriaDocumento", nullable=true)
	private CategoriaDocumento categoriaDocumento;
	
	@ManyToOne
	@JoinColumn(name="idPatrimonio", nullable=false)
	private Patrimonio patrimonio;
	
	@ManyToOne
	@JoinColumn(name="idGrupoLancamento", nullable=false)
	private GrupoLancamento grupoLancamento;
	
	public Benfeitoria() {
		
	}
	
	@Override
	public String getLabel() {
		return this.descricao;
	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 50);
		EntityPersistenceUtil.validaCampoNulo("Detalhes da benfeitoria", this.detalheBenfeitoria);
		EntityPersistenceUtil.validaCampoNulo("Data de início", this.dataInicio);
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

	public String getDetalheBenfeitoria() {
		return detalheBenfeitoria;
	}

	public void setDetalheBenfeitoria(String detalheBenfeitoria) {
		this.detalheBenfeitoria = detalheBenfeitoria;
	}

	public double getValorBenfeitoria() {
		return valorBenfeitoria;
	}

	public void setValorBenfeitoria(double valorBenfeitoria) {
		this.valorBenfeitoria = valorBenfeitoria;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public CategoriaDocumento getCategoriaDocumento() {
		return categoriaDocumento;
	}

	public void setCategoriaDocumento(CategoriaDocumento categoriaDocumento) {
		this.categoriaDocumento = categoriaDocumento;
	}

	public Patrimonio getPatrimonio() {
		return patrimonio;
	}

	public void setPatrimonio(Patrimonio patrimonio) {
		this.patrimonio = patrimonio;
	}

	public GrupoLancamento getGrupoLancamento() {
		return grupoLancamento;
	}

	public void setGrupoLancamento(GrupoLancamento grupoLancamento) {
		this.grupoLancamento = grupoLancamento;
	}
}