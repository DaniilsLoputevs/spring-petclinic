package org.springframework.samples.petclinic.intergation.oportal;

import jakarta.validation.constraints.NotBlank;
import org.springframework.samples.petclinic.owner.Visit;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO for {@link Visit}
 */
public record VisitOPortalDto(LocalDate date, @NotBlank String description, VetDto vet) {
	/**
	 * DTO for {@link org.springframework.samples.petclinic.vet.Vet}
	 */
	public record VetDto(@NotBlank String firstName, @NotBlank String lastName,
						 Set<SpecialtyDto> specialties) {
		/**
		 * DTO for {@link org.springframework.samples.petclinic.vet.Specialty}
		 */
		public record SpecialtyDto(String name) {
		}
	}


}
