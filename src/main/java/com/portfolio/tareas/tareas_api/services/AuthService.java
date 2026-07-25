package com.portfolio.tareas.tareas_api.services;

import com.portfolio.tareas.tareas_api.dto.AuthResponse;
import com.portfolio.tareas.tareas_api.dto.LoginRequest;
import com.portfolio.tareas.tareas_api.dto.RegisterRequest;
import com.portfolio.tareas.tareas_api.dto.UserResponse;
import com.portfolio.tareas.tareas_api.models.AppUser;
import com.portfolio.tareas.tareas_api.repositories.UserRepository;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthService(
		UserRepository userRepository,
		PasswordEncoder passwordEncoder,
		AuthenticationManager authenticationManager,
		JwtService jwtService
	) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		String username = request.username().trim();
		String email = request.email().trim().toLowerCase(Locale.ROOT);

		if (userRepository.existsByUsernameIgnoreCase(username)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya esta registrado");
		}

		if (userRepository.existsByEmailIgnoreCase(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya esta registrado");
		}

		AppUser user = new AppUser(username, email, passwordEncoder.encode(request.password()));
		AppUser savedUser = userRepository.save(user);
		return buildAuthResponse(savedUser);
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		String usernameOrEmail = request.usernameOrEmail().trim();

		try {
			authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(usernameOrEmail, request.password())
			);
		} catch (BadCredentialsException ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas");
		}

		AppUser user = userRepository
			.findByUsernameIgnoreCaseOrEmailIgnoreCase(usernameOrEmail, usernameOrEmail)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas"));

		return buildAuthResponse(user);
	}

	private AuthResponse buildAuthResponse(AppUser user) {
		String token = jwtService.generateToken(user);
		return AuthResponse.bearer(token, jwtService.getExpirationMs(), UserResponse.from(user));
	}
}
