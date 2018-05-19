package br.com.hslife.orcamento.util;

import br.com.hslife.loteria.configuration.LoteriaProperties;
import br.com.hslife.loteria.model.CidadeUF;
import br.com.hslife.loteria.model.Lotomania;
import br.com.hslife.loteria.repository.CidadeUFRepository;
import br.com.hslife.loteria.repository.LotomaniaRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Classe que realiza o download do arquivo Zip dos resultados da Lotomania
 * e grava as informações na base de dados.
 * 
 * @author herculeshssj
 *
 */
@Component
@Transactional
public class ExtratorLotomania {
	
	private static final Logger log = LoggerFactory.getLogger(ExtratorLotomania.class);

	@Autowired
	private LoteriaProperties properties;
	
	@Autowired
	private LotomaniaRepository repository;
	
	@Autowired
	private CidadeUFRepository cidadeUfRepository;

	public String execute() {

		String result = null;

		try {
			// Declaração de variáveis
			StringBuilder htmlContent = new StringBuilder();

			// Faz o download do arquivo Zip com os resultados da Lotomania
			URL url = new URL(properties.getUrlLotomania());

			CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL)); // gerenciamento
																						// dos
																						// cookies

			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(); // abre
																						// a
																						// conexão

			// Seta os atributos do request
			urlConnection.addRequestProperty("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Safari/604.1.38");
			urlConnection.addRequestProperty("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			urlConnection.addRequestProperty("Accept-Language", "pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3");
			urlConnection.addRequestProperty("Accept-Encoding", "gzip, deflate");
			urlConnection.addRequestProperty("Referer",
					"http://loterias.caixa.gov.br/wps/portal/loterias/landing/lotomania/");
			urlConnection.addRequestProperty("Connection", "keep-alive");

			// Tratando o redirect - Fonte:
			// http://www.mkyong.com/java/java-httpurlconnection-follow-redirect-example/
			boolean redirect = false;
			// normalmente, 3xx is redirect
			int status = urlConnection.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
						|| status == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
			}

