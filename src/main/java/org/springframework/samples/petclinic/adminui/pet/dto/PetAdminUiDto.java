package org.springframework.samples.petclinic.adminui.pet.dto;

import java.time.LocalDate;

/**
 * DTO for {@link org.springframework.samples.petclinic.owner.Pet}
 */
public record PetAdminUiDto(Integer id, String name, LocalDate birthDate, Integer typeId, Integer ownerId) {
}
