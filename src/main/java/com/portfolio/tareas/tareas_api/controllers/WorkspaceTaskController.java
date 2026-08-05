package com.portfolio.tareas.tareas_api.controllers;

import com.portfolio.tareas.tareas_api.dto.CreateWorkspaceTaskRequest;
import com.portfolio.tareas.tareas_api.dto.TaskResponse;
import com.portfolio.tareas.tareas_api.dto.UpdateWorkspaceTaskRequest;
import com.portfolio.tareas.tareas_api.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/tasks")
@Tag(name = "Tareas por area", description = "Endpoints para gestionar tareas dentro de un area de trabajo")
public class WorkspaceTaskController {

    private static final MediaType EXCEL_MEDIA_TYPE = MediaType.parseMediaType(
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    private final TaskService taskService;

    public WorkspaceTaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Listar tareas del area", description = "Cualquier miembro puede ver el itinerario del area.")
    public List<TaskResponse> getWorkspaceTasks(@PathVariable(name = "workspaceId") UUID workspaceId) {
        return taskService.getWorkspaceTasks(workspaceId);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Ver tarea del area", description = "Obtiene una tarea si pertenece al area indicada.")
    public ResponseEntity<TaskResponse> getWorkspaceTask(
        @PathVariable(name = "workspaceId") UUID workspaceId,
        @PathVariable(name = "taskId") UUID taskId
    ) {
        return ResponseEntity.ok(taskService.getWorkspaceTask(workspaceId, taskId));
    }

    @PostMapping
    @Operation(summary = "Crear tarea en area", description = "Solo OWNER o ADMIN pueden crear tareas en el area.")
    public ResponseEntity<TaskResponse> createWorkspaceTask(
        @PathVariable(name = "workspaceId") UUID workspaceId,
        @Valid @RequestBody CreateWorkspaceTaskRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createWorkspaceTask(workspaceId, request));
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Actualizar tarea del area", description = "Solo OWNER o ADMIN pueden actualizar tareas del area.")
    public ResponseEntity<TaskResponse> updateWorkspaceTask(
        @PathVariable(name = "workspaceId") UUID workspaceId,
        @PathVariable(name = "taskId") UUID taskId,
        @Valid @RequestBody UpdateWorkspaceTaskRequest request
    ) {
        return ResponseEntity.ok(taskService.updateWorkspaceTask(workspaceId, taskId, request));
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Eliminar tarea del area", description = "Solo OWNER o ADMIN pueden eliminar tareas del area.")
    public ResponseEntity<Void> deleteWorkspaceTask(
        @PathVariable(name = "workspaceId") UUID workspaceId,
        @PathVariable(name = "taskId") UUID taskId
    ) {
        taskService.deleteWorkspaceTask(workspaceId, taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export")
    @Operation(summary = "Exportar mis tareas del area", description = "Descarga un Excel con las tareas asignadas al usuario autenticado.")
    public ResponseEntity<byte[]> exportMyWorkspaceTasks(@PathVariable(name = "workspaceId") UUID workspaceId) {
        byte[] file = taskService.exportAssignedWorkspaceTasks(workspaceId);
        ContentDisposition contentDisposition = ContentDisposition
            .attachment()
            .filename("workspace-" + workspaceId + "-mis-tareas.xlsx")
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .contentType(EXCEL_MEDIA_TYPE)
            .body(file);
    }
}
