package com.portfolio.tareas.tareas_api.controllers;

import com.portfolio.tareas.tareas_api.dto.CreateWorkspaceTaskRequest;
import com.portfolio.tareas.tareas_api.dto.TaskResponse;
import com.portfolio.tareas.tareas_api.dto.UpdateWorkspaceTaskRequest;
import com.portfolio.tareas.tareas_api.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
@Tag(name = "Tareas", description = "Endpoints para administrar y consultar tareas")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Listar mis tareas asignadas")
    public ResponseEntity<List<TaskResponse>> getMyTasks() {
        return ResponseEntity.ok(taskService.getAssignedTasksForCurrentUser());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tarea por ID")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(taskService.getTaskForCurrentUser(id));
    }

    @GetMapping("/workspace/{workspaceId}")
    @Operation(summary = "Listar tareas de un area de trabajo")
    public ResponseEntity<List<TaskResponse>> getWorkspaceTasks(@PathVariable(name = "workspaceId") UUID workspaceId) {
        return ResponseEntity.ok(taskService.getWorkspaceTasks(workspaceId));
    }

    @GetMapping("/workspace/{workspaceId}/{taskId}")
    @Operation(summary = "Ver detalle de una tarea del area de trabajo")
    public ResponseEntity<TaskResponse> getWorkspaceTask(
            @PathVariable(name = "workspaceId") UUID workspaceId,
            @PathVariable(name = "taskId") UUID taskId) {
        return ResponseEntity.ok(taskService.getWorkspaceTask(workspaceId, taskId));
    }

    @PostMapping("/workspace/{workspaceId}")
    @Operation(summary = "Crear tarea en un area de trabajo")
    public ResponseEntity<TaskResponse> createWorkspaceTask(
            @PathVariable(name = "workspaceId") UUID workspaceId,
            @RequestBody CreateWorkspaceTaskRequest request) {
        return ResponseEntity.ok(taskService.createWorkspaceTask(workspaceId, request));
    }

    @PutMapping("/workspace/{workspaceId}/{taskId}")
    @Operation(summary = "Actualizar tarea en un area de trabajo")
    public ResponseEntity<TaskResponse> updateWorkspaceTask(
            @PathVariable(name = "workspaceId") UUID workspaceId,
            @PathVariable(name = "taskId") UUID taskId,
            @RequestBody UpdateWorkspaceTaskRequest request) {
        return ResponseEntity.ok(taskService.updateWorkspaceTask(workspaceId, taskId, request));
    }

    @DeleteMapping("/workspace/{workspaceId}/{taskId}")
    @Operation(summary = "Eliminar tarea de un area de trabajo")
    public ResponseEntity<Void> deleteWorkspaceTask(
            @PathVariable(name = "workspaceId") UUID workspaceId,
            @PathVariable(name = "taskId") UUID taskId) {
        taskService.deleteWorkspaceTask(workspaceId, taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/workspace/{workspaceId}/export")
    @Operation(summary = "Exportar mis tareas asignadas a Excel")
    public ResponseEntity<byte[]> exportAssignedWorkspaceTasks(@PathVariable(name = "workspaceId") UUID workspaceId) {
        byte[] excelFile = taskService.exportAssignedWorkspaceTasks(workspaceId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"mis_tareas.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelFile);
    }
}
