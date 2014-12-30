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

package br.com.hslife.orcamento.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.component.OpcaoSistemaComponent;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IOpcaoSistema;

@Service
public class OpcaoSistemaService implements IOpcaoSistema {
	
	@Autowired
	private OpcaoSistemaComponent component;

	public OpcaoSistemaComponent getComponent() {
		return component;
	}

	public void setComponent(OpcaoSistemaComponent component) {
		this.component = component;
	}

	public void salvarOpcoesGlobal(Map<String, Object> opcoesSistema) throws BusinessException {
		getComponent().salvarOpcoesGlobal(opcoesSistema);
	}
	
	public void salvarOpcoesGlobalAdmin(Map<String, Object> opcoesSistema) throws BusinessException {
		getComponent().salvarOpcoesGlobalAdmin(opcoesSistema);
	}
	
	public void salvarOpcoesUser(Map<String, Object> opcoesSistema, Usuario usuario) throws BusinessException {
		getComponent().salvarOpcoesUser(opcoesSistema, usuario);
	}
	
	public Map<String, Object> buscarOpcoesGlobalAdmin() throws BusinessException {
		return getComponent().buscarOpcoesGlobalAdmin();
	}
	
	public Map<String, Object> buscarOpcoesUser(Usuario usuario) throws BusinessException {
		return getComponent().buscarOpcoesUser(usuario);
	}
}
