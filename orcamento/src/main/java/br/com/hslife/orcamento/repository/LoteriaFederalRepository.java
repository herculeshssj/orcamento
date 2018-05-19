package br.com.hslife.orcamento.repository;

import br.com.hslife.loteria.model.LoteriaFederal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="loteriaFederal", path="federal")
public interface LoteriaFederalRepository extends PagingAndSortingRepository<LoteriaFederal, Long>{

	public LoteriaFederal findFirstByConcurso(@Param("concurso") Integer concurso);
}
