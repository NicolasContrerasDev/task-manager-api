package com.portfolio.tareas.tareas_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWorkspaceRequest(
	@NotBlank(message = "El nombre no puede estar vacio")
	@Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
	String name,

	String description
) {
}