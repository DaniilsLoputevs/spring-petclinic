package org.springframework.samples.petclinic.adminui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.adminui.pet.dto.PetAdminUiDto;
import org.springframework.samples.petclinic.adminui.pet.mapper.PetAdminUiMapper;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/admin-ui/pets")
public class PetResource {

	private final PetRepository petRepository;

	private final PetAdminUiMapper petAdminUiMapper;

	private final ObjectMapper objectMapper;

	public PetResource(PetRepository petRepository,
					   PetAdminUiMapper petAdminUiMapper,
					   ObjectMapper objectMapper) {
		this.petRepository = petRepository;
		this.petAdminUiMapper = petAdminUiMapper;
		this.objectMapper = objectMapper;
	}

	@GetMapping
	public PagedModel<PetAdminUiDto> getAll(@ModelAttribute PetFilter filter, Pageable pageable) {
		Specification<Pet> spec = filter.toSpecification();
		Page<Pet> pets = petRepository.findAll(spec, pageable);
		Page<PetAdminUiDto> petAdminUiDtoPage = pets.map(petAdminUiMapper::toPetAdminUiDto);
		return new PagedModel<>(petAdminUiDtoPage);
	}

	@GetMapping("/{id}")
	public PetAdminUiDto getOne(@PathVariable Integer id) {
		Optional<Pet> petOptional = petRepository.findById(id);
		return petAdminUiMapper.toPetAdminUiDto(petOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
	}

	@GetMapping("/by-ids")
	public List<PetAdminUiDto> getMany(@RequestParam List<Integer> ids) {
		List<Pet> pets = petRepository.findAllById(ids);
		return pets.stream()
			.map(petAdminUiMapper::toPetAdminUiDto)
			.toList();
	}

	@PostMapping
	public PetAdminUiDto create(@RequestBody PetAdminUiDto dto) {
		Pet pet = petAdminUiMapper.toEntity(dto);
		Pet resultPet = petRepository.save(pet);
		return petAdminUiMapper.toPetAdminUiDto(resultPet);
	}

	@PatchMapping("/{id}")
	public PetAdminUiDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
		Pet pet = petRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		PetAdminUiDto petAdminUiDto = petAdminUiMapper.toPetAdminUiDto(pet);
		objectMapper.readerForUpdating(petAdminUiDto).readValue(patchNode);
		petAdminUiMapper.updateWithNull(petAdminUiDto, pet);

		Pet resultPet = petRepository.save(pet);
		return petAdminUiMapper.toPetAdminUiDto(resultPet);
	}

	@PatchMapping
	public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) throws IOException {
		Collection<Pet> pets = petRepository.findAllById(ids);

		for (Pet pet : pets) {
			PetAdminUiDto petAdminUiDto = petAdminUiMapper.toPetAdminUiDto(pet);
			objectMapper.readerForUpdating(petAdminUiDto).readValue(patchNode);
			petAdminUiMapper.updateWithNull(petAdminUiDto, pet);
		}

		List<Pet> resultPets = petRepository.saveAll(pets);
		return resultPets.stream()
			.map(Pet::getId)
			.toList();
	}

	@DeleteMapping("/{id}")
	public PetAdminUiDto delete(@PathVariable Integer id) {
		Pet pet = petRepository.findById(id).orElse(null);
		if (pet != null) {
			petRepository.delete(pet);
		}
		return petAdminUiMapper.toPetAdminUiDto(pet);
	}

	@DeleteMapping
	public void deleteMany(@RequestParam List<Integer> ids) {
		petRepository.deleteAllById(ids);
	}
}
