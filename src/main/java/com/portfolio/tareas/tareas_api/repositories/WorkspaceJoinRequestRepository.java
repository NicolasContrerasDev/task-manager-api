package com.portfolio.tareas.tareas_api.repositories;

import com.portfolio.tareas.tareas_api.models.JoinRequestStatus;
import com.portfolio.tareas.tareas_api.models.WorkspaceJoinRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceJoinRequestRepository extends JpaRepository<WorkspaceJoinRequest, UUID> {

	@EntityGraph(attributePaths = {"user"})
	List<WorkspaceJoinRequest> findByWorkspaceIdAndStatusOrderByRequestedAtAsc(UUID workspaceId, JoinRequestStatus status);

	Optional<WorkspaceJoinRequest> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

	boolean existsByWorkspaceIdAndUserIdAndStatus(UUID workspaceId, UUID userId, JoinRequestStatus status);
}