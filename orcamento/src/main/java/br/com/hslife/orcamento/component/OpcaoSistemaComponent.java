/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a qualquer
    
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

package br.com.hslife.orcamento.component;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.facade.IOpcaoSistema;

@Component
@Transactional(propagation=Propagation.SUPPORTS)
public class OpcaoSistemaComponent {
	
	private static final Logger logger = LogManager.getLogger(OpcaoSistemaComponent.class);
	
	@Autowired
	private IOpcaoSistema service;
	
	@Autowired
	private UsuarioComponent usuarioComponent;
	
	@Autowired
	private IMoeda moedaService;
	
	public UsuarioComponent getUsuarioComponent() {
		return usuarioComponent;
	}

	public IMoeda getMoedaService() {
		return moedaService;
	}

	public IOpcaoSistema getService() {
		return service;
	}

	// Guarda em cache os valores das opções do sistema
	private Map<Usuario, Map<String, Object>> cacheUsuarioOpcoesSistema = new HashMap<>();
	
	public OpcaoSistema buscarPorChaveEUsuario(String chave, Usuario usuario) throws ApplicationException {
		return getService().buscarOpcaoUsuarioPorChave(chave, usuario);
	}
	
	// Método foi deixado no componente para poder centralizar a manutenção de cada parâmetro
	// O Service não conhece os parâmetros existentes, o Component sim.
	public void setarOpcoesPadraoUsuario(Usuario entity) {
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
		opcoesUsuario.put("ARQUIVO_TEMPO_GUARDA_DIVIDATERCEIRO", Integer.valueOf(1));
		opcoesUsuario.put("ARQUIVO_TEMPO_GUARDA_PAGAMENTODIVIDATERCEIRO", Integer.valueOf(1));
		opcoesUsuario.put("CONTROLAR_ESTOQUE_DESPENSA", Boolean.TRUE);
		getService().salvarOpcoesUser(opcoesUsuario, entity);
	}
	
	/* Atualização do cache de opções do sistema */
	public void atualizarCacheOpcoesSistema() throws ApplicationException {		
		Usuario usuarioLogado = getUsuarioComponent().getUsuarioLogado();
		Moeda moedaPadrao = getMoedaService().buscarPadraoPorUsuario(usuarioLogado);
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
		cacheUsuarioOpcoesSistema.get(usuarioLogado).put("CONTROLAR_ESTOQUE_DESPENSA", this.getControlarEstoqueItemDespensa());
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
		Usuario usuarioLogado = getUsuarioComponent().getUsuarioLogado();
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
		} catch (ApplicationException be) {
			logger.catching(be);
		} catch (Exception e) {
			logger.catching(e);
		}
		return false;
	}
	
	public Boolean getExibirBuscasRealizadas() {
		Usuario usuarioLogado = getUsuarioComponent().getUsuarioLogado();
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
		} catch (ApplicationException be) {
			logger.catching(be);
		} catch (Exception e) {
			logger.catching(e);
		}
		return false;
	}
	
	public Integer getLimiteQuantidadeRegistros() {
		Usuario usuarioLogado = getUsuarioComponent().getUsuarioLogado();
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
		} catch (ApplicationException be) {
			logger.catching(be);
		} catch (Exception e) {
			logger.catching(e);
		}
		return 100; // valor padrão.
	}
	
	public String getFormaAgrupamentoPagamento() {
		Usuario usuarioLogado = getUsuarioComponent().getUsuarioLogado();
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
		} catch (ApplicationException be) {
			logger.catching(be);
		} catch (Exception e) {
			logger.catching(e);
		}
		return "";
	}
	
	public Boolean getExibirContasInativas() {
		Usuario usuarioLogado = getUsuarioComponent().getUsuarioLogado();
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
		} catch (ApplicationException be) {
			logger.catching(be);
		} catch (Exception e) {
			logger.catching(e);
		}
		return false;
	}
	
	public Integer getLimiteQuantidadeFechamentos() {
		Usuario usuarioLogado = getUsuarioComponent().getUsuarioLogado();
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
		} catch (ApplicationException be) {
			logger.catching(be);
		} catch (Exception e) {
			logger.catching(e);
		}	
		return 12; // valor padrão.
	}
	
	public Boolean getNotificarAgendamentosEmail(Usuario usuario) {
		try {
			OpcaoSistema opcao = buscarPorChaveEUsuario("NOTIFICAR_AGENDAMENTO_EMAIL", usuario);
			if (opcao != null)
				return Boolean.valueOf(opcao.getValor());
		} catch (ApplicationException be) {
			logger.catching(be);
		} catch (Exception e) {
			logger.catching(e);
		}
		return false;
	}
	
	public Map<String, Integer> getOpcoesArquivosAnexados(Usuario usuario) {
		Map<String, Integer> opcoes = new HashMap<String, Integer>();
			
		for (OpcaoSistema opcao : getService().buscarOpcoesUserPorCasoUso("Arquivo", usuario)) {
			opcoes.put(opcao.getChave(), Integer.valueOf(opcao.getValor()));
		}
		return opcoes;
	}
	
	public Moeda getMoedaPadrao() {
		Usuario usuarioLogado = getUsuarioComponent().getUsuarioLogado();
		
		// Verifica se o valor existe no cache
		if (recuperaParametroCacheUsuario(usuarioLogado, "MOEDA_PADRAO") != null) {
			return (Moeda)recuperaParametroCacheUsuario(usuarioLogado, "MOEDA_PADRAO");
		} else {
			Moeda moeda = getMoedaService().buscarPadraoPorUsuario(usuarioLogado);
			setarParametroCacheUsuario(usuarioLogado, "MOEDA_PADRAO", moeda);
			return (Moeda)recuperaParametroCacheUsuario(usuarioLogado, "MOEDA_PADRAO");
		}
	}
	
	public Boolean getControlarEstoqueItemDespensa() {
		Usuario usuarioLogado = getUsuarioComponent().getUsuarioLogado();
		try {
			// Verifica se o valor existe no cache
			if (recuperaParametroCacheUsuario(usuarioLogado, "CONTROLAR_ESTOQUE_DESPENSA") != null) {
				return Boolean.valueOf((Boolean)recuperaParametroCacheUsuario(usuarioLogado, "CONTROLAR_ESTOQUE_DESPENSA"));
			} else {
				OpcaoSistema opcao = buscarPorChaveEUsuario("CONTROLAR_ESTOQUE_DESPENSA", usuarioLogado);
				if (opcao != null) {
					setarParametroCacheUsuario(usuarioLogado, "CONTROLAR_ESTOQUE_DESPENSA", Boolean.valueOf(opcao.getValor()));
					return Boolean.valueOf((Boolean)recuperaParametroCacheUsuario(usuarioLogado, "CONTROLAR_ESTOQUE_DESPENSA"));
				}
			}
		} catch (ApplicationException be) {
			logger.catching(be);
		} catch (Exception e) {
			logger.catching(e);
		}
		return true;
	}
}