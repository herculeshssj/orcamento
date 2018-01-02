/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor
	
	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

    para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 - 
	
	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/

package br.com.hslife.orcamento.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import br.com.caelum.stella.format.CNPJFormatter;
import br.com.caelum.stella.format.CPFFormatter;
import br.com.caelum.stella.format.Formatter;

public class Util {
	
	/** Seção de constantes **/
	
	public static final String DATAHORA = "datahora";
	public static final String DATA = "data";
	public static final String HORA = "hora";
	public static final String DATABASE = "database";
	public static final String DATABASE_DATA = "database_data";
	
	public static final String[] meses = new String[]{"JANEIRO", "FEVEREIRO", "MARÇO", "ABRIL",
			"MAIO", "JUNHO", "JULHO", "AGOSTO", "SETEMBRO", "OUTUBRO", "NOVEMBRO", "DEZEMBRO"};
		
	private Util() {
		// Esta classe não pode ser instanciada
	}
	
	public static String MD5(String texto) {
        String sen = "";
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            BigInteger hash = new BigInteger(1, md.digest(texto.getBytes()));
            sen = hash.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }        
        return sen;
    }

    public static String SHA1(String texto) {
        String sen = "";
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            BigInteger hash = new BigInteger(1, md.digest(texto.getBytes()));
            sen = hash.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return sen;
    }
    
    public static String SHA256(String texto) {
        String sen = "";
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            BigInteger hash = new BigInteger(1, md.digest(texto.getBytes()));
            sen = hash.toString(16);
        } catch (NullPointerException e) {
        	e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return sen;
    }
    
	public static String moedaBrasil(double valor) {
		NumberFormat formatarMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
		return formatarMoeda.format(valor);
	}
    
	public static String formatarCPF(String cpf) {
		Formatter formatador = new CPFFormatter();
		return formatador.format(cpf);
	}
	
	public static String formatarCNPJ(String cnpj) {
		Formatter formatador = new CNPJFormatter();
		return formatador.format(cnpj);
	}
	
	public static String formataDataHora(Date dataHora, String opcao) {
		SimpleDateFormat formata = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		if (dataHora == null)
			return "";
		if (opcao.equals(Util.DATAHORA)) {
			formata = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		}
		if (opcao.equals(Util.DATA)) {
			formata = new SimpleDateFormat("dd/MM/yyyy");
		}
		if (opcao.equals(Util.HORA)) {
			formata = new SimpleDateFormat("HH:mm");
		}
		if (opcao.equals(Util.DATABASE)) {
			formata = new SimpleDateFormat("yyyy-MM-dd");
		}
		if (opcao.equals(Util.DATABASE_DATA)) {
			formata = new SimpleDateFormat("yyyy-MM-dd");
		}

		return formata.format(dataHora);
	}
    
    /*
     * Retorna o primeiro dia do mês anterior a partir da data atual
     */
    public static Date primeiroDiaMesAnterior() {
    	// Pega a data atual
    	Calendar dataAtual = Calendar.getInstance();
    	
    	// Seta para o mês anterior 
    	dataAtual.add(Calendar.MONTH, -1);
    	
    	// Seta para o primeiro dia do mês 
    	dataAtual.set(Calendar.DAY_OF_MONTH, 1);
    	
    	// Retorna o resultado
    	return dataAtual.getTime();
    }
    
    /*
     * Retorna o primeiro dia do mês anterior a partir da data atual
     */
    public static Date ultimoDiaMesAnterior() {
    	// Pega a data atual
    	Calendar dataAtual = Calendar.getInstance();
    	
    	// Seta para o primeiro dia do mês 
    	dataAtual.set(Calendar.DAY_OF_MONTH, 1);
    	
    	// "Rola" para o dia anterior do ano
    	dataAtual.add(Calendar.DAY_OF_YEAR, -1);
    	
    	// Retorna o resultado
    	return dataAtual.getTime();
    }
    
    /*
     * Retorna o último dia do mês atual
     */
    public static Date ultimoDiaMesAtual() {
    	// Pega a data atual
    	Calendar dataAtual = Calendar.getInstance();
    	
    	// Seta para primeiro dia do mês 
    	dataAtual.set(Calendar.DAY_OF_MONTH, 1);
    	
    	// "Rola" para o mês seguinte
    	dataAtual.add(Calendar.MONTH, 1);
    	
    	// "Rola" para o dia anterior do ano
    	dataAtual.add(Calendar.DAY_OF_YEAR, -1);
    	
    	// Retorna o resultado
    	return dataAtual.getTime();
    }
    
    /*
     * Retorna o primeiro dia do mês atual
     */
    public static Date primeiroDiaMesAtual() {
    	// Pega a data atual
    	Calendar dataAtual = Calendar.getInstance();
    	
    	// Seta para primeiro dia do mês 
    	dataAtual.set(Calendar.DAY_OF_MONTH, 1);
    	
    	// Retorna o resultado
    	return dataAtual.getTime();
    }
    
    /**
     * Remove todos os acentos da string passada como parâmetro
     * @param acentuada string a ter os acentos removidos
     * @return nova string sem os acentos
     */
    public static String removerAcentos(String acentuada) {
    	CharSequence cs = new StringBuilder(acentuada);
    	return Normalizer.normalize(cs, Normalizer.Form.NFKD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
    
    /*
     * Arredonda valores reais para duas casas decimais.
     */
    public static double arredondar(double valor) {
		BigDecimal valorAArredondar = new BigDecimal(valor);
		return valorAArredondar.setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }
    
    /*
     * Verifica se o e-mail informado é válido.
     */
    public static boolean validaEmail(String email) {
    	Pattern p = Pattern.compile("[a-zA-Z0-9]+[a-zA-Z0-9_.-]+@{1}[a-zA-Z0-9_.-]*\\.+[a-z]{2,4}");
    	Matcher m = p.matcher(email);
    	boolean matchFound = m.matches();
    	if (matchFound) {
    		return true;
    	} else {
    		return false;
    	}    	
    }  
    
    public static String montarString(String[] strings) {
    	StringBuilder messageBuilder = new StringBuilder();
		for (String s : strings) {
			messageBuilder.append(s);
			messageBuilder.append("\n\n");
		}
		return messageBuilder.toString();
    }
    
    public static Date primeiroDiaAno(int ano) {
    	// Pega a data atual
    	Calendar data = Calendar.getInstance();
    	
    	// Seta o ano informado na data atual
    	data.set(Calendar.YEAR, ano);
    	
    	// Seta o dia 01/01 na data atual
    	data.set(Calendar.DAY_OF_YEAR, 1);
    	
    	// Retorna a data
    	return data.getTime();
    }
    
    public static Date ultimoDiaAno(int ano) {
    	// Pega a data atual
    	Calendar data = Calendar.getInstance();
    	
    	// Seta o ano informado na data atual
    	data.set(Calendar.YEAR, ano);
    	
    	// Seta o dia 31/12 na data atual
    	data.set(Calendar.MONTH, Calendar.DECEMBER);
    	data.set(Calendar.DAY_OF_MONTH, 31);
    	
    	// Retorna a data
    	return data.getTime();
    }
    
    /**
     * Retorna um objeto Date representando o primeiro dia do mês informado
     * e para o ano informado.
     */
    public static Date primeiroDiaMes(int mes, int ano) {
    	// Pega a data atual
    	Calendar data = Calendar.getInstance();
    	
    	// Seta para o ano informado
    	data.set(Calendar.YEAR, ano);
    	
    	// Seta para primeiro dia do mês 
    	data.set(Calendar.DAY_OF_MONTH, 1);
    	
    	// Seta para o mês informado
    	data.set(Calendar.MONTH, mes);
    	
    	// Retorna o resultado
    	return data.getTime();
    }
    
    /**
     * Retorna um objeto Date representando o primeiro dia do mês e ano 
     * da data informada.
     */
    public static Date primeiroDiaMes(Date data) {
    	// Pega a data atual
    	Calendar dataTemp = Calendar.getInstance();
    	
    	// Seta a data informada na variável
    	dataTemp.setTime(data);
    	
    	// Seta para primeiro dia do mês 
    	dataTemp.set(Calendar.DAY_OF_MONTH, 1);
    	
    	// Retorna o resultado
    	return dataTemp.getTime();
    }
    
    public static Date ultimoDiaMes(int mes, int ano) {
    	// Pega a data atual
    	Calendar data = Calendar.getInstance();
    	
    	// Seta para o ano informado
    	data.set(Calendar.YEAR, ano);
    	
    	// Seta para o mês informado
    	data.set(Calendar.MONTH, mes + 1);
    	
    	// Seta para o primeiro dia do mês 
    	data.set(Calendar.DAY_OF_MONTH, 1);
    	
    	// "Rola" para o dia anterior do ano
    	data.add(Calendar.DAY_OF_YEAR, -1);
    	
    	// Retorna o resultado
    	return data.getTime();
    }
    
    /**
     * Suprime o texto de uma string até o limite especificado. A
     * string gerada recebe uma (...) no final do texto.
     */
    public static String suprimirTextoFim(String texto, int limite) {
    	String textoSuprimido = "";
    	
    	if (texto == null || texto.isEmpty()) {
    		return textoSuprimido;
    	}
    	
    	if (texto.length() > limite) {
    		textoSuprimido = texto.substring(0, limite - 5) + " ...";    		
    	} else {
    		return texto;
    	}
    	
    	return textoSuprimido;
    }
    
    public static String suprimirTextoMeio(String texto, int limite) {
    	String textoSuprimido = "";
    	
    	if (texto == null || texto.isEmpty()) {
    		return textoSuprimido;
    	}
    	
    	if (texto.length() > limite) {
    		// Para saber onde as reticências ficarão deve-se calcular a diferença entre o tamanho da String com o limite informado,
    		// somando +5 relativo ao texto da String
    		int diferenca = (texto.length() - limite) + 5;
    		
    		// Agora calcula-se o tamanho das Strings que ficarão à direita e à esquerda
    		int tamanhoString = Math.round(new BigDecimal((texto.length() - diferenca) / 2).floatValue());
    		
    		// Agora se gera a parte da esquerda da String suprimida acrescido das reticências
    		textoSuprimido = texto.substring(0, tamanhoString) + " ... ";
    		
    		// Agora se gera a parte da direita da String
    		textoSuprimido = textoSuprimido + texto.substring(texto.length() - tamanhoString);
    	} else {
    		return texto;
    	}
    	
    	return textoSuprimido;
    }
    
    // Verifica se o valor passado só possui números
    // Retorna true se o campo só for composto de números
    public static boolean onlyNumber(String value) {
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(value);
        boolean matchFound = m.matches();
        if (matchFound) {
            return true;
        } else {
            return false;
        }
    }
    
    // Verifica se a string informada é vazia
    // Retorna true se a string for vazia ou nula
    public static boolean eVazio(String obj) {
    	return obj == null || obj.trim().isEmpty();
    }
    
    // Calcula a quantidade de dias entre duas datas
    public static int quantidadeDias(Date inicio, Date fim) {
    	Calendar dataInicial = Calendar.getInstance();
    	dataInicial.setTime(inicio);
    	
    	Calendar dataFinal = Calendar.getInstance();
    	dataFinal.setTime(fim);
    	
    	int contador = 0;
    	while (dataInicial.before(dataFinal)) {
    		dataInicial.add(Calendar.DAY_OF_YEAR, 1);
    		contador++;
    	}
    	
    	return contador;
    }
    
    public static String gerarJson(Map<String, String> dados) {
    	if (dados != null && !dados.isEmpty()) {
    		JSONObject json = new JSONObject();
    		
    		for (String s : dados.keySet()) {
    			json.put(s, dados.get(s));
    		}
    		
    		return json.toString();
    	}
    	
    	return "{}";
	}
    
    public static String gerarJsonArray(Map<String, String[]> dados) {
    	if (dados != null && !dados.isEmpty()) {
    		JSONObject json = new JSONObject();
    		
    		for (String s : dados.keySet()) {
    			json.put(s, Arrays.toString(dados.get(s)));
    		}
    		
    		return json.toString();
    	}
    	
    	return "{}";
	}
    
    public static String tempoTranscorrido(Date inicio, Date fim) {
		return Util.tempoTranscorrido(inicio.getTime(), fim.getTime());
	}
	
    /**
     * Retorna as horas e minutos transcorridos entre as datas informadas.
     * 
     * @author eprioli
     * @param inicio
     * @param fim
     * @return
     */
	public static String tempoTranscorrido(long inicio, long fim) {
		long segundos = (fim - inicio) / 1000;		
		int dias = (int) Math.floor(segundos / 86400);
        segundos -= dias * 86400;
        int horas = (int) Math.floor(segundos / 3600);
        segundos -= horas * 3600;
        int minutos = (int) Math.floor(segundos / 60);
        segundos -= minutos * 60;
        return horas + "h " + minutos + "m";
	}
}