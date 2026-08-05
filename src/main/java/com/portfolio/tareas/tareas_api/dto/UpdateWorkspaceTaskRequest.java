package com.portfolio.tareas.tareas_api.dto;

import com.portfolio.tareas.tareas_api.models.TaskStatus;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateWorkspaceTaskRequest(
	@Size(max = 100, message = "El titulo no puede superar los 100 caracteres")
	String title,

	String description,

	TaskStatus status,

	UUID assignedUserId,

	LocalDateTime dueDate,

	Boolean clearDueDate,

	String imageData,

	Boolean clearImage
) {
}