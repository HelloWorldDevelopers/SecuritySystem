package com.rnt.SecuritySystem.securityconfig;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Vishal Dabade
 *
 */

@Getter
@Setter
@Data
public class UserDetail extends User {

	private static final long serialVersionUID = 338308531428207638L;

	private int userId;

	public UserDetail(String userName, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,
			int userId) {
		super(userName, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.userId = userId;
	}

	 

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
