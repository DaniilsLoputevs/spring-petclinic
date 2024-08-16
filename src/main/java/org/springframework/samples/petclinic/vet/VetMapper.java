package org.springframework.samples.petclinic.vet;

import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface VetMapper {
    Vet toEntity(VetDto vetDto);

    @Mapping(target = "specialtyIds", expression = "java(specialtiesToSpecialtyIds(vet.getSpecialtiesInternal()))")
    VetDto toDto(Vet vet);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Vet partialUpdate(VetDto vetDto, @MappingTarget Vet vet);

    default Set<Integer> specialtiesToSpecialtyIds(Set<Specialty> specialties) {
        return specialties.stream().map(Specialty::getId).collect(Collectors.toSet());
    }

    Vet updateWithNull(VetDto vetDto, @MappingTarget Vet vet);
}
