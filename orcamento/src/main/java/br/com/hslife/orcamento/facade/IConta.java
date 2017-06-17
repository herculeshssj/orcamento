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

package br.com.hslife.orcamento.facade;

import java.util.List;

import br.com.hslife.orcamento.entity.CartaoCredito;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoConta;

public interface IConta extends ICRUDService<Conta> {
	
	public List<Conta> buscarTodos();
	
	public List<Conta> buscarPorDescricao(String descricao);
	
	public List<Conta> buscarTodosAtivos();
	
	public List<Conta> buscarTodosAtivosPorUsuario(Usuario usuario);
	
	public List<Conta> buscarPorUsuario(Usuario usuario);
	
	public List<Conta> buscarAtivosPorUsuario(Usuario usuario);
	
	public void ativarConta(Conta conta);
	
	public void desativarConta(Conta conta, String situacaoLancamentos, boolean fecharPeriodo);
	
	public List<Conta> buscarPorDescricaoEUsuario(String descricao, Usuario usuario);
	
	public List<Conta> buscarPorTipoContaEUsuario(TipoConta tipoConta, Usuario usuario);
	
	public List<Conta> buscarSomenteTipoCartaoPorUsuario(Usuario usuario);
	
	public List<Conta> buscarSomenteTipoCartaoAtivosPorUsuario(Usuario usuario);
	
	public Conta buscarPorCartaoCredito(CartaoCredito cartao);
	
	public List<Conta> buscarDescricaoOuTipoContaOuAtivoPorUsuario(String descricao, TipoConta[] tipoConta, Usuario usuario, Boolean ativo);
	
}