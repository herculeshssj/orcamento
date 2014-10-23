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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.component.ContaComponent;
import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.DetalheOrcamento;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.Orcamento;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IOrcamento;
import br.com.hslife.orcamento.model.CriterioLancamentoConta;
import br.com.hslife.orcamento.model.ResumoMensalContas;
import br.com.hslife.orcamento.repository.CategoriaRepository;
import br.com.hslife.orcamento.repository.FavorecidoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.MeioPagamentoRepository;
import br.com.hslife.orcamento.repository.MoedaRepository;
import br.com.hslife.orcamento.repository.OrcamentoRepository;

@Service("orcamentoService")
public class OrcamentoService extends AbstractCRUDService<Orcamento> implements IOrcamento {
	
	@Autowired
	private OrcamentoRepository repository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private FavorecidoRepository favorecidoRepository;
	
	@Autowired
	private MeioPagamentoRepository meioPagamentoRepository;
	
	@Autowired
	private MoedaRepository moedaRepository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private ContaComponent contaComponent;

	public OrcamentoRepository getRepository() {
		return repository;
	}

	public void setRepository(OrcamentoRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public void cadastrar(Orcamento entity) throws BusinessException {
		// Trata os IDs dos detalhes antes de prosseguir com o cadastro
		for (DetalheOrcamento detalhe : entity.getDetalhes()) {
			if (detalhe.isIdChanged()) detalhe.setId(null);
		}
		super.cadastrar(entity);
	}
	
	@Override
	public void alterar(Orcamento entity) throws BusinessException {
		// Trata os IDs dos detalhes antes de prosseguir com o cadastro
		for (DetalheOrcamento detalhe : entity.getDetalhes()) {
			if (detalhe.isIdChanged()) detalhe.setId(null);
		}
		super.alterar(entity);
	}
	
	@Override
	public List<Orcamento> buscarTodosPorUsuario(Usuario usuario) throws BusinessException {
		return getRepository().findAllByUsuario(usuario);
	}
	
	@Override
	public void atualizarValores(Orcamento entity) throws BusinessException {
		// Busca todos os lançamentos no período indicado no orçamento
		CriterioLancamentoConta criterioBusca = new CriterioLancamentoConta();
		criterioBusca.setDataInicio(entity.getInicio());
		criterioBusca.setDataFim(entity.getFim());
		
		List<LancamentoConta> lancamentos = lancamentoContaRepository.findByCriterioLancamentoConta(criterioBusca);
		
		// Organiza os lançamentos e calcula seu saldo
		ResumoMensalContas resumoMensal = new ResumoMensalContas();
		switch (entity.getAbrangenciaOrcamento()) {
			case CATEGORIA : 
				resumoMensal.setCategorias(contaComponent.organizarLancamentosPorCategoria(lancamentos), 0, contaComponent.calcularSaldoLancamentos(lancamentos));
				
				for (DetalheOrcamento detalhe : entity.getDetalhes()) {
					
					for (Categoria c : resumoMensal.getCategorias()) {
						if (c.getDescricao().equals(detalhe.getDescricao())) {
							detalhe.setRealizado(Math.abs(c.getSaldoPago()));
						}
					}
					
				}
				
				break;
			case FAVORECIDO : 
				resumoMensal.setFavorecidos(contaComponent.organizarLancamentosPorFavorecido(lancamentos), 0, contaComponent.calcularSaldoLancamentos(lancamentos));
				break;
			case MEIOPAGAMENTO : 
				resumoMensal.setMeiosPagamento(contaComponent.organizarLancamentosPorMeioPagamento(lancamentos), 0, contaComponent.calcularSaldoLancamentos(lancamentos));
				break;
		}
		
		// Persiste os dados na base
		getRepository().update(entity);
	}
}