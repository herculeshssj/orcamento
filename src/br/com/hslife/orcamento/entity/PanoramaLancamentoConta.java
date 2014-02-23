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
import javax.persistence.Transient;

import br.com.hslife.orcamento.exception.BusinessException;

@Entity
@Table(name="panoramalancamentoconta")
@SuppressWarnings("serial")
public class PanoramaLancamentoConta extends EntityPersistence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Transient
	private String oid;
	
	@Column
	private int ano;
	
	@Column(nullable=false)
	private String descricao;
	
	@ManyToOne
	@JoinColumn(name="idConta", nullable=false)
	private Conta conta;
	
	@Column(nullable=false, precision=18, scale=2)
	private double janeiro;
	
	@Column(nullable=false, precision=18, scale=2)
	private double fevereiro;
	
	@Column(nullable=false, precision=18, scale=2)
	private double marco;
	
	@Column(nullable=false, precision=18, scale=2)
	private double abril;
	
	@Column(nullable=false, precision=18, scale=2)
	private double maio;
	
	@Column(nullable=false, precision=18, scale=2)
	private double junho;
	
	@Column(nullable=false, precision=18, scale=2)
	private double julho;
	
	@Column(nullable=false, precision=18, scale=2)
	private double agosto;
	
	@Column(nullable=false, precision=18, scale=2)
	private double setembro;
	
	@Column(nullable=false, precision=18, scale=2)
	private double outubro;
	
	@Column(nullable=false, precision=18, scale=2)
	private double novembro;
	
	@Column(nullable=false, precision=18, scale=2)
	private double dezembro;
	
	@Column(nullable=false)
	private int indice;
	
	public PanoramaLancamentoConta() {
		
	}

	@Override
	public String getLabel() {
		return descricao;
	}

	@Override
	public void validate() throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public double getJaneiro() {
		return janeiro;
	}

	public void setJaneiro(double janeiro) {
		this.janeiro = janeiro;
	}

	public double getFevereiro() {
		return fevereiro;
	}

	public void setFevereiro(double fevereiro) {
		this.fevereiro = fevereiro;
	}

	public double getMarco() {
		return marco;
	}

	public void setMarco(double marco) {
		this.marco = marco;
	}

	public double getAbril() {
		return abril;
	}

	public void setAbril(double abril) {
		this.abril = abril;
	}

	public double getMaio() {
		return maio;
	}

	public void setMaio(double maio) {
		this.maio = maio;
	}

	public double getJunho() {
		return junho;
	}

	public void setJunho(double junho) {
		this.junho = junho;
	}

	public double getJulho() {
		return julho;
	}

	public void setJulho(double julho) {
		this.julho = julho;
	}

	public double getAgosto() {
		return agosto;
	}

	public void setAgosto(double agosto) {
		this.agosto = agosto;
	}

	public double getSetembro() {
		return setembro;
	}

	public void setSetembro(double setembro) {
		this.setembro = setembro;
	}

	public double getOutubro() {
		return outubro;
	}

	public void setOutubro(double outubro) {
		this.outubro = outubro;
	}

	public double getNovembro() {
		return novembro;
	}

	public void setNovembro(double novembro) {
		this.novembro = novembro;
	}

	public double getDezembro() {
		return dezembro;
	}

	public void setDezembro(double dezembro) {
		this.dezembro = dezembro;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}