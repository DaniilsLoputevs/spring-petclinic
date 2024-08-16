package org.springframework.samples.petclinic.intergation.oportal;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.owner.*;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.sqids.Sqids;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/oportal")
public class OwnerOPortalRestController {

	private final OwnerRepository ownerRepository;

	private final OwnerIntegrationMapper ownerIntegrationMapper;

	private final PetIntegrationMapper petIntegrationMapper;

	private final PetRepository petRepository;

	private final VisitRepository visitRepository;

	private final VisitMapper visitMapper;

	private final VetRepository vetRepository;

	private final VisitService visitService;

	public OwnerOPortalRestController(OwnerRepository ownerRepository,
									  OwnerIntegrationMapper ownerIntegrationMapper,
									  PetIntegrationMapper petIntegrationMapper,
									  PetRepository petRepository,
									  VisitRepository visitRepository,
									  VisitMapper visitMapper,
									  VetRepository vetRepository,
									  VisitService visitService) {
		this.ownerRepository = ownerRepository;
		this.ownerIntegrationMapper = ownerIntegrationMapper;
		this.petIntegrationMapper = petIntegrationMapper;
		this.petRepository = petRepository;
		this.visitRepository = visitRepository;
		this.visitMapper = visitMapper;
		this.vetRepository = vetRepository;
		this.visitService = visitService;
	}


	// add rate-limit
	@PostMapping(path = {"/owners/find-by-key-fields"})
	public List<OwnerInfoDto> findByKeyFields(@RequestBody List<OwnerMappingKeyFieldDto> keyFieldDtoList) {
		if (keyFieldDtoList.size() > 100) {
			throw new IllegalArgumentException("Too many owners to find");
		}

		List<String> telephones = keyFieldDtoList.stream()
			.map(OwnerMappingKeyFieldDto::telephone).toList();
		List<Owner> fullMatchOwners = ownerRepository.findByTelephoneIn(telephones).stream()
			.filter(o -> keyFieldDtoList.contains(
				new OwnerMappingKeyFieldDto(o.getFirstName(), o.getLastName(), o.getTelephone())))
			.toList();

		return fullMatchOwners.stream().map(ownerIntegrationMapper::toDto).toList();
	}

	@GetMapping("/owners/{ownerId}/pets")
	public Slice<PetOPortalDto> findPetsByOwnerId(@PathVariable String ownerId, Pageable pageable) {
		Sqids sqids= Sqids.builder().minLength(10)
			.alphabet("vjW3Co7l2RePyY8DwaU04Tzt9fHQrqSVKdpimLGIJOgb5ZEFxnXM1kBN6cuhsA")
			.build();

		int decodedId = sqids.decode(ownerId).get(0).intValue();
		Slice<Pet> pets = petRepository.findByOwnerId(decodedId, pageable);
		return pets.map(petIntegrationMapper::toDto);
	}

	@GetMapping("/owners/{ownerId}/pets/{petId}")
	public Slice<VisitOPortalDto> findVisitsByOwnerAndPetId(@PathVariable String ownerId,
															@PathVariable String petId, Pageable pageable) {
		Slice<Visit> visits = visitRepository.findByPetId(petId, pageable);
		return visits.map(visitMapper::toDto);
	}

	@GetMapping("/visits/{id}")
	public VisitOPortalDto getOne(@PathVariable Integer id) {
		Optional<Visit> visitOptional = visitRepository.findById(id);
		return visitMapper.toDto(visitOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
	}

//	@PostMapping("/owners/{ownerId}/pets/{petId}/visits") ???
	@PostMapping("/visits")
	public VisitOPortalDto create(@RequestBody @Valid VisitCreateDto visitCreateDto) {
		visitService.createVisit()
		Visit visit = visitMapper.toEntity(visitCreateDto);
		Visit resultVisit = visitRepository.save(visit);
		return visitMapper.toDto(resultVisit);
	}
}

