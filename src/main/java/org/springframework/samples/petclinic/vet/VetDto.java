package org.springframework.samples.petclinic.vet;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.Set;

/**
 * DTO for {@link org.springframework.samples.petclinic.vet.Vet}
 */
public class VetDto {
	private Integer id;
	@NotBlank
	private String firstName;
	@NotBlank
	private String lastName;
	private Set<Integer> specialtyIds;

	public VetDto() {
	}

	public VetDto(Integer id, String firstName, String lastName, Set<Integer> specialtyIds) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.specialtyIds = specialtyIds;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Set<Integer> getSpecialtyIds() {
		return specialtyIds;
	}

	public void setSpecialtyIds(Set<Integer> specialtyIds) {
		this.specialtyIds = specialtyIds;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VetDto entity = (VetDto) o;
		return Objects.equals(this.id, entity.id) &&
			   Objects.equals(this.firstName, entity.firstName) &&
			   Objects.equals(this.lastName, entity.lastName) &&
			   Objects.equals(this.specialtyIds, entity.specialtyIds);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, firstName, lastName, specialtyIds);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +
			   "id = " + id + ", " +
			   "firstName = " + firstName + ", " +
			   "lastName = " + lastName + ", " +
			   "specialtyIds = " + specialtyIds + ")";
	}
}
