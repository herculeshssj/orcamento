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

package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Endereco;
import br.com.hslife.orcamento.entity.Pessoal;
import br.com.hslife.orcamento.exception.BusinessException;
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
	
	private List<Endereco> listaEndereco;
	
	public InformacaoPessoalController() {		
		moduleTitle = "Informações pessoais";
	}
	
	@Override
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
		} catch (BusinessException be) {
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
			getService().salvarEnderecos(listaEndereco, getUsuarioLogado());
			infoMessage("Dados salvos com sucesso!");
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void salvarEndereco() {
		try {
			endereco.setUsuario(getUsuarioLogado());
			endereco.validate();
			if (!listaEndereco.contains(endereco)) {
				listaEndereco.add(endereco);
			}
			endereco = new Endereco();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void editarEndereco() {
		// Método criado unicamente para disparar o action do commandButton
	}
	
	public void excluirEndereco() {
		listaEndereco.remove(endereco);
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
}