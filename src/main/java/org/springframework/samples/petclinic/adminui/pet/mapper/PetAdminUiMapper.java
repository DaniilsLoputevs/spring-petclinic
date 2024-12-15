package org.springframework.samples.petclinic.adminui.pet.mapper;

import org.mapstruct.*;
import org.springframework.samples.petclinic.adminui.pet.dto.PetAdminUiDto;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PetAdminUiMapper {
	@Mapping(source = "ownerId", target = "owner.id") @Mapping(source = "typeId", target = "type.id")
	Pet toEntity(PetAdminUiDto petAdminUiDto);

	@InheritInverseConfiguration(name = "toEntity") PetAdminUiDto toPetAdminUiDto(Pet pet);

	@Mapping(source = "ownerId", target = "owner") @Mapping(source = "typeId", target = "type")
	Pet updateWithNull(PetAdminUiDto petAdminUiDto, @MappingTarget Pet pet);

	default PetType createPetType(Integer typeId) {
		if (typeId == null) {
			return null;
		}
		PetType petType = new PetType();
		petType.setId(typeId);
		return petType;
	}

	default Owner createOwner(Integer ownerId) {
		if (ownerId == null) {
			return null;
		}
		Owner owner = new Owner();
		owner.setId(ownerId);
		return owner;
	}
}
