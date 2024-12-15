package org.springframework.samples.petclinic.adminui.owner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/adminui/owners")
public class AdminUIRestController {

	private final OwnerRepository ownerRepository;

	private final OwnerAdminUiMapper ownerAdminUiMapper;

	private final ObjectMapper objectMapper;

	public AdminUIRestController(OwnerRepository ownerRepository,
								 OwnerAdminUiMapper ownerAdminUiMapper,
								 ObjectMapper objectMapper) {
		this.ownerRepository = ownerRepository;
		this.ownerAdminUiMapper = ownerAdminUiMapper;
		this.objectMapper = objectMapper;
	}

	@GetMapping
	public PagedModel<OwnerAdminDto> getAll(@ModelAttribute OwnerFilter filter, Pageable pageable) {
		Specification<Owner> spec = filter.toSpecification();
		Page<Owner> owners = ownerRepository.findAll(spec, pageable);
		Page<OwnerAdminDto> ownerAdminDtoPage = owners.map(ownerAdminUiMapper::toOwnerAdminDto);
		return new PagedModel<>(ownerAdminDtoPage);
	}

	@GetMapping("/{id}")
	public OwnerAdminDto getOne(@PathVariable Integer id) {
		Optional<Owner> ownerOptional = ownerRepository.findById(id);
		return ownerAdminUiMapper.toOwnerAdminDto(ownerOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
	}

	@PatchMapping("/{id}")
	public OwnerAdminDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
		Owner owner = ownerRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		OwnerAdminDto ownerAdminDto = ownerAdminUiMapper.toOwnerAdminDto(owner);
		objectMapper.readerForUpdating(ownerAdminDto).readValue(patchNode);
		ownerAdminUiMapper.updateWithNull(ownerAdminDto, owner);

		Owner resultOwner = ownerRepository.save(owner);
		return ownerAdminUiMapper.toOwnerAdminDto(resultOwner);
	}
}

