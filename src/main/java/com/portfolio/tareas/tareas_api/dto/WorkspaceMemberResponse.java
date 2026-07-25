package com.portfolio.tareas.tareas_api.dto;

import com.portfolio.tareas.tareas_api.models.WorkspaceMember;
import java.time.LocalDateTime;
import java.util.UUID;

public record WorkspaceMemberResponse(
	UUID membershipId,
	UUID userId,
	String username,
	String email,
	String role,
	LocalDateTime joinedAt
) {

	public static WorkspaceMemberResponse from(WorkspaceMember member) {
		return new WorkspaceMemberResponse(
			member.getId(),
			member.getUser().getId(),
			member.getUser().getUsername(),
			member.getUser().getEmail(),
			member.getRole().name(),
			member.getJoinedAt()
		);
	}
}
