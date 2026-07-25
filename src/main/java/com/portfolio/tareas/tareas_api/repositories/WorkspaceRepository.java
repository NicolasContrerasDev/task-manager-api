package com.portfolio.tareas.tareas_api.repositories;

import com.portfolio.tareas.tareas_api.models.Workspace;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
}
