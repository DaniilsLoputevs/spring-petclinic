package org.springframework.samples.petclinic.vet;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface VetMapper {
	Vet toEntity(VetwithoutSalaryDTO vetwithoutSalaryDTO);

	VetwithoutSalaryDTO toVetwithoutSalaryDTO(Vet vet);
}
