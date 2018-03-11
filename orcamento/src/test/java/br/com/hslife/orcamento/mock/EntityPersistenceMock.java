package br.com.hslife.orcamento.mock;

import java.util.Date;

import br.com.hslife.orcamento.entity.CategoriaDocumento;
import br.com.hslife.orcamento.entity.Meta;
import br.com.hslife.orcamento.entity.Moeda;
import br.com.hslife.orcamento.entity.Usuario;
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
}