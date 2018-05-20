package br.com.hslife.orcamento.repository;

import br.com.hslife.orcamento.entity.Lotomania;
import org.springframework.stereotype.Repository;
import javax.persistence.NoResultException;

@Repository
public class LotomaniaRepository extends AbstractCRUDRepository<Lotomania> {

	public LotomaniaRepository() {
		super(new Lotomania(), Lotomania.class);
	}

	public Lotomania findFirstByConcurso(Integer concurso) {
		try {
			return getSession().createQuery("SELECT l FROM Lotomania l WHERE l.concurso = :concurso", Lotomania.class)
					.setParameter("concurso", concurso)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
