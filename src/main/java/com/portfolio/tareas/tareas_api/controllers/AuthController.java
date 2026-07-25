package com.portfolio.tareas.tareas_api.controllers;

import com.portfolio.tareas.tareas_api.dto.AuthResponse;
import com.portfolio.tareas.tareas_api.dto.LoginRequest;
import com.portfolio.tareas.tareas_api.dto.RegisterRequest;
import com.portfolio.tareas.tareas_api.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticacion", description = "Endpoints para registro y login")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	@Operation(summary = "Registrar usuario", description = "Crea un usuario nuevo y devuelve un token JWT.")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}

	@PostMapping("/login")
	@Operation(summary = "Iniciar sesion", description = "Valida credenciales y devuelve un token JWT.")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}
}
