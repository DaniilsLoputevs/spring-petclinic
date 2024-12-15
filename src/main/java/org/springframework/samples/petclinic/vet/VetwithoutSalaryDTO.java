package org.springframework.samples.petclinic.vet;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.Set;

/**
 * DTO for {@link Vet}
 */
public class VetwithoutSalaryDTO {
	private final Integer id;
	@NotBlank
	private final String firstName;
	@NotBlank
	private final String lastName;
	private final Set<SpecialtyDto> specialties;

	public VetwithoutSalaryDTO(Integer id, String firstName, String lastName, Set<SpecialtyDto> specialties) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.specialties = specialties;
	}

	public Integer getId() {return id;}

	public String getFirstName() {return firstName;}

	public String getLastName() {return lastName;}

	public Set<SpecialtyDto> getSpecialties() {return specialties;}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VetwithoutSalaryDTO entity = (VetwithoutSalaryDTO) o;
		return Objects.equals(this.id, entity.id) &&
			Objects.equals(this.firstName, entity.firstName) &&
			Objects.equals(this.lastName, entity.lastName) &&
			Objects.equals(this.specialties, entity.specialties);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, firstName, lastName, specialties);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +
			"id = " + id + ", " +
			"firstName = " + firstName + ", " +
			"lastName = " + lastName + ", " +
			"specialties = " + specialties + ")";
	}

	/**
	 * DTO for {@link Specialty}
	 */
	public static class SpecialtyDto {
		private final Integer id;
		private final String name;

		public SpecialtyDto(Integer id, String name) {
			this.id = id;
			this.name = name;
		}

		public Integer getId() {return id;}

		public String getName() {return name;}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			SpecialtyDto entity = (SpecialtyDto) o;
			return Objects.equals(this.id, entity.id) &&
				Objects.equals(this.name, entity.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(id, name);
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "(" +
				"id = " + id + ", " +
				"name = " + name + ")";
		}
	}
}
