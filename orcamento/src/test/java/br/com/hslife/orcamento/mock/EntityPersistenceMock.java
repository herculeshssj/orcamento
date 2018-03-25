package br.com.hslife.orcamento.mock;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import br.com.hslife.orcamento.entity.CategoriaDocumento;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.ContaCompartilhada;
import br.com.hslife.orcamento.entity.Meta;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Pessoal;
import br.com.hslife.orcamento.entity.RelatorioColuna;
import br.com.hslife.orcamento.entity.RelatorioCustomizado;
import br.com.hslife.orcamento.entity.RelatorioParametro;
import br.com.hslife.orcamento.entity.Telefone;
import br.com.hslife.orcamento.entity.UnidadeMedida;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoDado;
import br.com.hslife.orcamento.util.Util;

public class EntityPersistenceMock {

	public static Meta mockMeta() {
		Meta meta = new Meta();
		meta.setUsuario(EntityPersistenceMock.mockUsuario());
		meta.setDescricao(UtilsMock.mockString(50));
		meta.setMoeda(EntityPersistenceMock.mockMoeda(meta.getUsuario()));
		return meta;
	}
	
	/*** A partir deste ponto, estão os mocks que foram implementados prontos para usar ***/
	
	public static Moeda mockMoeda(Usuario usuario) {
		Moeda moeda = new Moeda();
		moeda.setAtivo(true);
		moeda.setCodigoMonetario("BRL");
		moeda.setNome("Real");
		moeda.setPadrao(true);
		moeda.setPais("Brasil");
		moeda.setSiglaPais("BR");
		moeda.setUsuario(usuario == null ? EntityPersistenceMock.mockUsuario() : usuario);
		moeda.setSimboloMonetario("R$");
		return moeda;
	}
	
	public static CategoriaDocumento mockCategoriaDocumento() {
		CategoriaDocumento categoriaDocumento = new CategoriaDocumento();
		categoriaDocumento.setDescricao(UtilsMock.mockString(50));
		categoriaDocumento.setUsuario(EntityPersistenceMock.mockUsuario());
		return categoriaDocumento;
	}
	
	public static Usuario mockUsuario() {
		return new Usuario.Builder().email("contato@hslife.com.br")
				.login("usuario_" + Util.formataDataHora(new Date(), Util.DATAHORA))
				.nome("Usuário de Teste - " + Util.formataDataHora(new Date(), Util.DATAHORA))
				.senha(Util.SHA256("teste")).tokenID(Util.SHA256(new Date().toString())).build();
	}

	public static Conta mockConta() {
		Conta conta = new Conta();
		conta.setDescricao("Conta de teste");
		conta.setDataAbertura(new Date());
		conta.setSaldoInicial(100);
		conta.setTipoConta(TipoConta.CORRENTE);
		conta.setUsuario(EntityPersistenceMock.mockUsuario());
		conta.setMoeda(EntityPersistenceMock.mockMoeda(conta.getUsuario()));
		return conta;
	}
	
	public static Conta mockConta(Usuario usuario, Moeda moeda) {
		Conta conta = new Conta();
		conta.setDescricao("Conta de teste");
		conta.setDataAbertura(new Date());
		conta.setSaldoInicial(100);
		conta.setTipoConta(TipoConta.CORRENTE);
		conta.setUsuario(usuario);
		conta.setMoeda(moeda);
		return conta;
	}
	
	public static RelatorioCustomizado mockRelatorioCustomizado(Usuario usuario, String consultaSQL,
			SortedSet<RelatorioColuna> colunas, Set<RelatorioParametro> parametros) {
		RelatorioCustomizado entity = new RelatorioCustomizado();
		entity.setNome("Relatório de teste");
		entity.setDescricao("Relatório customizado para testes");
		entity.setConsultaSQL(consultaSQL);
		entity.setUsuario(usuario);
		entity.setColunasRelatorio(colunas);
		entity.setParametrosRelatorio(parametros);
		return entity;
	}
	
	// Cria uma nova instância de RelatorioCustomizado
	public static RelatorioCustomizado mockRelatorioCustomizado(Usuario usuario) {
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
	
	public static UnidadeMedida mockUnidadeMedida(Usuario usuario) {
		return new UnidadeMedida.Builder().descricao("Unidade de Medida de teste").sigla("UMT").usuario(usuario)
				.build();
	}
	
	public static Telefone mockTelefone(Usuario usuario) {
		return new Telefone.Builder().descricao("Telefone de teste").ddd("021").numero("1234-5678").ramal("901")
				.usuario(usuario).build();
	}
	
	public static Pessoal mockPessoal(Usuario usuario) {
		return new Pessoal.Builder().dataNascimento(Calendar.getInstance().getTime()).escolaridade("Superior")
				.estadoCivil("Casado").etnia("Afrobrasileira").filiacaoMae("Mãe do usuário")
				.filiacaoPai("Pai do usuário").genero('M').nacionalidade("Brasileira").naturalidade("Rio de Janeiro")
				.tipoSanguineo("O+").usuario(usuario).build();
	}
	
	public static ContaCompartilhada mockContaCompartilhada(Conta conta, Usuario usuario) {
		ContaCompartilhada contaCompartilhada = new ContaCompartilhada();
		contaCompartilhada.setConta(conta);
		contaCompartilhada.setUsuario(usuario);
		return contaCompartilhada;
	}
}