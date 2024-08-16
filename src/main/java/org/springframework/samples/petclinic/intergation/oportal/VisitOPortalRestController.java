package org.springframework.samples.petclinic.intergation.oportal;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.owner.VisitMapper;
import org.springframework.samples.petclinic.owner.VisitRepository;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oportal/visits")
public class VisitOPortalRestController {

	private final VisitRepository visitRepository;

	private final VisitMapper visitMapper;

	private final VetRepository vetRepository;

	public VisitOPortalRestController(VisitRepository visitRepository,
									  VisitMapper visitMapper,
									  VetRepository vetRepository) {
		this.visitRepository = visitRepository;
		this.visitMapper = visitMapper;
		this.vetRepository = vetRepository;
	}


	@GetMapping("/by-pet-with-vet")
	public Slice<VisitOPortalDto> findByPet_Id(@RequestParam Integer id, Pageable pageable) {
		Slice<Visit> visits = visitRepository.findByPetId(id, pageable);

		return visits.map(visitMapper::toDto);
	}
}

