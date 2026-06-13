package com.portfolio.tareas.tareas_api.repositories;

import com.portfolio.tareas.tareas_api.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}