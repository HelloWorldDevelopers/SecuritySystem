package com.rnt.SecuritySystem.service;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rnt.SecuritySystem.entity.EmployeeMaster;
import com.rnt.SecuritySystem.repo.UserInfoRepo;
import com.rnt.SecuritySystem.securityconfig.UserDetail;
 

/**
 * @author Vishal Dabade
 *
 */
@Service
public class CustomeUserDetailsService implements UserDetailsService {

	@Autowired
	UserInfoRepo userInfoRepo;

	@Override
	public UserDetail loadUserByUsername(String email) throws UsernameNotFoundException {
		try {
			EmployeeMaster optionalUser = userInfoRepo.findByEmail(email).orElse(null);
			if (Objects.nonNull(optionalUser))
				return new UserDetail(optionalUser.getEmail(), optionalUser.getPassword(), true, true, true, true,
						maGrantedAuthorities(), optionalUser.getStaffId());
		} catch (Exception e) {
		}
		return null;
	}

	private Collection<? extends GrantedAuthority> maGrantedAuthorities() {
		List<String> list = Arrays.asList("User", "Admin");
		return list.stream().map(e -> new SimpleGrantedAuthority(e)).collect(Collectors.toList());

	}
}
