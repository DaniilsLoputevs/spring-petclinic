package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;

/**
 * DTO for {@link Pet}
 */
public record PetOPortalDto(String id, String name, LocalDate birthDate, String typeName) {
}
