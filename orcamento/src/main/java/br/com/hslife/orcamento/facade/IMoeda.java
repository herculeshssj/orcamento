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
package br.com.hslife.orcamento.facade;

import java.util.List;

import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Usuario;

public interface IMoeda extends ICRUDService<Moeda>{
	
	List<Moeda> buscarPorNomeEUsuario(String nome, Usuario usuario);
	
	List<Moeda> buscarPorUsuario(Usuario usuario);
	
	Moeda buscarPadraoPorUsuario(Usuario usuario);
	
	List<Moeda> buscarPorNomeUsuarioEAtivo(String nome, Usuario usuario, boolean ativo);

	List<Moeda> buscarAtivosPorUsuario(Usuario usuario);
	
	List<String> buscarTodosCodigoMonetarioPorUsuario(Usuario usuario);
	
	Moeda buscarCodigoMonetarioPorUsuario(String codigoMonetario, Usuario usuario);
}
