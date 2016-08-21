/***
  
  	Copyright (c) 2012 - 2016 Hércules S. S. José

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="detalhelancamento")
public class DetalheLancamento extends EntityPersistence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6890221385058056755L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false, length=50)
	private String descricao;
	
	@Column(nullable=false)
	@Temporal(TemporalType.DATE)
	private Date data;
	
	@Column(precision=18, scale=2)
	private double valor;

	@Override
	public String getLabel() {
		return this.descricao;
	}

	@Override
	public void validate() throws ApplicationException {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 50);
		EntityPersistenceUtil.validaCampoNulo("Data", this.data);
	}
	
	@Override
	public boolean equals(Object obj) {
		DetalheLancamento other = (DetalheLancamento)obj;
		if (this.id == null) {
			return this.descricao.equals(other.getDescricao()) && this.data.equals(other.getData()) && this.valor == other.valor;
		} 
		return super.equals(obj);
	}
	
	@SuppressWarnings("deprecation")
	public void setData(Date data) {
		if (data != null) {
			data.setHours(0);
			data.setMinutes(0);
			data.setSeconds(0);
		}
		this.data = data;
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

	public Date getData() {
		return data;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}
}