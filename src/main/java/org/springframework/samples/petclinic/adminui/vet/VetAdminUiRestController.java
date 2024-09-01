package org.springframework.samples.petclinic.adminui.vet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.samples.petclinic.adminui.vet.dto.VetAdminUiDto;
import org.springframework.samples.petclinic.adminui.vet.mapper.VetAdminUiMapper;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/adminui/vets")
public class VetAdminUiRestController {

	private final VetRepository vetRepository;

	private final VetAdminUiMapper vetAdminUiMapper;

	private final ObjectMapper objectMapper;

	public VetAdminUiRestController(VetRepository vetRepository,
									VetAdminUiMapper vetAdminUiMapper,
									ObjectMapper objectMapper) {
		this.vetRepository = vetRepository;
		this.vetAdminUiMapper = vetAdminUiMapper;
		this.objectMapper = objectMapper;
	}

	@GetMapping
	public Page<VetAdminUiDto> getList(@Nullable @ModelAttribute VetFilter filter, Pageable pageable) {
		Specification<Vet> spec = filter.toSpecification();
		Page<Vet> vets = vetRepository.findAll(spec, pageable);
		return vets.map(vetAdminUiMapper::toVetAdminUiDto);
	}

	@GetMapping("/{id}")
	public VetAdminUiDto getOne(@PathVariable Integer id) {
		Optional<Vet> vetOptional = vetRepository.findById(id);
		return vetAdminUiMapper.toVetAdminUiDto(vetOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
	}

	@GetMapping("/by-ids")
	public List<VetAdminUiDto> getMany(@RequestParam List<Integer> ids) {
		List<Vet> vets = vetRepository.findAllById(ids);
		return vets.stream()
			.map(vetAdminUiMapper::toVetAdminUiDto)
			.toList();
	}

	@PostMapping
	public VetAdminUiDto create(@RequestBody @Valid VetAdminUiDto dto) {
		Vet vet = vetAdminUiMapper.toEntity(dto);
		Vet resultVet = vetRepository.save(vet);
		return vetAdminUiMapper.toVetAdminUiDto(resultVet);
	}

	@PatchMapping("/{id}")
	public VetAdminUiDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
		Vet vet = vetRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		VetAdminUiDto vetAdminUiDto = vetAdminUiMapper.toVetAdminUiDto(vet);
		objectMapper.readerForUpdating(vetAdminUiDto).readValue(patchNode);
		vetAdminUiMapper.updateWithNull(vetAdminUiDto, vet);

		Vet resultVet = vetRepository.save(vet);
		return vetAdminUiMapper.toVetAdminUiDto(resultVet);
	}

	@PatchMapping
	public List<Integer> patchMany(@RequestParam @Valid List<Integer> ids, @RequestBody JsonNode patchNode) throws IOException {
		Collection<Vet> vets = vetRepository.findAllById(ids);

		for (Vet vet : vets) {
			VetAdminUiDto vetAdminUiDto = vetAdminUiMapper.toVetAdminUiDto(vet);
			objectMapper.readerForUpdating(vetAdminUiDto).readValue(patchNode);
			vetAdminUiMapper.updateWithNull(vetAdminUiDto, vet);
		}

		List<Vet> resultVets = vetRepository.saveAll(vets);
		return resultVets.stream()
			.map(Vet::getId)
			.toList();
	}

	@DeleteMapping("/{id}")
	public VetAdminUiDto delete(@PathVariable Integer id) {
		Vet vet = vetRepository.findById(id).orElse(null);
		if (vet != null) {
			vetRepository.delete(vet);
		}
		return vetAdminUiMapper.toVetAdminUiDto(vet);
	}

	@DeleteMapping
	public void deleteMany(@RequestParam List<Integer> ids) {
		vetRepository.deleteAllById(ids);
	}
}
