/***

	Copyright (c) 2012 - 2021 Hércules S. S. José

	Este arquivo é parte do programa Orçamento Doméstico.


	Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

	modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

	publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

	Licença.


	Este programa é distribuído na esperança que possa ser útil, mas SEM

	NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer

	MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor

	GNU em português para maiores detalhes.


	Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob

	o nome de "LICENSE" junto com este programa, se não, acesse o site do

	projeto no endereco https://github.com/herculeshssj/orcamento ou escreva

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth

	Floor, Boston, MA  02110-1301, USA.


	Para mais informações sobre o programa Orçamento Doméstico e seu autor

	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

	para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.component;

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.hslife.orcamento.model.InfoCotacao;

@Component
public class InfoCotacaoComponent {
	
	private static final Logger logger = LogManager.getLogger(InfoCotacaoComponent.class);
	
	@Autowired
	private OpcaoSistemaComponent opcaoSistemaComponent;
	
	public OpcaoSistemaComponent getOpcaoSistemaComponent() {
		return opcaoSistemaComponent;
	}
	
	public InfoCotacao invokeAPI(String ticker) {
		InfoCotacao info = new InfoCotacao();
		try {
			
			if (ticker == null) {
				logger.warn("Ticker não informado. API não invocada");
				return info;
			}
			
			// Verifica se o API KEY foi informado
			if (getOpcaoSistemaComponent().getAlphaVantageApiKey().equals("demo")) {
				logger.warn("API KEY não informado. API não invocada");
				return info;
			}
			
			// Invoca a API usando o RestTemplate
			ResponseEntity<String> resposta = new RestTemplate()
					.getForEntity("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + ticker + "&apikey=" 
			+ getOpcaoSistemaComponent().getAlphaVantageApiKey(), String.class);
			
			// Verifica se a resposta foi código 200
			if (resposta.getStatusCode().equals(HttpStatus.OK)) {
				
				// Mapeia o objeto Json retornado
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(resposta.getBody());
				
				// Obtém o array da série histórica
				JsonNode timeSerieNode = rootNode.get("Time Series (Daily)");
				
				// Itera os nós do array
				for (Iterator<JsonNode> i = timeSerieNode.elements(); i.hasNext(); ) {
					// Obtém os valores do nó
					JsonNode dayNode = i.next();
					
					// Popula os atributos do objeto
					
					info.setOpen(new BigDecimal(dayNode.get("1. open").asDouble()));
					info.setHigh(new BigDecimal(dayNode.get("2. high").asDouble()));
					info.setLow(new BigDecimal(dayNode.get("3. low").asDouble()));
					info.setClose(new BigDecimal(dayNode.get("4. close").asDouble()));
					info.setVolume(new BigDecimal(dayNode.get("5. volume").asDouble()));
					
					// Interrompe a iteração
					break; // FIXME substituir o for por do..while. Só interessa o primeiro item da iteração, os demais não são importantes.
				}
				
			} else {
				// Loga a falha na requisição
				logger.error("Falha ao invocar a API: \n Código: " + resposta.getStatusCode() + "\n Mensagem: " + resposta.getBody());
			}
			
		} catch (Exception e) {
			logger.catching(e);
		}
		
		return info;
	}
}
