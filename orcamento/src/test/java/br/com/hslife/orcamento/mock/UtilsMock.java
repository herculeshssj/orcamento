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
	
	/*
	 * Calculo o dígito verificador do CPF
	 * Fonte: http://www.guj.com.br/t/gerador-cpf/75653/6
	 */
	private static String calcDigVerif(String num) {  
        Integer primDig, segDig;  
        int soma = 0, peso = 10;  
        for (int i = 0; i < num.length(); i++)  
                soma += Integer.parseInt(num.substring(i, i + 1)) * peso--;  
        if (soma % 11 == 0 | soma % 11 == 1)  
            primDig = new Integer(0);  
        else  
            primDig = new Integer(11 - (soma % 11));  
        soma = 0;  
        peso = 11;  
        for (int i = 0; i < num.length(); i++)  
                soma += Integer.parseInt(num.substring(i, i + 1)) * peso--;  
        soma += primDig.intValue() * 2;  
        if (soma % 11 == 0 | soma % 11 == 1)  
            segDig = new Integer(0);  
        else  
            segDig = new Integer(11 - (soma % 11));  
        return primDig.toString() + segDig.toString();  
    } 
	
	/*
	 * Gera um número de CPF.
	 * Fonte: http://www.guj.com.br/t/gerador-cpf/75653/6 
	 */
    public static String mockCPF() {  
        String iniciais = "";  
        Integer numero;  
        for (int i = 0; i < 9; i++) {  
            numero = new Integer((int) (Math.random() * 10));  
            iniciais += numero.toString();  
        }  
        return iniciais + calcDigVerif(iniciais);  
    }
}