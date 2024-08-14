package org.springframework.samples.petclinic.system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.samples.petclinic.system.user.User;
import org.springframework.samples.petclinic.system.user.UserRepository;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

	private final UserRepository userRepository;

	public WebSecurityConfiguration(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Bean
	@Order(99)
	public SecurityFilterChain legacy(HttpSecurity http) throws Exception {
		return http.securityMatcher("/**")
			.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
//				.requestMatchers("/vets.html").hasRole("MANAGER")
				.requestMatchers("/vets").permitAll()
				.anyRequest().hasRole("legacy")) // ??
			.userDetailsService(formLoginUserService())
			.headers(Customizer.withDefaults())
			.sessionManagement(Customizer.withDefaults())
			.formLogin(Customizer.withDefaults())
			.oauth2Login(o2l -> o2l.userInfoEndpoint(uie -> uie.userAuthoritiesMapper(userAuthoritiesMapper())))
			.anonymous(AnonymousConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.build();
	}

	public GrantedAuthoritiesMapper userAuthoritiesMapper() {
		return (authorities) -> {
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
			authorities.forEach(authority -> {
				if (!(authority instanceof OidcUserAuthority oidcUserAuthority)) {
					return;
				}

				// noinspection unchecked
				Optional.ofNullable(oidcUserAuthority.getAttributes().get("resource_access"))
					.map(ra -> ((Map<String, ?>) ra).get("sb-legacy"))
					.map(sbLegacy -> ((Map<String, ?>) sbLegacy).get("roles"))
					.ifPresent(roles -> ((List<String>) roles).stream()
						.map(r -> new SimpleGrantedAuthority("ROLE_" + r))
						.forEach(mappedAuthorities::add));
			});
			return mappedAuthorities;
		};
	}

	@Bean
	public UserDetailsService formLoginUserService() {
		return new UserDetailsService() {

				@Override
				@Transactional
				public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
					User user = userRepository.findByUsernameIgnoreCase(username)
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
		};

	}

	@Bean
	@Order(10)
	public SecurityFilterChain integration(HttpSecurity http) throws Exception {
		return http.securityMatcher("/rest/**")
			.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
				.anyRequest().authenticated())
			.sessionManagement(sessionManagement -> sessionManagement
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.headers(Customizer.withDefaults())
			.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
				.jwt(jwt -> jwt
					.jwkSetUri("http://localhost:9082/realms/petclinic/protocol/openid-connect/certs")
					.jwtAuthenticationConverter(jwtAuthenticationConverter())))
			.anonymous(AnonymousConfigurer::disable)
			.csrf(CsrfConfigurer::disable).build();
	}

	@Bean
	@Order(20)
	public SecurityFilterChain admin(HttpSecurity http) throws Exception {
		return http.securityMatcher("/admin/**")
			.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
				.anyRequest().authenticated())
			.sessionManagement(sessionManagement -> sessionManagement
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.headers(Customizer.withDefaults()) // what is that?
			.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
				.jwt(jwt -> jwt
					.jwkSetUri("http://localhost:9082/realms/petclinic-backoffice/protocol/openid-connect/certs")
					.jwtAuthenticationConverter(jwtAuthenticationConverter())))
			.anonymous(AnonymousConfigurer::disable)
			.csrf(CsrfConfigurer::disable).build();
	}

	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
		grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

		return jwtAuthenticationConverter;
	}

}
