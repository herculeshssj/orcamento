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
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.hslife.orcamento.enumeration.StatusDivida;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;
import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="dividaterceiro")
@SuppressWarnings("serial")
public class DividaTerceiro extends EntityPersistence {
	
	@Column(nullable=false, precision=18, scale=2)
	private double valorDivida;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date dataNegociacao;
	
	@Column(length=4000, nullable=false)
	private String justificativa;
	
	@Column(columnDefinition="text")
	private String termoDivida;
	
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="idArquivoTermoDivida", nullable=true)
	private Arquivo arquivoTermoDivida;
	
	@Column(columnDefinition="text")
	private String termoQuitacao;
	
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="idArquivoTermoQuitacao", nullable=true)
	private Arquivo arquivoTermoQuitacao;
	
	@Column(length=15, nullable=false)
	@Enumerated(EnumType.STRING)
	private StatusDivida statusDivida;
	
	@Column(length=10, nullable=false)
	@Enumerated(EnumType.STRING)
	private TipoCategoria tipoCategoria;
	
	@ManyToOne
	@JoinColumn(name="idMoeda", nullable=false)
	private Moeda moeda;
	
	@ManyToOne
	@JoinColumn(name="idFavorecido", nullable=false)
	private Favorecido favorecido;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	@OneToMany(mappedBy="dividaTerceiro", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private List<PagamentoDividaTerceiro> pagamentos = new LinkedList<>();
	
	@Transient
	private ModeloDocumento modeloDocumento;
	
	public DividaTerceiro() {		
		this.statusDivida = StatusDivida.REGISTRADO;
	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaCampoNulo("Data da negociação", this.dataNegociacao);
		EntityPersistenceUtil.validaCampoNulo("Justificativa", this.justificativa);
		EntityPersistenceUtil.validaCampoNulo("Categoria da dívida", this.tipoCategoria);
		EntityPersistenceUtil.validaCampoNulo("Favorecido", this.favorecido);
		EntityPersistenceUtil.validaCampoNulo("Moeda", this.moeda);
		EntityPersistenceUtil.validaCampoNulo("Usuário", this.usuario);
		
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Justificativa", this.justificativa, 4000);
	}

	@Override
	public String getLabel() {
		return this.tipoCategoria 
				+ " com " 
				+ this.favorecido.getNome() 
				+ " no valor de " 
				+ this.moeda.getSimboloMonetario() 
				+ " " 
				+ this.valorDivida 
				+ " - " 
				+ this.statusDivida;
	}
	
	public double getTotalPago() {
		double total = 0;
		
		if (pagamentos != null) {
			for (int i = 0; i < pagamentos.size(); i++) {
				total += pagamentos.get(i).getValorPago() * pagamentos.get(i).getTaxaConversao();
			}
		}
		
		return Util.arredondar(total);
	}
	
	public double getTotalAPagar() {
		double total = 0;
		
		if (pagamentos != null) {
			for (int i = 0; i < pagamentos.size(); i++) {
				total += pagamentos.get(i).getValorPago() * pagamentos.get(i).getTaxaConversao();
			}
		}
		
		return Util.arredondar(this.valorDivida - total);
	}
	
	/*
	 * Retorna a quantidade de dias desde o último pagamento realizado
	 */
	public int getQuantidadeDiasUltimoPagamento() {
		int quantDias = 0;
		
		// Pega o último pagamento efetuado caso exista
		if (this.pagamentos != null && !this.pagamentos.isEmpty()) {
			PagamentoDividaTerceiro pagamento = this.pagamentos.get(this.pagamentos.size() - 1);
			quantDias = Util.quantidadeDias(pagamento.getDataPagamento(), new Date());
		} else {
			// Calcula em cima da data de negociação
			quantDias = Util.quantidadeDias(this.dataNegociacao, new Date());
		}
		
		return quantDias;
	}
	
	/*
	 * Retorna a data do último pagamento efetuado. Se nenhum pagamento foi realizado
	 * retorna data da negociação
	 */
	public Date getUltimoPagamentoEfetuado() {
		if (this.pagamentos != null && !this.pagamentos.isEmpty()) {
			return pagamentos.get(this.pagamentos.size() - 1).getDataPagamento();
		} else {
			return this.dataNegociacao;
		}
	}

	public double getValorDivida() {
		return valorDivida;
	}

	public void setValorDivida(double valorDivida) {
		this.valorDivida = valorDivida;
	}

	public Date getDataNegociacao() {
		return dataNegociacao;
	}

	public void setDataNegociacao(Date dataNegociacao) {
		this.dataNegociacao = dataNegociacao;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getTermoDivida() {
		return termoDivida;
	}

	public void setTermoDivida(String termoDivida) {
		this.termoDivida = termoDivida;
	}

	public String getTermoQuitacao() {
		return termoQuitacao;
	}

	public void setTermoQuitacao(String termoQuitacao) {
		this.termoQuitacao = termoQuitacao;
	}

	public StatusDivida getStatusDivida() {
		return statusDivida;
	}

	public void setStatusDivida(StatusDivida statusDivida) {
		this.statusDivida = statusDivida;
	}

	public TipoCategoria getTipoCategoria() {
		return tipoCategoria;
	}

	public void setTipoCategoria(TipoCategoria tipoCategoria) {
		this.tipoCategoria = tipoCategoria;
	}

	public Favorecido getFavorecido() {
		return favorecido;
	}

	public void setFavorecido(Favorecido favorecido) {
		this.favorecido = favorecido;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<PagamentoDividaTerceiro> getPagamentos() {
		return pagamentos;
	}

	public void setPagamentos(List<PagamentoDividaTerceiro> pagamentos) {
		this.pagamentos = pagamentos;
	}

	public Moeda getMoeda() {
		return moeda;
	}

	public void setMoeda(Moeda moeda) {
		this.moeda = moeda;
	}

	public ModeloDocumento getModeloDocumento() {
		return modeloDocumento;
	}

	public void setModeloDocumento(ModeloDocumento modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
	}

	public Arquivo getArquivoTermoDivida() {
		return arquivoTermoDivida;
	}

	public void setArquivoTermoDivida(Arquivo arquivoTermoDivida) {
		this.arquivoTermoDivida = arquivoTermoDivida;
	}

	public Arquivo getArquivoTermoQuitacao() {
		return arquivoTermoQuitacao;
	}

	public void setArquivoTermoQuitacao(Arquivo arquivoTermoQuitacao) {
		this.arquivoTermoQuitacao = arquivoTermoQuitacao;
	}
}
