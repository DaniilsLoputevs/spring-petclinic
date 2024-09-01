package org.springframework.samples.petclinic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.samples.petclinic.system.user.UserRepository;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

	private final UserRepository userRepository;

	private final ClientRegistrationRepository clientRegistrationRepository;

	private final String adminuiPetclinicJwkSetUri;

	private final String springPetclinicJwkSetUri;

	public WebSecurityConfiguration(UserRepository userRepository,
									ClientRegistrationRepository clientRegistrationRepository,
									@Value("${spring-petclinic.adminui.oauth2.resourceserver.jwt.issuer-uri}") String adminuiPetclinicJwkSetUri,
									@Value("${spring-petclinic.api.oauth2.resourceserver.jwt.jwk-set-uri}") String springPetclinicJwkSetUri) {
		this.userRepository = userRepository;
		this.clientRegistrationRepository = clientRegistrationRepository;
		this.adminuiPetclinicJwkSetUri = adminuiPetclinicJwkSetUri;
		this.springPetclinicJwkSetUri = springPetclinicJwkSetUri;
	}

	@Bean
	@Order(99)
	public SecurityFilterChain legacyUi(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
			.anyRequest().authenticated());
		http.headers(Customizer.withDefaults());
		http.sessionManagement(Customizer.withDefaults());
		http.formLogin(Customizer.withDefaults());
		http.oauth2Login(Customizer.withDefaults());
		http.logout(logout -> logout
			.logoutSuccessHandler(oidcClientInitiatedLogoutSuccessHandler()));
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

	OidcClientInitiatedLogoutSuccessHandler oidcClientInitiatedLogoutSuccessHandler() {
		OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
		successHandler.setPostLogoutRedirectUri("http://localhost:8080/");
		return successHandler;
	}

	public GrantedAuthoritiesMapper userAuthoritiesMapper() {
		return authorities -> {
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


	@Bean
	@Order(10)
	public SecurityFilterChain adminui(HttpSecurity http) throws Exception {
		http.securityMatcher("/adminui/**");
		http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
			.anyRequest().authenticated());
		http.headers(Customizer.withDefaults());
		http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
			.jwt(jwt -> jwt
				.jwkSetUri(adminuiPetclinicJwkSetUri)
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


	@Bean
	@Order(20)
	public SecurityFilterChain api(HttpSecurity http) throws Exception {
		http.securityMatcher("/api/**");
		http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
			.anyRequest().authenticated());
		http.headers(Customizer.withDefaults());
		http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
			.jwt(jwt -> jwt
				.jwkSetUri(springPetclinicJwkSetUri)
				.jwtAuthenticationConverter(jwtAuthenticationConverter())));
		http.anonymous(Customizer.withDefaults());
		http.csrf(Customizer.withDefaults());
		return http.build();
	}
}
