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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.hslife.orcamento.enumeration.SituacaoOrcamento;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;
import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="detalheorcamento")
@SuppressWarnings("serial")
public class DetalheOrcamento extends EntityPersistence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column(length=50, nullable=false)
	private String descricao;
	
	@Column(nullable=false)
	private Long idEntity;
	
	@Column(length=10, nullable = true)
	@Enumerated(EnumType.STRING)
	private TipoCategoria tipoCategoria;
	
	@Column(nullable=false, precision=18, scale=2)
	private double previsao;
	
	@Column(nullable=false, precision=18, scale=2)
	private double previsaoCredito;
	
	@Column(nullable=false, precision=18, scale=2)
	private double previsaoDebito;
	
	@Column(nullable=false, precision=18, scale=2)
	private double realizado;
	
	@Column(nullable=false, precision=18, scale=2)
	private double realizadoCredito;
	
	@Column(nullable=false, precision=18, scale=2)
	private double realizadoDebito;
	
	@Transient
	private boolean selecionado;
	
	@Transient
	private boolean idChanged;
	
	public DetalheOrcamento() {
		
	}
	
	public DetalheOrcamento(Long idEntity, String descricao) {
		this.idEntity = idEntity;
		this.descricao = descricao;
		
		this.id = idEntity;
		this.idChanged = true;
	}
	
	public DetalheOrcamento(Long idEntity, String descricao, TipoCategoria tipoCategoria) {
		this.idEntity = idEntity;
		this.descricao = descricao;
		this.tipoCategoria = tipoCategoria;
		
		this.id = idEntity;
		this.idChanged = true;
	}
	
	@Override
	public String getLabel() {
		return this.descricao;
	}
	
	@Override
	public void validate() throws BusinessException{
		EntityPersistenceUtil.validaCampoNulo("Descrição do item", descricao);
		
		EntityPersistenceUtil.validaCampoNulo("ID do item", idEntity);
	}
	
	public double getPorcentagem() {
		try {
			return Util.arredondar( (this.realizado / this.previsao) * 100 ); 
		} catch (NumberFormatException nfe) {
			System.out.println("Houve divisão por zero, infinity ou NaN");
			return 0;
		}
	}
	
	public double getPorcentagemCredito() {
		try {
			return Util.arredondar( (this.realizadoCredito / this.previsaoCredito) * 100 ); 
		} catch (NumberFormatException nfe) {
			System.out.println("Houve divisão por zero, infinity ou NaN");
			return 0;
		}
	}
	
	public double getPorcentagemDebito() {
		try {
			return Util.arredondar( (this.realizadoDebito / this.previsaoDebito) * 100 ); 
		} catch (NumberFormatException nfe) {
			System.out.println("Houve divisão por zero, infinity ou NaN");
			return 0;
		}
	}
	
	public SituacaoOrcamento getSituacao() {
		if (this.tipoCategoria.equals(TipoCategoria.CREDITO)) {
			if (Math.floor(getPorcentagem()) <= 70) return SituacaoOrcamento.RUIM;
			if (Math.floor(getPorcentagem()) > 70 && Math.floor(getPorcentagem()) <= 100) return SituacaoOrcamento.REGULAR;
			if (Math.floor(getPorcentagem()) > 100) return SituacaoOrcamento.BOM;
		} else {
			if (Math.floor(getPorcentagem()) <= 70) return SituacaoOrcamento.BOM;
			if (Math.floor(getPorcentagem()) > 70 && Math.floor(getPorcentagem()) <= 100) return SituacaoOrcamento.REGULAR;
			if (Math.floor(getPorcentagem()) > 100) return SituacaoOrcamento.RUIM;			
		}
		return SituacaoOrcamento.BOM;
	}
	
	public SituacaoOrcamento getSituacaoCredito() {
		if (Math.floor(getPorcentagemCredito()) <= 70) return SituacaoOrcamento.RUIM;
		if (Math.floor(getPorcentagemCredito()) > 70 && Math.floor(getPorcentagemCredito()) <= 100) return SituacaoOrcamento.REGULAR;
		if (Math.floor(getPorcentagemCredito()) > 100) return SituacaoOrcamento.BOM;
		return SituacaoOrcamento.BOM;
	}
	
	public SituacaoOrcamento getSituacaoDebito() {
		if (Math.floor(getPorcentagemDebito()) <= 70) return SituacaoOrcamento.BOM;
		if (Math.floor(getPorcentagemDebito()) > 70 && Math.floor(getPorcentagemDebito()) <= 100) return SituacaoOrcamento.REGULAR;
		if (Math.floor(getPorcentagemDebito()) > 100) return SituacaoOrcamento.RUIM;			
		return SituacaoOrcamento.BOM;
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

	public Long getIdEntity() {
		return idEntity;
	}

	public void setIdEntity(Long idEntity) {
		this.idEntity = idEntity;
	}

	public double getPrevisao() {
		return previsao;
	}

	public void setPrevisao(double previsao) {
		this.previsao = previsao;
	}

	public double getPrevisaoCredito() {
		return previsaoCredito;
	}

	public void setPrevisaoCredito(double previsaoCredito) {
		this.previsaoCredito = previsaoCredito;
	}

	public double getPrevisaoDebito() {
		return previsaoDebito;
	}

	public void setPrevisaoDebito(double previsaoDebito) {
		this.previsaoDebito = previsaoDebito;
	}

	public double getRealizado() {
		return realizado;
	}

	public void setRealizado(double realizado) {
		this.realizado = realizado;
	}

	public double getRealizadoCredito() {
		return realizadoCredito;
	}

	public void setRealizadoCredito(double realizadoCredito) {
		this.realizadoCredito = realizadoCredito;
	}

	public double getRealizadoDebito() {
		return realizadoDebito;
	}

	public void setRealizadoDebito(double realizadoDebito) {
		this.realizadoDebito = realizadoDebito;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isIdChanged() {
		return idChanged;
	}

	public void setIdChanged(boolean idChanged) {
		this.idChanged = idChanged;
	}

	public TipoCategoria getTipoCategoria() {
		return tipoCategoria;
	}

	public void setTipoCategoria(TipoCategoria tipoCategoria) {
		this.tipoCategoria = tipoCategoria;
	}
}