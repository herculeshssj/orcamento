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

package br.com.hslife.orcamento.component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoOpcaoSistema;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.repository.OpcaoSistemaRepository;

@Component
public class OpcaoSistemaComponent implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6347980819772095724L;
	
	@Autowired
	private OpcaoSistemaRepository opcaoSistemaRepository;
	
	@Autowired
	private UsuarioComponent usuarioComponent;
	
	// Guarda em cache os valores das opções do sistema
	private Map<String, Object> cacheOpcoesSistema = new HashMap<>();
	
	public OpcaoSistema buscarPorChaveEUsuario(String chave, Usuario usuario) throws BusinessException {
		return opcaoSistemaRepository.findOpcaoUserByChave(chave, usuario);
	}

	public Map<String, Object> buscarOpcoesGlobalAdminPorCDU(String cdu) {
		List<OpcaoSistema> opcoesSistema = opcaoSistemaRepository.findOpcoesGlobalAdminByCDU(cdu);
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		for (OpcaoSistema opcao : opcoesSistema) {
			if (opcao.getTipoValor().equals("STRING")) {
				parametros.put(opcao.getChave(), opcao.getValor());
			}
			if (opcao.getTipoValor().equals("BOOLEAN")) {
				parametros.put(opcao.getChave(), Boolean.valueOf(opcao.getValor()));
			}
			if (opcao.getTipoValor().equals("INTEGER")) {
				parametros.put(opcao.getChave(), Integer.valueOf(opcao.getValor()));
			}
		}
		
		return parametros;
	}
	
	public Map<String, Object> buscarOpcoesGlobalAdmin() {
		List<OpcaoSistema> opcoesSistema = opcaoSistemaRepository.findOpcoesGlobalAdmin();
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		for (OpcaoSistema opcao : opcoesSistema) {
			if (opcao.getTipoValor().equals("STRING")) {
				parametros.put(opcao.getChave(), opcao.getValor());
			}
			if (opcao.getTipoValor().equals("BOOLEAN")) {
				parametros.put(opcao.getChave(), Boolean.valueOf(opcao.getValor()));
			}
			if (opcao.getTipoValor().equals("INTEGER")) {
				parametros.put(opcao.getChave(), Integer.valueOf(opcao.getValor()));
			}
		}
		
		return parametros;
	}
	
	public Map<String, Object> buscarOpcoesUser(Usuario usuario) {
		List<OpcaoSistema> opcoesSistema = opcaoSistemaRepository.findOpcoesUser(usuario);
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		for (OpcaoSistema opcao : opcoesSistema) {
			if (opcao.getTipoValor().equals("STRING")) {
				parametros.put(opcao.getChave(), opcao.getValor());
			}
			if (opcao.getTipoValor().equals("BOOLEAN")) {
				parametros.put(opcao.getChave(), Boolean.valueOf(opcao.getValor()));
			}
			if (opcao.getTipoValor().equals("INTEGER")) {
				parametros.put(opcao.getChave(), Integer.valueOf(opcao.getValor()));
			}
		}
		
		return parametros;
	}
	
	public void salvarOpcoesGlobal(Map<String, Object> opcoesSistema) {
		
	}
	
	public void salvarOpcoesGlobalAdmin(Map<String, Object> opcoesSistema) throws BusinessException {
		OpcaoSistema opcao = new OpcaoSistema();
		for (String chave : opcoesSistema.keySet()) {
			opcao = opcaoSistemaRepository.findOpcaoGlobalAdminByChave(chave);
			validarValorOpcaoSistema(opcao, opcoesSistema.get(opcao.getChave()));
			/*
			if (opcao.getTipoValor().equals("STRING")) {
				opcao.setValor((String)opcoesSistema.get(chave));
			}
			if (opcao.getTipoValor().equals("BOOLEAN")) {
				opcao.setValor(Boolean.toString((Boolean)opcoesSistema.get(chave)));
			}
			if (opcao.getTipoValor().equals("INTEGER")) {
				opcao.setValor(Integer.toString((Integer)opcoesSistema.get(chave)));
			}*/
			opcao.setValor((String)opcoesSistema.get(chave));
			opcaoSistemaRepository.update(opcao);
		}
	}
	
	public void salvarOpcoesUser(Map<String, Object> opcoesSistema, Usuario usuario) throws BusinessException {
		OpcaoSistema opcao;
		for (String chave : opcoesSistema.keySet()) {			
			opcao = opcaoSistemaRepository.findOpcaoUserByChave(chave, usuario);
			if (opcao != null) {
				validarValorOpcaoSistema(opcao, opcoesSistema.get(opcao.getChave()));			
				opcao.setValor((String)opcoesSistema.get(chave));
				opcaoSistemaRepository.update(opcao);
			} else {
				opcao = new OpcaoSistema();
				opcao.setCasoDeUso("");
				opcao.setChave(chave);
				opcao.setTipoOpcaoSistema(TipoOpcaoSistema.USER);
				opcao.setUsuario(usuario);
				if (opcoesSistema.get(chave) instanceof String) {
					opcao.setTipoValor("STRING");
					opcao.setValor((String)opcoesSistema.get(chave));
				}
				if (opcoesSistema.get(chave) instanceof Boolean) {
					opcao.setTipoValor("BOOLEAN");
					opcao.setValor(Boolean.toString((Boolean)opcoesSistema.get(chave)));
				}
				if (opcoesSistema.get(chave) instanceof Integer) {
					opcao.setTipoValor("INTEGER");
					opcao.setValor(Integer.toString((Integer)opcoesSistema.get(chave)));
				}
				opcaoSistemaRepository.save(opcao);
			}
		}
	}
	
	public void excluirOpcoesUsuario(Usuario usuario) {
		// Exclui as opções do sistema do usuário
		for (OpcaoSistema opcao : opcaoSistemaRepository.findByUsuario(usuario)) {
			opcaoSistemaRepository.delete(opcao);
		}
	}
	
	private void validarValorOpcaoSistema(OpcaoSistema opcao, Object valor) throws BusinessException {
		if (opcao.isRequired()) {
			if (valor == null || ((String)valor).isEmpty()) {
				throw new BusinessException("Campo " + opcao.getChave() + " não pode ser vazio!");
			}
		}
	}
	
	public void setarOpcoesPadraoUsuario(Usuario entity) throws BusinessException {
		// Seta as opções do sistema que são individuais para cada usuário
		Map<String, Object> opcoesUsuario = new HashMap<String, Object>();
		opcoesUsuario.put("GERAL_SUPRIMIR_TEXTO_MEIO", Boolean.FALSE);
		opcoesUsuario.put("GERAL_EXIBIR_BUSCAS_REALIZADAS", Boolean.FALSE);
		opcoesUsuario.put("CONTA_EXIBIR_INATIVAS", Boolean.TRUE);
		opcoesUsuario.put("LANCAMENTO_LIMITE_QUANTIDADE_REGISTROS", Integer.valueOf(100));
		opcoesUsuario.put("RESUMO_FORMA_AGRUPAMENTO_PAGAMENTOS", "INDIVIDUAL");
		opcoesUsuario.put("CONTA_EXIBIR_MEIO_PAGAMENTO", Boolean.FALSE);
		opcoesUsuario.put("RESUMO_LIMITE_QUANTIDADE_FECHAMENTOS", Integer.valueOf(12));
		opcoesUsuario.put("NOTIFICAR_AGENDAMENTO_EMAIL", Boolean.FALSE);
		opcoesUsuario.put("ARQUIVO_TEMPO_GUARDA_GERAL", Integer.valueOf(1));
		opcoesUsuario.put("ARQUIVO_TEMPO_GUARDA_LANCAMENTOCONTA", Integer.valueOf(1));
		opcoesUsuario.put("ARQUIVO_TEMPO_GUARDA_FATURACARTAO", Integer.valueOf(1));
		opcoesUsuario.put("ARQUIVO_TEMPO_GUARDA_LANCAMENTOPERIODICO", Integer.valueOf(1));
		opcoesUsuario.put("ARQUIVO_TEMPO_GUARDA_DOCUMENTOS", Integer.valueOf(1));
		this.salvarOpcoesUser(opcoesUsuario, entity);
	}
	
	/* Atualização do cache de opções do sistema */
	public void atualizarCacheOpcoesSistema() {
		cacheOpcoesSistema.clear();
		cacheOpcoesSistema.put("CONTA_EXIBIR_MEIO_PAGAMENTO", this.getExibirMeioPagamento());
		cacheOpcoesSistema.put("GERAL_EXIBIR_BUSCAS_REALIZADAS", this.getExibirBuscasRealizadas());
	}
	
	/*** Métodos Getters das opções do sistema existentes ***/
	
	public Boolean getExibirMeioPagamento() {
		try {
			// Verifica se o valor existe no cache
			if (cacheOpcoesSistema.containsKey("CONTA_EXIBIR_MEIO_PAGAMENTO") && cacheOpcoesSistema.get("CONTA_EXIBIR_MEIO_PAGAMENTO") != null) {
				return Boolean.valueOf((Boolean)cacheOpcoesSistema.get("CONTA_EXIBIR_MEIO_PAGAMENTO"));
			} else {
				OpcaoSistema opcao = buscarPorChaveEUsuario("CONTA_EXIBIR_MEIO_PAGAMENTO", usuarioComponent.getUsuarioLogado());
				if (opcao != null)
					return Boolean.valueOf(opcao.getValor());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Boolean getExibirBuscasRealizadas() {
		try {
			// Verifica se o valor existe no cache
			if (cacheOpcoesSistema.containsKey("GERAL_EXIBIR_BUSCAS_REALIZADAS") && cacheOpcoesSistema.get("GERAL_EXIBIR_BUSCAS_REALIZADAS") != null) {
				return Boolean.valueOf((Boolean)cacheOpcoesSistema.get("GERAL_EXIBIR_BUSCAS_REALIZADAS"));
			} else {
				OpcaoSistema opcao = buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", usuarioComponent.getUsuarioLogado());
				if (opcao != null)
					return Boolean.valueOf(opcao.getValor());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getFormaAgrupamentoPagamento() {
		try {
			OpcaoSistema opcao = buscarPorChaveEUsuario("RESUMO_FORMA_AGRUPAMENTO_PAGAMENTOS", usuarioComponent.getUsuarioLogado());
			if (opcao != null)
				return (String)opcao.getValor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public Boolean getExibirContasInativas() {
		try {
			OpcaoSistema opcao = buscarPorChaveEUsuario("CONTA_EXIBIR_INATIVAS", usuarioComponent.getUsuarioLogado());
			if (opcao != null)
				return Boolean.valueOf(opcao.getValor());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Integer getLimiteQuantidadeRegistros() {
		try {
			OpcaoSistema opcao = buscarPorChaveEUsuario("LANCAMENTO_LIMITE_QUANTIDADE_REGISTROS", usuarioComponent.getUsuarioLogado());
			if (opcao != null)
				return Integer.valueOf(opcao.getValor());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 100; // valor padrão.
	}
	
	public Integer getLimiteQuantidadeFechamentos() {
		try {
			OpcaoSistema opcao = buscarPorChaveEUsuario("RESUMO_LIMITE_QUANTIDADE_FECHAMENTOS", usuarioComponent.getUsuarioLogado());
			if (opcao != null)
				return Integer.valueOf(opcao.getValor());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 12; // valor padrão.
	}
	
	public Boolean getNotificarAgendamentosEmail(Usuario usuario) {
		try {
			OpcaoSistema opcao = buscarPorChaveEUsuario("NOTIFICAR_AGENDAMENTO_EMAIL", usuario);
			if (opcao != null)
				return Boolean.valueOf(opcao.getValor());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Map<String, Integer> getOpcoesArquivosAnexados(Usuario usuario) {
		Map<String, Integer> opcoes = new HashMap<String, Integer>();
		try {
			for (OpcaoSistema opcao : opcaoSistemaRepository.findOpcoesUserByCasoUso("Arquivo", usuario)) {
				opcoes.put(opcao.getChave(), Integer.valueOf(opcao.getValor()));
			}
			return opcoes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return opcoes; 
	}
	
	/*** Métodos Setters das opções do sistema existentes ***/
}