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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.RegraImportacao;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.facade.IRegraImportacao;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class RegraImportacaoServiceTest extends AbstractTestServices {
	
	private RegraImportacao regra = new RegraImportacao();
	private List<RegraImportacao> listaRegra = new ArrayList<RegraImportacao>();
	private Conta conta = new Conta();
	
	@Autowired
	private IRegraImportacao regraImportacaoService;
	
	@Autowired
	private IUsuario usuarioService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private IConta contaService;
	
	@SuppressWarnings("deprecation")
	@Before
	public void initializeTestEnvironment() throws ApplicationException {
		Usuario usuario = EntityInitializerFactory.initializeUsuario();
		usuarioService.cadastrar(usuario);
		
		Moeda moeda = EntityInitializerFactory.initializeMoeda(usuario);
		moedaService.cadastrar(moeda);
		
		conta = EntityInitializerFactory.initializeConta(usuario, moeda);
		contaService.cadastrar(conta);
		
		regra = EntityInitializerFactory.initializeRegraImportacao(conta);
		
		for (int i=0; i<3; i++) {
			RegraImportacao r = EntityInitializerFactory.initializeRegraImportacao(conta);
			r.setTexto("Regra " + i);
			listaRegra.add(r);
		}
	}
	
	@Test
	public void testCadastrar() throws ApplicationException {
		// Realiza o cadastro da regra
		regraImportacaoService.cadastrar(regra);
		
		// Verifica se a regra foi cadastrada com sucesso
		assertNotNull(regra.getId());
	}
	
	@Test
	public void testAlterar() throws ApplicationException {
		// Realiza o cadastro da regra
		regraImportacaoService.cadastrar(regra);
		
		// Altera as informações da regra
		regra.setIdCategoria(10l);
		regra.setIdFavorecido(20l);
		regra.setIdMeioPagamento(30l);
		regra.setTexto("texto a pesquisar");
		
		// Altera a regra
		regraImportacaoService.alterar(regra);
		
		// Verifica se a regra foi alterada com sucesso
		RegraImportacao regraTest = regraImportacaoService.buscarPorID(regra.getId());
		
		assertEquals(regra.getTexto(), regraTest.getTexto());
		assertEquals(regra.getIdCategoria(), regraTest.getIdCategoria());
		assertEquals(regra.getIdFavorecido(), regraTest.getIdFavorecido());
		assertEquals(regra.getIdMeioPagamento(), regraTest.getIdMeioPagamento());
	}
	
	@Test
	public void testExcluir() throws ApplicationException {
		// Realiza o cadastro da regra
		regraImportacaoService.cadastrar(regra);
		
		// Exclui a regra
		regraImportacaoService.excluir(regra);
		
		// Verifica se a regra foi excluída
		RegraImportacao regraTest = regraImportacaoService.buscarPorID(regra.getId());
		assertNull(regraTest);
	}
	
	@Test
	public void testBuscarPorID() throws ApplicationException {
		// Realiza o cadastro da regra
		regraImportacaoService.cadastrar(regra);
		
		// Verifica se a regra existe na base
		RegraImportacao regraTest = regraImportacaoService.buscarPorID(regra.getId());
		
		assertNotNull(regraTest);
	}
	
	@SuppressWarnings("deprecation")
	@Test(expected=BusinessException.class)
	public void testValidar() throws ApplicationException {
		// Verifica se a entidade está consistente para ser persistida
		regraImportacaoService.validar(regra);
		
		// Cadastra a regra
		regraImportacaoService.cadastrar(regra);
		
		RegraImportacao regraTest = EntityInitializerFactory.initializeRegraImportacao(conta);
		regraImportacaoService.validar(regraTest);
	}
	
	@Test
	public void testBuscarTodosPorConta() throws ApplicationException {
		Conta conta = null;
		// Salva as regras
		for (int i=0; i<3; i++) {
			conta = listaRegra.get(i).getConta();
			regraImportacaoService.cadastrar(listaRegra.get(i));
		}
		
		// Verifica se a lista obtida é igual a lista enviada para gravar
		List<RegraImportacao> listaRegraTest = regraImportacaoService.buscarTodosPorConta(conta);
		
		for (int i=0; i<3; i++) {
			if (listaRegraTest.contains(listaRegra.get(i))) {
				for (RegraImportacao r : listaRegraTest) {
					if (r.equals(listaRegra.get(i))) {
						assertEquals(listaRegra.get(i).getConta(), r.getConta());
						assertEquals(listaRegra.get(i).getTexto(), r.getTexto());
						assertEquals(listaRegra.get(i).getIdCategoria(), r.getIdCategoria());
						assertEquals(listaRegra.get(i).getIdFavorecido(), r.getIdFavorecido());
						assertEquals(listaRegra.get(i).getIdMeioPagamento(), r.getIdMeioPagamento());
					}
				}
			}
		}
	}
}