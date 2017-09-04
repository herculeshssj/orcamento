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

package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Endereco;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.Telefone;
import br.com.hslife.orcamento.enumeration.TipoPessoa;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.IFavorecido;

@Component
@Scope("session")
public class FavorecidoController extends AbstractCRUDController<Favorecido> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8824820815235564499L;

	@Autowired
	private IFavorecido service; 
	
	private String nomeFavorecido;
	private boolean somenteAtivos = true;
	private boolean somenteFinanceiro = true;
	
	private Endereco endereco = new Endereco();
	private Telefone telefone = new Telefone();
	
	private List<Endereco> listaEndereco = new ArrayList<>();
	private List<Telefone> listaTelefone = new ArrayList<>();

	public FavorecidoController() {
		super(new Favorecido());
		moduleTitle = "Favorecido / Sacado";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Favorecido();
		listEntity = new ArrayList<Favorecido>();
		
		endereco = new Endereco();
		telefone = new Telefone();
		
		listaEndereco = new ArrayList<>();
		listaTelefone = new ArrayList<>();
	}
	
	@Override
	public void find() {
		try {
			listEntity = getService().buscarTipoPessoaENomeEAtivoEFinanceiroPorUsuario(null, nomeFavorecido, somenteAtivos, somenteFinanceiro, getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	@Override
	public String save() {
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}
	
	@Override
	public String edit() {
		try {
			String retorno = super.edit();
			
			// Traz os telefones e endereços do favorecido
			listaEndereco = getService().buscarEnderecos(entity);
			listaTelefone = getService().buscarTelefones(entity);
			
			return retorno;
			
		} catch (BusinessException | ValidationException be) {
			errorMessage(be.getMessage());
		}
		return "";
	}
	
	public void salvarEndereco() {
		try {
			// Verifica se o favorecido já está salvo na base
			if (entity.getId() == null) {
				warnMessage("Salve primeiro o favorecido antes de cadastrar um endereço!");
				return;
			}
			
			endereco.setFavorecido(entity);
			getService().salvarEndereco(endereco);
			listaEndereco = getService().buscarEnderecos(entity);
			endereco = new Endereco();
			infoMessage("Endereço salvo com sucesso!");
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void salvarTelefone() {
		try {
			// Verifica se o favorecido já está salvo na base
			if (entity.getId() == null) {
				warnMessage("Salve primeiro o favorecido antes de cadastrar um telefone!");
				return;
			}
			
			telefone.setFavorecido(entity);
			getService().salvarTelefone(telefone);
			listaTelefone = getService().buscarTelefones(entity);
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
			listaEndereco = getService().buscarEnderecos(entity);
			endereco = new Endereco();
			infoMessage("Endereço excluído com sucesso!");
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void excluirTelefone() {
		try {
			getService().excluirTelefone(telefone);
			listaTelefone = getService().buscarTelefones(entity);
			telefone = new Telefone();
			infoMessage("Telefone excluído com sucesso!");
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public List<SelectItem> getListaTipoPessoa() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		listaSelectItem.add(new SelectItem(TipoPessoa.FISICA, "Física"));
		listaSelectItem.add(new SelectItem(TipoPessoa.JURIDICA, "Jurídica"));
		return listaSelectItem;
	}

	public IFavorecido getService() {
		return service;
	}

	public void setService(IFavorecido service) {
		this.service = service;
	}

	public String getNomeFavorecido() {
		return nomeFavorecido;
	}

	public void setNomeFavorecido(String nomeFavorecido) {
		this.nomeFavorecido = nomeFavorecido;
	}

	public boolean isSomenteAtivos() {
		return somenteAtivos;
	}

	public void setSomenteAtivos(boolean somenteAtivos) {
		this.somenteAtivos = somenteAtivos;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public Telefone getTelefone() {
		return telefone;
	}

	public void setTelefone(Telefone telefone) {
		this.telefone = telefone;
	}

	public List<Endereco> getListaEndereco() {
		return listaEndereco;
	}

	public void setListaEndereco(List<Endereco> listaEndereco) {
		this.listaEndereco = listaEndereco;
	}

	public List<Telefone> getListaTelefone() {
		return listaTelefone;
	}

	public void setListaTelefone(List<Telefone> listaTelefone) {
		this.listaTelefone = listaTelefone;
	}

	public boolean isSomenteFinanceiro() {
		return somenteFinanceiro;
	}

	public void setSomenteFinanceiro(boolean somenteFinanceiro) {
		this.somenteFinanceiro = somenteFinanceiro;
	}
}