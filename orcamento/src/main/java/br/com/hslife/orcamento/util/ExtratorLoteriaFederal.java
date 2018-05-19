package br.com.hslife.orcamento.util;

import br.com.hslife.loteria.configuration.LoteriaProperties;
import br.com.hslife.loteria.model.LoteriaFederal;
import br.com.hslife.loteria.repository.LoteriaFederalRepository;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Classe que realiza o download do arquivo Zip dos resultados da Loteria
 * Federal e grava as informações na base de dados.
 * 
 * @author herculeshssj
 *
 */
@Component
@Transactional
public class ExtratorLoteriaFederal {
	
	private static final Logger log = LoggerFactory.getLogger(ExtratorLoteriaFederal.class);

	@Autowired
	private LoteriaProperties properties;
	
	@Autowired
	private LoteriaFederalRepository repository;

	public String execute() {

		String result = null;

		try {
			// Declaração de variáveis
			StringBuilder htmlContent = new StringBuilder();

			// Faz o download do arquivo Zip com os resultados da Loteria
			// Federal
			URL url = new URL(properties.getUrlFederal());

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
					"http://loterias.caixa.gov.br/wps/portal/loterias/landing/federal");
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
				Files.deleteIfExists(Paths.get("temp_federal.zip"));
				FileOutputStream outputStream = new FileOutputStream("temp_federal.zip");

				int bytesRead = -1;
				byte[] buffer = new byte[4096];
				while ((bytesRead = stream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}

				stream.close();
				outputStream.close();

				ZipFile zipFile = new ZipFile("temp_federal.zip");

				for (Enumeration<? extends ZipEntry> filesInZip = zipFile.entries(); filesInZip.hasMoreElements();) {
					ZipEntry fileInZip = filesInZip.nextElement();

					if (fileInZip.getName().toLowerCase().endsWith("htm")
							|| fileInZip.getName().toLowerCase().endsWith("html")) {

						Scanner leituraArquivo = new Scanner(zipFile.getInputStream(fileInZip));
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
				for (Element row : table.getElementsByTag("tr")) {
					Elements cols = row.getElementsByTag("td");
					LoteriaFederal loteria = new LoteriaFederal();

					// Pula a primeira linha
					if (cols.get(0).text().equals("Concurso"))
						continue;
					
					loteria.setConcurso(Integer.valueOf(cols.get(0).text()));

					loteria.setDataSorteio(converteData(cols.get(1).text()));

					loteria.setPrimeiroPremio(Integer.valueOf(cols.get(2).text()));
					loteria.setSegundoPremio(Integer.valueOf(cols.get(3).text()));
					loteria.setTerceiroPremio(Integer.valueOf(cols.get(4).text()));
					loteria.setQuartoPremio(Integer.valueOf(cols.get(5).text()));
					loteria.setQuintaPremio(Integer.valueOf(cols.get(6).text()));

					loteria.setValorPrimeiroPremio(converteNumeroReal(cols.get(7).text()));
					loteria.setValorSegundoPremio(converteNumeroReal(cols.get(8).text()));
					loteria.setValorTerceiroPremio(converteNumeroReal(cols.get(9).text()));
					loteria.setValorQuartoPremio(converteNumeroReal(cols.get(10).text()));
					loteria.setValorQuintoPremio(converteNumeroReal(cols.get(11).text()));
					
					// Salva os concursos não cadastrados na base
					if (repository.findFirstByConcurso(loteria.getConcurso()) == null) {
						repository.save(loteria);
						log.info("Concurso " + loteria.getConcurso() + " salvo");
					}
				}
			}

			result = "Resultados obtidos com sucesso!";

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private double converteNumeroReal(String numero) {
		return Double.valueOf(numero.replace(".", "").replace(",", "."));
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
}
