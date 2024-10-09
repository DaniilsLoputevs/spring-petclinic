package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;

/**
 * DTO for {@link Pet}
 */
public record PetDto(Integer id, String name, LocalDate birthDate, Integer typeId, Integer ownerId) {
}
