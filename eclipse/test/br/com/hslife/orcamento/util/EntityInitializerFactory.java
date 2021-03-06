/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

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

package br.com.hslife.orcamento.util;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import br.com.hslife.orcamento.entity.Banco;
import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.ContaCompartilhada;
import br.com.hslife.orcamento.entity.DividaTerceiro;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.Investimento;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.ModeloDocumento;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.PagamentoDividaTerceiro;
import br.com.hslife.orcamento.entity.Pessoal;
import br.com.hslife.orcamento.entity.RegraImportacao;
import br.com.hslife.orcamento.entity.RelatorioColuna;
import br.com.hslife.orcamento.entity.RelatorioCustomizado;
import br.com.hslife.orcamento.entity.RelatorioParametro;
import br.com.hslife.orcamento.entity.Telefone;
import br.com.hslife.orcamento.entity.UnidadeMedida;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoDado;
import br.com.hslife.orcamento.enumeration.TipoInvestimento;
import br.com.hslife.orcamento.enumeration.TipoLancamento;
import br.com.hslife.orcamento.enumeration.TipoPessoa;
import br.com.hslife.orcamento.enumeration.TipoUsuario;

public class EntityInitializerFactory {

	private EntityInitializerFactory() {
		// Classe não pode ser inicializada.
	}
	
	public static Banco createBanco(Usuario usuario) {
		return new Banco.Builder()
				.ativo(true)
				.nome("Banco de teste")
				.numero("000")
				.padrao(false)
				.usuario(usuario)
				.build();
	}
	
	public static Usuario createUsuario() {
		return new Usuario.Builder()
			.email("contato@hslife.com.br")
			.login("usuario_" + Util.formataDataHora(new Date(), Util.DATAHORA))
			.nome("Usuário de Teste - " + Util.formataDataHora(new Date(), Util.DATAHORA))
			.senha(Util.SHA256("teste"))
			.tokenID(Util.SHA256(new Date().toString()))
			.build();
	}
	
	public static UnidadeMedida createUnidadeMedida(Usuario usuario) {
		return new UnidadeMedida.Builder()
				.descricao("Unidade de Medida de teste")
				.sigla("UMT")
				.usuario(usuario)
				.build();
	}
	
	public static Telefone createTelefone(Usuario usuario) {
		return new Telefone.Builder()
				.descricao("Telefone de teste")
				.ddd("021")
				.numero("1234-5678")
				.ramal("901")
				.usuario(usuario)
				.build();
	}
	
	public static RegraImportacao createRegraImportacao(Conta conta, String texto) {
		return new RegraImportacao.Builder()
				.texto(texto)
				.idCategoria(1l)
				.idFavorecido(1l)
				.idMeioPagamento(1l)
				.conta(conta)
				.build();
	}
	
	@SuppressWarnings("deprecation")
	public static Pessoal createPessoal(Usuario usuario) {
		return new Pessoal.Builder()
				.dataNascimento(new Date(1980-1900, 1, 1))
				.escolaridade("Superior")
				.estadoCivil("Casado")
				.etnia("Afrobrasileira")
				.filiacaoMae("Mãe do usuário")
				.filiacaoPai("Pai do usuário")
				.genero('M')
				.nacionalidade("Brasileira")
				.naturalidade("Rio de Janeiro")
				.tipoSanguineo("O+")
				.usuario(usuario)
				.build();
	}
	
	// Cria uma nova instância de RelatorioCustomizado
	public static RelatorioCustomizado createRelatorioCustomizado(Usuario usuario) {
		RelatorioCustomizado entity = new RelatorioCustomizado();
		entity.setNome("Relatório de teste");
		entity.setDescricao("Relatório customizado para testes");
		entity.setConsultaSQL("SELECT * FROM lancamentoconta");
		entity.setUsuario(usuario);
		
		SortedSet<RelatorioColuna> colunas = new TreeSet<>();
		for (int i = 0; i < 3; i++) {
			RelatorioColuna coluna = new RelatorioColuna();
			coluna.setNomeColuna("coluna" + i);
			coluna.setTextoExibicao("Coluna " + i);
			coluna.setTipoDado(TipoDado.STRING);
			colunas.add(coluna);
		}
		entity.setColunasRelatorio(colunas);
		
		Set<RelatorioParametro> parametros = new LinkedHashSet<>();
		for (int i = 0; i < 3; i++) {
			RelatorioParametro parametro = new RelatorioParametro();
			parametro.setNomeParametro("parametro" + i);
			parametro.setTextoExibicao("Parâmetro " + i);
			parametro.setTipoDado(TipoDado.STRING);
			parametros.add(parametro);
		}
		entity.setParametrosRelatorio(parametros);
		
		return entity;
	}
	
	public static RelatorioCustomizado createRelatorioCustomizado(Usuario usuario, String consultaSQL, SortedSet<RelatorioColuna> colunas, Set<RelatorioParametro> parametros) {
		RelatorioCustomizado entity = new RelatorioCustomizado();
		entity.setNome("Relatório de teste");
		entity.setDescricao("Relatório customizado para testes");
		entity.setConsultaSQL(consultaSQL);
		entity.setUsuario(usuario);
		entity.setColunasRelatorio(colunas);
		entity.setParametrosRelatorio(parametros);
		return entity;
	}
	
	public static Conta createConta(Usuario usuario, Moeda moeda) {
		Conta conta = new Conta();
		conta.setDescricao("Conta de teste");
		conta.setDataAbertura(new Date());
		conta.setSaldoInicial(100);
		conta.setTipoConta(TipoConta.CORRENTE);
		conta.setUsuario(usuario);
		conta.setMoeda(moeda);
		return conta;
	}
	
