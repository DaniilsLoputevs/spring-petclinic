package org.springframework.samples.petclinic.adminui.owner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.samples.petclinic.adminui.owner.dto.OwnerAdminUiDto;
import org.springframework.samples.petclinic.adminui.owner.mapper.OwnerAdminUiMapper;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/adminui/owners")
public class OwnerAdminUiRestController {

	private final OwnerRepository ownerRepository;

	private final OwnerAdminUiMapper ownerAdminUiMapper;

	private final ObjectMapper objectMapper;

	public OwnerAdminUiRestController(OwnerRepository ownerRepository,
									  OwnerAdminUiMapper ownerAdminUiMapper,
									  ObjectMapper objectMapper) {
		this.ownerRepository = ownerRepository;
		this.ownerAdminUiMapper = ownerAdminUiMapper;
		this.objectMapper = objectMapper;
	}

	@GetMapping
	public Page<OwnerAdminUiDto> getList(@Nullable @ModelAttribute OwnerFilter filter, Pageable pageable) {
		Specification<Owner> spec = filter.toSpecification();
		Page<Owner> owners = ownerRepository.findAll(spec, pageable);
		return owners.map(ownerAdminUiMapper::toOwnerAdminUiDto);
	}

	@GetMapping("/{id}")
	public OwnerAdminUiDto getOne(@PathVariable Integer id) {
		Optional<Owner> ownerOptional = ownerRepository.findById(id);
		return ownerAdminUiMapper.toOwnerAdminUiDto(ownerOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
	}

	@PatchMapping("/{id}")
	public OwnerAdminUiDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
		Owner owner = ownerRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		OwnerAdminUiDto ownerAdminUiDto = ownerAdminUiMapper.toOwnerAdminUiDto(owner);
		objectMapper.readerForUpdating(ownerAdminUiDto).readValue(patchNode);
		ownerAdminUiMapper.updateWithNull(ownerAdminUiDto, owner);

		Owner resultOwner = ownerRepository.save(owner);
		return ownerAdminUiMapper.toOwnerAdminUiDto(resultOwner);
	}
}

