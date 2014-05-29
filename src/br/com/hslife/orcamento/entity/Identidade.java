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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.hslife.orcamento.enumeration.TipoIdentidade;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="identidade")
@SuppressWarnings("serial")
public class Identidade extends EntityPersistence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=30, nullable=false)
	@Enumerated(EnumType.STRING)
	private TipoIdentidade tipoIdentidade;
	
	@Column(nullable=true, length=20)
	private String numero;
	
	@Column(nullable=true, length=50)
	private String orgaoExpedidor;
	
	@Column(nullable=true, length=100)
	private String municipio;
	
	@Column(nullable=true, length=2)
	private String uf;
	
	@Column(nullable=true, length=3)
	private String zona;
	
	@Column(nullable=true, length=4)
	private String secao;
	
	@Column(nullable=true, length=5)
	private String serie;
	
	@Column(nullable=true, length=5)
	private String livro;
	
	@Column(nullable=true, length=5)
	private String folha;
	
	@Column(nullable=true, length=5)
	private String categoria;
	
	@Column(nullable=true, length=30)
	private String pais;
	
	@Column(nullable=true)
	@Temporal(TemporalType.DATE)
	private Date dataExpedicao;
	
	@Column(nullable=true)
	@Temporal(TemporalType.DATE)
	private Date dataValidade;
	
	@Column(nullable=true)
	@Temporal(TemporalType.DATE)
	private Date dataPrimeiraHabilitacao;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	public Identidade() {
		
	}
	
	public Identidade(Usuario usuario, TipoIdentidade tipoIdentidade) {
		this.usuario = usuario;
		this.tipoIdentidade = tipoIdentidade;
	}

	public Long getId() {
		return id;
	}
	
	@Override
	public String getLabel() {
		return this.toString();
	}
	
	@Override
	public void validate() throws BusinessException {
		switch (this.tipoIdentidade) {
			case CARTEIRA_TRABALHO : validCarteiraTrabalho(); break;
			case CERTIDAO_NASCIMENTO : validCertidaoNascimento(); break;
			case CNH : validCnh(); break;
			case CPF : validCpf(); break; 
			case DOC_MILITAR : validDocMilitar(); break;
			case PASSAPORTE : validPassaporte(); break;
			case RG : validRg(); break;
			case TITULO_ELEITOR : validTituloEleitor(); break;
			default: ;// Não faz nada
		}
	}

	private void validTituloEleitor() throws BusinessException {
		// Verifica se todos os campos foram preenchidos no Título de Eleitor
		boolean resultado = Util.eVazio(this.getNumero()) && Util.eVazio(this.getMunicipio()) && Util.eVazio(this.getUf()) && Util.eVazio(this.getZona()) && Util.eVazio(this.getSecao()) && this.getDataExpedicao() == null;
		
		if (!resultado) {
			resultado = !Util.eVazio(this.getNumero()) && !Util.eVazio(this.getMunicipio()) && !Util.eVazio(this.getUf()) && !Util.eVazio(this.getZona()) && !Util.eVazio(this.getSecao()) && this.getDataExpedicao() != null;
			if (!resultado){
				throw new BusinessException("Todos os campos do título de eleitor são obrigatórios!");
			}
		}
	}

	private void validRg() throws BusinessException {
		// Verifica se todos os campos foram preenchidos no RG
		if ( (Util.eVazio(this.getNumero()) && Util.eVazio(this.getOrgaoExpedidor()) && Util.eVazio(this.getUf()) && this.getDataExpedicao() == null)
					|| (!Util.eVazio(this.getNumero()) && !Util.eVazio(this.getOrgaoExpedidor()) && !Util.eVazio(this.getUf()) && this.getDataExpedicao() != null)) {
				// Nada a fazer. Pode gravar sem problemas
			} else {
				throw new BusinessException("Todos os campos do RG são obrigatórios!");
			}		
	}

	private void validPassaporte() throws BusinessException {
		// Verifica se todos os campos foram preenchidos no passaporte
		boolean resultado = Util.eVazio(this.getNumero()) && Util.eVazio(this.getPais()) && Util.eVazio(this.getOrgaoExpedidor()) && this.getDataExpedicao() == null && this.getDataValidade() == null;
		
		if (!resultado) {
			resultado = !Util.eVazio(this.getNumero()) && !Util.eVazio(this.getPais()) && !Util.eVazio(this.getOrgaoExpedidor()) && this.getDataExpedicao() != null && this.getDataValidade() != null;
			if (!resultado) {
				throw new BusinessException("Todos os campos do passaporte são obrigatórios!");
			}
		}
	}

	private void validDocMilitar() throws BusinessException {
		// Verifica se todos os campos foram preenchidos no certificado de reservista
		boolean resultado = Util.eVazio(this.getNumero()) && Util.eVazio(this.getSerie()) && Util.eVazio(this.getOrgaoExpedidor()) && Util.eVazio(this.getMunicipio()) && Util.eVazio(this.getUf());
		
		if (!resultado) {
			resultado = !Util.eVazio(this.getNumero()) && !Util.eVazio(this.getSerie()) && !Util.eVazio(this.getOrgaoExpedidor()) && !Util.eVazio(this.getMunicipio()) && !Util.eVazio(this.getUf());
			if (!resultado) {
				throw new BusinessException("Todos os campos do certificado de reservista são obrigatórios!");
			}
		}
	}

	private void validCpf() throws BusinessException {
		// Verifica se a identidade passada é um CPF válido
		if (this.getNumero() != null && !this.getNumero().trim().isEmpty()) {
			if (!Util.validaCPF(this.getNumero()))
				throw new BusinessException("O CPF informado não é válido!");
		}		
	}

	private void validCnh() throws BusinessException {
		// Verifica se todos os campos foram preenchidos na carteira de motorista
		boolean resultado = Util.eVazio(this.getNumero()) && Util.eVazio(this.getCategoria()) && Util.eVazio(this.getMunicipio()) && Util.eVazio(this.getUf()) && this.getDataExpedicao() == null && this.getDataPrimeiraHabilitacao() ==  null && this.getDataValidade() == null;
		
		if (!resultado) {
			resultado = !Util.eVazio(this.getNumero()) && !Util.eVazio(this.getCategoria()) && !Util.eVazio(this.getMunicipio()) && !Util.eVazio(this.getUf()) && this.getDataExpedicao() != null && this.getDataPrimeiraHabilitacao() !=  null && this.getDataValidade() != null;
			if (!resultado)
				throw new BusinessException("Todos os campos da carteira de motorista são obrigatórios!");
		}
	}

	private void validCertidaoNascimento() throws BusinessException {
		// Verifica se todos os campos foram preenchidos na certidão de nascimento
		boolean resultado = Util.eVazio(this.getNumero()) && Util.eVazio(this.getOrgaoExpedidor()) && Util.eVazio(this.getLivro()) && Util.eVazio(this.getFolha()) && this.getDataExpedicao() == null;
		
		if (!resultado) {
			resultado = !Util.eVazio(this.getNumero()) && !Util.eVazio(this.getOrgaoExpedidor()) && !Util.eVazio(this.getLivro()) && !Util.eVazio(this.getFolha()) && this.getDataExpedicao() != null;
			if (!resultado) {
				throw new BusinessException("Todos os campos da certidão de nascimento são obrigatórios!");
			}
		}
	}

	private void validCarteiraTrabalho() throws BusinessException {
		// Verifica se todos os campos foram preenchidos na carteira de trabalho
		boolean resultado = Util.eVazio(this.getNumero()) && Util.eVazio(this.getSerie()) && Util.eVazio(this.getOrgaoExpedidor()) && Util.eVazio(this.getUf()) && this.getDataExpedicao() == null;
		
		if (!resultado) {
			resultado = !Util.eVazio(this.getNumero()) && !Util.eVazio(this.getSerie()) && !Util.eVazio(this.getOrgaoExpedidor()) && !Util.eVazio(this.getUf()) && this.getDataExpedicao() != null;
			if (!resultado) {
				throw new BusinessException("Todos os campos da carteira de trabalho são obrigatórios!");
			}
		}
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TipoIdentidade getTipoIdentidade() {
		return tipoIdentidade;
	}

	public void setTipoIdentidade(TipoIdentidade tipoIdentidade) {
		this.tipoIdentidade = tipoIdentidade;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getOrgaoExpedidor() {
		return orgaoExpedidor;
	}

	public void setOrgaoExpedidor(String orgaoExpedidor) {
		this.orgaoExpedidor = orgaoExpedidor;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public Date getDataExpedicao() {
		return dataExpedicao;
	}

	public void setDataExpedicao(Date dataExpedicao) {
		this.dataExpedicao = dataExpedicao;
	}

	public String getZona() {
		return zona;
	}

	public void setZona(String zona) {
		this.zona = zona;
	}

	public String getSecao() {
		return secao;
	}

	public void setSecao(String secao) {
		this.secao = secao;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getLivro() {
		return livro;
	}

	public void setLivro(String livro) {
		this.livro = livro;
	}

	public String getFolha() {
		return folha;
	}

	public void setFolha(String folha) {
		this.folha = folha;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public Date getDataValidade() {
		return dataValidade;
	}

	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
	}

	public Date getDataPrimeiraHabilitacao() {
		return dataPrimeiraHabilitacao;
	}

	public void setDataPrimeiraHabilitacao(Date dataPrimeiraHabilitacao) {
		this.dataPrimeiraHabilitacao = dataPrimeiraHabilitacao;
	}
}