package br.com.hslife.orcamento.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.util.ExtratorLotofacil;

@Component
public class LotofacilTask {

	private static final Logger log = LoggerFactory.getLogger(LotofacilTask.class);
	
	@Autowired
	private ExtratorLotofacil extrator;

    //@Scheduled(fixedRate = 3600000)
    public void atualizarResultados() {
    	log.info(extrator.execute());
    }
}
