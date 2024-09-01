package org.springframework.samples.petclinic.api.owner.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.samples.petclinic.api.owner.dto.PetInfoDto;
import org.springframework.samples.petclinic.owner.Pet;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PetApiMapper {
	PetInfoDto toPetInfoDto(Pet pet);
}
