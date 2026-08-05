package com.portfolio.tareas.tareas_api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;

import com.portfolio.tareas.tareas_api.dto.UpdateWorkspaceMemberRoleRequest;
import com.portfolio.tareas.tareas_api.models.WorkspaceRole;
import com.portfolio.tareas.tareas_api.repositories.UserRepository;
import com.portfolio.tareas.tareas_api.repositories.WorkspaceJoinRequestRepository;
import com.portfolio.tareas.tareas_api.repositories.WorkspaceMemberRepository;
import com.portfolio.tareas.tareas_api.repositories.WorkspaceRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class WorkspaceServiceTest {

	@Mock
	private WorkspaceRepository workspaceRepository;

	@Mock
	private WorkspaceMemberRepository workspaceMemberRepository;

	@Mock
	private WorkspaceJoinRequestRepository workspaceJoinRequestRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CurrentUserService currentUserService;

	@Test
	void updateMemberRoleRejectsOwnerAssignments() {
		WorkspaceService workspaceService = new WorkspaceService(
			workspaceRepository,
			workspaceMemberRepository,
			workspaceJoinRequestRepository,
			userRepository,
			currentUserService
		);

		assertThatThrownBy(() -> workspaceService.updateMemberRole(
				UUID.randomUUID(),
				UUID.randomUUID(),
				new UpdateWorkspaceMemberRoleRequest(WorkspaceRole.OWNER)
			))
			.isInstanceOfSatisfying(ResponseStatusException.class, ex ->
				assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
			);

		verifyNoInteractions(workspaceRepository, workspaceMemberRepository, workspaceJoinRequestRepository, userRepository, currentUserService);
	}
}