package com.example.taskManager.controller;

import com.example.taskManager.dto.TaskRequest;
import com.example.taskManager.dto.TaskResponse;
import com.example.taskManager.entity.TaskPriority;
import com.example.taskManager.entity.TaskStatus;
import com.example.taskManager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    //POST
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request){
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //GET
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(){
        List<TaskResponse> taskResponses = taskService.getAllTasks();
        return ResponseEntity.ok(taskResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable(value="id") Long id){
        TaskResponse response = taskService.getTaskById(id);
        return ResponseEntity.ok(response);
    }

    //get by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(@PathVariable(name = "status") TaskStatus status){
        List<TaskResponse> taskResponses = taskService.getTaskByStatus(status);
        return ResponseEntity.ok(taskResponses);
    }

    //get by priority
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskResponse>> getTasksByPriority(@PathVariable(name = "priority") TaskPriority priority){
        List<TaskResponse> taskResponses = taskService.getTaskByPriority(priority);
        return ResponseEntity.ok(taskResponses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskResponse>> getTasksByTitle(@RequestParam String title){
        List<TaskResponse> taskResponses = taskService.searchTaskByTitle(title);
        return ResponseEntity.ok(taskResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTaskById(@PathVariable(value = "id") Long id, @Valid @RequestBody TaskRequest request){
        TaskResponse response = taskService.updateTask(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskById(@PathVariable(value = "id") Long id){
        taskService.deleteTask(id);
    }
}
