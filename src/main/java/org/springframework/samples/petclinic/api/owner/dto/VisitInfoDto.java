package org.springframework.samples.petclinic.api.owner.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * DTO for {@link org.springframework.samples.petclinic.owner.Visit}
 */
public record VisitInfoDto(String id, LocalDate date, @NotBlank String description) {
}
