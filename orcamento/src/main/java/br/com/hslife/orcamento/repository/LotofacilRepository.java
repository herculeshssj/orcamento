package br.com.hslife.orcamento.repository;

import br.com.hslife.loteria.model.Lotofacil;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="lotofacil", path="lotofacil")
public interface LotofacilRepository extends PagingAndSortingRepository<Lotofacil, Long>{

	public Lotofacil findFirstByConcurso(@Param("concurso") Integer concurso);
}
