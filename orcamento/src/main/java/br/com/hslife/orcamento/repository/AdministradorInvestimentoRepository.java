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

import br.com.hslife.orcamento.entity.AdministradorInvestimento;
import br.com.hslife.orcamento.entity.Usuario;

@Repository
public class AdministradorInvestimentoRepository extends AbstractCRUDRepository<AdministradorInvestimento>{
	
	public AdministradorInvestimentoRepository() {
		super(new AdministradorInvestimento(), AdministradorInvestimento.class);
	}
	
	public List<AdministradorInvestimento> findAllByUsuario(Usuario usuario) {
		return getSession().createQuery("SELECT a FROM AdministradorInvestimento a WHERE a.usuario.id = :idUsuario ORDER BY a.nome ASC", AdministradorInvestimento.class)
				.setParameter("idUsuario", usuario.getId())
				.getResultList();
	}
	
	public List<AdministradorInvestimento> findAllByNomeAndUsuario(String nome, Usuario usuario) {
		return getSession().createQuery("SELECT a FROM AdministradorInvestimento a WHERE a.nome LIKE :nome AND a.usuario.id = :idUsuario ORDER BY a.nome ASC", AdministradorInvestimento.class)
				.setParameter("nome", "%" + nome.toLowerCase() + "%")
				.setParameter("idUsuario", usuario.getId())
				.getResultList();
	}
}
