package org.springframework.samples.petclinic.owner;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VisitRepository extends JpaRepository<Visit, Integer>, JpaSpecificationExecutor<Visit> {

	@EntityGraph(attributePaths = {"vet"}, type = EntityGraph.EntityGraphType.LOAD)
	Slice<Visit> findByPetId(Integer id, Pageable pageable);
}
