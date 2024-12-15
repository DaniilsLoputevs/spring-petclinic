package org.springframework.samples.petclinic.owner;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.samples.petclinic.owner.dto.PetInfoDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PetApiMapper {
	PetInfoDto toPetInfoDto(Pet pet);
}
