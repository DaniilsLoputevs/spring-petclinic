package org.springframework.samples.petclinic.owner;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Integer> {

	List<Pet> findAllByOwner_Id(Integer id, Pageable pageable);
}
