package org.springframework.samples.petclinic.intergation.oportal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for {@link org.springframework.samples.petclinic.owner.Owner}
 */
public record OwnerMappingKeyFieldDto(@NotBlank String firstName, @NotBlank String lastName,
									  @Pattern(message = "Telephone must be a 10-digit number", regexp = "\\d{10}") @NotBlank String telephone) {
}
