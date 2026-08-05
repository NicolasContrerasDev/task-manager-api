package com.portfolio.tareas.tareas_api.controllers;

import com.portfolio.tareas.tareas_api.dto.AuthResponse;
import com.portfolio.tareas.tareas_api.dto.AuthSession;
import com.portfolio.tareas.tareas_api.dto.LoginRequest;
import com.portfolio.tareas.tareas_api.dto.RegisterRequest;
import com.portfolio.tareas.tareas_api.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacion", description = "Endpoints para registro y login")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	@Operation(summary = "Registrar usuario", description = "Crea un usuario nuevo y envía el JWT en el encabezado Authorization.")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return authenticatedResponse(HttpStatus.CREATED, "Registro completado", authService.register(request));
	}

	@PostMapping("/login")
	@Operation(summary = "Iniciar sesion", description = "Valida credenciales y envía el JWT en el encabezado Authorization.")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return authenticatedResponse(HttpStatus.OK, "Inicio de sesion exitoso", authService.login(request));
	}

	private ResponseEntity<AuthResponse> authenticatedResponse(HttpStatus status, String message, AuthSession session) {
		return ResponseEntity.status(status)
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + session.token())
			.body(new AuthResponse(message));
	}
}
