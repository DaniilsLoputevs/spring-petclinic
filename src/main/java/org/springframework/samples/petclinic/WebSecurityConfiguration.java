package org.springframework.samples.petclinic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.petclinic.system.user.UserRepository;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

	private final UserRepository userRepository;

	public WebSecurityConfiguration(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Bean
	public SecurityFilterChain filterChain1(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
			.anyRequest().authenticated());
		http.headers(Customizer.withDefaults());
		http.sessionManagement(Customizer.withDefaults());
		http.formLogin(Customizer.withDefaults());
		http.anonymous(Customizer.withDefaults());
		http.csrf(Customizer.withDefaults());
		http.userDetailsService(jpaUserDetailsService());
		return http.build();
	}

	public UserDetailsService jpaUserDetailsService() {
		return username -> {
			org.springframework.samples.petclinic.system.user.User user = userRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", username)));

			List<GrantedAuthority> grantedAuthorities = user
				.getAuthorities().stream()
				.map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
				.collect(Collectors.toList());

			return User.withUsername(user.getUsername())
				.password(user.getPassword())
				.authorities(grantedAuthorities)
				.disabled(!Boolean.TRUE.equals(user.getEnabled()))
				.build();
		};
	}
}
