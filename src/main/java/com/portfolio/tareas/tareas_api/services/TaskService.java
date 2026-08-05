package com.portfolio.tareas.tareas_api.services;

import com.portfolio.tareas.tareas_api.dto.CreateWorkspaceTaskRequest;
import com.portfolio.tareas.tareas_api.dto.TaskResponse;
import com.portfolio.tareas.tareas_api.dto.UpdateWorkspaceTaskRequest;
import com.portfolio.tareas.tareas_api.models.AppUser;
import com.portfolio.tareas.tareas_api.models.Task;
import com.portfolio.tareas.tareas_api.models.TaskStatus;
import com.portfolio.tareas.tareas_api.models.WorkspaceMember;
import com.portfolio.tareas.tareas_api.models.WorkspaceRole;
import com.portfolio.tareas.tareas_api.repositories.TaskRepository;
import com.portfolio.tareas.tareas_api.repositories.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final WorkspaceService workspaceService;
    private final CurrentUserService currentUserService;
    private final TaskExportService taskExportService;

    public TaskService(
        TaskRepository taskRepository,
        UserRepository userRepository,
        WorkspaceService workspaceService,
        CurrentUserService currentUserService,
        TaskExportService taskExportService
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.workspaceService = workspaceService;
        this.currentUserService = currentUserService;
        this.taskExportService = taskExportService;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAssignedTasksForCurrentUser() {
        AppUser currentUser = currentUserService.getCurrentUser();
        return taskRepository
            .findByAssignedToIdOrderByCreatedAtDesc(currentUser.getId())
            .stream()
            .map(TaskResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskForCurrentUser(UUID id) {
        Task task = findTaskOrThrow(id);
        ensureCurrentUserCanView(task);
        return TaskResponse.from(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getWorkspaceTasks(UUID workspaceId) {
        workspaceService.requireCurrentUserMembership(workspaceId);
        return taskRepository
            .findByWorkspaceIdOrderByCreatedAtDesc(workspaceId)
            .stream()
            .map(TaskResponse::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getWorkspaceTask(UUID workspaceId, UUID taskId) {
        workspaceService.requireCurrentUserMembership(workspaceId);
        return TaskResponse.from(findWorkspaceTaskOrThrow(workspaceId, taskId));
    }

    @Transactional
    public TaskResponse createWorkspaceTask(UUID workspaceId, CreateWorkspaceTaskRequest request) {
        WorkspaceMember membership = workspaceService.requireTaskManager(workspaceId);
        AppUser assignedUser = findAssignedUser(membership, request.assignedUserId());

        Task task = new Task();
        task.setTitle(request.title().trim());
        task.setDescription(normalizeDescription(request.description()));
        task.setStatus(request.status() != null ? request.status() : TaskStatus.PENDING);
        task.setWorkspace(membership.getWorkspace());
        task.setAssignedTo(assignedUser);
        task.setDueDate(request.dueDate());
        task.setImageData(request.imageData());

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateWorkspaceTask(UUID workspaceId, UUID taskId, UpdateWorkspaceTaskRequest request) {
        WorkspaceMember membership = workspaceService.requireTaskManager(workspaceId);
        Task task = findWorkspaceTaskOrThrow(workspaceId, taskId);

        if (request.title() != null && !request.title().isBlank()) {
            task.setTitle(request.title().trim());
        }

        if (request.description() != null) {
            task.setDescription(normalizeDescription(request.description()));
        }

        if (request.status() != null) {
            task.setStatus(request.status());
        }

        if (request.assignedUserId() != null) {
            AppUser currentAssignee = task.getAssignedTo();
            boolean isChangingAssignee = currentAssignee == null || !currentAssignee.getId().equals(request.assignedUserId());
            if (isChangingAssignee) {
                task.setAssignedTo(findAssignedUser(membership, request.assignedUserId()));
            }
        }

        if (Boolean.TRUE.equals(request.clearDueDate())) {
            task.setDueDate(null);
        } else if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }

        if (Boolean.TRUE.equals(request.clearImage())) {
            task.setImageData(null);
        } else if (request.imageData() != null) {
            task.setImageData(request.imageData());
        }

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public void deleteWorkspaceTask(UUID workspaceId, UUID taskId) {
        workspaceService.requireTaskManager(workspaceId);
        Task task = findWorkspaceTaskOrThrow(workspaceId, taskId);
        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public byte[] exportAssignedWorkspaceTasks(UUID workspaceId) {
        WorkspaceMember membership = workspaceService.requireCurrentUserMembership(workspaceId);
        AppUser currentUser = currentUserService.getCurrentUser();
        List<Task> tasks = taskRepository.findByWorkspaceIdAndAssignedToIdOrderByCreatedAtDesc(
            workspaceId,
            currentUser.getId()
        );

        return taskExportService.exportAssignedTasks(membership.getWorkspace(), currentUser, tasks);
    }

    private Task findTaskOrThrow(UUID id) {
        return taskRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada"));
    }

    private Task findWorkspaceTaskOrThrow(UUID workspaceId, UUID taskId) {
        return taskRepository
            .findByIdAndWorkspaceId(taskId, workspaceId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada en esta area"));
    }

    private AppUser findAssignedUser(WorkspaceMember actorMembership, UUID assignedUserId) {
        AppUser assignedUser = userRepository
            .findById(assignedUserId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario asignado no encontrado"));

        WorkspaceMember assignedMembership = workspaceService.requireMembership(
            actorMembership.getWorkspace().getId(),
            assignedUser.getId()
        );

        if (assignedMembership.getRole() == WorkspaceRole.OWNER && actorMembership.getRole() != WorkspaceRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el dueño del espacio puede asignarse tareas a sí mismo");
        }

        return assignedUser;
    }

    private void ensureCurrentUserCanView(Task task) {
        if (task.getWorkspace() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada");
        }
        workspaceService.requireCurrentUserMembership(task.getWorkspace().getId());
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }
}