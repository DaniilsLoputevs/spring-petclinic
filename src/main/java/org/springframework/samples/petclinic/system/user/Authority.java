package org.springframework.samples.petclinic.system.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;

@Entity @Table(name = "authority")
public class Authority implements GrantedAuthority {
	@Id @Column(name = "id", nullable = false) private Long id;

	@Column(name = "AUTHORITY", unique = true, length = 50) private String name;

	public Long getId() {return id;}

	public void setId(Long id) {this.id = id;}

	public String getName() {return name;}

	public void setName(String name) {this.name = name;}

	@Override
	public String getAuthority() {
		return name;
	}
}
