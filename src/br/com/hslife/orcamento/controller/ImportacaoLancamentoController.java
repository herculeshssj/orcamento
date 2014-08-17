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

import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IImportacaoLancamento;
import br.com.hslife.orcamento.model.InfoOFX;

@Component("importacaoLancamentoMB")
@Scope("session")
public class ImportacaoLancamentoController extends AbstractController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3650197694657342579L;

	@Autowired
	private IConta contaService;
	
	@Autowired
	private IImportacaoLancamento service;
	
	private Conta contaSelecionada;
	private Arquivo arquivoAnexado;
	private boolean gerarNovosLancamentos;
	private boolean exibirInfoArquivo;
	private InfoOFX infoArquivo;
	private String tipoImportacao = "CONTA";
	
	private List<LancamentoImportado> lancamentosImportadosValidos;
	private List<LancamentoConta> lancamentoContaAInserir;
	private List<LancamentoConta> lancamentoContaAAtualizar;
	
	private LancamentoImportado entity;
	private List<LancamentoImportado> listEntity;
	private Long idEntity;

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
		initializeEntity();
		tipoImportacao = "CONTA";
		return goToListPage();
	}
	
	public String startUpCartao() {
		initializeEntity();
		tipoImportacao = "CARTAO";
		return goToListPage();
	}
	
	public String find() {
		try {
			listEntity = getService().buscarLancamentoImportadoPorConta(contaSelecionada);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return null;
	}
	
	public String create() {
		return this.goToStep1();
	}
	
	public String cancel() {
		try {
			exibirInfoArquivo = false;
			// Verifica se a listagem de resultados está nula ou não para poder efetuar novamente a busca
			if (listEntity != null && !listEntity.isEmpty()) {
				// Inicializa os objetos
				initializeEntity();
				
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", getUsuarioLogado());
							
				// Determina se a busca será executada novamente
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {					
					find();
				}
			} else {
				initializeEntity();
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "/pages/ImportacaoLancamento/listImportacaoLancamento";
	}
	
	public String goToStep1() {
		exibirInfoArquivo = false;
		return "/pages/ImportacaoLancamento/formImportacaoLancamentoPasso1";
	}
	
	public String goToStep2() {
		try {
			lancamentosImportadosValidos = getService().buscarLancamentoImportadoPorConta(contaSelecionada);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "/pages/ImportacaoLancamento/formImportacaoLancamentoPasso2";
	}
	
	public String goToStep3() {
		try {
			lancamentoContaAAtualizar = getService().buscarLancamentoContaAAtualizar(getService().buscarLancamentoImportadoPorConta(contaSelecionada));
			lancamentoContaAInserir = getService().gerarLancamentoContaAInserir(getService().buscarLancamentoImportadoPorConta(contaSelecionada));
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return "/pages/ImportacaoLancamento/formImportacaoLancamentoPasso3";
	}
	
	public String goToListPage() {
		return "/pages/ImportacaoLancamento/listImportacaoLancamento";
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
			warnMessage("Carregue um arquivo OFX antes de processar!");			
		} else {
			try {
				getService().processarArquivoImportado(arquivoAnexado, contaSelecionada);
				infoArquivo = getService().obterInformacaoArquivoImportado(arquivoAnexado, contaSelecionada);
				exibirInfoArquivo = true;
				infoMessage("Arquivo processado com sucesso!");
			} catch (Exception e) {
				errorMessage(e.getMessage());
			}
		}
	}
	
	public void excluirLancamentoImportado() {
		try {
			entity = getService().buscarPorID(idEntity);
			getService().excluirLancamentoImportado(entity);
			infoMessage("Lançamento excluído com sucesso!");
			// Verifica se a listagem de resultados está nula ou não para poder efetuar novamente a busca
			if (listEntity != null && !listEntity.isEmpty()) {
				// Inicializa os objetos
				initializeEntity();
				
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", getUsuarioLogado());
							
				// Determina se a busca será executada novamente
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {					
					find();
				}
			} else {
				initializeEntity();
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public void importarLancamento() {
		try {
			entity = getService().buscarPorID(idEntity);
			getService().importarLancamento(entity);
			infoMessage("Lançamento importado com sucesso!");
			
			// Verifica se a listagem de resultados está nula ou não para poder efetuar novamente a busca
			if (listEntity != null && !listEntity.isEmpty()) {
				// Inicializa os objetos
				initializeEntity();
				
				// Obtém o valor da opção do sistema
				OpcaoSistema opcao = getOpcoesSistema().buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", getUsuarioLogado());
							
				// Determina se a busca será executada novamente
				if (opcao != null && Boolean.valueOf(opcao.getValor())) {					
					find();
				}
			} else {
				initializeEntity();
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public String processarLancamentos() {
		try {
			getService().processarLancamentosImportados(contaSelecionada, gerarNovosLancamentos);
			infoMessage("Lançamentos importados com sucesso!");
			initializeEntity();
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return goToListPage();
	}
	
	public List<Conta> getListaConta() {
		try {
			switch (tipoImportacao) {
			case "CONTA":
				return contaService.buscarAtivosPorUsuario(getUsuarioLogado());
			case "CARTAO":
				return contaService.buscarSomenteTipoCartaoAtivosPorUsuario(getUsuarioLogado());
			default:
				break;
			}
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<>();
	}
	
	public IImportacaoLancamento getService() {
		return service;
	}

	public void setService(IImportacaoLancamento service) {
		this.service = service;
	}

	public void setContaService(IConta contaService) {
		this.contaService = contaService;
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

	public List<LancamentoImportado> getLancamentosImportadosValidos() {
		return lancamentosImportadosValidos;
	}

	public void setLancamentosImportadosValidos(
			List<LancamentoImportado> lancamentosImportadosValidos) {
		this.lancamentosImportadosValidos = lancamentosImportadosValidos;
	}

	public List<LancamentoConta> getLancamentoContaAInserir() {
		return lancamentoContaAInserir;
	}

	public void setLancamentoContaAInserir(
			List<LancamentoConta> lancamentoContaAInserir) {
		this.lancamentoContaAInserir = lancamentoContaAInserir;
	}

	public List<LancamentoConta> getLancamentoContaAAtualizar() {
		return lancamentoContaAAtualizar;
	}

	public void setLancamentoContaAAtualizar(
			List<LancamentoConta> lancamentoContaAAtualizar) {
		this.lancamentoContaAAtualizar = lancamentoContaAAtualizar;
	}

	public boolean isGerarNovosLancamentos() {
		return gerarNovosLancamentos;
	}

	public void setGerarNovosLancamentos(boolean gerarNovosLancamentos) {
		this.gerarNovosLancamentos = gerarNovosLancamentos;
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

	public String getTipoImportacao() {
		return tipoImportacao;
	}

	public void setTipoImportacao(String tipoImportacao) {
		this.tipoImportacao = tipoImportacao;
	}
}