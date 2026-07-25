package com.portfolio.tareas.tareas_api.dto;

import com.portfolio.tareas.tareas_api.models.AppUser;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
	UUID id,
	String username,
	String email,
	String role,
	LocalDateTime createdAt
) {

	public static UserResponse from(AppUser user) {
		return new UserResponse(
			user.getId(),
			user.getUsername(),
			user.getEmail(),
			user.getRole().name(),
			user.getCreatedAt()
		);
	}
}
