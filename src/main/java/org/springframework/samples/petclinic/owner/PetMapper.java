package org.springframework.samples.petclinic.owner;

import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PetMapper {
	@Mapping(source = "owner.id", target = "ownerId")
	@Mapping(source = "type.id", target = "typeId")
	PetDto toPetDto(Pet pet);

	@Mapping(source = "ownerId", target = "owner")
	@Mapping(source = "typeId", target = "type")
	Pet updateWithNull(PetDto petDto, @MappingTarget Pet pet);

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
