package com.portfolio.tareas.tareas_api.services;

import com.portfolio.tareas.tareas_api.models.Task;
import com.portfolio.tareas.tareas_api.repositories.TaskRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

	private final TaskRepository taskRepository;

	public TaskService(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public List<Task> getAllTasks() {
		return taskRepository.findAll();
	}

	public Optional<Task> getTaskById(Long id) {
		return taskRepository.findById(id);
	}

	public Task createTask(Task task) {
		return taskRepository.save(task);
	}

	public Optional<Task> updateTask(Long id, Task taskDetails) {
		return taskRepository.findById(id).map(existingTask -> {
			existingTask.setTitle(taskDetails.getTitle());
			existingTask.setDescription(taskDetails.getDescription());
			existingTask.setStatus(taskDetails.getStatus());
			return taskRepository.save(existingTask);
		});
	}

	public boolean deleteTask(Long id) {
		return taskRepository.findById(id).map(task -> {
			taskRepository.delete(task);
			return true;
		}).orElse(false);
	}
}