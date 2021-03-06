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

para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor,

Boston, MA  02110-1301, USA.


Para mais informações sobre o programa Orçamento Doméstico e seu autor

entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/

package br.com.hslife.orcamento.entity;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.MappedSuperclass;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

@MappedSuperclass
public abstract class EntityPersistence implements Comparable<EntityPersistence>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8755844239005706031L;
	
	private static final Logger LOGGER = LogManager.getLogger(EntityPersistence.class);
	
	/**
	 * Retorna uma representação mais amigável da entidade, útil
	 * para exibição em tela.
	 * 
	 * @return texto simplificado da entidade
	 */
	public abstract String getLabel();
	
	/**
	 * Realiza a validação dos atributos da entidade.
	 * 
	 * @throws lança um ValidationException caso o atributo não corresponda
	 * às regras implementadas 
	 */
	public abstract void validate();

	/**
	 * @return the id
	 */
	public abstract Long getId();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Long.signum(getId() == null ? 0 : getId() ^ (getId() == null ? 1 : getId() >>> 32));
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getId() == null) return false;
		if (getClass() != obj.getClass()) return false;

		EntityPersistence other = (EntityPersistence) obj;
		if (!getId().equals(other.getId())) return false;
		return true;
	}
	
	
	@Override
	public int compareTo(EntityPersistence o) {
		if (this.getId() != null && o != null && o.getId() != null)
			return this.getId().compareTo(o.getId());
		return 0;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + this.getId() + "]";
	}
	
	public Map<String, String> getFieldValues() {
		Map<String, String> camposClasse = new HashMap<String, String>();
		try {
			for (Method method : this.getClass().getDeclaredMethods()) {
				if (method.getName().substring(0, 2).equals("is") || method.getName().substring(0, 3).equals("get")) {
					
					// Extrai o nome do atributo a partir no nome do método get ou is
					String nomeAtributo = "";
					if (method.getName().substring(0, 2).equals("is")) {
						nomeAtributo = method.getName().substring(2);
					} else {
						nomeAtributo = method.getName().substring(3);
					}
					
					// Extrai o valor do método Getter
					Object valorAtributo = method.invoke(this);
					
					if (valorAtributo == null)					
						camposClasse.put(nomeAtributo, "null");
					else
						camposClasse.put(nomeAtributo, valorAtributo.toString());
				}
			}
		}
		catch (Exception e) {
			LOGGER.catching(e);
			e.printStackTrace();
		}
		return camposClasse;
	}
	
	public String generateJsonValues() {
		JSONObject json = new JSONObject();
		
		for (String s : this.getFieldValues().keySet()) {
			json.put(s, this.getFieldValues().get(s));
		} 
		
		return json.toString();
	}
}