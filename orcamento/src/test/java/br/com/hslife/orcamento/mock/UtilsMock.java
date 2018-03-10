package br.com.hslife.orcamento.mock;

import java.util.Random;

public class UtilsMock {

	/*
	 * Gera uma string com o tamanho informado
	 */
	public static String mockString(int tamanho) {
		String value = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
		if (tamanho > 0) {
			StringBuilder stringMocked = new StringBuilder();
			for (int i = 0; i < tamanho; i++) {
				stringMocked.append(value.charAt(new Random().nextInt(value.length())));
			}
			return stringMocked.toString();
		}
		return "";
	}
	
	/*
	 * Gera uma string com o tamanho informado contendo espaço
	 */
	public static String mockStringComEspaco(int tamanho) {
		String value = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz ";
		if (tamanho > 0) {
			StringBuilder stringMocked = new StringBuilder();
			for (int i = 0; i < tamanho; i++) {
				stringMocked.append(value.charAt(new Random().nextInt(value.length())));
			}
			return stringMocked.toString();
		}
		return "";
	}
	
	/*
	 * Gera um novo e-mail válido
	 */
	public static String mockEmail() {
		return UtilsMock.mockString(8) + "@" + UtilsMock.mockString(8) + "." + UtilsMock.mockString(3);
	}
}