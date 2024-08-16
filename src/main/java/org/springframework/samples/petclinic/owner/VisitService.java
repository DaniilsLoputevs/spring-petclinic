package org.springframework.samples.petclinic.owner;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class VisitService {

	private final VisitRepository visitRepository;

	public VisitService(VisitRepository visitRepository) {
		this.visitRepository = visitRepository;
	}

	public Visit createVisit(String ownerId, String petId,
							 LocalDate date, String description) {

		Visit visit = new Visit();
		visit.setPetId(Integer.valueOf(petId));
		visit.setDate(date);
		visit.setDescription(description);
		// find appropriate vet

		return visitRepository.save(visit);
	}
}
