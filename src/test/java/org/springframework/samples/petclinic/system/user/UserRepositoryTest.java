package org.springframework.samples.petclinic.system.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("postgres")
class UserRepositoryTest {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	void createUser() {
		Role role = new Role();
		role.setName("ADMIN");
		role = roleRepository.save(role);

		User user = new User();
		user.setUsername("user");
		user.setPassword("user");
		user.setEnabled(true);

		HashSet<Role> authorities = new HashSet<>();
		authorities.add(role);

		user.setAuthorities(authorities);

		userRepository.save(user);
	}
}
