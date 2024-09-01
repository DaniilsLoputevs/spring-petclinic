package org.springframework.samples.petclinic.adminui.vet;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.util.StringUtils;

public record VetFilter(String firstNameContains, String lastNameContains) {
	public Specification<Vet> toSpecification() {
		return Specification.where(firstNameContainsSpec())
			.and(lastNameContainsSpec());
	}

	private Specification<Vet> firstNameContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(firstNameContains)
			? cb.like(root.get("firstName"), "%" + firstNameContains + "%")
			: null);
	}

	private Specification<Vet> lastNameContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(lastNameContains)
			? cb.like(root.get("lastName"), "%" + lastNameContains + "%")
			: null);
	}
}
