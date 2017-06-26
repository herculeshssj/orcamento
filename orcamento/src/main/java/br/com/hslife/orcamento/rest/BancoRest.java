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

package br.com.hslife.orcamento.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.hslife.orcamento.entity.Banco;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.facade.IBanco;
import br.com.hslife.orcamento.rest.json.BancoJson;

@Controller
@RequestMapping("/banco")
public class BancoRest extends AbstractRest<Banco> {
	
	@Autowired
	private IBanco service;
	
	@RequestMapping(method = RequestMethod.GET, 
			headers = "Accept=application/xml, application/json", 
			produces = {"application/json", "application/xml" })
	@ResponseBody
	public List<BancoJson> getAll() {
		// Temporariamento usando os dados do usuário ID 1 (admin)
		Usuario usuarioLogado = new Usuario();
		usuarioLogado.setId(1l);
		listEntity = getService().buscarAtivosPorUsuario(usuarioLogado);
		return Banco.toListJson(listEntity);
	}

	public IBanco getService() {
		return service;
	}
}