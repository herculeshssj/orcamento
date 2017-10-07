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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Investimento;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoInvestimento;
import br.com.hslife.orcamento.facade.IInvestimento;
import br.com.hslife.orcamento.model.InfoCotacao;
import br.com.hslife.orcamento.repository.CategoriaInvestimentoRepository;
import br.com.hslife.orcamento.repository.ContaRepository;
import br.com.hslife.orcamento.repository.InvestimentoRepository;

@Service("investimentoService")
public class InvestimentoService extends AbstractCRUDService<Investimento> implements IInvestimento {
	
	@Autowired
	private InvestimentoRepository repository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private CategoriaInvestimentoRepository categoriaInvestimentoRepository;
	
	public InvestimentoRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return this.repository;
	}

	public ContaRepository getContaRepository() {
		this.contaRepository.setSessionFactory(this.sessionFactory);
		return contaRepository;
	}

	public CategoriaInvestimentoRepository getCategoriaInvestimentoRepository() {
		this.categoriaInvestimentoRepository.setSessionFactory(this.sessionFactory);
		return categoriaInvestimentoRepository;
	}

	@Override
	public List<Investimento> buscarPorConta(Conta conta) {
		return getRepository().findByConta(conta);
	}
	
	@Override
	public List<Conta> gerarCarteiraInvestimento(Usuario usuario) {
		// Instancia uma lista de contas investimento
		List<Conta> contasInvestimento = new ArrayList<>();
		
		// Traz todos os investimentos do usuário
		List<Investimento> investimentos = getRepository().findByUsuario(usuario);
		
		// Itera os investimentos para adicioná-los na lista de contas nas suas respectivas categorias
		for (Investimento investimento : investimentos) {
			
			// Verifica se o investimento está inativo
			if (!investimento.isAtivo()) {
				continue;
			}
			
			// Verifica se existe a conta está cadastrada
			int indexConta = contasInvestimento.indexOf(investimento.getConta());
			if (indexConta < 0) {
				contasInvestimento.add(investimento.getConta());
				indexConta = contasInvestimento.indexOf(investimento.getConta());
			}
			
			// Verifica se a categoria de investimento está cadastrada
			int indexCategoria = contasInvestimento.get(indexConta).getCategoriasInvestimento().indexOf(investimento.getCategoriaInvestimento());
			if (indexCategoria < 0) {
				contasInvestimento.get(indexConta).getCategoriasInvestimento().add(investimento.getCategoriaInvestimento());
				indexCategoria = contasInvestimento.get(indexConta).getCategoriasInvestimento().indexOf(investimento.getCategoriaInvestimento());
			}
			
			// Adiciona o investimento na categoria
			contasInvestimento.get(indexConta).getCategoriasInvestimento().get(indexCategoria).getInvestimentos().add(investimento);
			
			// Obtém a cotação dos investimentos de renda variável
			if (investimento.getCategoriaInvestimento().getTipoInvestimento().equals(TipoInvestimento.VARIAVEL)) {
				investimento.setInfoCotacao(new InfoCotacao(investimento.getTicker()));
			}
			
		}
			
		// Retorna a lista de contas populada
		return contasInvestimento;
	}
}