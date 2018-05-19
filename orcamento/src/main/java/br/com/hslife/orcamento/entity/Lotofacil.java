package br.com.hslife.orcamento.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "lotofacil")
public class Lotofacil extends AbstractModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private int concurso;

	@Column(name = "datasorteio", nullable = false)
	@Temporal(TemporalType.DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date dataSorteio;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "lotofacil_cidadeuf", 
		joinColumns = @JoinColumn(name = "id_lotofacil", nullable=false), 
			inverseJoinColumns = @JoinColumn(name = "id_cidadeuf"))
	private List<CidadeUF> cidadeUF;

	@Column(nullable = false)
	private int bola1;

	@Column(nullable = false)
	private int bola2;

	@Column(nullable = false)
	private int bola3;

	@Column(nullable = false)
	private int bola4;

	@Column(nullable = false)
	private int bola5;

	@Column(nullable = false)
	private int bola6;

	@Column(nullable = false)
	private int bola7;

	@Column(nullable = false)
	private int bola8;

	@Column(nullable = false)
	private int bola9;

	@Column(nullable = false)
	private int bola10;

	@Column(nullable = false)
	private int bola11;

	@Column(nullable = false)
	private int bola12;

	@Column(nullable = false)
	private int bola13;

	@Column(nullable = false)
	private int bola14;

	@Column(nullable = false)
	private int bola15;

	@Column(name = "arrecadacaototal", nullable = false)
	private double arrecadacaoTotal;

	@Column(name = "ganhadores15numeros", nullable = false)
	private int ganhadores15Numeros;

	@Column(name = "ganhadores14numeros", nullable = false)
	private int ganhadores14Numeros;

	@Column(name = "ganhadores13numeros", nullable = false)
	private int ganhadores13Numeros;

	@Column(name = "ganhadores12numeros", nullable = false)
	private int ganhadores12Numeros;

	@Column(name = "ganhadores11numeros", nullable = false)
	private int ganhadores11Numeros;

	@Column(name = "valorrateio15numeros", nullable = false)
	private double valorRateio15Numeros;

	@Column(name = "valorrateio14numeros", nullable = false)
	private double valorRateio14Numeros;

	@Column(name = "valorrateio13numeros", nullable = false)
	private double valorRateio13Numeros;

	@Column(name = "valorrateio12numeros", nullable = false)
	private double valorRateio12Numeros;

	@Column(name = "valorrateio11numeros", nullable = false)
	private double valorRateio11Numeros;

	@Column(name = "acumulado15numeros", nullable = false)
	private double acumulado15Numeros;

	@Column(name = "estimativapremio", nullable = false)
	private double estimativaPremio;

	@Column(name = "valoracumuladoespecial", nullable = false)
	private double valorAcumuladoEspecial;
	
	public Lotofacil() {
		this.cidadeUF = new ArrayList<>();
	}

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

	public int getBola1() {
		return bola1;
	}

	public void setBola1(int bola1) {
		this.bola1 = bola1;
	}

	public int getBola2() {
		return bola2;
	}

	public void setBola2(int bola2) {
		this.bola2 = bola2;
	}

	public int getBola3() {
		return bola3;
	}

	public void setBola3(int bola3) {
		this.bola3 = bola3;
	}

	public int getBola4() {
		return bola4;
	}

	public void setBola4(int bola4) {
		this.bola4 = bola4;
	}

	public int getBola5() {
		return bola5;
	}

	public void setBola5(int bola5) {
		this.bola5 = bola5;
	}

	public int getBola6() {
		return bola6;
	}

	public void setBola6(int bola6) {
		this.bola6 = bola6;
	}

	public int getBola7() {
		return bola7;
	}

	public void setBola7(int bola7) {
		this.bola7 = bola7;
	}

	public int getBola8() {
		return bola8;
	}

	public void setBola8(int bola8) {
		this.bola8 = bola8;
	}

	public int getBola9() {
		return bola9;
	}

	public void setBola9(int bola9) {
		this.bola9 = bola9;
	}

	public int getBola10() {
		return bola10;
	}

	public void setBola10(int bola10) {
		this.bola10 = bola10;
	}

	public int getBola11() {
		return bola11;
	}

	public void setBola11(int bola11) {
		this.bola11 = bola11;
	}

	public int getBola12() {
		return bola12;
	}

	public void setBola12(int bola12) {
		this.bola12 = bola12;
	}

	public int getBola13() {
		return bola13;
	}

	public void setBola13(int bola13) {
		this.bola13 = bola13;
	}

	public int getBola14() {
		return bola14;
	}

	public void setBola14(int bola14) {
		this.bola14 = bola14;
	}

	public int getBola15() {
		return bola15;
	}

	public void setBola15(int bola15) {
		this.bola15 = bola15;
	}

	public double getArrecadacaoTotal() {
		return arrecadacaoTotal;
	}

	public void setArrecadacaoTotal(double arrecadacaoTotal) {
		this.arrecadacaoTotal = arrecadacaoTotal;
	}

	public int getGanhadores15Numeros() {
		return ganhadores15Numeros;
	}

	public void setGanhadores15Numeros(int ganhadores15Numeros) {
		this.ganhadores15Numeros = ganhadores15Numeros;
	}

	public int getGanhadores14Numeros() {
		return ganhadores14Numeros;
	}

	public void setGanhadores14Numeros(int ganhadores14Numeros) {
		this.ganhadores14Numeros = ganhadores14Numeros;
	}

	public int getGanhadores13Numeros() {
		return ganhadores13Numeros;
	}

	public void setGanhadores13Numeros(int ganhadores13Numeros) {
		this.ganhadores13Numeros = ganhadores13Numeros;
	}

	public int getGanhadores12Numeros() {
		return ganhadores12Numeros;
	}

	public void setGanhadores12Numeros(int ganhadores12Numeros) {
		this.ganhadores12Numeros = ganhadores12Numeros;
	}

	public int getGanhadores11Numeros() {
		return ganhadores11Numeros;
	}

	public void setGanhadores11Numeros(int ganhadores11Numeros) {
		this.ganhadores11Numeros = ganhadores11Numeros;
	}

	public double getValorRateio15Numeros() {
		return valorRateio15Numeros;
	}

	public void setValorRateio15Numeros(double valorRateio15Numeros) {
		this.valorRateio15Numeros = valorRateio15Numeros;
	}

	public double getValorRateio14Numeros() {
		return valorRateio14Numeros;
	}

	public void setValorRateio14Numeros(double valorRateio14Numeros) {
		this.valorRateio14Numeros = valorRateio14Numeros;
	}

	public double getValorRateio13Numeros() {
		return valorRateio13Numeros;
	}

	public void setValorRateio13Numeros(double valorRateio13Numeros) {
		this.valorRateio13Numeros = valorRateio13Numeros;
	}

	public double getValorRateio12Numeros() {
		return valorRateio12Numeros;
	}

	public void setValorRateio12Numeros(double valorRateio12Numeros) {
		this.valorRateio12Numeros = valorRateio12Numeros;
	}

	public double getValorRateio11Numeros() {
		return valorRateio11Numeros;
	}

	public void setValorRateio11Numeros(double valorRateio11Numeros) {
		this.valorRateio11Numeros = valorRateio11Numeros;
	}

	public double getAcumulado15Numeros() {
		return acumulado15Numeros;
	}

	public void setAcumulado15Numeros(double acumulado15Numeros) {
		this.acumulado15Numeros = acumulado15Numeros;
	}

	public double getEstimativaPremio() {
		return estimativaPremio;
	}

	public void setEstimativaPremio(double estimativaPremio) {
		this.estimativaPremio = estimativaPremio;
	}

	public double getValorAcumuladoEspecial() {
		return valorAcumuladoEspecial;
	}

	public void setValorAcumuladoEspecial(double valorAcumuladoEspecial) {
		this.valorAcumuladoEspecial = valorAcumuladoEspecial;
	}

	public List<CidadeUF> getCidadeUF() {
		return cidadeUF;
	}

	public void setCidadeUF(List<CidadeUF> cidadeUF) {
		this.cidadeUF = cidadeUF;
	}
}