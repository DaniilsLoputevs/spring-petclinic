package org.springframework.samples.petclinic.adminui.owner;

import org.mapstruct.*;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OwnerAdminUiMapper {
	@Mapping(target = "petIds", expression = "java(petsToPetIds(owner.getPets()))")
	OwnerAdminDto toOwnerAdminDto(Owner owner);

	default List<Integer> petsToPetIds(List<Pet> pets) {
		return pets.stream().map(Pet::getId).collect(Collectors.toList());
	}

	Owner updateWithNull(OwnerAdminDto ownerAdminDto, @MappingTarget Owner owner);
}
