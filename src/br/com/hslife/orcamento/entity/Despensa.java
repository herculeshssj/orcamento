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

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import br.com.hslife.orcamento.exception.BusinessException;

@Entity
@Table(name="despensa")
@SuppressWarnings("serial")
public class Despensa extends EntityPersistence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;	
	
	@Column(length=50, nullable=false)	
	private String descricao;
	
	@Column
	private String localArmazenamento;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	@OneToMany(mappedBy="despensa", fetch=FetchType.EAGER)
	@Fetch(value=FetchMode.SELECT)
	private List<ItemDespensa> itensDespensa;
	
	@Override
	public String getLabel() {
		return this.descricao;
	}
	
	@Override
	public void validate() throws BusinessException {
		if (this.descricao == null || this.descricao.trim().isEmpty()) {
			throw new BusinessException("Informe uma descrição!");
		}
		
		if (this.usuario == null) {
			throw new BusinessException("Informe o usuário!");
		}
		
		if (this.localArmazenamento != null && this.localArmazenamento.length() > 255) {
			throw new BusinessException("Campo 'local de armazenamento' aceita somente 255 caracteres!");
		}
	}
	
	public String getQuantidadeItens() {
		if (itensDespensa ==  null) {
			return "0";
		} else {
			return Integer.toString(itensDespensa.size());
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

	public String getLocalArmazenamento() {
		return localArmazenamento;
	}

	public void setLocalArmazenamento(String localArmazenamento) {
		this.localArmazenamento = localArmazenamento;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<ItemDespensa> getItensDespensa() {
		return itensDespensa;
	}

	public void setItensDespensa(List<ItemDespensa> itensDespensa) {
		this.itensDespensa = itensDespensa;
	}	
}