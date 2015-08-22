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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Entity
@Table(name="pessoal")
@SuppressWarnings("serial")
public class Pessoal extends EntityPersistence {

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
	
	public Pessoal() {
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
		
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Etnia", this.etnia, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Tipo Sanguíneo", this.tipoSanguineo, 5);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Naturalidade", this.naturalidade, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Nacionalidade", this.nacionalidade, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Escolaridade", this.escolaridade, 50);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Filiação Pai", this.filiacaoPai, 100);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Filiação Mãe", this.filiacaoMae, 100);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Estado Civil", this.estadoCivil, 50);
		
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