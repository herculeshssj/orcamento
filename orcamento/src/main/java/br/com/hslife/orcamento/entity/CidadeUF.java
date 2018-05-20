package br.com.hslife.orcamento.entity;

import br.com.hslife.orcamento.util.Util;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="cidadeuf")
public class CidadeUF extends EntityPersistence {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=true, length=100)
	private String cidade;
	
	@Column(nullable=false, length=2)
	private String uf;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "cidadeUF")
	private List<Lotofacil> lotofacil;
	
	public CidadeUF() {
		// TODO Auto-generated constructor stub
	}
	
	public CidadeUF(String cidade, String uf) {
		this.cidade = cidade;
		this.uf = uf;
	}

	@Override
	public void validate() {

	}

	@Override
	public String getLabel() {
		return Util.concatenar(
				this.cidade,
				" - ",
				this.uf
		);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}
}