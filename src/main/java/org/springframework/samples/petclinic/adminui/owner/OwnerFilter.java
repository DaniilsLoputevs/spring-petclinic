package org.springframework.samples.petclinic.adminui.owner;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.util.StringUtils;

public record OwnerFilter(String firstNameContains, String lastNameStarts, String telephoneContains) {
	public Specification<Owner> toSpecification() {
		return Specification.where(firstNameContainsSpec())
			.and(lastNameStartsSpec())
			.and(telephoneContainsSpec());
	}

	private Specification<Owner> firstNameContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(firstNameContains)
			? cb.like(cb.lower(root.get("firstName")), "%" + firstNameContains.toLowerCase() + "%")
			: null);
	}

	private Specification<Owner> lastNameStartsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(lastNameStarts)
			? cb.like(cb.lower(root.get("lastName")), lastNameStarts.toLowerCase() + "%")
			: null);
	}

	private Specification<Owner> telephoneContainsSpec() {
		return ((root, query, cb) -> StringUtils.hasText(telephoneContains)
			? cb.like(root.get("telephone"), "%" + telephoneContains + "%")
			: null);
	}
}
