/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

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

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento ou escreva 
    
    para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor, 
    
    Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor entre  

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class CriterioLog {
	
	// Atributo que armazena os critérios selecionados
	private Map<String, Criterion> hibernateCriterions = new TreeMap<String, Criterion>();

	private Date inicio;	
	private Date fim;
	private String logger;
	private String logLevel;

	public String getLogger() {
		return logger;
	}

	public void setLogger(String logger) {
		this.logger = logger;
		
		hibernateCriterions.remove("logger");
		
		if (logger != null && !logger.isEmpty()) {
			hibernateCriterions.put("logger", Restrictions.eq("logger", logger));
		}
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
		
		hibernateCriterions.remove("logLevel");
		
		if (logLevel != null && !logLevel.isEmpty()) {
			hibernateCriterions.put("logLevel", Restrictions.eq("logLevel", logLevel));
		}
	}

	public Date getInicio() {
		return inicio;
	}

	@SuppressWarnings("deprecation")
	public void setInicio(Date inicio) {
		this.inicio = inicio;
		
		hibernateCriterions.remove("inicio");
		
		if (inicio != null) {
			// Seta a data de início para 00:00:00 do dia
			inicio.setHours(0);
			inicio.setMinutes(0);
			inicio.setSeconds(0);
			
			hibernateCriterions.put("inicio", Restrictions.ge("logDate", inicio));
		}
	}

	public Date getFim() {
		return fim;
	}

	@SuppressWarnings("deprecation")
	public void setFim(Date fim) {
		this.fim = fim;
		
		hibernateCriterions.remove("fim");
		
		if (fim != null) {
			// Seta a data de fim para 23:59:59
			fim.setHours(23);
			fim.setMinutes(59);
			fim.setSeconds(59);
			
			hibernateCriterions.put("fim", Restrictions.le("logDate", fim));
		}
	}	
	
	public List<Criterion> buildCriteria() {
		return new ArrayList<Criterion>(hibernateCriterions.values());
	}
}