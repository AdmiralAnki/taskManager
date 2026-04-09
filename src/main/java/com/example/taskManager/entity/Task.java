package com.example.taskManager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="tasks")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "A task needs a Title ")
    @Size(min = 3, max = 100, message = "Title should be between 3 and 100 characters")
    @Column(name = "title")
    private String title;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name="status",nullable = false, length = 20)
    private TaskStatus taskStatus;

    @Enumerated(EnumType.STRING)
    @Column(name="priority", length = 20)
    private TaskPriority taskPriority;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    //------------------Audit fields------------------

    @CreationTimestamp
    @Column(name = "created_at",updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
