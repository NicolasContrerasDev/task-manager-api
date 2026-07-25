package com.portfolio.tareas.tareas_api.dto;

import com.portfolio.tareas.tareas_api.models.Task;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(
    UUID id,
    String title,
    String description,
    String status,
    UUID workspaceId,
    String workspaceName,
    UserResponse assignedTo,
    LocalDateTime createdAt
) {

    public static TaskResponse from(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus().name(),
            task.getWorkspace() != null ? task.getWorkspace().getId() : null,
            task.getWorkspace() != null ? task.getWorkspace().getName() : null,
            task.getAssignedTo() != null ? UserResponse.from(task.getAssignedTo()) : null,
            task.getCreatedAt()
        );
    }
}