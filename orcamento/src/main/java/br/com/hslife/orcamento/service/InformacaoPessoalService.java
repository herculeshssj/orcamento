/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a qualquer
    
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

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.entity.Endereco;
import br.com.hslife.orcamento.entity.Pessoal;
import br.com.hslife.orcamento.entity.Telefone;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.facade.IInformacaoPessoal;
import br.com.hslife.orcamento.repository.EnderecoRepository;
import br.com.hslife.orcamento.repository.PessoalRepository;
import br.com.hslife.orcamento.repository.TelefoneRepository;

@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor={ApplicationException.class})
public class InformacaoPessoalService implements IInformacaoPessoal {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private PessoalRepository pessoalRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;

	public PessoalRepository getPessoalRepository() {
		this.pessoalRepository.setSessionFactory(this.sessionFactory);
		return pessoalRepository;
	}

	public EnderecoRepository getEnderecoRepository() {
		this.enderecoRepository.setSessionFactory(this.sessionFactory);
		return enderecoRepository;
	}

	public TelefoneRepository getTelefoneRepository() {
		this.telefoneRepository.setSessionFactory(this.sessionFactory);
		return telefoneRepository;
	}

	@Override
	public void salvarDadosPessoais(Pessoal pessoal) {
		pessoal.validate();
		
		// Se a entidade não possuir ID, grava um novo registro, caso contrário atualiza
		if (pessoal.getId() == null) {
			getPessoalRepository().save(pessoal);
		} else {
			getPessoalRepository().update(pessoal);
		}
	}
	
	@Override
	public Pessoal buscarDadosPessoais(Usuario usuario) {
		return getPessoalRepository().findByUsuario(usuario);
	}

	@Override
	public List<Endereco> buscarEnderecos(Usuario usuario) {
		return getEnderecoRepository().findByUsuario(usuario);
	}

	@Override
	public List<Telefone> buscarTelefones(Usuario usuario) {
		return getTelefoneRepository().findByUsuario(usuario);
	}

	@Override
	public void salvarEndereco(Endereco entity) {
		entity.validate();
		if (entity.getId() == null) {
			getEnderecoRepository().save(entity);
		} else {
			getEnderecoRepository().update(entity);
		}
	}

	@Override
	public void excluirEndereco(Endereco entity) {
		getEnderecoRepository().delete(entity);		
	}

	@Override
	public void salvarTelefone(Telefone entity) {
		entity.validate();
		if (entity.getId() == null) {
			getTelefoneRepository().save(entity);
		} else {
			getTelefoneRepository().update(entity);
		}
	}

	@Override
	public void excluirTelefone(Telefone entity) {
		getTelefoneRepository().delete(entity);		
	}	
}