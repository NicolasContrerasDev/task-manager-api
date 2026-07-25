package com.portfolio.tareas.tareas_api.controllers;

import com.portfolio.tareas.tareas_api.dto.CreateWorkspaceRequest;
import com.portfolio.tareas.tareas_api.dto.UpdateWorkspaceMemberRoleRequest;
import com.portfolio.tareas.tareas_api.dto.WorkspaceMemberResponse;
import com.portfolio.tareas.tareas_api.dto.WorkspaceResponse;
import com.portfolio.tareas.tareas_api.services.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workspaces")
@CrossOrigin(origins = "*")
@Tag(name = "Areas de trabajo", description = "Endpoints para crear areas, unirse y administrar miembros")
public class WorkspaceController {

	private final WorkspaceService workspaceService;

	public WorkspaceController(WorkspaceService workspaceService) {
		this.workspaceService = workspaceService;
	}

	@PostMapping
	@Operation(summary = "Crear area de trabajo", description = "Crea un area y asigna al usuario autenticado como OWNER.")
	public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody CreateWorkspaceRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(workspaceService.createWorkspace(request));
	}

	@GetMapping
	@Operation(summary = "Listar mis areas", description = "Obtiene las areas donde participa el usuario autenticado.")
	public List<WorkspaceResponse> getMyWorkspaces() {
		return workspaceService.getMyWorkspaces();
	}

	@GetMapping("/{workspaceId}")
	@Operation(summary = "Ver area por ID", description = "Obtiene un area si el usuario pertenece a ella.")
	public ResponseEntity<WorkspaceResponse> getWorkspace(@PathVariable(name = "workspaceId") UUID workspaceId) {
		return ResponseEntity.ok(workspaceService.getWorkspace(workspaceId));
	}

	@PostMapping("/{workspaceId}/join")
	@Operation(summary = "Unirse a un area", description = "Une al usuario autenticado al area como USER usando el ID del area.")
	public ResponseEntity<WorkspaceMemberResponse> joinWorkspace(@PathVariable(name = "workspaceId") UUID workspaceId) {
		return ResponseEntity.status(HttpStatus.CREATED).body(workspaceService.joinWorkspace(workspaceId));
	}

	@GetMapping("/{workspaceId}/members")
	@Operation(summary = "Listar miembros", description = "Lista miembros si el usuario pertenece al area.")
	public List<WorkspaceMemberResponse> getMembers(@PathVariable(name = "workspaceId") UUID workspaceId) {
		return workspaceService.getMembers(workspaceId);
	}

	@PatchMapping("/{workspaceId}/members/{userId}/role")
	@Operation(summary = "Cambiar rol de miembro", description = "Solo OWNER o ADMIN pueden asignar ADMIN o USER.")
	public ResponseEntity<WorkspaceMemberResponse> updateMemberRole(
		@PathVariable(name = "workspaceId") UUID workspaceId,
		@PathVariable(name = "userId") UUID userId,
		@Valid @RequestBody UpdateWorkspaceMemberRoleRequest request
	) {
		return ResponseEntity.ok(workspaceService.updateMemberRole(workspaceId, userId, request));
	}
}
