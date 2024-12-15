package org.springframework.samples.petclinic.adminui;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public record PetFilter(String nameContains, LocalDate birthDateLte) {
	public Specification<Pet> toSpecification() {
		return Specification.where(nameContainsSpec())
			.and(birthDateLteSpec());
	}

	private Specification<Pet> nameContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(nameContains)
			? cb.like(root.get("name"), "%" + nameContains + "%")
			: null);
	}

	private Specification<Pet> birthDateLteSpec() {
		return ((root, query, cb) -> birthDateLte != null
			? cb.lessThanOrEqualTo(root.get("birthDate"), birthDateLte)
			: null);
	}
}
