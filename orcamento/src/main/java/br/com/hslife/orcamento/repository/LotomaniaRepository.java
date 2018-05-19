package br.com.hslife.orcamento.repository;

import br.com.hslife.loteria.model.Lotomania;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="lotomania", path="lotomania")
public interface LotomaniaRepository extends PagingAndSortingRepository<Lotomania, Long>{

	public Lotomania findFirstByConcurso(@Param("concurso") Integer concurso);
}
