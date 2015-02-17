/***
  
  	Copyright (c) 2012 - 2015 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@gmail.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.util;

import java.util.Date;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Endereco;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.RegraImportacao;
import br.com.hslife.orcamento.entity.Telefone;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoUsuario;

public class EntityInitializerFactory {

	private EntityInitializerFactory() {
		// Classe não pode ser inicializada.
	}
	
	public static Usuario initializeUsuario() {
		Usuario usuario = new Usuario();
		usuario.setEmail("contato@hslife.com.br");
		usuario.setLogin("usuario_" + Util.formataDataHora(new Date(), Util.DATAHORA));
		usuario.setNome("Usuário de Teste - PessoalRepository");
		usuario.setSenha(Util.SHA1("teste"));
		usuario.setTipoUsuario(TipoUsuario.ROLE_USER);
		return usuario;
	}
	
	public static Endereco initializeEndereco(Usuario usuario) {
		Endereco endereco = new Endereco();
		endereco.setTipoLogradouro("Avenida");
		endereco.setLogradouro("Ministro Lafaeyte de Andrade");
		endereco.setNumero("1683");
		endereco.setComplemento("Bl. 3 Apt. 404");
		endereco.setBairro("Marco II");
		endereco.setCidade("Nova Iguaçu");
		endereco.setEstado("RJ");
		endereco.setCep("26261220");
		endereco.setDescricao("Residencial");
		endereco.setUsuario(usuario);
		return endereco;
	}
	
	public static Telefone initializeTelefone(Usuario usuario) {
		Telefone telefone = new Telefone();
		telefone.setDdd("21");
		telefone.setDescricao("Comercial");
		telefone.setNumero("32936010");
		telefone.setRamal("6010");
		telefone.setUsuario(usuario);
		return telefone;
	}
	
	public static Moeda initializeMoeda(Usuario usuario) {
		Moeda moeda = new Moeda();
		moeda.setAtivo(true);
		moeda.setCodigoMonetario("BRL");
		moeda.setNome("Real");
		moeda.setPadrao(true);
		moeda.setPais("Brasil");
		moeda.setSiglaPais("BR");
		moeda.setUsuario(usuario);
		moeda.setSimboloMonetario("R$");
		return moeda;
	}
	
	public static Conta initializeConta(Usuario usuario, Moeda moeda) {
		Conta conta = new Conta();
		conta.setDescricao("Conta de teste - Calendario de atividades");
		conta.setDataAbertura(new Date());
		conta.setSaldoInicial(100);
		conta.setTipoConta(TipoConta.CORRENTE);
		conta.setUsuario(usuario);
		conta.setMoeda(moeda);
		return conta;
	}
	
	public static RegraImportacao initializeRegraImportacao(Conta conta) {
		RegraImportacao regra = new RegraImportacao();
		regra.setTexto("teste");
		regra.setIdCategoria(1l);
		regra.setIdFavorecido(1l);
		regra.setIdMeioPagamento(1l);
		regra.setConta(conta);
		return regra;
	}
}
