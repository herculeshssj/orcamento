/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
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

package br.com.hslife.orcamento.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.RegraImportacao;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IRegraImportacao;
import br.com.hslife.orcamento.repository.RegraImportacaoRepository;

@Service("regraImportacaoService")
public class RegraImportacaoService extends AbstractCRUDService<RegraImportacao> implements IRegraImportacao {

	@Autowired
	private RegraImportacaoRepository repository;
	
	@Autowired
	private ICategoria categoriaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;
	
	public RegraImportacaoRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}
	
	public ICategoria getCategoriaService() {
		return categoriaService;
	}

	public IFavorecido getFavorecidoService() {
		return favorecidoService;
	}

	public IMeioPagamento getMeioPagamentoService() {
		return meioPagamentoService;
	}
	
	@Override
	public void validar(RegraImportacao entity) {
		RegraImportacao regra = getRepository().findEqualEntity(entity);
		if (regra != null && !regra.equals(entity)) {
			throw new BusinessException("Existe uma regra com os mesmo parâmetros informados!");
		}
	}

	@Override
	public List<RegraImportacao> buscarTodosPorConta(Conta conta) {
		return getRepository().findAllByConta(conta);
	}
	
	public LancamentoConta processarRegras(Conta conta, LancamentoConta lancamento) {
		List<LancamentoConta> lancamentos = new ArrayList<>();
		lancamentos.add(lancamento);
		return this.processarRegras(conta, lancamentos).get(0);
	}
	
	public List<LancamentoConta> processarRegras(Conta conta, List<LancamentoConta> lancamentos) {
		Set<LancamentoConta> lancamentosProcessados = new HashSet<LancamentoConta>();
		
		List<RegraImportacao> regras = this.buscarTodosPorConta(conta);
		// Não processa nada caso não tenha nenhuma regra cadastrada
		if (regras == null || regras.isEmpty()) {
			lancamentosProcessados.addAll(lancamentos);
			return new ArrayList<LancamentoConta>(lancamentosProcessados);
		}
		
		for (LancamentoConta lancamento : lancamentos) {
			
			for (RegraImportacao regra : regras) {
				//Pattern p = Pattern.compile(regra.getTexto());
		        //Matcher m = p.matcher(lancamento.getDescricao());
				boolean matchFound = lancamento.getDescricao().toUpperCase().contains(regra.getTexto().toUpperCase());
		        if (matchFound) {
		        	if (regra.getIdCategoria() != null) {
		        		lancamento.setCategoria(getCategoriaService().buscarPorID(regra.getIdCategoria()));
		        	}
		        	if (regra.getIdFavorecido() != null) {
		        		lancamento.setFavorecido(getFavorecidoService().buscarPorID(regra.getIdFavorecido()));
		        	}
		        	if (regra.getIdMeioPagamento() != null) {
		        		lancamento.setMeioPagamento(getMeioPagamentoService().buscarPorID(regra.getIdMeioPagamento()));
		        	}
		        	lancamentosProcessados.add(lancamento);
		        	break;
		        } else {
		            continue;
		        }				
			}
			
		}
		
		// Adiciona os lançamentos que não se enquadraram nas regras cadastradas
		for (LancamentoConta l : lancamentos) {
			if (!lancamentosProcessados.contains(l)) {
				lancamentosProcessados.add(l);
			}
		}
		
		return new ArrayList<LancamentoConta>(lancamentosProcessados);
	}
}