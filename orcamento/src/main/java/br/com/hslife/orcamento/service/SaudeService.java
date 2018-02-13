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

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth

	Floor, Boston, MA  02110-1301, USA.


	Para mais informações sobre o programa Orçamento Doméstico e seu autor

	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

	para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/package br.com.hslife.orcamento.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.HistoricoSaude;
import br.com.hslife.orcamento.entity.Saude;
import br.com.hslife.orcamento.entity.TratamentoSaude;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.facade.ISaude;
import br.com.hslife.orcamento.repository.HistoricoSaudeRepository;
import br.com.hslife.orcamento.repository.SaudeRepository;
import br.com.hslife.orcamento.repository.TratamentoSaudeRepository;

@Service
public class SaudeService extends AbstractCRUDService<Saude> implements ISaude {
	
	@Autowired
	private SaudeRepository repository;
	
	@Autowired
	private TratamentoSaudeRepository tratamentoSaudeRepository;
	
	@Autowired
	private HistoricoSaudeRepository historicoSaudeRepository;
	
	public SaudeRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return this.repository;
	}
	
	public TratamentoSaudeRepository getTratamentoSaudeRepository() {
		this.tratamentoSaudeRepository.setSessionFactory(sessionFactory);
		return tratamentoSaudeRepository;
	}

	public HistoricoSaudeRepository getHistoricoSaudeRepository() {
		this.historicoSaudeRepository.setSessionFactory(sessionFactory);
		return historicoSaudeRepository;
	}

	@Override
	public List<Saude> buscarTodosAtivosPorUsuario(Usuario usuario) {
		return getRepository().findAllEnableByUsuario(usuario);
	}
	
	@Override
	public void salvarTratamentoSaude(TratamentoSaude tratamento) {
		// Valida o tratamento
		tratamento.validate();
		
		// Salva o tratamento na base
		if (tratamento.getId() == null)
			getTratamentoSaudeRepository().save(tratamento);
		else
			getTratamentoSaudeRepository().update(tratamento);
		
		// Busca uma instância de saúde na base
		Saude saude = getRepository().findById(tratamento.getSaude().getId());
		
		// Remove a instância do tratamento caso exista
		saude.getTratamento().remove(tratamento);
		
		// Adiciona a instância nos tratamentos
		saude.getTratamento().add(tratamento);
		
		// Salva a instância de saúde
		this.alterar(saude);
	}
	
	@Override
	public void excluirTratamentoSaude(TratamentoSaude tratamento) {
		// Busca uma instância de saúde na base
		Saude saude = getRepository().findById(tratamento.getSaude().getId());
		
		// Busca o tratamento
		TratamentoSaude ts = getTratamentoSaudeRepository().findById(tratamento.getId());
		
		// Remove a instância do tratamento caso exista
		saude.getTratamento().remove(ts);
		
		// Remove o tratamento da base
		getTratamentoSaudeRepository().delete(ts);
		
		// Salva a instância de saúde
		this.alterar(saude);
	}
	
	@Override
	public void salvarHistoricoSaude(HistoricoSaude historico) {
		// Valida o histórico
		historico.validate();
		
		// Salva o histórico na base
		if (historico.getId() == null)
			getHistoricoSaudeRepository().save(historico);
		else
			getHistoricoSaudeRepository().update(historico);
		
		// Busca uma instância de saúde na base
		Saude saude = getRepository().findById(historico.getSaude().getId());
		
		// Remove a instância do histórico caso exista
		saude.getHistorico().remove(historico);
		
		// Adiciona a instância no histórico
		saude.getHistorico().add(historico);
		
		// Salva a instância de saúde
		this.alterar(saude);
	}
	
	@Override
	public void excluirHistoricoSaude(HistoricoSaude historico) {
		// Busca uma instância de saúde na base
		Saude saude = getRepository().findById(historico.getSaude().getId());
		
		// Busca o histórico
		HistoricoSaude hs = getHistoricoSaudeRepository().findById(historico.getId());
		
		// Remove a instância do histórico caso exista
		saude.getHistorico().remove(hs);
		
		// Remove o histórico da base
		getHistoricoSaudeRepository().delete(hs);
		
		// Salva a instância de saúde
		this.alterar(saude);
	}
}
