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

***/package br.com.hslife.orcamento.controller;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.CategoriaInvestimento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.ICategoriaInvestimento;

@Component("categoriaInvestimentoMB")
@Scope("session")
public class CategoriaInvestimentoController extends AbstractCRUDController<CategoriaInvestimento>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1236132161590555846L;
	private static final Logger logger = LogManager.getLogger(CategoriaInvestimento.class);

	@Autowired
	private ICategoriaInvestimento service;
	
	private String descricaoCategoria;
	private boolean somenteAtivos = true;

	public CategoriaInvestimentoController() {
		super(new CategoriaInvestimento());
		
		moduleTitle = "Categoria de Investimentos";
	}

	@Override
	protected void initializeEntity() {
		entity = new CategoriaInvestimento();
		listEntity = new ArrayList<CategoriaInvestimento>();
	}
	
	@Override
	public void find() {
		try {
			listEntity = getService().buscarPorDescricaoETipoEAtivo(descricaoCategoria, null, somenteAtivos);
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String delete() {
		try {
			return super.delete();
		} catch (Exception e) {
			errorMessage("Não é possível excluir! Existem vínculos existentes com o registro!");
			logger.catching(e);
		}
		return "";
	}
	

	/* Métodos Getters e Setters */
	
	public ICategoriaInvestimento getService() {
		return service;
	}

	public void setService(ICategoriaInvestimento service) {
		this.service = service;
	}

	public String getDescricaoCategoria() {
		return descricaoCategoria;
	}

	public void setDescricaoCategoria(String descricaoCategoria) {
		this.descricaoCategoria = descricaoCategoria;
	}

	public boolean isSomenteAtivos() {
		return somenteAtivos;
	}

	public void setSomenteAtivos(boolean somenteAtivos) {
		this.somenteAtivos = somenteAtivos;
	}
}
