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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.hslife.orcamento.exception.BusinessException;

@Entity
@Table(name="telefone")
@SuppressWarnings("serial")
public class Telefone extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50, nullable=false)
	private String descricao;
	
	@Column(length=5, nullable=true)
	private String ddd;
	
	@Column(length=15, nullable=false)
	private String numero;
	
	@Column(length=5, nullable=true)
	private String ramal;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	public Telefone() {
	}

	@Override
	public String getLabel() {		
		StringBuilder textoBuilder = new StringBuilder();
		
		textoBuilder.append(this.descricao + ":");
		
		if (this.ddd != null && !this.ddd.trim().isEmpty()) {
			textoBuilder.append(" (" + this.ddd + ")");
		}
		
		textoBuilder.append(" ");
		textoBuilder.append(this.numero);
		
		if (this.ramal != null && !this.ramal.trim().isEmpty()) {
			textoBuilder.append(", Ramal: " + this.ramal);
		}
		
		return textoBuilder.toString();
	}
	
	@Override
	public void validate() throws BusinessException {
		if (this.descricao.trim().length() > 50) {
			throw new BusinessException("Campo 'Descrição' aceita no máximo 50 caracteres!");
		}
		
		if (this.ddd != null && this.ddd.trim().length() > 5) {
			throw new BusinessException("Campo 'DDD' aceita no máximo 5 caracteres!");
		}
		
		if (this.numero.trim().length() > 50) {
			throw new BusinessException("Campo 'Número' aceita no máximo 15 caracteres!");
		}
		
		if (this.ramal != null && this.ramal.trim().length() > 5) {
			throw new BusinessException("Campo 'Ramal' aceita no máximo 5 caracteres!");
		}
		
		if (this.usuario == null) {
			throw new BusinessException("Informe o usuário!");
		}
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDdd() {
		return ddd;
	}

	public void setDdd(String ddd) {
		this.ddd = ddd;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getRamal() {
		return ramal;
	}

	public void setRamal(String ramal) {
		this.ramal = ramal;
	}
}