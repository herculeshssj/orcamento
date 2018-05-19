package br.com.hslife.orcamento.component;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="loteria")
public class LoteriaProperties {

	/*
	urlFederal: http://www1.caixa.gov.br/loterias/_arquivos/loterias/D_federa.zip
  urlLotofacil: http://www1.caixa.gov.br/loterias/_arquivos/loterias/D_lotfac.zip
  urlLotomania: http://www1.caixa.gov.br/loterias/_arquivos/loterias/D_lotoma.zip
	 */
	
	private String urlFederal;
	private String urlLotofacil;
	private String urlLotomania;

	public String getUrlFederal() {
		return urlFederal;
	}

	public void setUrlFederal(String urlFederal) {
		this.urlFederal = urlFederal;
	}

	public String getUrlLotofacil() {
		return urlLotofacil;
	}

	public void setUrlLotofacil(String urlLotofacil) {
		this.urlLotofacil = urlLotofacil;
	}

	public String getUrlLotomania() {
		return urlLotomania;
	}

	public void setUrlLotomania(String urlLotomania) {
		this.urlLotomania = urlLotomania;
	}	
}