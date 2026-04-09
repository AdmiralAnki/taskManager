package com.example.taskManager.dto;

import com.example.taskManager.entity.TaskPriority;
import com.example.taskManager.entity.TaskStatus;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//This represents the data that user provides

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {

    @NotBlank(message = "Title cannot be empty")
    @Size(min = 3, max = 100, message = "Title has to be between 3 and 100 characters")
    private String title;

    @Size(max = 500)
    private String description;

    @NotNull
    private TaskStatus status;

    private TaskPriority priority;

    private LocalDateTime dueDate;

}
