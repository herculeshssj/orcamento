/***
  
  	Copyright (c) 2012 - 2015 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@gmail.com, ou ainda escreva para 

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

public class CriterioAuditoria {
	
	// Atributo que armazena os critérios selecionados
	private Map<String, Criterion> hibernateCriterions = new TreeMap<String, Criterion>();

	private String usuario;	
	private String transacao;	
	private String classe;	
	private Date inicio;	
	private Date fim;

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
		
		hibernateCriterions.remove("usuario");
		
		if (usuario != null && !usuario.isEmpty()) {
			hibernateCriterions.put("usuario", Restrictions.eq("usuario", usuario));
		}
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
		
		hibernateCriterions.remove("inicio");
		
		if (inicio != null) {
			hibernateCriterions.put("inicio", Restrictions.ge("data", inicio));
		}
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
		
		hibernateCriterions.remove("fim");
		
		if (fim != null) {
			hibernateCriterions.put("fim", Restrictions.le("data", fim));
		}
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
		
		hibernateCriterions.remove("classe");
		
		if (classe != null && !classe.isEmpty()) {
			hibernateCriterions.put("classe", Restrictions.eq("classe", classe));
		}
	}

	public String getTransacao() {
		return transacao;
	}

	public void setTransacao(String transacao) {
		this.transacao = transacao;
		
		hibernateCriterions.remove("transacao");
		
		if (transacao != null && !transacao.isEmpty()) {
			hibernateCriterions.put("transacao", Restrictions.eq("transacao", transacao));
		}
	}	
	
	public List<Criterion> buildCriteria() {
		return new ArrayList<Criterion>(hibernateCriterions.values());
	}
}