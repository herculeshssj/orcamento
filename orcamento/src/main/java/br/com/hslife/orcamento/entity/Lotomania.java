package br.com.hslife.orcamento.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "lotomania")
public class Lotomania extends AbstractModel {

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
	@JoinTable(name = "lotomania_cidadeuf", 
		joinColumns = @JoinColumn(name = "id_lotomania", nullable=false), 
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
	
	@Column(nullable = false)
	private int bola16;
	
	@Column(nullable = false)
	private int bola17;
	
	@Column(nullable = false)
	private int bola18;
	
	@Column(nullable = false)
	private int bola19;
	
	@Column(nullable = false)
	private int bola20;

	@Column(name = "arrecadacaototal", nullable = false)
	private double arrecadacaoTotal;

	@Column(name = "ganhadores20numeros", nullable = false)
	private int ganhadores20Numeros;

	@Column(name = "ganhadores19numeros", nullable = false)
	private int ganhadores19Numeros;

	@Column(name = "ganhadores18numeros", nullable = false)
	private int ganhadores18Numeros;

	@Column(name = "ganhadores17numeros", nullable = false)
	private int ganhadores17Numeros;

	@Column(name = "ganhadores16numeros", nullable = false)
	private int ganhadores16Numeros;
	
	@Column(name = "ganhadoresnenhumnumero", nullable = false)
	private int ganhadoresNenhumNumero;

	@Column(name = "valorrateio20numeros", nullable = false)
	private double valorRateio20Numeros;

	@Column(name = "valorrateio19numeros", nullable = false)
	private double valorRateio19Numeros;

	@Column(name = "valorrateio18numeros", nullable = false)
	private double valorRateio18Numeros;

	@Column(name = "valorrateio17numeros", nullable = false)
	private double valorRateio17Numeros;

	@Column(name = "valorrateio16numeros", nullable = false)
	private double valorRateio16Numeros;
	
	@Column(name = "valorrateionenhumnumero", nullable = false)
	private double valorRateioNenhumNumero;

	@Column(name = "acumulado20numeros", nullable = false)
	private double acumulado20Numeros;
	
	@Column(name = "acumulado19numeros", nullable = false)
	private double acumulado19Numeros;
	
	@Column(name = "acumulado18numeros", nullable = false)
	private double acumulado18Numeros;
	
	@Column(name = "acumulado17numeros", nullable = false)
	private double acumulado17Numeros;
	
	@Column(name = "acumulado16numeros", nullable = false)
	private double acumulado16Numeros;
	
	@Column(name = "acumuladonenhumnumero", nullable = false)
	private double acumuladoNenhumNumero;

	@Column(name = "estimativapremio", nullable = false)
	private double estimativaPremio;

	@Column(name = "valoracumuladoespecial", nullable = false)
	private double valorAcumuladoEspecial;
	
	public Lotomania() {
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

	public List<CidadeUF> getCidadeUF() {
		return cidadeUF;
	}

	public void setCidadeUF(List<CidadeUF> cidadeUF) {
		this.cidadeUF = cidadeUF;
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

	public int getBola16() {
		return bola16;
	}

	public void setBola16(int bola16) {
		this.bola16 = bola16;
	}

	public int getBola17() {
		return bola17;
	}

	public void setBola17(int bola17) {
		this.bola17 = bola17;
	}

	public int getBola18() {
		return bola18;
	}

	public void setBola18(int bola18) {
		this.bola18 = bola18;
	}

	public int getBola19() {
		return bola19;
	}

	public void setBola19(int bola19) {
		this.bola19 = bola19;
	}

	public int getBola20() {
		return bola20;
	}

	public void setBola20(int bola20) {
		this.bola20 = bola20;
	}

	public double getArrecadacaoTotal() {
		return arrecadacaoTotal;
	}

	public void setArrecadacaoTotal(double arrecadacaoTotal) {
		this.arrecadacaoTotal = arrecadacaoTotal;
	}

	public int getGanhadores20Numeros() {
		return ganhadores20Numeros;
	}

	public void setGanhadores20Numeros(int ganhadores20Numeros) {
		this.ganhadores20Numeros = ganhadores20Numeros;
	}

	public int getGanhadores19Numeros() {
		return ganhadores19Numeros;
	}

	public void setGanhadores19Numeros(int ganhadores19Numeros) {
		this.ganhadores19Numeros = ganhadores19Numeros;
	}

	public int getGanhadores18Numeros() {
		return ganhadores18Numeros;
	}

	public void setGanhadores18Numeros(int ganhadores18Numeros) {
		this.ganhadores18Numeros = ganhadores18Numeros;
	}

	public int getGanhadores17Numeros() {
		return ganhadores17Numeros;
	}

	public void setGanhadores17Numeros(int ganhadores17Numeros) {
		this.ganhadores17Numeros = ganhadores17Numeros;
	}

	public int getGanhadores16Numeros() {
		return ganhadores16Numeros;
	}

	public void setGanhadores16Numeros(int ganhadores16Numeros) {
		this.ganhadores16Numeros = ganhadores16Numeros;
	}

	public int getGanhadoresNenhumNumero() {
		return ganhadoresNenhumNumero;
	}

	public void setGanhadoresNenhumNumero(int ganhadoresNenhumNumero) {
		this.ganhadoresNenhumNumero = ganhadoresNenhumNumero;
	}

	public double getValorRateio20Numeros() {
		return valorRateio20Numeros;
	}

	public void setValorRateio20Numeros(double valorRateio20Numeros) {
		this.valorRateio20Numeros = valorRateio20Numeros;
	}

	public double getValorRateio19Numeros() {
		return valorRateio19Numeros;
	}

	public void setValorRateio19Numeros(double valorRateio19Numeros) {
		this.valorRateio19Numeros = valorRateio19Numeros;
	}

	public double getValorRateio18Numeros() {
		return valorRateio18Numeros;
	}

	public void setValorRateio18Numeros(double valorRateio18Numeros) {
		this.valorRateio18Numeros = valorRateio18Numeros;
	}

	public double getValorRateio17Numeros() {
		return valorRateio17Numeros;
	}

	public void setValorRateio17Numeros(double valorRateio17Numeros) {
		this.valorRateio17Numeros = valorRateio17Numeros;
	}

	public double getValorRateio16Numeros() {
		return valorRateio16Numeros;
	}

	public void setValorRateio16Numeros(double valorRateio16Numeros) {
		this.valorRateio16Numeros = valorRateio16Numeros;
	}

	public double getValorRateioNenhumNumero() {
		return valorRateioNenhumNumero;
	}

	public void setValorRateioNenhumNumero(double valorRateioNenhumNumero) {
		this.valorRateioNenhumNumero = valorRateioNenhumNumero;
	}

	public double getAcumulado20Numeros() {
		return acumulado20Numeros;
	}

	public void setAcumulado20Numeros(double acumulado20Numeros) {
		this.acumulado20Numeros = acumulado20Numeros;
	}

	public double getAcumulado19Numeros() {
		return acumulado19Numeros;
	}

	public void setAcumulado19Numeros(double acumulado19Numeros) {
		this.acumulado19Numeros = acumulado19Numeros;
	}

	public double getAcumulado18Numeros() {
		return acumulado18Numeros;
	}

	public void setAcumulado18Numeros(double acumulado18Numeros) {
		this.acumulado18Numeros = acumulado18Numeros;
	}

	public double getAcumulado17Numeros() {
		return acumulado17Numeros;
	}

	public void setAcumulado17Numeros(double acumulado17Numeros) {
		this.acumulado17Numeros = acumulado17Numeros;
	}

	public double getAcumulado16Numeros() {
		return acumulado16Numeros;
	}

	public void setAcumulado16Numeros(double acumulado16Numeros) {
		this.acumulado16Numeros = acumulado16Numeros;
	}

	public double getAcumuladoNenhumNumero() {
		return acumuladoNenhumNumero;
	}

	public void setAcumuladoNenhumNumero(double acumuladoNenhumNumero) {
		this.acumuladoNenhumNumero = acumuladoNenhumNumero;
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
}