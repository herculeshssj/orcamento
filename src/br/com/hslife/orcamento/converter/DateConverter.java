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

package br.com.hslife.orcamento.converter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value="dateConverter")
public class DateConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {		
		try {
			if (value == null || value.isEmpty()) return null;
			String[] dataSeparada = value.split("/");
			Calendar temp = Calendar.getInstance();
			temp.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataSeparada[0]));
			temp.set(Calendar.MONTH, Integer.parseInt(dataSeparada[1]) - 1);
			temp.set(Calendar.YEAR, Integer.parseInt(dataSeparada[2]));
			return temp.getTime();		
		} catch (Exception e) {			
			e.printStackTrace();
			throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Formato de data inválido!", "Formato de data inválido!"));
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) return "";
		SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy");
		return formataData.format((Date)value);
	}
}
