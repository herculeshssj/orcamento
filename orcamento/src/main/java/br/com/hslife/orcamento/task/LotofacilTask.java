package br.com.hslife.orcamento.task;

import br.com.hslife.orcamento.util.ExtratorLotofacil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LotofacilTask {

	private static final Logger log = LoggerFactory.getLogger(LotofacilTask.class);
	
	@Autowired
	private ExtratorLotofacil extrator;

    @Scheduled(fixedRate = 3600000)
    public void atualizarResultados() {
    	log.info(extrator.execute());
    }
}
