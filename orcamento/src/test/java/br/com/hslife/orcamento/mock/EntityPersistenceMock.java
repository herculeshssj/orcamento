package br.com.hslife.orcamento.mock;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.CategoriaDocumento;
import br.com.hslife.orcamento.entity.Combustivel;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.ContaCompartilhada;
import br.com.hslife.orcamento.entity.EntityPersistence;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.entity.Meta;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Patrimonio;
import br.com.hslife.orcamento.entity.Pessoal;
import br.com.hslife.orcamento.entity.RelatorioColuna;
import br.com.hslife.orcamento.entity.RelatorioCustomizado;
import br.com.hslife.orcamento.entity.RelatorioParametro;
import br.com.hslife.orcamento.entity.Seguro;
import br.com.hslife.orcamento.entity.Telefone;
import br.com.hslife.orcamento.entity.UnidadeMedida;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.EntityPersistenceEnum;
import br.com.hslife.orcamento.enumeration.Periodicidade;
import br.com.hslife.orcamento.enumeration.PremioSeguro;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoDado;
import br.com.hslife.orcamento.enumeration.TipoPessoa;
import br.com.hslife.orcamento.enumeration.TipoSeguro;
import br.com.hslife.orcamento.util.Util;

public class EntityPersistenceMock {
	
	private Map<EntityPersistenceEnum, EntityPersistence> mapEntidade = new HashMap<>();
	
	public EntityPersistence get(EntityPersistenceEnum entidade) {
		return mapEntidade.get(entidade);
	}
	
	public EntityPersistenceMock criaCombustivel() {
		Combustivel combustivel = new Combustivel.Builder()
				.descricao("Combustível teste")
				.distribuidora("Distribuidora de teste")
				.build(); 
		
		mapEntidade.put(EntityPersistenceEnum.COMBUSTIVEL, combustivel);
		return this;
	}
	
	public EntityPersistenceMock criarUsuario() {
		// Popula a entidade
		Usuario usuario = new Usuario.Builder()
				.email(UtilsMock.mockEmail())
				.login(UtilsMock.mockString(10))
				.nome(UtilsMock.mockStringComEspaco(20))
				.senha(Util.SHA256(UtilsMock.mockString(10)))
				.tokenID(Util.SHA256(UtilsMock.mockString(50)))
				.build();
		
		// Salva no Map e retorna o mock
		mapEntidade.put(EntityPersistenceEnum.USUARIO, usuario);
		return this;
	}

	public EntityPersistenceMock criarUsuario(Usuario usuario) {
		// Salva no Map e retorna o mock
		mapEntidade.put(EntityPersistenceEnum.USUARIO, usuario);
		return this;
	}
	
	public EntityPersistenceMock comMoedaPadrao() {
		// Popula a entidade
		Moeda moeda = new Moeda.Builder()
				.ativo(true)
				.codigoMonetario("BRL")
				.nome("Real")
				.padrao(true)
				.pais("Brasil")
				.siglaPais("BR")
				.usuario((Usuario)this.get(EntityPersistenceEnum.USUARIO))
				.simboloMonetario("R$")
				.build();
		
		// Salva no Map e retorna o mock
		mapEntidade.put(EntityPersistenceEnum.MOEDA, moeda);
		return this;
	}
	
	public EntityPersistenceMock comFavorecido(boolean padrao) {
		// Popula a entidade
		Favorecido favorecido = new Favorecido();
		favorecido.setNome(UtilsMock.mockStringComEspaco(20));
		favorecido.setTipoPessoa(TipoPessoa.FISICA);
		favorecido.setPadrao(padrao);
		favorecido.setCpfCnpj(UtilsMock.mockCPF());
		favorecido.setUsuario((Usuario)this.get(EntityPersistenceEnum.USUARIO));
		
		// Salva no Map e retorna o mock
		mapEntidade.put(EntityPersistenceEnum.FAVORECIDO, favorecido);		
		return this;
	}
	
	public EntityPersistenceMock comMeioPagamento(boolean padrao) {
		// Popula a entidade
		MeioPagamento meioPagamento = new MeioPagamento();
		meioPagamento.setDescricao(UtilsMock.mockStringComEspaco(20));
		meioPagamento.setPadrao(padrao);
		meioPagamento.setUsuario((Usuario)this.get(EntityPersistenceEnum.USUARIO));
		
		// Salva no Map e retorna o mock
		mapEntidade.put(EntityPersistenceEnum.MEIOPAGAMENTO, meioPagamento);		
		return this;
	}
	
	public EntityPersistenceMock comContaCorrente() {
		Conta conta = new Conta();
		conta.setDescricao("Conta de teste");
		conta.setDataAbertura(new Date());
		conta.setSaldoInicial(100);
		conta.setTipoConta(TipoConta.CORRENTE);
		conta.setUsuario((Usuario)this.get(EntityPersistenceEnum.USUARIO));
		conta.setMoeda((Moeda)this.get(EntityPersistenceEnum.MOEDA));
		
		// Salva no Map e retorna o mock
		mapEntidade.put(EntityPersistenceEnum.CONTA, conta);
		return this;
	}
	
