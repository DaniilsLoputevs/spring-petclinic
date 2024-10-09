package org.springframework.samples.petclinic.owner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link Owner}
 */
public record OwnerWithPetDto(Integer id, @NotBlank String firstName, @NotBlank String lastName,
							  @NotBlank String address, @NotBlank String city,
							  @Pattern(message = "Telephone must be a 10-digit number", regexp = "\\d{10}") @NotBlank String telephone,
							  List<PetDto> pets) {
	/**
	 * DTO for {@link Pet}
	 */
	public record PetDto(Integer id, String name, LocalDate birthDate, String typeName) {
	}
}
