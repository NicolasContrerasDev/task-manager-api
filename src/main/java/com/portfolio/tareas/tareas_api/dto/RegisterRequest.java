package com.portfolio.tareas.tareas_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
	@NotBlank(message = "El nombre de usuario es obligatorio")
	@Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
	@Pattern(
		regexp = "^[A-Za-z0-9._-]+$",
		message = "El nombre de usuario solo puede contener letras, numeros, puntos, guiones y guiones bajos"
	)
	String username,

	@NotBlank(message = "El email es obligatorio")
	@Email(message = "El email no tiene un formato valido")
	@Size(max = 120, message = "El email no puede superar los 120 caracteres")
	String email,

	@NotBlank(message = "La contrasena es obligatoria")
	@Size(min = 9, max = 128, message = "La contrasena debe tener mas de 8 caracteres")
	String password
) {
}
