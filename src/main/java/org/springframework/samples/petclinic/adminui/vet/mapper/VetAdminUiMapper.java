package org.springframework.samples.petclinic.adminui.vet.mapper;

import org.mapstruct.*;
import org.springframework.samples.petclinic.adminui.vet.dto.VetAdminUiDto;
import org.springframework.samples.petclinic.vet.Specialty;
import org.springframework.samples.petclinic.vet.Vet;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface VetAdminUiMapper {
	Vet toEntity(VetAdminUiDto vetAdminUiDto);

	@Mapping(target = "specialtyIds", expression = "java(specialtiesToSpecialtyIds(vet.getSpecialties()))")
	VetAdminUiDto toVetAdminUiDto(Vet vet);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Vet partialUpdate(VetAdminUiDto vetAdminUiDto, @MappingTarget Vet vet);

	default Set<Integer> specialtiesToSpecialtyIds(List<Specialty> specialties) {
		return specialties.stream().map(Specialty::getId).collect(Collectors.toSet());
	}

	Vet updateWithNull(VetAdminUiDto vetAdminUiDto, @MappingTarget Vet vet);
}
