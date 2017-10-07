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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.CategoriaInvestimento;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Investimento;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.facade.IInvestimento;
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
	public Set<Conta> gerarCarteiraInvestimento(Usuario usuario) {
		// Instancia uma lista de contas investimentos que será populada a partir dos investimentos do usuário
		Set<Conta> contasInvestimento = new HashSet<>();
		
		// Traz todos os investimentos ativos do usuário
		List<Investimento> investimentos = getRepository().findByUsuario(usuario);
		
		// Traz todas as categorias de investimento
		List<CategoriaInvestimento> categoriasInvestimento = getCategoriaInvestimentoRepository().findAll();
		
		// Itera todos os investimentos existentes e popula a lista de contas
		for (Investimento i : investimentos) {
			contasInvestimento.add(i.getConta());
		}
		
		// Adiciona as categorias de investimentos para cada conta
		for (Conta c : contasInvestimento) {
			c.setCategoriasInvestimento(new HashSet<>(categoriasInvestimento));
		}
		
		// Itera todos os investimentos colocando-os nas respectivas contas e categorias
		for (Investimento investimento : investimentos) {
			for (Conta conta : contasInvestimento) {
				if (conta.equals(investimento.getConta())) {
					for (CategoriaInvestimento categoria : conta.getCategoriasInvestimento()) {
						if (conta.equals(investimento.getConta()) && categoria.equals(investimento.getCategoriaInvestimento())) {
							categoria.getInvestimentos().add(investimento);
						}
					}
				}
			}
		}
		
		// TODO continuar
		
		// Retorna a lista de contas populada
		return contasInvestimento;
	}
}