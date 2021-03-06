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

para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor,

Boston, MA  02110-1301, USA.


Para mais informações sobre o programa Orçamento Doméstico e seu autor

entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.ContaCompartilhada;
import br.com.hslife.orcamento.entity.Usuario;

@Repository
public class ContaCompartilhadaRepository extends AbstractCRUDRepository<ContaCompartilhada> {
	
	public ContaCompartilhadaRepository() {
		super(new ContaCompartilhada());
	}
	
	public ContaCompartilhada findByHash(String hash) {
		return (ContaCompartilhada)getSession().createQuery("FROM ContaCompartilhada c WHERE c.hashAutorizacao = :hash")
				.setString("hash", hash)
				.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<ContaCompartilhada> findAllByUsuarioLogado(Usuario usuarioLogado) {
		return getSession().createQuery("FROM ContaCompartilhada c WHERE c.conta.usuario.id = :idUsuario")
				.setLong("idUsuario", usuarioLogado.getId())
				.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ContaCompartilhada> findAllByUsuario(Usuario usuario) {
		return getSession().createQuery("FROM ContaCompartilhada c WHERE c.usuario.id = :idUsuario")
				.setLong("idUsuario", usuario.getId())
				.list();
	}
}
