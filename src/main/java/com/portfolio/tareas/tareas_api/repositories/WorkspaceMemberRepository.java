package com.portfolio.tareas.tareas_api.repositories;

import com.portfolio.tareas.tareas_api.models.WorkspaceMember;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, UUID> {

	@EntityGraph(attributePaths = {"workspace", "workspace.createdBy"})
	List<WorkspaceMember> findByUserIdOrderByJoinedAtDesc(UUID userId);

	@EntityGraph(attributePaths = {"workspace", "workspace.createdBy", "user"})
	Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

	@EntityGraph(attributePaths = {"user"})
	List<WorkspaceMember> findByWorkspaceIdOrderByJoinedAtAsc(UUID workspaceId);

	boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);
}
