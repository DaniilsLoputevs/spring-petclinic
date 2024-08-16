package org.springframework.samples.petclinic.intergation.oportal;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * DTO for {@link org.springframework.samples.petclinic.owner.Visit}
 */
public record VisitCreateDto(String ownerId, String petId, @Future LocalDate date, @NotBlank String description) {
}
