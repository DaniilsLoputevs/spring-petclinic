package org.springframework.samples.petclinic.owner.dto;

import java.time.LocalDate;

/**
 * DTO for {@link org.springframework.samples.petclinic.owner.Pet}
 */
public record PetInfoDto(Integer id, String name, LocalDate birthDate, String typeName) {
}
