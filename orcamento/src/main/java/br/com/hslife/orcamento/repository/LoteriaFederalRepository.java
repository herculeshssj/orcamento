package br.com.hslife.orcamento.repository;

import br.com.hslife.orcamento.entity.LoteriaFederal;
import org.springframework.stereotype.Repository;
import javax.persistence.NoResultException;

@Repository
public class LoteriaFederalRepository extends AbstractCRUDRepository<LoteriaFederal> {

	public LoteriaFederalRepository() {
		super(new LoteriaFederal(), LoteriaFederal.class);
	}

	public LoteriaFederal findFirstByConcurso(Integer concurso) {
		try {
			return getSession().createQuery("SELECT l FROM LoteriaFederal l WHERE l.concurso = :concurso", LoteriaFederal.class)
					.setParameter("concurso", concurso)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
