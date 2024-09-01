package org.springframework.samples.petclinic.adminui.owner.mapper;

import org.mapstruct.*;
import org.springframework.samples.petclinic.adminui.owner.dto.OwnerAdminUiDto;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OwnerAdminUiMapper {
    Owner toEntity(OwnerAdminUiDto ownerAdminUiDto);

    @Mapping(target = "petIds", expression = "java(petsToPetIds(owner.getPets()))")
    OwnerAdminUiDto toOwnerAdminUiDto(Owner owner);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Owner partialUpdate(OwnerAdminUiDto ownerAdminUiDto, @MappingTarget Owner owner);

    default List<Integer> petsToPetIds(List<Pet> pets) {
        return pets.stream().map(Pet::getId).collect(Collectors.toList());
    }

    Owner updateWithNull(OwnerAdminUiDto ownerAdminUiDto, @MappingTarget Owner owner);
}
