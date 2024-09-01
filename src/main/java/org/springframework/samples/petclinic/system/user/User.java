package org.springframework.samples.petclinic.system.user;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "user_", indexes = {
	@Index(name = "idx_securityuser_username", columnList = "USERNAME")
})
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
	@SequenceGenerator(name = "user_seq")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "USERNAME", unique = true, length = 50)
	private String username;

	@Column(name = "PASSWORD", length = 500)
	private String password;

	@Column(name = "ENABLED")
	private Boolean enabled;

	@ManyToMany
	@JoinTable(joinColumns = @JoinColumn(name = "user_id"))
	@BatchSize(size = 50)
	private Set<Authority> authorities = new LinkedHashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Set<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
