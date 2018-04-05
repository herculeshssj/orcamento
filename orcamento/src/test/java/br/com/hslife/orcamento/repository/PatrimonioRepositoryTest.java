package br.com.hslife.orcamento.repository;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.hslife.orcamento.entity.Patrimonio;
import br.com.hslife.orcamento.enumeration.EntityPersistenceEnum;
import br.com.hslife.orcamento.mock.EntityPersistenceMock;

public class PatrimonioRepositoryTest extends AbstractTestRepositories {
	
	private Patrimonio entity;
	
	@Autowired
	private PatrimonioRepository repository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private FavorecidoRepository favorecidoRepository;
	
	@Autowired
	private MoedaRepository moedaRepository;
	
	@Autowired
	private CategoriaDocumentoRepository categoriaDocumentoRepository;

	@Before
	public void initializeEntities() {
		repository.setSessionFactory(getSessionFactory());
		usuarioRepository.setSessionFactory(getSessionFactory());
		favorecidoRepository.setSessionFactory(getSessionFactory());
		moedaRepository.setSessionFactory(getSessionFactory());
		categoriaDocumentoRepository.setSessionFactory(getSessionFactory());
		
		EntityPersistenceMock epm = new EntityPersistenceMock().criarUsuario().comFavorecido(true).comMoedaPadrao().ePossuiImovel();
		
		entity = (Patrimonio)epm.get(EntityPersistenceEnum.PATRIMONIO);
		
		usuarioRepository.save(entity.getUsuario());
		favorecidoRepository.save(entity.getFavorecido());
		moedaRepository.save(entity.getMoeda());
		categoriaDocumentoRepository.save(entity.getCategoriaDocumento());
		repository.save(entity);
	}
	
	@Test
	public void testFindAllByUsuario() {
		List<Patrimonio> listaPatrimonio = repository.findAllByUsuario(entity.getUsuario());
		assertEquals(1, listaPatrimonio.size());
	}

}
