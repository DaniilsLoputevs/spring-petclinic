package org.springframework.samples.petclinic.api.owner;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.api.idencoder.IdEncoderApiRepository;
import org.springframework.samples.petclinic.api.owner.owner.OwnerInfoDto;
import org.springframework.samples.petclinic.owner.Owner;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class OwnerApiMapper {

	@Autowired public IdEncoderApiRepository idEncoderApiRepository;

	@Mapping(target = "id", source = "id", qualifiedByName = "encodeOwnerId")
	public abstract OwnerInfoDto toOwnerInfoDto(Owner owner);

	public abstract Owner toEntity(OwnerKeyFieldDto ownerKeyFieldDto);

	public abstract OwnerKeyFieldDto toOwnerKeyFieldDto(Owner owner);

	@Named("encodeOwnerId")
	public String encodeOwnerId(Integer id) {
		return idEncoderApiRepository.findEncoderByName("owner").encode(List.of(id.longValue()));
	}
}
