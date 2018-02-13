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

***/
package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Endereco;
import br.com.hslife.orcamento.entity.Pessoal;
import br.com.hslife.orcamento.entity.Telefone;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IInformacaoPessoal;

@Component("pessoalMB")
@Scope("session")
public class InformacaoPessoalController extends AbstractController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5508989331062227746L;

	@Autowired
	private IInformacaoPessoal service; 
	
	private Pessoal pessoal;
	private Endereco endereco;
	private Telefone telefone;
	
	private List<Endereco> listaEndereco;
	private List<Telefone> listaTelefone;
	
	public InformacaoPessoalController() {		
		moduleTitle = "Informações pessoais";
	}
	
	@Override
	@PostConstruct
	public String startUp() {
		try {
			// Busca os dados pessoais do usuário
			if (getService().buscarDadosPessoais(getUsuarioLogado()) == null) {
				pessoal = new Pessoal();
			} else {
				pessoal = getService().buscarDadosPessoais(getUsuarioLogado());
			}
			
			// Busca os endereços do usuário
			if (getService().buscarEnderecos(getUsuarioLogado()) == null) {
				endereco = new Endereco();
				listaEndereco = new ArrayList<>();
			} else {
				endereco = new Endereco();
				listaEndereco = getService().buscarEnderecos(getUsuarioLogado());
			}
			
			// Busca os telefones do usuário
			if (getService().buscarTelefones(getUsuarioLogado()) == null) {
				telefone = new Telefone();
				listaTelefone = new ArrayList<Telefone>();
			} else {
				telefone = new Telefone();
				listaTelefone = getService().buscarTelefones(getUsuarioLogado());
			}
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "/pages/Pessoal/formPessoal";
	}
	
	@Override
	protected void initializeEntity() {
				
	}

	public void salvarInformacaoPessoal() {
		try {
			// Cadastra os dados pessoais
			pessoal.setUsuario(getUsuarioLogado());
			getService().salvarDadosPessoais(pessoal);
			//getService().salvarEnderecos(listaEndereco, getUsuarioLogado());
			//getService().salvarTelefones(listaTelefone, getUsuarioLogado());
			infoMessage("Dados salvos com sucesso!");
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void salvarEndereco() {
		try {
			endereco.setUsuario(getUsuarioLogado());
			getService().salvarEndereco(endereco);
			listaEndereco = getService().buscarEnderecos(getUsuarioLogado());
			endereco = new Endereco();
			infoMessage("Endereço salvo com sucesso!");
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void salvarTelefone() {
		try {
			telefone.setUsuario(getUsuarioLogado());
			getService().salvarTelefone(telefone);
			listaTelefone = getService().buscarTelefones(getUsuarioLogado());
			telefone = new Telefone();
			infoMessage("Telefone salvo com sucesso!");
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void editarEndereco() {
		// Método criado unicamente para disparar o action do commandButton
	}
	
	public void editarTelefone() {
		// Método criado unicamento para disparar o action do commandButton
	}
	
	public void excluirEndereco() {
		try {			
			getService().excluirEndereco(endereco);
			listaEndereco = getService().buscarEnderecos(getUsuarioLogado());
			endereco = new Endereco();
			infoMessage("Endereço excluído com sucesso!");
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void excluirTelefone() {
		try {
			getService().excluirTelefone(telefone);
			listaTelefone = getService().buscarTelefones(getUsuarioLogado());
			telefone = new Telefone();
			infoMessage("Telefone excluído com sucesso!");
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public IInformacaoPessoal getService() {
		return service;
	}

	public void setService(IInformacaoPessoal service) {
		this.service = service;
	}

	public Pessoal getPessoal() {
		return pessoal;
	}

	public void setPessoal(Pessoal pessoal) {
		this.pessoal = pessoal;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public List<Endereco> getListaEndereco() {
		return listaEndereco;
	}

	public void setListaEndereco(List<Endereco> listaEndereco) {
		this.listaEndereco = listaEndereco;
	}

	public Telefone getTelefone() {
		return telefone;
	}

	public void setTelefone(Telefone telefone) {
		this.telefone = telefone;
	}

	public List<Telefone> getListaTelefone() {
		return listaTelefone;
	}

	public void setListaTelefone(List<Telefone> listaTelefone) {
		this.listaTelefone = listaTelefone;
	}
}
