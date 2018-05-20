package br.com.hslife.orcamento.task;

import br.com.hslife.orcamento.util.ExtratorLotomania;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LotomaniaTask {

	private static final Logger log = LoggerFactory.getLogger(LotomaniaTask.class);
	
	@Autowired
	private ExtratorLotomania extrator;

    @Scheduled(fixedRate = 3600000)
    public void atualizarResultados() {
    	log.info(extrator.execute());
    }
}
