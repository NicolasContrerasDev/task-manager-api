package com.portfolio.tareas.tareas_api.dto;

import com.portfolio.tareas.tareas_api.models.WorkspaceRole;
import jakarta.validation.constraints.NotNull;

public record UpdateWorkspaceMemberRoleRequest(
	@NotNull(message = "El rol es obligatorio")
	WorkspaceRole role
) {
}
