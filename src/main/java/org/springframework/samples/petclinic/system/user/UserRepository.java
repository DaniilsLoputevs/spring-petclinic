package org.springframework.samples.petclinic.system.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	@EntityGraph(attributePaths = {"authorities"})
	Optional<User> findByUsernameIgnoreCase(String username);
}