	public static Moeda createMoeda(Usuario usuario) {
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
	
	public static ContaCompartilhada createContaCompartilhada(Conta conta, Usuario usuario) {
		ContaCompartilhada contaCompartilhada = new ContaCompartilhada();
		contaCompartilhada.setConta(conta);
		contaCompartilhada.setUsuario(usuario);
		return contaCompartilhada;
	}
	
	@Deprecated
	public static Usuario initializeUsuario() {
		Usuario usuario = new Usuario();
		usuario.setEmail("contato@hslife.com.br");
		usuario.setLogin("usuario_" + Util.formataDataHora(new Date(), Util.DATAHORA));
		usuario.setNome("Usuário de Teste - PessoalRepository");
		usuario.setSenha(Util.SHA1("teste"));
		usuario.setTipoUsuario(TipoUsuario.ROLE_USER);
		return usuario;
	}
	
	@Deprecated
	public static Telefone initializeTelefone(Usuario usuario) {
		Telefone telefone = new Telefone();
		telefone.setDdd("21");
		telefone.setDescricao("Comercial");
		telefone.setNumero("32936010");
		telefone.setRamal("6010");
		telefone.setUsuario(usuario);
		return telefone;
	}
	
	@Deprecated
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
	
	@Deprecated
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
	
	@Deprecated
	public static RegraImportacao initializeRegraImportacao(Conta conta) {
		RegraImportacao regra = new RegraImportacao();
		regra.setTexto("teste");
		regra.setIdCategoria(1l);
		regra.setIdFavorecido(1l);
		regra.setIdMeioPagamento(1l);
		regra.setConta(conta);
		return regra;
	}
	
	public static Categoria initializeCategoria(Usuario usuario, TipoCategoria tipoCategoria, boolean padrao) {
		Categoria categoria = new Categoria();
		categoria.setAtivo(true);
		categoria.setPadrao(padrao);
		categoria.setTipoCategoria(tipoCategoria);
		categoria.setDescricao("Categoria de teste - " + tipoCategoria);
		categoria.setUsuario(usuario);
		return categoria;
	}
	
	public static Favorecido initializeFavorecido(Usuario usuario, TipoPessoa tipoPessoa, boolean padrao) {
		Favorecido favorecido = new Favorecido();
		favorecido.setAtivo(true);
		favorecido.setNome("Favorecido de teste - " + tipoPessoa);
		favorecido.setPadrao(padrao);
		favorecido.setTipoPessoa(tipoPessoa);
		favorecido.setUsuario(usuario);
		return favorecido;
	}
	
	public static MeioPagamento initializeMeioPagamento(Usuario usuario, boolean padrao) {
		MeioPagamento meioPagamento = new MeioPagamento();
		meioPagamento.setAtivo(true);
		meioPagamento.setDescricao("Meio de pagamento de teste");
		meioPagamento.setPadrao(padrao);
		meioPagamento.setUsuario(usuario);
		return meioPagamento;
	}
	
	public static ModeloDocumento initializeModeloDocumento(Usuario usuario) {
		ModeloDocumento modelo = new ModeloDocumento();
		modelo.setDescricao("Modelo de documento de teste");
		modelo.setConteudo("Conteúdo do modelo de documento de teste");
		modelo.setUsuario(usuario);
		return modelo;
	}
	
	public static DividaTerceiro initializeDividaTerceiro(Usuario usuario, Favorecido favorecido, Moeda moeda) {
		DividaTerceiro divida = new DividaTerceiro();		
		divida.setDataNegociacao(new Date());
		divida.setFavorecido(favorecido);
		divida.setJustificativa("Justificativa da dívida de teste");
		divida.setTermoDivida("Termo da dívida de teste");
		divida.setTermoQuitacao("Termo de quitação da dívida de teste");
		divida.setTipoCategoria(TipoCategoria.CREDITO);
		divida.setUsuario(usuario);
		divida.setValorDivida(1000);
		divida.setMoeda(moeda);
		
		PagamentoDividaTerceiro pagamento;
		for (int i = 0; i < 3; i++) {
			pagamento = new PagamentoDividaTerceiro();
			pagamento.setComprovantePagamento("Comprovante de pagamento da dívida de teste " + i);
			pagamento.setDataPagamento(new Date());
			pagamento.setDividaTerceiro(divida);
			pagamento.setValorPago(100);
			divida.getPagamentos().add(pagamento);
		}
		
		return divida;
	}
	
	public static Investimento createInvestimento(Banco banco, Usuario usuario) {
		Investimento investimento = new Investimento();
		
		investimento.setBanco(banco);
		investimento.setCnpj("84202625000126");
		investimento.setDescricao("Investimento de teste");
		investimento.setTipoInvestimento(TipoInvestimento.FUNDO_INVESTIMENTO);
		investimento.setInicioInvestimento(new Date());
		investimento.setObservacao("Observação sobre o investimento de teste");
		investimento.setUsuario(usuario);
		
		investimento.criaResumoInvestimento(new Date());
		
		investimento.movimentarInvestimento(TipoLancamento.RECEITA, "Aplicação", new Date(), 100);
		investimento.movimentarInvestimento(TipoLancamento.RECEITA, "Rendimento", new Date(), 50);
		investimento.movimentarInvestimento(TipoLancamento.DESPESA, "Resgate", new Date(), 30);
		
		return investimento;
	}
}
