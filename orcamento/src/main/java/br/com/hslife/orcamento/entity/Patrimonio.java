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
@Table(name="patrimonio")
@SuppressWarnings("serial")
public class Patrimonio extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50, nullable=false)
	private String descricao;
	
	@Column(columnDefinition="text", nullable=false)
	private String detalheEntradaPatrimonio;
	
	@Column(nullable=false, precision=18, scale=2)
	private double valorPatrimonio;
	
	@Column(nullable=false)
	@Temporal(TemporalType.DATE)	
	private Date dataEntrada;
	
	@Column(nullable=true)
	@Temporal(TemporalType.DATE)
	private Date dataSaida;
	
	@Column(columnDefinition="text", nullable=true)
	private String detalheSaidaPatrimonio;
	
	@Column
	private boolean ativo;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name="idCategoriaDocumento", nullable=true)
	private CategoriaDocumento categoriaDocumento;
	
	@ManyToOne
	@JoinColumn(name="idFavorecido", nullable=false)
	private Favorecido favorecido;
	
	@ManyToOne
	@JoinColumn(name="idMoeda", nullable=false)
	private Moeda moeda;
	
	@ManyToOne
	@JoinColumn(name="idMeioPagamento", nullable=true)
	private MeioPagamento meioPagamento;
	
	public Patrimonio() {
		ativo = false;
	}

	@Override
	public String getLabel() {
		return this.descricao;
	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 50);
		EntityPersistenceUtil.validaCampoNulo("Detalhes da aquisição", this.detalheEntradaPatrimonio);
		EntityPersistenceUtil.validaCampoNulo("Data de aquisição", this.dataEntrada);
		EntityPersistenceUtil.validaCampoNulo("Favorecido", this.favorecido);
		EntityPersistenceUtil.validaCampoNulo("Moeda", this.moeda);
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

	public String getDetalheEntradaPatrimonio() {
		return detalheEntradaPatrimonio;
	}

	public void setDetalheEntradaPatrimonio(String detalheEntradaPatrimonio) {
		this.detalheEntradaPatrimonio = detalheEntradaPatrimonio;
	}

	public double getValorPatrimonio() {
		return valorPatrimonio;
	}

	public void setValorPatrimonio(double valorPatrimonio) {
		this.valorPatrimonio = valorPatrimonio;
	}

	public Date getDataEntrada() {
		return dataEntrada;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public Date getDataSaida() {
		return dataSaida;
	}

	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}

	public String getDetalheSaidaPatrimonio() {
		return detalheSaidaPatrimonio;
	}

	public void setDetalheSaidaPatrimonio(String detalheSaidaPatrimonio) {
		this.detalheSaidaPatrimonio = detalheSaidaPatrimonio;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public CategoriaDocumento getCategoriaDocumento() {
		return categoriaDocumento;
	}

	public void setCategoriaDocumento(CategoriaDocumento categoriaDocumento) {
		this.categoriaDocumento = categoriaDocumento;
	}

	public Favorecido getFavorecido() {
		return favorecido;
	}

	public void setFavorecido(Favorecido favorecido) {
		this.favorecido = favorecido;
	}

	public Moeda getMoeda() {
		return moeda;
	}

	public void setMoeda(Moeda moeda) {
		this.moeda = moeda;
	}

	public MeioPagamento getMeioPagamento() {
		return meioPagamento;
	}

	public void setMeioPagamento(MeioPagamento meioPagamento) {
		this.meioPagamento = meioPagamento;
	}
}
