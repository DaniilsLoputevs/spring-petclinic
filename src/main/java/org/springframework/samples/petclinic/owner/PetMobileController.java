package org.springframework.samples.petclinic.owner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/rest/pets")
public class PetMobileController {

	private final PetRepository petRepository;

	private final PetMapper petMapper;

	private final ObjectMapper objectMapper;

	public PetMobileController(PetRepository petRepository,
							   PetMapper petMapper,
							   ObjectMapper objectMapper) {
		this.petRepository = petRepository;
		this.petMapper = petMapper;
		this.objectMapper = objectMapper;
	}

	@PatchMapping("/{id}")
	public PetDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
		Pet pet = petRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		PetDto petDto = petMapper.toPetDto(pet);
		objectMapper.readerForUpdating(petDto).readValue(patchNode);
		petMapper.updateWithNull(petDto, pet);

		Pet resultPet = petRepository.save(pet);
		return petMapper.toPetDto(resultPet);
	}
}

