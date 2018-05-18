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

import br.com.hslife.orcamento.entity.*;
import br.com.hslife.orcamento.enumeration.Container;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.*;
import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

@Component
@Scope("session")
public class SeguroController extends AbstractCRUDController<Seguro>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -372533275028178301L;
	
	@Autowired
	private ISeguro service;

	@Autowired
	private IConta contaService;

	@Autowired
	private IMoeda moedaService;

	@Autowired
	private ICategoria categoriaService;

	@Autowired
	private IFavorecido favorecidoService;

	@Autowired
	private IMeioPagamento meioPagamentoService;

	private Seguro seguroSelecionado;
	private boolean exibirAtivos = true;
	
	public SeguroController() {
		super(new Seguro());
		
		moduleTitle = "Seguros";
	}

	@Override
	protected void initializeEntity() {
		entity = new Seguro();
		listEntity = new ArrayList<>();
	}

	@Override
	public void find() {
		
	}

	@Override
	public List<Seguro> getListEntity() {
		return getService().buscarTodosPorUsuarioEAtivo(getUsuarioLogado(), isExibirAtivos());
	}

	public void atualizaListaSeguro() {
		this.getListEntity();
	}

	public void carregarArquivo(FileUploadEvent event) {
		if (event.getFile() != null) {
			Arquivo arquivo = new Arquivo();
			arquivo.setDados(event.getFile().getContents());
			arquivo.setNomeArquivo(event.getFile().getFileName().replace(" ", "."));
			arquivo.setContentType(event.getFile().getContentType());
			arquivo.setTamanho(event.getFile().getSize());
			arquivo.setContainer(Container.SEGURO);
			arquivo.setUsuario(getUsuarioLogado());
			arquivo.setAttribute("arquivo");
			if (entity.getIdArquivo() == null)
				entity.setIdArquivo(getArquivoComponent().carregarArquivo(arquivo));
			else
				entity.setIdArquivo(getArquivoComponent().substituirArquivo(arquivo, entity.getIdArquivo()));
		}
	}

	public void baixarArquivo() {
		Arquivo arquivo = getArquivoComponent().buscarArquivo(entity.getIdArquivo());
		if (arquivo == null) {
			warnMessage("Nenhum arquivo adicionado!");
		} else {
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			try {
				response.setContentType(arquivo.getContentType());
				response.setHeader("Content-Disposition", "attachment; filename=" + arquivo.getNomeArquivo());
				response.setContentLength(arquivo.getDados().length);
				ServletOutputStream output = response.getOutputStream();
				output.write(arquivo.getDados(), 0, arquivo.getDados().length);
				FacesContext.getCurrentInstance().responseComplete();
			} catch (Exception e) {
				errorMessage(e.getMessage());
			}
		}
	}

	public void excluirArquivo() {
		if (entity.getIdArquivo() == null) {
			warnMessage("Nenhum arquivo adicionado!");
		} else {
			getArquivoComponent().excluirArquivo(entity.getIdArquivo());
			entity.setIdArquivo(null);
			infoMessage("Arquivo excluído! Salve para confirmar as alterações.");
		}
	}

	public String encerrarSeguro() {
		getService().encerrarSeguro(entity);
		infoMessage("Seguro encerrado com sucesso!");
		return list();
	}

	public String reativarSeguro() {
		getService().reativarSeguro(entity);
		infoMessage("Seguro encerrado com sucesso!");
		return list();
	}

	public List<Conta> getListaConta() {
		if (getOpcoesSistema().getExibirContasInativas()) {
			return contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", new TipoConta[]{TipoConta.CORRENTE, TipoConta.POUPANCA, TipoConta.OUTROS, TipoConta.CARTAO}, getUsuarioLogado(), null);
		} else {
			return contaService.buscarDescricaoOuTipoContaOuAtivoPorUsuario("", new TipoConta[]{TipoConta.CORRENTE, TipoConta.POUPANCA, TipoConta.OUTROS, TipoConta.CARTAO}, getUsuarioLogado(), true);
		}
	}

	public List<Categoria> getListaCategoria() {
		return categoriaService.buscarAtivosPorTipoCategoriaEUsuario(TipoCategoria.DEBITO, getUsuarioLogado());
	}

	public List<Favorecido> getListaFavorecido() {
		return favorecidoService.buscarAtivosPorUsuario(getUsuarioLogado());
	}

	public List<MeioPagamento> getListaMeioPagamento() {
		return meioPagamentoService.buscarAtivosPorUsuario(getUsuarioLogado());
	}

	public List<Moeda> getListaMoeda() {
		return moedaService.buscarAtivosPorUsuario(getUsuarioLogado());
	}

	/**
	 * @return the service
	 */
	public ISeguro getService() {
		return service;
	}


	public Seguro getSeguroSelecionado() {
		return seguroSelecionado;
	}

	public void setSeguroSelecionado(Seguro seguroSelecionado) {
		this.seguroSelecionado = seguroSelecionado;
	}

	public boolean isExibirAtivos() {
		return exibirAtivos;
	}

	public void setExibirAtivos(boolean exibirAtivos) {
		this.exibirAtivos = exibirAtivos;
	}
}
