package org.springframework.samples.petclinic.owner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for {@link Owner}
 */
public record OwnerMinimalDto(Integer id, @NotBlank String firstName, @NotBlank String lastName) {
}
