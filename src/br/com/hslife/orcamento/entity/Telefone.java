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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.hslife.orcamento.exception.BusinessException;

@Entity
@Table(name="pessoal")
@SuppressWarnings("serial")
public class Telefone extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=1, nullable=true)
	private char genero;
	
	@Column(length=50, nullable=true)
	private String etnia;
	
	@Column(length=5, nullable=true)
	private String tipoSanguineo;
	
	@Column(nullable=true)
	@Temporal(TemporalType.DATE)
	private Date dataNascimento;
	
	@Column(length=50, nullable=true)	
	private String nacionalidade;
	
	@Column(length=50, nullable=true)
	private String naturalidade;
	
	@Column(length=50, nullable=true)
	private String escolaridade;
	
	@Column(length=100, nullable=true)
	private String filiacaoPai;
	
	@Column(length=100, nullable=true)
	private String filiacaoMae;
	
	@Column(length=50, nullable=true)
	private String estadoCivil;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	public Telefone() {
		genero = 'M';
	}

	@Override
	public String getLabel() {		
		return this.usuario.getNome();
	}
	
	@Override
	public void validate() throws BusinessException {
		if (this.genero != 'M' && this.genero != 'F') {
			throw new BusinessException("Gênero inexistente!");
		}
		
		if (this.etnia != null && this.etnia.trim().length() > 50) {
			throw new BusinessException("Campo aceita no máximo 50 caracteres!");
		}
		
		if (this.tipoSanguineo != null && this.tipoSanguineo.trim().length() > 5) {
			throw new BusinessException("Campo aceita no máximo 5 caracteres!");
		}
		
		if (this.naturalidade != null && this.naturalidade.trim().length() > 50) {
			throw new BusinessException("Campo aceita no máximo 50 caracteres!");
		}
		
		if (this.nacionalidade != null && this.nacionalidade.trim().length() > 50) {
			throw new BusinessException("Campo aceita no máximo 50 caracteres!");
		}
		
		if (this.escolaridade != null && this.escolaridade.trim().length() > 50) {
			throw new BusinessException("Campo aceita no máximo 50 caracteres!");
		}
		
		if (this.filiacaoPai != null && this.filiacaoPai.trim().length() > 100) {
			throw new BusinessException("Campo aceita no máximo 100 caracteres!");
		}
		
		if (this.filiacaoMae != null && this.filiacaoMae.trim().length() > 100) {
			throw new BusinessException("Campo aceita no máximo 100 caracteres!");
		}
		
		if (this.estadoCivil != null && this.estadoCivil.trim().length() > 50) {
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

	public char getGenero() {
		return genero;
	}

	public void setGenero(char genero) {
		this.genero = genero;
	}

	public String getEtnia() {
		return etnia;
	}

	public void setEtnia(String etnia) {
		this.etnia = etnia;
	}

	public String getTipoSanguineo() {
		return tipoSanguineo;
	}

	public void setTipoSanguineo(String tipoSanguineo) {
		this.tipoSanguineo = tipoSanguineo;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getNacionalidade() {
		return nacionalidade;
	}

	public void setNacionalidade(String nacionalidade) {
		this.nacionalidade = nacionalidade;
	}

	public String getNaturalidade() {
		return naturalidade;
	}

	public void setNaturalidade(String naturalidade) {
		this.naturalidade = naturalidade;
	}

	public String getEscolaridade() {
		return escolaridade;
	}

	public void setEscolaridade(String escolaridade) {
		this.escolaridade = escolaridade;
	}

	public String getFiliacaoPai() {
		return filiacaoPai;
	}

	public void setFiliacaoPai(String filiacaoPai) {
		this.filiacaoPai = filiacaoPai;
	}

	public String getFiliacaoMae() {
		return filiacaoMae;
	}

	public void setFiliacaoMae(String filiacaoMae) {
		this.filiacaoMae = filiacaoMae;
	}

	public String getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}