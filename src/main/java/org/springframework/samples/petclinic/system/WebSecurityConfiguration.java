package org.springframework.samples.petclinic.system;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.samples.petclinic.system.security.UserRepository;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

	private final UserRepository repository;
	private final ClientRegistrationRepository clientRegistrationRepository;

	public WebSecurityConfiguration(UserRepository repository, ClientRegistrationRepository clientRegistrationRepository) {
		this.repository = repository;
		this.clientRegistrationRepository = clientRegistrationRepository;
	}

	@Bean
	@Order(99)
	public SecurityFilterChain legacyui(HttpSecurity http) throws Exception {
		http.securityMatcher("/**");
		http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
			.anyRequest().authenticated());
		http.userDetailsService(jpaUserDetailsService());
		http.headers(Customizer.withDefaults());
		http.sessionManagement(Customizer.withDefaults());
		http.formLogin(Customizer.withDefaults());

		http.oauth2Login(o2l -> o2l
			.userInfoEndpoint(uie -> uie.userAuthoritiesMapper(userAuthoritiesMapper())));
		http.logout(logout -> logout
			.logoutSuccessHandler(oidcClientInitiatedLogoutSuccessHandler()));
		http.anonymous(Customizer.withDefaults());
		http.csrf(CsrfConfigurer::disable);
		return http.build();
	}

	@Bean
	public UserDetailsService jpaUserDetailsService() {
		return username -> {
			org.springframework.samples.petclinic.system.security.User user = repository.findByUsernameIgnoreCase(username)
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


	public GrantedAuthoritiesMapper userAuthoritiesMapper() {
		return (authorities) -> {
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
			authorities.forEach(authority -> {

				if (!(authority instanceof OidcUserAuthority oidcUserAuthority)) {
					return;
				}

				// noinspection unchecked
				Optional.ofNullable(oidcUserAuthority.getAttributes().get("resource_access"))
					.map(ra -> ((Map<String, ?>) ra).get("legacyui"))
					.map(sbLegacy -> ((Map<String, ?>) sbLegacy).get("roles"))
					.ifPresent(roles -> ((List<String>) roles).stream()
						.map(r -> new SimpleGrantedAuthority("ROLE_" + r))
						.forEach(mappedAuthorities::add));
			});
			return mappedAuthorities;
		};
	}

	OidcClientInitiatedLogoutSuccessHandler oidcClientInitiatedLogoutSuccessHandler() {
		OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
		successHandler.setPostLogoutRedirectUri("http://localhost:8080/");
		return successHandler;
	}

	@Bean
	@Order(10)
	public SecurityFilterChain oportal(HttpSecurity http) throws Exception {
		http.securityMatcher("/oportal/**");
		http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
			.anyRequest().authenticated());
		http.headers(Customizer.withDefaults());
		http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
			.jwt(jwt -> jwt
				.jwkSetUri("http://localhost:9082/realms/petclinic/protocol/openid-connect/certs")
				.jwtAuthenticationConverter(jwtAuthenticationConverter())));
		http.anonymous(Customizer.withDefaults());
		http.csrf(Customizer.withDefaults());
		return http.build();
	}

	@Bean
	@Order(20)
	public SecurityFilterChain adminui(HttpSecurity http) throws Exception {
		http.securityMatcher("/admin/**");
		http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
			.anyRequest().authenticated());
		http.headers(Customizer.withDefaults());
		http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
			.jwt(jwt -> jwt
				.jwkSetUri("http://localhost:9082/realms/petclinic-backoffice/protocol/openid-connect/certs")
				.jwtAuthenticationConverter(jwtAuthenticationConverter())));
		http.anonymous(Customizer.withDefaults());
		http.csrf(Customizer.withDefaults());
		return http.build();
	}

	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
		grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

		return jwtAuthenticationConverter;
	}

//	private class JpaUserDetailsService implements UserDetailsService {
//
//		@Override
//		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//			org.springframework.samples.petclinic.system.security.User user = repository.findByUsernameIgnoreCase(username)
//				.orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", username)));
//
//			return createUserDetails(user);
//		}
//
//		private UserDetails createUserDetails(org.springframework.samples.petclinic.system.security.User user) {
//			List<GrantedAuthority> grantedAuthorities = user
//				.getAuthorities()
//				.stream()
//				.map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
//				.collect(Collectors.toList());
//
//			return User.withUsername(user.getUsername())
//				.password(user.getPassword())
//				.authorities(grantedAuthorities)
//				.disabled(!Boolean.TRUE.equals(user.getEnabled()))
//				.build();
//		}
//	}
}
