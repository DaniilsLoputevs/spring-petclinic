package org.springframework.samples.petclinic.owner;

import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

@org.mapstruct.Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, componentModel = "spring")
public interface OwnerMapper {
    OwnerDto toOwnerDto(Owner owner);

    Owner toEntity(OwnerDto ownerDto);

    Owner updateWithNull(OwnerDto ownerDto, @MappingTarget Owner owner);

    OwnerMinimalDto toOwnerMinimalDto(Owner owner);

    OwnerWithPetDto toOwnerWithPetDto(Owner owner);

    @AfterMapping
    default void linkPets(@MappingTarget Owner owner) {
        owner.getPets().forEach(pet -> pet.setOwner(owner));
    }
}
