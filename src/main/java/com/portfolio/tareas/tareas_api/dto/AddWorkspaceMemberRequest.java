package com.portfolio.tareas.tareas_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Identifies an existing account that should join a workspace. */
public record AddWorkspaceMemberRequest(
	@NotBlank(message = "Escribe un usuario o correo")
	@Size(max = 100, message = "El usuario o correo es demasiado largo")
	String identifier
) {
}
