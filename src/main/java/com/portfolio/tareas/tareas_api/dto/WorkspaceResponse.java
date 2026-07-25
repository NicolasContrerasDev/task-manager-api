package com.portfolio.tareas.tareas_api.dto;

import com.portfolio.tareas.tareas_api.models.Workspace;
import com.portfolio.tareas.tareas_api.models.WorkspaceRole;
import java.time.LocalDateTime;
import java.util.UUID;

public record WorkspaceResponse(
	UUID id,
	String name,
	String description,
	UUID ownerId,
	String ownerUsername,
	String myRole,
	LocalDateTime createdAt
) {

	public static WorkspaceResponse from(Workspace workspace, WorkspaceRole currentUserRole) {
		return new WorkspaceResponse(
			workspace.getId(),
			workspace.getName(),
			workspace.getDescription(),
			workspace.getCreatedBy().getId(),
			workspace.getCreatedBy().getUsername(),
			currentUserRole.name(),
			workspace.getCreatedAt()
		);
	}
}
