package com.portfolio.tareas.tareas_api.services;

import com.portfolio.tareas.tareas_api.dto.CreateWorkspaceRequest;
import com.portfolio.tareas.tareas_api.dto.UpdateWorkspaceMemberRoleRequest;
import com.portfolio.tareas.tareas_api.dto.WorkspaceMemberResponse;
import com.portfolio.tareas.tareas_api.dto.WorkspaceResponse;
import com.portfolio.tareas.tareas_api.models.AppUser;
import com.portfolio.tareas.tareas_api.models.Workspace;
import com.portfolio.tareas.tareas_api.models.WorkspaceMember;
import com.portfolio.tareas.tareas_api.models.WorkspaceRole;
import com.portfolio.tareas.tareas_api.repositories.WorkspaceMemberRepository;
import com.portfolio.tareas.tareas_api.repositories.WorkspaceRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WorkspaceService {

	private final WorkspaceRepository workspaceRepository;
	private final WorkspaceMemberRepository workspaceMemberRepository;
	private final CurrentUserService currentUserService;

	public WorkspaceService(
		WorkspaceRepository workspaceRepository,
		WorkspaceMemberRepository workspaceMemberRepository,
		CurrentUserService currentUserService
	) {
		this.workspaceRepository = workspaceRepository;
		this.workspaceMemberRepository = workspaceMemberRepository;
		this.currentUserService = currentUserService;
	}

	@Transactional
	public WorkspaceResponse createWorkspace(CreateWorkspaceRequest request) {
		AppUser currentUser = currentUserService.getCurrentUser();
		Workspace workspace = new Workspace(
			request.name().trim(),
			normalizeDescription(request.description()),
			currentUser
		);
		Workspace savedWorkspace = workspaceRepository.save(workspace);
		workspaceMemberRepository.save(new WorkspaceMember(savedWorkspace, currentUser, WorkspaceRole.OWNER));

		return WorkspaceResponse.from(savedWorkspace, WorkspaceRole.OWNER);
	}

	@Transactional(readOnly = true)
	public List<WorkspaceResponse> getMyWorkspaces() {
		AppUser currentUser = currentUserService.getCurrentUser();
		return workspaceMemberRepository
			.findByUserIdOrderByJoinedAtDesc(currentUser.getId())
			.stream()
			.map(member -> WorkspaceResponse.from(member.getWorkspace(), member.getRole()))
			.toList();
	}

	@Transactional(readOnly = true)
	public WorkspaceResponse getWorkspace(UUID workspaceId) {
		WorkspaceMember membership = requireCurrentUserMembership(workspaceId);
		return WorkspaceResponse.from(membership.getWorkspace(), membership.getRole());
	}

	@Transactional
	public WorkspaceMemberResponse joinWorkspace(UUID workspaceId) {
		AppUser currentUser = currentUserService.getCurrentUser();
		Workspace workspace = findWorkspaceOrThrow(workspaceId);

		if (workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, currentUser.getId())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya pertenece a esta area de trabajo");
		}

		WorkspaceMember member = workspaceMemberRepository.save(
			new WorkspaceMember(workspace, currentUser, WorkspaceRole.USER)
		);

		return WorkspaceMemberResponse.from(member);
	}

	@Transactional(readOnly = true)
	public List<WorkspaceMemberResponse> getMembers(UUID workspaceId) {
		requireCurrentUserMembership(workspaceId);
		return workspaceMemberRepository
			.findByWorkspaceIdOrderByJoinedAtAsc(workspaceId)
			.stream()
			.map(WorkspaceMemberResponse::from)
			.toList();
	}

	@Transactional
	public WorkspaceMemberResponse updateMemberRole(
		UUID workspaceId,
		UUID userId,
		UpdateWorkspaceMemberRoleRequest request
	) {
		if (request.role() == WorkspaceRole.OWNER) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol OWNER solo pertenece al duenio del area");
		}

		WorkspaceMember actorMembership = requireCurrentUserMembership(workspaceId);
		if (!actorMembership.getRole().canManageMembers()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo los administradores pueden cambiar roles");
		}

		WorkspaceMember targetMembership = workspaceMemberRepository
			.findByWorkspaceIdAndUserId(workspaceId, userId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no pertenece a esta area"));

		if (targetMembership.getRole() == WorkspaceRole.OWNER) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar al duenio del area");
		}

		targetMembership.setRole(request.role());
		return WorkspaceMemberResponse.from(workspaceMemberRepository.save(targetMembership));
	}

	@Transactional(readOnly = true)
	public WorkspaceMember requireCurrentUserMembership(UUID workspaceId) {
		AppUser currentUser = currentUserService.getCurrentUser();
		return workspaceMemberRepository
			.findByWorkspaceIdAndUserId(workspaceId, currentUser.getId())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No perteneces a esta area"));
	}

	@Transactional(readOnly = true)
	public WorkspaceMember requireTaskManager(UUID workspaceId) {
		WorkspaceMember membership = requireCurrentUserMembership(workspaceId);
		if (!membership.getRole().canManageTasks()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo duenios o administradores pueden gestionar tareas");
		}
		return membership;
	}

	@Transactional(readOnly = true)
	public void requireUserIsMember(UUID workspaceId, UUID userId) {
		if (!workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario asignado no pertenece a esta area");
		}
	}

	private Workspace findWorkspaceOrThrow(UUID workspaceId) {
		return workspaceRepository
			.findById(workspaceId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Area de trabajo no encontrada"));
	}

	private String normalizeDescription(String description) {
		if (description == null || description.isBlank()) {
			return null;
		}
		return description.trim();
	}
}
