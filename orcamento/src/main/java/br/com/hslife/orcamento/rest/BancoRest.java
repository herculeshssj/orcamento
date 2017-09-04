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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.hslife.orcamento.entity.Banco;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IBanco;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.rest.json.BancoJson;

@Controller
@RequestMapping("/banco")
public class BancoRest extends AbstractRest<Banco> {
	
	// Tutorial: http://satish-tech-talks.blogspot.com.br/2012/12/restful-web-service-with-spring-31.html
	// Apoio:
	// http://www.baeldung.com/wp-content/uploads/2013/09/Building-REST-Services-with-Spring.pdf
	// https://gist.github.com/jteso/1813740
	
	@Autowired
	private IBanco service;
	
	@Autowired
	private IUsuario usuarioService;
	
	@RequestMapping(
			method = RequestMethod.GET, 
			headers = "Accept=application/json", 
			produces = {"application/json" })
	@ResponseBody
	public List<BancoJson> buscarTodos() {
		// Temporariamento usando os dados do usuário ID 1 (admin)
		Usuario usuarioLogado = new Usuario();
		usuarioLogado.setId(1l);
		listEntity = getService().buscarAtivosPorUsuario(usuarioLogado);
		return Banco.toListJson(listEntity);
	}
	
	@RequestMapping(
			value="/{id}",
			method=RequestMethod.GET,
			headers="Accept=application/json",
			produces={"application/json"})
	@ResponseBody
	public BancoJson buscarPorID(
			@PathVariable Long id) {
		entity = getService().buscarPorID(id);
		if (entity != null)
			return entity.toJson();
		else
			return new BancoJson();
	}
	
	@RequestMapping(
			value="/{id}",
			method=RequestMethod.DELETE,
			headers="Accept=application/json",
			produces={"application/json"})
	@ResponseBody
	public String excluir(
			@PathVariable Long id) {
		try {
			Banco banco = getService().buscarPorID(id);
			
			if (banco != null) {
				getService().excluir(banco);
			} else {
				return responseMessage("Registro não encontrado!");
			}
			
			return successMessage();
		} catch (ValidationException | BusinessException e) {
			return responseMessage(e.getMessage());
		}
	}
	
	@RequestMapping(
			method=RequestMethod.POST,
			headers="Accept=application/json",
			produces={"application/json"},
			consumes={"application/json"})
	@ResponseBody
	public String cadastrar(
			@RequestBody BancoJson bancoJson) {
		
		try {
			// Converte o objeto Json para sua representação de entidade
			Banco banco = bancoJson.toEntity();
			
			// Valida a entidade
			banco.validate();
			
			// Atribui o usuário logado a entidade (temporariamente o usuário ID 1)
			Usuario usuario = getUsuarioService().buscarPorID(1l);
			banco.setUsuario(usuario);
			
			// Exclui o valor setado no ID
			banco.setId(null);
			
			// Persiste a entidade
			getService().cadastrar(banco);
			
			return successMessage();
		} catch (ValidationException | BusinessException e) {
			return responseMessage(e.getMessage());
		}
	}
	
	@RequestMapping(
			value="/{id}",
			method=RequestMethod.PUT,
			headers="Accept=application/json",
			produces={"application/json"},
			consumes={"application/json"})
	@ResponseBody
	public String alterar(
			@PathVariable Long id,
			@RequestBody BancoJson bancoJson) {
		
		try {
			// Converte o objeto Json para sua representação de entidade
			Banco banco = bancoJson.toEntity();
			
			// Valida a entidade
			banco.validate();
			
			// Busca a entidade a ser alterada
			entity = getService().buscarPorID(id);
			
			if (entity == null)
				return responseMessage("Registro não encontrado!");
			
			// Seta os atributos que são editáveis
			entity.setAtivo(banco.isAtivo());
			entity.setNome(banco.getNome());
			entity.setNumero(banco.getNumero());
			entity.setPadrao(banco.isPadrao());
			
			// Persiste a entidade
			getService().alterar(entity);
			
			return successMessage();
		} catch (ValidationException | BusinessException e) {
			return responseMessage(e.getMessage());
		}
	}

	public IBanco getService() {
		return service;
	}

	public IUsuario getUsuarioService() {
		return usuarioService;
	}
}