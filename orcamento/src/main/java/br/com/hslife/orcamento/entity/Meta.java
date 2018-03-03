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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.SortComparator;

import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;
import br.com.hslife.orcamento.util.ItemMetaComparator;
import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="meta")
@SuppressWarnings("serial")
public class Meta extends EntityPersistence {
	
	@Column(length=50, nullable=false)
	private String descricao;
	
	@Column(nullable=false, precision=18, scale=2)
	private double totalReceita;
	
	@Column(nullable=false, precision=18, scale=2)
	private double totalDespesa;
	
	@Column(nullable=false, precision=18, scale=2)
	private double previsaoReceita;
	
	@Column(nullable=false, precision=18, scale=2)
	private double previsaoDespesa;
	
	@Column(length=50, nullable=true)
	private String objetivo;
	
	@Column(columnDefinition="text", nullable=true)
	private String detalheObjetivo;
	
	@Column
	private boolean ativo;
	
	@ManyToOne
	@JoinColumn(name="idMoeda", nullable=false)
	private Moeda moeda;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	@OneToMany(mappedBy="meta", fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	@SortComparator(ItemMetaComparator.class)
	private List<ItemMeta> itens; 
	
	public Meta() {
		ativo = true;
		itens = new ArrayList<>();
	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 50);
	}

	@Override
	public String getLabel() {
		return this.descricao;
	}

	public void recalculaTotais() {
		this.totalReceita = 0.0;
		this.totalDespesa = 0.0;
		
		for (ItemMeta item : this.itens) {
			if (item.getTipoLancamento().equals(TipoLancamento.RECEITA)) {
				this.totalReceita += item.getValor();
			} else {
				this.totalDespesa += item.getValor();
			}
		}
	}
	
	public int getPorcentagemReceita() {
		int porcentagem = 0;
		if (this.previsaoReceita > 0) {
			porcentagem = new BigDecimal(Util.arredondar((this.totalReceita / this.previsaoReceita) * 100)).intValue();
		}
		
		if (porcentagem > 100)
			porcentagem = 100;
		else if (porcentagem < 0)
			porcentagem = 0;
		
		return porcentagem;
	}
	
	public int getPorcentagemDespesa() {
		int porcentagem = 0;
		if (this.previsaoDespesa > 0) {
			porcentagem = new BigDecimal(Util.arredondar((this.totalDespesa / this.previsaoDespesa) * 100)).intValue();
		}
		
		if (porcentagem > 100)
			porcentagem = 100;
		else if (porcentagem < 0)
			porcentagem = 0;
		
		return porcentagem;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public double getTotalReceita() {
		return totalReceita;
	}

	public void setTotalReceita(double totalReceita) {
		this.totalReceita = totalReceita;
	}

	public double getTotalDespesa() {
		return totalDespesa;
	}

	public void setTotalDespesa(double totalDespesa) {
		this.totalDespesa = totalDespesa;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Moeda getMoeda() {
		return moeda;
	}

	public void setMoeda(Moeda moeda) {
		this.moeda = moeda;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<ItemMeta> getItens() {
		return itens;
	}

	public void setItens(List<ItemMeta> itens) {
		this.itens = itens;
	}

	public double getPrevisaoReceita() {
		return previsaoReceita;
	}

	public void setPrevisaoReceita(double previsaoReceita) {
		this.previsaoReceita = previsaoReceita;
	}

	public double getPrevisaoDespesa() {
		return previsaoDespesa;
	}

	public void setPrevisaoDespesa(double previsaoDespesa) {
		this.previsaoDespesa = previsaoDespesa;
	}

	public String getObjetivo() {
		return objetivo;
	}

	public void setObjetivo(String objetivo) {
		this.objetivo = objetivo;
	}

	public String getDetalheObjetivo() {
		return detalheObjetivo;
	}

	public void setDetalheObjetivo(String detalheObjetivo) {
		this.detalheObjetivo = detalheObjetivo;
	}	
}
