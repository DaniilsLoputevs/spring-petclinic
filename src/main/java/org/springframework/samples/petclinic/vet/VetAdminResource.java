package org.springframework.samples.petclinic.vet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/vets")
public class VetAdminResource {

	private final VetRepository vetRepository;

	private final VetMapper vetMapper;

	private final ObjectMapper objectMapper;

	public VetAdminResource(VetRepository vetRepository,
							VetMapper vetMapper,
							ObjectMapper objectMapper) {
		this.vetRepository = vetRepository;
		this.vetMapper = vetMapper;
		this.objectMapper = objectMapper;
	}

	@GetMapping
	public Page<VetDto> getList(@ModelAttribute VetFilter filter, Pageable pageable) {
		Specification<Vet> spec = filter.toSpecification();
		Page<Vet> vets = vetRepository.findAll(spec, pageable);
		return vets.map(vetMapper::toDto);
	}

	@GetMapping("/{id}")
	public VetDto getOne(@PathVariable Integer id) {
		Optional<Vet> vetOptional = vetRepository.findById(id);
		return vetMapper.toDto(vetOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
	}

	@GetMapping("/by-ids")
	public List<VetDto> getMany(@RequestParam List<Integer> ids) {
		List<Vet> vets = vetRepository.findAllById(ids);
		return vets.stream()
			.map(vetMapper::toDto)
			.toList();
	}

	@PostMapping
	public VetDto create(@RequestBody @Valid VetDto dto) {
		Vet vet = vetMapper.toEntity(dto);
		Vet resultVet = vetRepository.save(vet);
		return vetMapper.toDto(resultVet);
	}

	@PatchMapping("/{id}")
	public VetDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
		Vet vet = vetRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		VetDto vetDto = vetMapper.toDto(vet);
		objectMapper.readerForUpdating(vetDto).readValue(patchNode);
		vetMapper.updateWithNull(vetDto, vet);

		Vet resultVet = vetRepository.save(vet);
		return vetMapper.toDto(resultVet);
	}

	@PatchMapping
	public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) throws IOException {
		Collection<Vet> vets = vetRepository.findAllById(ids);

		for (Vet vet : vets) {
			VetDto vetDto = vetMapper.toDto(vet);
			objectMapper.readerForUpdating(vetDto).readValue(patchNode);
			vetMapper.updateWithNull(vetDto, vet);
		}

		List<Vet> resultVets = vetRepository.saveAll(vets);
		return resultVets.stream()
			.map(Vet::getId)
			.toList();
	}

	@DeleteMapping("/{id}")
	public VetDto delete(@PathVariable Integer id) {
		Vet vet = vetRepository.findById(id).orElse(null);
		if (vet != null) {
			vetRepository.delete(vet);
		}
		return vetMapper.toDto(vet);
	}

	@DeleteMapping
	public void deleteMany(@RequestParam List<Integer> ids) {
		vetRepository.deleteAllById(ids);
	}
}
