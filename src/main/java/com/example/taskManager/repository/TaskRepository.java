package com.example.taskManager.repository;

import com.example.taskManager.entity.Task;
import com.example.taskManager.entity.TaskPriority;
import com.example.taskManager.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    List<Task> findByTaskStatus(TaskStatus taskStatus);

    List<Task> findByTaskPriority(TaskPriority taskPriority);

    List<Task> findByTitleContainingIgnoreCase(String title);

    List<Task> findByDueDateBefore(LocalDateTime dueDateBefore);

}
