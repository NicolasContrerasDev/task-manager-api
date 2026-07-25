package com.portfolio.tareas.tareas_api.services;

import com.portfolio.tareas.tareas_api.models.AppUser;
import com.portfolio.tareas.tareas_api.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrentUserService {

	private final UserRepository userRepository;

	public CurrentUserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public AppUser getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
		}

		String username = authentication.getName();
		return userRepository
			.findByUsernameIgnoreCaseOrEmailIgnoreCase(username, username)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
	}
}
