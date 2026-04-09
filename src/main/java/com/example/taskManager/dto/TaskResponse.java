package com.example.taskManager.dto;

import com.example.taskManager.entity.Task;
import com.example.taskManager.entity.TaskPriority;
import com.example.taskManager.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public static TaskResponse from(Task task){
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getTaskStatus())
                .priority(task.getTaskPriority())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public static List<TaskResponse> fromList(List<Task> tasks){

        return tasks.stream().map(TaskResponse::from).toList();
    }

}
