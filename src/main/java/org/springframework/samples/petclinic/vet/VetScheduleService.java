package org.springframework.samples.petclinic.vet;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service public class VetScheduleService {

	private final VetRepository vetRepository;

	public VetScheduleService(VetRepository vetRepository) {
		this.vetRepository = vetRepository;
	}

	public Vet findApropriateVet(Owner owner, Pet referenceById, Visit visit) {
		var surgeryId = 2;
		return vetRepository.findAllBySpecialties_IdIn(List.of(surgeryId)).stream().findAny().orElse(null);
	}
}
