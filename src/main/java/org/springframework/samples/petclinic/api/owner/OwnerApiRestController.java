package org.springframework.samples.petclinic.api.owner;

import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.api.idencoder.IdEncodedApiRepository;
import org.springframework.samples.petclinic.api.owner.dto.OwnerInfoDto;
import org.springframework.samples.petclinic.api.owner.dto.PetInfoDto;
import org.springframework.samples.petclinic.api.owner.mapper.OwnerApiMapper;
import org.springframework.samples.petclinic.api.owner.mapper.PetApiMapper;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/owners")
public class OwnerApiRestController {

	private final OwnerRepository ownerRepository;

	private final OwnerApiMapper ownerApiMapper;

	private final PetRepository petRepository;

	private final PetApiMapper petApiMapper;

	private final IdEncodedApiRepository idEncodedApiRepository;

	public OwnerApiRestController(OwnerRepository ownerRepository,
								  OwnerApiMapper ownerApiMapper,
								  PetRepository petRepository,
								  PetApiMapper petApiMapper,
								  IdEncodedApiRepository idEncodedApiRepository) {
		this.ownerRepository = ownerRepository;
		this.ownerApiMapper = ownerApiMapper;
		this.petRepository = petRepository;
		this.petApiMapper = petApiMapper;
		this.idEncodedApiRepository = idEncodedApiRepository;
	}

	@PostMapping(path = {"/by-key-fields"})
	public List<OwnerInfoDto> findByTelephoneIn(@RequestBody Collection<OwnerKeyFieldsDto> ownerKeyFieldsDtos) {
		if (ownerKeyFieldsDtos.size() > 100) {
			throw new IllegalArgumentException("Too may owners");
		}

		List<String> telephones = ownerKeyFieldsDtos.stream().map(OwnerKeyFieldsDto::telephone).toList();

		List<Owner> owners = ownerRepository.findByTelephoneIn(telephones).stream().filter(o ->
			ownerKeyFieldsDtos.contains(ownerApiMapper.toOwnerKeyFieldsDto(o))).toList();

		return owners.stream()
			.map(ownerApiMapper::toOwnerInfoDto)
			.toList();
	}

	@GetMapping("/{ownerId}/pets")
	public List<PetInfoDto> findAllByOwner_Id(@PathVariable String ownerId, Pageable pageable) {
		int ownerIdDecoded = idEncodedApiRepository.findEncoderByName("owner")
			.decode(ownerId).getFirst().intValue();
		List<Pet> pets = petRepository.findAllByOwner_Id(ownerIdDecoded, pageable);
		return pets.stream()
			.map(petApiMapper::toPetInfoDto)
			.toList();
	}
}

