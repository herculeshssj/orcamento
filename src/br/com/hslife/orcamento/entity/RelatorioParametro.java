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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.hslife.orcamento.enumeration.TipoDado;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="relatoriocoluna")
@SuppressWarnings("serial")
public class RelatorioParametro extends EntityPersistence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50, nullable=false)
	private String nomeParametro;
	
	@Column(length=100, nullable=false)
	private String textoExibicao;
	
	@Column(length=15, nullable=false)
	@Enumerated(EnumType.STRING)
	private TipoDado tipoDado;
	
	public RelatorioParametro() {
		
	}
	
	@Override
	public String getLabel() {
		return this.textoExibicao + "(" + this.nomeParametro + ")";
	}

	@Override
	public void validate() throws BusinessException {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Nome do parâmetro", this.nomeParametro, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Texto de exibição", this.textoExibicao, 50);
		EntityPersistenceUtil.validaCampoNulo("Tipo de dado", this.tipoDado);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTextoExibicao() {
		return textoExibicao;
	}

	public void setTextoExibicao(String textoExibicao) {
		this.textoExibicao = textoExibicao;
	}

	public String getNomeParametro() {
		return nomeParametro;
	}

	public void setNomeParametro(String nomeParametro) {
		this.nomeParametro = nomeParametro;
	}

	public TipoDado getTipoDado() {
		return tipoDado;
	}

	public void setTipoDado(TipoDado tipoDado) {
		this.tipoDado = tipoDado;
	}
}