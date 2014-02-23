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

import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Endereco;
import br.com.hslife.orcamento.entity.Pessoal;
import br.com.hslife.orcamento.entity.Telefone;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IInformacaoPessoal;
import br.com.hslife.orcamento.repository.EnderecoRepository;
import br.com.hslife.orcamento.repository.PessoalRepository;
import br.com.hslife.orcamento.repository.TelefoneRepository;

@Service
public class InformacaoPessoalService implements IInformacaoPessoal {
	
	@Autowired
	private PessoalRepository pessoalRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private UsuarioComponent usuarioComponent;

	@Override
	public void salvarDadosPessoais(Pessoal pessoal) throws BusinessException {
		pessoal.validate();
		
		// Se a entidade não possuir ID, grava um novo registro, caso contrário atualiza
		if (pessoal.getId() == null) {
			pessoalRepository.save(pessoal);
		} else {
			pessoalRepository.update(pessoal);
		}
	}
	
	@Override
	public void salvarEnderecos(List<Endereco> enderecos, Usuario usuario) throws BusinessException {
		List<Endereco> enderecosUsuario = enderecoRepository.findByUsuario(usuario);
		enderecosUsuario.removeAll(enderecos);
		// Itera a listagem de endereços encontrados e que foram removidos pelo usuário
		for (Endereco e : enderecosUsuario) {
			enderecoRepository.delete(e);
		}
				
		// Valida cada endereço e depois salva
		for (Endereco endereco : enderecos) {
			endereco.validate();
			if (endereco.getId() == null)
				enderecoRepository.save(endereco);
			else
				enderecoRepository.update(endereco);
		}
	}

	@Override
	public void salvarTelefones(List<Telefone> telefones, Usuario usuario) throws BusinessException {
		List<Telefone> telefonesUsuario = telefoneRepository.findByUsuario(usuario);
		telefonesUsuario.removeAll(telefones);
		// Itera a listagem de telefones encontrados e que foram removidos pelo usuário
		for (Telefone t : telefonesUsuario) {
			telefoneRepository.delete(t);
		}
		
		// Valida cada telefone e depois salva
		for (Telefone telefone : telefones) {
			telefone.validate();
			if (telefone.getId() == null)
				telefoneRepository.save(telefone);
			else
				telefoneRepository.update(telefone);
		}
	}

	@Override
	public Pessoal buscarDadosPessoais(Usuario usuario) throws BusinessException {
		return pessoalRepository.findByUsuario(usuario);
	}

	@Override
	public List<Endereco> buscarEnderecos(Usuario usuario) throws BusinessException {
		return enderecoRepository.findByUsuario(usuario);
	}

	@Override
	public List<Telefone> buscarTelefones(Usuario usuario) throws BusinessException {
		return telefoneRepository.findByUsuario(usuario);
	}	
}