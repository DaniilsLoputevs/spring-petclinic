package org.springframework.samples.petclinic.vet;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public record VetFilter(String firstNameContains, String lastNameContains, String specialtiesNameContains) {
	public Specification<Vet> toSpecification() {
		return Specification.where(firstNameContainsSpec())
			.and(lastNameContainsSpec())
			.and(specialtiesNameContainsSpec());
	}

	private Specification<Vet> firstNameContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(firstNameContains)
			? cb.like(cb.lower(root.get("firstName")), "%" + firstNameContains.toLowerCase() + "%")
			: null);
	}

	private Specification<Vet> lastNameContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(lastNameContains)
			? cb.like(cb.lower(root.get("lastName")), "%" + lastNameContains.toLowerCase() + "%")
			: null);
	}

	private Specification<Vet> specialtiesNameContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(specialtiesNameContains)
			? cb.like(cb.lower(root.get("specialties")
			.get("name")), "%" + specialtiesNameContains.toLowerCase() + "%")
			: null);
	}
}
