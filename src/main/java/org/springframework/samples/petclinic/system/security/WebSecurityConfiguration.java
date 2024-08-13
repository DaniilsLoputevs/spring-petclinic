package org.springframework.samples.petclinic.system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

	private final JpaUserService jpaUserService;

	public WebSecurityConfiguration(JpaUserService jpaUserService) {
		this.jpaUserService = jpaUserService;
	}

	@Bean
	@Order(99)
	public SecurityFilterChain legacy(HttpSecurity http) throws Exception {
		return http.securityMatcher("/**")
			.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
				.requestMatchers("/vets.html").hasRole("MANAGER")
				.requestMatchers("/vets").permitAll()
				.anyRequest().hasRole("LEGACY")) // ??
			.userDetailsService(jpaUserService)
			.headers(Customizer.withDefaults())
			.sessionManagement(Customizer.withDefaults())
			.formLogin(Customizer.withDefaults())
			.anonymous(AnonymousConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.build();
	}

	@Bean
	@Order(10)
	public SecurityFilterChain microservice(HttpSecurity http) throws Exception {
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
