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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.ContaCompartilhada;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.CadastroSistema;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IContaCompartilhada;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.facade.IResumoEstatistica;

@Component
@Scope("session")
public class PanoramaCadastroController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4894402409315374812L;
	
	@Autowired
	private ICategoria categoriaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private IResumoEstatistica resumoEstatisticaService;
	
	@Autowired
	private IContaCompartilhada contaCompartilhadaService;
	
	public CadastroSistema cadastroSelecionado;
	public Long idRegistro;
	public List<Conta> contas;
	
	public PanoramaCadastroController() {
		moduleTitle = "Panorama dos Cadastros";
		
		cadastroSelecionado = CadastroSistema.CATEGORIA;
	}
	
	@Override
	public String startUp() {
		return "/pages/ResumoEstatistica/panoramaCadastro";
	}
	
	@Override
	protected void initializeEntity() {
		
	}
	
	public void find() {
		if (cadastroSelecionado == null) {
			warnMessage("Selecione o cadastro!");
			return;
		}
		
		if (idRegistro == null || idRegistro == 0) {
			warnMessage("Selecione o registro!");
		}
		
		try {
			contas = resumoEstatisticaService.gerarRelatorioPanoramaCadastro(cadastroSelecionado, idRegistro);
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public List<SelectItem> getListaCadastro() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		for (CadastroSistema enumeration : CadastroSistema.values()) {
			if (enumeration.equals(CadastroSistema.MEIOPAGAMENTO) && !getOpcoesSistema().getExibirMeioPagamento()) continue;
			listaSelectItem.add(new SelectItem(enumeration, enumeration.toString()));
		}
		return listaSelectItem;
	}
	
	public List<SelectItem> getListaRegistro() {
		List<SelectItem> listaSelectItem = new ArrayList<SelectItem>();
		
		// Declara Set de usuários para armazenar os proprietários das contas
		Set<Usuario> usuarios = new HashSet<>();
		
		// Traz as contas compartilhadas para com o usuário atualmente logado
		List<ContaCompartilhada> contasCompartilhadas = getContaCompartilhadaService().buscarTodosPorUsuario(getUsuarioLogado());
					
		// Acrescenta no Set os usuários das contas compartilhadas dos demais usuários
		for (ContaCompartilhada contaCompartilhada : contasCompartilhadas) {
			usuarios.add(contaCompartilhada.getConta().getUsuario());
		}
		
		// Acrescenta o usuário logado
		usuarios.add(getUsuarioLogado());
		
		try {
		
			switch (cadastroSelecionado) {
				case CATEGORIA : 
					for (Categoria c : categoriaService.buscarTipoCategoriaEDescricaoEAtivoPorUsuario(null, null, null, new ArrayList<>(usuarios))) {
						if (c.getUsuario().equals(getUsuarioLogado())) {
							listaSelectItem.add(new SelectItem(c.getId(), c.getTipoCategoria() + " - " + c.getDescricao() 
							+ (c.isAtivo() ? "" : " [INATIVO]")));
						} else {
							listaSelectItem.add(new SelectItem(c.getId(), c.getUsuario().getLogin() + " - " + c.getTipoCategoria() + " - " + c.getDescricao() 
							+ (c.isAtivo() ? "" : " [INATIVO]")));
						}
						
					}
					break;
				case FAVORECIDO : 
					for (Favorecido f : favorecidoService.buscarTipoPessoaENomeEAtivoPorUsuario(null, null, null, new ArrayList<>(usuarios))) {
						if (f.getUsuario().equals(getUsuarioLogado())) {
							listaSelectItem.add(new SelectItem(f.getId(), f.getTipoPessoa() + " - " + f.getNome()
							+ (f.isAtivo() ? "" : " [INATIVO]")));
						} else {
							listaSelectItem.add(new SelectItem(f.getId(), f.getUsuario().getLogin() + " - " + f.getTipoPessoa() + " - " + f.getNome()
							+ (f.isAtivo() ? "" : " [INATIVO]")));
						}
					}
					break;
				case MEIOPAGAMENTO : 
					for (MeioPagamento m : meioPagamentoService.buscarDescricaoEAtivoPorUsuario(null, null, new ArrayList<>(usuarios))) {
						if (m.getUsuario().equals(getUsuarioLogado())) {
							listaSelectItem.add(new SelectItem(m.getId(), m.getDescricao()
									+ (m.isAtivo() ? "" : " [INATIVO]")));
						} else {
							listaSelectItem.add(new SelectItem(m.getId(), m.getUsuario().getLogin() + " - " + m.getDescricao()
									+ (m.isAtivo() ? "" : " [INATIVO]")));
						}
					}
					break;
				case MOEDA : 
					for (Moeda m : moedaService.buscarAtivosPorUsuario(getUsuarioLogado())) {
						listaSelectItem.add(new SelectItem(m.getId(), m.getLabel()));
					}
					break;
			}
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		
		return listaSelectItem;
	}

	public CadastroSistema getCadastroSelecionado() {
		return cadastroSelecionado;
	}

	public void setCadastroSelecionado(CadastroSistema cadastroSelecionado) {
		this.cadastroSelecionado = cadastroSelecionado;
	}

	public Long getIdRegistro() {
		return idRegistro;
	}

	public void setIdRegistro(Long idRegistro) {
		this.idRegistro = idRegistro;
	}

	public List<Conta> getContas() {
		return contas;
	}

	public void setContas(List<Conta> contas) {
		this.contas = contas;
	}

	public IContaCompartilhada getContaCompartilhadaService() {
		return contaCompartilhadaService;
	}
}
