package br.com.hslife.orcamento.repository;

import br.com.hslife.orcamento.entity.Lotofacil;
import org.springframework.stereotype.Repository;
import javax.persistence.NoResultException;

@Repository
public class LotofacilRepository extends AbstractCRUDRepository<Lotofacil> {

	public LotofacilRepository() {
		super(new Lotofacil(), Lotofacil.class);
	}

	public Lotofacil findFirstByConcurso(Integer concurso) {
		try {
			return getSession().createQuery("SELECT l FROM Lotofacil l WHERE l.concurso = :concurso", Lotofacil.class)
					.setParameter("concurso", concurso)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
