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

	public void setOpcaoSistemaRepository(OpcaoSistemaRepository opcaoSistemaRepository) {
		this.opcaoSistemaRepository = opcaoSistemaRepository;
	}
	
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
				opcao.setValor((String)opcoesSistema.get(chave));
				opcao.setUsuario(usuario);
				if (opcoesSistema.get(chave) instanceof String) {
					opcao.setTipoValor("STRING");
				}
				if (opcoesSistema.get(chave) instanceof Boolean) {
					opcao.setTipoValor("BOOLEAN");
				}
				if (opcoesSistema.get(chave) instanceof Integer) {
					opcao.setTipoValor("INTEGER");
				}
				opcaoSistemaRepository.save(opcao);
			}
		}
	}
	
	private void validarValorOpcaoSistema(OpcaoSistema opcao, Object valor) throws BusinessException {
		if (opcao.isRequired()) {
			if (valor == null || ((String)valor).isEmpty()) {
				throw new BusinessException("Campo " + opcao.getChave() + " não pode ser vazio!");
			}
		}
	}
	
	/*** Métodos Getters das opções do sistema existentes ***/
	
	public Boolean getExibirMeioPagamento() {
		try {
			OpcaoSistema opcao = buscarPorChaveEUsuario("EXIBIR_MEIO_PAGAMENTO", usuarioComponent.getUsuarioLogado());
			if (opcao != null)
				return Boolean.valueOf(opcao.getValor());
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/*** Métodos Setters das opções do sistema existentes ***/
}