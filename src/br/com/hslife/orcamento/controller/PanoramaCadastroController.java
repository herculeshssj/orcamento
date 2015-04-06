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

package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IMeioPagamento;

@Component("panoramaCadastroMB")
@Scope("session")
public class PanoramaCadastroController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4894402409315374812L;
	
	public enum Cadastro {
		CATEGORIA, FAVORECIDO, MEIOPAGAMENTO;
	}
	
	@Autowired
	private ICategoria categoriaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;
	
	public Cadastro cadastroSelecionado;
	public Long idRegistro;

	public PanoramaCadastroController() {
		moduleTitle = "Panorama dos Cadastros";
		
		cadastroSelecionado = Cadastro.CATEGORIA;
	}
	
	@Override
	public String startUp() {
		return "/pages/ResumoEstatistica/panoramaCadastro";
	}
	
	@Override
	protected void initializeEntity() {
		
	}
	
	public void find() {
		if (cadastroSelecionado != null) {
			warnMessage("Selecione o cadastro!");
			return;
		}
		
		if (idRegistro == null || idRegistro == 0) {
			warnMessage("Selecione o registro!");
		}
	}
	
	public List<SelectItem> getListaCadastro() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (Cadastro enumeration : Cadastro.values()) {
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaRegistro() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		
		try {
		
			switch (cadastroSelecionado) {
				case CATEGORIA : 
					for (Categoria c : categoriaService.buscarTipoCategoriaEDescricaoEAtivoPorUsuario(null, null, null, getUsuarioLogado())) {
						listaSelectItem.add(new SelectItem(c.getId(), c.getTipoCategoria() + " - " + c.getDescricao()));
					}
					break;
				case FAVORECIDO : 
					for (Favorecido f : favorecidoService.buscarTipoPessoaENomeEAtivoPorUsuario(null, null, null, getUsuarioLogado())) {
						listaSelectItem.add(new SelectItem(f.getId(), f.getNome()));
					}
					break;
				case MEIOPAGAMENTO : 
					for (MeioPagamento m : meioPagamentoService.buscarDescricaoEAtivoPorUsuario(null, null, getUsuarioLogado())) {
						listaSelectItem.add(new SelectItem(m.getId(), m.getDescricao()));
					}
					break;
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		return listaSelectItem;
	}

	public Cadastro getCadastroSelecionado() {
		return cadastroSelecionado;
	}

	public void setCadastroSelecionado(Cadastro cadastroSelecionado) {
		this.cadastroSelecionado = cadastroSelecionado;
	}

	public Long getIdRegistro() {
		return idRegistro;
	}

	public void setIdRegistro(Long idRegistro) {
		this.idRegistro = idRegistro;
	}
}