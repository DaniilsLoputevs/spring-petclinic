package org.springframework.samples.petclinic.owner;

import org.mapstruct.*;
import org.springframework.samples.petclinic.intergation.oportal.VisitCreateDto;
import org.springframework.samples.petclinic.intergation.oportal.VisitOPortalDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface VisitMapper {
	Visit toEntity(VisitOPortalDto visitOPortalDto);

	VisitOPortalDto toDto(Visit visit);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Visit partialUpdate(VisitOPortalDto visitOPortalDto, @MappingTarget Visit visit);

	Visit toEntity(VisitCreateDto visitCreateDto);

	VisitCreateDto toDto1(Visit visit);
}
