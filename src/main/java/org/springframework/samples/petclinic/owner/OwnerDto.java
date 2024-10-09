package org.springframework.samples.petclinic.owner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for {@link Owner}
 */
public record OwnerDto(Integer id, @NotBlank String firstName, @NotBlank String lastName, @NotBlank String address,
					   @NotBlank String city,
					   @Pattern(message = "Telephone must be a 10-digit number", regexp = "\\d{10}") @NotBlank String telephone) {
}
