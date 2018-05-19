package br.com.hslife.orcamento.repository;

import br.com.hslife.loteria.model.CidadeUF;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CidadeUFRepository extends JpaRepository<CidadeUF, Long>{

	public CidadeUF findFirstByCidadeAndUfAllIgnoreCase(String cidade, String uf);
}
