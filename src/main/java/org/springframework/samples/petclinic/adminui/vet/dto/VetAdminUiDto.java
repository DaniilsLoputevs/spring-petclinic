package org.springframework.samples.petclinic.adminui.vet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for {@link org.springframework.samples.petclinic.vet.Vet}
 */
public record VetAdminUiDto(Integer id, @NotBlank String firstName, @NotBlank String lastName, @Positive BigDecimal salary, Set<Integer> specialtyIds) {
}
