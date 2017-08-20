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

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.entity.Logs;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.facade.ILog;
import br.com.hslife.orcamento.model.CriterioLog;
import br.com.hslife.orcamento.repository.LogRepository;

@Service("logService")
@Transactional(propagation=Propagation.REQUIRED, rollbackFor={ApplicationException.class})
public class LogService implements ILog {
	
	@Autowired
	public SessionFactory sessionFactory;
	
	@Autowired
	private LogRepository repository;

	public LogRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}

	@Override
	public List<Logs> buscarPorCriterios(CriterioLog criterioBusca) {
		return getRepository().findByCriteriosLog(criterioBusca);
	}
	
	@Override
	public List<String> buscarTodosLoggers() {
		return getRepository().findAllLogger();
	}
	
	@Override
	public List<String> buscarTodosNiveis() {
		return getRepository().findAllLevel();
	}
	
	@Override
	public Logs buscarPorID(Long id) {
		return getRepository().findByID(id);
	}
	
	@Override
	public void excluir(Logs logs) {
		getRepository().delete(logs);
	}
	
	@Override
	public void alterar(Logs logs) {
		getRepository().update(logs);
	}
	
	@Override
	public Logs buscarExcecaoMaisRecente() {
		// Traz o log de exceção mais recente que não tenha sido enviado ainda para o Admininstrador do Sistema
		return getRepository().findMostRecentException();
	}
}