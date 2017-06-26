/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da 

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor
	
	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

    para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 - 
	
	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/

package br.com.hslife.orcamento.rest.json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Banco;
import br.com.hslife.orcamento.exception.BusinessException;

@XmlRootElement
@Component
public class BancoJson extends AbstractJson {

	private Long id;
	private String nome;
	private String numero;
	private Boolean padrao;
	private Boolean ativo;
	private String usuario;
	private Long usuarioId;
	
	public BancoJson() {
		
	}
	
	@Override
	public Banco toEntity() {
		// Busco o banco por ID e por usuário
		// Fazer a busca em conjunto é importante para evitar um usuário ver os dados de outro
		Banco banco = (Banco)getSession()
				.createQuery("FROM Banco banco WHERE banco.id = :idBanco AND banco.usuario.id = :idUsuario")
				.setLong("idBanco", this.id)
				.setLong("idUsuario", this.usuarioId)
				.uniqueResult();
		
		if (banco != null) {
			// Atribui os valores do JSon na entidade, com exceção do usuário.
			banco.setAtivo(this.ativo);
			banco.setNome(this.nome);
			banco.setNumero(this.numero);
			banco.setPadrao(this.padrao);
		} else {
			throw new BusinessException("Entidade com dados inválidos!");
		}
		
		return banco;
	}
	
	public static List<Banco> toListEntity(List<BancoJson> listJson, SessionFactory sessionFactory) {
		List<Banco> listEntity = new ArrayList<>();
		for (BancoJson bancoJson : listJson) {
			bancoJson.setSessionFactory(sessionFactory);			
			listEntity.add(bancoJson.toEntity());
		}
		return listEntity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Boolean getPadrao() {
		return padrao;
	}

	public void setPadrao(Boolean padrao) {
		this.padrao = padrao;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
}