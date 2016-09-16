/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

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

package br.com.hslife.orcamento.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.entity.OpcaoSistema;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoOpcaoSistema;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IOpcaoSistema;
import br.com.hslife.orcamento.repository.OpcaoSistemaRepository;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;

@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor={BusinessException.class})
public class OpcaoSistemaService implements IOpcaoSistema {
	
	@Autowired
	public SessionFactory sessionFactory;
	
	@Autowired
	private OpcaoSistemaRepository repository;
	
	public OpcaoSistemaRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}

	public void salvarOpcoesGlobal(Map<String, Object> opcoesSistema) {
		// TODO implementar
	}
	
//	public void excluirOpcoesUsuario(Usuario usuario) {
//	// Exclui as opções do sistema do usuário
//	for (OpcaoSistema opcao : opcaoSistemaRepository.findByUsuario(usuario)) {
//		opcaoSistemaRepository.delete(opcao);
//	}
//}
	
	public void salvarOpcoesGlobalAdmin(Map<String, Object> opcoesSistema) throws BusinessException {
		OpcaoSistema opcao = new OpcaoSistema();
		for (String chave : opcoesSistema.keySet()) {
			opcao = getRepository().findOpcaoGlobalAdminByChave(chave);
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
			getRepository().update(opcao);
		}
	}
	
	public void salvarOpcoesUser(Map<String, Object> opcoesSistema, Usuario usuario) throws BusinessException {
		OpcaoSistema opcao;
		for (String chave : opcoesSistema.keySet()) {			
			opcao = getRepository().findOpcaoUserByChave(chave, usuario);
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
				getRepository().update(opcao);
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
				getRepository().save(opcao);
			}
		}
	}
	
	private void validarValorOpcaoSistema(OpcaoSistema opcao, Object valor) throws BusinessException {
		if (opcao.isRequired()) {
			EntityPersistenceUtil.validaCampoNulo(opcao.getChave(), valor);
		}
	}
	
	public List<OpcaoSistema> buscarOpcoesGlobalAdmin() throws BusinessException {
		return getRepository().findOpcoesGlobalAdmin();
	}
	
	public Map<String, Object> buscarMapOpcoesGlobalAdmin() {
		List<OpcaoSistema> opcoesSistema = getRepository().findOpcoesGlobalAdmin();
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
	
	public Map<String, Object> buscarMapOpcoesUser(Usuario usuario) {
		List<OpcaoSistema> opcoesSistema = getRepository().findOpcoesUser(usuario);
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
	
	public Map<String, Object> buscarOpcoesGlobalAdminPorCDU(String cdu) throws BusinessException {
		List<OpcaoSistema> opcoesSistema = getRepository().findOpcoesGlobalAdminByCDU(cdu);
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
	
	@Override
	public OpcaoSistema buscarOpcaoUsuarioPorChave(String chave, Usuario usuario) throws BusinessException {
		return getRepository().findOpcaoUserByChave(chave, usuario);
	}
	
	@Override
	public List<OpcaoSistema> buscarOpcoesUserPorCasoUso(String casoDeUso, Usuario usuario) throws BusinessException {
		return getRepository().findOpcoesUserByCasoUso(casoDeUso, usuario);
	}
}