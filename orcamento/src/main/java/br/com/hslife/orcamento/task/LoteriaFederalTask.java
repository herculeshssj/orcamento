package br.com.hslife.orcamento.task;

import br.com.hslife.orcamento.util.ExtratorLoteriaFederal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LoteriaFederalTask {

	private static final Logger log = LoggerFactory.getLogger(LoteriaFederalTask.class);
	
	@Autowired
	private ExtratorLoteriaFederal extrator;

    @Scheduled(fixedRate = 3600000)
    public void atualizarResultados() {
    	log.info(extrator.execute());
    }
}
