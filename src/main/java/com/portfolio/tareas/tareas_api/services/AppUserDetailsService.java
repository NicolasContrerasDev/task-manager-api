package com.portfolio.tareas.tareas_api.services;

import com.portfolio.tareas.tareas_api.models.AppUser;
import com.portfolio.tareas.tareas_api.repositories.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public AppUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
		AppUser user = userRepository
			.findByUsernameIgnoreCaseOrEmailIgnoreCase(usernameOrEmail, usernameOrEmail)
			.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

		return User
			.withUsername(user.getUsername())
			.password(user.getPasswordHash())
			.authorities("ROLE_" + user.getRole().name())
			.build();
	}
}
