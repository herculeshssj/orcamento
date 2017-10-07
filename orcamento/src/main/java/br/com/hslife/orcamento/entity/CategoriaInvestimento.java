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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.hslife.orcamento.enumeration.TipoInvestimento;
import br.com.hslife.orcamento.rest.json.CategoriaInvestimentoJson;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;
import br.com.hslife.orcamento.util.Util;


@Entity
@Table(name="categoriainvestimento")
@SuppressWarnings("serial")
public class CategoriaInvestimento extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50, nullable=false)
	private String descricao;

	@Column
	private boolean ativo;
	
	@Column(length=10, nullable=false)
	@Enumerated(EnumType.STRING)
	private TipoInvestimento tipoInvestimento;
	
	/*** Atributos usados para a Carteira de Investimento ***/
	
	@Transient
	private List<Investimento> investimentos = new ArrayList<>();
	
	@Transient
	private double totalInvestimento = 0.0;
	
	/*** Atributos usados para a Carteira de Investimento ***/
	
	public CategoriaInvestimento() {
		ativo = true;
	}
		
	@Override
	public String getLabel() {
		return this.descricao;
	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 50);
		EntityPersistenceUtil.validaCampoNulo("Tipo de investimento", this.tipoInvestimento);
	}
	
	@Override
	public CategoriaInvestimentoJson toJson() {
		return new CategoriaInvestimentoJson();
	}

	public Long getId() {
		return id;
	}
	
	/*
	 * Calcula o percentual de cada investimento contido na categoria.
	 * Usado no resumo Carteira de Investimento
	 */
	public void calcularPercentuaisInvestimento() {
		if (this.investimentos != null && !this.investimentos.isEmpty()) {
			// Calcula o total investido
			for (Investimento i : this.investimentos) {
				this.totalInvestimento += i.getValorInvestimento();
			}
			
			// Seta em cada investimento o seu respectivo percentual
			for (Investimento in : this.investimentos) {
				
				double valorInvestimento = in.getValorInvestimento();
				
				// Previne problemas com operação com zero
				if (this.totalInvestimento != 0 & valorInvestimento != 0) {
					in.setPercentualInvestimento(Util.arredondar( (valorInvestimento / this.totalInvestimento) * 100 ));
				}
				
			}
		}
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

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public TipoInvestimento getTipoInvestimento() {
		return tipoInvestimento;
	}

	public void setTipoInvestimento(TipoInvestimento tipoInvestimento) {
		this.tipoInvestimento = tipoInvestimento;
	}

	public List<Investimento> getInvestimentos() {
		return investimentos;
	}

	public void setInvestimentos(List<Investimento> investimentos) {
		this.investimentos = investimentos;
	}

	public double getTotalInvestimento() {
		return totalInvestimento;
	}
}