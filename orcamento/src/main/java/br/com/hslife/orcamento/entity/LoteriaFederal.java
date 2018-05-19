package br.com.hslife.orcamento.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="federal")
public class LoteriaFederal extends AbstractModel {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false)
	private int concurso;
	
	@Column(name="datasorteio", nullable=false)
	@Temporal(TemporalType.DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date dataSorteio;
	
	@Column(name="primeiropremio", nullable=false)
	private int primeiroPremio;
	
	@Column(name="segundopremio", nullable=false)
	private int segundoPremio;
	
	@Column(name="terceiropremio", nullable=false)
	private int terceiroPremio;
	
	@Column(name="quartopremio", nullable=false)
	private int quartoPremio;
	
	@Column(name="quintapremio", nullable=false)
	private int quintaPremio;
	
	@Column(name="valorprimeiropremio", nullable=false)
	private double valorPrimeiroPremio;
	
	@Column(name="valorsegundopremio", nullable=false)
	private double valorSegundoPremio;
	
	@Column(name="valorterceiropremio", nullable=false)
	private double valorTerceiroPremio;
	
	@Column(name="valorquartopremio", nullable=false)
	private double valorQuartoPremio;
	
	@Column(name="valorquintopremio", nullable=false)
	private double valorQuintoPremio;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getConcurso() {
		return concurso;
	}

	public void setConcurso(int concurso) {
		this.concurso = concurso;
	}

	public Date getDataSorteio() {
		return dataSorteio;
	}

	public void setDataSorteio(Date dataSorteio) {
		this.dataSorteio = dataSorteio;
	}

	public int getPrimeiroPremio() {
		return primeiroPremio;
	}

	public void setPrimeiroPremio(int primeiroPremio) {
		this.primeiroPremio = primeiroPremio;
	}

	public int getSegundoPremio() {
		return segundoPremio;
	}

	public void setSegundoPremio(int segundoPremio) {
		this.segundoPremio = segundoPremio;
	}

	public int getTerceiroPremio() {
		return terceiroPremio;
	}

	public void setTerceiroPremio(int terceiroPremio) {
		this.terceiroPremio = terceiroPremio;
	}

	public int getQuartoPremio() {
		return quartoPremio;
	}

	public void setQuartoPremio(int quartoPremio) {
		this.quartoPremio = quartoPremio;
	}

	public int getQuintaPremio() {
		return quintaPremio;
	}

	public void setQuintaPremio(int quintaPremio) {
		this.quintaPremio = quintaPremio;
	}

	public double getValorPrimeiroPremio() {
		return valorPrimeiroPremio;
	}

	public void setValorPrimeiroPremio(double valorPrimeiroPremio) {
		this.valorPrimeiroPremio = valorPrimeiroPremio;
	}

	public double getValorSegundoPremio() {
		return valorSegundoPremio;
	}

	public void setValorSegundoPremio(double valorSegundoPremio) {
		this.valorSegundoPremio = valorSegundoPremio;
	}

	public double getValorTerceiroPremio() {
		return valorTerceiroPremio;
	}

	public void setValorTerceiroPremio(double valorTerceiroPremio) {
		this.valorTerceiroPremio = valorTerceiroPremio;
	}

	public double getValorQuartoPremio() {
		return valorQuartoPremio;
	}

	public void setValorQuartoPremio(double valorQuartoPremio) {
		this.valorQuartoPremio = valorQuartoPremio;
	}

	public double getValorQuintoPremio() {
		return valorQuintoPremio;
	}

	public void setValorQuintoPremio(double valorQuintoPremio) {
		this.valorQuintoPremio = valorQuintoPremio;
	}
}