			// Caso haja redirect, faz uma nova requisição para a nova
			// localização
			if (redirect) {

				// Pega o redirect URL fornecida no campo 'location' do
				// cabeçalho de resposta
				String newUrl = urlConnection.getHeaderField("Location");

				// Pega o cookie, por via das dúvidas
				String cookies = urlConnection.getHeaderField("Set-Cookie");

				// Abre novamente a conexão
				urlConnection = (HttpURLConnection) new URL(newUrl).openConnection();
				urlConnection.setRequestProperty("Cookie", cookies);
				urlConnection.addRequestProperty("User-Agent",
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Safari/604.1.38");

			}

			if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream stream = urlConnection.getInputStream();
				Files.deleteIfExists(Paths.get("temp_lotomania.zip"));
				FileOutputStream outputStream = new FileOutputStream("temp_lotomania.zip");

				int bytesRead = -1;
				byte[] buffer = new byte[4096];
				while ((bytesRead = stream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}

				stream.close();
				outputStream.close();

				ZipFile zipFile = new ZipFile("temp_lotomania.zip");

				for (Enumeration<? extends ZipEntry> filesInZip = zipFile.entries(); filesInZip.hasMoreElements();) {
					ZipEntry fileInZip = filesInZip.nextElement();

					if (fileInZip.getName().toLowerCase().endsWith("htm")
							|| fileInZip.getName().toLowerCase().endsWith("html")) {

						Scanner leituraArquivo = new Scanner(zipFile.getInputStream(fileInZip), "ISO-8859-1");
						while (leituraArquivo.hasNext()) {
							htmlContent.append(leituraArquivo.nextLine());
						}
						leituraArquivo.close();
					}
				}
				
				zipFile.close();
			}

			// Lê o documento HTML
			Document htmlDocument = Jsoup.parse(htmlContent.toString());

			// Itera a tabela existente no arquivo e armazena as informações
			// no List
			for (Element table : htmlDocument.getElementsByTag("table")) {
				Lotomania loteria = new Lotomania();
				boolean vaiProcessarCidades = false;
				int quantCidades = 0;
				int i = 0;
				for (Element row : table.getElementsByTag("tr")) {
					Elements cols = row.getElementsByTag("td");

					// Pula a primeira linha
					if (cols.isEmpty())
						continue;
					
					if (vaiProcessarCidades) {
						loteria.getCidadeUF().add(new CidadeUF(cols.get(0).text(), cols.get(1).text()));
						i++;
						if (i > quantCidades) {
							vaiProcessarCidades = false;
							quantCidades = 0;
							i = 0;
						} else
							continue;
					} else {
						loteria.setConcurso(Integer.valueOf(cols.get(0).text()));

						loteria.setDataSorteio(converteData(cols.get(1).text()));
						
						loteria.setBola1(Integer.valueOf(cols.get(2).text()));
						loteria.setBola2(Integer.valueOf(cols.get(3).text()));
						loteria.setBola3(Integer.valueOf(cols.get(4).text()));
						loteria.setBola4(Integer.valueOf(cols.get(5).text()));
						loteria.setBola5(Integer.valueOf(cols.get(6).text()));
						loteria.setBola6(Integer.valueOf(cols.get(7).text()));
						loteria.setBola7(Integer.valueOf(cols.get(8).text()));
						loteria.setBola8(Integer.valueOf(cols.get(9).text()));
						loteria.setBola9(Integer.valueOf(cols.get(10).text()));
						loteria.setBola10(Integer.valueOf(cols.get(11).text()));
						loteria.setBola11(Integer.valueOf(cols.get(12).text()));
						loteria.setBola12(Integer.valueOf(cols.get(13).text()));
						loteria.setBola13(Integer.valueOf(cols.get(14).text()));
						loteria.setBola14(Integer.valueOf(cols.get(15).text()));
						loteria.setBola15(Integer.valueOf(cols.get(16).text()));
						loteria.setBola16(Integer.valueOf(cols.get(17).text()));
						loteria.setBola17(Integer.valueOf(cols.get(18).text()));
						loteria.setBola18(Integer.valueOf(cols.get(19).text()));
						loteria.setBola19(Integer.valueOf(cols.get(20).text()));
						loteria.setBola20(Integer.valueOf(cols.get(21).text()));
						
						loteria.setArrecadacaoTotal(converteNumeroReal(cols.get(22).text()));
						
						loteria.setGanhadores20Numeros(Integer.valueOf(cols.get(23).text()));
						loteria.setGanhadores19Numeros(Integer.valueOf(cols.get(26).text()));
						loteria.setGanhadores18Numeros(Integer.valueOf(cols.get(27).text()));
						loteria.setGanhadores17Numeros(Integer.valueOf(cols.get(28).text()));
						loteria.setGanhadores16Numeros(Integer.valueOf(cols.get(29).text()));
						loteria.setGanhadoresNenhumNumero(Integer.valueOf(cols.get(30).text()));
						
						loteria.setValorRateio20Numeros(converteNumeroReal(cols.get(31).text()));
						loteria.setValorRateio19Numeros(converteNumeroReal(cols.get(32).text()));
						loteria.setValorRateio18Numeros(converteNumeroReal(cols.get(33).text()));
						loteria.setValorRateio17Numeros(converteNumeroReal(cols.get(34).text()));
						loteria.setValorRateio16Numeros(converteNumeroReal(cols.get(35).text()));
						loteria.setValorRateioNenhumNumero(converteNumeroReal(cols.get(36).text()));
						
						loteria.setAcumulado20Numeros(converteNumeroReal(cols.get(37).text()));
						loteria.setAcumulado19Numeros(converteNumeroReal(cols.get(38).text()));
						loteria.setAcumulado18Numeros(converteNumeroReal(cols.get(39).text()));
						loteria.setAcumulado17Numeros(converteNumeroReal(cols.get(40).text()));
						loteria.setAcumulado16Numeros(converteNumeroReal(cols.get(41).text()));
						loteria.setAcumuladoNenhumNumero(converteNumeroReal(cols.get(42).text()));
						loteria.setEstimativaPremio(converteNumeroReal(cols.get(43).text()));
						loteria.setValorAcumuladoEspecial(converteNumeroReal(cols.get(44).text()));
						
						if (loteria.getGanhadores20Numeros() > 0) {
							quantCidades = loteria.getGanhadores20Numeros();
							vaiProcessarCidades = true;
							loteria.getCidadeUF().add(new CidadeUF(cols.get(24).text(), cols.get(25).text()));
							i++;
							continue;
						} else {
							loteria.getCidadeUF().add(new CidadeUF(cols.get(24).text(), cols.get(25).text()));
						}
					}
					
					if (repository.findFirstByConcurso(loteria.getConcurso()) == null) {
						// Busca as instâncias de Cidade e UF para atribuir a coleção
						loteria.setCidadeUF(setarCidadeUF(loteria.getCidadeUF()));
						repository.save(loteria);
						log.info("Concurso " + loteria.getConcurso() + " salvo");
					}
					loteria = new Lotomania();
				}
			}

			result = "Resultados obtidos com sucesso!";

		} catch (Exception e) {
			e.printStackTrace();
			result = "Falha ao recuperar os resultados: " + e.getMessage();
		}

		return result;
	}

	private double converteNumeroReal(String numero) {
		if (numero == null || numero.trim().isEmpty()) {
			return 0.0;
		} else {
			return Double.valueOf(numero.replace(".", "").replace(",", "."));
		}
	}

	private Date converteData(String data) {
		// Separa a data
		String[] dataSeparada = data.split("/");
		
		// Instancia uma nova data 
		Calendar novaData = Calendar.getInstance();
		
		// Seta dia, mês e ano na nova data
		novaData.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dataSeparada[0]));
		novaData.set(Calendar.MONTH, Integer.valueOf(dataSeparada[1]) - 1);
		novaData.set(Calendar.YEAR, Integer.valueOf(dataSeparada[2]));
		
		// Retorna a nova data
		return novaData.getTime();
	}
	
	private List<CidadeUF> setarCidadeUF(List<CidadeUF> cidades) {
		List<CidadeUF> listaCidades = new ArrayList<>();
		
		// Itera a lista de cidades recebidas
		for (CidadeUF cidadeUF : cidades) {
			
			// Busca a cidade e o UF
			CidadeUF cidade = cidadeUfRepository.findFirstByCidadeAndUfAllIgnoreCase(cidadeUF.getCidade(), cidadeUF.getUf());
			
			// Caso a cidade exista, adiciona na listagem
			if (cidade != null) {
				listaCidades.add(cidade);
			} else {
				// Caso contrário, cria uma nova instância e adiciona na lista
				listaCidades.add(new CidadeUF(cidadeUF.getCidade().trim(), cidadeUF.getUf().trim()));
			}
		}
		
		return listaCidades;
	}
}
