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
package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.ResultadoScript;
import br.com.hslife.orcamento.entity.Script;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IScript;
import br.com.hslife.orcamento.facade.IUsuario;

@Component("scriptMB")
@Scope("session")
public class ScriptController extends AbstractCRUDController<Script>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5008455377685602947L;

	@Autowired
	private IScript service;
	
	@Autowired
	private IUsuario usuarioService;
	
	private String nomeScript;
	private boolean somenteAtivos = true;
	private Long idResultadoScript;
	private ResultadoScript resultadoScript;
	
	public ScriptController() {
		super(new Script());
		
		moduleTitle = "Rotinas automatizadas";
	}

	@Override
	protected void initializeEntity() {
		entity = new Script();
		listEntity = new ArrayList<Script>();
	}

	@Override
	public void find() {		
		try {
			listEntity = getService().buscarPorNomeEAtivo(nomeScript, somenteAtivos);
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String verDetalhesExecucao() {
		try {
			resultadoScript = getService().buscarResultadoPorID(idResultadoScript);
			return "/pages/Script/verDetalhesExecucao";
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public List<ResultadoScript> getUltimasExecucoes() {
		List<ResultadoScript> resultados = new ArrayList<>();
		try {
			 // Traz todos os scripts ativos
			List<Script> scriptsAtivos = getService().buscarPorNomeEAtivo("", true);
			
			// Itera os script ativos em busca da última execução de cada um deles
			for (Script script : scriptsAtivos) {
				// Verifica se o script possui um usuário setado a ele para saber se o 
				// usuário logado pode acessar suas informações
				boolean podeTrazer = false;
				if (script.getUsuario() != null) {
					if (script.getUsuario().equals(getUsuarioLogado())) {
						podeTrazer = true;
					}
				} else {
					// Verifica se o usuário atualmente logado é o admin
					if (getUsuarioLogado().getLogin().equals("admin")) {
						podeTrazer = true;
					}
				}
				
				// Traz o resultado da última execução
				if (podeTrazer) {
					ResultadoScript resultado = getService().buscarUltimoResultadoScript(script);
					if (resultado != null && resultado.getResultado() != null && !resultado.getResultado().trim().isEmpty())
						resultados.add(resultado);
				}
					
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return resultados;
	}

	public List<Usuario> getListaUsuario() {
		try {
			return getUsuarioService().buscarTodos();
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<Usuario>();
	}
	
	public String getNomeScript() {
		return nomeScript;
	}

	public void setNomeScript(String nomeScript) {
		this.nomeScript = nomeScript;
	}

	public IScript getService() {
		return service;
	}

	public boolean isSomenteAtivos() {
		return somenteAtivos;
	}

	public void setSomenteAtivos(boolean somenteAtivos) {
		this.somenteAtivos = somenteAtivos;
	}

	public IUsuario getUsuarioService() {
		return usuarioService;
	}

	public Long getIdResultadoScript() {
		return idResultadoScript;
	}

	public void setIdResultadoScript(Long idResultadoScript) {
		this.idResultadoScript = idResultadoScript;
	}

	public ResultadoScript getResultadoScript() {
		return resultadoScript;
	}

	public void setResultadoScript(ResultadoScript resultadoScript) {
		this.resultadoScript = resultadoScript;
	}
}
