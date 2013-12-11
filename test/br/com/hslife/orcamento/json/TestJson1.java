package br.com.hslife.orcamento.json;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoCategoria;


public class TestJson1 {

	//@Test
	public void main() throws JSONException { 
		
		/* ------------------------------------------------------- * TESTE 1 * cria um JSONObject para armazenar dados de um filme * -------------------------------------------------------*/ //instancia um novo JSONObject 
		JSONObject my_obj = new JSONObject(); //preenche o objeto com os campos: titulo, ano e genero 
		my_obj.put("titulo", "JSON x XML: a Batalha Final"); 
		my_obj.put("ano", 2012); 
		my_obj.put("genero", "Ação"); //serializa para uma string e imprime 
		String json_string = my_obj.toString(); 
		System.out.println("objeto original -> " + json_string); 
		System.out.println(); //altera o titulo e imprime a nova configuração do objeto 
		my_obj.put("titulo", "JSON x XML: o Confronto das Linguagens"); 
		json_string = my_obj.toString(); 
		System.out.println("objeto com o título modificado -> " + json_string); 
		System.out.println(); //recupera campo por campo com o método get() e imprime cada um 
		String titulo = my_obj.getString("titulo"); 
		Integer ano = my_obj.getInt("ano"); 
		String genero = my_obj.getString("genero"); 
		System.out.println("titulo: " + titulo); 
		System.out.println("ano: " + ano); 
		System.out.println("genero: " + genero);	 
		}
	
	@Test
	public void saveJsonValues() {
		Usuario usuario = new Usuario();
		usuario.setId(10l);
		
		Categoria categoria = new Categoria();
		categoria.setAtivo(true);
		categoria.setDescricao("Categoria de teste");
		categoria.setId(100l);
		categoria.setPadrao(false);
		categoria.setTipoCategoria(TipoCategoria.DEBITO);
		categoria.setUsuario(usuario);
		
		JSONObject json = new JSONObject();
		
		for (String s : categoria.getFieldValues().keySet()) {
			json.put(s, categoria.getFieldValues().get(s));
		} 
		
		System.out.println("JSON gerado ->  " + json.toString());
	}
	
	@Test
	public void readJsonValues() {
		Usuario usuario = new Usuario();
		usuario.setId(10l);
		
		Categoria categoria = new Categoria();
		categoria.setAtivo(true);
		categoria.setDescricao("Categoria de teste");
		categoria.setId(100l);
		categoria.setPadrao(false);
		categoria.setTipoCategoria(TipoCategoria.DEBITO);
		categoria.setUsuario(usuario);
		
		JSONObject json = new JSONObject();
		
		for (String s : categoria.getFieldValues().keySet()) {
			json.put(s, categoria.getFieldValues().get(s));
		}
		
			
		JSONObject jsonRead = new JSONObject(json.toString());
		
		System.out.println("JSON lido ->  " + jsonRead.toString());
		
		for (Object obj : jsonRead.keySet()) {
			System.out.println("Chave: " + obj + "; valor: " + jsonRead.get((String)obj));
		}
	}
}
