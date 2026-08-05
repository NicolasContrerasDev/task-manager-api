package com.portfolio.tareas.tareas_api.dto;

import com.portfolio.tareas.tareas_api.models.WorkspaceJoinRequest;
import java.time.LocalDateTime;
import java.util.UUID;

public record WorkspaceJoinRequestResponse(
	UUID requestId,
	UUID workspaceId,
	UUID userId,
	String username,
	String email,
	String status,
	LocalDateTime requestedAt
) {

	public static WorkspaceJoinRequestResponse from(WorkspaceJoinRequest request) {
		return new WorkspaceJoinRequestResponse(
			request.getId(),
			request.getWorkspace().getId(),
			request.getUser().getId(),
			request.getUser().getUsername(),
			request.getUser().getEmail(),
			request.getStatus().name(),
			request.getRequestedAt()
		);
	}
}