/***
  
  	Copyright (c) 2012 - 2016 Hércules S. S. José

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

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento ou escreva 
    
    para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor, 
    
    Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor entre  

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.facade;

import java.util.List;

import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoPessoa;
import br.com.hslife.orcamento.exception.ApplicationException;

public interface IFavorecido extends ICRUDService<Favorecido> {
	
	/**
	 * Buscar um favorecido a partir dos parâmetros informados.
	 * Caso não encontre, o favorecido padrão é retornado.
	 */
	public Favorecido buscarFavorecido(String nomeFavorecido, Usuario usuario) throws ApplicationException;
	
	public List<Favorecido> buscarPorUsuario(Usuario usuario) throws ApplicationException;
	
	public List<Favorecido> buscarPorNomeEUsuario(String nome, Usuario usuario) throws ApplicationException;
	
	public List<Favorecido> buscarAtivosPorUsuario(Usuario usuario) throws ApplicationException;
	
	public List<Favorecido> buscarPorNomeUsuarioEAtivo(String nome, Usuario usuario, boolean ativo) throws ApplicationException;
	
	public List<Favorecido> buscarTipoPessoaENomeEAtivoPorUsuario(TipoPessoa tipoPessoa, String nome, Boolean ativo, Usuario usuario) throws ApplicationException;
}
