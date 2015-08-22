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

package br.com.hslife.orcamento.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.RegraImportacao;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.IncrementoClonagemLancamento;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoPessoa;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.ICategoria;
import br.com.hslife.orcamento.facade.IConta;
import br.com.hslife.orcamento.facade.IFavorecido;
import br.com.hslife.orcamento.facade.IMeioPagamento;
import br.com.hslife.orcamento.facade.IMoeda;
import br.com.hslife.orcamento.facade.IRegraImportacao;
import br.com.hslife.orcamento.facade.IUsuario;
import br.com.hslife.orcamento.util.EntityInitializerFactory;

public class RegraImportacaoComponentTest extends AbstractTestComponents {
	
	Conta conta = new Conta();
	Categoria categoria = new Categoria();
	Favorecido favorecido = new Favorecido();
	MeioPagamento meioPagamento = new MeioPagamento();
	
	@Autowired
	private RegraImportacaoComponent regraImportacaoComponent;
	
	@Autowired
	private IRegraImportacao regraImportacaoService;
	
	@Autowired
	private IUsuario usuarioService;
	
	@Autowired
	private IMoeda moedaService;
	
	@Autowired
	private IConta contaService;
	
	@Autowired
	private ICategoria categoriaService;
	
	@Autowired
	private IFavorecido favorecidoService;
	
	@Autowired
	private IMeioPagamento meioPagamentoService;

	@Before
	public void initializeTestEnvironment() throws BusinessException {
		Usuario usuario = EntityInitializerFactory.initializeUsuario();
		usuarioService.cadastrar(usuario);
		
		Moeda moeda = EntityInitializerFactory.initializeMoeda(usuario);
		moedaService.cadastrar(moeda);
		
		conta = EntityInitializerFactory.initializeConta(usuario, moeda);
		contaService.cadastrar(conta);
		
		categoriaService.cadastrar(EntityInitializerFactory.initializeCategoria(usuario, TipoCategoria.DEBITO, true));
		categoriaService.cadastrar(EntityInitializerFactory.initializeCategoria(usuario, TipoCategoria.CREDITO, true));
		categoriaService.cadastrar(EntityInitializerFactory.initializeCategoria(usuario, TipoCategoria.CREDITO, false));
		
		categoria = EntityInitializerFactory.initializeCategoria(usuario, TipoCategoria.DEBITO, false);
		categoriaService.cadastrar(categoria);
		
		favorecidoService.cadastrar(EntityInitializerFactory.initializeFavorecido(usuario, TipoPessoa.FISICA, true));
		
		favorecido = EntityInitializerFactory.initializeFavorecido(usuario, TipoPessoa.FISICA, false);
		favorecidoService.cadastrar(favorecido);
		
		meioPagamentoService.cadastrar(EntityInitializerFactory.initializeMeioPagamento(usuario, true));
		
		meioPagamento = EntityInitializerFactory.initializeMeioPagamento(usuario, false);
		meioPagamentoService.cadastrar(meioPagamento);
	}
		
	@Test
	public void testProcessarRegras() throws BusinessException {			
		List<LancamentoConta> lancamentosAProcessar = new ArrayList<LancamentoConta>();
		List<LancamentoConta> lancamentosProcessados = new ArrayList<LancamentoConta>();
		
		// Cadastra as regras que serão usadas
		for (int i=0; i<3; i++) {
			RegraImportacao r = EntityInitializerFactory.initializeRegraImportacao(conta);
			r.setTexto("Regra " + i);
			r.setIdCategoria(categoria.getId());
			r.setIdFavorecido(favorecido.getId());
			r.setIdMeioPagamento(meioPagamento.getId());
			regraImportacaoService.cadastrar(r);
		}
		
		// Gera os lançamentos a processar
		LancamentoConta lancamento = new LancamentoConta();
		lancamento.setDataPagamento(new Date());
		lancamento.setDescricao("Lançamento de Teste Regra 2");
		
		lancamentosAProcessar.addAll(lancamento.clonarLancamentos(3, IncrementoClonagemLancamento.DIA));
		
		lancamentosProcessados = regraImportacaoComponent.processarRegras(conta, lancamentosAProcessar);
		
		int i = 0;
		
		for (LancamentoConta processado : lancamentosProcessados) {
			for (LancamentoConta aProcessar : lancamentosAProcessar) {
				if (aProcessar.getDataPagamento().equals(processado.getDataPagamento())) {
					assertEquals(categoria.getId(), processado.getCategoria().getId());
					assertEquals(favorecido.getId(), processado.getFavorecido().getId());
					assertEquals(meioPagamento.getId(), processado.getMeioPagamento().getId());
				}
			}
			i++;
		}
		
		assertEquals(3, i);
	}
	
	@Test
	public void testProcessarRegrasSemRegrasCadastradas() throws BusinessException {
		List<LancamentoConta> lancamentosAProcessar = new ArrayList<LancamentoConta>();
		List<LancamentoConta> lancamentosProcessados = new ArrayList<LancamentoConta>();
		
		// Gera os lançamentos a processar
		LancamentoConta lancamento = new LancamentoConta();
		lancamento.setDataPagamento(new Date());
		lancamento.setDescricao("Lançamento de Teste Regra 2");
		
		lancamentosAProcessar.addAll(lancamento.clonarLancamentos(3, IncrementoClonagemLancamento.DIA));
		
		lancamentosProcessados = regraImportacaoComponent.processarRegras(conta, lancamentosAProcessar);
		
		for (LancamentoConta processado : lancamentosProcessados) {
			for (LancamentoConta aProcessar : lancamentosAProcessar) {
				if (aProcessar.getDataPagamento().equals(processado.getDataPagamento())) {
					assertNull(processado.getCategoria());
					assertNull(processado.getFavorecido());
					assertNull(processado.getMeioPagamento());
				}
			}
		}
		
	}	
}
