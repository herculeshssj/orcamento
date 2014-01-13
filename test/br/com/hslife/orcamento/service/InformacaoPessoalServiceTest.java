package br.com.hslife.orcamento.service;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Pessoal;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoUsuario;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IInformacaoPessoal;
import br.com.hslife.orcamento.util.Util;

public class InformacaoPessoalServiceTest extends AbstractTestServices {
	
	private Usuario usuario = new Usuario();
	private Pessoal pessoal = new Pessoal();
	
	@Autowired
	private IInformacaoPessoal informacaoPessoalService;
	
	@Autowired
	private UsuarioService usuarioService;
	
	public void setInformacaoPessoalService(
			IInformacaoPessoal informacaoPessoalService) {
		this.informacaoPessoalService = informacaoPessoalService;
	}

	@SuppressWarnings("deprecation")
	@Before
	public void initializeEntities() throws BusinessException {
		// Cria um novo usuário
		usuario.setEmail("contato@hslife.com.br");
		usuario.setLogin("usuario_" + Util.formataDataHora(new Date(), Util.DATAHORA));
		usuario.setNome("Usuário de Teste - PessoalRepository");
		usuario.setSenha(Util.SHA1("teste"));
		usuario.setTipoUsuario(TipoUsuario.ROLE_USER);
		usuarioService.cadastrar(usuario);
		
		// Cria uma nova informação pessoal
		pessoal.setUsuario(usuario);
		pessoal.setDataNascimento(new Date(1980, 5, 15));
		pessoal.setEscolaridade("Superior completo");
		pessoal.setEstadoCivil("Solteiro");
		pessoal.setEtnia("Parda");
		pessoal.setFiliacaoMae("Mãe de teste");
		pessoal.setFiliacaoPai("Pai de teste");
		pessoal.setNacionalidade("Brasileira");
		pessoal.setNaturalidade("Rio de Janeiro");
	}

	public void testCadastrar() throws BusinessException {
		informacaoPessoalService.cadastrar(pessoal);
		
		Assert.assertNotNull(pessoal.getId());
	}
	
	@Test
	public void testBuscarPorUsuario() throws BusinessException {
		informacaoPessoalService.cadastrar(pessoal);
		
		// Testa o método em questão
		Pessoal pessoalTest = informacaoPessoalService.buscarPorUsuario(usuario);
		Assert.assertEquals(pessoal.getId(), pessoalTest.getId());
	}
}
