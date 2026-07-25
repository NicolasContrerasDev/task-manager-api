package com.portfolio.tareas.tareas_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
	@NotBlank(message = "El usuario o email es obligatorio")
	@Size(max = 120, message = "El usuario o email no puede superar los 120 caracteres")
	String usernameOrEmail,

	@NotBlank(message = "La contrasena es obligatoria")
	@Size(min = 9, max = 128, message = "La contrasena debe tener mas de 8 caracteres")
	String password
) {
}
