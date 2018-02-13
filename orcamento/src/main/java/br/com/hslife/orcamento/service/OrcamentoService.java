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

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.DetalheOrcamento;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Orcamento;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.AbrangenciaOrcamento;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.facade.IOrcamento;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.model.ResumoMensalContas;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.OrcamentoRepository;
import br.com.hslife.orcamento.util.LancamentoContaUtil;

@Service("orcamentoService")
public class OrcamentoService extends AbstractCRUDService<Orcamento> implements IOrcamento {
	
	@Autowired
	private OrcamentoRepository repository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;

	public OrcamentoRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}
	
	public LancamentoContaRepository getLancamentoContaRepository() {
		this.lancamentoContaRepository.setSessionFactory(this.sessionFactory);
		return lancamentoContaRepository;
	}

	@Override
	public void cadastrar(Orcamento entity) {
		// Trata os IDs dos detalhes antes de prosseguir com o cadastro
		for (DetalheOrcamento detalhe : entity.getDetalhes()) {
			if (detalhe.isIdChanged()) detalhe.setId(null);
		}
		super.cadastrar(entity);
	}
	
	@Override
	public void alterar(Orcamento entity) {
		// Trata os IDs dos detalhes antes de prosseguir com o cadastro
		for (DetalheOrcamento detalhe : entity.getDetalhes()) {
			if (detalhe.isIdChanged()) detalhe.setId(null);
		}
		super.alterar(entity);
	}
	
	@Override
	public List<Orcamento> buscarTodosPorUsuario(Usuario usuario) {
		return getRepository().findAllByUsuario(usuario);
	}
	
	@Override
	public List<Orcamento> buscarTodosAtivosInativosPorUsuario(boolean ativo, Usuario usuario) {
		return getRepository().findAllEnableDisableByUsuario(ativo, usuario);
	}
	
	@Override
	public List<Orcamento> buscarAbrangeciaPorUsuario(AbrangenciaOrcamento abrangencia, Usuario usuario) {
		return getRepository().findAbrangenciaByUsuario(abrangencia, usuario);
	}
	
	@Override
	public void gerarOrcamento(Orcamento entity) {
		getRepository().save(entity.gerarOrcamento());
	}
	
	@Override
	public void atualizarValores(Orcamento entity) {
		// Busca todos os lançamentos no período indicado no orçamento
		CriterioBuscaLancamentoConta criterioBusca = new CriterioBuscaLancamentoConta();
		criterioBusca.setDataInicio(entity.getInicio());
		criterioBusca.setDataFim(entity.getFim());
		criterioBusca.setConta(entity.getConta());
		criterioBusca.setTipoConta(new TipoConta[]{entity.getTipoConta()});
		
		List<LancamentoConta> lancamentos = getLancamentoContaRepository().findByCriterioBusca(criterioBusca);
		
		// Organiza os lançamentos e calcula seu saldo
		ResumoMensalContas resumoMensal = new ResumoMensalContas();
		switch (entity.getAbrangenciaOrcamento()) {
			case CATEGORIA : 
				resumoMensal.setCategorias(LancamentoContaUtil.organizarLancamentosPorCategoria(lancamentos), 0, LancamentoContaUtil.calcularSaldoLancamentosComConversao(lancamentos));
				
				for (DetalheOrcamento detalhe : entity.getDetalhes()) {
					detalhe.setRealizado(0); // Apaga o valor registrado anteriormente
					for (Categoria c : resumoMensal.getCategorias()) {						
						if (c.getDescricao().equals(detalhe.getDescricao())) {
							detalhe.setRealizado(Math.abs(c.getSaldoPago()));
						}
					}
					
				}
				
				break;
			case FAVORECIDO : 
				resumoMensal.setFavorecidos(LancamentoContaUtil.organizarLancamentosPorFavorecido(lancamentos));
				
				for (DetalheOrcamento detalhe : entity.getDetalhes()) {
					detalhe.setRealizadoCredito(0); // Apaga o valor registrado anteriormente
					detalhe.setRealizadoDebito(0); // Apaga o valor registrado anteriormente
					for (Favorecido f : resumoMensal.getFavorecidos()) {						
						if (f.getNome().equals(detalhe.getDescricao())) {
							detalhe.setRealizadoCredito(Math.abs(f.getSaldoCredito()));
							detalhe.setRealizadoDebito(Math.abs(f.getSaldoDebito()));
						}
					}
					
				}
				
				break;
			case MEIOPAGAMENTO : 
				resumoMensal.setMeiosPagamento(LancamentoContaUtil.organizarLancamentosPorMeioPagamento(lancamentos));
				
				for (DetalheOrcamento detalhe : entity.getDetalhes()) {
					detalhe.setRealizadoCredito(0); // Apaga o valor registrado anteriormente
					detalhe.setRealizadoDebito(0); // Apaga o valor registrado anteriormente
					for (MeioPagamento m : resumoMensal.getMeiosPagamento()) {						
						if (m.getDescricao().equals(detalhe.getDescricao())) {
							detalhe.setRealizadoCredito(Math.abs(m.getSaldoCredito()));
							detalhe.setRealizadoDebito(Math.abs(m.getSaldoDebito()));
						}
					}
					
				}
								
				break;
		}
		
		// Persiste os dados na base
		getRepository().update(entity);
	}
	
	@Override
	public List<Orcamento> buscarAbrangenciaAtivosInativosPorUsuario(AbrangenciaOrcamento abrangencia, boolean ativo, Usuario usuario) {
		return getRepository().findAbrangenciaEnableDisableByUsuario(abrangencia, ativo, usuario);
	}
}
