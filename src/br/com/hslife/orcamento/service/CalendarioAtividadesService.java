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

package br.com.hslife.orcamento.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.Agenda;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICalendarioAtividades;
import br.com.hslife.orcamento.model.CriterioLancamentoConta;
import br.com.hslife.orcamento.repository.AgendaRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;

@Service("calendarioAtividadesService")
public class CalendarioAtividadesService extends AbstractCRUDService<Agenda> implements ICalendarioAtividades {

	@Autowired
	private AgendaRepository repository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	public AgendaRepository getRepository() {
		return repository;
	}

	public void setRepository(AgendaRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public List<Agenda> buscarAgendamentoLancamentosAgendados(Conta conta, Date dataInicio, Date dataFim) throws BusinessException {
		CriterioLancamentoConta criterioBusca = new CriterioLancamentoConta();
		List<Agenda> agendamentos = new ArrayList<>();
		Agenda agenda = new Agenda();
		criterioBusca.setConta(conta);
		criterioBusca.setDataInicio(dataInicio);
		criterioBusca.setDataFim(dataFim);
		criterioBusca.setAgendado(true);
		for (LancamentoConta lancamento : lancamentoContaRepository.findByCriterioLancamentoConta(criterioBusca)) {
			agenda.setDataInicio(lancamento.getDataPagamento());
			agenda.setDescricao(lancamento.getConta().getLabel() + " - " + lancamento.getDescricao());
			agenda.setLancamentoAgendado(lancamento);
			agenda.setUsuario(lancamento.getConta().getUsuario());
			agendamentos.add(agenda);
			agenda = new Agenda();
		}		
		return agendamentos;
	}

	@Override
	public void validar(Agenda entity) throws BusinessException {
		// TODO Auto-generated method stub
		
	}
	
	
}