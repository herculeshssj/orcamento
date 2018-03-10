package br.com.hslife.orcamento.mock;

import java.util.Date;
import java.util.Random;

import br.com.hslife.orcamento.entity.CategoriaDocumento;
import br.com.hslife.orcamento.entity.Meta;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoUsuario;
import br.com.hslife.orcamento.util.Util;

public class EntityPersistenceMock {

	/*
	 * Gera uma nova entidade 'CategoriaDocumento'
	 */
	public static CategoriaDocumento mockCategoriaDocumento() {
		CategoriaDocumento categoriaDocumento = new CategoriaDocumento();
		categoriaDocumento.setDescricao(UtilsMock.mockString(50));
		categoriaDocumento.setUsuario(EntityPersistenceMock.mockUsuario());
		return categoriaDocumento;
	}
	
	/*
	 * Gera uma nova entidade 'Usuario'
	 */
	public static Usuario mockUsuario() {
		Usuario usuario = new Usuario();
		usuario.setAtivo(new Random().nextBoolean());
		usuario.setDataCriacao(new Date());
		usuario.setEmail(UtilsMock.mockEmail());
		usuario.setLogin(UtilsMock.mockString(8));
		usuario.setNome(UtilsMock.mockStringComEspaco(30));
		usuario.setSenha(Util.MD5(UtilsMock.mockString(8)));
		usuario.setTipoUsuario(TipoUsuario.ROLE_USER);
		return usuario;
	}

	public static Meta mockMeta() {
		Meta meta = new Meta();
		meta.setUsuario(EntityPersistenceMock.mockUsuario());
		meta.setDescricao(UtilsMock.mockString(50));
		return meta; // TODO gerar os valores falsos e criar o builder na entidade
	}
}