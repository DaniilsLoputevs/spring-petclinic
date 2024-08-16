package org.springframework.samples.petclinic.intergation.oportal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link org.springframework.samples.petclinic.owner.Owner}
 */
public record OwnerOPortalDto(Integer id, @NotBlank String firstName, @NotBlank String lastName,
							  @Pattern(message = "Telephone must be a 10-digit number", regexp = "\\d{10}") @NotBlank String telephone,
							  List<PetDto> pets) {
	/**
	 * DTO for {@link org.springframework.samples.petclinic.owner.Pet}
	 */
	public record PetDto(Integer id, String name, LocalDate birthDate, String typeName) {
	}
}
