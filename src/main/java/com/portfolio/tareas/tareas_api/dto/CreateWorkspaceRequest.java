package com.portfolio.tareas.tareas_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWorkspaceRequest(
	@NotBlank(message = "El nombre del area es obligatorio")
	@Size(max = 100, message = "El nombre del area no puede superar los 100 caracteres")
	String name,

	@Size(max = 500, message = "La descripcion no puede superar los 500 caracteres")
	String description
) {
}
