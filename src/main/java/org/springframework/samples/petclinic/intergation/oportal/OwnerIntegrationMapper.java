package org.springframework.samples.petclinic.intergation.oportal;

import org.mapstruct.*;
import org.springframework.samples.petclinic.owner.Owner;
import org.sqids.Sqids;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OwnerIntegrationMapper {
	Owner toEntity(OwnerInfoDto ownerInfoDto);

	@Mapping(source = "id", target = "id", qualifiedByName = "encodeId")
	OwnerInfoDto toDto(Owner owner);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Owner partialUpdate(OwnerInfoDto ownerInfoDto, @MappingTarget Owner owner);

	@Named("encodeId")
	default String encodeId(Long ownerId) {
		Sqids sqids= Sqids.builder().minLength(10)
			.alphabet("vjW3Co7l2RePyY8DwaU04Tzt9fHQrqSVKdpimLGIJOgb5ZEFxnXM1kBN6cuhsA")
			.build();
		return sqids.encode(List.of(ownerId));
	}
}
