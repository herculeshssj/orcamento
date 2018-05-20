package br.com.hslife.orcamento.repository;

import br.com.hslife.orcamento.entity.CidadeUF;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;

@Repository
public class CidadeUFRepository extends AbstractCRUDRepository<CidadeUF> {

	public CidadeUFRepository() {
		super(new CidadeUF(), CidadeUF.class);
	}

	public CidadeUF findFirstByCidadeAndUfAllIgnoreCase(String cidade, String uf) {
		try {
			return getSession().createQuery("SELECT c FROM CidadeUF c WHERE c.cidade = :cidade AND c.uf = :uf", CidadeUF.class)
					.setParameter("cidade", cidade)
					.setParameter("uf", uf)
					.setMaxResults(1)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
