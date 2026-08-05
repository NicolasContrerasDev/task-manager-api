package com.portfolio.tareas.tareas_api.dto;

import com.portfolio.tareas.tareas_api.models.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateWorkspaceTaskRequest(
	@NotBlank(message = "El titulo no puede estar vacio")
	@Size(max = 100, message = "El titulo no puede superar los 100 caracteres")
	String title,

	String description,

	TaskStatus status,

	@NotNull(message = "El usuario asignado es obligatorio")
	UUID assignedUserId,

	LocalDateTime dueDate,

	String imageData
) {
}