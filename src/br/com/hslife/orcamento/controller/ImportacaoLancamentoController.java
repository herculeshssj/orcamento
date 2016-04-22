/***
  
  	Copyright (c) 2012 - 2016 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.List;

import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.facade.IImportacaoLancamento;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.model.InfoOFX;

@Component("importacaoLancamentoMB")
@Scope("session")
public class ImportacaoLancamentoController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3650197694657342579L;
	
	@Autowired
	private IImportacaoLancamento service;
	
	@Autowired
	private IMoeda moedaService;
	
	private Conta contaSelecionada;
	private Arquivo arquivoAnexado;	
	private boolean exibirInfoArquivo;
	private InfoOFX infoArquivo;
	private List<LancamentoConta> lancamentoContaACriarAtualizar;
	private String tipoArquivo = "OFX";
	
	private LancamentoImportado entity;
	private List<LancamentoImportado> listEntity;
	private Long idEntity;
	
	private boolean selecionarTodosLancamentos;

	private String goToListPage = "";
	
	public ImportacaoLancamentoController() {
		moduleTitle = "Importação de Lançamentos";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new LancamentoImportado();
		listEntity = new ArrayList<LancamentoImportado>();
	}
	
	@Override
	public String startUp() {
		return goToListPage();
	}
	
	public String getModuleTitle() {		
		return super.getModuleTitle() + actionTitle;
	}
	
	private void find() {
		listEntity = getService().buscarLancamentoImportadoPorConta(contaSelecionada);		
	}
	
	public String create() {
		exibirInfoArquivo = false;
		actionTitle = "";
		find();
		return "/pages/ImportacaoLancamento/formImportacaoLancamento";
	}
	
	public String cancel() {
		initializeEntity();
		return goToListPage;
	}
	
	public String confirmarImportacao() {
		// Altera a moeda dos lançamentos importados que foram alterados
		for (LancamentoImportado li : getService().buscarLancamentoImportadoPorConta(contaSelecionada)) {
			if (!li.getMoeda().equals(listEntity.get(listEntity.indexOf(li)).getMoeda())) {
				// Salva o lançamento importado
				getService().atualizarLancamentoImportado(listEntity.get(listEntity.indexOf(li)));
			}
		}
		
		lancamentoContaACriarAtualizar = getService().buscarLancamentoContaACriarAtualizar(contaSelecionada, getService().buscarLancamentoImportadoPorConta(contaSelecionada));
		actionTitle = " - Confirmar";
		
		return "/pages/ImportacaoLancamento/confirmarImportacao";
	}
	
	public String goToListPage() {
		return this.goToListPage;
	}
	
	public void carregarArquivo(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (event.getFile().getSize() > 16777216) {
				errorMessage("Arquivo excedeu o tamanho máximo de 16 MB!");
			} else {
				arquivoAnexado = new Arquivo();
				arquivoAnexado.setDados(event.getFile().getContents());
				arquivoAnexado.setNomeArquivo(event.getFile().getFileName().replace(" ", "."));
				arquivoAnexado.setContentType(event.getFile().getContentType());
				arquivoAnexado.setTamanho(event.getFile().getSize());				
			}
		} else {
			infoMessage("Nenhum arquivo anexado!");
		}
		exibirInfoArquivo = false;
	}
	
	public void processarArquivo() {
		if (arquivoAnexado == null || arquivoAnexado.getDados() == null || arquivoAnexado.getDados().length == 0) {
			warnMessage("Carregue um arquivo OFX/CSV antes de processar!");			
		} else {
			try {
				if (tipoArquivo.equalsIgnoreCase("OFX")) {
					getService().processarArquivoImportado(arquivoAnexado, contaSelecionada);
					infoArquivo = getService().obterInformacaoArquivoImportado(arquivoAnexado, contaSelecionada);
					exibirInfoArquivo = true;
				} else {
					getService().processarArquivoCSVImportado(arquivoAnexado, contaSelecionada);
					exibirInfoArquivo = false;
				}				
				infoMessage("Arquivo processado com sucesso!");
				find();
			} catch (Exception e) {
				errorMessage(e.getMessage());
			}
		}
	}
	
	public void excluirLancamentoImportado() {
		entity = getService().buscarPorID(idEntity);
		getService().excluirLancamentoImportado(entity);
		infoMessage("Lançamento excluído com sucesso!");
		find();		
	}
	
	public void apagarLancamentos() {
		getService().apagarLancamentosImportados(contaSelecionada);
		infoMessage("Lançamentos excluídos com sucesso!");
		find();
	}
	
	public void importarLancamento() {
		entity = getService().buscarPorID(idEntity);
		getService().importarLancamento(entity);
		infoMessage("Lançamento importado com sucesso!");
		find();		
	}
	
	public String processarLancamentos() {
		getService().processarLancamentos(contaSelecionada, lancamentoContaACriarAtualizar);
		infoMessage("Lançamentos importados com sucesso!");
		initializeEntity();
		
		return goToListPage();
	}
	
	public void selecionarTodos() {
		if (lancamentoContaACriarAtualizar != null && lancamentoContaACriarAtualizar.size() > 0)
			for (LancamentoConta l : lancamentoContaACriarAtualizar) {
				if (selecionarTodosLancamentos) {
					l.setSelecionado(true);
				} else {
					l.setSelecionado(false);
				}
			}
	}
	
	public List<Conta> getListaConta() {
		List<Conta> lista = new ArrayList<>();
		lista.add(contaSelecionada);
		return lista;
	}
	
	public int getQuantRegistros() {
		if (listEntity == null || listEntity.isEmpty()) {
			return 0;
		} else {
			return listEntity.size();
		}
	}
	
	public List<String> getListaCodigoMonetario() {
		List<String> result = moedaService.buscarTodosCodigoMonetarioPorUsuario(getUsuarioLogado());
		
		if (result == null) {
			return new ArrayList<>();
		} else {
			return result;
		}
	}
	
	public IImportacaoLancamento getService() {
		return service;
	}

	public void setService(IImportacaoLancamento service) {
		this.service = service;
	}

	public Conta getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(Conta contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}

	public Arquivo getArquivoAnexado() {
		return arquivoAnexado;
	}

	public void setArquivoAnexado(Arquivo arquivoAnexado) {
		this.arquivoAnexado = arquivoAnexado;
	}

	public LancamentoImportado getEntity() {
		return entity;
	}

	public void setEntity(LancamentoImportado entity) {
		this.entity = entity;
	}

	public List<LancamentoImportado> getListEntity() {
		return listEntity;
	}

	public void setListEntity(List<LancamentoImportado> listEntity) {
		this.listEntity = listEntity;
	}

	public Long getIdEntity() {
		return idEntity;
	}

	public void setIdEntity(Long idEntity) {
		this.idEntity = idEntity;
	}

	public boolean isExibirInfoArquivo() {
		return exibirInfoArquivo;
	}

	public void setExibirInfoArquivo(boolean exibirInfoArquivo) {
		this.exibirInfoArquivo = exibirInfoArquivo;
	}

	public InfoOFX getInfoArquivo() {
		return infoArquivo;
	}

	public void setInfoArquivo(InfoOFX infoArquivo) {
		this.infoArquivo = infoArquivo;
	}

	public String getGoToListPage() {
		return goToListPage;
	}

	public void setGoToListPage(String goToListPage) {
		this.goToListPage = goToListPage;
	}

	public List<LancamentoConta> getLancamentoContaACriarAtualizar() {
		return lancamentoContaACriarAtualizar;
	}

	public void setLancamentoContaACriarAtualizar(List<LancamentoConta> lancamentoContaACriarAtualizar) {
		this.lancamentoContaACriarAtualizar = lancamentoContaACriarAtualizar;
	}

	public boolean isSelecionarTodosLancamentos() {
		return selecionarTodosLancamentos;
	}

	public void setSelecionarTodosLancamentos(boolean selecionarTodosLancamentos) {
		this.selecionarTodosLancamentos = selecionarTodosLancamentos;
	}

	public String getTipoArquivo() {
		return tipoArquivo;
	}

	public void setTipoArquivo(String tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}
}