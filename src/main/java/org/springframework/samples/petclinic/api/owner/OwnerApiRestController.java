package org.springframework.samples.petclinic.api.owner;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.samples.petclinic.api.idencoder.IdEncoderApiRepository;
import org.springframework.samples.petclinic.api.idencoder.IdEncoderConfigurationProperties;
import org.springframework.samples.petclinic.api.owner.owner.OwnerInfoDto;
import org.springframework.samples.petclinic.owner.*;
import org.springframework.samples.petclinic.owner.dto.PetInfoDto;
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

	private final IdEncoderConfigurationProperties idEncoderConfigurationProperties;

	private final IdEncoderApiRepository idEncoderApiRepository;

	public OwnerApiRestController(OwnerRepository ownerRepository,
								  OwnerApiMapper ownerApiMapper,
								  PetRepository petRepository,
								  PetApiMapper petApiMapper,
								  IdEncoderConfigurationProperties idEncoderConfigurationProperties,
								  IdEncoderApiRepository idEncoderApiRepository) {
		this.ownerRepository = ownerRepository;
		this.ownerApiMapper = ownerApiMapper;
		this.petRepository = petRepository;
		this.petApiMapper = petApiMapper;
		this.idEncoderConfigurationProperties = idEncoderConfigurationProperties;
		this.idEncoderApiRepository = idEncoderApiRepository;
	}

	@PostMapping(path = {"/by-key-fields"})
	public List<OwnerInfoDto> findAllByTelephoneIn(@RequestBody Collection<OwnerKeyFieldDto> ownerKeyFieldDtos) {
		var telephones = ownerKeyFieldDtos.stream().map(OwnerKeyFieldDto::telephone).toList();
		List<Owner> owners = ownerRepository.findAllByTelephoneIn(telephones).stream()
			.filter(o -> ownerKeyFieldDtos.contains(ownerApiMapper.toOwnerKeyFieldDto(o)))
			.toList();

		return owners.stream()
			.map(ownerApiMapper::toOwnerInfoDto)
			.toList();
	}
	@GetMapping("/{id}")
	public @ResponseBody Slice<PetInfoDto> findByOwner_Id(@PathVariable String id, Pageable pageable) {
		var ownerIdDecoder = idEncoderApiRepository.findEncoderByName("owner").decode(id).get(0).intValue();
		Slice<Pet> pets = petRepository.findByOwner_Id(ownerIdDecoder, pageable);
		return pets.map(petApiMapper::toPetInfoDto);
	}
}

