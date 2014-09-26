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

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.hslife.orcamento.enumeration.TipoAgrupamentoBusca;

@SuppressWarnings("serial")
@Entity
@Table(name="buscasalva")
public class BuscaSalva extends EntityPersistence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false, length=50)
	private String descricao;
	
	@Column(nullable=true, length=50)
	private String textoBusca;
	
	@Column(nullable=true, length=20)
	private String textoParcela;
	
	@Column(nullable=true)
	@Temporal(TemporalType.DATE)
	private Date dataInicio;
	
	@Column(nullable=true)
	@Temporal(TemporalType.DATE)
	private Date dataFim;
	
	@Column(length=15, nullable=false)
	@Enumerated(EnumType.STRING)
	private TipoAgrupamentoBusca tipoAgrupamentoBusca;
	
	@ManyToOne
	@JoinColumn(name="idConta", nullable=false)
	private Conta conta;
	
	@Column
	private int agendados;
	
	@Column
	private int quitados;
	
	@Column
	private boolean exibirSaldoAnterior;
	
	@Column
	private boolean pesquisarTermo;

	public BuscaSalva() {
		tipoAgrupamentoBusca = TipoAgrupamentoBusca.NENHUM;
	}

	public Long getId() {
		return id;
	}
	
	@Override
	public String getLabel() {
		return this.descricao;
	}
	
	@Override
	public void validate() {
				
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTextoBusca() {
		return textoBusca;
	}

	public void setTextoBusca(String textoBusca) {
		this.textoBusca = textoBusca;
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

	public TipoAgrupamentoBusca getTipoAgrupamentoBusca() {
		return tipoAgrupamentoBusca;
	}

	public void setTipoAgrupamentoBusca(TipoAgrupamentoBusca tipoAgrupamentoBusca) {
		this.tipoAgrupamentoBusca = tipoAgrupamentoBusca;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public boolean isExibirSaldoAnterior() {
		return exibirSaldoAnterior;
	}

	public void setExibirSaldoAnterior(boolean exibirSaldoAnterior) {
		this.exibirSaldoAnterior = exibirSaldoAnterior;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getAgendados() {
		return agendados;
	}

	public void setAgendados(int agendados) {
		this.agendados = agendados;
	}

	public String getTextoParcela() {
		return textoParcela;
	}

	public void setTextoParcela(String textoParcela) {
		this.textoParcela = textoParcela;
	}

	public int getQuitados() {
		return quitados;
	}

	public void setQuitados(int quitados) {
		this.quitados = quitados;
	}

	public boolean isPesquisarTermo() {
		return pesquisarTermo;
	}

	public void setPesquisarTermo(boolean pesquisarTermo) {
		this.pesquisarTermo = pesquisarTermo;
	}
}