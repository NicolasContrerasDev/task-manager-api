package com.portfolio.tareas.tareas_api.controllers;

import com.portfolio.tareas.tareas_api.models.Task;
import com.portfolio.tareas.tareas_api.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
@Tag(name = "Tareas", description = "Endpoints para gestionar tus tareas")
public class TaskController {

	private final TaskService taskService;

	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	@GetMapping
	@Operation(summary = "Listar todas las tareas", description = "Obtiene la lista completa de tareas guardadas en la base de datos.")
	public List<Task> getAllTasks() {
		return taskService.getAllTasks();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Buscar tarea por ID", description = "Retorna una única tarea según el ID proporcionado.")
	public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
		return taskService.getTaskById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	@Operation(summary = "Crear nueva tarea", description = "Crea una tarea nueva. El ID y la fecha de creación se generan automáticamente.")
	public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
		return ResponseEntity.ok(taskService.createTask(task));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar tarea", description = "Modifica el título, descripción o estado de una tarea existente con los siguientes estados PENDING,\r\n"
			+ "    IN_PROGRESS,\r\n" + "    COMPLETED.")
	public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task taskDetails) {
		return taskService.updateTask(id, taskDetails).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar tarea", description = "Borra permanentemente una tarea de la base de datos usando su ID.")
	public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
		if (taskService.deleteTask(id)) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}
}