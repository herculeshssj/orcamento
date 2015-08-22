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

package br.com.hslife.orcamento.component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoOpcaoSistema;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.repository.MoedaRepository;
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
	
	@Autowired
	private MoedaRepository moedaRepository;
	
	// Guarda em cache os valores das opções do sistema
	private Map<Usuario, Map<String, Object>> cacheUsuarioOpcoesSistema = new HashMap<>();
	
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
			//opcao.setValor((String)opcoesSistema.get(chave)); -- Mudança decorrente de usar o JSF 2.2.10 e PrimeFaces 5.2
			opcaoSistemaRepository.update(opcao);
		}
	}
	
	public void salvarOpcoesUser(Map<String, Object> opcoesSistema, Usuario usuario) throws BusinessException {
		OpcaoSistema opcao;
		for (String chave : opcoesSistema.keySet()) {			
			opcao = opcaoSistemaRepository.findOpcaoUserByChave(chave, usuario);
			if (opcao != null) {
				validarValorOpcaoSistema(opcao, opcoesSistema.get(opcao.getChave()));			
				//opcao.setValor((String)opcoesSistema.get(chave)); -- Mudança decorrente de usar o JSF 2.2.10 e PrimeFaces 5.2
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
			//if (valor == null || ((String)valor).isEmpty()) { -- Mudança feita em virtude da atualização para JSF 2.2.10 e PrimeFaces 5.2
			if (valor == null) {
				throw new BusinessException("Campo " + opcao.getChave() + " não pode ser vazio!");
			}
		}
	}
	
	public void setarOpcoesPadraoUsuario(Usuario entity) throws BusinessException {
		// Seta as opções do sistema que são individuais para cada usuário
		Map<String, Object> opcoesUsuario = new HashMap<String, Object>();
		opcoesUsuario.put("GERAL_EXIBIR_BUSCAS_REALIZADAS", Boolean.FALSE);
		opcoesUsuario.put("CONTA_EXIBIR_INATIVAS", Boolean.TRUE);
		opcoesUsuario.put("LANCAMENTO_LIMITE_QUANTIDADE_REGISTROS", Integer.valueOf(100));
		opcoesUsuario.put("RESUMO_FORMA_AGRUPAMENTO_PAGAMENTOS", "INDIVIDUAL");
		opcoesUsuario.put("CONTA_EXIBIR_MEIO_PAGAMENTO", Boolean.FALSE);
		opcoesUsuario.put("RESUMO_LIMITE_QUANTIDADE_FECHAMENTOS", Integer.valueOf(12));
		opcoesUsuario.put("NOTIFICAR_AGENDAMENTO_EMAIL", Boolean.FALSE);
		opcoesUsuario.put("ARQUIVO_TEMPO_GUARDA_LANCAMENTOCONTA", Integer.valueOf(1));
		opcoesUsuario.put("ARQUIVO_TEMPO_GUARDA_FATURACARTAO", Integer.valueOf(1));
		opcoesUsuario.put("ARQUIVO_TEMPO_GUARDA_LANCAMENTOPERIODICO", Integer.valueOf(1));
		opcoesUsuario.put("ARQUIVO_TEMPO_GUARDA_DOCUMENTOS", Integer.valueOf(1));
		this.salvarOpcoesUser(opcoesUsuario, entity);
	}
	
	/* Atualização do cache de opções do sistema */
	public void atualizarCacheOpcoesSistema() {		
		Usuario usuarioLogado = usuarioComponent.getUsuarioLogado();
		Moeda moedaPadrao = moedaRepository.findDefaultByUsuario(usuarioLogado);
		// Verifica se existe entrada no cache para o usuário atual
		if (cacheUsuarioOpcoesSistema.get(usuarioLogado) == null) {
			// Cria um novo Map de parâmetros para o usuário
			cacheUsuarioOpcoesSistema.put(usuarioLogado, new HashMap<>());
		}
		
		// Limpa o cache de parâmetros do usuários e carrega novamente os parâmetros
		cacheUsuarioOpcoesSistema.get(usuarioLogado).clear();
		cacheUsuarioOpcoesSistema.get(usuarioLogado).put("CONTA_EXIBIR_MEIO_PAGAMENTO", this.getExibirMeioPagamento());
		cacheUsuarioOpcoesSistema.get(usuarioLogado).put("GERAL_EXIBIR_BUSCAS_REALIZADAS", this.getExibirBuscasRealizadas());
		cacheUsuarioOpcoesSistema.get(usuarioLogado).put("LANCAMENTO_LIMITE_QUANTIDADE_REGISTROS", this.getLimiteQuantidadeRegistros());
		cacheUsuarioOpcoesSistema.get(usuarioLogado).put("CONTA_EXIBIR_INATIVAS", this.getExibirContasInativas());
		cacheUsuarioOpcoesSistema.get(usuarioLogado).put("RESUMO_LIMITE_QUANTIDADE_FECHAMENTOS", this.getLimiteQuantidadeFechamentos());
		cacheUsuarioOpcoesSistema.get(usuarioLogado).put("RESUMO_FORMA_AGRUPAMENTO_PAGAMENTOS", this.getFormaAgrupamentoPagamento());
		cacheUsuarioOpcoesSistema.get(usuarioLogado).put("MOEDA_PADRAO", moedaPadrao);
	}
	
	/**
	 * Retorna o valor do parâmetro informado do cache de usuários.
	 * Caso o parâmetro não seja encontrado ou o usuário informa não
	 * exista o valor retornado é null. 
	 * 
	 * @param usuarioLogado Usuário atualmente logado
	 * @param parametro Parâmetro cujo valor deseja recuperar
	 * @return valor do parâmetro no cache
	 */
	private Object recuperaParametroCacheUsuario(Usuario usuarioLogado, String parametro) {
		Object valueParameter = null;
		if (cacheUsuarioOpcoesSistema.containsKey(usuarioLogado)) {
			if (cacheUsuarioOpcoesSistema.get(usuarioLogado).containsKey(parametro)) {
				valueParameter = cacheUsuarioOpcoesSistema.get(usuarioLogado).get(parametro);
			}
		}
		
		return valueParameter;
	}
	
	/**
	 * Armazena o valor informado do parâmetro no cache de usuários.
	 * Caso o valor ainda não existe, ou o usuário ainda não foi 
	 * setado no cache, o mesmo será feito.
	 * 
	 * @param usuarioLogado Usuário atualmente logado
	 * @param parametro Parâmetro cujo valor deseja armazenar
	 * @param valor valor a ser armazenado
	 */
	private void setarParametroCacheUsuario(Usuario usuarioLogado, String parametro, Object valor) {
		if (!cacheUsuarioOpcoesSistema.containsKey(usuarioLogado)) {
			cacheUsuarioOpcoesSistema.put(usuarioLogado, new HashMap<>());
			cacheUsuarioOpcoesSistema.get(usuarioLogado).put(parametro, valor);
		} else {
			cacheUsuarioOpcoesSistema.get(usuarioLogado).put(parametro, valor);
		}
	}
	
	/*** Métodos Getters das opções do sistema existentes ***/
	
	public Boolean getExibirMeioPagamento() {
		Usuario usuarioLogado = usuarioComponent.getUsuarioLogado();
		try {
			// Verifica se o valor existe no cache
			if (recuperaParametroCacheUsuario(usuarioLogado, "CONTA_EXIBIR_MEIO_PAGAMENTO") != null) {
				return Boolean.valueOf((Boolean)recuperaParametroCacheUsuario(usuarioLogado, "CONTA_EXIBIR_MEIO_PAGAMENTO"));
			} else {
				OpcaoSistema opcao = buscarPorChaveEUsuario("CONTA_EXIBIR_MEIO_PAGAMENTO", usuarioLogado);
				if (opcao != null) {
					setarParametroCacheUsuario(usuarioLogado, "CONTA_EXIBIR_MEIO_PAGAMENTO", Boolean.valueOf(opcao.getValor()));
					return Boolean.valueOf((Boolean)recuperaParametroCacheUsuario(usuarioLogado, "CONTA_EXIBIR_MEIO_PAGAMENTO"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Boolean getExibirBuscasRealizadas() {
		Usuario usuarioLogado = usuarioComponent.getUsuarioLogado();
		try {
			// Verifica se o valor existe no cache
			if (recuperaParametroCacheUsuario(usuarioLogado, "GERAL_EXIBIR_BUSCAS_REALIZADAS") != null) {
				return Boolean.valueOf((Boolean)recuperaParametroCacheUsuario(usuarioLogado, "GERAL_EXIBIR_BUSCAS_REALIZADAS"));
			} else {
				OpcaoSistema opcao = buscarPorChaveEUsuario("GERAL_EXIBIR_BUSCAS_REALIZADAS", usuarioLogado);
				if (opcao != null) {
					setarParametroCacheUsuario(usuarioLogado, "GERAL_EXIBIR_BUSCAS_REALIZADAS", Boolean.valueOf(opcao.getValor()));
					return Boolean.valueOf((Boolean)recuperaParametroCacheUsuario(usuarioLogado, "GERAL_EXIBIR_BUSCAS_REALIZADAS"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Integer getLimiteQuantidadeRegistros() {
		Usuario usuarioLogado = usuarioComponent.getUsuarioLogado();
		try {
			// Verifica se o valor existe no cache
			if (recuperaParametroCacheUsuario(usuarioLogado, "LANCAMENTO_LIMITE_QUANTIDADE_REGISTROS") != null) {
				return Integer.valueOf((Integer)recuperaParametroCacheUsuario(usuarioLogado, "LANCAMENTO_LIMITE_QUANTIDADE_REGISTROS"));
			} else {
				OpcaoSistema opcao = buscarPorChaveEUsuario("LANCAMENTO_LIMITE_QUANTIDADE_REGISTROS", usuarioLogado);
				if (opcao != null) {
					setarParametroCacheUsuario(usuarioLogado, "LANCAMENTO_LIMITE_QUANTIDADE_REGISTROS", Integer.valueOf(opcao.getValor()));
					return Integer.valueOf((Integer)recuperaParametroCacheUsuario(usuarioLogado, "LANCAMENTO_LIMITE_QUANTIDADE_REGISTROS"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 100; // valor padrão.
	}
	
	public String getFormaAgrupamentoPagamento() {
		Usuario usuarioLogado = usuarioComponent.getUsuarioLogado();
		try {
			// Verifica se o valor existe no cache
			if (recuperaParametroCacheUsuario(usuarioLogado, "RESUMO_FORMA_AGRUPAMENTO_PAGAMENTOS") != null) {
				return (String)recuperaParametroCacheUsuario(usuarioLogado, "RESUMO_FORMA_AGRUPAMENTO_PAGAMENTOS");
			} else {
				OpcaoSistema opcao = buscarPorChaveEUsuario("RESUMO_FORMA_AGRUPAMENTO_PAGAMENTOS", usuarioLogado);
				if (opcao != null) {
					setarParametroCacheUsuario(usuarioLogado, "RESUMO_FORMA_AGRUPAMENTO_PAGAMENTOS", opcao.getValor());
					return (String)recuperaParametroCacheUsuario(usuarioLogado, "RESUMO_FORMA_AGRUPAMENTO_PAGAMENTOS");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public Boolean getExibirContasInativas() {
		Usuario usuarioLogado = usuarioComponent.getUsuarioLogado();
		try {
			// Verifica se o valor existe no cache
			if (recuperaParametroCacheUsuario(usuarioLogado, "CONTA_EXIBIR_INATIVAS") != null) {
				return Boolean.valueOf((Boolean)recuperaParametroCacheUsuario(usuarioLogado, "CONTA_EXIBIR_INATIVAS"));
			} else {
				OpcaoSistema opcao = buscarPorChaveEUsuario("CONTA_EXIBIR_INATIVAS", usuarioLogado);
				if (opcao != null) {
					setarParametroCacheUsuario(usuarioLogado, "CONTA_EXIBIR_INATIVAS", Boolean.valueOf(opcao.getValor()));
					return Boolean.valueOf((Boolean)recuperaParametroCacheUsuario(usuarioLogado, "CONTA_EXIBIR_INATIVAS"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Integer getLimiteQuantidadeFechamentos() {
		Usuario usuarioLogado = usuarioComponent.getUsuarioLogado();
		try {
			// Verifica se o valor existe no cache
			if (recuperaParametroCacheUsuario(usuarioLogado, "RESUMO_LIMITE_QUANTIDADE_FECHAMENTOS") != null) {
				return Integer.valueOf((Integer)recuperaParametroCacheUsuario(usuarioLogado, "RESUMO_LIMITE_QUANTIDADE_FECHAMENTOS"));
			} else {
				OpcaoSistema opcao = buscarPorChaveEUsuario("RESUMO_LIMITE_QUANTIDADE_FECHAMENTOS", usuarioLogado);
				if (opcao != null) {
					setarParametroCacheUsuario(usuarioLogado, "RESUMO_LIMITE_QUANTIDADE_FECHAMENTOS", Integer.valueOf(opcao.getValor()));
					return Integer.valueOf((Integer)recuperaParametroCacheUsuario(usuarioLogado, "RESUMO_LIMITE_QUANTIDADE_FECHAMENTOS"));
				}
			}
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
	
	public Moeda getMoedaPadrao() {
		Usuario usuarioLogado = usuarioComponent.getUsuarioLogado();
		try {
			// Verifica se o valor existe no cache
			if (recuperaParametroCacheUsuario(usuarioLogado, "MOEDA_PADRAO") != null) {
				return (Moeda)recuperaParametroCacheUsuario(usuarioLogado, "MOEDA_PADRAO");
			} else {
				Moeda moeda = moedaRepository.findDefaultByUsuario(usuarioLogado);
				setarParametroCacheUsuario(usuarioLogado, "MOEDA_PADRAO", moeda);
				return (Moeda)recuperaParametroCacheUsuario(usuarioLogado, "MOEDA_PADRAO");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; // Este return nunca deve ser invocado.
	}
	
	/*** Métodos Setters das opções do sistema existentes ***/
}