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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.hslife.orcamento.exception.BusinessException;

@Entity
@Table(name="endereco")
@SuppressWarnings("serial")
public class Endereco extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50, nullable=false)
	private String descricao;
	
	@Column(length=30, nullable=false)
	private String tipoLogradouro;
	
	@Column(length=150, nullable=false)
	private String logradouro;
	
	@Column(length=10, nullable=true)
	private String numero;
	
	@Column(length=50, nullable=true)
	private String complemento;
	
	@Column(length=50, nullable=false)
	private String bairro;
	
	@Column(length=100, nullable=false)
	private String cidade;
	
	@Column(length=2, nullable=false)
	private String estado;
	
	@Column(length=8, nullable=true)
	private String cep;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	public Endereco() {
		
	}

	@Override
	public String getLabel() {
		StringBuilder textoBuild = new StringBuilder();
		
		textoBuild.append(descricao);
		textoBuild.append(": ");
		textoBuild.append(tipoLogradouro);
		textoBuild.append(" ");
		textoBuild.append(logradouro);
		textoBuild.append(", ");
		textoBuild.append(numero);
		
		if (complemento != null && !complemento.trim().isEmpty()) {
			textoBuild.append(" - ");
			textoBuild.append(complemento);
		}
			
		textoBuild.append(" - ");
		textoBuild.append(bairro);
		textoBuild.append(" - ");
		textoBuild.append(cidade);
		textoBuild.append(", ");
		textoBuild.append(estado);
		
		if (cep != null && !cep.trim().isEmpty()) {
			textoBuild.append(" - CEP: ");
			textoBuild.append(cep);
		}
		
		return textoBuild.toString();
	}
	
	@Override
	public void validate() throws BusinessException {
		if (this.tipoLogradouro.trim().length() > 50) {
			throw new BusinessException("Campo aceita no máximo 50 caracteres!");
		}
		
		if (this.logradouro.trim().length() > 150) {
			throw new BusinessException("Campo aceita no máximo 150 caracteres!");
		}
		
		if (this.numero != null && this.numero.trim().length() > 10) {
			throw new BusinessException("Campo aceita no máximo 10 caracteres!");
		}
		
		if (this.complemento != null && this.complemento.trim().length() > 50) {
			throw new BusinessException("Campo aceita no máximo 50 caracteres!");
		}
		
		if (this.bairro.trim().length() > 50) {
			throw new BusinessException("Campo aceita no máximo 50 caracteres!");
		}
		
		if (this.cidade.trim().length() > 100) {
			throw new BusinessException("Campo aceita no máximo 100 caracteres!");
		}
		
		if (this.estado.trim().length() != 2) {
			throw new BusinessException("Campo aceita exatamente 2 caracteres!");
		}
		
		if (this.cep != null && this.cep.trim().length() != 8) {
			throw new BusinessException("Campo aceita exatamente 8 caracteres!");
		}
		
		if (this.descricao.trim().length() > 50) {
			throw new BusinessException("Campo aceita no máximo 50 caracteres!");
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTipoLogradouro() {
		return tipoLogradouro;
	}

	public void setTipoLogradouro(String tipoLogradouro) {
		this.tipoLogradouro = tipoLogradouro;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}