	public EntityPersistenceMock comArquivoEmMaos() {
		Arquivo arquivo = new Arquivo.Builder()
				.contentType("text/html")
				.dados("<html><body><h1>Hello World!</h1></body></html>".getBytes())
				.nomeArquivo(UtilsMock.mockString(10))
				.tamanho(8192l)
				.usuario((Usuario)this.get(EntityPersistenceEnum.USUARIO))
				.build();
		
		// Salva no Map e retorna o mock
		mapEntidade.put(EntityPersistenceEnum.ARQUIVO, arquivo);		
		return this;
	}
	
	public EntityPersistenceMock ePossuiImovel() {
		Patrimonio patrimonio = new Patrimonio.Builder()
				.categoriaDocumento(new CategoriaDocumento(UtilsMock.mockString(30), (Usuario)this.get(EntityPersistenceEnum.USUARIO)))
				.dataEntrada(Calendar.getInstance().getTime())
				.descricao(UtilsMock.mockString(30))
				.favorecido((Favorecido)this.get(EntityPersistenceEnum.FAVORECIDO))
				.meioPagamento((MeioPagamento)this.get(EntityPersistenceEnum.MEIOPAGAMENTO))
				.moeda((Moeda)this.get(EntityPersistenceEnum.MOEDA))
				.usuario((Usuario)this.get(EntityPersistenceEnum.USUARIO))
				.valorPatrimonio(UtilsMock.mockDouble())
				.detalheEntradaPatrimonio(UtilsMock.mockString(1000))
				.build();
				
		// Salva no Map e retorna o mock
		mapEntidade.put(EntityPersistenceEnum.PATRIMONIO, patrimonio);		
		return this;
	}
	
	public EntityPersistenceMock ePossuiSeguro() {
		Seguro seguro = new Seguro.Builder()
				.descricao("Seguro de teste")
				.dataAquisicao(Calendar.getInstance())
				.dataRenovacao(Calendar.getInstance())
				.cobertura("Cobertura do seguro de teste")
				.valorCobertura(10000.0)
				.valorSegura(1000.0)
				.observacao("Observações do seguro de teste")
				.tipoSeguro(TipoSeguro.TERCEIROS)
				.periodicidadeRenovacao(Periodicidade.ANUAL)
				.periodicidadePagamento(Periodicidade.ANUAL)
				.premioSeguro(PremioSeguro.FIXO)
				.build();
		
		// Seta a conta que será definida no lançamento periódico criado
		seguro.setConta((Conta)this.get(EntityPersistenceEnum.CONTA));
		
		mapEntidade.put(EntityPersistenceEnum.SEGURO, seguro);
		return this;
	}

	@Deprecated
	public static Meta mockMeta() {
		Meta meta = new Meta();
		meta.setUsuario(EntityPersistenceMock.mockUsuario());
		meta.setDescricao(UtilsMock.mockString(50));
		meta.setMoeda(EntityPersistenceMock.mockMoeda(meta.getUsuario()));
		return meta;
	}
	
	/*** A partir deste ponto, estão os mocks que foram implementados prontos para usar ***/
	@Deprecated
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
	@Deprecated
	public static CategoriaDocumento mockCategoriaDocumento() {
		CategoriaDocumento categoriaDocumento = new CategoriaDocumento();
		categoriaDocumento.setDescricao(UtilsMock.mockString(50));
		categoriaDocumento.setUsuario(EntityPersistenceMock.mockUsuario());
		return categoriaDocumento;
	}
	@Deprecated
	public static Usuario mockUsuario() {
		return new Usuario.Builder().email("contato@hslife.com.br")
				.login("usuario_" + Util.formataDataHora(new Date(), Util.DATAHORA))
				.nome("Usuário de Teste - " + Util.formataDataHora(new Date(), Util.DATAHORA))
				.senha(Util.SHA256("teste")).tokenID(Util.SHA256(new Date().toString())).build();
	}
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	
	@Deprecated
	public static UnidadeMedida mockUnidadeMedida(Usuario usuario) {
		return new UnidadeMedida.Builder().descricao("Unidade de Medida de teste").sigla("UMT").usuario(usuario)
				.build();
	}
	@Deprecated
	public static Telefone mockTelefone(Usuario usuario) {
		return new Telefone.Builder().descricao("Telefone de teste").ddd("021").numero("1234-5678").ramal("901")
				.usuario(usuario).build();
	}
	@Deprecated
	public static Pessoal mockPessoal(Usuario usuario) {
		return new Pessoal.Builder().dataNascimento(Calendar.getInstance().getTime()).escolaridade("Superior")
				.estadoCivil("Casado").etnia("Afrobrasileira").filiacaoMae("Mãe do usuário")
				.filiacaoPai("Pai do usuário").genero('M').nacionalidade("Brasileira").naturalidade("Rio de Janeiro")
				.tipoSanguineo("O+").usuario(usuario).build();
	}
	@Deprecated
	public static ContaCompartilhada mockContaCompartilhada(Conta conta, Usuario usuario) {
		ContaCompartilhada contaCompartilhada = new ContaCompartilhada();
		contaCompartilhada.setConta(conta);
		contaCompartilhada.setUsuario(usuario);
		return contaCompartilhada;
	}
}