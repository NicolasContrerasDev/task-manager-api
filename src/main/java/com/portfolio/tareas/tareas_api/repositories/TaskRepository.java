package com.portfolio.tareas.tareas_api.repositories;

import com.portfolio.tareas.tareas_api.models.Task;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByAssignedToIdOrderByCreatedAtDesc(UUID assignedToId);

    List<Task> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);

    List<Task> findByWorkspaceIdAndAssignedToIdOrderByCreatedAtDesc(UUID workspaceId, UUID assignedToId);

    Optional<Task> findByIdAndWorkspaceId(UUID taskId, UUID workspaceId);
}
