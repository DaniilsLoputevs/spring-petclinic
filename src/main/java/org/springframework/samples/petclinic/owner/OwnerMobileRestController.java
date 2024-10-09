package org.springframework.samples.petclinic.owner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/rest/owners")
public class OwnerMobileRestController {

	public static final String CACHE_NAME = "owners";

	private final OwnerRepository ownerRepository;

	private final OwnerMapper ownerMapper;

	private final ObjectMapper objectMapper;

	private final CacheManager cacheManager;

	private OwnerMobileRestController self;

	public OwnerMobileRestController(OwnerRepository ownerRepository,
									 OwnerMapper ownerMapper,
									 ObjectMapper objectMapper,
									 CacheManager cacheManager) {
		this.ownerRepository = ownerRepository;
		this.ownerMapper = ownerMapper;
		this.objectMapper = objectMapper;
		this.cacheManager = cacheManager;
	}

	@Autowired
	public void setSelf(OwnerMobileRestController self) {
		this.self = self;
	}

	@GetMapping
	public ResponseEntity<PagedModel<OwnerDto>> getAll(@ModelAttribute OwnerApiFilter filter, Pageable pageable) {
		Page<Owner> owners = ownerRepository.findAll(filter.toSpecification(), pageable);
		Page<OwnerDto> ownerDtoPage = owners.map(ownerMapper::toOwnerDto);

		return ResponseEntity.ok()
			.cacheControl(CacheControl.noCache())
			.body(new PagedModel<>(ownerDtoPage));
	}

	@GetMapping("/by-ids")
	public ResponseEntity<List<OwnerDto>> getMany(@RequestParam List<Integer> ids) {

		List<OwnerDto> resOwners = new ArrayList<>(ids.size());

		Cache ownerCache = cacheManager.getCache(CACHE_NAME);
		if (ownerCache != null) {
			var cachedOwners = ids.stream().map(ownerCache::get).filter(Objects::nonNull)
				.map(Cache.ValueWrapper::get).filter(Objects::nonNull).map(o -> (OwnerDto) o).toList();

			resOwners.addAll(cachedOwners);
		}

		var missingInCacheIds = ListUtils.removeAll(ids, resOwners.stream().map(OwnerDto::id).toList());

		List<OwnerDto> loadedFromDb = ownerRepository.findAllById(missingInCacheIds)
			.stream().map(ownerMapper::toOwnerDto).toList();

		resOwners.addAll(loadedFromDb);

		if (ownerCache != null) {
			loadedFromDb.forEach(ownerDto -> ownerCache.putIfAbsent(ownerDto.id(), ownerDto));
		}


		return ResponseEntity.ok()
			.cacheControl(CacheControl.noCache())
			.body(resOwners);
	}

	@PostMapping
	public OwnerDto createOne(@RequestBody @Valid OwnerDto ownerDto) {
		if (ownerDto.id() != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must be null");
		}

		Owner owner = ownerMapper.toEntity(ownerDto);
		Owner resultOwner = ownerRepository.save(owner);
		return ownerMapper.toOwnerDto(resultOwner);
	}

	@GetMapping("/{id}")
	public ResponseEntity<OwnerDto> getOne(@PathVariable Integer id) {

		OwnerDto ownerDto = self.getOneInternal(id);

		return ResponseEntity.ok()
			.cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
			.body(ownerDto);
	}

	@Cacheable(CACHE_NAME)
	public OwnerDto getOneInternal(Integer id) {
		Optional<Owner> ownerOptional = ownerRepository.findById(id);

		return ownerMapper.toOwnerDto(ownerOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
	}

	@Cacheable("owners-with-pets")
	@GetMapping("/with-pets/{id}")
	public ResponseEntity<OwnerWithPetDto> getOne1(@PathVariable Integer id) {
		Optional<Owner> ownerOptional = ownerRepository.findById(id);
		OwnerWithPetDto ownerWithPetDto = ownerMapper.toOwnerWithPetDto(ownerOptional.orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));

		return ResponseEntity.ok()
			.cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
			.body(ownerWithPetDto);
	}

	@DeleteMapping("/{id}")
	@CacheEvict(CACHE_NAME)
	public OwnerDto delete(@PathVariable Integer id) {
		Owner owner = ownerRepository.findById(id).orElse(null);
		if (owner != null) {
			ownerRepository.delete(owner);
		}
		return ownerMapper.toOwnerDto(owner);
	}

	@PatchMapping("/{id}")
	@CacheEvict(value = CACHE_NAME, key = "#id")
	public OwnerDto patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
		Owner owner = ownerRepository.findById(id).orElseThrow(() ->
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

		OwnerDto ownerDto = ownerMapper.toOwnerDto(owner);
		objectMapper.readerForUpdating(ownerDto).readValue(patchNode);
		ownerMapper.updateWithNull(ownerDto, owner);

		Owner resultOwner = ownerRepository.save(owner);
		return ownerMapper.toOwnerDto(resultOwner);
	}

//	@Override
//	@PostConstruct
//	public void afterPropertiesSet() throws Exception {
//		self = applicationContext.getBean(OwnerMobileRestController.class);
//	}
}

