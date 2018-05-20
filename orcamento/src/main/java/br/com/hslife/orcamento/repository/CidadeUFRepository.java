package br.com.hslife.orcamento.repository;

import br.com.hslife.orcamento.entity.CidadeUF;
import org.springframework.stereotype.Repository;

@Repository
public class CidadeUFRepository extends AbstractCRUDRepository<CidadeUF> {

	public CidadeUFRepository() {
		super(new CidadeUF(), CidadeUF.class);
	}

	public CidadeUF findFirstByCidadeAndUfAllIgnoreCase(String cidade, String uf) {
		// TODO implementar
		return null;
	}
}
