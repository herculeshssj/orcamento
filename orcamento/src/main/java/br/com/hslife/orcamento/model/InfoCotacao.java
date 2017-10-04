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

package br.com.hslife.orcamento.model;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class InfoCotacao {
	
	private String symbol;
	private String name;
	private String currency;
	private String stockExchange;
	private BigDecimal quoteAsk;
	private BigDecimal quoteBid;
	private BigDecimal quotePrice;
	private BigDecimal quotePrevClose;
	private BigDecimal quoteOpen;
	
	private static final Logger logger = LogManager.getLogger(InfoCotacao.class);
	
	public InfoCotacao(String ticker) {
		// Invoca a API
		this.invokeAPI(ticker);
	}
	
	private void invokeAPI(String ticker) {
		try {
			
			if (ticker == null) {
				logger.warn("Ticker não informado. API não invocada");
				return;
			}
			
			Stock stock = YahooFinance.get(ticker);
			
			// Popula os atributos do objeto
			this.symbol = stock.getSymbol();
			this.name = stock.getName();
			this.currency = stock.getCurrency();
			this.stockExchange = stock.getStockExchange();
			this.quoteAsk = stock.getQuote().getAsk();
			this.quoteBid = stock.getQuote().getBid();
			this.quotePrice = stock.getQuote().getPrice();
			this.quotePrevClose = stock.getQuote().getPreviousClose();
			this.quoteOpen = stock.getQuote().getOpen();
			
		} catch (Exception e) {
			logger.catching(e);
		}
	}

	public String getSymbol() {
		return symbol;
	}

	public String getName() {
		return name;
	}

	public String getCurrency() {
		return currency;
	}

	public String getStockExchange() {
		return stockExchange;
	}

	public BigDecimal getQuoteAsk() {
		return quoteAsk;
	}

	public BigDecimal getQuoteBid() {
		return quoteBid;
	}

	public BigDecimal getQuotePrice() {
		return quotePrice;
	}

	public BigDecimal getQuotePrevClose() {
		return quotePrevClose;
	}

	public static Logger getLogger() {
		return logger;
	}

	public BigDecimal getQuoteOpen() {
		return quoteOpen;
	}
}