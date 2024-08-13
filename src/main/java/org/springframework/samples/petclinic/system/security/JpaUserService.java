package org.springframework.samples.petclinic.system.security;

import org.springframework.samples.petclinic.system.user.User;
import org.springframework.samples.petclinic.system.user.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

//@Component
public class JpaUserService implements UserDetailsService {

	private final UserRepository repository;

	public JpaUserService(UserRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.findByUsernameIgnoreCase(username)
			.orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", username)));

		return createUserDetails(user);
	}

	private UserDetails createUserDetails(User user) {
		List<GrantedAuthority> grantedAuthorities = user
			.getAuthorities()
			.stream()
			.map(authority -> new SimpleGrantedAuthority("ROLE_" + authority.getAuthority()))
			.collect(Collectors.toList());

		return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
			.password(user.getPassword())
			.authorities(grantedAuthorities)
			.disabled(!Boolean.TRUE.equals(user.getEnabled()))
			.build();
	}
}
